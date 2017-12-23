/*
 * Created on Apr 2, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment.control.tools;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

import tools.http.Worker;


/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ConsolWorker extends Worker
{
	private String myIP;
	private final Consol mConsol;
	public ConsolWorker() { mConsol = null; }
	
	public ConsolWorker(Vector pPool, File pRoot, boolean pNoCache, Consol pConsol, String IP) 
	{
		super(pPool, pRoot, pNoCache);
		myIP = IP;
		mConsol = pConsol;
	}	
	
	private String response(String msg)
	{
		String val = "";
		String button = "<br>\n<input type='button' value='Status' onClick='_status();'>";
		String script = "\n<script>function _status(){document.location='Status.Monitor.html';}</script>";
		return "<html><header><title>Monitor Manager</title></header><body><B> From: " + myIP + "</B><br>" + msg + button + "</body>" +
				script + "</html>"; 
	}
	protected String processRequest(String fname)
	{
//		register(fname); // register access pattern to files
	  File targ = new File(root, fname);
	  if (fname.startsWith("Status."))
	  {
		  String val = "";
		  targ.delete();
		  if (mConsol == null)
			  val = response("Status Not implemented yet");
		  else if (fname.equalsIgnoreCase("Status.Monitor.html"))
		  {
			  val = mConsol.getStatus(myIP);
		  } else if (fname.startsWith("Status.Monitor.NewTest") || fname.startsWith("Status.Monitor.ResumeTest"))
		  {
			  StringTokenizer st = new StringTokenizer(fname, ".");
			  for (int i=0; i<3; i++) st.nextToken();
			  String userId = st.nextToken();
			  String testId = st.nextToken();
			  if (fname.startsWith("Status.Monitor.NewTest"))
			  {
				  mConsol.startNewTest(userId, testId);
				  val = response("Started test for UserId:" + userId + ", and test ID: " + 
				  		testId);
			  } else if (fname.startsWith("Status.Monitor.ResumeTest"))
			  {
				  mConsol.resumeTest(userId, testId);
					val = response("Resumed test for UserId:" + userId + ", and test ID: " + 
						  testId);
			  }
		  } else if (fname.equalsIgnoreCase("Status.Monitor.StopTest.html"))
		  {
			  mConsol.StopTest();
			  val = response("Stoped current test");
		  } else if (fname.equalsIgnoreCase("Status.Monitor.NextTask.html"))
		  {
			  mConsol.nextTask();
			  val = response("Moved to next Task");
		  } else if (fname.equalsIgnoreCase("Status.Monitor.NextPhase.html"))
		  {
			  mConsol.nextPhase();
			  val = response("Moved to next phase");
		  } else if (fname.startsWith("Status.Monitor.SetNextTask"))
		  {
			  StringTokenizer st = new StringTokenizer(fname, ".");
			  for (int i=0; i<3; i++) st.nextToken();
			  String nextTask = st.nextToken().trim();
			  mConsol.setNextTask(Integer.parseInt(nextTask));
			  val = response("Set next task to specific value");
		  }
		  return val;
	   }
	   return null;
	}
}
