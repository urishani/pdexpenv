/*
 * Created on Sep 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.logs;

import java.io.File;
import java.net.URL;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LogDir extends LogTreeElement {

	final String[] mList;
	URL mURL;
	String mPath = ".";
	public LogDir(URL pURL, String pDir)
	{
		mURL = pURL;
		File f = new RemoteFile(mURL, pDir);
		mName = pDir;
		mPath = pDir;
		if (mURL != null)
			mName = pURL.getHost()+ "/"+ mName;
		if (!f.exists()) mIgnore = true;
		if (!f.isDirectory())
		{
			mList = new String[0];
			mPos = 0;
			mLast = 0;
			return;
		}			
		mList = f.list();
		mPos = 0;
		mLast = mList.length;
	}

	/* (non-Javadoc)
	 * @see tools.logs.LogTreeElement#scanForChildren()
	 */
	LogTreeElement scanForChildren() {
		while (mPos < mLast)
		{
			String name = mList[mPos];
			boolean isDir = name.startsWith(RemoteFile.DIR);
			name = name.substring((isDir?RemoteFile.DIR.length():RemoteFile.FILE.length()));
			mPos++;
			String path = mPath + File.separator + name;
			if (isDir)
			{
				LogDir d = new LogDir(mURL, path);
				if (d.countChildren() > 0)
					return d;
			} else
			{
				if (path.endsWith(Log.EXTENSION))
					return new LogUser(mURL, path);
			}
		}
		return null;
	}

}
