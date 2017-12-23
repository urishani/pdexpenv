/*
 * Created on Aug 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Vector;

import tools.logs.Log.LogEvent;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LogUser extends LogTreeElement {
	final URL mURL;
	public LogUser(URL pURL, String pFile)
	{
		mPos = 0;
		mLast = -1;
		mName = pFile;
		mURL = pURL;
	}
	/* (non-Javadoc)
	 * @see tools.logs.LogTreeElement#scanEvents()
	 */
	LogTreeElement scanForChildren() {
		if (mLast == -1)
		{
			System.out.println("Reading " + mName);
			BufferedReader bIn = null;
			
			RemoteFile f = new RemoteFile(mURL, mName);
			if (!f.exists()) 
				return null;
			if (f.isDirectory())
				return null;
			bIn = f.getBufferedReader();

			Log l = new Log(bIn);
			Vector events = new Vector(100);
			Object event = null;
			while ((event = l.readLogEvent()) != null)
				events.add(event);
			mEvents = (LogEvent[]) events.toArray(new Log.LogEvent[0]);
			mLast = mEvents.length;			
		}

		while (mPos < mLast && mEvents[mPos].mTime != 0)
			mPos++;
		if (mPos >= mLast)
			return null;
		mPos++;
		return new LogTest(mEvents, mPos-1);
	}
	/**
	 * @param murl2
	 * @param name
	 * @return
	 */
	private String readRemoteFile(URL murl2, String name) {
		// TODO Auto-generated method stub
		return "Remote File";
	}
}
