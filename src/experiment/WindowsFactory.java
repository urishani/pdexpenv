/*
 * Created on 07/12/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.TextArea;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import tools.html.AbstractHtmlPanel;
import tools.html.HtmlExplorer;
import tools.html.HtmlPanel;
import tools.windows.AbstractWindowsFactory;
import tools.windows.HeaderComponent;
import tools.windows.WindowContainer;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WindowsFactory extends AbstractWindowsFactory
{
	private HeaderComponent mStatusLine;

	private  Component mMain = null;
	private  Container mMainContainer = null;
	private  Component mInput = null;
	private  Container mInputContainer = null;
	private  MessagesPanel mMessages = null;
	private  Container mMessagesContainer = null;
	private  Component mCommands = null;
	private  Container mCommandsContainer = null;
	private  Container mTablePanel = null;
	private  Container mTableContainer = null;
	private  HtmlExplorer mHelpPanel = null;
	private  WindowContainer mHelpContainer = null;
	private  AbstractHtmlPanel mWizardPanel = null;
	private  Container mWizardContainer = null;



	public WindowsFactory(MyProperties pProperties, EventProxy pProxy)
	{
		super ( pProperties, pProxy);

//		programming window
		mMain =	new JTextArea(10,50);
		mMain.setBackground( new Color(Integer.parseInt(mProperties.getProperty("Project.Window.Background"), 16)));
		mMain.setForeground( new Color(Integer.parseInt(mProperties.getProperty("Project.Window.Foreground"), 16)));
		mMainContainer = createWindowContainer("Programming", mMain);
		Font mono = new Font("Monospaced",Font.BOLD,16);
		mMain.setFont(mono);
// Optional Input window
		mInput = new TextArea();
		mInput.setBackground(new Color(Integer.parseInt(mProperties.getProperty("Project.Window.Background"), 16)));
		mInput.setForeground(new Color(Integer.parseInt(mProperties.getProperty("Project.Window.Foreground"), 16)));
		mInputContainer = createWindowContainer("Input", mInput);

// Messages window
		Box msgs = new Box(BoxLayout.Y_AXIS);
		msgs.add(new JLabel(""));
		mMessages = new MessagesPanel(msgs, mProperties);
		mMessagesContainer = createWindowContainer("Messages", mMessages);
//		mMessagesContainer.setBackground(mMessages.getBackground());

// Commands panel
   		HtmlPanel cmds = HtmlPanel.create(mProperties.getArgsPropery("Commands.Args"), "Commands", null);
//		cmds.setFile( mProperties.getProperty("Project.WorkDir") + "/" + mProperties.getProperty("Commands.Filename.Login")); //"data/Commands/test.html");		
		cmds.setOpaque(true);
		mCommands = cmds;
		mCommandsContainer = createWindowContainer("Commands", mCommands);

//	Wizard panel
		mWizardPanel = HtmlPanel.create(mProperties.getArgsPropery("Wizard.Args"), "Wizard", null);
		mWizardContainer = createWindowContainer("Wizard", mWizardPanel);
		mWizardPanel.setOpaque(true);

// Help panel		
		mHelpPanel = new HtmlExplorer();
		mHelpContainer = createWindowContainer("Help", mHelpPanel);
		mHelpPanel.setButtonsPanel( mHelpContainer.getHtmlPanel());
		
// Table panel		
		Box tmp = new Box(BoxLayout.X_AXIS);
		tmp.add(Box.createHorizontalStrut(10));
		mTablePanel = new Box(BoxLayout.Y_AXIS);
		tmp.add(mTablePanel);
		tmp.add(Box.createHorizontalStrut(10));
		mTableContainer = createWindowContainer("Table", tmp);
		
// Status line
	   String doStatus_S = mProperties.getProperty("Status");
	   if (doStatus_S != null && doStatus_S.trim().equals("1"))
	   {
			mStatusLine = createHeader(mProperties.getProperty("Status.Title"), 
						mProperties.getProperty("Status.Title.Buttons"));
	   }
	}

	public  Component getCommands() {
		return mCommands;
	}

	public  AbstractHtmlPanel getWizardPanel() {
		return mWizardPanel;
	}

	public  Container getWizardContainer() {
		return mWizardContainer;
	}

	public  Container getCommandsContainer() {
		return mCommandsContainer;
	}

	public  HtmlExplorer getHelpPanel() {
		return (HtmlExplorer)mHelpPanel;
	}

	public  WindowContainer getHelpContainer() {
		return mHelpContainer;
	}

	public  Component getInput() {
		return mInput;
	}

	public Container getInputContainer() {
		return mInputContainer;
	}

	public Component getMain() {
		return mMain;
	}

	public Container getMainContainer() {
		return mMainContainer;
	}

	public MessagesPanel getMessages() {
		return mMessages;
	}

	public Container getMessagesContainer() {
		return mMessagesContainer;
	}

	public Container getTableContainer() {
		return mTableContainer;
	}

	public Container getTablePanel() {
		return mTablePanel;
	}
	
	public HtmlPanel getCommandsPanel() {
		return (HtmlPanel) mCommands;
	}

	public Component getStatusLine() {
		return mStatusLine;
	}
}

