/*
 * Created on 16/11/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment.control;

import java.util.Properties;

import tools.html.AbstractHtmlPanel;
import tools.logs.Log;
import experiment.EventProxy;
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
public abstract class AbstractWizardControl 
	extends AbstractControl
	implements EventProxy.ProxyListener,
	CodeProgram.MyPrintStream,
	CodeProgram.MyReadStream
{
	public AbstractWizardControl(MyProperties pProperties, View pView, Table pTable)
	{
		super(pProperties, pView, pTable);
	}
			
		/**
		 * @see applications.HtmlPanel.HTMLListener#onEvent(java.lang.String, java.lang.String)
		 */
		public boolean onEvent(String pName, String pValue, AbstractHtmlPanel pPanel) 
		{
//			println("onEvent in WizardControl[" + getDomain() + "]: " + pName + " -- " + pValue);
			if (pName.equalsIgnoreCase("init"))
			{
				hideCode(pPanel);
				return true;
			}
			if (pName.startsWith("Wizard."))
			{
				if (pName.equalsIgnoreCase("Wizard.Execute"))
				{
					if (verify(pPanel))
					{
						Main.getMonitor().getLog().logEvent(Log.VERIFY, "correct");
						execute(pPanel);
					}
					else
					{
						Main.getMonitor().getLog().logEvent(Log.VERIFY, "fail");
					}
					return true;
				}
				if (pName.equalsIgnoreCase("Wizard.ShowCode"))
				{
					if (verify(pPanel))
					{
						Main.getMonitor().getLog().logEvent(Log.SHOWCODE, "show");
						Main.getMonitor().codeShown(true);
						String code = createCode(pPanel);
						showCode(pPanel, code);
					//ToDo	
					}
					return true;
				}
				if (pName.equalsIgnoreCase("Wizard.Clear"))
				{
					Main.getMonitor().getLog().logEvent(Log.CLEAR, "clear wizard");
					Properties p = pPanel.getTextFields();
					p.remove("Wizard.Execute");
					p.remove("Wizard.Clear");
					p.remove("Wizard.HideCode");
					p.remove("Wizard.ShowCode");
					pPanel.setTextFields(p, true);
					return true;
				}
				if (pName.equalsIgnoreCase("Wizard.HideCode"))
				{
					Main.getMonitor().getLog().logEvent(Log.SHOWCODE, "hide");
					hideCode(pPanel);
					return true;
				}
			}
			return false;
		}

		/**
		 * 
		 */
		public abstract void start();

		/**
		 * @see gi.CodeProgram.MyReadStream#readln()
		 */
		public String readln() {
			return mView.getInput(true);
		}
		/**
		 * @see gi.CodeProgram.MyReadStream#read()
	 	*/
		public String read() {
			return mView.getInput(false);
		}

		

	/**
	 * 
	 */
	abstract public String getDomain();

	protected static int WIZARD = 1;
	protected static int CODING = 2;

	static private CodeInterpreter mInterpreter = null;

	private int[][] mSaveTable = null;
	protected void saveTableState()
	{
		mSaveTable = mTable.getOrigTable();		
	}
	

	/**
	 * Compiles and eecute the program in pCode
	 * @param pCode a program to be compiled and executed.
	 */
	protected void compile(String  pCode) 
	{
		String program = pCode;
//		System.out.println("program= " + program);
		if (mInterpreter == null)
			mInterpreter = new CodeInterpreter(true);
		CodeProgram code = null;
		try {
			code = mInterpreter.compile(program);
		} catch (Exception e) {
			printErrln("Error: " + e.getMessage());
		}

		if (code == null)
		{
			printErr("Program compiled with errors\n------\n");
			return;
		} else
		{
//			code.print();
			code.setTable(mTable);
			code.setOut(this); 
			code.setIn(this);
			String rc = code.execute(); // This is a success/fail string message for the execution.
			String message = ((getType() == WIZARD)?"Program compiled without errors\n":"") + rc;
			if (rc.startsWith("Error"))
				printErrln(message);
			else
				println(message);
		}
		mView.getMessagesWindow().infoMessage("-----\n");
		mView.updateTable();
		mView.show();
	}
	
	protected int getType()
	{
		return WIZARD;
	}
	
	/**
	 * exeuctes via the <code>compile()</code> method the program created within the
	 * <code>createCode()</code> method, and checks the results and 
	 * tells the user about his/her success and rewards for this task.
	 * 
	 * @param pPanel which is being executed
	 */
	protected void execute(AbstractHtmlPanel pPanel) 
	{
		saveTableState();
		String code = createCode(pPanel);
//		if (pPanelType.startsWith("Coding"))
		compile(code);
//		println("Executing Program...\n");
		int success = Main.getMonitor().checkTest(State.TASK_CURRENT);
		mView.doLayout();
		int reward = Main.getMonitor().getReward();
		int mTask = State.TASK_CURRENT;
		switch(success){
			case Table.ALL_ERROR:
				Main.getMonitor().getLog().logEvent(Log.EXECUTE, "all wrong");
				Main.getMonitor().getLog().logEvent(Log.REWARD, "Penalty:" +reward);
				mView.showPopup(false, "Incorrect results.\n" +
					"A penalty of " + Math.abs(reward) + ((Math.abs(reward) == 1)?" point has":" points have") +
					" been taken off of your score.\n" + 
					"Click OK to get the next task.", "Task Execution");
				Main.getMonitor().newProblem(State.COURSE_CURRENT, State.TASK_PSEUDO, State.PHASE_CURRENT, true);
			break;
			case Table.AREA_ERROR:
				Main.getMonitor().getLog().logEvent(Log.EXECUTE, "area_error");
				Main.getMonitor().getLog().logEvent(Log.REWARD, "Penalty:" +reward);
				mView.showPopup(false, "Incorrect results.\n" +
					"You performed the correct action on the cell/s you selected,\n" +
					"BUT!! You did not select the correct cell/s\n" +
					"A penalty of " + (Math.abs(reward)) + ((Math.abs(reward) == 1)?" point has":" points have") +
					" been taken off of your score.\n" + 
					"Click OK to get the next task.", "Task Execution");
				Main.getMonitor().newProblem(State.COURSE_CURRENT, State.TASK_PSEUDO, State.PHASE_CURRENT, true);
			break;
			case Table.VALUE_ERROR:
				Main.getMonitor().getLog().logEvent(Log.EXECUTE, "value_error");
				Main.getMonitor().getLog().logEvent(Log.REWARD, "Penalty:" +reward);
				mView.showPopup(false, "Incorrect results.\n " +
					"You selected the correct cell/s,\n " +
					"BUT!! The value in the cell/s you selected \n " +
					"don't match the task requirements\n" + 
					"A penalty of " + (Math.abs(reward)) + ((Math.abs(reward) == 1)?" point has":" points have") +
					" been taken off of your score.\n"+
					"Click OK to get the next task.", "Task Execution");
				Main.getMonitor().newProblem(State.COURSE_CURRENT, State.TASK_PSEUDO, State.PHASE_CURRENT, true);
			break;
			case Table.METHOD_ERROR:
				Main.getMonitor().getLog().logEvent(Log.EXECUTE, "method_error");
				Main.getMonitor().getLog().logEvent(Log.REWARD, "Penalty:" +reward);
				mView.showPopup(false, "Correct results, BUT!!...\n " +
					"All the calculations need to be done by your program, \n " +
					"you did some calculations in your head...\n" + 
					"You must have the program do all the calculations!!\n" + 
					"You will get no penalty, but also no reward.\n" +
					"Click OK to get the next task.", "Task Execution");
				Main.getMonitor().newProblem(State.COURSE_CURRENT, State.TASK_PSEUDO, State.PHASE_CURRENT, true);
			break;
			case Table.VALUE_AREA_ERROR:
				Main.getMonitor().getLog().logEvent(Log.EXECUTE, "value_area_error");
				Main.getMonitor().getLog().logEvent(Log.REWARD, "Penalty:" +reward);
				mView.showPopup(false, "Incorrect results.\n" +
						"You did not select the correct cell/s\n" +
						" and the value in the cell/s you selected is incorrect too.\n" +
						"A penalty of " + (Math.abs(reward)) + ((Math.abs(reward) == 1)?" point has":" points have") +
						" been taken off of your score.\n" + 
						"Click OK to get the next task.", "Task Execution");
				Main.getMonitor().newProblem(State.COURSE_CURRENT, State.TASK_PSEUDO, State.PHASE_CURRENT, true);
			break;
			case Table.VALUE_METHOD_ERROR:
				Main.getMonitor().getLog().logEvent(Log.EXECUTE, "value_method_error");
				Main.getMonitor().getLog().logEvent(Log.REWARD, "Penalty:" +reward);
				mView.showPopup(false, "Incorrect results!\n " +
					"You selected the correct cell/s,\n" +
					"BUT!! The value in the cell/s you selected is incorrect.\n" +
					"Also, all the calculations need to be done by your program, \n " +
					"you did some calculations in your head...\n" + 
					"You must have the program do all the calculations!!\n" + 
					"A penalty of " + (Math.abs(reward)) + ((Math.abs(reward) == 1)?" point has":" points have") +
					" been taken off of your score.\n" + 
					"Click OK to get the next task.", "Task Execution");
				Main.getMonitor().newProblem(State.COURSE_CURRENT, State.TASK_PSEUDO, State.PHASE_CURRENT, true);
			break;
			case Table.AREA_METHOD_ERROR:
				Main.getMonitor().getLog().logEvent(Log.EXECUTE, "area_method_error");
				Main.getMonitor().getLog().logEvent(Log.REWARD, "Penalty:" +reward);
				mView.showPopup(false, "Incorrect results.\n" +
					"The value of the cell/s you selected is correct,\n" +
					"BUT!! You did not select the correct cell/s.\n" +
					"You also did not use the program to make the calculations,\n" +
					"Do not make calculations in your head - have the program do it instead.\n" +
					"A penalty of " + (Math.abs(reward)) + ((Math.abs(reward) == 1)?" point has":" points have") +
					" been taken off of your score.\n" + 
					"Click OK to get the next task.", "Task Execution");
				Main.getMonitor().newProblem(State.COURSE_CURRENT, State.TASK_PSEUDO, State.PHASE_CURRENT, true);
			break;
			case Table.CORRECT:
				Main.getMonitor().getLog().logEvent(Log.EXECUTE, "correct");
				Main.getMonitor().getLog().logEvent(Log.REWARD, "Reward:" +reward);
				mView.showPopup(true, "Good work!!\n"  + reward + ((reward == 1)?" point has":" points have") + 
					" been added to your score!!", "Task Execution");
				Main.getMonitor().newProblem(State.COURSE_CURRENT, State.TASK_PSEUDO, State.PHASE_CURRENT, true);
			break;
			case Table.NO_ACTION_PROGRAM:
				Main.getMonitor().getLog().logEvent(Log.EXECUTE, "all wrong");
				Main.getMonitor().getLog().logEvent(Log.REWARD, "Penalty:" +reward);
				mView.showPopup(false, "Incorrect results.\n" +
					"Your program did not do anything with the table.\n" + 
					"A penalty of " + Math.abs(reward) + ((Math.abs(reward) == 1)?" point has":" points have") +
					" been taken off of your score.\n" + 
					"Click OK to get the next task.", "Task Execution");
				Main.getMonitor().newProblem(State.COURSE_CURRENT, State.TASK_PSEUDO, State.PHASE_CURRENT, true);
			break;

		}
//		if (success==Table.ALLTRUE)
//		{
//			Main.getMonitor().getLog().logEvent(Log.EXECUTE, "correct");
//			Main.getMonitor().getLog().logEvent(Log.REWARD, "Reward:" +reward);
//			mView.showPopup(true, "Good work!!\n"  + reward + ((reward == 1)?" point has":" points have") + 
//				" been added to your score!!", "Task Execution");
//			Main.getMonitor().newProblem(State.COURSE_CURRENT, State.TASK_NEXT, State.PHASE_CURRENT, true);
//		}		
//		else
//		{
//			Main.getMonitor().getLog().logEvent(Log.EXECUTE, "fail");
//			mView.showPopup(false, "Incorrect results.\n Click OK to get the next task.", "Task Execution");
//			Main.getMonitor().newProblem(State.COURSE_CURRENT, State.TASK_NEXT, State.PHASE_CURRENT, true);
//		}
	}

	/**
	 * @param pPanel
	 * @return
	 */
	protected abstract String createCode(AbstractHtmlPanel pPanel);

	/**
	 * @param pPanel
	 * @return
	 */
	protected abstract boolean verify(AbstractHtmlPanel pPanel);
}

		
