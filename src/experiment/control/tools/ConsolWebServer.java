/*
 * Created on Apr 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment.control.tools;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;

import tools.http.WebServer;
import tools.http.Worker;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ConsolWebServer extends WebServer {

	private Consol mConsol;
	
	public ConsolWebServer(int pPort, String pProps, boolean pNoCache, Consol pConsol)
	{
		mConsol = pConsol;
		init(pPort, pProps, pNoCache);
	}

	private int ubyte(byte b) { int i = (int)b; return (i >= 0)?i:256+i; }

	/* (non-Javadoc)
	 * @see tools.http.WebServer#makeWorker(java.util.Vector, java.io.File, boolean)
	 */
	protected Worker makeWorker(
		Vector pThreads,
		File pRoot,
		boolean pNoCache) 
		{
			String IP = "";
			try {
				InetAddress localHost = InetAddress.getLocalHost();
				byte[] ip = localHost.getAddress();
				IP = ubyte(ip[0])+"." + ubyte(ip[1])+"."+ ubyte(ip[2])+"."+ ubyte(ip[3]);
			} catch (UnknownHostException e) { }
			return new ConsolWorker(pThreads, pRoot, pNoCache, mConsol, IP);
		}

//	public static void main(String[] args) {	}
}
