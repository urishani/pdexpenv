/*
 * Created on 21/03/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.logs;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import tools.Base64;
import tools.html.Sniffer;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Log {
	private String mUserId, mExamId;
	private BufferedWriter mOutFile = null;
	private BufferedReader mInFile = null;
	private String mFileName;
	private long mStartTime, mElapsedTime;
	public static final int START = 1;
	public static final int HELP = 2;
	public static final int PHASE = 3;
	public static final int TEST = 4;
	public static final int CLOSE = 5;
	public static final int PHASE_CNT = 6;
	public static final int COURSE = 7;
	public static final int SELECT_WIZARD = 8;
	public static final int END_REST = 9;
	public static final int EXECUTE = 10;
	public static final int VERIFY = 11;
	public static final int SHOWCODE = 12;
	public static final int COMPILECODE = 13;
	public static final int CLEAR = 14;
	public static final int RESET = 15;
	public static final int SKIP = 16;
	public static final int REWARD = 17;
	public static final int TOTAL_REWARD = 18;
	public static final int CODE = 19;
	public static final int SESSION = 20;
	public static final int PARAMS = 21;
	public static final String separator = ",";
	static final String[] eventNames = new String[] {
		"NONE", "START", "HELP", "PHASE", "TEST", "CLOSE", "PHASE_CNT", "COURSE", 
		"SELECT_WIZARD", "END_REST", "EXECUTE", "VERIFY", "SHOWCODE", "COMPILECODE", 
		"CLEAR", "RESET", "SKIP", "REWARD", "TOTAL_REWARD", "CODE", "SESSION", "PARAMS"};
	public static final String EXTENSION = "csv";
	
	private String eventName(int entry)
	{
		if (entry < 0 || entry >= eventNames.length) return "UNKNOWN";
		return eventNames[entry];
	} 
	
	public Log(String pUserId, String pExamId, String pDir) throws IOException 
	{
		if (pDir == null || pDir.length()==0)
			pDir = ".";
		pDir += File.separator;
		
		mUserId = pUserId;
		mExamId = pExamId;
		mFileName = pDir + pUserId + "_" + pExamId + "." + EXTENSION;
/*		String a = "Hello \n new line \t\n new line 3";
		String b = encode(a);
		String c = decode(b);
		System.err.println("  test decode/encode:");
		System.out.println("a = '" + a  + "'");
		System.out.println("b = '" + b  + "'");
		System.out.println("c = '" + c  + "'");
		System.err.println("-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-");
		*/
	}
	
	/**
	 * @param fileName
	 */
	public Log(BufferedReader pInFile) {
		mInFile = pInFile;	
	}

	private void setMode(boolean pWrite)
	{
		try {
			if (pWrite && mOutFile == null)
			{
				if (mInFile != null)
					mInFile.close();
				mOutFile = new BufferedWriter(new FileWriter(mFileName, true));
				mInFile = null;
			} else if (!pWrite && mInFile == null)
			{
				if (mOutFile != null)
					mOutFile.close();
				mInFile = new BufferedReader(new FileReader(mFileName));
				mOutFile = null;
			}
		} catch (IOException e) {
			System.err.println("Cannot append/read to/from log file '" + mFileName + "': " + e.getMessage());
		}
	}
	public String getFileName() { return mFileName;}
	public void start()
	{
		setMode(true); // append to end of file
		Date d = new Date();
		mStartTime = d.getTime();
		logEvent (START, d.toString());
	}
	public void logEvent(int pEvent, String pData)
	{
		setMode(true); // append to end of file
		String line = "";
		if (pEvent == START)
		{
			line = "0" + separator;
			mElapsedTime = 0;
		} else
		{
			long dtime = (new Date().getTime())-mStartTime;
			line = Long.toString(dtime) + separator;
			mElapsedTime = dtime;
		}
		if (pEvent == CODE)
		{
			pData = encode(pData);
		}

		line +=  pEvent + separator + eventName(pEvent) + separator + pData + " ";
//		line += "\r\n";
		try {
			mOutFile.write(line);
			mOutFile.newLine();
			mOutFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	/**
	 * @param pData
	 * @return
	 */
	private String encode(String pData) {
		return Base64.encodeObject(pData, Base64.DONT_BREAK_LINES);
	}

	public void startRead()
	{
		if (mInFile != null)
			mInFile = null;
		setMode(false);
	}
	

	public LogEvent readLogEvent()
	{
		String e = readEvent();
		if (e == null)  return null;
		return new LogEvent(e);
	}
	
	public void print()
	{
		LogEvent e;
		while ((e = readLogEvent()) != null  )
		{
			System.out.print("Time = " + e.getTime());
			System.out.print("Type = " + e.getTypeName());
			System.out.print("Data = " + e.getData());
			System.out.println("\n----------------");			
		}
	}
	
	public static void printTree(LogTreeElement tree, String tab)
	{
		for (int i = 0; i < tree.countChildren(); i++)
		{
			LogTreeElement ch = tree.getChildAt(i);
			System.out.println(tab + i + "- " + ch.toString());
			printTree(ch, tab+"   ");
		}
	}
	
	public static void main(String args[])
	{
//		RemoteFile.Server s = new RemoteFile.Server("Data/www/www-server.properties");
//		s.start();
		final JTree tTree= new JTree();
//		try {
//			t = new JTree(new LogModel(null, "data/www")); //new URL("FILE://localhost:8082/"), "."));
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
		final JScrollPane tView = new JScrollPane(tTree);
		JButton bLocal = new JButton("Local");
		final JButton bRemote = new JButton("Remote");
		JButton bSneef = new JButton("Sneef");
		JLabel lURL = new JLabel("Remote IP:");
		final JTextField tURL = new JTextField(20);
		bRemote.setEnabled(false);
		tURL.addKeyListener(new KeyListener(){
			public void keyTyped(KeyEvent e) {
				bRemote.setEnabled(tURL.getText().length()>0);
			}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}});
		JPanel pURL = new JPanel();
		pURL.add(lURL); pURL.add(tURL);
		JPanel pButtons = new JPanel();
		pButtons.add(bLocal); pButtons.add(bRemote);pButtons.add(bSneef);
		bLocal.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				tTree.setModel(new LogModel((URL)null, "data/www"));
			}});
		bRemote.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				try {
					tTree.setModel(new LogModel(
							new	URL("File://"+tURL.getText()+":8082"), "."));
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}
			}});
		bSneef.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Stack ips = Sniffer.sniffForAllIps(8082);
				URL[] urls = new URL[ips.size()];
				for (int i=0; i< ips.size(); i++)
					try {
						urls[i] = new URL("File://"+ips.get(i)+":8082");
					} catch (MalformedURLException e1) {
						e1.printStackTrace();
						continue;
					}
				tTree.setModel(new LogModel(urls, "."));
			}});

		Frame f = new Frame();
		Box p = new Box(BoxLayout.Y_AXIS);
		f.add(p);
		p.add(pURL);pURL.setVisible(true);pURL.setOpaque(true);
		p.add(pButtons);pButtons.setVisible(true);
		p.add(tView);
		
//System.out.println(f.getComponentCount());
		f.setBounds(10,10,300,500);
		f.show();

		/*
		String t = "a b+c d%e";
		String t1 = URLEncoder.encode(t);
		String t2 = URLDecoder.decode(t1);
		System.out.println("t= '" + t + "'");
		System.out.println("t1= '" + t1 + "'");
		System.out.println("t2= '" + t2 + "'");
//		LogTreeElement tree = new LogDir(null, "data/www" ); //logs/Jhon Doe_0000.csv");
//		printTree(tree, "");
		Log l=null;
		try {
			l = new Log("Jhon Doe","0000","data/www/logs");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		l.print();
*/
	}
	public String readEvent()
	{
		setMode(false); // set back to read mode from start if was in write mode.
		try {
			return mInFile.readLine();
		} catch (IOException e) {
			//e.printStackTrace();
		}	
		return null;
	}
	public void close()
	{
		if (mOutFile != null)
			logEvent(CLOSE, "");
		try {
			if (mOutFile != null) mOutFile.close();
			if (mInFile != null) mInFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String decode(String pData) {
		return (String) Base64.decodeToObject(pData);
	}
	public class LogEvent
	{
		/**
		 * @return Returns the mData.
		 */
		public String getData() {
			return mData;
		}
		/**
		 * @return Returns the mTime.
		 */
		public long getTime() {
			return mTime;
		}
		/**
		 * @return Returns the mTypeName.
		 */
		public String getTypeName() {
			return mTypeName;
		}
		/**
		 * @return Returns the mType.
		 */
		public int getType() {
			return mType;
		}
		long mTime;
		int mType;
		String mData;
		private String mTypeName;
		
		public LogEvent(String p)
		{
			StringTokenizer st = new StringTokenizer(p, separator);
			mTime = Long.parseLong(st.nextToken());
			mType = Integer.parseInt(st.nextToken());
			mTypeName = st.nextToken();
			mData = st.nextToken();
			while (st.hasMoreTokens())
				mData+= separator + st.nextToken();
			if (mType == CODE)
				mData = decode(mData.trim());
		}
	}
}

class LogModel implements TreeModel
	{
		LogTreeElement mRoot;
		LogDir mDir = null;
		private Vector mListeners = new Vector(10);
		public LogModel(URL pURL, String pPath)
		{
			mRoot = new LogDir(pURL, pPath);
		}
		/**
		 * @param urls
		 * @param string
		 */
		public LogModel(URL[] pURLs, String pPath) {
			mRoot = new LogNetwork(pURLs, pPath);
		}
		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeModel#getRoot()
		 */
		public Object getRoot() {
			return mRoot;
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
		 */
		public Object getChild(Object parent, int index) {
			return ((LogTreeElement)parent).getChildAt(index);
/*			if (parent instanceof String) // this is root 
			{
				if (index < mFiles.length) 
				{
					if (mLogs == null)
						mLogs = new LogUser[mFiles.length];
					if (mLogs[index] == null)
						mLogs[index] = new LogUser(null, mFiles[index]);
					return mLogs[index];
				}
				else return null;
			}
			return null;
*/
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
		 */
		public int getChildCount(Object parent) {
			return ((LogTreeElement)parent).countChildren();
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
		 */
		public boolean isLeaf(Object node) {
			return (node instanceof LogLeaf);
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
		 */
		public void valueForPathChanged(TreePath path, Object newValue) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
		 */
		public int getIndexOfChild(Object parent, Object child) {
			if (parent == null || child == null || isLeaf(parent))
				return -1;
			return ((LogTreeElement)child).getIndex();
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
		 */
		public void addTreeModelListener(TreeModelListener l) {
			mListeners.add(l);
			
		}

		/* (non-Javadoc)
		 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
		 */
		public void removeTreeModelListener(TreeModelListener l) {
			for (int i = 0; i < mListeners.size(); i++)
				if (mListeners.get(i) == l)
				{
					mListeners.remove(i);
					i--;
				}
		}
	
	}
