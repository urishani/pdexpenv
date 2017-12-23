/*
 * Created on Aug 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.logs;

import tools.logs.Log.LogEvent;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LogTask extends LogTreeElement {
	/**
	 * @param events
	 * @param pos
	 * @param pos2
	 */
	public LogTask(LogEvent[] pEvents, int pPos, int pLast) {
		super(pEvents, pPos);
		//System.out.println("LogTask called " + pPos + " -- " + pLast);
		mLast = pLast;
		if (mEvents[mPos].mType == Log.TEST)
			mName = "Task: " + mEvents[mPos].mData;
		mPos++;
	}

	/* (non-Javadoc)
	 * @see tools.logs.LogTreeElement#scanEvents()
	 */
	LogTreeElement scanForChildren() {
		if (mPos < mLast)
		{
			mPos++;
			return new LogLeaf(mEvents[mPos-1], mEvents[mPos-2]);
		}
		return null;
	}

}
