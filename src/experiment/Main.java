/*
 * Created on 07/12/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import tools.html.HtmlPanel;

import experiment.control.AbstractControl;
import experiment.control.BaseControl;
import experiment.control.CodingControl;
import experiment.control.MonitorControl;
import experiment.control.WizardControl4AddAreaMod;
import experiment.control.WizardControl4AddAreaReg;
import experiment.control.WizardControl4AddCellMod;
import experiment.control.WizardControl4AddCellReg;
import experiment.control.WizardControl4AddRowMod;
import experiment.control.WizardControl4AddRowReg;
import experiment.control.WizardControl4MultAreaMod;
import experiment.control.WizardControl4MultAreaReg;
import experiment.control.WizardControl4MultCellMod;
import experiment.control.WizardControl4MultCellReg;
import experiment.control.WizardControl4MultRowMod;
import experiment.control.WizardControl4MultRowReg;
import experiment.control.WizardControl4RemAreaMod;
import experiment.control.WizardControl4RemAreaReg;
import experiment.control.WizardControl4RemCellMod;
import experiment.control.WizardControl4RemCellReg;
import experiment.control.WizardControl4RemRowMod;
import experiment.control.WizardControl4RemRowReg;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Main extends WindowAdapter 
{
	private static View mView;

	private static final MyProperties mProperties = new MyProperties();
	private static MonitorControl mMonitor;
	// Also start a web server thread for a consol of the monitor.
	private static experiment.control.tools.Consol mConsol =
		 new experiment.control.tools.Consol(mProperties);

	public static MonitorControl getMonitor() 
	{
		return mMonitor;
	}
	public static MyProperties getProperties()
	{
		return mProperties;
	}
	public static void main(String args[]) {
		String propertiesFile = "properties";
		if (args.length > 0)
			propertiesFile = args[0];
		try {
			mProperties.load(new FileInputStream(new File(propertiesFile)));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find properties file: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Cannot read properties file: " + e.getMessage());
		}
		
		EventProxy.setTraceFromProperties(mProperties, "Trace.EventProxy");
		HtmlPanel.setTraceFromProperties(mProperties, "Trace.HtmlPanel");
		mView = new View(mProperties, EventProxy.getInstance());
		mView.setTitle(mProperties.getProperty("Project.Title"));
		mView.setVisible(true);
		
		mView.setDefaultCloseOperation(
							WindowConstants.DO_NOTHING_ON_CLOSE);
		JFrame.setDefaultLookAndFeelDecorated(false);
		mView.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				mView.showPopup(false, 
					"You cannot close this window.\n" +
					"Please ask the supervisor to stop or pause the experiment.", 
					"Illegal action");
			}
		});

		Table table = new Table();
		BaseControl control = new BaseControl(mProperties, mView, table);
		EventProxy.getInstance().addListener(control);
		// parameters for table size in properties file:
		int tableDimCol = Integer.parseInt(mProperties.getProperty("Project.Table.Columns"));
		int tableDimRow = Integer.parseInt(mProperties.getProperty("Project.Table.Rows"));
		
		table.initialize(tableDimRow,tableDimCol,Table.RANDOM);
		
		mView.setTable(table);
		mMonitor = new MonitorControl(mProperties, mView, table);
		mMonitor.start();

		AbstractControl wCodingControl = new CodingControl(mProperties, mView, table);
		wCodingControl.start();
				

//		creating controls and listeners for the regular No Remainder tools:
		
		AbstractControl wControlRemAreaReg = new WizardControl4RemAreaReg(mProperties, mView, table);
		wControlRemAreaReg.start();
		
		AbstractControl wControlRemRowReg = new WizardControl4RemRowReg(mProperties, mView, table);
		wControlRemRowReg.start();
		
		AbstractControl wControlRemCellReg = new WizardControl4RemCellReg(mProperties, mView, table);
		wControlRemCellReg.start();
		
//		creating controls and listeners for the modified No Remainder tools:
		
		AbstractControl wControlRemAreaMod = new WizardControl4RemAreaMod(mProperties, mView, table);
		wControlRemAreaMod.start();
		
		AbstractControl wControlRemRowMod = new WizardControl4RemRowMod(mProperties, mView, table);
		wControlRemRowMod.start();
		
		AbstractControl wControlRemCellMod = new WizardControl4RemCellMod(mProperties, mView, table);
		wControlRemCellMod.start();

//		creating controls and listeners for the regular Multiply tools:

		AbstractControl wControlMultAreaReg = new WizardControl4MultAreaReg(mProperties, mView, table);
		wControlMultAreaReg.start();
		
		AbstractControl wControlMultRowReg = new WizardControl4MultRowReg(mProperties, mView, table);
		wControlMultRowReg.start();
		
		AbstractControl wControlMultCellReg = new WizardControl4MultCellReg(mProperties, mView, table);
		wControlMultCellReg.start();
		
//		creating controls and listeners for the modified Multiply tools:

		AbstractControl wControlMultAreaMod = new WizardControl4MultAreaMod(mProperties, mView, table);
		wControlMultAreaMod.start();
		
		AbstractControl wControlMultRowMod = new WizardControl4MultRowMod(mProperties, mView, table);
		wControlMultRowMod.start();
		
		AbstractControl wControlMultCellMod = new WizardControl4MultCellMod(mProperties, mView, table);
		wControlMultCellMod.start();
		

//		creating controls and listeners for the regular Addition tools:

		AbstractControl wControlAddAreaReg = new WizardControl4AddAreaReg(mProperties, mView, table);
		wControlAddAreaReg.start();
				
		AbstractControl wControlAddRowReg = new WizardControl4AddRowReg(mProperties, mView, table);
		wControlAddRowReg.start();
				
		AbstractControl wControlAddCellReg = new WizardControl4AddCellReg(mProperties, mView, table);
		wControlAddCellReg.start();

//		creating controls and listeners for the modified Addition tools:

		AbstractControl wControlAddAreaMod = new WizardControl4AddAreaMod(mProperties, mView, table);
		wControlAddAreaMod.start();
				
		AbstractControl wControlAddRowMod = new WizardControl4AddRowMod(mProperties, mView, table);
		wControlAddRowMod.start();
				
		AbstractControl wControlAddCellMod = new WizardControl4AddCellMod(mProperties, mView, table);
		wControlAddCellMod.start();		
	}
	public static experiment.control.tools.Consol getConsol() {
		return mConsol;
	}
}
