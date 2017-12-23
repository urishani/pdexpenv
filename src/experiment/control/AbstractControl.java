/*
 * Created on 16/11/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment.control;

import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import tools.html.AbstractHtmlPanel;
import tools.html.HComponent;
import experiment.EventProxy;
import experiment.MyProperties;
import experiment.Table;
import experiment.View;
import gi.CodeInterpreter;
import gi.CodeProgram;


/**
 * @author gshani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class AbstractControl 
	implements EventProxy.ProxyListener,
	CodeProgram.MyPrintStream,
	CodeProgram.MyReadStream
{
	private CodeInterpreter mExpInterpreter = null;
	protected String mDomain = "";
	protected final View mView;
	protected final Properties mProperties;
	protected final Table mTable;


	public AbstractControl(MyProperties pProperties, View pView, Table pTable)
		{
			mProperties = pProperties;
			mView = pView;
			mTable = pTable;
			EventProxy.getInstance(getDomain()).addListener(this);
		}
		
		/**
		 * @see applications.HtmlPanel.HTMLListener#onEvent(java.lang.String, java.lang.String)
		 */
		public boolean onEvent(String pName, String pValue, AbstractHtmlPanel pPanel) {
			System.out.println("Event for id= "+ pName + ", value= " + pValue);
			return false;
		}

		public void simulateEvent(String pName, String pValue, AbstractHtmlPanel pPanel)
		{
			EventProxy.getInstance(getDomain()).delegate(pName, pValue, pPanel);
		}

		/**
		 * 
		 */
		public abstract void start();

		/**
		 * @see gi.CodeProgram.MyReadStream#readln()
		 */
		public String readln() {
			return mView.getInput(true);
		}
		/**
		 * @see gi.CodeProgram.MyReadStream#read()
	 	*/
		public String read() {
			return mView.getInput(false);
		}

		protected void setIntParam(AbstractHtmlPanel pPanel, String pName, int pVal) throws java.lang.Exception
		{
//			JTextField fld = null;
//			JLabel lbl = null;
//			try
//			{
				HComponent cmp = pPanel.getComponent(pName);
				if (cmp==null) 
					throw new java.lang.Exception("No such field in panel: '"+ pName + "'");
				cmp.setText(centeredInt(0, pVal));
/*				if (o instanceof JTextField)
					fld = (JTextField)o;
				else if (o instanceof JLabel)
					lbl = (JLabel)o;
			} catch (ClassCastException cce) {}
			if (fld != null)
			{
				int n = fld.getColumns();
				fld.setText(centeredInt(n, pVal));
			}			
			if (lbl != null)
			{
				int n = lbl.getText().length();
				lbl.setText(centeredInt(n,pVal));
			}
*/						
		}
	
		/**
		 * @param n
		 * @param pVal
		 * @return String containing pVal padded with one blank on each side
		 */
		private String centeredInt(int n, int pVal) 
		{
//			final String pad = "                       ";
//			final int padLen = pad.length();
			String intS = Integer.toString(pVal);
			return " " + intS + " ";
/*			int intSize = intS.length();
			int preLen = (n-intSize)/2;
			if (preLen <=0) preLen = 1;
			if (preLen > padLen) preLen = padLen;
			int postLen = (n- intSize - preLen);
			if (postLen <=0) postLen = 1;
			if (postLen > padLen) postLen = padLen; 
			return pad.substring(0,preLen) + intS + pad.substring(0,postLen);
*/
		}


		private String getParam(AbstractHtmlPanel pPanel, String pName)  throws java.lang.Exception
		{
//			JTextField fld = null;
//			JLabel lbl = null;
			HComponent cmp = pPanel.getComponent(pName);
/*			try
			{
				Object o = pPanel.getComponent(pName);
				if (o==null) 
					throw new java.lang.Exception("No such field in panel: '"+ pName + "'");
				if (o instanceof JTextField)
					fld = (JTextField)o;
				else if (o instanceof JLabel)
					lbl = (JLabel)o;
			} catch (ClassCastException cce) {}
*/			
			String sVal = cmp.getText().trim();
//			if (fld != null) sVal = fld.getText().trim();
//			else if (lbl != null) sVal = lbl.getText().trim();
			if (sVal.length() == 0)
				throw new java.lang.Exception("Empty field '" + pName +"'");
			return sVal;
		}

		protected int getIntParam(AbstractHtmlPanel pPanel, String pName, Integer pLow, Integer pHigh) throws java.lang.Exception
		{
			int val = 0;
			String sVal = getParam(pPanel, pName);
			boolean isNum = true;
			try
			{
				val = Integer.parseInt(sVal);
			} catch (NumberFormatException nfe)
			{
				//throw new java.lang.Exception("Illegal number: '" + sVal + "' in field '" + pName + "'");
				isNum = false;
			}				
			if (!isNum) // check if it is an expression
			{
				if (mExpInterpreter == null) mExpInterpreter = new CodeInterpreter(true);
				CodeProgram cp = mExpInterpreter.compile("exit "+sVal+"\n;"); 
				if (cp == null)		
					throw new java.lang.Exception("Illegal expression: '" + sVal + "' in field '" + pName + "'");
				String newVal = null;
				String res = cp.execute();
				if (res != null && !res.startsWith("Error"))
					 newVal = cp.getExitValue();
				else 
					System.err.println("Expression evaluation error: '" + res + "'");
				if (newVal == null)
					throw new java.lang.Exception("Uncomputable simple expression: '" + sVal + "' in field '" + pName + "'");
				val = Integer.parseInt(newVal);
			}
			if (pLow != null && pLow.intValue() >= val)
				throw new java.lang.Exception("Number too small: '" + sVal + "' in field '" + pName + "'");
			if (pHigh != null && pHigh.intValue() <= val)
				throw new java.lang.Exception("Number too big: '" + sVal + "' in field '" + pName + "'");
			return val;
		}

//		protected String getExpParam(AbstractHtmlPanel pPanel, String pName, Integer pLow, Integer pHigh) throws java.lang.Exception
//		{
//			int val = 0;
//			String sVal = getParam(pPanel, pName);
//			if (mExpInterpreter == null) mExpInterpreter = new CodeInterpreter(true);
//			CodeProgram cp = mExpInterpreter.compile("A:="+sVal+"\n;exit A;"); 
//			if (cp == null)		
//				throw new java.lang.Exception("Illegal expression");
//			cp.execute();
//			sVal = cp.getExitValue();
//			if (sVal == null)
//				throw new java.lang.Exception("Uncomputable expression");
//			try
//			{
//				val = Integer.parseInt(sVal);
//			} catch (NumberFormatException nfe)
//			{
//				throw new java.lang.Exception("Illegal number: '" + sVal + "' in field '" + pName + "'");
//			}					
//			if (pLow != null && pLow.intValue() >= val)
//				throw new java.lang.Exception("Number too small: '" + sVal + "' in field '" + pName + "'");
//			if (pHigh != null && pHigh.intValue() <= val)
//				throw new java.lang.Exception("Number too big: '" + sVal + "' in field '" + pName + "'");
//			return sVal;
//		}
		/**
		 * 
		 * @return the event proxy to which this controller is registerd on instantiation.
		 */
		protected EventProxy getMyProxy()
		{
			return EventProxy.getInstance(getDomain());
		}
		/**
		 * @return the name of the domain for this controller instance or class so it is registered
		 * to the correct domain on initialization. 
		 */
		abstract public String getDomain();
		
		protected void hideCode(AbstractHtmlPanel pPanel)
		{
			HComponent sc = pPanel.getComponent("Example");
			if (sc != null)	sc.setVisible(false);	
			HComponent bh = pPanel.getComponent("Wizard.HideCode");
			if (bh != null) bh.setEnabled(false);		
			HComponent l = pPanel.getComponent("Wizard.CodeLabel");
			if (l != null) l.setVisible(false);		
			HComponent bs = pPanel.getComponent("Wizard.ShowCode");
			if (bs != null) bs.setEnabled(true);
			HComponent be = pPanel.getComponent("Wizard.Execute");
			if (be != null) be.setEnabled(true);
			HComponent bcl = pPanel.getComponent("Wizard.Clear");
			if (bcl != null) bcl.setEnabled(true);
			pPanel.setVisible(true);
			mView.setVisible(true);
		}
		protected void showCode(AbstractHtmlPanel pPanel, String pCode)
		{
//			ExamplePanel ep = new ExamplePanel();
//			String wText = "<body>r/><input type='button' id='Wizard.HideCode' value='Hide Code'/>" +
//			               "<input type='text' cols='40' rows='20' disable='1' id='Example'/></body>";
//		  				               
//			mView.setWizard(null, wText, getDomain(),HtmlPanel.TRIGGER_BUTTONS);
//			
			HComponent sc = pPanel.getComponent("Example");
//			HComponent c = sc;
			if (sc != null)
			{
				sc.setText(pCode);
				sc.setVisible(true);
/*				if (sc instanceof JScrollPane)
				{
					c = pPanel.getComponent("Example.content");
				}
				if (c instanceof JTextComponent) 
				{
					((JTextComponent)c).setText(pCode);
				}					
				if (sc instanceof JScrollPane)
				{
					((JScrollPane)sc).getViewport().setViewPosition(new Point(0,0));
				}
*/				
				HComponent bh = pPanel.getComponent("Wizard.HideCode");
				if (bh != null) bh.setEnabled(true);		
				HComponent l = pPanel.getComponent("Wizard.CodeLabel");
				if (l != null) l.setVisible(true);
				HComponent bs = pPanel.getComponent("Wizard.ShowCode");
				if (bs != null) bs.setVisible(false); 
				HComponent be = pPanel.getComponent("Wizard.Execute");
				if (be != null) be.setEnabled(false);
				HComponent bcl = pPanel.getComponent("Wizard.Clear");
				if (bcl != null)bcl.setEnabled(false);
			
			}
			else				
				JOptionPane.showMessageDialog(mView, pCode, "Show code for " + getDomain(), JOptionPane.INFORMATION_MESSAGE);
			pPanel.setVisible(true);
			mView.setVisible(true);
		}
			
		/**
		 * @param class1
		 * @return
		 */
		protected String baseClassName(Class class1) {
			String r = "";
			StringTokenizer st = new StringTokenizer(class1.getName(), ".");
			while (st.hasMoreTokens()) r = st.nextToken();
			return r;
		}

		public void println(String string) {
			mView.getMessagesWindow().infoMessage(string + "\n");
			mView.show();
		}

		public void printErrln(String string) {
			mView.getMessagesWindow().errorMessage(string + "\n");
			mView.show();
		}

		public void print(String string) {
			mView.getMessagesWindow().outputMessage(string);
			mView.show();
		}

		public void printErr(String string) {
			mView.getMessagesWindow().errorMessage(string);
			mView.show();
		}

}

		
