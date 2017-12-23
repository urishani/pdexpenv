/*
 * Created on 26/03/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.html;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Stack;
import java.util.Vector;

import tools.http.*;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Sniffer {
	final int mPort;
	
	public Sniffer(int pPort)
	{ 
		mPort = pPort;
	}
	public static void main(String[] a) throws Exception 
	{
		SnifferWebServer sws = new SnifferWebServer();
		sws.doMain(a);
		sws.run();
	}

	/* print to stdout */
	protected static void p(String s) {
		System.out.println(s);
	}
	/* print to stderr */
	protected static void pe(String s) {
		System.err.println(s);
	}


	public static Stack sniffForAllIps(int pPort)
	{
		Stack ips = new Stack();
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			byte[] ip = localHost.getAddress();
			InetAddress newIp= null;
			Sleeper s = new Sleeper(2000);	s.start();

 			PingThread[] pt = new PingThread[254];
			for (int i=0; i< 254; i++)
			{
				ip[3]= (byte)(i+1); // range from 1 to 254.
				String hostName = 	ubyte(ip[0])+"." + ubyte(ip[1])+"."+ ubyte(ip[2])+"."+ ubyte(ip[3]);
				pt[i] = new PingThread(hostName, pPort);
				pt[i].start();
			}

			s.myWait();

			for (int i=0; i< 254; i++)
			{
				if (pt[i].isOk()) 
					ips.add(pt[i].getHost() );
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ips;
	}

	public String createFrames()
	{
		//create frames
		Stack ips = sniffForAllIps(mPort);
		if (ips == null) ips = new Stack();
		int cnt = ips.size();
		int dimx = 1;//(int)Math.floor(Math.sqrt((double)cnt));
		int dimy = 1;//(int)Math.ceil(cnt/dimx);
		if (cnt <= 3)
			dimx = cnt;
		else
		{
			dimx = (int)Math.floor(Math.sqrt((double)cnt/3)*3);
			dimy = (int)Math.ceil(cnt/dimx);
		}
		String result = "<html>";
		if (cnt == 0) result += "<body>No working machine found</body></html>";
		//else if (cnt == 1) result += "<body><script>document.location='index.html';</script></body></html>";
		else
		{
			int j=0;
			result+= "<frameset rows='";
			for (int y=0; y<dimy; y++) 
			{
				result += "1*";
				if (y==dimy-1) result += "'>";
				else result += ",";
			}
			result += "\n";
			for (int y=0; y<dimy; y++)
			{
				if (dimx == 1)
				{
					if (j >=cnt) { result+="<frame>"; continue; }
					result +="<frame src='http://" +ips.get(j)+":8081'>\n";
					j++;
				}
				else
				{
					result += "<frameset cols='"; 
					for (int x=0; x <dimx; x++)
					{
						result += "1*";
						if (x==dimx-1) result += "'>";
						else result += ",";
					}
					result += "\n";
					for (int x=0; x <dimx; x++)
					{
						if (j >=cnt) { result+="<frame>"; continue; }
						result += "<frame src='http://" + ips.get(j) + ":8081'>\n";
						j++; 
					}					
					result += "</frameset>\n";
				} 
			}
			result += "</frameset>\n";
			result += "</body></html>";
		}
		return result;
	}
	 static int ubyte(byte b) { int i = (int)b; return (i >= 0)?i:256+i; }
}

class Sleeper extends Thread
{
	long t;
	public Sleeper(long t) {this.t = t;}
	public void run() {
		p("Start sleeper"); 
		while(true){try {
		p("Sleeping " + t + " ms");
		for (int i=0; i< 10; i++) {sleep(t/10);System.out.print("+"); System.out.flush();} 
		p("... done");
		} catch (InterruptedException e) {
		} 
		myNotify();
		myWait();
	}}
	public synchronized void myNotify() { this.notify();}
	public synchronized void myWait() { try {
		this.wait();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
	} }
	/* print to stdout */
	protected static void p(String s) {
		System.out.println(s);
	}
	/* print to stderr */
	protected static void pe(String s) {
		System.err.println(s);
	}
}

class PingThread extends Thread
{
	final int mPort;
	final String mHost;
	boolean ok = false;
	public PingThread(String host, int pPort) 
	{
		mPort = pPort;
		mHost = host; 
	}
	public void run()
	{
			ok = ping(mHost);
	}
	public boolean isOk() { return ok; }
	public String getHost() { return mHost;}
	public boolean ping(String host) 
	{
		try {
//		  p("Pinging " + mHost);
		  Socket t = new Socket(host, mPort);
		  t.close();
		} catch (ConnectException e)
		{
			pe(mHost + ": " + e.getMessage());
			return false;
		} 
		catch (IOException e) 
		{
		  //e.printStackTrace();
		  pe(mHost + ": " + e.getMessage());
		  return false;
		}
		return true;
	}

	/* print to stdout */
	protected static void p(String s) {
		System.out.println(s);
	}
	/* print to stderr */
	protected static void pe(String s) {
		System.err.println(s);
	}
}

class SnifferWebServer extends WebServer
{

	/* (non-Javadoc)
	 * @see tools.http.WebServer#makeWorker(java.util.Vector, java.io.File, boolean)
	 */
	protected Worker makeWorker(Vector pThreads, File pRoot, boolean pNoCache) {
		return new SnifferWorker(pThreads, pRoot, pNoCache);
	}
}

class SnifferWorker extends Worker
{
	/* (non-Javadoc)
	 * @see tools.http.Worker#processRequest(java.lang.String)
	 */
	public SnifferWorker(Vector pThreads, File pRoot, boolean pNoCache)
	{
		super(pThreads, pRoot, pNoCache);
	}
	protected String processRequest(String fname) 
	{
		String val = null;
		File targ = new File(root, fname);
		targ.delete();
	    val = new Sniffer(8081).createFrames();
	    return val;
	}

}

