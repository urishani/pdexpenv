package gi;

//import gi.CodeProgram.CodeWord;

import java.io.LineNumberReader;
import java.util.Properties;
import java.util.Stack;

public class CodeInterpreter extends LL1_Grammar {

	private LineNumberReader mSource = null; 
	private CodeProgram mProgram= null;
	public CodeProgram getProgram() { return mProgram; }
	private boolean mShowAsm = false;

	private int labelCnter = 0;
	private int nextLabel() { return ++labelCnter; }
	
	private int topLabel(Object pO)
	{
		int l = Integer.parseInt(((Properties)pO).getProperty("topLabel"));
		//int[] l = string2Array((String)pO);
		return l*2 - 1; // make it uneven number
	}

	private int endLabel(Object pO)
	{
		int l = Integer.parseInt(((Properties)pO).getProperty("endLabel"));
		//int[] l = string2Array((String)pO);
		return l*2; // make it even number
	}
	
	private String symbol(Object pO)
	{
		return ((Properties)pO).getProperty("symbol");
	}

//	private int[] string2Array(Object s)
//	{
//		StringTokenizer st = new StringTokenizer((String)s, "_");
//		int r[] = new int[st.countTokens()];
//		int i=0;
//		while (st.hasMoreTokens())
//			r[i++] = Integer.parseInt(st.nextToken());
//		return r;
//	}
//
	private Object setAttributes(int loopLabel1, int loopLabel2, String id1) 
	{
		Properties p = new Properties();
		p.setProperty("topLabel",Integer.toString(loopLabel1));
		p.setProperty("endLabel",Integer.toString(loopLabel2));
		if (id1 != null) p.setProperty("symbol",id1);
		return p;
	}
//	private String array2String(int[] a)
//	{
//		String r = "";
//		for (int i=0; i< a.length; i++)
//			r += a[i] + "_";
//		return r;
//	}
	
	private Stack forStack = new Stack();

	private String location() {
		return "Line " + (mSource.getLineNumber()+1);				
	}
	
	public CodeInterpreter(boolean pShowAsm) {
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
		put("SPACE", new Repetition(PosixClass.space(), 1, INFINITY)); // [[:space:]]+
		put("COMMENT", new Concatenation(
							new Singleton("#"),
							new Repetition(
								new Union(
									new NonMatch("\n"),
									PosixClass.print()),
								1, INFINITY)));
	
		/**
		* Semantic Specification
		*/
// if do else elseif endif semantics
//-----------------------------------		
		Semantics skip_do = new Semantics() {
			public void evaluate(ParseTree tree) {
				int final_label = nextLabel();
				tree.attribute = setAttributes(final_label, final_label, null); //new int[]{final_label, final_label});
				tree.phrase[5].attribute = tree.attribute; // pass this label number to the "else->" subtree root.
				if (mShowAsm) System.out.println("     BF   do" + topLabel(tree.attribute));
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.BF, Integer.toString(topLabel(tree.attribute)), location());
			}

		};

		Semantics skip_elseif_do = new Semantics() {
			public void evaluate(ParseTree tree) {
				//Integer doLable = (Integer) tree.attribute;
				int doLabel = nextLabel();
				int final_label = endLabel(tree.attribute);
				//labels[0]= doLabel; // new 'top' label
				tree.attribute = setAttributes(doLabel, final_label, null); //array2String(labels);
				tree.phrase[6].attribute = tree.attribute; // pass this label number to the "else->" subtree root.
				if (mShowAsm) System.out.println("     BF   do" + topLabel(tree.attribute));
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.BF, Integer.toString(topLabel(tree.attribute)), location());
			}
		};

		Semantics final_if_label = new Semantics() {
			public void evaluate(ParseTree tree) {
				// The tree.attrribute value was set by the start_do semantic action.
				String labelPrefix = "do";
				int labelNum = topLabel(tree.attribute);
				if (tree.phrase[5].phrase.length != 0) // "else->" is not empty. So it is the end of an else block.
				{
					labelPrefix = "else";
					labelNum= endLabel(tree.attribute); 
				}
				if (mShowAsm) System.out.println(labelPrefix + labelNum + ":");
				mProgram.new CodeWord(labelNum, CodeProgram.CodeWord.NOOP, null, location());
			}
		};
				
		Semantics skip_else = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("     GOTO  else" + endLabel(tree.attribute));
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.GOTO, Integer.toString(endLabel(tree.attribute)), location());
				if (mShowAsm) System.out.println("do" + topLabel(tree.attribute) + ":");
				mProgram.new CodeWord(topLabel(tree.attribute), CodeProgram.CodeWord.NOOP, null, location());
			}
		};
										
// Loop semantics
//------------------						
//  {"while", label_loop, "Condition", exit_loop, "loop", "StmtList", go_loop, "end", "loop", ";"},
		Semantics label_loop = new Semantics() {
			public void evaluate(ParseTree tree) {
				int loopLabel = nextLabel();
				tree.attribute = setAttributes(loopLabel, loopLabel, null); //array2String(new int[]{loopLabel, loopLabel});
				if (mShowAsm) System.out.println("top" + topLabel(tree.attribute) + ":");
				mProgram.new CodeWord(topLabel(tree.attribute), CodeProgram.CodeWord.NOOP, null, location());
			}
		};
		Semantics exit_loop = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("     BF   end" + endLabel(tree.attribute));
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.BF, Integer.toString(endLabel(tree.attribute)), location());
			}
		};
		Semantics go_loop = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("      GOTO  top" + topLabel(tree.attribute));
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.GOTO, Integer.toString(topLabel(tree.attribute)), location());
				if (mShowAsm) System.out.println("end" + endLabel(tree.attribute) + ":");
				mProgram.new CodeWord(endLabel(tree.attribute), CodeProgram.CodeWord.NOOP, null, location());
			}
		};

// For statement semantics
//------------------------
//  {"for", "IDENTIFIER", ":=" , "Expr", label_floop, "to", "Expr", "loop", exit_floop, "StmtList", "next", "IDENTIFIER", go_floop, ";"},
  Semantics label_floop = new Semantics() {
	  public void evaluate(ParseTree tree) 
	  {
	 	String id1 = tree.phrase[1].attribute.toString();
		int loopLabel = nextLabel();
		tree.attribute = setAttributes(loopLabel, loopLabel, id1);//array2String(new int[]{loopLabel, loopLabel})+"";
		if (mShowAsm) System.out.println("      STORE " + symbol(tree.attribute));
		mProgram.new CodeWord(-1, CodeProgram.CodeWord.STORE, id1, location());
		if (mShowAsm) System.out.println("ftop" + topLabel(tree.attribute) + ":");
		mProgram.new CodeWord(topLabel(tree.attribute), CodeProgram.CodeWord.NOOP, null, location());
	  }
  };

  Semantics exit_floop = new Semantics() {
	  public void evaluate(ParseTree tree) {
	  	String id1= symbol(tree.attribute);
		if (mShowAsm) System.out.println("      LOAD  " + id1);
		mProgram.new CodeWord(-1, CodeProgram.CodeWord.LOAD, id1, location());
		if (mShowAsm) System.out.println("      COMP  " + ">=");
		mProgram.new CodeWord(-1, CodeProgram.CodeWord.COMP, ">=", location());
 		if (mShowAsm) System.out.println("      BF   fend" + endLabel(tree.attribute));
	    mProgram.new CodeWord(-1, CodeProgram.CodeWord.BF, Integer.toString(endLabel(tree.attribute)), location());
	  }
  };
  Semantics go_floop = new Semantics() {
	  public void evaluate(ParseTree tree)  throws Exception
	  {
  	    String id1 = symbol(tree.attribute);
	  	String id2 = tree.phrase[11].attribute.toString();
		if (!id1.equals(id2))
		{
			Exception ex = new Exception("Loop ID '" + id1 + "' does not match terminating id '" + id2 + "'");
			if (mSource != null)
				ex.extend(mSource);
			throw ex;
		}
		if (mShowAsm) System.out.println("      LOAD  " + id2);
		mProgram.new CodeWord(-1, CodeProgram.CodeWord.LOAD, id2, location());
		if (mShowAsm) System.out.println("      LC  1");
		mProgram.new CodeWord(-1, CodeProgram.CodeWord.LC, "1", location());
		if (mShowAsm) System.out.println("      +");
		mProgram.new CodeWord(-1, CodeProgram.CodeWord.MATH_OP, "+", location());
		if (mShowAsm) System.out.println("      STORE " + id2);
		mProgram.new CodeWord(-1, CodeProgram.CodeWord.STORE, id2, location());
		if (mShowAsm) System.out.println("      GOTO  ftop" + topLabel(tree.attribute));
		mProgram.new CodeWord(-1, CodeProgram.CodeWord.GOTO, Integer.toString(topLabel(tree.attribute)), location());
		if (mShowAsm) System.out.println("fend" + endLabel(tree.attribute) + ":");
		mProgram.new CodeWord(endLabel(tree.attribute), CodeProgram.CodeWord.NOOP, null, location());
	  }
  };
		
// Logical semantics
//----------------------
		Semantics compare = new Semantics() {
			public void evaluate(ParseTree tree) {
				String operator = tree.phrase[1].phrase[0].attribute.toString();
				if (mShowAsm) System.out.println("      COMP  " + operator);
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.COMP, operator, location());
			}
		};
		Semantics and_or = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("      " + tree.phrase[0].attribute.toString());
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.LOGIC_OP, tree.phrase[0].attribute.toString(), location());
			}
		};
		
//Arithmetic semantics
//----------------------
  		Semantics math_op = new Semantics() {
			  public void evaluate(ParseTree tree) {
				  if (mShowAsm) System.out.println("      " + tree.phrase[0].attribute.toString());
				  mProgram.new CodeWord(-1, CodeProgram.CodeWord.MATH_OP, tree.phrase[0].attribute.toString(), location());
			  }
		  };

//Load/Store semantics
//----------------------
		Semantics load_id = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("      LOAD  " + tree.phrase[0].attribute);
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.LOAD, tree.phrase[0].attribute.toString(), location());
			}
		};
		Semantics load_literal = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("      LC    " + tree.phrase[0].attribute);
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.LC, tree.phrase[0].attribute.toString(), location());
			}
		};
		Semantics load_string = new Semantics() {
			public void evaluate(ParseTree tree) {
				String literal = tree.phrase[0].attribute.toString();
				if (mShowAsm) System.out.println("      LC    " + literal);
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.LC, literal.substring(1,literal.length()-1), location());
			}
		};
		Semantics store = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("      STORE " + tree.phrase[0].attribute);
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.STORE, tree.phrase[0].attribute.toString(), location());
			}
		};		

// I/O semantics
//---------------
		Semantics print = new Semantics() {
			public void evaluate(ParseTree tree) {
				String asmCmd = "PRINT";
				int code = CodeProgram.CodeWord.PRINT;
				if (tree.phrase[0].attribute.toString().equalsIgnoreCase("println"))
				{
					asmCmd = "PRINTLN";
					code = 	CodeProgram.CodeWord.PRINTLN;
				}
				if (mShowAsm) System.out.println("      "+ asmCmd);
				mProgram.new CodeWord(-1, code, null, location());
			}
		};
		Semantics exit = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("      exit");
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.EXIT, null, location());
			}
		};

		Semantics read = new Semantics() {
			public void evaluate(ParseTree tree) {
				String asmCmd = "READ";
				int code = CodeProgram.CodeWord.READ;
				if (tree.phrase[0].attribute.toString().equalsIgnoreCase("readln"))
				{
					asmCmd = "READLN";
					code = 	CodeProgram.CodeWord.READ;
				}
				if (mShowAsm) System.out.println("      "+ asmCmd + "  " + tree.phrase[1].attribute);
				mProgram.new CodeWord(-1, code, tree.phrase[1].attribute.toString(), location());
			}
		};

// Table reference semantics
//----------------------------
		Semantics loadTableref = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("      TABLE REF" );
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.TABLE, "REF", location());
			}
		};
		Semantics tableDimY = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("      TABLE Y" );
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.TABLE, "Y", location());
			}
		};
		Semantics tableDimX = new Semantics() {
		public void evaluate(ParseTree tree) {
			if (mShowAsm) System.out.println("      TABLE X" );
			mProgram.new CodeWord(-1, CodeProgram.CodeWord.TABLE, "X", location());
			}
		};
		Semantics storeTR = new Semantics() {
			public void evaluate(ParseTree tree) {
				if (mShowAsm) System.out.println("      STORE tableref");
				mProgram.new CodeWord(-1, CodeProgram.CodeWord.STORE_TABLE, null, location());
			}
		};

//		Misceleneous semantics
//		------------------------
			  Semantics comment = new Semantics() {
				  public void evaluate(ParseTree tree) {
					  if (mShowAsm) System.out.println("    COMMENT  " + tree.phrase[0].attribute);
					  mProgram.new CodeWord(-1, CodeProgram.CodeWord.COMMENT, tree.phrase[0].attribute.toString(), location());
				  }
			  };		


//-------------------------------------- end of semantics --------------------------------------------

		/**
		* Syntax Specification
		*/
		put("StmtList", new Object[][] {
			{"Stmt", "StmtList"},
			{}
		});
		put("Stmt", new Object[][] {
			{"IDENTIFIER", ":=", "GeneralExpr", store, ";"},
			{"tableref", ":=", "GeneralExpr", storeTR, ";"},
			{"print", "Expr", print, ";"},
			{"println", "Expr", print, ";"},
			{"read", "IDENTIFIER", read, ";"},
			{"readln", "IDENTIFIER", read, ";"},
			{"while", label_loop, "Condition", exit_loop, "loop", "StmtList", go_loop, "end", "loop", ";"},
//			{"for", "IDENTIFIER", ":=" , label_floop, "Expr", eval_floop, "to", "Expr", exit_floop, "loop", "StmtList", go_floop, "next", "IDENTIFIER", ";"},
//			{"for", "IDENTIFIER", ":=" , "Expr", label_floop, "to", "Expr", exit_floop, "loop", "StmtList", "next", "IDENTIFIER", go_floop, ";"},
//			{"for", "IDENTIFIER", ":=" , "Expr", label_floop, "to", "Expr", "loop", exit_floop, "StmtList", go_floop, "next", "IDENTIFIER", ";"},
			{"for", "IDENTIFIER", ":=" , "Expr", label_floop, "to", "Expr", "loop", exit_floop, "StmtList", "next", "IDENTIFIER", go_floop, ";"},
			{"if", "Condition", skip_do, "do", "StmtList", "else->", final_if_label, "endif", ";"},
			{"COMMENT", comment},
			{"exit", "Expr", exit, ";"}
		});
		put("else->", new Object[][]{
			{skip_else, "else", "StmtList"},
			{skip_else, "elseif", "Condition", skip_elseif_do, "do", "StmtList", "else->"},
			{}
		});
		put("tableref", new Object[][]{
			{"table", "[", "Expr", "tableref->"}
		});
		put("tableref->", new Object[][]{
			{",", "Expr", "]"}
		});
		put("tableref-right", new Object[][]{
			{"tableref", loadTableref},
			{"table-dim"}
		});
		put("table-dim", new Object[][]{
			{"table.x", tableDimX},
			{"table.n", tableDimX},
			{"table.y",  tableDimY},
			{"table.m", tableDimY}
		});

		put("Comparison", new Object[][] {
			{"Expr", "compOp", "Expr", compare},
		});
		put("compOp", new Object[][] {
			{"<="/*, setOperator*/},
			{"<"/*, setOperator*/},
			{">="/*, setOperator*/},
			{">"/*, setOperator*/},
			{"="/*, setOperator*/},
			{"<>"/*, setOperator*/}
		});

		put("Condition", new Object[][] {
			{"C-Term", "C-Expr->"}
		});
		put("C-Expr->", new Object[][] {
			{"AND", "C-Term", and_or, "C-Expr->"},
			{}
		});
		put("C-Term", new Object[][] {
			{"C-Factor", "C-Term->"}
		});
		put("C-Term->", new Object[][] {
			{"OR", "C-Factor", and_or, "C-Term->"},
			{}
		});
		put("C-Factor", new Object[][] {
//			{"IDENTIFIER", load_id},
			{"Comparison"},
//			{"L-LITERAL", load_literal},
			{"(", "C-Expr", ")"}
		});


		put("Expr", new Object[][] {
			{"Term", "Expr->"}
		});
		put("Expr->", new Object[][] {
			{"+", "Term", math_op, "Expr->"},
			{"-", "Term", math_op, "Expr->"},
			{}
		});
		put("Term", new Object[][] {
			{"Factor", "Term->"}
		});
		put("Term->", new Object[][] {
			{"*", "Factor", math_op, "Term->"},
			{"/", "Factor", math_op, "Term->"},
			{"REM", "Factor", math_op, "Term->"},
			{}
		});
		put("Factor", new Object[][] {
			{"IDENTIFIER", load_id},
			{"LITERAL", load_literal},
			{"tableref-right"},
			{"(", "Expr", ")"}
		});
		put("GeneralExpr", new Object[][] {
			{"STRING", load_string},
			{"Expr"}
		});
		put("String", new Object[][] {
			{"'", "LITERAL", "'", load_string}
		});
// ---------------------------- end of syntax specifications -----------------------------------------
	}

	public static void main(String[] argv) throws Exception {
		boolean aShowAsm = false;
		boolean aTrace = false;
		int aLimit = 1000;
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
			if (argv[a] != null) { nArgs[b]= argv[a]; b++; }
		}
		CodeInterpreter interpreter = new CodeInterpreter(aShowAsm);
		ParseTree pt = interpreter.interpret(nArgs);
		CodeProgram mProgram = interpreter.getProgram();
		//mProgram.print();

		mProgram.setTrace(aTrace);
		mProgram.setLimit(aLimit);
		if (pt == null)
		{
			System.out.println("Program compiled with errors \n Tip: Some errors happen if you forget to put a ; at the end of a statement.");
			return;
		} else
		{
			System.out.println("Program compiled w/out errors"); 
			mProgram.execute();
			System.out.println("Program execution completed.");
		}
	}
	/* (non-Javadoc)
	 * @see gi.Grammar#interpret(java.io.InputStream)
	 */
	public CodeProgram compile(String program) throws Exception
	{
		mProgram = new CodeProgram();
		ParseTree pt;
			pt = interpret(program);
		//System.out.println(pt);
		if (pt == null) return null;
		return getProgram();
	}

	/* (non-Javadoc)
	 * @see gi.Grammar#interpret(java.io.LineNumberReader)
	 */
	ParseTree interpret(LineNumberReader source) throws Exception {
		mSource = source;
		return super.interpret(source);
	}

}
