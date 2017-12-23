package gi;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import experiment.Table;

/*
 * Created on 25/10/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CodeProgram 
{
	private MyReadStream mIn = null;
	private int sLimit = 10000;
	private boolean sTrace = false;
	private Hashtable sVars = new Hashtable();
	private Vector sLabel = new Vector();
	private Vector sProgram = new Vector();
	private Stack  sStack = new Stack();
	private int mStep = 0;
	private MyPrintStream mOut = null;
	private Table mTable = null;

	public CodeProgram() {};
	
	public interface MyPrintStream
	{
		void println(String string);
		void print(String string);
	}
	public interface MyReadStream
	{
		String readln();
		String read();
	}
	public void reset()
	{
		sVars = new Hashtable();
		sLabel = new Vector();
		sProgram = new Vector();
		sStack = new Stack();	
		sTrace = false;
	}
	
	public void print()
	{
		for (int i = 0; i < sProgram.size(); i++)
		   System.out.println(((CodeWord)sProgram.elementAt(i)).toString());
	}

	public String getExitValue()
	{
		if (sStack.isEmpty()) return null;
		return (String)sStack.pop();
	}

	public String execute()
	{
//		System.out.println("Executing");
		// reset counters
		Iterator i = sProgram.iterator();
		CodeWord cmd = null;
		while (i.hasNext())
		   ((CodeWord)i.next()).resetCounter();
		try
		{
			int pc = 0;
			mStep = 0;
			while (pc >= 0)
			{
				if (pc >= sProgram.size())
				   return "Execution completed";  
				cmd = (CodeWord)sProgram.elementAt(pc);
				pc = cmd.eval(pc);
			}
		} catch (Exception e)
		{
			return "Error: Runtime error [" + cmd.mLocation + "]: " + baseClassName(e.getClass()) + " - "+ e.getMessage() + "\n Tip: Some errors happen if you forget to put a ; at teh end of a statement";
			// e.printStackTrace();
		}
		return "Execution completed without errors";
	}
	
	/**
	 * @param class1
	 * @return
	 */
	private String baseClassName(Class class1) {
		String r = "";
		StringTokenizer st = new StringTokenizer(class1.getName(), ".");
		while (st.hasMoreTokens()) r = st.nextToken();
		return r;
	}

	public int setLimit(int pLimit)
	{
		int tLimit = sLimit;
		if (pLimit > 0)
		   sLimit = pLimit;
		return tLimit;
	}
		
	public void setTrace(boolean pTrace)
	{
		sTrace = pTrace;
	}

	public class CodeWord
	{
		private int mCode;
		private String mArg;
		private int mLabel;
		private int mCounter = 0;
		private String mLocation;
		protected final static int ERR = -1;
		protected final static int LOAD = 0;
		protected final static int STORE = 1;
		protected final static int LC = 2;
		protected final static int COMP = 3;
		protected final static int BF = 4;
		protected final static int JMP = 5;
//		protected final static int MULT = 6;
//		protected final static int DIV = 7;
//		protected final static int REM = 8;
//		protected final static int MOD  = 9;
//		protected final static int ADD = 10;
//		protected final static int SUB = 11;
		protected final static int GOTO = 12;
		protected final static int NOOP = 13;
		protected final static int PRINT = 14;
		protected final static int TABLE = 15;
		protected final static int STORE_TABLE = 16;
		protected final static int LOAD_TABLE = 17;
		protected final static int PRINTLN = 18;
		protected final static int COMMENT = 19;
		protected final static int READ = 20;
		protected final static int LOGIC_OP = 21;
		protected final static int MATH_OP = 22;
		protected final static int STORENP = 23;
		protected final static int EXIT = 24;
		
		private void setLabel()
		{
			if (mLabel < 0) return;
			if (mLabel >= sLabel.size())
				sLabel.setSize(mLabel + 1);
			sLabel.set(mLabel, Integer.toString(sProgram.size()));
		}

		public CodeWord(int pLabel, int pCode, String pArg, String pLocation)
			{
				mCode = pCode;
				mArg = pArg;
				mLabel = pLabel;
				if (pLocation == null)
					mLocation = "unknow location";
				else
					mLocation = pLocation;
				sProgram.add(this);
				setLabel();
			}
		
		public void resetCounter()
		{ 
			mCounter = 0;
		}
	
	    private void trace()
	    {
	    	if (!sTrace) return;
	    	System.out.println(++mStep + " - " + toString());
	    }
	
		public String toString()
		{
			String label = "";
			if (mLabel > 0)
			   label = mLabel +":";
			return label + "\t" + mCode + " \t" + mArg + " ; \t" + mLocation;
		}
		private String popString() throws Exception
		{
			if (sStack.isEmpty())
				throw new Exception("Stack underflow.");
			return (String)sStack.pop();
		}
					
		private String peekString() throws Exception
		{
			if (sStack.isEmpty())
				throw new Exception("Stack underflow.");
			return (String)sStack.peek();
		}
		private int popInt() throws Exception
		{
			String v1 = popString();
			return (Integer.decode(v1)).intValue();
		}
		public int eval(int pc) throws Exception
		{
			trace();
			mCounter++;
			if (mCounter > sLimit) throw new Exception("Infinite loop detected");
			switch (mCode) {
				case LOAD : String v = (String)sVars.get(mArg);
					if (v == null) 
					   throw new Exception ("Var '" + mArg + "' is not defined. Tip: some errors happen if you forget to put a ; at the end of a statement.");
					sStack.push(v);
					break;
				case STORE: v = popString();
					sVars.put(mArg, v);
					break;
				case STORENP: v = peekString();
					sVars.put(mArg, v);
					break;
				case READ:
					{
						v = "0 value";
						if (mIn != null)
							v = mIn.readln();
						System.out.println("READ: " + v + "into " + mArg);
						sVars.put(mArg, v);
					}
					break;
				case LC: sStack.push(mArg);
					break;
				case COMP: 
					{	
						int v1 = popInt();
					  	int v2 = popInt();
					  	int cmp = 0;
					  	if (mArg.equals("<") && v2 < v1) cmp = 1;
						else if (mArg.equals("<=") && v2 <= v1) cmp = 1;
						else if (mArg.equals("<=") && v2 <= v1) cmp = 1;
						else if (mArg.equals(">") && v2 > v1) cmp = 1;
						else if (mArg.equals(">=") && v2 >= v1) cmp = 1;
						else if (mArg.equals("=") && v2 == v1) cmp = 1;
						else if (mArg.equals("<>") && v2 != v1) cmp = 1;
					  	sStack.push(Integer.toString(cmp));
					}
				  	break;
				case BF: 
					{
						int v1 = popInt();
						if (v1 == 0) // branch on false condition
						{
							String l = (String)sLabel.get(Integer.parseInt(mArg));
							if (l == null)
								throw new Exception("Label "+ mArg+ " not found.");
							pc = Integer.parseInt(l) - 1 ; // so pc ++ will set it correclty
						}
					}
					break;
				case GOTO: 
					{
						String l = (String)sLabel.get(Integer.parseInt(mArg));
						if (l == null)
						throw new Exception("Label "+ mArg+ " not found.");
					    pc = Integer.parseInt(l) - 1 ; // so pc ++ will set it correclty
					}
					break;
				case MATH_OP:
					{
						int i1 = popInt();
						int i2 = popInt();
							if (mArg.equals("*"))
								sStack.push(Integer.toString(i2 * i1));
							else if(mArg.equals("/"))
								sStack.push(Integer.toString((int)(i2 / i1)));
							else if(mArg.equals("+"))
								sStack.push(Integer.toString(i2 + i1));
							else if(mArg.equals("-"))
								sStack.push(Integer.toString(i2 - i1));
							else if(mArg.equals("REM"))
								sStack.push(Integer.toString(i2 % i1));
							else System.err.println("illeagal math operator: '" + mArg + "'");
									
							break;
									}
				case PRINT: case PRINTLN:
					{ 
						String s = popString();
						if (mCode == PRINTLN)
							s += "\n";							
						if (mOut == null)
							System.out.print(s);
						else mOut.print(s);
					}
					break;
				case EXIT: return Integer.MAX_VALUE;
				case TABLE:
					{
						if (mTable == null)
							throw new Exception("No Table defined for this execution.");
						else if (mArg.equals("Y"))
							sStack.push(Integer.toString(mTable.getDim()[1]));
						else if (mArg.equals("X"))
							sStack.push(Integer.toString(mTable.getDim()[0]));
						else if (mArg.equals("REF"))
						{
							int n = popInt();
							int m = popInt();
							sStack.push(Integer.toString(mTable.getElement(m,n)));
						}
					}
					break;
				case STORE_TABLE:
					{
						int val = popInt();
						int n = popInt();
						int m = popInt();
						mTable.setElement(m,n,val);
						break;
					}
				case LOGIC_OP:
					{
						boolean l1 = popInt() == 1;
						boolean l2 = popInt() == 1;
						if (mArg.equals("AND"))
							sStack.push((l1 && l2)?"1":"0");
						else if(mArg.equals("OR"))
							sStack.push((l1 || l2)?"1":"0");
						else System.err.println("illeagal logical operator: '" + mArg + "'");
						break;
					}
				default :
					break;
				}
			return ++pc;
		}
	}
	/**
	 * @param stream
	 */
	public void setOut(MyPrintStream stream) {
		mOut = stream;
	}

	/**
	 * @param stream
	 */
	public void setIn(MyReadStream stream) {
		mIn = stream;
	}

	/**
	 * @param table
	 */
	public void setTable(Table pTable) 
	{
		mTable = pTable;
	}

}
