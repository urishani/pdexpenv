/*
 * Created on 16/11/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment.control;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;

import tools.html.AbstractHtmlPanel;
import tools.html.HComponent;
import tools.html.HtmlPanel;
import tools.logs.Log;
import experiment.EventProxy;
import experiment.MyProperties;
import experiment.Table;
import experiment.View;

/**
 * @author gshani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MonitorControl  extends AbstractWizardControl
{

	private int mReward;
	HtmlPanel timerPanel = HtmlPanel.create();
	Log mLog;
	private String LOGIN_PHASE = "Monitor.login.ok";
	private State mState;
	Random mRandomizer = new Random(new Date().getTime());
	public static final int ADDORMULTTYPE = 1;
	public static final int REMTYPE = 2;

	public Log getLog() { return mLog; }
	
	/**
	 * This method is called from outside to start a new test for a specific user and test ID
	 * @param pUser
	 * @param pTestId
	 */
	public void startNewTest(String pUser, String pTestId)
	{
		if (mState != null)
			mState.save(mProperties.getProperty("State.Dir"));
		mState = new State(pUser, pTestId);
		mState.reset();
		if (mLog != null)
		{
			mLog.close();
			mLog = null;
		}
		EventProxy.getInstance(getDomain()).delegate("Monitor.NewTest", "", null);
	}
	/**
	 * This method is called from outside to resume a stopped test for a specific user and test ID
	 * @param pUser
	 * @param pTestId
	 */
	public void resumeTest(String pUser, String pTestId)
	{
		if (mState != null)
			mState.save(mProperties.getProperty("State.Dir"));
		mState = State.restore(new State(pUser, pTestId), mProperties.getProperty("State.Dir"));
		if (mLog != null)
		{
			mLog.close();
			mLog = null;
		}
		EventProxy.getInstance(getDomain()).delegate("Monitor.ResumeTest", "", null);
	}
//	protected void getWeight(){
//		return mWeight;
//	}
	protected void prepareTest( int pKind)
	{
		int rowStart = 0;
		int rowEnd = 0; 
		int colStart = 0; 
		int colEnd = 0;
		int addValue = 0;
		int markWith = 0;
		int multiplyBy = 0; 
		int divideBy = 0;
		int colRunLength = 0;
		int rowRunLength = 0;
		int colLocation = 0;
		int rowLocation = 0;
		int cellCount = 0;
		
		AbstractHtmlPanel p = mView.getCommandsPanel();
		int[] dim = mTable.getDim();
		saveTableState();
		if (mLog != null) mLog.logEvent(Log.TEST, Integer.toString(pKind));		
		
//		Object c = p.getComponent("colRunLength");
		boolean complex = p.getComponent("colRunLength") != null;

//		Object d = p.getComponent("rowRunLength");
		boolean complex2 = p.getComponent("rowRunLength") != null;
		
		int rows = dim[0];
		int cols = dim[1];
		int colSpread = Integer.parseInt(mProperties.getProperty("Project.Table.ColumnSpread"));
		int rowSpread = Integer.parseInt(mProperties.getProperty("Project.Table.RowSpread"));
		
//			colRunLength = mRandomizer.nextInt(cols-2) + 3;
			colRunLength = mRandomizer.nextInt(colSpread) + 2;
			colStart = mRandomizer.nextInt(cols-colRunLength + 1) + 1;
			colEnd = colStart + colRunLength - 1;
			colLocation = mRandomizer.nextInt(cols) + 1;

//			rowRunLength = mRandomizer.nextInt(rows-2) + 3;
			rowRunLength = mRandomizer.nextInt(rowSpread) + 2;
			rowStart = mRandomizer.nextInt(rows-rowRunLength + 1) + 1;
			rowEnd = rowStart + rowRunLength - 1;
			rowLocation =  mRandomizer.nextInt(rows) + 1;

			addValue = 3 + mRandomizer.nextInt(50);
			multiplyBy = 3 + mRandomizer.nextInt(10);
			divideBy = 2 + mRandomizer.nextInt(9);
			markWith = 111 * (mRandomizer.nextInt(6) + 3);

		// calculating number of cells to change in task:
		if (complex2) {cellCount = rowRunLength * colRunLength;} 
		else {cellCount = colRunLength;}
		int mScore =  mState.getScore();
	try
	{
		setIntParam(p, "scoreInPoints", mScore);
		getLog().logEvent(Log.TOTAL_REWARD, "Total Reward:" +mScore);

		switch (pKind)
		{
			case State.TASK_CELL_ADD: 
				setIntParam(p, "rowStart", rowLocation);
				setIntParam(p, "colStart", colLocation);
				setIntParam(p, "addValue", addValue);
			break;
				
			case State.TASK_ROW_ADD: 
				setIntParam(p, "rowStart", rowStart);
				setIntParam(p, "colStart", colStart);
				setIntParam(p, "addValue", addValue);
	
				if (complex) 
				{
					setIntParam(p, "colRunLength", colRunLength);
					setIntParam(p, "cellCount", cellCount);
				}
				else
				{
					setIntParam(p, "colEnd", colEnd);
				}
			break;

			case State.TASK_AREA_ADD:
				setIntParam(p, "rowStart", rowStart);
				setIntParam(p, "colStart", colStart);
				setIntParam(p, "addValue", addValue);

				if (complex)
				{
					setIntParam(p, "rowRunLength", rowRunLength);
					setIntParam(p, "colRunLength", colRunLength);
					setIntParam(p, "cellCount", cellCount);
				}
				else
				{
					setIntParam(p, "rowEnd", rowEnd);
					setIntParam(p, "colEnd", colEnd);
				}
				break;
			}

		} catch (Exception e) 
		{
			System.err.println("Error while prepating test for kind= " + pKind);
			System.err.println("e: '" + e.getMessage() + "'");
		}
		
		try
		{
			switch (pKind)
			{
				case State.TASK_CELL_MULT: 
					setIntParam(p, "rowStart",rowLocation);
					setIntParam(p, "colStart", colLocation);
					setIntParam(p, "multiplyBy", multiplyBy);
				break;
					
				case State.TASK_ROW_MULT: 
					setIntParam(p, "rowStart", rowStart);
					setIntParam(p, "colStart", colStart);
					setIntParam(p, "multiplyBy", multiplyBy);

				if (complex)
				{
					setIntParam(p, "colRunLength", colRunLength);
					setIntParam(p, "cellCount", cellCount);
				}
				else
				{
					setIntParam(p, "colEnd", colEnd);
				}
				break;
				
				case State.TASK_AREA_MULT:
					setIntParam(p, "rowStart",rowStart);
					setIntParam(p, "colStart", colStart);
					setIntParam(p, "multiplyBy", multiplyBy);

				if (complex)
				{
					setIntParam(p, "rowRunLength", rowRunLength);
					setIntParam(p, "colRunLength", colRunLength);
					setIntParam(p, "cellCount", cellCount);
				}
				else
				{
					setIntParam(p, "rowEnd", rowEnd);
					setIntParam(p, "colEnd", colEnd);
				}
				break;
		}

		} catch (Exception e) 
		{
			System.err.println("Error while prepating test for kind= " + pKind);
			System.err.println("e: '" + e.getMessage() + "'");
		}

		try
		{
			switch (pKind)
			{
				case State.TASK_CELL_REM: 
					setIntParam(p, "rowStart", rowLocation);
					setIntParam(p, "colStart", colLocation);
					setIntParam(p, "divideBy", divideBy);
					setIntParam(p, "markWith", markWith);
				break;
				
				case State.TASK_ROW_REM: 
					setIntParam(p, "rowStart", rowStart);
					setIntParam(p, "colStart", colStart);
					setIntParam(p, "divideBy", divideBy);
					setIntParam(p, "markWith", markWith);

				if (complex)
				{
					setIntParam(p, "colRunLength", colRunLength);
					setIntParam(p, "cellCount", cellCount);
				}
				else
				{
					setIntParam(p, "colEnd",  colEnd);
				}
				break;
				
				case State.TASK_AREA_REM:
					setIntParam(p, "rowStart", rowStart);
					setIntParam(p, "colStart", colStart);
					setIntParam(p, "divideBy", divideBy);
					setIntParam(p, "markWith", markWith);

				if (complex)
				{
					setIntParam(p, "rowRunLength", rowRunLength);
					setIntParam(p, "colRunLength", colRunLength);
					setIntParam(p, "cellCount", cellCount);
				}
				else
				{
					setIntParam(p, "rowEnd", rowEnd);
					setIntParam(p, "colEnd", colEnd);
				}
				break;
			}

		} catch (Exception e) 
		{
			System.err.println("Error while prepating test for kind= " + pKind);
			System.err.println("e: '" + e.getMessage() + "'");
		}
		
	}
//	calculating the reward===>
	protected int getReward(){
		return mReward;
//		Integer.parseInt();

	}

	protected int checkTest( int pKind)
		{
			if (pKind == State.TASK_CURRENT)
				pKind = mState.getTask();
			int taskCat = 0;
			int rowStart = 0;
			int rowEnd = 0; 
			int colStart = 0; 
			int colEnd = 0;
			int addValue = 0;
			int markWith = 0;
			int multiplyBy = 0; 
			int divideBy = 0;
			int colRunLength = 0;
			int rowRunLength = 0;
	
			AbstractHtmlPanel p = mView.getCommandsPanel();
			
			//Object compl2 = ;
			boolean complex = p.getComponent("colRunLength") != null;
			
			try {
				switch (pKind)
				{
			// addition cases:
					case State.TASK_CELL_ADD: 
						rowStart = getIntParam(p, "rowStart", null, null);
						rowEnd = rowStart;
						colStart = getIntParam(p, "colStart",  null, null);
						colEnd = colStart;
						addValue = getIntParam(p, "addValue",  null, null);
					break;

					case State.TASK_ROW_ADD: 
					if (complex)
					{
						rowStart = getIntParam(p, "rowStart", null, null);
						rowEnd = rowStart;
						colStart = getIntParam(p, "colStart",  null, null);
						colRunLength = getIntParam(p, "colRunLength", null, null);
						colEnd = colStart + colRunLength - 1;
						addValue = getIntParam(p, "addValue",  null, null);
					}
					else
					{
						rowStart = getIntParam(p, "rowStart", null, null);
						rowEnd = rowStart;
						colStart = getIntParam(p, "colStart",  null, null);
						colEnd = getIntParam(p, "colEnd",  null, null);
						addValue = getIntParam(p, "addValue",  null, null);
					}
					break;

					case State.TASK_AREA_ADD:
					if (complex)
					{
						rowStart = getIntParam(p, "rowStart", null, null);
						rowRunLength = getIntParam(p, "rowRunLength", null, null);
						rowEnd = rowStart + rowRunLength - 1;
						colStart = getIntParam(p, "colStart",  null, null);
						colRunLength = getIntParam(p, "colRunLength", null, null);
						colEnd = colStart + colRunLength - 1;
						addValue = getIntParam(p, "addValue",  null, null);
					}
					else 
					{
						rowStart = getIntParam(p, "rowStart", null, null);
						rowEnd = getIntParam(p, "rowEnd",  null, null);
						colStart = getIntParam(p, "colStart",  null, null);
						colEnd = getIntParam(p, "colEnd",  null, null);
						addValue = getIntParam(p, "addValue",  null, null);
					}
					break;
		// multiplication cases:
					case State.TASK_CELL_MULT: 
						rowStart = getIntParam(p, "rowStart", null, null);
						rowEnd = rowStart;
						colStart = getIntParam(p, "colStart",  null, null);
						colEnd = colStart;
						multiplyBy = getIntParam(p, "multiplyBy", null, null);
					break;

					case State.TASK_ROW_MULT: 
					if (complex)
					{
						rowStart = getIntParam(p, "rowStart", null, null);
						rowEnd = rowStart;
						colStart = getIntParam(p, "colStart",  null, null);
						colRunLength = getIntParam(p, "colRunLength", null, null);
						colEnd = colStart + colRunLength - 1;
						multiplyBy = getIntParam(p, "multiplyBy", null, null);
					}
					else
					{
						rowStart = getIntParam(p, "rowStart", null, null);
						rowEnd = rowStart;
						colStart = getIntParam(p, "colStart",  null, null);
						colEnd = getIntParam(p, "colEnd",  null, null);
						multiplyBy = getIntParam(p, "multiplyBy", null, null);
					}
					break;

					case State.TASK_AREA_MULT:
					if (complex)
					{
						rowStart = getIntParam(p, "rowStart", null, null);
						rowRunLength = getIntParam(p, "rowRunLength", null, null);
						rowEnd = rowStart + rowRunLength - 1;
						colStart = getIntParam(p, "colStart",  null, null);
						colRunLength = getIntParam(p, "colRunLength", null, null);
						colEnd = colStart + colRunLength - 1;
						multiplyBy = getIntParam(p, "multiplyBy", null, null);
					}
					else
					{
						rowStart = getIntParam(p, "rowStart", null, null);
						rowEnd = getIntParam(p, "rowEnd",  null, null);
						colStart = getIntParam(p, "colStart",  null, null);
						colEnd = getIntParam(p, "colEnd",  null, null);
						multiplyBy = getIntParam(p, "multiplyBy", null, null);
					}
					break;
		// REM cases:
					 case State.TASK_CELL_REM: 
						rowStart = getIntParam(p, "rowStart", null, null);
						rowEnd = rowStart;
						colStart = getIntParam(p, "colStart",  null, null);
						colEnd = colStart;
	 					divideBy = getIntParam(p, "divideBy",  null, null);
	 					markWith = getIntParam(p, "markWith",  null, null);
					break;

					case State.TASK_ROW_REM: 
					if (complex)
					{
						rowStart = getIntParam(p, "rowStart", null, null);
						rowEnd = rowStart;
						colStart = getIntParam(p, "colStart",  null, null);
						colRunLength = getIntParam(p, "colRunLength", null, null);
						colEnd = colStart + colRunLength - 1;
						divideBy = getIntParam(p, "divideBy",  null, null);
						markWith = getIntParam(p, "markWith",  null, null);
					}
					else 
					{
						rowStart = getIntParam(p, "rowStart", null, null);
						rowEnd = rowStart;
						colStart = getIntParam(p, "colStart",  null, null);
						colEnd = getIntParam(p, "colEnd",  null, null);
						divideBy = getIntParam(p, "divideBy",  null, null);
						markWith = getIntParam(p, "markWith",  null, null);
					}
					break;

					case State.TASK_AREA_REM:
					if (complex)
					{
						rowStart = getIntParam(p, "rowStart", null, null);
						rowRunLength = getIntParam(p, "rowRunLength", null, null);
						rowEnd = rowStart + rowRunLength - 1;
						colStart = getIntParam(p, "colStart",  null, null);
						colRunLength = getIntParam(p, "colRunLength", null, null);
						colEnd = colStart + colRunLength - 1;
						divideBy = getIntParam(p, "divideBy",  null, null);
						markWith = getIntParam(p, "markWith",  null, null);
					}
					else
					{						
						rowStart = getIntParam(p, "rowStart", null, null);
						rowEnd = getIntParam(p, "rowEnd",  null, null);
						colStart = getIntParam(p, "colStart",  null, null);
						colEnd = getIntParam(p, "colEnd",  null, null);
						divideBy = getIntParam(p, "divideBy",  null, null);
						markWith = getIntParam(p, "markWith",  null, null);
					}
					break;

				}
			} catch (Exception e) 
			{
				println("error In checkTest: " + e.getClass() + " - " + e.getMessage());
				return 0;
			}
	
			int[][] vt = mTable.getOrigTable();
			boolean[][] rtch = mTable.getCleanReadTouch();
			boolean[][] wtch = mTable.getCleanWriteTouch(); // dont need this yet
			int n = vt.length;
			int m = vt[0].length;

			switch (pKind)
			{ 
				case State.TASK_CELL_ADD: case State.TASK_ROW_ADD: case State.TASK_AREA_ADD:
				taskCat = ADDORMULTTYPE;	
				for (int i=0; i<n; i++)// first index runs on rows, 
					for (int j=0; j<m; j++){ //second on columns
						vt[i][j]+= addValue; // adding to value table on every cell in table
						if (i >= rowStart && i <= rowEnd){ 
							if (j >= colStart && j <= colEnd){
								rtch[i][j]=true; // marking readTouch only on select cells
								wtch[i][j]=true; // marking expected write cells
							}
						}
				}
				break;

				case State.TASK_CELL_MULT: case State.TASK_ROW_MULT: case State.TASK_AREA_MULT:
				taskCat = ADDORMULTTYPE;	
					for (int i=0; i<n; i++)// first index runs on rows, 
					for (int j=0; j<m; j++){ //second on columns
						vt[i][j]*= multiplyBy; // multiply every cell in value table
						if (i >= rowStart && i <= rowEnd){ 
							if (j >= colStart && j <= colEnd){
								rtch[i][j]=true; // marking readTouch only on select cells
								wtch[i][j]=true; // marking expected write cells
							}
						}
				}

				break;
				
				case State.TASK_CELL_REM: case State.TASK_ROW_REM: case State.TASK_AREA_REM:
				taskCat = REMTYPE;	
				for (int i=0; i<n; i++)// first index runs on rows, 
					for (int j=0; j<m; j++){ //second on columns
						if (vt[i][j]% divideBy == 0) // checking remainder on all cells in value table
						{
							vt[i][j] = markWith; // marking cells
						}
						if (i >= rowStart && i <= rowEnd){ 
							if (j >= colStart && j <= colEnd){
								rtch[i][j]=true; // marking readTouch only on select cells
								if (vt[i][j]% divideBy == 0) // checking remainder on all cells in value table
								{
									wtch[i][j]=true; // marking expected write cells
								}
							}
						}
				}

				break;

			}

			
			int success = mTable.compareTable(vt, rtch, wtch, taskCat); // sending two tables: valuetable and readtable
			mReward = computeReward(success);
//			String mMessage = getMessage(success); 
			mState.registerReward(mReward);
			mState.registerSuccess(success);
			return success;
		}

	public void codeShown (boolean pShowCode){
		if (pShowCode) {
			mState.registerShowCode(pShowCode);
			int task = mState.getTask();
			getLog().logEvent(Log.SHOWCODE, ""+ task +"" );
		} 
	}
/**
	 * @param success
	 * @return
	 */
	private String getMessage(int pSuccess) {
		String message=null;
		switch(pSuccess){
			case Table.ALL_ERROR:message="";
			break;
			case Table.AREA_ERROR:message="";
			break;
			case Table.VALUE_ERROR:message="";
			break;
			case Table.CORRECT:message="";
			break;
		}


		return message;
	}

	private int computeReward(int success){
	
	int phase = mState.getPhase();
	int course = mState.getCourse();
	int task = mState.getTask();
	
		if (success == Table.ALL_ERROR || success == Table.VALUE_ERROR|| success == Table.AREA_ERROR || success == Table.VALUE_AREA_ERROR || success == Table.VALUE_METHOD_ERROR || success == Table.AREA_METHOD_ERROR || success == Table.NO_ACTION_PROGRAM)
		{ 
			String e = mProperties.getProperty("Project.Penalty.Course." + course + ".Phase." + phase);
			int penalty = Integer.parseInt(e);
			return penalty;
		}
		if (success == Table.METHOD_ERROR){
			String e = mProperties.getProperty("Project.NoReward.Course." + course + ".Phase." + phase);
			int noreward = Integer.parseInt(e);
			return noreward;
		}
//		if (success == Table.CORRECT)
		{
			String r = mProperties.getProperty("Project.Reward.Course." + course + ".Phase." + phase + ".Task." + task);
			int reward = Integer.parseInt(r);
			return reward;
		}


	}
	/**
	 * @param pProperties
	 * @param pView
	 * @param pTable
	 */
	public MonitorControl(MyProperties pProperties, View pView, Table pTable) {
		super(pProperties, pView, pTable);
	}

	/**
	 * 
	 */
	public void start()
	{
		// show login screen
		timerPanel.killTimers(true);
		if (mState == null)
		{
			mState = new State("Jhon Doe", "0000");
			mState.reset();
		}
		try {
			mLog = new Log(mState.getUserId(), mState.getExamId(), mProperties.getProperty("Log.Dir"));
			mLog.start();
		} catch (IOException e) {
			System.err.println("Cannot create Log for user '" + 
			mState.getUserId() + "', exam '" + mState.getExamId() + "': " + e.getMessage());
		}

		if (mState.getCourse() == 0)
		{
			mView.setFullScreen(null, 
				"<body>" +
//				"<include name='data/IntroPages/LoginPanelControl.html'/> " +				"Welcome user '" + mState.getUserId() + "'<br/><br/>\n" + 
				"You will perform test #'" + mState.getExamId() + "<br/><br/>\n" + 
				"This is initial selection screen\n" +				"<br/>\n" +
				"Click when ready to continue: <br/> \n"+
				"<input type='button' id='" + LOGIN_PHASE + ".one' value='OK' />" +
				"<input type='button' id='" + LOGIN_PHASE + ".two' value='OK'/>" +
				"<input type='button' id='" + LOGIN_PHASE + ".three' value='OK'/></body>",
				getDomain(), AbstractHtmlPanel.TRIGGER_BUTTONS);
			mView.setMode(View.FULL_SCREEN);
			return;
		} 
		if (mState.getPhase() != 0)
		{
			switch (mState.getPhase())
			{ 
				case 1: simulateEvent("Monitor.Phase1.Rest.Timer", "end", timerPanel); break;
				case 2: simulateEvent("Monitor.Phase2.Test.Ok", "", timerPanel); break;
				default: simulateEvent("Monitor.Debrief.Outro", "", timerPanel);
			}
			return;
		}
		
	} // now fall through to the on event handling below.

	public void newProblem(int pCourse, int pTask, int pPhase, boolean pNewTable)
	{
		// selectin ".1" in properties file uses the non-end-point task descriptions 
		String difficulty = mProperties.getProperty("Project.Task.Difficulty");
		
		// parameters for table size in properties file:
		int tableDimCol = Integer.parseInt(mProperties.getProperty("Project.Table.Columns"));
		int tableDimRow = Integer.parseInt(mProperties.getProperty("Project.Table.Rows"));
		int phase = mState.getPhase();
		mState.nextCourse(pCourse);
		int course = mState.getCourse(); // implements case pCourse == COURSE_CURRENT
		mState.nextTask(pTask);
		int task = mState.getTask(); // implements case pTask == TASK_CURRENT
		System.err.println("new task = " + mState.getTask());
//		mState.getStatistics().registerSample(new Integer(task));

//		if (pPhase != PHASE_CURRENT)
//			mPhase = pPhase;
// we currently ignore the paramater for pPhase cuz we can do nothin about it			
		
		if (pNewTable)
		{
			mTable.initialize(tableDimRow, tableDimCol, Table.RANDOM);
		}
		
		AbstractHtmlPanel pCommands = mView.setCommands
		(
			
			mProperties.getProperty("Project.WorkDir") + "/" +
			mProperties.getProperty("Commands.Filename.Course." + course + ".Phase."+ phase +"." + task).trim() + difficulty,
			null, "Commands", AbstractHtmlPanel.TRIGGER_BUTTONS
		);
		if (course == 3 || phase ==2)
		{
			mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" +
				mProperties.getProperty("Wizard.Filename.Coding"),
				null, "CodingControl", AbstractHtmlPanel.TRIGGER_BUTTONS
			);
		} else
		{
			mView.setWizard(
				mProperties.getProperty("Project.WorkDir") + "/" +
				mProperties.getProperty("Wizard.Filename.Empty"),
				null, "Commands", AbstractHtmlPanel.TRIGGER_BUTTONS
			);
		}
		mView.setHelp
		(
			mProperties.getProperty("Project.HelpDir") + "/" +
			mProperties.getProperty("Help.Filename.Course." + course + ".Phase." + phase + "." + task)
		);
		prepareTest(task);
		//pCommands.repaint();
	}
	
	
	/**
	 * @see applications.HtmlPanel.HTMLListener#onEvent(java.lang.String, java.lang.String)
	 */
	public boolean onEvent(String pName, String pValue, AbstractHtmlPanel pPanel) 
	{
		
		// Handle timer ticks
		if (pName.equalsIgnoreCase("Monitor.Phase1.Rest.Button"))  
			// phase1 rest button event.
			if (doRestTimerTick(pName, pValue, pPanel)) return true;
		if (pName.equalsIgnoreCase("Monitor.Phase2.Rest.Button"))  
			// phase2 rest timer event.
			if (doRestTimerTick(pName, pValue, pPanel)) return true;
			
		// Handle the actual flow		
		if (pName.equalsIgnoreCase("Monitor.NewTest"))  
			return doStart();
		if (pName.equalsIgnoreCase("init"))
			// ignore this one
			return true;
		if (pName.equalsIgnoreCase("Monitor.ResumeTest"))  
			return doStart();
		if (pName.equalsIgnoreCase("Monitor.StopTest"))  
			return doStopTest();
		if (pName.startsWith(LOGIN_PHASE)) 
			// login panel finished.
			return doLogin(pName);//instruction
		if (pName.equalsIgnoreCase("Monitor.Phase1.Test.ok"))  
			// login panel finished.
			return doPhaseTests();
		if (pName.equalsIgnoreCase("Monitor.Phase1.Rest.Timer") && pValue.equalsIgnoreCase("end")) 
			// login panel finished.
			return doPhase1TrialIntro(pPanel);
		if (pName.equalsIgnoreCase("Monitor.Phase1.Rest.Timer") && pValue.equalsIgnoreCase("OK"))   
			// login panel finished.
			return doPhase1TrialIntro(pPanel);// go to the screen between rounds.
		if (pName.equalsIgnoreCase("Monitor.Phase1.Rest.Button"))  
			// phase2 rest button event.
			return doPhase1TrialRest(pName, pValue, pPanel);
		if (pName.equalsIgnoreCase("Monitor.Phase2.Intro")) 
			return doPhase2Intro();
		if (pName.equalsIgnoreCase("Monitor.Phase2.Test.Ok") && pValue.equalsIgnoreCase("OK"))
			// finished introduction to phase 2
			return doPhase2Trial(pPanel);
		if (pName.equalsIgnoreCase("Monitor.Phase2.Test.Ok") && pValue.equalsIgnoreCase("end"))
			// finished resting in a phase 2 round
			return doPhase2Trial(pPanel);
		if (pName.equalsIgnoreCase("Monitor.Phase2.Rest.Timer") && pValue.equalsIgnoreCase("end")) 
			// login panel finished.
			return doPhase2Trial(pPanel);
		if (pName.equalsIgnoreCase("Monitor.Phase2.Trial.Intro"))
			// phase2 rest timer finished finished.
			return doPhaseTests(); // doPhase2TrialIntro();
		if (pName.equalsIgnoreCase("Monitor.Phase2.Rest.Button"))  
			// phase2 rest timer event.
			return doPhase2TrialRest(pName, pValue, pPanel);
			//return doPhase2TrialIntro();
 		if (pName.equalsIgnoreCase("Monitor.Debrief.Outro"))  
			// finished test 2
			return doPhase2Outro();
		if (pName.equalsIgnoreCase("Monitor.end"))  
			// finished full test
			return true;

		return false;
	}

	private boolean doStart() {
		start();
		return true;
	}

	private boolean doPhase2Outro() {
		mLog.logEvent(Log.PHASE, "3");
		mState.setPhase(State.PHASE_THREE);
		mView.setFullScreen(null, 
			"<body>Thank you for taking part in this experiment<br/>" +
			"Your total score for this session of the experiment is:" + mState.getScore() + "<br/>" +
			"<input type='button' id='Monitor.end' value='OK - to continue'/>" +
			"</body>",
			getDomain(), AbstractHtmlPanel.TRIGGER_BUTTONS);
		mView.setMode(View.FULL_SCREEN);
		return true;
	}

	private boolean doEndSession1() {
		mLog.logEvent(Log.SESSION, "End Session 1");
		mState.setPhase(State.PHASE_THREE);
		mView.setFullScreen(null, 
			"<body>This is the end of the first Session.<br/>" +
			"Please speak with the experimenter for details about the next and final session.<br/>" +
			"Your total score for this session of the experiment is:" + mState.getScore() + "<br/>" +
			"<input type='button' id='Monitor.end' value='OK - to continue'/>" +
			"</body>",
			getDomain(), AbstractHtmlPanel.TRIGGER_BUTTONS);
		mView.setMode(View.FULL_SCREEN);
		return true;
	}


	private boolean doRestTimerTick(
		String pName,
		String pValue,
		AbstractHtmlPanel pPanel) {
		if (pValue.equalsIgnoreCase("tick"))
		{
			HComponent b = pPanel.getComponent(pName);
			int cval = Integer.parseInt(b.getText().trim());
			cval --;
			b.setText(Integer.toString(cval));
			return true;
		}
		return false;
	}
	
	private boolean doPhase2TrialRest(
		String pName,
		String pValue,
		AbstractHtmlPanel pPanel) {
		HComponent p = pPanel.getComponent("prefix");
		HComponent b = pPanel.getComponent(pName);
		HComponent l = pPanel.getComponent("units");
		if (pValue.equalsIgnoreCase("end"))
		{
			p.setVisible(false);
			b.setText("Continue");
			l.setText(".      ");
			return true;
		} else if (pValue.equalsIgnoreCase("Continue"))
		{
			simulateEvent("Monitor.Phase2.Trial.Intro", "", pPanel);
			return true;
		}

		return false;
	}
// rest before each round in phase 2 ==>
	private boolean doPhase2Trial(AbstractHtmlPanel pPanel) {
		mState.pausePhaseTime();
		String tdp2_s = mProperties.getProperty("Timer.Delay.Phase2.Test").trim();
		long tdp2 = Integer.parseInt(tdp2_s)*60000;
		long timeLeft = (tdp2 - mState.getPhaseTime())/1000; // in seconds
		int phase2Cnt = mState.getPhase2Cnt();
		System.err.println("timeLeft 2 = " + timeLeft);
		if (timeLeft <= 0 || phase2Cnt <= 0) // not in the middle of a phase step - go to next one)
		{
			String countLimit2_s = mProperties.getProperty("Block.Repeats.Phase2").trim();
			int countLimit2_i = Integer.parseInt(countLimit2_s);
			phase2Cnt ++ ;
			timeLeft = tdp2/1000; // in seconds
			mState.stopPhaseTime();
			mState.setPhase2Cnt(phase2Cnt);
			if (phase2Cnt > countLimit2_i)
			{
				simulateEvent("Monitor.Debrief.Outro", "", pPanel);
				return true;
			}
			if (countLimit2_i == 0) // if this is a single phase session go to end
			{
				simulateEvent("Monitor.Debrief.Outro", "", pPanel);
				return true;
			}
		}
		
		
		mState.setPhase(State.PHASE_TWO);
		mLog.logEvent(Log.PHASE, "2");
		mLog.logEvent(Log.PHASE_CNT, "" + phase2Cnt);

		
		String tdpr_s = mProperties.getProperty("Timer.Delay.Phase2.Rest").trim();
		mView.setFullScreen(null, 
			"<body>This is instructions screen for phase2 round '" + mState.getPhase2Cnt() + "' in test '" + mState.getExamId() + ".<br/>" +
			"<input type='label' id='prefix' value='You can now rest for '/>" +
			"<input type='button' id='Monitor.Phase2.Rest.Button' timer='" + tdpr_s  + 
			"' value='          " + tdpr_s + "          ' /> " + 
			"<input type='label' id='units' value='seconds.'/><br/>"+
			" After that, you will start another test session of " +
			(int)Math.floor(timeLeft/60) +":"+(timeLeft%60) + " minutes.<br/>" +
			"</body>",
			getDomain(), AbstractHtmlPanel.TRIGGER_BUTTONS);
//		mView.setHelp
//		(
//			mProperties.getProperty("Project.HelpDir") + "/" +
//			mProperties.getProperty("Help.Filename.Course." + mState.getCourse() + ".Phase.2.1")
//		);
//		mView.setMode(View.INTRO_SCREEN);
		mView.setMode(View.FULL_SCREEN);
		return true;
	}

	private boolean doPhase2TrialIntro() {
		return doPhaseTests();
/*		
		// Set up the round's timer
		long skipTime = mState.getPhaseTime();
		String tdp2_s = mProperties.getProperty("Timer.Delay.Phase2.Test").trim();
		int tdp2_i = (int)(Integer.parseInt(tdp2_s)*60000 - skipTime)/1000; // in seconds
		AbstractHtmlPanel.Timer tdp2_t = timerPanel.new Timer(tdp2_i, tdp2_i, getMyProxy(), "Monitor.Phase2.Test.OK", timerPanel);

		// make a new problem to solve and set the screen layout appropriately
		mState.setPhase(State.PHASE_TWO);
		newProblem(State.COURSE_THREE, State.TASK_NEXT, State.PHASE_TWO, true);
		
		tdp2_t.start();
		
		mState.startPhaseTime(); // start it counting time.
		mLog.logEvent(Log.END_REST, "");
		
		mView.setMode(View.TEST_SCREEN);

		// Log and resume tha timers.
		mLog.logEvent(Log.END_REST, "");
		mState.resumePhaseTime(); // resume if paused or stopped to count time.
		tdp2_t.start();

		return true;
*/
	}

	private boolean doPhase2Intro() {
		int course = mState.getCourse();
// to stop the session if this is the first of two sessions
// and there is no phase 2.
		String countLimit2_s = mProperties.getProperty("Block.Repeats.Phase2").trim();
		int countLimit2_i = Integer.parseInt(countLimit2_s);
		if (countLimit2_i == 0) // if this is a single phase session go to end
		{
			doEndSession1();
			return true;
		}
		if (course !=3){
			mView.setFullScreen(null, 
				"<body>This is instructions screen for Phase 2 of test '" + mState.getExamId() + "'<br/>" +
				"<include name='data/IntroPages/Instructions.phase.2.course." + course + ".html'/><br/>" +
				"When you click on this button: "+
				"<input type='button' id='Monitor.Phase2.Test.Ok' value='OK'/><br/>" + 
				"This second phase of the test will start</body>",
				getDomain(), AbstractHtmlPanel.TRIGGER_BUTTONS);
			mView.setMode(View.FULL_SCREEN);
			mState.stopPhaseTime(); // if you come through here, timer should be reset.
			mView.setHelp
			(
				mProperties.getProperty("Project.HelpDir") + "/" +
				mProperties.getProperty("Help.Filename.Course." + mState.getCourse() + ".Phase.2.1")
			);
			mView.setMode(View.INTRO_SCREEN);
		}
		else{
			
			mView.setFullScreen(null, 
				"<body>This is instructions screen for Phase 2 of test '" + mState.getExamId() + "'<br/>" +
				"<include name='data/IntroPages/Instructions.phase.2.course." + course + ".html'/><br/>" +
				"When you click on this button: "+
				"<input type='button' id='Monitor.Phase2.Test.Ok' value='OK'/><br/>" + 
				"This second phase of the test will start</body>",
				getDomain(), AbstractHtmlPanel.TRIGGER_BUTTONS);
			mView.setMode(View.FULL_SCREEN);
			mState.stopPhaseTime(); // if you come through here, timer should be reset.

		}
		return true;
	}
// this puts continue if the timer finishes ticking ==>
	private boolean doPhase1TrialRest(
		String pName,
		String pValue,
		AbstractHtmlPanel pPanel) {
		HComponent b = pPanel.getComponent(pName);
		HComponent l = pPanel.getComponent("units");
		HComponent p = pPanel.getComponent("prefix");
		if (pValue.equalsIgnoreCase("end"))
		{
			p.setVisible(false);
			b.setText("Continue");
			l.setText(".      ");
		} else if (pValue.equalsIgnoreCase("Continue"))	
		{						
			simulateEvent("Monitor.Phase1.Test.ok", "", pPanel);
			return true;
		}

		return true;
	}

	private boolean doPhase1TrialIntro(AbstractHtmlPanel pPanel) {
		String tdp1_s = mProperties.getProperty("Timer.Delay.Phase1.Test").trim();
		long tdp1 = Integer.parseInt(tdp1_s)*60000;
		long timeLeft = (tdp1 - mState.getPhaseTime())/1000; // in seconds
		int phase1Cnt = mState.getPhase1Cnt();
		System.err.println("timeLeft 1 = " + timeLeft);
		if (timeLeft <= 0 || phase1Cnt <= 0) // not in the middle of a phase step - go to next one
		{
			String countLimit1_s = mProperties.getProperty("Block.Repeats.Phase1").trim();
			int countLimit1_i = Integer.parseInt(countLimit1_s);
			phase1Cnt++ ;
			mState.setPhase1Cnt(phase1Cnt);
			timeLeft = tdp1/1000; // in seconds
			mState.stopPhaseTime();
			if (phase1Cnt > countLimit1_i)
			{
				simulateEvent("Monitor.Phase2.Intro", "", pPanel);
				return true;
			}
		}
		mLog.logEvent(Log.PHASE, "1");
		mState.setPhase(State.PHASE_ONE);
		mLog.logEvent(Log.PHASE_CNT, "" + phase1Cnt);
		int course = mState.getCourse();
		String tdpr_s = mProperties.getProperty("Timer.Delay.Phase1.Rest").trim();
//				System.err.println("tdpr_s="+tdpr_s +", timeLeft=" + timeLeft);
		mView.setFullScreen(null, 
			"<body>The next screen will start a new round in the experiment." + // + mState.getPhase1Cnt() + "' in test '" + mState.getExamId()+ "'.<br/>" +
			"<input type='label' id='prefix' value='You can now rest for '/>" +
			"<input type='button' id='Monitor.Phase1.Rest.Button' timer='" + tdpr_s  + 
			"' value='          " + tdpr_s + "          ' /> " + 
			"<input type='label' id='units' value='seconds.'/><br/>"+
			" After that, you will start another round lasting: " + 
			(int)Math.floor(timeLeft/60)+":"+(timeLeft%60) + " minutes.<br/>" +
			"</body>",
			getDomain(), AbstractHtmlPanel.TRIGGER_BUTTONS);
//		mView.setHelp
//		(
//			mProperties.getProperty("Project.HelpDir") + "/" +
//			mProperties.getProperty("Help.Filename.Course." + mState.getCourse() + ".Phase.1.1")
//		);
		mView.setMode(View.FULL_SCREEN);
//		mView.setMode(View.INTRO_SCREEN);
		return true;
	}

	private boolean doPhaseTests() {
		// Set up the round's timer
		String phaseNum = Integer.toString(mState.getPhase());
		long skipTime = mState.getPhaseTime();
		String tdp_s = mProperties.getProperty("Timer.Delay.Phase" + phaseNum + ".Test").trim();
		int tdp_i = (int)(Integer.parseInt(tdp_s)*60000 - skipTime)/1000; // in seconds
		AbstractHtmlPanel.Timer tdp_t = timerPanel.new Timer(tdp_i, tdp_i, getMyProxy(), "Monitor.Phase" + phaseNum + ".Rest.Timer", timerPanel);

		// make a new problem to solve and set the screen layout appropriately
		int problemPhase = State.PHASE_ONE;
		if (phaseNum.equals("2")) problemPhase = State.PHASE_TWO;
		newProblem(State.COURSE_CURRENT, State.TASK_PSEUDO, problemPhase, true);
		mView.setMode(View.TEST_SCREEN);

		// Log and resume the timers.
		mLog.logEvent(Log.END_REST, "");
		mState.resumePhaseTime(); // resume if paused or stopped to count time.
		tdp_t.start();

		return true;
	}

	private boolean doLogin(String pName) {
		if (pName.equalsIgnoreCase(LOGIN_PHASE + ".one")) 
		{
			mLog.logEvent(Log.COURSE, Integer.toString(State.COURSE_ONE));
			mState.setCourse(State.COURSE_ONE);
		} 
		else if (pName.equalsIgnoreCase(LOGIN_PHASE + ".two")) 
		{
			mLog.logEvent(Log.COURSE, Integer.toString(State.COURSE_TWO));
			mState.setCourse(State.COURSE_TWO);
		}
		else if (pName.equalsIgnoreCase(LOGIN_PHASE + ".three"))  
		{
			mLog.logEvent(Log.COURSE, Integer.toString(State.COURSE_THREE));
			mState.setCourse(State.COURSE_THREE);
		}
		// show instructions screen (second screen)
		int course = mState.getCourse();
		mView.setFullScreen(null, // change this to a file name, and turn next parameter to null.
			"<body>This is the instructions screen for test " + mState.getExamId()+ "<br/>" +
			"<include name='data/IntroPages/Instructions.phase.1.course." + course + ".html'/><br/>" +
			"When you click on this button: "+
			"<input type='button' id='Monitor.Phase1.Rest.Timer' value='OK'/><br/>" + 
			" You will start the test</body>",
			getDomain(), AbstractHtmlPanel.TRIGGER_BUTTONS);
		mView.setHelp
		(
			mProperties.getProperty("Project.HelpDir") + "/" +
			mProperties.getProperty("Help.Filename.Course." + mState.getCourse() + ".Phase.1.1")
		);
		mView.setMode(View.INTRO_SCREEN);
		mState.setPhase(1);
		mState.stopPhaseTime(); // if you come through here, timer should be reset.
		return true;
	}

	private boolean doStopTest() {
		timerPanel.killTimers(true);
		EventProxy.getInstance(getDomain()).clearQueue(true); // clear all pending events.
		mState.save(mProperties.getProperty("State.Dir"));
		mState = null;
		mLog.close();
		mLog = null;
		mView.setFullScreen(null, 
			"<body>TEST STOPPED/FINISHED<br/>  <br/>" +
			"WAIT FOR FURTHER INSTRUCTIONS</body>",
			getDomain(), AbstractHtmlPanel.TRIGGER_BUTTONS);
		mView.setMode(View.FULL_SCREEN);
		return true;
	}
		/* (non-Javadoc)
		 * @see experiment.control.AbstractControl#getDomain()
		 */
	public String getDomain() 
	{
		return "Monitor";
	}	/**
	* @return
	*/
   public Properties getTestProperties() {
   	 if (mState != null)
   	 	return mState.getTestProperties();
   	 return new Properties();
   }
   
   protected String createCode(AbstractHtmlPanel pPanel) 
   {return null;}

/* (non-Javadoc)
 * @see experiment.control.AbstractWizardControl#verify(tools.HtmlPanel)
 */
protected boolean verify(AbstractHtmlPanel pPanel) {
	return true;
} // nothing to do here, just need to implement this abstract method.
/**
 * 
 */
public void stopTest() 
{
	EventProxy.getInstance(getDomain()).delegate("Monitor.StopTest", "", null);
}

/**
 * 
 */
public void nextTask() {
	EventProxy.getInstance(getDomain()).delegate("Monitor.ResumeTest", "", null);
}

/**
 * 
 */
public void setNextTask(int pTask) {
	if (mState == null) return;
	mState.setNextTask(pTask);
}

/**
 * 
 */
public void nextPhase() {
	if (mState == null) return;
	int p = mState.getPhase();
	p++;
	mState.setPhase(p);
	nextTask();
}

}


