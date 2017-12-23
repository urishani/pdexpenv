/*
 * Created on 17/12/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment.control;

import java.util.Properties;

import javax.swing.text.JTextComponent;

import tools.html.AbstractHtmlPanel;
import tools.html.HComponent;
import tools.logs.Log;
import experiment.Main;
import experiment.MyProperties;
import experiment.Table;
import experiment.View;
import gi.CodeInterpreter;
import gi.CodeProgram;

/**
 * @author gshani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CodingControl extends AbstractWizardControl {

	/**
	 * @param pProperties
	 * @param pView
	 * @param pTable
	 */
	public CodingControl(MyProperties pProperties, View pView, Table pTable) {
		super(pProperties, pView, pTable);
	}

	public boolean onEvent(String pName, String pValue, AbstractHtmlPanel pPanel) {
// 		println("onEvent in CodingControl: " + pName + " -- " + pValue);
// 		mView.show();
 		if (pName.equalsIgnoreCase("init"))
 			return true;
 			
 		// disable execute, disable compile
 		else if (pName.equalsIgnoreCase("Program"))
 		{
			pPanel.getComponent("Coding.Execute").setEnabled(false);
			return true;
							// disable execute, enable compile.
 		}
		
		else if (pName.startsWith("Coding."))
		{
			if (pName.equalsIgnoreCase("Coding.Execute"))
			{
				execute(pPanel);
				pPanel.getComponent("Coding.Execute").setEnabled(false);
				return true;
		}
			else if (pName.equalsIgnoreCase("Coding.Clear"))
			{
				Main.getMonitor().getLog().logEvent(Log.CLEAR, "clear coding");
				Properties p = pPanel.getTextFields();
				p.remove("Coding.Execute");
				p.remove("Coding.Clear");
				p.remove("Coding.Compile");
				pPanel.setTextFields(p, true);
				pPanel.getComponent("Coding.Execute").setEnabled(false);
				// disable execute, disable compile
				return true;
			}
			else if (pName.equalsIgnoreCase("Coding.Compile"))
			{
				mView.getMessagesWindow().messagesClear(); 

				if (verify(pPanel))
				{
					pPanel.getComponent("Coding.Execute").setEnabled(true);
				}
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see experiment.AbstractControl#start()
	 */
	public void start() {
	}

	public String getDomain()
	{
		return "CodingControl";
	}



	/* (non-Javadoc)
	 * @see experiment.control.AbstractWizardControl#createCode(tools.HtmlPanel)
	 */
	protected String createCode(AbstractHtmlPanel pPanel)
	{
		HComponent fld = pPanel.getComponent("Program.content");
		if (fld == null)
		{
			new Exception("Cannot find field 'Program.content' in panel").printStackTrace();
			return "";
		}
		return fld.getText();
	}



	/* (non-Javadoc)
	 * @see experiment.control.AbstractWizardControl#verify(tools.HtmlPanel)
	 */
	protected boolean verify(AbstractHtmlPanel pPanel) {
		String program = createCode(pPanel);

		Main.getMonitor().getLog().logEvent(Log.CODE, program);
		Properties p = mView.getCommandsPanel().getTextFields();
		Main.getMonitor().getLog().logEvent(Log.PARAMS, p.toString().replace('{','"').replace('}','"'));
		if (program.trim().length() == 0){
			printErr("Program is empty!!\n------\n");
			return false;
		}

		CodeInterpreter interpreter = new CodeInterpreter(true);
		CodeProgram code = null;

		try {
			code = interpreter.compile(program);
		} catch (Exception e) {
			printErrln("Error: " + e.getMessage() + "\n Tip: Some errors happen if you forget a ; at the end of a statement.");
			Main.getMonitor().getLog().logEvent(Log.COMPILECODE, "error");
			
		}

		if (code != null)
		{
			Main.getMonitor().getLog().logEvent(Log.COMPILECODE, "ok");
			println("Program compiled without errors\n Click 'Execute' to run it.\n");
//			mView.getMessagesWindow().infoMessage("Program compiled without errors\nExecuting...\n");
		}
		mView.getMessagesWindow().infoMessage("-----\n");
		return code != null;
	}
	/* 
	 * Identifies the type of panel this one controlled: WIZARD or CODING.
	 * Used in determining how to handle the execute() and compile() methods.
	 */
	protected int getType() {
		return CODING;
	}

}