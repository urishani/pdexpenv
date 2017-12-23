/*
 * Created on 12/11/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.html;

import gi.*;
import gi.LL1_Grammar;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HtmlInterpreter extends LL1_Grammar {

	private CodeProgram mProgram= new CodeProgram();
	public CodeProgram getProgram() { return mProgram; }
	private boolean mShowAsm = false;
	private Stack mStack = new Stack();
	private ArrayList mControls = new ArrayList();

	HtmlInterpreter(boolean pShowAsm) {
		/**
		* Lexical Specification
		*/
		int INFINITY = -1;
		mShowAsm = pShowAsm; 
		put("STRING", 
			new Concatenation(
				new Singleton("'"), 
				new Concatenation(
					new Repetition(
						new Union(
							new NonMatch("\'\n"), 
							PosixClass.print()),
						1, INFINITY), 
					new Singleton("'"))));
		put("LITERAL", new Repetition(PosixClass.digit(), 1, INFINITY)); // [[:digit:]]+
		put("IDENTIFIER", new Repetition(PosixClass.alpha(), 1, INFINITY)); // [[:alpha:]]+
		put("TEXT", 
			new Repetition(
//				new Union(
					new NonMatch("<\n"), 
//					PosixClass.print()),
				1, INFINITY));
		put("SPACE", new Repetition(PosixClass.space(), 1, INFINITY)); // [[:space:]]+

		/**
		* Semantic Specification
		*/
		Semantics text = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("      TEXT " + tree.phrase[0].attribute);
				appendText(tree.phrase[0].attribute.toString());
			}
		};
		Semantics br = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println(" <br> ");
				mControls.add(new CodeWord(CodeWord.NEW_LINE, null));
			}
		};
		Semantics lt = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println(" <lt> ");
				appendText("<");
			}
		};
		
		Semantics start_input = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("input:");
				mStack.push(new CodeWord(CodeWord.START_INPUT, ""));
			}
		};
		
		Semantics input_param = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("param name = '" + tree.phrase[0].attribute.toString() + "'" );
				mStack.push(new CodeWord(CodeWord.PARAM_VAL, new Boolean(true).toString()));
				mStack.push(new CodeWord(CodeWord.PARAM_NAME, tree.phrase[0].attribute.toString()));
			}
		};

		Semantics input_param_val = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("param " + tree.phrase[0].attribute.toString() + " = " + tree.phrase[2].attribute.toString() );
				mStack.push(new CodeWord(CodeWord.PARAM_VAL, tree.phrase[2].attribute.toString().replace('\'',' ').trim()));
				mStack.push(new CodeWord(CodeWord.PARAM_NAME, tree.phrase[0].attribute.toString()));
			}
		};

		Semantics end_input = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println(":input");
				mControls.add(generateControl(CodeWord.END_INPUT, mStack));
			}
		};

		/**
		* Syntax Specification
		*/
		put("StmtList", new Object[][] {
			{"Stmt", "StmtList"},
			{}
		});
		put("Stmt", new Object[][] {
			{"<br>", br},{"input"},{"<lt>", lt}, {"TEXT", text},
		});
		put("input", new Object[][] {
			{"<input ", start_input, "input_params", " />", end_input } 
		});
		put("input_params", new Object[][] {
			{ "input_param", "input_params" } 
		});
		put("input_param", new Object[][] {
			{"IDENTIFIER", input_param},
			{"IDENTIFIER", "=", "STRING", input_param_val} 
		});

	}
	
	/**
	 * @param string
	 */
	protected void appendText(String string) {
		CodeWord cw = null;
		if (mStack.size() > 0) 
		{
			cw = (CodeWord)mStack.peek(); 
			if (cw.mType == CodeWord.TEXT)
				cw.append(string);
			else
				cw = null;
		}
		if (cw == null) 
			mStack.push(new CodeWord(CodeWord.TEXT, string));
	}

	/**
	 * @param i
	 * @param mStack
	 * @return
	 */
	protected Object generateControl(int i, Stack pStack) {
		// TODO Auto-generated method stub
		return new Object();
	}

	private Object generateControl(int i, String string) {
		// TODO Auto-generated method stub
	 	return new Object();
	}

	public static void main(String[] argv) throws Exception {
		boolean aShowAsm = false;
		boolean aTrace = false;
		int aLimit = 100000;
		int a = 0;
		int b = 0;
		boolean help = argv.length == 0;
		for (a=0; a < argv.length; a++)
		{		
		   if (argv[a].equalsIgnoreCase("-asm")) { aShowAsm = true; argv[a]=null; continue; }
		   else if (argv[a].equalsIgnoreCase("-trace")) { aTrace = true;  argv[a]=null; continue;}
		   else if (argv[a].equalsIgnoreCase("-limit"))
		   {
				argv[a]=null;
				if (a <= argv.length)
				{
					a++;
					aLimit = Integer.parseInt(argv[a]);
					argv[a]=null;
				}
				continue;
		   } else if (argv[a].equalsIgnoreCase("-help"))
		   { help = true; argv[a]= null; continue;}
		   b++; 
		}
		if (help)
		{
			 System.out.println("Args: [-asm] [-trace] [-limit <n>] [-tree] {-|<file-name>}");
			 System.out.println(" Where: ");
			 System.out.println("\t -asm --> print the assembly progra.");
			 System.out.println("\t -trace --> prints execution trace.");
			 System.out.println("\t -limit <n> --> protects against infinite execution up to limit <n>.");
			 System.out.println("\t -tree --> prints parse tree.");
			 System.out.println("\t - --> reads input from standard input.");
			 System.out.println("\t <file-name> --> reads input from file <file-name>.");
			 return;
		}
		String[] nArgs = new String[b];
//		System.out.println(nArgs.length);
		for (a=0,b=0;a<argv.length;a++)
		{ 
			System.out.println(argv[a]);
			if (argv[a] != null) { nArgs[b]= argv[a]; b++; }
		}
		HtmlInterpreter interpreter = new HtmlInterpreter(aShowAsm);
		ParseTree pt = interpreter.interpret(nArgs);
		CodeProgram mProgram = interpreter.getProgram();
		//mProgram.print();

		mProgram.setTrace(aTrace);
		mProgram.setLimit(aLimit);
		if (pt == null)
		{
			System.out.println("Program compiled with errors");
			return;
		} else
		{
			System.out.println("Program compiled w/out errors"); 
			mProgram.execute();
			System.out.println("Program execution completed.");
		}
	}

	private class CodeWord {
		private int mType = NULL;
		private String mVal = "";
		public CodeWord(int pType, String pVal)
		{
			mType = pType;
			mVal = pVal;
		}

		public static final int NULL = 0;
		public static final int TEXT = 1;
		public static final int START_INPUT = 2;
		public static final int END_INPUT= 3;
		public static final int PARAM_NAME = 4;
		public static final int PARAM_VAL = 5;
		public static final int NEW_LINE = 6;
		/**
		 * @return type of code word.
		 */
		public int getType() {
			return mType;
		}

		/**
		 * @param string  appends to value of code word.
		 */
		public void append(String string) {
			mVal += string;			
		}

		/**
		 * @return value of code word.
		 */
		public String getVal() {
			return mVal;
		}

		/**
		 * @param string   sets value of code word.
		 */
		public void setVal(String string) {
			mVal = string;
		}

	}
}
