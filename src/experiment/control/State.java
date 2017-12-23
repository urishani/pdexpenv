/*
 * Created on 18/03/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import experiment.Main;

import tools.statistics.GroupsModel;
import tools.statistics.Statistics;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class State implements Serializable {

	public static final int COURSE_NONE=0,
		COURSE_CURRENT = 0, COURSE_ANY = -1, COURSE_ONE = 1, COURSE_TWO = 2,COURSE_THREE = 3;
	protected static final int COURSE_NUMBER = 3;
	public static final int PHASE_NONE = 0;
	public static final int PHASE_ONE = 1;
	public static final int PHASE_TWO = 2;
	public static final int PHASE_THREE = 3;
	public static final int PHASE_CURRENT = 0;

	public final static int TASK_PSEUDO = -3;
	public final static int TASK_NEXT = -2;
	public final static int TASK_ANY = -1;
	public final static int TASK_CURRENT = 0;
	public final static int TASK_AREA_ADD = 1;
	public final static int TASK_ROW_ADD = 2;
	public final static int TASK_CELL_ADD = 3;
	public final static int TASK_AREA_REM = 4;
	public final static int TASK_ROW_REM = 5;
	public final static int TASK_CELL_REM = 6;
	public final static int TASK_AREA_MULT = 7;
	public final static int TASK_ROW_MULT = 8;
	public final static int TASK_CELL_MULT = 9;
	public final static int TASK_NUMBER = 9;


	protected final int RANDOMIZER = 1;
	private int[] mTaskPlan;// = {2,3,9,3,9,3,9};
	private int[] mTaskPlanPhase1 = Main.getProperties().getIntsProperty("TaskPlan.Phase1");
	private int[] mTaskPlanPhase2 = Main.getProperties().getIntsProperty("TaskPlan.Phase2");
	private int[] mTaskCountGood = {0,0,0,0,0,0,0,0,0,0};
	private int[] mTaskCountBad = {0,0,0,0,0,0,0,0,0,0};
	private int[] mShowCode = {0,0,0,0,0,0,0,0,0,0};
	private int mIndex = 0;
	private int mScore = 0;
	private int mTaskScore = 0;
	private int mCourse = COURSE_ONE;
	private int mTask = 0; //TASK_AREA_MULT; // stam
	private int mWeight = 0;	
	private int mPhase = PHASE_ONE; // stam
	private int mPhase2Cnt = 0;
	private int mPhase1Cnt = 0;
	private String mUserId, mExamId;
	private String mFileName;
	private Statistics mStatistics;
	private long mPhaseStartTime;
	private long mInitTime;
	private long mPhaseElapsedTime;
	private int mNextTask = -1;

	public State(String pUserId, String pExamId)
	{
		mUserId = pUserId;
		mExamId = pExamId;
		mFileName = mUserId + "_" + mExamId;
		startTotalTime();
	}

	private void flow()
	{
		/*openingFinishingScreen();
		  selectCourseScreen();
		  startPhase1Screen();// also compute trialTime
		  phase1HelpScreen();
		  while (phase1Cnt < phase1Trials)
		  {
   		     phase1RestScreen();
		     startPhase1Trial( trialTime);
		     phase1Cnt++;
		     resetPhase2TrialTime(); // computes trialTime
		  }
		  startPhase2Screen(); // also compute trialTime;
		  while (phase2Cnt < phase2Trials)
		  {
		     phase2RestScreen();
		     startPhase2Trial( trialTime);
		     phase2Cnt ++;
		     resetPhase2TrialTime(); // computes trialTime
		  }
		  thanksScreen();
		*/
	}
	
	public boolean save(String pDir)
	{
		int fileNum=0;
		File file;
		if (pDir == null || pDir.length() == 0)
			pDir = ".";
		pDir += File.separator;
		while (true)
		{
			file = new File(pDir + mFileName+"."+fileNum);
			if (!file.exists()) break;
			fileNum++;
		}
		File f = new File(pDir + mFileName); 
		if (f.exists())
			f.renameTo(new File(pDir + mFileName+"."+fileNum));
		file = new File(pDir + mFileName);

		OutputStream os;
		pausePhaseTime();
		try {
			os = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(this);
		} catch (FileNotFoundException e) {
			System.out.println("Cannot write status file '" + pDir + mFileName + "'");
			return false;
		} catch (IOException e) {
			System.err.println("Error serializing status file '" + pDir + mFileName + "': " + e.getMessage());
			return false;
		}
		return true;	
	}
	protected String getFileName() { return mFileName; }

	public void reset()
	{
		mCourse = COURSE_NONE;
		mTask = 0; //TASK_AREA_MULT; // stam
		mPhase = PHASE_NONE; // need to select one
		mPhase2Cnt = 0;
		mPhase1Cnt = 0;
		mPhaseStartTime = 0;
		for (int i=0; i<10; i++){
			mShowCode[i] = 0;
			mTaskCountGood[i] = 0;
			mTaskCountBad[i] = 0;
		}
		 
		mStatistics = new Statistics(GroupsModel.createGroupsModel(
				new int[][]{{1,4},{4,7},{7,10}}));
		stopPhaseTime();
	}
	public static State restore(State pState, String pDir)
	{
		if (pDir == null || pDir.length() == 0)
			pDir = ".";
		pDir += File.separator;
		String fn = pDir + pState.getFileName();
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new FileInputStream(new File(fn)));
			State s = (State)ois.readObject();
			s.pausePhaseTime();
			s.startTotalTime();
			return s;
		} catch (FileNotFoundException e) {
			System.err.println("Error finding input status file '" + fn + "': "+ e.getMessage());
			return null;
		} catch (IOException e) {
			System.err.println("Error opening input status file '" + fn + "': "+ e.getMessage());
			return null;
		} catch (ClassNotFoundException e) {
			System.err.println("Error deserializing input status file '" + fn + "': "+ e.getMessage());
			return null;
		}
	}

	public int getCourse() {
		return mCourse;
	}
	public int getPhase() {
		return mPhase;
	}
	public int getPhase1Cnt() {
		return mPhase1Cnt;
	}
	public int getPhase2Cnt() {
		return mPhase2Cnt;
	}
	public int getTask() {
		return mTask;
	}
	public int getScore() {
//		mScore = mScore + 1;
		return mScore;
	}

	public void setCourse(int i) {
		mCourse = i;
		mPhaseStartTime= 0;
	}

	public void setPhase(int i) {
		mPhase = i;
		if (mPhase == 1) 
		{
			mTaskPlan = mTaskPlanPhase1;
//			mIndex = 0;
		}
		else 
		{
			mTaskPlan = mTaskPlanPhase2;
//			mIndex = 0;
//			for (int j=0; j<10; j++){
//				mShowCode[j] = 0;
//				mTaskCountGood[j] = 0;
//				mTaskCountBad[j] = 0;
//			}
		}
	}

	public void setPhase1Cnt(int i) {
		if (mPhase1Cnt != i)
		{
			mStatistics.reset();
			stopPhaseTime();
		}
		mPhase1Cnt = i;
	}

	public void setPhase2Cnt(int i) {
		if (mPhase2Cnt != i)
		{
			mStatistics.reset();
			stopPhaseTime();
		}
		mPhase2Cnt = i;
	}

	public void registerSuccess(int pSuccess){
		int success = pSuccess;
		int task = mTask; 
		if (success != 0){ 
			mTaskCountBad[task]++;
			}
		else
			mTaskCountGood[task]++;
	}
	
	public void registerShowCode(boolean mCodeShown){
		boolean show = mCodeShown;
		if (show){
			int task = mTask;
			mShowCode[task]++;

		}
	}
	
	public void registerReward(int mReward){
		mScore = mScore + mReward;
		mTaskScore = mReward;
	
	}

	public void setTask(int i ) {
		mTask = i;
		mStatistics.registerSample(new Integer(i));
	}

	public void setNextTask(int i) {
		mNextTask = i;
	//	mStatistics.registerSample(new Integer(i));
	}

	/**
	 * @param pCourse
	 */
	public void nextCourse(int pCourse) {
		if (pCourse == COURSE_ANY)
			mCourse = 1 + (int)Math.random()*(COURSE_NUMBER-1);
		else if (pCourse != COURSE_CURRENT)
			mCourse = pCourse;
	}

	/**
	 * @param pTask
	 */
	public void nextTask(int pTask) {
		if (mNextTask > 0) // force a specific task.
		{
			setTask( mNextTask);
			mNextTask = -1;
			return;
		}
		if (pTask == TASK_ANY)
		{
			Random r = new Random(new Date().getTime());
			setTask( r.nextInt((TASK_NUMBER-1))+1);
			return;	
		} else if (pTask == TASK_NEXT)
		{
			int task = getTask();
			task++;
			if (task > TASK_NUMBER || task < 1) task = 1;
			setTask(task);
			return;
		}else if (pTask == TASK_PSEUDO) // define this one too 
		{ 
			int phase = getPhase();
			int task = getTask(); 
			if (mIndex >= mTaskPlan.length) mIndex = 0;
			setTask(mTaskPlan[mIndex]);
			mIndex++;
			return; 
		}
			
		else if (pTask != TASK_CURRENT)
			setTask(pTask);
	}

	/**
	 * @return
	 */
	
	public Properties getTestProperties() {
		Properties p = new Properties();
		p.setProperty("user", mUserId);
		p.setProperty("testId", mExamId);
		p.setProperty("testing", "Test-value");
		p.setProperty("Course", Integer.toString(mCourse));
		p.setProperty("Task", Integer.toString(mTask));
		p.setProperty("Tasks", Integer.toString(mStatistics.size()));
		p.setProperty("Phase", Integer.toString(mPhase));
		p.setProperty("Phase1Cnt", Integer.toString(mPhase1Cnt));
		p.setProperty("Phase2Cnt", Integer.toString(mPhase2Cnt));
		p.setProperty("Reward", Integer.toString(mTaskScore));
		p.setProperty("Score", Integer.toString(mScore));

		p.setProperty("TaskGood1", Integer.toString(mTaskCountGood[1]));
		p.setProperty("TaskGood2", Integer.toString(mTaskCountGood[2]));
		p.setProperty("TaskGood3", Integer.toString(mTaskCountGood[3]));
		p.setProperty("TaskGood4", Integer.toString(mTaskCountGood[4]));
		p.setProperty("TaskGood5", Integer.toString(mTaskCountGood[5]));
		p.setProperty("TaskGood6", Integer.toString(mTaskCountGood[6]));
		p.setProperty("TaskGood7", Integer.toString(mTaskCountGood[7]));
		p.setProperty("TaskGood8", Integer.toString(mTaskCountGood[8]));
		p.setProperty("TaskGood9", Integer.toString(mTaskCountGood[9]));
		p.setProperty("TaskBad1", Integer.toString(mTaskCountBad[1]));
		p.setProperty("TaskBad2", Integer.toString(mTaskCountBad[2]));
		p.setProperty("TaskBad3", Integer.toString(mTaskCountBad[3]));
		p.setProperty("TaskBad4", Integer.toString(mTaskCountBad[4]));
		p.setProperty("TaskBad5", Integer.toString(mTaskCountBad[5]));
		p.setProperty("TaskBad6", Integer.toString(mTaskCountBad[6]));
		p.setProperty("TaskBad7", Integer.toString(mTaskCountBad[7]));
		p.setProperty("TaskBad8", Integer.toString(mTaskCountBad[8]));
		p.setProperty("TaskBad9", Integer.toString(mTaskCountBad[9]));

		p.setProperty("CodeShown1", Integer.toString(mShowCode[1]));
		p.setProperty("CodeShown2", Integer.toString(mShowCode[2]));
		p.setProperty("CodeShown3", Integer.toString(mShowCode[3]));
		p.setProperty("CodeShown4", Integer.toString(mShowCode[4]));
		p.setProperty("CodeShown5", Integer.toString(mShowCode[5]));
		p.setProperty("CodeShown6", Integer.toString(mShowCode[6]));
		p.setProperty("CodeShown7", Integer.toString(mShowCode[7]));
		p.setProperty("CodeShown8", Integer.toString(mShowCode[8]));
		p.setProperty("CodeShown9", Integer.toString(mShowCode[9]));
		
		int phet = (int)(getPhaseTime()/1000);
		p.setProperty("PhaseElapsedTime", Integer.toString(phet));
		p.setProperty("PhaseElapsedTime.m", Integer.toString((int)(phet/60)));
		p.setProperty("PhaseElapsedTime.s", Integer.toString(phet%60));
		int tet = (int)(getTotalTime()/1000);
		p.setProperty("TotalElapsedTime", Integer.toString(tet));
		p.setProperty("TotalElapsedTime.m", Integer.toString((int)(tet/60)));
		p.setProperty("TotalElapsedTime.s", Integer.toString(tet%60));
		p.setProperty("NextTask", Integer.toString(mNextTask));
		return p;
	}

	private void startTotalTime() { mInitTime = new Date().getTime(); }
	private long getTotalTime() {return new Date().getTime() - mInitTime;	}

	/**
	 * @return
	 */
	public long getPhaseTime() {
		if (mPhaseStartTime == 0) return mPhaseElapsedTime;
		return (new Date().getTime()) - mPhaseStartTime + mPhaseElapsedTime;
	}

	/**
	 * @param l
	 */
	public void resumePhaseTime() {	if (mPhaseStartTime == 0) startPhaseTime();	}
	public void restartPhaseTime() { stopPhaseTime(); startPhaseTime();	}
	public void startPhaseTime() { mPhaseStartTime = new Date().getTime(); }
	public void stopPhaseTime() { mPhaseStartTime = 0; mPhaseElapsedTime= 0; }
	public void pausePhaseTime() {
		mPhaseElapsedTime = getPhaseTime();
		mPhaseStartTime = 0;
	}

	/**
	 * @return
	 */
	public Statistics getStatistics() {
		return mStatistics;
	}

	/**
	 * @return
	 */
	public String getExamId() {
		return mExamId;
	}

	/**
	 * @return
	 */
	public long getMPhaseElapsedTime() {
		return mPhaseElapsedTime;
	}

	/**
	 * @return
	 */
	public String getUserId() {
		return mUserId;
	}

	/**
	 * @param l
	 */
	public void setMPhaseElapsedTime(long l) {
		mPhaseElapsedTime = l;
	}

}
