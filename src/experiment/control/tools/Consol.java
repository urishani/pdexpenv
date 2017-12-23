/*
 * Created on 15/02/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment.control.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import tools.http.WebServer;
import tools.logs.RemoteFile.Server;
import experiment.Main;


/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Consol 
{
	private Properties mProperties;
	private WebServer mWebServer;
	private Server mRemoteFileServer;
	private String mTemplate =""; //"not implemented yet";
	public Consol(Properties pProperties)
	{
		mProperties = pProperties;
		String root = mProperties.getProperty("Project.Web.Root");
		String serverProperties = root + File.separator + "www-server.properties"; 
		mWebServer = new ConsolWebServer(0, serverProperties, 
			true, this );
		mWebServer.start();
		mRemoteFileServer = new Server(serverProperties);
		mRemoteFileServer.start();
		File tFile = new File(root + File.separator +  mProperties.getProperty("Project.Status.Template"));
		FileInputStream is;
		try {
			is = new FileInputStream(tFile);
			byte[] buf = new byte[10240];
			int n;
			while ((n = is.read(buf)) > 0) 
			{
				mTemplate += new String(buf);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("Template= '" + mTemplate + "'\n==============================================================================");
	};
	private String embed(String pTemplate, Properties pProperties)
	{
		String res = "";
		boolean first = true;
		StringTokenizer st = new StringTokenizer(pTemplate, "@");
		while (st.hasMoreElements())
		{
			String nextPart = st.nextToken("@");
			if (!first) nextPart = nextPart.substring(1);
			first = false;
			res += nextPart;
			if (st.hasMoreTokens())
			{
				String name = st.nextToken(";").substring(1);
				String val = pProperties.getProperty(name);
				if (val == null)
					val = "@" + name + "=null";
				res += val;
			}	
		}
		return res;
	}
	
	public byte[] peekSession()
	{
		// TODO
		return new byte[0];
	}
	public String getStatus(String IP)
	{
		Properties p = Main.getMonitor().getTestProperties();
		p.setProperty("IP",IP);
		return embed(mTemplate, p);
	}
	/**
	 * @param userId
	 * @param testId
	 */
	public void startNewTest(String userId, String testId) 
	{
		Main.getMonitor().startNewTest(userId, testId);	
	}
	/**
	 * 
	 */
	public void StopTest() {
		Main.getMonitor().stopTest();	
	}
	/**
	 * @param userId
	 * @param testId
	 */
	public void resumeTest(String userId, String testId) {
		Main.getMonitor().resumeTest(userId, testId);	
	}
	/**
	 * 
	 */
	public void nextTask() {
		Main.getMonitor().nextTask();	
	}
	public void nextPhase() {
		Main.getMonitor().nextPhase();	
	}
	public void setNextTask(int pTask) {
		Main.getMonitor().setNextTask(pTask);	
	}
}
