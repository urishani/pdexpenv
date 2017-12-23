/*
 * Created on 24/11/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import experiment.control.MonitorControl;

/**
 * @author gshani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Table 
{
	public static final int CORRECT = 0;
	public static final int VALUE_ERROR = 1;
	public static final int AREA_ERROR = 2;
	public static final int VALUE_AREA_ERROR = 3;
	public static final int METHOD_ERROR = 4;
	public static final int VALUE_METHOD_ERROR = 5;
	public static final int AREA_METHOD_ERROR = 6;
	public static final int ALL_ERROR = 7;
	public static final int NO_ACTION_PROGRAM = 8;
	private int[][] mTableState;
	private boolean[][] mReadTouch;
	private boolean[][] mWriteTouch;
	private int[][] mOrigTable;
	private boolean[][] mCleanTouch;
	Font mFont = new Font("Tahoma",Font.PLAIN,10);
	Dimension mCellDim = new Dimension(10,10);
	private AbstractTableModel mDataModel = new AbstractTableModel() 
	{
		public int getColumnCount() { return mTable[0].length;}
		public int getRowCount() { return mTable.length;}
		public Class getColumnClass( int column ){ return Integer.class;}
		public Object getValueAt(int row, int col) 
		 { return new Integer(mTable[row][col]); }
	} ;
	private int[][] mTable = null;
	private ArrayList mRegistry = new ArrayList();
	public static final int RANDOM = 1;
	
	private int[][] copyTable(int[][] pTable)
	{
		int[][] newTable = null;
		if (pTable != null)
		{
			int n = pTable.length;
			int m = pTable[0].length;
			newTable = new int[n][m];
			for (int i=0; i<n; i++) for (int j=0; j< m; j++)
				newTable[i][j] = pTable[i][j];	
		}
		return newTable;
	}
	private boolean[][] copyReadTouch(int[][] pTable)
	{
		boolean[][] newReadTouch = null;
		if (pTable != null)
		{
			int n = pTable.length;
			int m = pTable[0].length;
			newReadTouch = new boolean[n][m];
			for (int i=0; i<n; i++) for (int j=0; j< m; j++)
				newReadTouch[i][j] = false;	
		}
		return newReadTouch;
	}

	private boolean[][] copyWriteTouch(int[][] pTable)
	{
		boolean[][] newWriteTouch = null;
		if (pTable != null)
		{
			int n = pTable.length;
			int m = pTable[0].length;
			newWriteTouch = new boolean[n][m];
			for (int i=0; i<n; i++) for (int j=0; j< m; j++)
			newWriteTouch[i][j] = false;	
		}
		return newWriteTouch;
	}

	public void resetTable()
	{
		mTable = copyTable(mOrigTable);
		for (int i=0; i< mReadTouch.length;i++) 
		for (int j=0; j< mReadTouch[i].length;j++)
			mReadTouch[i][j]= false;
		for (int i=0; i< mWriteTouch.length;i++) 
		for (int j=0; j< mWriteTouch[i].length;j++)
			mWriteTouch[i][j]= false;
		mDataModel.fireTableDataChanged();
	}

	public void saveTableState()
	{
		mTableState = copyTable(mTable);
	}
	
	public class CustomTableCellRenderer extends DefaultTableCellRenderer
	{
		public CustomTableCellRenderer()
		{ 
			super();
			setHorizontalAlignment(CENTER);
			setMaximumSize(mCellDim);
		}
		/**
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column) 
	 	{
			CustomTableCellRenderer cell = (CustomTableCellRenderer) super.getTableCellRendererComponent
							(table, value, isSelected, hasFocus, row, column);
			//System.out.println("---------- CustomTableCellRenderer called for cell: "+ cell);
			Dimension d = cell.getSize();
//			System.err.println("d= " + d);
			d.width = d.height;
//			cell.setSize(d);
		 	if( row == 0 || column == 0)
		 	{
				cell.setBackground( Color.blue);
				cell.setForeground( Color.WHITE);
 		 	} else
 		 	{
 		 		Color c = Color.WHITE;
// 		 		if (mTable[row][column] != mTableState[row][column])
				if (mReadTouch[row][column])
					c = Color.LIGHT_GRAY;			
				if (mWriteTouch[row][column])
					c = Color.YELLOW;
				cell.setBackground( c);
				cell.setForeground( Color.black);
 		 	}
 		 	cell.setFont(mFont);
 		 	cell.setMaximumSize(mCellDim);
			cell.setMinimumSize(mCellDim);
 		 	cell.setSize(mCellDim);
// 		 	System.err.println("cell size = " + cell.getSize());

 		 	return cell;
		}
	}

	public JTable getJTable(TableModel pModel)
	{
		JTable t = new JTable(pModel);
		t.setCellEditor(null);
//		Font mono = new Font("Monospaced",Font.LAYOUT_RIGHT_TO_LEFT + Font.ITALIC,30);
//		t.setFont(mono);
		return t;
//		return new JTable(pModel) {
//			private TableCellRenderer mRenderer = new Table.CustomTableCellRenderer();
//			public TableCellRenderer getCellRenderer(int row, int column) {
//				System.out.println("+++++++++++++++++++++ getCellRenderer called");
////				if ((row == 0) || (column == 0)) {
//					return mRenderer;
////				}
//				// else...
////				return super.getCellRenderer(row, column);
//			}
//		};
//
//
	}
	public class TableException extends Exception{
		/**
		 * @param string
		 */
		public TableException(String string) 
		{
			super(string);
		}
	};
	
	public Table(){};
	public Table(int pN, int pM)
	{
		initialize(pN, pM, RANDOM);
	}
	public TableModel getDataModel()
	{
		return mDataModel;
	} 

	/**
	 * 
	 * @param pN
	 * @param pM
	 * @param pVal
	 * @throws TableException
	 */
	public void setElement (int pN, int pM, int pVal) throws TableException
	{ 
		if (mTable == null)
			throw new TableException("Table is null");
		if (pN < 1 || pN > mTable.length || pM < 1 || pM > mTable[0].length)
			throw new TableException("Indexes [" + pN + "," + pM + "] are out of table bounds (1.." + 
				mTable.length + "),(1.." + mTable[0].length + ")");
		int old = mTable[pN][pM];
		mTable[pN][pM] = pVal;
		mWriteTouch[pN][pM] = true;
		mDataModel.fireTableCellUpdated(pN-1,pM-1);
	}
	/**
	 * 
	 * @param pN
	 * @param pM
	 * @return
	 * @throws TableException
	 */
	public int getElement (int pN, int pM) throws TableException
	{
		mReadTouch[pN][pM] = true;
		return mTable[pN][pM];
	}
	/**
	 * 
	 * @param pN
	 * @param pM
	 * @param pMethod
	 */
	public void initialize (int pN, int pM, int pMethod)
	{
		mTable = new int[pN+1][pM+1];
		mReadTouch = new boolean[pN+1][pM+1];
		mWriteTouch = new boolean[pN+1][pM+1];

		for (int n=1;n<=pN;n++)
			mTable[n][0]= n;
		for (int m=1;m<=pM;m++)
			mTable[0][m]= m;

		for (int i=0; i< mReadTouch.length;i++) 
		for (int j=0; j< mReadTouch[i].length;j++)
			mReadTouch[i][j]= false;

		for (int i=0; i< mWriteTouch.length;i++) 
		for (int j=0; j< mWriteTouch[i].length;j++)
			mWriteTouch[i][j]= false;

		switch(pMethod) 
		{
			case RANDOM: 
				Random r = new Random(new Date().getTime());
				for (int n=1;n<=pN;n++)for( int m=1;m<=pM;m++)
				  mTable[n][m]= r.nextInt(100);
			break;
		}
		mOrigTable = copyTable(mTable);
		saveTableState();
		mDataModel.fireTableDataChanged();
	}
	/**
	 * @return array of 2 ints for the x and y dimensions of this table.
	 */
	public int[] getDim() {
		if (mTable == null)
			return new int[]{0,0};
		return new int[]{mTable.length-1, mTable[0].length-1};
	}
	/**
	 * @return
	 */
	public int[][] getOrigTable() {
		return copyTable(mOrigTable);
	}

	public boolean[][] getCleanReadTouch() {
		return copyReadTouch(mOrigTable);
	}

	public boolean[][] getCleanWriteTouch() {
		return copyWriteTouch(mOrigTable);
	}

	/**
	 * @param pWriteTable
	 * @param pKind
	 * @param t
	 * @return
	 */
	public int compareTable(int[][] pTable, boolean[][] pReadTable, boolean[][] pWriteTable, int pTask) 
	{
		int taskType = pTask;
		int result = 0;
		int r = 0;
		int readTouchErrType = 1;
		int valueErrType = 2;

		if (mTable == null)
			return (ALL_ERROR);

		int n = mTable.length;
		int m = mTable[0].length;
		if (n != pTable.length || m != pTable[0].length)
			return ALL_ERROR;

		int writeTouchErrCount = 0;	
		int valueReadErrCount = 0;
		int touchReadErrCount = 0;
		int writeNoReadErrCount = 0;
		int readNoWriteErrCount = 0;
		int valueWriteErrCount = 0;
		int readTouchErrCount = 0;
		int valueErrCount = 0;
		boolean goodRead = false;
		boolean goodNoRead = false;
		boolean missedRead = false;
		boolean excessRead = false;
		boolean goodWrite = false;
//		boolean goodValue = false;
		boolean missedWrite = false;
		boolean excessWrite = false;
		boolean goodNoWrite = false;
		boolean writeNoRead = false;
		boolean readNoWrite = false;
		boolean didReadWrite = false;
		boolean areaError = false;
		boolean valueError = false;
		boolean methodError = false;
		boolean areaReadError = false;
		boolean areaWriteError = false;
		boolean noAction = true;
		
		
		for (int i=1; i<n; i++) 
			for (int j=1; j<m; j++){
				
				boolean cellHasBeenProcessed = false;
				boolean inArea = false;
				boolean goodValue = false;

				if (mReadTouch[i][j] || mWriteTouch[i][j]){
					cellHasBeenProcessed = true;
					noAction = false;
				}
					
				if (mReadTouch[i][j] && pReadTable[i][j])
					goodRead = true;
				if (!mReadTouch[i][j] && pReadTable[i][j])
					missedRead = true;
				if (mReadTouch[i][j] && !pReadTable[i][j])
					excessRead = true;

				if(mWriteTouch[i][j] && pWriteTable[i][j])
					goodWrite = true;
				if(!mWriteTouch[i][j] && pWriteTable[i][j])
					missedWrite = true;
				if(mWriteTouch[i][j] && !pWriteTable[i][j])
					excessWrite = true;

				if (mWriteTouch[i][j] && mReadTouch[i][j])
					didReadWrite = true;
				if (mWriteTouch[i][j] && !mReadTouch[i][j])
					writeNoRead = true;
				if (!mWriteTouch[i][j] && mReadTouch[i][j])
					readNoWrite = true;

				if (mTable[i][j] == pTable[i][j]) 
					goodValue = true;
				if (pReadTable[i][j])
					inArea = true;
				
				
				if (taskType == MonitorControl.ADDORMULTTYPE){
					if (cellHasBeenProcessed){
						if (!goodValue)	valueError = true;
						if (writeNoRead) methodError = true;
						if (readNoWrite) methodError = true;
						if (!inArea)areaError = true;
					}else{ // NOT cellHasBeenProcessed
						if (inArea){
							areaError = true;
//							if (!goodValue) valueError = true;
						}
					}	
				}else{ // for REMTYPE
					if (mTable[i][j] == pTable[i][j]) 
						goodValue = true;
//					else goodValue = false;
					
					if (cellHasBeenProcessed){
						if (!goodValue)	valueError = true;
						if (writeNoRead) methodError = true;
//						if (readNoWrite && !goodValue) methodError = true;
						if (!inArea) areaError = true;
					}else{ // NOT cellHasBeenProcessed
						if (inArea) {
							areaError = true;
//							if (!goodValue) valueError = true;
						}
					}	
				}
			}

		int valueEval = 0; // for value errors
		int methodEval = 0; // for value errors in readTouched cells
		int areaEval = 0; // for method errors
		
		if (valueError) valueEval = 1; 			
		if (areaError) areaEval = 2;
		if (methodError) methodEval = 4;
		
			r = valueEval + areaEval + methodEval; //creating a combined value for the error type
			if (noAction) r = 8;
			switch(r){
				case 0:result=CORRECT;
				break;
				case 1:result=VALUE_ERROR;
				break;
				case 2:result=AREA_ERROR;
				break;
				case 3:result=VALUE_AREA_ERROR;
				break;
				case 4:result=METHOD_ERROR;
				break;
				case 5:result=VALUE_METHOD_ERROR;
				break;
				case 6:result=AREA_METHOD_ERROR;
				break;
				case 7:result=ALL_ERROR;
				break;
				case 8:result=NO_ACTION_PROGRAM;
				break;
			}
		return result;
	}

}
