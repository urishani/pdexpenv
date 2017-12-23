/*
 * Created on 09/11/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package experiment;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.TextArea;
import java.io.IOException;
import java.util.Locale;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;

import tools.html.AbstractHtmlPanel;
import tools.html.HtmlExplorer;
import tools.html.HtmlPanel;

public class View extends JDialog //JFrame// implements ComponentListener
{
	private HtmlPanel mFullScreenPanel;
	private Container mFullScreenContainer;
	public static final int TEST_SCREEN = 0;
	public static final int FULL_SCREEN = 1;
	public static final int INTRO_SCREEN = 2;

	private WindowsFactory mWindowsFactory;
	private JSplitPane mScreen;
	private JSplitPane mRightPage;
	private JSplitPane mLeftPage;
	private JSplitPane mMainAndInput;

	private JTable mTable;

	private final MyProperties mProperties;
	private final EventProxy mProxy;
	public View(MyProperties pProperties, EventProxy pProxy) {
		mProperties = pProperties;
		mProxy = pProxy;
		Color bg = new Color(Integer.parseInt(mProperties.getProperty("Project.Window.Background"), 16));
		Color fg = new Color(Integer.parseInt(mProperties.getProperty("Project.Window.Foreground"), 16));
//		pack();
		int h = Integer.parseInt(mProperties.getProperty("Project.Size.Height"));
		int w = Integer.parseInt(mProperties.getProperty("Project.Size.Width"));
		setBounds(0,0,w,h);
		setResizable(false);
		mWindowsFactory = new WindowsFactory(mProperties, mProxy);
		
		mScreen = new JSplitPane();//JSplitPane.HORIZONTAL_SPLIT, null, null);
		mLeftPage = new JSplitPane(); 
		mLeftPage.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mScreen.setLeftComponent(mLeftPage);
		mRightPage = new JSplitPane(); 
		mRightPage.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mLeftPage.setBackground(bg);
		mRightPage.setBackground(bg);
		mLeftPage.setForeground(fg);
		mRightPage.setForeground(fg);
		mWindowsFactory.getHelpPanel().setBackground(bg);

// Status line option
	    String doStatus_S = mProperties.getProperty("Status");
	    if (doStatus_S != null && doStatus_S.trim().equals("1"))
	    {
			Box tmp = new Box(BoxLayout.Y_AXIS);
			tmp.setAlignmentX(Component.LEFT_ALIGNMENT);
			mScreen.setAlignmentX(Component.LEFT_ALIGNMENT);
			mRightPage.setAlignmentX(Component.LEFT_ALIGNMENT);
			tmp.add(mRightPage);
			tmp.add(mWindowsFactory.getStatusLine());
			mScreen.setRightComponent(tmp);
		} else
			mScreen.setRightComponent(mRightPage);

		mMainAndInput = new JSplitPane();
		mMainAndInput.setOrientation(JSplitPane.VERTICAL_SPLIT);

		mLeftPage.setLeftComponent(mWindowsFactory.getCommandsContainer());
		mRightPage.setRightComponent(mWindowsFactory.getTableContainer());
		mLeftPage.setRightComponent(mWindowsFactory.getMainContainer());
		mRightPage.setLeftComponent(mWindowsFactory.getMessagesContainer());

		mFullScreenContainer = new JPanel(new BorderLayout());
		mFullScreenPanel = HtmlPanel.create(mProperties.getArgsPropery("FullScreen.args"),null, null);
		mFullScreenContainer.add(mFullScreenPanel, BorderLayout.CENTER);
		mFullScreenPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
		mFullScreenPanel.setForeground(Color.WHITE);
		mFullScreenContainer.setForeground(fg);
		mFullScreenContainer.setBackground(bg);
//		mFullScreenContainer.setSize(new Dimension(w/2, h/2));
		
		Container contentPane = getContentPane();
		contentPane.setLocale(new Locale(mProperties.getProperty("Project.Locale"),""));
	}

	/**
	 * 
	 */
	public  void setMode(int pMode) 
	{
		Container contentPane = getContentPane();
		contentPane.removeAll();
		mFullScreenContainer.setVisible(false); mScreen.setVisible(false);
		int w = contentPane.getWidth()-10;
		int h = contentPane.getHeight()-10;
		switch (pMode)
		{
			case FULL_SCREEN:
				contentPane.setLayout(new BorderLayout());
				contentPane.add(mFullScreenContainer, BorderLayout.CENTER); 
				//mFullScreenPanel.setSize(new Dimension(w/2, h/2));
				mFullScreenContainer.setVisible(true);
				mWindowsFactory.getHelpContainer().getHtmlPanel().getComponent("Help.Stop").setVisible(true);
				stopHelp();
				break;
				
			case TEST_SCREEN:
				mScreen.setDividerLocation(w*5/10);
//				mLeftPage.setDividerLocation(h*415/1000);
				mRightPage.setDividerLocation(h*3/10);
				mScreen.setLeftComponent(mLeftPage);
				mMainAndInput.setDividerLocation(h*415/1000); // not showed at first.
				contentPane.add(mScreen); 
				mWindowsFactory.getHelpContainer().getHtmlPanel().getComponent("Help.Stop").setVisible(true);
				stopHelp();
				mScreen.setVisible(true);
				break;
			case INTRO_SCREEN:
				mScreen.setDividerLocation(w/2);
				mScreen.setLeftComponent(mFullScreenContainer);
				mFullScreenContainer.setVisible(true);
				contentPane.add(mScreen);
				doHelp();
				mWindowsFactory.getHelpContainer().getHtmlPanel().getComponent("Help.Stop").setVisible(false);
 				mScreen.setVisible(true);
				break;
		}
		setVisible(true);
	}


	public  AbstractHtmlPanel setFullScreen(String pFilename, String pText, String pProxy, int pTriggerLevel)
	{ 
		if (pText == null)
		   	mFullScreenPanel.setFile(pFilename);
		else
		mFullScreenPanel.setText(pText);
		
		if (pProxy != null)
		{
			mFullScreenPanel.setProxy(EventProxy.getInstance(pProxy));
		}
		mFullScreenPanel.setTriggerLevel(pTriggerLevel);

		return mFullScreenPanel;
	}

	public  AbstractHtmlPanel getCommandsPanel()
	{
		return mWindowsFactory.getCommandsPanel();
	}
	
	public  AbstractHtmlPanel setCommands(String pFilename, String pText, String pProxy, int pTriggerLevel)
	{
		HtmlPanel wPanel = mWindowsFactory.getCommandsPanel();
		if (pText == null)
		   wPanel.setFile(pFilename);
		else
		   wPanel.setText(pText);
		if (pProxy != null)
		{
		   wPanel.setProxy(EventProxy.getInstance(pProxy));
		}
		wPanel.setTriggerLevel(pTriggerLevel);
		return wPanel;
	}

	public  AbstractHtmlPanel setWizard(String pFilename, String pText, String pProxy, int pTriggerLevel)
	{
		HtmlPanel wPanel = (HtmlPanel) mWindowsFactory.getWizardPanel();
		if (pText == null && pFilename != null)
		   wPanel.setFile(pFilename);

		else if ( pText != null)
		   wPanel.setText(pText);

		if (pProxy != null)
		{
		   wPanel.setProxy(EventProxy.getInstance(pProxy));
		}
		wPanel.setTriggerLevel(pTriggerLevel);
		Container wContainer = mWindowsFactory.getWizardContainer();
		mLeftPage.setRightComponent(wContainer);
		return wPanel;
	}

 	public void setHelp(String pHelpFile)
 	{
 		HtmlExplorer hlp = mWindowsFactory.getHelpPanel();
 		try {
			hlp.setPage("file:" + pHelpFile);
		} catch (IOException e) {
			System.err.println("Cannot set help for url 'file:" + pHelpFile +"'");
			System.err.println(e.getMessage());
		}
 	}
	/**
	 * @param pTable
	 */
	public void setTable(Table pTable)
	{
		Table.CustomTableCellRenderer renderer = pTable.new CustomTableCellRenderer();
		JTable table = new JTable(pTable.getDataModel());
		table.doLayout(); //sizeColumnsToFit(true);
		table.setDefaultRenderer(Integer.class, renderer); 
		mWindowsFactory.getTablePanel().add(table,0);
		mTable = table;
		show();
	}

	/**
	 * 
	 */
	public  String getInput(boolean pLine)
	{
		TextArea in = (TextArea)mWindowsFactory.getInput();
		String txt = in.getText();
		int pos1 = in.getSelectionStart();
		int pos2 = in.getSelectionEnd();
		String rest = txt.substring(pos2);
		while (rest.charAt(0)==' ')
		{
			rest = rest.substring(1);
			pos2++;		
		}
		String result = "";
		StringTokenizer st = new StringTokenizer(rest, (pLine?"\n":" "));
		if (st.hasMoreTokens())
		{
			result = st.nextToken();
			pos1 = pos2;
			pos2 += result.length();
		}
		in.setSelectionStart(pos1);
		in.setSelectionEnd(pos2);
		return result;
	}

	public  void updateTable() 
	{
		mTable.repaint();
	}

	/**
	 * @param string help target
	 */
	public  void doHelp() 
	{
//		System.out.println("DoHelp called"); // + string);
		mScreen.setRightComponent(mWindowsFactory.getHelpContainer());
//		mRightPage.setRightComponent(mWindowsFactory.getHelpContainer());
	}
	
	public HtmlExplorer getHelpExplorer()
	{
		return mWindowsFactory.getHelpPanel();
	}

	/**
	 * 
	 */
	public  void stopHelp() {
//		System.out.println("stopHelp called");
		mScreen.setRightComponent(mRightPage);
//		mRightPage.setRightComponent(mWindowsFactory.getTableContainer());
	}

	/**
	 * 
	 */
	public MessagesPanel getMessagesWindow()
	{
		return mWindowsFactory.getMessages();
	}

	/**
	 * 
	 */
	public  void setProgram() 
	{
		mLeftPage.setLeftComponent(mWindowsFactory.getMainContainer());
		show();
	}

	private Stack popups = new Stack();
	/**
	 * @param b
	 * @param string
	 */
	public void showPopup(boolean b, String pMessage ,String pTitle) {
		Component m = mWindowsFactory.getMain();
		JOptionPane.showMessageDialog(m, pMessage, pTitle, 
			(b?JOptionPane.INFORMATION_MESSAGE:JOptionPane.ERROR_MESSAGE));
//		m.repaint();
//		showConfirmDialog(this, string);	
	}
	
}

