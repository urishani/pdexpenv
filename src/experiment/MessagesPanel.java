/*
 * Created on 07/12/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment;

import java.awt.Color;
import java.awt.Point;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MessagesPanel extends JScrollPane
{
	private boolean mLogSameLine = false;
	private final Box mMessages;
	private Color mBg = null;
	private Color mFg = null;
	private Color mErrBg = null;
	private Color mErrFg = null;
	private Color mMsgBg = null;
	private Color mMsgFg = null;
	private final Properties mProperties;
	//private int mH = 0;

	public MessagesPanel(Box pMessages, Properties pProperties)
	{
		super(pMessages);
		mMessages = pMessages;
		mProperties = pProperties;
		String clr = mProperties.getProperty("Project.Messages.Background").trim();
		mMessages.setOpaque(true);
		if (clr.length() > 0) 
		{
			mBg = new Color(Integer.parseInt(clr, 16));
			setBackground(mBg);
			mMessages.setBackground(mBg);
		} 
		clr = mProperties.getProperty("Project.Messages.Foreground").trim();
		if (clr.length() > 0)
		{
			mFg = new Color(Integer.parseInt(clr, 16));
			setForeground(mFg);
			mMessages.setForeground(mFg);
		}
		clr = mProperties.getProperty("Project.Messages.Err.Background").trim();
		if (clr.length() > 0) mErrBg = new Color(Integer.parseInt(clr, 16));
		clr = mProperties.getProperty("Project.Messages.Err.Foreground").trim();
		if (clr.length() > 0) mErrFg = new Color(Integer.parseInt(clr, 16));
		clr = mProperties.getProperty("Project.Messages.Msg.Background").trim();
		if (clr.length() > 0) mMsgBg = new Color(Integer.parseInt(clr, 16));
		clr = mProperties.getProperty("Project.Messages.Msg.Foreground").trim();
		if (clr.length() > 0) mMsgFg = new Color(Integer.parseInt(clr, 16));
	}
	/**
	 * @param string
	 */
	private void message(String string, Color pColor, Color pBG) 
	{
//		System.out.print("("+ mMessages.getComponentCount()+")++++" + string);
		boolean newLine = (string.charAt(string.length()-1)=='\n');
		if (newLine) string = string.substring(0,string.length()-2);
		JTextArea line = new JTextArea(string);
		//mH += new StringTokenizer(string, "/n").countTokens() + 1;
		//System.out.println("mH = " + mH);
//		System.out.println("'"+string+"' : " + (newLine?"Has":"No")+ " new line");
//		System.out.println("LogSameLine = " + Boolean.toString(mLogSameLine));
		//StringTokenizer st = new StringTokenizer(string, "\n");
		//while (st.hasMoreTokens())
		//{
			//JLabel lastLine = null;
			//if (mMessages.getComponentCount() > 0)
				//lastLine = (JLabel) mMessages.getComponents()[mMessages.getComponentCount()-1];
			//if (lastLine != null && lastLine.getForeground() != pColor)
				//mLogSameLine = false; // do not write different colors on same line!
			//if (mLogSameLine && lastLine != null)
			//{
				//line = lastLine;
				//line.setText(line.getText()+ st.nextToken());
			//} else
			//{
				//line = new JLabel(string); //st.nextToken());
				if (pBG != null)
					line.setBackground(pBG);
				line.setOpaque(true);
				line.setForeground(pColor);
				mMessages.add(line,0);
			//}
			//mLogSameLine = false;
		//}
		//int h = mMessages.getHeight();
		JViewport vp =getViewport();
//		System.err.println("h= " + h + ", h2= " + getHeight());
		//if (getHeight() < mH)
		vp.setViewPosition(new Point(0,0));
		invalidate();
		//mLogSameLine = !newLine;
	}

	/**
	 * @param string
	 */
	public synchronized void messagesClear() 
	{
		//setForeground(Color.BLUE);
//		System.out.println("LOG CLEAR");
		mMessages.removeAll(); //setText("");
		mLogSameLine = false;
		repaint();
	}

	/**
	 * @param string
	 */
	public synchronized void errorMessage(String string) 
	{
//		System.out.println("logError");
		message(string, mErrFg, mErrBg);
	}

	/**
	 * @param string
	 */
	public synchronized void infoMessage(String string) 
	{
//		System.out.println("logMessage");
		message(string, mMsgFg, mMsgBg);
	}


	/**
	 * @param string
	 */
	public synchronized void outputMessage(String string) {
//		System.out.println("logOutput");
//		String messageBg = mProperties.getProperty("Project.Messages.Msg.Background");
//		int messageFg = Integer.parseInt(mProperties.getProperty("Project.Messages.Msg.Foreground"));
		message(string, Color.GREEN, Color.black);
	}



}
