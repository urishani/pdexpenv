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
public class LogPhase extends LogTreeElement {
	/**
	 * @param events
	 * @param pos
	 * @param pos2
	 */
	public LogPhase(LogEvent[] pEvents, int pPos, int pLast) {
		super(pEvents, pPos);
		//System.out.println("Phase " + pPos + " -- " + pLast);
		mLast = pLast;
		if (mEvents[mPos].mType == Log.PHASE)
			mName = "Phase: " + mEvents[mPos].mData;
		mPos++;
	}

	/* (non-Javadoc)
	 * @see tools.logs.LogTreeElement#scanEvents()
	 */
	LogTreeElement scanForChildren() {
		if (mPos >= mLast)
			return null;
		Log.LogEvent e = null;
		if ((e = mEvents[mPos]).mType != Log.PHASE_CNT)
		{
			mPos++;
			return new LogLeaf(e, mEvents[mPos-2]);
		}
		String phase_cnt = e.mData;
		int pos = mPos;
		while (++mPos <= mLast)
		{
			if (mPos == mLast || ((e = mEvents[mPos]).mType == Log.PHASE_CNT && !e.mData.equals(phase_cnt)))
					return new LogPhaseCnt(mEvents, pos, mPos);
		}
		return null;
	}

}
