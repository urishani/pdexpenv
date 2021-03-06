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
public class WizardControl4AddAreaMod extends AbstractWizardControl {

	/**
	 * @param pProperties
	 * @param pView
	 * @param pTable
	 */
	public WizardControl4AddAreaMod(MyProperties pProperties, View pView, Table pTable) {
		super(pProperties, pView, pTable);
	}

	// panel members
	int tableRows;
	int tableCols;
	int rowStart, rowEnd, colStart, colEnd, addValue;

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
	protected String createCode(AbstractHtmlPanel pPanel)
	{
		String code = 
			"  for row := " + rowStart + " to " + rowEnd + " loop\n" +
			"      for column := " + colStart + " to " + colEnd + " loop\n" +
			"         table[ row, column] := table[ row, column] + " + addValue + ";\n" +
			"      next column;\n" +
			"   next row;\n" +
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
			rowStart = getIntParam(pPanel, "rowStart", new Integer(0), new Integer(tableRows+1));
			rowEnd = getIntParam(pPanel, "rowEnd", new Integer(rowStart-1), new Integer(tableRows+1));
			colStart = getIntParam(pPanel, "colStart", new Integer(0), new Integer(tableCols+1));
			colEnd = getIntParam(pPanel, "colEnd", new Integer(colStart-1), new Integer(tableCols+1));
			addValue = getIntParam(pPanel, "addValue", new Integer(0), null);
		} catch (Exception e) 	
		{
			printErrln("Error: " + e.getMessage());
			return false;
		}
				
//		println("starting row = " + rowStart);		
//		println("ending row = " + rowEnd);
//		println("col = " + colStart);
//		println("ending col = " + colEnd);
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
		return "Wizard.AddAreaMod";
	}
}