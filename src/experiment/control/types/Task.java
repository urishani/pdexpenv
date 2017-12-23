/*
 * Created on 29/02/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment.control.types;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
	public class Task extends Enum {
		private Task(String pName){	super(pName);}
		protected String getGroup() { return "TASK";}
		private static int mVal= 0;
		
		/* (non-Javadoc)
		 * @see experiment.control.types.Enum#getEnum(boolean)
		 */
		protected int getEnum(boolean next) {
			if (next) return ++mVal;
			else return mVal;
		}
	
		/* (non-Javadoc)
		 * @see experiment.control.types.Enum#setEnum(int)
		 */
		protected void setEnum(int v) {
			mVal = v;
		}
	
		public static final Task AREA_ADD = new Task("AREA_ADD");
		public static final Task ROW_ADD = new Task("ROW_ADD");
		public static final Task CELL_ADD = new Task("CELL_ADD");
		public static final Task AREA_REM = new Task("AREA_REM");
		public static final Task ROW_REM = new Task("ROW_REM");
		public static final Task CELL_REM = new Task("CELL_REM");
		public static final Task AREA_MULT = new Task("AREA_MULT");
		public static final Task ROW_MULT = new Task("ROW_MULT");
		public static final Task CELL_MULT = new Task("CELL_MULT");
	}
