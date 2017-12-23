/*
 * Created on Apr 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.http;

import java.io.File;
import java.util.Vector;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestWebServer extends WebServer {
	/* (non-Javadoc)
	 * @see tools.http.WebServer#makeWorker(java.util.Vector, java.io.File, boolean)
	 */
	protected Worker makeWorker(Vector pThreads, File pRoot, boolean pNoCache) {
		return new TestWorker(pThreads, pRoot, pNoCache);
	}
	
	public static void main(String[] a) throws Exception
	{
		TestWebServer tws = new TestWebServer();
		tws.doMain(a);
		tws.start();
	}
}

class TestWorker extends Worker
{
	/* (non-Javadoc)
	 * @see tools.http.Worker#processRequest(java.lang.String)
	 */
	public TestWorker(Vector pThreads, File pRoot, boolean pNoCache)
	{
		super(pThreads, pRoot, pNoCache);
	}
	protected String processRequest(String fname) 
	{
		return null;
	}

}
