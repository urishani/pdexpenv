package tools.http.chat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import tools.http.WebServer;
import tools.http.Worker;

public abstract class AChatServer extends WebServer
{

	// marks a CHAT message
	public static final String CHAT = "CHAT ";
	static Map<String, String> mUsers = new HashMap<String, String>(10);
	static String mUser = "anonymous";
	static String mPort_S = "8080";
	static String mHost = "localhost"; 

    // Date formatting for the chat protocol;
	private static SimpleDateFormat mSimpleDateFormat = null;
	/**
	 * Obtains a static refefrence to a simle date format with a certain date format.
	 * @return DateFormat.
	 */
    private static synchronized DateFormat getDateFormat() {
    	if (null == mSimpleDateFormat) {
    		mSimpleDateFormat = new SimpleDateFormat();
    		mSimpleDateFormat.applyPattern("yyyyMMdd hhmmss");
    	}
    	return mSimpleDateFormat;
    }
    /**
     * Converts a Date to a String according to fixed format.
     * @param pDate Date to be converted to String
     * @return String of the Date.
     */
    public static String date2String(Date pDate) {
    	return getDateFormat().format(pDate);
    }
    /**
     * Parses a String to a Date
     * @param pDate String date of a certain fixed format.
     * @return Date for that string. If illegal, than present time's date is returned.
     */
    public static Date string2Date(String pDate) {
    	try {
			return getDateFormat().parse(pDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Date();
		}
    }


	public void doMain(String a[]) {
		try {
			// remove own parameters:
			// look for -user
			int p = -1;
			for (p= 0; p < a.length; p++)
				if (a[p].toUpperCase().startsWith("-USER")) {
					mUser = a[p+1];
					a[p]=null;
					a[p+1]=null;
					break;
				}
		
			super.doMain(a);
			System.out.println("user=" + mUser);
			if (null == mSS)
				return;
			mPort_S = Integer.toString(mPort);
			mHost = mSS.getInetAddress().getHostName();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected Worker makeWorker(Vector pThreads, File pRoot, boolean pNoCache) {
		return new ServerChatWorker(pThreads, pRoot, pNoCache, this);
	}
	
	/**
	 * Add a user to the managed list if not there yet.
	 * @param user String encoding user as &lt;name>:&lt;port>.
	 * @param s Socket from which the message received.
	 */
	public static boolean manageUser(String user, Socket s) {
		InetAddress adrs = s.getInetAddress();
		String remoteHost = adrs.getHostAddress();
		String parts[] = user.split(":");
		user = parts[0];
		String port_S = "8080";
		if (parts.length > 1)
			port_S = parts[1];
		return manageUser(user, remoteHost, port_S);
	}
	
	/**
	 * Registers a new user with a certain host and port.
	 * @param user String user id
	 * @param host String host name
	 * @param port String port number
	 * @return boolean true for success, false for failure.
	 */
	public static boolean manageUser(String user, String host, String port) {
		String hostIp = host + ":" + port;
		String currentHostIp = mUsers.get(user);
		if (null != currentHostIp) {
			if (hostIp.equals(currentHostIp))
				return true;
			else 
				return false;
		}
		mUsers.put(user, hostIp);
		System.out.println("new user '" + user + "@" + hostIp + "] added.");
		return true;
	}

	/**
	 * Abstract method triggered when a new incoming message arrives.
	 * @param pFromUser String name of the user sending message.
	 * @param pFromMachine String name of the machine sending the message.
	 * @param pFromPort int of the port where from machine can accept messages.
	 * @param pAtDate Date for time of message creation
	 * @param pMsg String for the message.
	 */
	public abstract void receiveMsg(String pFromUser, String pFromMachine, int pFromPort, Date pAtDate, String pMsg );
	
	public static void main(String[] a) throws Exception
	{
		AChatServer srv = new AChatServer(){
			// This is how we handle incoming messages.
			// TODO implement code that will handle this incoming messages.
			public void receiveMsg(String pFromUser, String pFromMachine, int pFromPort, Date pAtDate, String pMsg) {
				System.out.println("Message from [" + pFromUser + " @ " + pFromMachine  +":" + pFromPort + "] at " + pAtDate.toString() + ":");
				System.out.println("\t" + join(pMsg.split("\n"),"\n\t"));
			}
		};
		srv.doMain(a);
		srv.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		while (true) {
			System.out.println("Enter a line in format <to user>;<ip>:<port>;<one line msg with $ to mean new line>:");
			String line = br.readLine();
			if (null == line || line.length() == 0)
			{
				System.out.println("Quit chat!");
				return;
			}
			String parts[] = line.split(";");
			if (parts.length < 2) {
				System.out.println("Missing parts in the input line. Try again");
				continue;
			}
			String user = parts[0];
			String to = parts[1];
			String msg = "";
			if (parts.length > 2)
				msg = parts[2];
			msg = join(msg.split("\\$"), "\n");
			parts = to.split(":");
			String port = "8080";
			if (parts.length > 1)
				port = parts[1];
			String host = parts[0];
				
			// This is how we handle outgoing messages
			// TODO implement code that will eventually call this method.
			// 1. Make suer the user is registered - that can be done once per each new user when connecting to him/her
			srv.manageUser(user, host, port);
			// 2. send the message. This is done for every message sent out after registration that is done above.
			srv.send(user, msg);
		}
		
		
	}

	
	public static String send(String pToUser, String pMsg) {
		return send(pToUser, null, 8080, pMsg);
	}
	/**
	 * Send message to a user at an ip address.
	 * @param pToUser String name of user to receive message.
	 * @param pToHost String name of the host ip to receive message.
	 * @param pToPort int port # listening in the host for messages.
	 * @param pMsg String message.
	 * @return String message if anything whent wrong. null if all is okay.
	 */
	public static String send(String pToUser, String pToHost, int pToPort, String pMsg) {
		String rc = null;
		if (null == pToHost) {
			String hostIP = mUsers.get(pToUser);
			if (null == hostIP)
				return "User [" + pToUser + "] not on my list.";
			String parts[] = hostIP.split(":");
			if (parts.length > 0)
				pToHost = parts[0];
			if (parts.length > 1)
				pToPort = Integer.parseInt(parts[1]);
		}
		Socket outS = null;
		OutputStream out = null;
		try {
			outS = new Socket(pToHost, pToPort);
			out = outS.getOutputStream();
			out.write(s2b(CHAT + mUser + ":" + mPort_S + ";" + AChatServer.date2String(new Date()) + "\n" + pMsg + "\000"));
			out.flush();
			out.close();
			outS.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rc = e.getMessage();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rc = e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rc = e.getMessage();
		} finally {
			if (null != out) {
				try {
					out.flush();
					out.close();
				} catch (IOException e) {}
			}
			if (null != outS)
				try {
					outS.close();
				} catch (IOException e) {}
		}
		return rc;
	}

	private static byte[] s2b(String string) {
		byte[] buff = new byte[string.length()];
		for (int i=0; i < string.length(); i++)
			buff[i] = (byte)string.charAt(i);
		return buff;
	}

	/**
	 * Inverse of String.split().
	 * Joins strings in an array into a delimiter-separated string 
	 * @param lines String[] of items to be joined.
	 * @param delim String delimiter to separate the tokens in the joined string. If null, comma "," is assumed
	 * @return
	 */
	public static String join(String[] lines, String delim) {
		if (null == delim)
			delim = ",";
		String result = "";
		for (int i = 0; i < lines.length; i++) {
			result += lines[i];
			if (i < lines.length -1)
				result += delim;
		}
		return result;
	}


}

class ServerChatWorker extends Worker
{
	/* (non-Javadoc)
	 * @see tools.http.Worker#processRequest(java.lang.String)
	 */
	public ServerChatWorker(Vector pThreads, File pRoot, boolean pNoCache, AChatServer pServer)
	{
		super(pThreads, pRoot, pNoCache);
		addHandler(new ChatHandler(pServer), true);
	}
	
	
	private class ChatHandler implements Worker.Handler {

		private final AChatServer mServer;
		
		public ChatHandler(AChatServer pServer) {
			mServer = pServer;
		}
		
		public boolean handleRequest(String firstLine, InputStream is, PrintStream ps, Socket s) throws IOException {
			if (false == firstLine.startsWith(AChatServer.CHAT))
					return false;
			System.out.println("Doing chat");
			firstLine = firstLine.substring(AChatServer.CHAT.length());
			String parts[] = firstLine.split(";");
			String user = parts[0].trim();
			String date_S = parts[1].trim();

			if (false == AChatServer.manageUser(user, s)) {
				System.err.println("User [" + user + "] already exists with different identification. Ignored.");
			}
			parts = user.split(":");
			user = parts[0];
			int port = Integer.parseInt(parts[1]);
			// Now read all the message:
			StringBuffer sb = new StringBuffer(1000);
			int b = -1;
			while (b != 0) {
				b = is.read();
				if (b != 0)
					sb.append((char)b);
			}
			String host = s.getInetAddress().getHostAddress();
			mServer.receiveMsg(user, host, port, AChatServer.string2Date(date_S), sb.toString());
			return true;
		}
		
	}
	

}
