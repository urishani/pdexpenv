/*
 * Created on 24/11/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import tools.html.AbstractHtmlPanel;

/**
 * @author shani
 *
 * A singleton which manages events from all controls in the application
 * which can come from an HtmlPanel, or regular awt controls.
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class EventProxy {
	private static String gPropertiesRoot;
	private static MyProperties gProperties;
	private String mSubject;
	private static Dictionary mInstances = new Hashtable();
	private boolean mTrace = false;
	private static boolean mGtrace = false;
	private ArrayList mRegistry = new ArrayList();

	private static EventThread mEventThread = new EventThread();
	static{
		mEventThread.start();
	}
	
	/**
	 * @param pTrace marks tracing on.
	 */
	private EventProxy()
	{
		this("");
	}
	public static EventProxy getInstance()
	{
		return getInstance("");
	}

	/**
	 * @param subject name of separate proxy to allow different emitters and
	 * listeners to work independent of each other.
	 * @return
	 */
	public static EventProxy getInstance(String subject)
	{
		if (subject == null)
			subject = "";
		EventProxy ep = (EventProxy) mInstances.get(subject); 			
		if (ep == null)
		{
//			System.err.println("Creating a new EventProxy for '"+subject+"'");
			ep = new EventProxy(subject);
			mInstances.put(subject, ep);
			if (gProperties != null)
			{
				String tmp = gProperties.getProperty(gPropertiesRoot + "." + subject);
				if (tmp != null)
					ep.setTrace(tmp.equals("1"));
			}
		}
		return ep;
	}
	public String toString() { return mSubject;}		
	/**
	 * @param subject
	 */
	private EventProxy(String pSubject) 
	{
		mSubject = pSubject;	
	}
	public void setTrace (boolean pTrace) 
	{
		mTrace = pTrace;
		System.out.println("Setting EventProxy trace of '" + mSubject + "' to " + pTrace);
	}
	public static void setGlobalTrace (boolean pTrace) 
	{
		mGtrace = pTrace;
		System.out.println("Setting global EventProxy trace to " + pTrace);
	}

	public void removeListener(ProxyListener pListener)
	{
		mRegistry.remove(pListener);
	}

	public void addListener(ProxyListener pListener)
	{
		removeListener(pListener);
		mRegistry.add(pListener);
	}

	public void delegate(String pName, String pValue, AbstractHtmlPanel pPanel)
	{
		ProxyEvent pe = new ProxyEvent(pName, pValue, pPanel, this);
		if (mTrace || mGtrace && !pe.mName.equalsIgnoreCase("init"))
			System.out.println(mSubject + " Event pushed. " + pe.toString());
		mEventThread.push(pe);
	}

	public interface ProxyListener
	{
		public boolean onEvent(String pName, String pValue, AbstractHtmlPanel pPanel);
	}
	/**
	 * @param event
	 */
	public void publish(ProxyEvent pe) 
	{
		if (pe == null) return;
		if (mTrace || mGtrace)
			System.out.println(mSubject + " Event triggered. " + pe.toString());
		for (int i=0; i < mRegistry.size(); i++)
		{
			ProxyListener a = (ProxyListener)mRegistry.get(i);
			a.onEvent(pe.mName, pe.mValue, pe.mPanel);
		}
		if (mRegistry.size() == 0 && !pe.mName.equalsIgnoreCase("init"))
			System.out.println("No listeners for " + mSubject + " event. " + pe.toString());

	}
	/**
	 * @param b
	 */
	public void clearQueue(boolean b) {
		mEventThread.clearQueue( b?null:mSubject);
	}
	/**
	 * @return
	 */
	public String getSubject() { return mSubject; }
	/**
	 * @param mProperties
	 * @param string
	 */
	public static void setTraceFromProperties(MyProperties mProperties, String pRoot) {
		gProperties = mProperties;
		gPropertiesRoot = pRoot;
		String tmp = null;
		if (null != (tmp = gProperties.getProperty(gPropertiesRoot+".Global")))
			setGlobalTrace(tmp.equals("1"));
		Enumeration k = mInstances.keys();
		while (k.hasMoreElements())
		{
			String subj = (String)k.nextElement();
			tmp = gProperties.getProperty(gPropertiesRoot + "." + subj);
			if (tmp != null)
			{
				EventProxy ep = (EventProxy) mInstances.get(subj);
				ep.setTrace(tmp.equals("1"));
			}
		}
	}
}

class ProxyEvent
{
	String mName, mValue;
	AbstractHtmlPanel mPanel;
	static int mCnt = 0;
	private int mNum;
	EventProxy mProxy;
	public ProxyEvent(String pName, String pValue, AbstractHtmlPanel pPanel, EventProxy pProxy)
	{
		mName = pName;
		mValue = pValue;
		mPanel = pPanel;
		mNum = mCnt++;
		mProxy = pProxy;
	}
	public String toString()
	{
		return mProxy.toString() + "event #" + mNum + ": Name= '"+ mName + "', Value= '" + mValue +"'"; 
	}
	/**
	 * 
	 */
	public void publish() 
	{
		mProxy.publish(this);
	}
}
	

class EventThread extends Thread
{
	private static LinkedList mQueue = new LinkedList();
	public synchronized void push(ProxyEvent pe)
	{
		mQueue.add(pe);
		this.notify();
	}
	/**
	 * @param string
	 */
	public void clearQueue(String string) 
	{
		if (string == null) mQueue.clear();
		else 
		{
			for (int i=0; i< mQueue.size(); i++)
			{
				ProxyEvent pe = (ProxyEvent) mQueue.get(i);
				if (pe.mProxy.getSubject().equalsIgnoreCase(string))
				{
					mQueue.remove(i);
					i--; 
				}
			}
		}
	}

	public synchronized ProxyEvent pop()
	{
		while (mQueue.size() == 0)
		{
			try{this.wait();}
			catch(InterruptedException e){};
		}
		return (ProxyEvent) mQueue.remove(0);
	}
	
	public void run()
	{
		ProxyEvent pe = null;
		while (true)
		{
			pe = pop();
			pe.publish();
		}
	}

}

