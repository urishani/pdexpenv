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
public class LogCourse extends LogTreeElement {
	/**
	 * @param events
	 * @param pos
	 */
	public LogCourse(LogEvent[] pEvents, int pPos, int pLast) {
		super(pEvents, pPos);
		mLast = pLast;
		if (mEvents[mPos].mType == Log.COURSE)
			mName = "Course: " + mEvents[mPos].mData;
		mPos++;
	}

	/* (non-Javadoc)
	 * @see tools.logs.LogTreeElement#scanEvents()
	 */
	LogTreeElement scanForChildren() {
		if (mPos >= mLast) 
			return null;
		Log.LogEvent e = null;
		if ((e = mEvents[mPos]).mType != Log.PHASE)
		{
			mPos++;
			return new LogLeaf(e, mEvents[mPos-2]);
		}

		String phase = e.mData;
		int pos = mPos;
		while (++mPos <= mLast)
		{
			if (mPos == mLast || ((e = mEvents[mPos]).mType == Log.PHASE && !e.mData.equals(phase)))
					return new LogPhase(mEvents, pos, mPos);
		}
		return null;
	}

}
