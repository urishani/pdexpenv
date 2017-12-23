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
public class LogLeaf extends LogTreeElement {
	/**
	 * @param e
	 */
	public LogLeaf(LogEvent e, LogEvent e1) {
		//System.out.println("LogLead " + e.mTime + "/" + e.mData + " -- " + e1.mTime + "/" + e1.mData);
		if (e.mType == Log.END_REST)
			mName = "Rest for: " + (new Float((float)(e.mTime - e1.mTime)/1000)).toString() + "sec.";
		else
			mName = e.getTypeName()+": " + e.mData;
		if (e.mType == Log.HELP && !e.mData.startsWith("file"))
			mIgnore = true;
		if (e.mType == Log.PHASE || e.mType == Log.PHASE_CNT ||
				e.mType == Log.COURSE)
			mIgnore = true;

	}

	/* (non-Javadoc)
	 * @see tools.logs.LogTreeElement#scanEvents()
	 */
	LogTreeElement scanForChildren() {
		return null;
	}

}
