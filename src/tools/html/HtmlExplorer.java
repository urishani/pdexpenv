package tools.html;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class HtmlExplorer extends JEditorPane implements HyperlinkListener
{
	/* (non-Javadoc)
	 * @see javax.swing.text.JTextComponent#copy()
	 */
	public void copy() {
		// Do nothing
		// super.copy();
		System.out.println("Copy called!!");
	}
//	private JEditorPane editorPane = new JEditorPane();

	private ArrayList mEvents = new ArrayList(100);
	private Stack mBackStack = new Stack();
	private Stack mForwardStack = new Stack();
	private String mCurrentUrl = null;
	private AbstractHtmlPanel mPanel = null;
	
	public HtmlExplorer() {
		super();
		addHyperlinkListener(this);
		setEditable(false);
	}
	public HtmlExplorer(String pUrl) {
		this();
		try { 
			setPage(pUrl);
			mCurrentUrl = pUrl;
		}
		catch(Exception ex) { 
			System.err.println("Error setting explorer page [" + pUrl + "]. Reason: " + ex.getMessage());
		}
	}

	public static void main(String args[]) {
		String url = 
			"file:" + System.getProperty("user.dir") +
			System.getProperty("file.separator") +
			"JEditorPane.html"; //"java.util.Hashtable.html";
		if (args.length > 0)
			url = args[0];
			
		JFrame frame = new JFrame();
		Container contentPane = frame.getContentPane();
		HtmlExplorer editorPane = new HtmlExplorer(url); 
		contentPane.add(new JScrollPane(editorPane), 
						BorderLayout.CENTER);
			
		GJApp.launch(frame, 
					"JEditorPane",300,300,650,450);
	}
	public boolean hasForward()
	{
		return mForwardStack.size() > 0;
	}
	public boolean hasBackward()
	{
		return mBackStack.size() > 0;
	}
	public void doBackward()
	{
		hyperlinkUpdate("BACK", null);
	}
	public void doForward()
	{
		hyperlinkUpdate("FORWARD", null);
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
	 */
	public void hyperlinkUpdate(HyperlinkEvent arg0) {
		Date d = new Date();
//		System.out.println(arg0.getEventType() + ": [" + d.toString() + "] " + arg0.getURL());
		if (arg0.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		{
			URL url = arg0.getURL();
			String refName = url.getFile();
			hyperlinkUpdate(refName, url);
		}
	}
	
	private void hyperlinkUpdate (String refName, URL url)
	{
		System.err.println(refName);
		
		if (refName.equalsIgnoreCase("BACK"))
		{
			System.out.println("going back");
			if (mBackStack.size() > 0)
			{
				String sUrl = (String)mBackStack.pop();
				if (mCurrentUrl != null)
					mForwardStack.push(mCurrentUrl);
				mCurrentUrl = sUrl;
			} else 
			{
				System.out.println("No backward to go");
				return; // do nothing
			}
		} else if (refName.equalsIgnoreCase("FORWARD"))
		{
			System.out.println("going next");
			if (mForwardStack.size() > 0)
			{
				String sUrl = (String)mForwardStack.pop();
				mBackStack.push(mCurrentUrl);
				mCurrentUrl = sUrl;
			} else
			{
				System.out.println("No forward to go");
				return; // do nothing
			}
		} else if (url != null)
		{
			if (mCurrentUrl != null)
				mBackStack.push(mCurrentUrl);
			mCurrentUrl = url.toString();	
			mForwardStack.clear(); // new forward branch needs to be created.			
		}
		
		while (mCurrentUrl != null)
		{
			String failed = null;
			try {
				url= new URL(mCurrentUrl);
				//((JEditorPane)arg0.getSource()).
				setPage(url);
				return;
			} catch (MalformedURLException e2) {
				failed = e2.getMessage();
			} catch (IOException e) {
				failed = e.getMessage();
			}
			if (failed != null)
			{
				System.out.println("URL illegal or not found [" + mCurrentUrl + "]. Reason: " + failed);
				if (mBackStack.size() > 0)
				{
					mCurrentUrl = (String) mBackStack.pop();
					continue;
				}
				if (mForwardStack.size() > 0) {
					mCurrentUrl = (String) mForwardStack.pop();
					continue;
				}
				return;
			}
		}
	}

	/**
	 * @see javax.swing.JEditorPane#setPage(java.lang.String)
	 */
	public void setPage(String arg0) throws IOException {
		super.setPage(arg0);
		if (mCurrentUrl != null)
			mBackStack.push(mCurrentUrl);
		mForwardStack.clear();
		mCurrentUrl = arg0.toString();
	}

	/**
	 * 
	 * @author shani
	 *
	 * To change the template for this generated type comment go to
	 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
	 */
	public class DatedUrl
	{
		private URL mUrl;
		private Date mDate;

		public DatedUrl(Date pDate, URL pUrl)
		{
			mDate = pDate;
			mUrl = pUrl;
		}
		public String toSring()
		{
			return mDate.toString()+ ": " + mUrl;
		}
	}
	/** 
	 * @see javax.swing.JEditorPane#setPage(java.net.URL)
	 */
	public void setPage(URL arg0) throws IOException {
		System.out.println(new Date().toString()+": " + arg0);
//		try {
			super.setPage(arg0);
//		} catch (IOException e) {
//			System.err.println("No help found. " + e.getMessage());
//		}
		mEvents.add(new DatedUrl(new Date(), arg0));
		if (mPanel != null)
			mPanel.simulateEvent("Help.URL", arg0.toString());
//		System.out.println("mEvents:\n" + mEvents);
	}
	
	public ArrayList getDatedUrls()
	{
		return mEvents; 
	}
	public void resetDatedUrls() 
	{
		mEvents.clear();
	}
	/**
	 * @param panel
	 */
	public void setButtonsPanel(AbstractHtmlPanel pPanel) {
		mPanel = pPanel;
	}
	/**
	 * 
	 */
	public void reload() {
		try {
			setPage(mCurrentUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @return
	 */
	public String getCurrent() {
		return mCurrentUrl;
	}
}
	
class GJApp extends WindowAdapter {
	static private JPanel statusArea = new JPanel();
	static private JLabel status = new JLabel(" some Status info ");
	static private ResourceBundle resources;

	public static void launch(final JFrame f, String title,
							  final int x, final int y, 
							  final int w, int h) {
		launch(f,title,x,y,w,h,null);	
	}
	public static void launch(final JFrame f, String title,
							  final int x, final int y, 
							  final int w, int h,
							  String propertiesFilename) {
		f.setTitle(title);
		f.setBounds(x,y,w,h);
		f.setVisible(true);

		statusArea.setBorder(BorderFactory.createEtchedBorder());
		statusArea.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		statusArea.add(status);
		status.setHorizontalAlignment(JLabel.LEFT);

		f.setDefaultCloseOperation(
							WindowConstants.DISPOSE_ON_CLOSE);

		if(propertiesFilename != null) {
			resources = ResourceBundle.getBundle(
						propertiesFilename, Locale.getDefault());
		}

		f.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
	}
	static public JPanel getStatusArea() {
		return statusArea;
	}
	static public void showStatus(String s) {
		status.setText(s);
	}
	static Object getResource(String key) {
		if(resources != null) {
			return resources.getString(key);
		}
		return null;
	}
}
