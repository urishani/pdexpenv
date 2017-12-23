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
public class WizardControl4AddCellReg extends AbstractWizardControl {

	/**
	 * @param pProperties
	 * @param pView
	 * @param pTable
	 */
	public WizardControl4AddCellReg(MyProperties pProperties, View pView, Table pTable) {
		super(pProperties, pView, pTable);
	}

	// panel members
	int tableRows;
	int tableCols;
	int rowIndex, colIndex, addValue;


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
			"   row := " + rowIndex + ";" +
			"   column := " + colIndex + ";" +
			"   	table[row, column] := table[row, column] + " + addValue + ";\n" +
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
			addValue = getIntParam(pPanel, "addValue", new Integer(0), null);
		} catch (Exception e) 	
		{
			printErrln("Error: " + e.getMessage());
			return false;
		}
				
//		println("Cell's row = " + rowIndex);		
//		println("Cell's column = " + colIndex);
//		println("Add = " + addValue);
		return true;
	}

	/* (non-Javadoc)
	 * @see experiment.AbstractControl#start()
	 */
	public void start() {
	}

	public String getDomain()
	{
		return "Wizard.AddCellReg";
	}

}