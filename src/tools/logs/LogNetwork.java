/*
 * Created on Sep 6, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.logs;

import java.net.URL;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LogNetwork extends LogTreeElement {

	URL[] mURLs = new URL[0];
	String mPath = ".";
	
	public LogNetwork(URL[] pURLs, String pPath)
	{
		mURLs = pURLs;
		mPath = pPath;
		mPos = 0;
		mLast = mURLs.length;
		mName = "network";
	}
	/* (non-Javadoc)
	 * @see tools.logs.LogTreeElement#scanForChildren()
	 */
	LogTreeElement scanForChildren() {
		if (mPos >= mLast)
			return null;
		mPos++;
		return new LogDir(mURLs[mPos-1], mPath);
	}

}
