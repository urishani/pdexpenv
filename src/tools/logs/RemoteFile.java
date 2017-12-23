/*
 * Created on Sep 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.logs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import experiment.Main;
import experiment.control.tools.Consol;
/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RemoteFile extends File
{
	public static final String NULL = "null:";
	public static final String FILE = "file:";
	public static final String DIR = "dir:";
	String mContent = null;
	String mType = NULL;
	String mList[] = null;
	final boolean mRemote;
	
	public RemoteFile(URL pURL, String pFile)
	{
		super (pFile);
		mRemote = pURL != null;
		if (!mRemote) return;
		mContent = readRemoteFile(pURL, pFile);
		if (mContent.startsWith(NULL))
		{
			mType = NULL;
			mContent = "";
			return;
		}
		
		if (mContent.startsWith(FILE))
			mType = FILE;
		else if (mContent.startsWith(DIR))
			mType = DIR;
		else return;
		mContent = mContent.substring(mType.length()+1);
		if (isDirectory())
		{
			StringTokenizer st = new StringTokenizer(mContent, "\n");
			Vector lines = new Vector(100);
			while (st.hasMoreTokens())
				lines.add(st.nextToken());
			mList = (String[]) lines.toArray( new String[0]);
		}
	}

	private String readRemoteFile(URL pURL, String pFileName)
	{
		String content = NULL + "\n0\n";

		try {
			System.out.println("Socket(" + pURL.getHost() + ":" + pURL.getPort());
			Socket client = new Socket(pURL.getHost(), pURL.getPort()); // url = new URL("http://localhost:8888");
			OutputStream outs = client.getOutputStream();
			InputStream ins = client.getInputStream();
			//Object o = url.getContent();
			outs.write(pFileName.getBytes());
			outs.flush();
			// read 2 lines first:
			String lines[] = {"","0"};
			for (int i=0; i<2; i++)
			{
				char x = (char)0;
				while ((x = (char)ins.read()) != '\n')
					lines[i] += String.valueOf(x);
			}
			if (lines[0].equalsIgnoreCase(NULL))
				return null;
			
			int length = Integer.parseInt(lines[1]);
			//System.out.println("Reading '" + lines[0] + "', length= "+ length);
			// read length of bytes
			byte buff[] = new byte[length];
			int p = 0;
			while (p < length)
			{
				p += ins.read(buff,p, length-p);
				//System.out.print(p+", "); System.out.flush();
			}
			client.close();
			content = lines[0] + "\n" + new String(buff);
//			System.out.println("received object " + o.getClass());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("-----------\n" + content + "\n-----------------------");
		return content;
	}


	private boolean remote() { return mRemote; }
	public boolean exists() { if (remote()) return mContent != null; else return super.exists();}
	public boolean canRead() { if (remote()) return exists(); else return super.canRead(); }
	public boolean canWrite() { if (remote()) return false; else return super.canWrite();}
	public boolean isDirectory() { if (remote()) return mType.equalsIgnoreCase(DIR); else return super.isDirectory(); }
	public boolean isFile() { if (remote()) return mType.equalsIgnoreCase(FILE);  else return super.isFile();}
	public long length() { if (remote()) return mContent.length();  else return super.length();}
	public String[] list() { 
		if (remote()) return mList;
		else return processList(super.getPath(), super.list());
	}

	/**
	 * PRocesses list of file names and prepends a 
	 * RemoteFile.FILE or RemoteFile.DIR to each depending on their type.
	 * @param names is a list of file names as obtained from FILE.list().
	 * This parameter is changed.
	 * @return a refernece to its modified parameter.
	 */
	public static String[] processList( String path, String names[])
	{
		for (int i= 0; i < names.length; i++)
		{
			File f = new File(path + File.separator + names[i]);
			if (f.isDirectory())
				names[i] = DIR + names[i];
			else
				names[i] = FILE + names[i];
		}
		return names;
	}

	public BufferedReader getBufferedReader()
	{
		if (remote())
		{
			if (isFile())
				return new BufferedReader(new StringReader(mContent));
			else
				return null;
		} else
			try {
				return new BufferedReader(new FileReader(this));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		return null;
	}
	
	static public class Server extends Thread
	{
		private Consol mConsol = Main.getConsol();
		private String mRoot = ".";
		private int mPort = 8082;
		public Server(String pFile)
		{
			Properties p = new Properties();
	        File f = new File (pFile);
	        if (f.exists()) {
				try {
					InputStream is = new BufferedInputStream(new
					     FileInputStream(f));
					p.load(is);
		        	is.close();
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
				}
	        }
	        mRoot = p.getProperty("root");
	        if (mRoot == null) mRoot = ".";
	        String r = p.getProperty("port");
	        if (r != null)
	        	mPort = Integer.parseInt(r)+1;
	        System.out.println("Server - Listening on port: " + mPort);
		}
	
		public void run() 
		{
			ServerSocket s = null;
			try {
				s = new ServerSocket(mPort);
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
			while (true) {
			try {
				Socket in = s.accept();
				InputStream ins = in.getInputStream();
				int b = 0;
//				System.out.println("Connection established");
				byte buff[] = new byte[1024];
				int len = ins.read(buff);
				if (len == 0) 
					continue;
				String fName = new String(buff).trim();
				byte resBuff[] = new byte[0];
				if (fName.startsWith("+peek"))
					resBuff = peekSession(fName);
				else
					resBuff = peekFile(fName);

				OutputStream out;
				out = in.getOutputStream();
				out.write(resBuff);
				out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private byte[] peekSession(String name) {
			return mConsol.peekSession();
		}

		private byte[] 	peekFile(String fName)
		{
			String result = NULL + "0\n"; // length = 0;
			byte[] resBuff = new byte[0];
			System.out.println("processing file: '" + fName + "'");
			String path = mRoot + File.separator + fName;
			File d = new File(path);
			if (d.exists())
			try {
				if (d.isDirectory())
				{
					result = DIR + "\n";
					String all = "";
					String names[] = d.list();
					processList(path, names);
					for (int i=0; i< names.length; i++) {
						all += names[i]+"\n";
					}
					result += all.length() + "\n";
					resBuff = all.getBytes();
				} else
				{
					result = FILE + "\n";
					int size = (int)d.length();
					result += size + "\n";
					byte fbuff[] = new byte[size];
					InputStream fins = new FileInputStream(d);
					int p = 0;
					while (p < size)
					{
						p+= fins.read(fbuff, p, size-p);
					}
					resBuff = fbuff;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		
			System.out.println("returning: " + d.getPath()); // + "\n" + result);
			return (result + new String(resBuff)).getBytes();
		}
	}
}
	
	
