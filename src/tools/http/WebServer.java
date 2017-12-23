/* An example of a very simple, multi-threaded HTTP server.
 * Implementation notes are in WebServer.html, and also
 * as comments in the source code.
 */
package tools.http;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;
public abstract class WebServer extends Thread implements HttpConstants
{

    /* static class data/methods */

//	protected static Hashtable mFilesLog = new Hashtable(200);
	protected boolean mNoCache = false;
	protected int mPort = 8080;
	protected ServerSocket mSS;

	private int cnt = 0;

    /* print to stdout */
    protected static void p(String s) {
        System.out.println(s);
    }

    /* print to the log file */
    protected static void log(String s) {
        synchronized (log) {
            log.println(s);
            log.flush();
        }
    }
    
    
    static PrintStream log = null;
    /* our server's configuration information is stored
     * in these properties
     */
    protected Properties props = new Properties();

    /* Where worker threads stand idle */
    public static Vector<Worker> mThreads = new Vector<Worker>();

    /* the web server's virtual root */
    protected File mRoot;

    /* timeout on client connections */
    public static int mTimeout = 0;

    /* max # worker threads */
    public static int workers = 5;


    /* load www-server.properties from java.home */
    private void loadProps(String pFile) throws IOException {
        File f = new File (pFile);
//                (System.getProperty("java.home")+File.separator+
//                    "lib"+File.separator+"www-server.properties");
        if (f.exists()) {
            InputStream is =new BufferedInputStream(new
                           FileInputStream(f));
            props.load(is);
            is.close();
            String r = props.getProperty("root");
            if (r != null) {
                mRoot = new File(r);
                if (!mRoot.exists()) {
                    throw new Error(mRoot + " doesn't exist as server root");
                }
            }
            r = props.getProperty("timeout");
            if (r != null) {
				mTimeout = Integer.parseInt(r);
            }
            r = props.getProperty("workers");
            if (r != null) {
                workers = Integer.parseInt(r);
            }
			r = props.getProperty("port");
			if (r != null) {
				mPort = Integer.parseInt(r);
			}
            r = props.getProperty("log");
            if (r != null) {
                p("opening log file: " + r);
                log = new PrintStream(new BufferedOutputStream(
                                      new FileOutputStream(r)));
            }
        }

        /* if no properties were specified, choose defaults */
        if (mRoot == null) {
            mRoot = new File(System.getProperty("user.dir"));
        }
        if (mTimeout <= 1000) {
			mTimeout = 5000;
        }
        if (workers < 25) {
            workers = 5;
        }
        if (log == null) {
            p("logging to stdout");
            log = System.out;
        }
    }

    void printProps() {
        p("root="+mRoot);
        p("timeout="+mTimeout);
        p("workers="+workers);
        p("port="+mPort);
    }

    public void doMain(String[] a) throws Exception 
    {
//    	int port = 8080;
    	boolean noCache = false;
    	String props =  System.getProperty("java.home") +File.separator + "www" + File.separator + "www-server.properties";
    	if (a.length == 0) p("Use -help argument for help");
    	for (int i=0; i< a.length; i++)
    	{
    		if (null == a[i])
    			continue;
			if (a[i].equalsIgnoreCase("-props"))
			{
				if (i < a.length) i++;
				else continue;
				props = a[i];
			} else if (a[i].equalsIgnoreCase("-port"))
    		{
    			if (i < a.length) i++;
    			else continue;
    			mPort = Integer.parseInt(a[i]);
    		} else if (a[i].equalsIgnoreCase("-nocache"))
    		{
    			noCache = true;
    		} else if (a[i].equalsIgnoreCase("-help"))
    		{
    			p("Arguments: [<port num>] [-nocache] [-port <port num>] [-props <properties file>] [-help]");
    			p("Where: ");
    			p("\t -port <port num> or <port num> - alternative port to default 8080.");
    			p("\t -nocache - all responses will be marked for no-cache in the client.");
    			p("\t -props <properties file> - sets properties file.");
    			p("\t -help - this message.");
    			return;
    		} else try 
    		{
	    		mPort = Integer.parseInt(a[i]);
    		} catch (NumberFormatException e)
    		{
    			p("Illegal argument '" + a[i] + "' - skipped"); 
    		}
    	}
		init(0, props, noCache);
    }
    protected void init(int pPort, String pProps, boolean pNoCache)
    {
    	if (pPort != 0)
    		mPort = pPort;
    	
		p("Using properties file '" + pProps + "'");
		try {
			loadProps(pProps);
		} catch (IOException e) {
			e.printStackTrace();
		}
        /* start worker threads */
        for (int i = 0; i < workers; ++i) {
            Worker w = makeWorker(mThreads, mRoot, pNoCache);
            (new Thread(w, "worker #"+i)).start();
            mThreads.addElement(w);
        }

		printProps();
		try {
			mSS = new ServerSocket(mPort);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
	}

	/**
	 * @param mThreads
	 * @param mRoot
	 * @param mNoCache
	 * @param mConsol
	 * @return
	 */
	protected abstract Worker makeWorker(Vector pThreads, File pRoot, boolean pNoCache);

	public void run()
	{
        while (true) {

            Socket s;
			try {
				s = mSS.accept();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			System.out.println("connection requested!");
            Worker w = null;
            synchronized (mThreads) {
				Worker ws;
                if (mThreads.isEmpty()) {
                    ws = makeWorker(mThreads, mRoot, mNoCache);
                } else {
                    ws = (Worker) mThreads.elementAt(0);
                    mThreads.removeElementAt(0);
                }

				ws.setSocket(s);
				//(new Thread(ws, "additional worker")).start();
				System.out.println("Worker " + ws.getName());
//				if (!ws.isAlive())
//					ws.start();
            }
        }
	}
}





