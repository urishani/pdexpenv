/*
 * Created on Aug 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.logs;

import java.rmi.dgc.Lease;

import tools.logs.Log.LogEvent;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LogTest extends LogTreeElement {
	/**
	 * @param events
	 * @param pos
	 */
	public LogTest(LogEvent[] pEvents, int pPos) {
		super(pEvents, pPos);
		if (mEvents[mPos].mTime == 0 &&
				mEvents[mPos].mType == Log.START)
			mName = mEvents[mPos].mData;
		mPos++;
		mLast = mEvents.length;
		//System.out.println("Test " + pPos + " -- " + mLast);
	}

	/* (non-Javadoc)
	 * @see tools.logs.LogTreeElement#scanEvent()
	 */
	LogTreeElement scanForChildren() {
		Log.LogEvent e = null;
		if (mPos >= mLast) return null;
		while (mPos < mLast && (e = mEvents[mPos]).mType != Log.COURSE && e.mType != Log.START) 
			return new LogLeaf(e, mEvents[mPos -1]);
		if (e.mType == Log.START || mPos >= mLast)
			return null;
		String course = e.mData;
		int pos = mPos;
		mPos++;
		while (mPos < mLast && (e = mEvents[mPos]).mType != Log.START)
		{
			mPos++;
			if (e.mType == Log.COURSE)
				if (!e.mData.equals(course))
					return new LogCourse(mEvents, pos, mPos-1);
		}
		if (mPos < mLast && e.mType == Log.START)
			mLast = mPos;
		return new LogCourse(mEvents, pos, mPos-1);
	}

}
