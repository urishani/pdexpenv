/*
 * Created on 13/11/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.html;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import experiment.EventProxy;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractHtmlPanel extends Box 
{
	private int mTriggerLevel = AbstractHtmlPanel.TRIGGER_ALL;
	private Component mNullComponent = new JTextArea();
	protected float mAlignment = Component.LEFT_ALIGNMENT;
	protected boolean mLeftJustify = true;
	protected int[] mMargins= new int[]{0,0,0,0};
	protected EventProxy mProxy;
	protected boolean mTrace = false;
	protected boolean mDebug= false;
	private Box mThis = this;
	private static ArrayList mTimers = new ArrayList();
	protected ArrayList mControls = new ArrayList();
//	protected Hashtable mProperties = new Hashtable();
	protected HAttributes mAttributes;
	protected Hashtable mDir = null; 
	protected String mInFile = "";
	protected static Properties defaultAttributes = null; 

	protected void clear()
	{
//		show(false);
		Component components[] = getComponents();
		for (int i= 0; i < components.length; i++) {
			components[i].setVisible(false);
		}
		removeAll();
		mDir = new Hashtable();
		mControls = new ArrayList();
		killTimers(false);
	}
	
	/**
	 * Get a component for a certain Id, so it can be processed per
	 * setting visibility, enablement, text as well as getting values 
	 * of these attributes.
	 * Sets the enabled status of a certain componnet of this panel.
	 * @param pId name of the component, its mId.
	 * @return the Component object for the pId.
	 */
	public HComponent getComponent(String pId)
	{
		HComponent c = (HComponent)mDir.get(pId);
		if (c == null) {
//			 System.err.println("No Component foung for id '"+ pId + "'");
			 return null; //mNullComponent; 
		} 
		return c;
	}
	protected AbstractHtmlPanel()
	{
		this(new String[]{}, "", null);
	}
	protected AbstractHtmlPanel( String[] args){ this(args, "", null); }
	protected AbstractHtmlPanel( String[] args, String pSubject, EventProxy pProxy)
	{
		super(BoxLayout.Y_AXIS);
		mDir = new Hashtable();
		if (pProxy != null)
			mProxy = pProxy;
		else if (pSubject != null) 
			mProxy = EventProxy.getInstance(pSubject);
		else
			mProxy = EventProxy.getInstance();
		boolean help = args.length == 0;
		for (int a=0; a < args.length; a++)
		{		
			if (args[a].equalsIgnoreCase("-help")) 
			{
				System.out.println("Args: <file-name> [-help] }");
				System.out.println(" Where: ");
				System.out.println(
					"\t <file-name> --> reads input from file <file-name>.");
				System.out.println("\t -help --> this help.");
				System.out.println("\t -debug --> print parsing traces.");
				System.out.println("\t -trace --> print event traces.");
				System.out.println("\t -leftRight --> fill items left to right.");
				System.out.println("\t -rightLeft --> fill items right to left.");
				System.out.println("\t -center --> center items in the middle, left to right.");
				System.out.println("\t -margins <t,l,r,b> --> set margins to values from 4-tuple with t-top, l-left, r-right, b-bottom.");
				return;
			} else if (args[a].equalsIgnoreCase("-debug"))
			{
				mDebug = true;
			} else if (args[a].equalsIgnoreCase("-trace"))
			{
				mTrace = true;
			} else if (args[a].equalsIgnoreCase("-leftRight"))
			{
				if (mAlignment != Component.CENTER_ALIGNMENT)
					mAlignment = Component.LEFT_ALIGNMENT;
				mLeftJustify = true;
			} else if (args[a].equalsIgnoreCase("-center"))
			{
				mAlignment = Component.CENTER_ALIGNMENT;
			} else if (args[a].equalsIgnoreCase("-rightLeft"))
			{
				if (mAlignment != Component.CENTER_ALIGNMENT)
					mAlignment = Component.RIGHT_ALIGNMENT;
				mLeftJustify = false;
			} else if (args[a].equalsIgnoreCase("-margins"))
			{
				a++;
				if (a >= args.length) continue;
				StringTokenizer st = new StringTokenizer(args[a],",");
				int margin = 0;
				while (st.hasMoreTokens() && margin < mMargins.length)
				{
					mMargins[margin]= Integer.parseInt(st.nextToken());
					margin ++;
				}
			} else mInFile = args[a];
		}
		
		if (defaultAttributes == null)
		{
			defaultAttributes = new Properties();
			defaultAttributes.put("ROWS", "1");
			defaultAttributes.put("COLUMNS", "5"); //	protected final int DEFAULT_WIDTH = 5;
			defaultAttributes.put("BORDER", "0");
			defaultAttributes.put("BORDERCOLOR", "0");
		}
		mAttributes = new HAttributes(defaultAttributes, null, null);
		mProxy.delegate("init", "", this); // signal initialization of this panel.
	}

	/**
	 * Prepares the parsed controls for display
	 *
	 */
	public abstract void prepare();
	

	/**
	 * Prepares a properties representation of input fields values.
	 * @return a properties where each componnet id is associated with its text value.
	 */
	public Properties getTextFields()
	{
		Properties p = new Properties();
		
		Enumeration k = mDir.keys();
		while (k.hasMoreElements())
		{
			String id = (String)k.nextElement();
			if (id.trim().length() == 0) continue;
			Object o = mDir.get(id);
			if (o instanceof HComponent)
			{
				p.setProperty(id, ((HComponent)o).getText());
			}
		}
		return p;
	}
	
	/**
	 * Sets back values of all input fields according to a pre-saved status.
	 * @param Properites which has been previousely prepared.
	 * @param pReset if true, the values are reset to blank.
	 */
	public void setTextFields(Properties pProperties, boolean pReset)
	{
		Enumeration e = pProperties.keys();
		while (e.hasMoreElements())
		{
			String id = (String)e.nextElement();
			String val = pProperties.getProperty(id);
			if (pReset) val = "";
			HComponent c = getComponent(id);
			if (c == null) continue;
			c.setText(val);
//			if (c instanceof JTextComponent)
//				((JTextComponent)c).setText(val);
//			else if (c instanceof JLabel)
//				((JLabel)c).setText(val);
//			else
//				System.err.println("Cannot set value '" + val + "' in component '" + id + "' of class '" + c.getClass().toString() + "'");
		}
	}

	// Levels of event triggering.
	public static final int TRIGGER_NONE = 0;
	public static final int TRIGGER_BUTTONS = 1;
	public static final int TRIGGER_INPUTS = 2;
	public static final int TRIGGER_ALL = 3;
	
	class MyActionEventListener implements ActionListener
	{
		/** (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		private String mName;
		public MyActionEventListener(String pName)
		{
			mName = pName; 
		}
		public void actionPerformed(ActionEvent arg0) 
		{
	//		System.out.println("event: "  + arg0 + ", for source: " + (arg0.getSource()).getClass());
			if (mTriggerLevel < HtmlPanel.TRIGGER_BUTTONS) return;
			if (arg0.getSource() instanceof AbstractButton)
				mProxy.delegate(mName, ((AbstractButton)arg0.getSource()).getText(), AbstractHtmlPanel.this);
			else if (arg0.getSource() instanceof JMenuItem)
				mProxy.delegate(mName, ((JMenuItem)arg0.getSource()).getText(), AbstractHtmlPanel.this);
		}
	}
	
	public void simulateEvent(String pName, String pValue)
	{
		mProxy.delegate(pName, pValue, this);
	}


	class MyTextEventListener implements KeyListener
	{
		/** (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		private String mName;
		public MyTextEventListener(String pName)
		{
			mName = pName; 
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		public void keyPressed(KeyEvent arg0) {
			if (mTriggerLevel < HtmlPanel.TRIGGER_INPUTS) return;
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		public void keyReleased(KeyEvent arg0) {
			if (mTriggerLevel < HtmlPanel.TRIGGER_INPUTS) return;
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		public void keyTyped(KeyEvent arg0) {
			if (mTriggerLevel < HtmlPanel.TRIGGER_INPUTS) return;
			HComponent tf = (HComponent)arg0.getSource();
			mProxy.delegate(mName, tf.getText().trim(), AbstractHtmlPanel.this);
//			tf.setText("   " + (tf.getText().trim()) + "   ");
			//tf.setSize(tf.getPreferredSize().width + 20, tf.getHeight()); 
			// + new Rectangle(10,0));
			mThis.doLayout();
		}
	}

	public class Timer extends Thread
	{
		private boolean mStop = false;
		private	final int mSeconds;
		private int mInterval;
		private final String mId;
		private EventProxy mProxy;
		private final AbstractHtmlPanel mPanel;
		private final Date mTime;
		
		public Timer(int pSeconds, int pInterval, 
			EventProxy pProxy, String pId, AbstractHtmlPanel pPanel)
		{
			if (pSeconds <= 0)
			{
				pSeconds = 0;
				pInterval = Math.max(pInterval, 0);
			} else
			{
				if (pInterval <= 0) pInterval = pSeconds;
			}
			
			mSeconds = pSeconds;
			mInterval = pInterval;
			mTime = new Date();
			mProxy = pProxy;
			mPanel = pPanel;
			mId = pId;
			mTimers.add(this);
		}
		
		public void kill()
		{
			if (mSeconds <= 0) return; // dont kill a forever timer.
			mStop = true;
			interrupt(); // run(0) will interrupt and stop.
		}
		public AbstractHtmlPanel getOwner()
		{
			return mPanel;
		}
		public void setProxy (EventProxy pProxy)
		{
			mProxy = pProxy;
		}
		
		public long timeLeft()
		{
			if (mSeconds <= 0) return -1;
			return (mSeconds*1000 - (new Date()).getTime() - mTime.getTime());
		}
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() 
		{
			mProxy.delegate(mId, "start", mPanel);
			if (mInterval > mSeconds && mSeconds > 0) 
				mInterval = mSeconds;
			int timeLeft = mSeconds;
			while ((mSeconds == 0 && mInterval > 0 )|| timeLeft > 0)
			{
				try {
					int sTime = timeLeft;
					if (mSeconds == 0) sTime = mInterval;
					else sTime = Math.min(mInterval, timeLeft);
					 
					System.err.println("Sleeping for " + sTime + " seconds");
					sleep( sTime * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					if (mStop) return;
					break;
				}
				mProxy.delegate(mId, "tick", mPanel);
				timeLeft = mSeconds - (int)((new Date()).getTime() - mTime.getTime())/1000;
				//System.err.println(timeLeft);
			}
			mProxy.delegate(mId, "end", mPanel);
			//System.err.println(timeLeft);
			//stop();
		}
	}
	/**
	 * Sets the level of events to be triggered.
	 * @param pLevel one of possible values: NONE, BUTTONS, INPUTS, ALL
	 * @return present level.
	 */
	public int setTriggerLevel(int pLevel)
	{
		int lvl = mTriggerLevel;
		mTriggerLevel = pLevel;
		return lvl;
	}
	/**
	 * @param proxy
	 */
	public void setProxy(EventProxy pProxy) 
	{
		mProxy = pProxy;
		for (int i=0; i< mTimers.size(); i++)
			((Timer)mTimers.get(i)).setProxy(pProxy);
		mProxy.delegate("init", "", this); // signal initialization of this panel.
	}

	public void killTimers(boolean pKillAll)
	{
		for (int i=0; i < mTimers.size(); i++)
		{
		   Timer t = (Timer)mTimers.get(i);
		   if (!pKillAll && t.getOwner() != this)
		   	  continue;
		   t.kill();
		}
		mTimers.clear();
	}


}
