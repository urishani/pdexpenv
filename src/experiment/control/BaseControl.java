/*
 * Created on 16/11/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment.control;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import tools.html.AbstractHtmlPanel;
import tools.html.HComponent;
import tools.html.HtmlExplorer;
import tools.logs.Log;
import experiment.Main;
import experiment.MyProperties;
import experiment.Table;
import experiment.View;


/**
 * @author gshani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BaseControl  extends AbstractWizardControl
{
	/**
	 * @param pProperties
	 * @param pView
	 * @param pTable
	 */
	public BaseControl(MyProperties pProperties, View pView, Table pTable) {
		super(pProperties, pView, pTable);
	}

	
	/**
	 * @see applications.HtmlPanel.HTMLListener#onEvent(java.lang.String, java.lang.String)
	 */
	public boolean onEvent(String pName, String pValue, AbstractHtmlPanel pPanel) {
		boolean done = false;
		if (pName.equalsIgnoreCase("Compile"))
		{
			compile();
			done= true;
		}

		else if (pName.equalsIgnoreCase("Message.clear"))
		{
			mView.getMessagesWindow().messagesClear(); 
			done= true;
		}
		else if (pName.startsWith("Login."))
		{
			if (pName.equalsIgnoreCase("Login.Timer"))
			{
				if (pValue.equalsIgnoreCase("Start")) done = true;
				else if (pValue.equalsIgnoreCase("tick"))
				{
					HComponent b = pPanel.getComponent(pName);
					int cval = Integer.parseInt(b.getText().trim());
					cval --;
					b.setText(Integer.toString(cval));
					//System.out.println("xxx  xxx");
					done= true;
				} else if (pValue.equalsIgnoreCase("end"))
				{
					HComponent b = pPanel.getComponent(pName);
					b.setText("!! Time out !!");					
					done= true;
				}	
			} 
			else if (pName.equalsIgnoreCase("Login.Task.Wizard"))
			{
				mView.setMode(View.TEST_SCREEN);
				mView.setCommands(
					mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Commands.Filename.Wizard"),
					null, "Commands", AbstractHtmlPanel.TRIGGER_BUTTONS
					);
				done= true;
			}

			if (pName.equalsIgnoreCase("Login.Task.ModWizard"))
			{
				mView.setMode(View.TEST_SCREEN);
				mView.setCommands(
					mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Commands.Filename.ModWizard"),
					null, "Commands", AbstractHtmlPanel.TRIGGER_BUTTONS
				);
				done= true;
			}

			if (pName.equalsIgnoreCase("Login.Task.Coding"))
			{
				mView.setMode(View.TEST_SCREEN);
				mView.setCommands(
					mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Commands.Filename.Coding"),
					null, "Commands", AbstractHtmlPanel.TRIGGER_BUTTONS
				);
				mView.setWizard(
					mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.Coding"),
					null, "CodingControl", AbstractHtmlPanel.TRIGGER_BUTTONS
				
				);
				done= true;
			}
			
		} 
		else if (pName.startsWith("Table.Reset"))
		{
			if (pName.equalsIgnoreCase("Table.Reset"))
			{
				Main.getMonitor().getLog().logEvent(Log.RESET, "reset table");
				mTable.resetTable();
				done= true;
			}
		}
		else if (pName.equalsIgnoreCase("Status.Timer"))
		{
			Properties p = Main.getMonitor().getTestProperties();
			pPanel.setTextFields(p, false);

/*			Enumeration k = p.keys();
			while (k.hasMoreElements())
			{
				
				String prop = (String)k.nextElement();
				String val = p.getProperty(prop);
				JLabel fld = (JLabel)pPanel.getComponent(prop);
				fld.setText(prop);
			}
*/		
		} else if (pName.startsWith("Commands"))
		{
			if (pName.equalsIgnoreCase("Commands.SkipTask"))
			{				
				Main.getMonitor().getLog().logEvent(Log.SKIP, "skip task");
				Main.getMonitor().newProblem(State.COURSE_CURRENT, State.TASK_PSEUDO, State.PHASE_CURRENT, true);
				done= true;
			}
		}
		else if (pName.startsWith("Wizard."))
		{
			if (pName.equalsIgnoreCase("Wizard.Start"))
			{
				mView.setWizard(
					mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename"), null, null, AbstractHtmlPanel.TRIGGER_BUTTONS);
				pPanel.getComponent("Wizard.Start").setEnabled(false );
				pPanel.getComponent("Wizard.Stop").setEnabled(true );
				pPanel.getComponent("Compile").setEnabled(false );
				done= true;
			}
//	if statements for the no remainder regular wizard tools:

			else if (pName.equalsIgnoreCase("Wizard.RemAreaReg"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.RemAreaReg"), null, "Wizard.RemAreaReg", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			} 
			
			else if (pName.equalsIgnoreCase("Wizard.RemRowReg"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.RemRowReg"), null, "Wizard.RemRowReg", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}
			
			else if (pName.equalsIgnoreCase("Wizard.RemCellReg"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.RemCellReg"), null, "Wizard.RemCellReg", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}    

//	if statements for the no remainder modified wizard tools:

			else if (pName.equalsIgnoreCase("Wizard.RemAreaMod"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.RemAreaMod"), null, "Wizard.RemAreaMod", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			} 
		
			else if (pName.equalsIgnoreCase("Wizard.RemRowMod"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.RemRowMod"), null, "Wizard.RemRowMod", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}
		
			else if (pName.equalsIgnoreCase("Wizard.RemCellMod"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.RemCellMod"), null, "Wizard.RemCellMod", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}    

//	if statements for the add a number regular wizard tools:

			else if (pName.equalsIgnoreCase("Wizard.AddAreaReg"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.AddAreaReg"), null, "Wizard.AddAreaReg", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}
			
			else if (pName.equalsIgnoreCase("Wizard.AddRowReg"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.AddRowReg"), null, "Wizard.AddRowReg", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}
			
			else if (pName.equalsIgnoreCase("Wizard.AddCellReg"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.AddCellReg"), null, "Wizard.AddCellReg", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}

//	if statements for the add a number modified wizard tools:

			else if (pName.equalsIgnoreCase("Wizard.AddAreaMod"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.AddAreaMod"), null, "Wizard.AddAreaMod", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}
		
			else if (pName.equalsIgnoreCase("Wizard.AddRowMod"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.AddRowMod"), null, "Wizard.AddRowMod", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}
		
			else if (pName.equalsIgnoreCase("Wizard.AddCellMod"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.AddCellMod"), null, "Wizard.AddCellMod", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}

//	if statements for the multiply by regular wizard tools:

			else if (pName.equalsIgnoreCase("Wizard.MultAreaReg"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.MultAreaReg"), null, "Wizard.MultAreaReg", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}
			
			else if (pName.equalsIgnoreCase("Wizard.MultRowReg"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.MultRowReg"), null, "Wizard.MultRowReg", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}
			
			else if (pName.equalsIgnoreCase("Wizard.MultCellReg"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.MultCellReg"), null, "Wizard.MultCellReg", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}

//	if statements for the multiply by modified wizard tools:

			else if (pName.equalsIgnoreCase("Wizard.MultAreaMod"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.MultAreaMod"), null, "Wizard.MultAreaMod", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}
		
			else if (pName.equalsIgnoreCase("Wizard.MultRowMod"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.MultRowMod"), null, "Wizard.MultRowMod", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}
		
			else if (pName.equalsIgnoreCase("Wizard.MultCellMod"))
			{
				Main.getMonitor().getLog().logEvent(Log.SELECT_WIZARD, pName);
				mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" + 
					mProperties.getProperty("Wizard.Filename.MultCellMod"), null, "Wizard.MultCellMod", AbstractHtmlPanel.TRIGGER_BUTTONS);
				done= true;
			}
// end of the wizard panel selecting conditions ----->

			else if (pName.equalsIgnoreCase("Wizard.Stop"))
			{
				mView.setProgram();
				pPanel.getComponent("Wizard.Start").setEnabled(true );
				pPanel.getComponent("Wizard.Stop").setEnabled(false );
				pPanel.getComponent("Compile").setEnabled(true );
				done= true;
			}				
		}
		else if (pName.equalsIgnoreCase("Help.Start"))
		{
			//System.out.println("Doing Help for :" + pName);
			mView.doHelp(); // pName); //"Data/Help/Test.html"); - by null, it will take the preset one.
			Main.getMonitor().getLog().logEvent(Log.HELP, "START");
			done= true;
		} else if (pName.equalsIgnoreCase("Help.Stop"))
		{
			mView.stopHelp();
			Main.getMonitor().getLog().logEvent(Log.HELP, "STOP");
			done= true;
		} else if (pName.equalsIgnoreCase("Help.Forward"))
		{
			HtmlExplorer htmle = mView.getHelpExplorer(); 
			htmle.doForward();
			done= true;
		} else if (pName.equalsIgnoreCase("Help.Back"))
		{
			HtmlExplorer htmle = mView.getHelpExplorer(); 
			htmle.doBackward();
			done= true;
		} else if (pName.equalsIgnoreCase("Help.URL"))
		{
			HtmlExplorer htmle = mView.getHelpExplorer(); 
			pPanel.getComponent("Help.Back").setEnabled(htmle.hasBackward());
			pPanel.getComponent("Help.Forward").setEnabled(htmle.hasForward());			
			Log l = Main.getMonitor().getLog();
				if (l != null)
					l.logEvent(Log.HELP, pValue);
		} else if (pName.equalsIgnoreCase("Help.Reload"))
		{
			HtmlExplorer htmle = mView.getHelpExplorer();
			htmle.reload();
		} else if (pName.equalsIgnoreCase("Help.Edit"))
		{
			HtmlExplorer htmle = mView.getHelpExplorer();
			String fileName= htmle.getCurrent();
			try {
				fileName = (new URL(fileName)).getFile();
			} catch (MalformedURLException e2) {
				e2.printStackTrace();
			}
			String cmd = mProperties.getProperty("Help.Edit.Command") + " " + fileName;
			System.out.println("Editing using command: '" + cmd + "'");
			try {
				Process p = Runtime.getRuntime().exec(cmd);
				p.waitFor();
			} catch (InterruptedException e1) {
//				e1.printStackTrace();
			} catch (IOException e) {
				System.err.println("Failed to execute '" + cmd + "': " + e.getMessage());
				//e.printStackTrace();
			}
		} 

		mView.show();
		return done;
	}

		/**
		 * 
		 */
		private void compile()
		{
			System.err.println("mView.getComponentTextAreaString() is obsolete");
//			String program = mView.getComponTextAreaString();
//			super.compile(program);
		}
		/**
		 * 
		 */
		public void start() {
		}


		/* (non-Javadoc)
		 * @see experiment.control.AbstractControl#getDomain()
		 */
		public String getDomain() {
			return "Commands";
		}

		protected String createCode(AbstractHtmlPanel pPanel) 
		{return null;} // nothing to do here, just need to implement this abstract method.
		protected boolean verify(AbstractHtmlPanel pPanel) 
		{return true;} // nothing to do here, just need to implement this abstract method.
}


