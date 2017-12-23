/*
 * Created on Apr 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.http;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import javax.net.ssl.HandshakeCompletedListener;

import com.sun.jmx.remote.util.OrderClassLoaders;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class Worker extends Thread implements HttpConstants
{
	protected final File root;

	final static int BUF_SIZE = 2048;

	static final byte[] EOL = {(byte)'\r', (byte)'\n' };

	/* buffer to use for requests */
	byte[] buf;
	/* Socket to client we're handling */
	protected Socket s;
	private final boolean mNoCache;


	public Worker() { 
		mNoCache = false; 
		root = null; 
		mHandlers.add(new Handler_GET());
	}
	
	public Worker(Vector pPool, File pRoot, boolean pNoCache) {
		buf = new byte[BUF_SIZE];
		s = null;
		root = pRoot;
		mNoCache = pNoCache;
		mHandlers.add(new Handler_GET());
	}
	
	/**
	 * Add a handler to list of handlers.
	 * @param handler Handler to add
	 * @param first boolean true to prepend the handler to the list, or add it to the end if false.
	 */
	public synchronized void addHandler(Handler handler, boolean first) 
	{
		if (first)
			mHandlers.addFirst(handler);
		else 
			mHandlers.addLast(handler);
	}
	
	synchronized void setSocket(Socket s) {
		this.s = s;
		notify();
	}
	
	public synchronized void run() {
		while(true) {
			if (s == null) {
				/* nothing to do */
				try {
					wait();
				} catch (InterruptedException e) {
					/* should not happen */
					continue;
				}
			}
			try {
				handleClient();
			} catch (Exception e) {
				e.printStackTrace();
			}
			/* go back in wait queue if there's fewer
			 * than numHandler connections.
			 */
			s = null;
			Vector<Worker> pool = WebServer.mThreads;
			synchronized (pool) {
				if (pool.size() >= WebServer.workers) {
					/* too many threads, exit this one */
					return;
				} else {
					pool.addElement(this);
				}
			}
		}
	}
	
	private LinkedList<Handler> mHandlers = new LinkedList<Handler>();
	
	void handleClient() throws IOException {
		InputStream is = s.getInputStream();
		OutputStream os = s.getOutputStream();
		if (is == null || os == null) return;
		is = new BufferedInputStream(is);
		PrintStream ps = new PrintStream(os);
		/* we will only block in read for this many milliseconds
		 * before we fail with java.io.InterruptedIOException,
		 * at which point we will abandon the connection.
		 */
		s.setSoTimeout(WebServer.mTimeout);
		s.setTcpNoDelay(true);
		/* zero out the buffer from last time */
	
		System.out.println("Received: ");
	
		StringBuffer buf = new StringBuffer(BUF_SIZE);
//		for (int i = 0; i < BUF_SIZE; i++) {
//			buf[i] = (byte)' ';
//		}
		try {
			while (true) {
				int b = is.read();
				if (-1 == b || '\n' == b || '\r' == b)
					break;
				buf.append((char)b);
			}
			System.out.println(buf);
//			int nread = 0, r = 0;
//
//			outerloop:
//
//				while (nread < BUF_SIZE) {
//					try {
//						r = is.read(buf, nread, BUF_SIZE - nread);
//					} catch (IOException e) {
//						System.out.println("EOF time out");
//						break outerloop;
//					}
//					if (r == -1) {
//						/* EOF */
//						//return;
//						break outerloop;
//					}
//					int i = nread;
//					nread += r;
//					for (; i < nread; i++) {
//						System.out.print((char)buf[i]);
//						if (buf[i] == (byte)'\n' || buf[i] == (byte)'\r') 
//						{
//							/* read one line */
//							break outerloop;
//						}
//					}
//				}
//
//			int i = 0;
//			for (; i < nread; i++) {
//				if (buf[i] == (byte)'\n' || buf[i] == (byte)'\r') 
//				{
//					/* read one line */
//					break;
//				}
//			}
//			for (int j=i; j < buf.length ; j++) buf[j]=0;

			boolean handled = false;
			for (int h= 0; h < mHandlers.size(); h++)
				if (handled = mHandlers.get(h).handleRequest(buf.toString(), is, ps, s))
					break;

			if (false == handled) 
			{
				/* we don't support this method */
				ps.print("HTTP/1.0 " + HTTP_BAD_METHOD +
				" unsupported method type: ");
				for (int i= 0; i < 5; i++)
					ps.write(buf.charAt(i));
			}
		} finally {
			ps.write(EOL);
			ps.flush();
			s.close();
		}
	}
	/**
	 * @param fname
	 * @return
	 */
	protected String processRequest(String fname) {return null;}

	private class Handler_GET implements Handler {
		public boolean handleRequest(String buf, InputStream is, PrintStream ps, Socket s) throws IOException {
			/* are we doing a GET or just a HEAD */
			boolean doingGet = false;
			/* beginning of file name */
			int index;
			if (buf.startsWith("GET ")) {
				doingGet = true;
				index = 4;
			} else if (buf.startsWith("HEAD ")) {
				doingGet = false;
				index = 5;
			} else 
				return false;

			int i = 0;
			/* find the file name, from:
			 * GET /foo/bar.html HTTP/1.0
			 * extract "/foo/bar.html"
			 */
//			for (i = index; i < buf.length; i++) {
//				if (buf[i] == (byte)' ') {
//					break;
//				}
//			}
			String fname = buf.substring(index).split(" ")[0].replace('/', File.separatorChar);
			if (fname.startsWith(File.separator)) {
				fname = fname.substring(1);
			}
			String processName = fname;
			if (processName.length() == 0)
				processName = "frames.html";

			String val = processRequest(processName);
			File targ = new File(root, fname);
			if (val != null)
			{
				targ = new File(root, processName);	
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(targ));
				osw.write(val);
				osw.close();
			} 
			if (targ.isDirectory()) 
			{
				File ind = new File(targ, "index.html");
				if (ind.exists()) 
				{
					targ = ind;
				}
			}
			boolean OK = printHeaders(targ, ps);
			if (doingGet) {
				if (OK) {
					sendFile(targ, ps);
				} else {
					send404(targ, ps);
				}
			}
			return true;
		}
		boolean printHeaders(File targ, PrintStream ps) throws IOException {
			//ps = System.out;
			boolean ret = false;
			int rCode = 0;
			if (!targ.exists()) {
				rCode = HTTP_NOT_FOUND;
				ps.print("HTTP/1.0 " + HTTP_NOT_FOUND + " not found");
				ps.write(EOL);
				ret = false;
			}  else {
				rCode = HTTP_OK;
				ps.print("HTTP/1.0 " + HTTP_OK+" OK");
				ps.write(EOL);
				ret = true;
			}
			WebServer.log("From " +s.getInetAddress().getHostAddress()+": GET " +
					targ.getAbsolutePath()+"-->"+rCode);
			ps.print("Server: Simple java");
			ps.write(EOL);
			if (mNoCache)
			{
				ps.print("Cache-Control: no-store"); 
				ps.write(EOL);
			}
			ps.print("Date: " + (new Date()));
			ps.write(EOL);
			if (ret) {
				if (!targ.isDirectory()) {
					ps.print("Content-length: "+targ.length());
					ps.write(EOL);
					ps.print("Last Modified: " + (new
							Date(targ.lastModified())));
					ps.write(EOL);
					String name = targ.getName();
					int ind = name.lastIndexOf('.');
					String ct = null;
					if (ind > 0) {
						ct = (String) map.get(name.substring(ind));
					}
					if (ct == null) {
						ct = "unknown/unknown";
					}
					ps.print("Content-type: " + ct);
					ps.write(EOL);
				} else {
					ps.print("Content-type: text/html");
					ps.write(EOL);
				}
			}
			return ret;
		}

		void send404(File targ, PrintStream ps) throws IOException {
			ps.write(EOL);
			ps.write(EOL);
			ps.println("Not Found\n\n"+
			"The requested resource was not found.\n");
		}

		void sendFile(File targ, PrintStream ps) throws IOException {
			InputStream is = null;
			System.out.println("Sending file '" + targ + "'");
			ps.write(EOL);
			if (targ.isDirectory()) {
				listDirectory(targ, ps);
				return;
			} else {
				is = new FileInputStream(targ.getAbsolutePath());
			}

			try {
				int n;
				while ((n = is.read(buf)) > 0) {
					ps.write(buf, 0, n);
				}
			} finally {
				is.close();
			}
		}
	}

	/* mapping of file extensions to content-types */
	static java.util.Hashtable map = new java.util.Hashtable();

	static {
		fillMap();
	}
	static void setSuffix(String k, String v) {
		map.put(k, v);
	}

	static void fillMap() {
		setSuffix("", "content/unknown");
		setSuffix(".uu", "application/octet-stream");
		setSuffix(".exe", "application/octet-stream");
		setSuffix(".ps", "application/postscript");
		setSuffix(".zip", "application/zip");
		setSuffix(".sh", "application/x-shar");
		setSuffix(".tar", "application/x-tar");
		setSuffix(".snd", "audio/basic");
		setSuffix(".au", "audio/basic");
		setSuffix(".wav", "audio/x-wav");
		setSuffix(".gif", "image/gif");
		setSuffix(".jpg", "image/jpeg");
		setSuffix(".jpeg", "image/jpeg");
		setSuffix(".htm", "text/html");
		setSuffix(".html", "text/html");
		setSuffix(".text", "text/plain");
		setSuffix(".c", "text/plain");
		setSuffix(".cc", "text/plain");
		setSuffix(".c++", "text/plain");
		setSuffix(".h", "text/plain");
		setSuffix(".pl", "text/plain");
		setSuffix(".txt", "text/plain");
		setSuffix(".java", "text/plain");
		setSuffix(".Monitor", "text/html");
	}

	void listDirectory(File dir, PrintStream ps) throws IOException {
		ps.println("<TITLE>Directory listing</TITLE><P>\n");
		ps.println("<A HREF=\"..\">Parent Directory</A><BR>\n");
		String[] list = dir.list();
		for (int i = 0; list != null && i < list.length; i++) {
			File f = new File(dir, list[i]);
			if (f.isDirectory()) {
				ps.println("<A HREF=\""+list[i]+"/\">"+list[i]+"/</A><BR>");
			} else {
				ps.println("<A HREF=\""+list[i]+"\">"+list[i]+"</A><BR");
			}
		}
		ps.println("<P><HR><BR><I>" + (new Date()) + "</I>");
	}
	
	/**
	 * Class to handle requests
	 * @author shani
	 *
	 */
	public interface Handler {
		/**
		 * Handle a request. If cannot handle, than return false. Otherwise - true.
		 * @param firstLine is a byte[] for the first input line.
		 * @param is InputStream of incoming bytes.
		 * @param ps OutputStream of outgoing reply
		 * @param s is the input socket
		 * @return boolean true if request handled.
		 */
		public boolean handleRequest(String firstLine, InputStream is, PrintStream ps, Socket s) throws IOException ;
		
	}
}








