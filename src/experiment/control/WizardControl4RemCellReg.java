/*
 * Created on 17/12/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment.control;

import tools.html.AbstractHtmlPanel;
import experiment.MyProperties;
import experiment.Table;
import experiment.View;

/**
 * @author gshani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WizardControl4RemCellReg extends AbstractWizardControl {

	/**
	 * @param pProperties
	 * @param pView
	 * @param pTable
	 */
	public WizardControl4RemCellReg(MyProperties pProperties, View pView, Table pTable) {
		super(pProperties, pView, pTable);
	}

	// panel members
	int tableRows;
	int tableCols;
	int rowIndex, colIndex, divideBy, markWith;

	public boolean onEvent(String pName, String pValue, AbstractHtmlPanel pPanel) {
		mView.show();
		if (super.onEvent(pName, pValue, pPanel))
			return true;
		return false;
	}

	/**
	 * @param pPanel from which code is created
	 * @return created code in our own language.
	 */
	protected String createCode(AbstractHtmlPanel pPanel) {
		String code = 
			"  row := " + rowIndex + ";" +
			"  column := " + colIndex + ";" +
			"      if table[row, column] REM " +divideBy + "= 0 do\n" +
			"         table[row, column] := " + markWith + ";\n" +
			"      endif;\n" +
			"\n";
		return code;
	}

	/**
	 * Verifies that all data in the panel is legal
	 * @param HtmlPanel which is being verified
	 */
	protected boolean verify(AbstractHtmlPanel pPanel) 
	{
		tableRows = mTable.getDim()[0];
		tableCols = mTable.getDim()[1];
		try 
		{
			rowIndex = getIntParam(pPanel, "rowIndex", new Integer(0), new Integer(tableRows+1));
			colIndex = getIntParam(pPanel, "colIndex", new Integer(0), new Integer(tableCols+1));
			divideBy = getIntParam(pPanel, "divideBy", new Integer(0), null);
			markWith = getIntParam(pPanel, "markWith", new Integer(0), null);
		} catch (Exception e) 	
		{
			printErrln("Error: " + e.getMessage());
			return false;
		}
				
//		println("Cell's row = " + rowIndex);		
//		println("Cell's column = " + colIndex);
//		println("divide by = " + divideBy);
//		println("mark with = " + markWith);
		return true;
	}

	/* (non-Javadoc)
	 * @see experiment.AbstractControl#start()
	 */
	public void start() {
	}

	public String getDomain()
	{
		return "Wizard.RemCellReg";
	}
}