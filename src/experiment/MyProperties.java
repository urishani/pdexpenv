/*
 * Created on 07/12/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MyProperties extends Properties
{
	public MyProperties() {

//settings for table: (moved to properties file)
//		setProperty("Table.Title", "Table Window");
//		setProperty("Table.Title.Buttons", 
//			"<body><input type='button' id='Table.Reset' value='Reset Table' color='FFFFFF'/>" +
//			"<input type='button' id='Help.Start' value='Help' color='FFFFFF'/></body>");

//settings for commands window: (moved to properties file)			
//		setProperty("Commands.Filename.Login", "commands/LoginPanelControl.html"); 
//		setProperty("Commands.Title", "Tasks Window");
//		setProperty("Commands.Args",/* "-trace")"-rightLeft*/"-margins 0,10,10,0");
//		setProperty("Commands.Title.Buttons", 
//			"<body><input type='button' id='Commands.SkipTask' value='Skip to next task' />" +
////			"<input type='button' id='Help.Start' value='Help'/>" +//			"</body>");
//
////settings for messages window:(moved to properties file)
//		setProperty("Messages.Title", "Messages Window");
//		setProperty("Messages.Title.Buttons", 
//			"<body> <input type='button' id='Message.clear' value='Clear Messages' /></body>");
//			/* "<input type='button' id='Help.Start' value='Help'/>" */
//			
//
//// settings for wizard/coding window:(moved to properties file)
//		setProperty("Programming.Title", "Work Area Window");
//		setProperty("Programming.Title.Buttons", 
//			"<input type='button' id='Help.Start' value='Help'/>");

////other settings: ===> moved to properties file
//		setProperty("Input.Title", "Input window");
//		setProperty("Input.Title.Buttons", 
//			"<body><input type='button' id='Help.Index' value='Help'/>" +
//			"<input type='button' id='Input.clear' value='Clear'/> " +
//			"<input type='button' id='Input.save' value='Save'/> " +
//			"<input type='button' id='Input.load' value='Load'/></body>");
//		setProperty("Project.Args", "");
//		setProperty("HtmlPanel.Args", "");
//		setProperty("Wizard.Title", "Work Area Window");
//		setProperty("Wizard.Title.Buttons", "<body/>");
////			"<body><input type='button' id='Help.Index' value='Help'/>" +
////			"<input type='button' id='Wizard.Reset' value='Reset'/> " +
////			"<input type='button' id='Wizard.Execute' value='Execute'/></body>");
//		setProperty("Wizard.Args", /*-rightLeft*/"-margins 0,0,10,0");
//
//		setProperty("FullScreen.args", "-center");


//// list of command panels: ==> moved to properties file
//// for phase 1:
//// 		task 1:
//			setProperty("Commands.Filename.Course.1.Phase1.1", "commands/WizardPanelControl_AddArea.html");
//			setProperty("Wizard.Filename.Course.1.Phase1.1", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.2.Phase1.1", "commands/ModWizardPanelControl_AddArea.html");
//			setProperty("Wizard.Filename.Course.2.Phase1.1", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.3.Phase1.1", "commands/CodingPanelControl_AddArea.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase1.1", "panels/coding.html");
//
////		task 2:
//			setProperty("Commands.Filename.Course.1.Phase1.2", "commands/WizardPanelControl_AddRow.html");
//			setProperty("Wizard.Filename.Course.1.Phase1.2", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.2.Phase1.2", "commands/ModWizardPanelControl_AddRow.html");
//			setProperty("Wizard.Filename.Course.2.Phase1.2", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.3.Phase1.2", "commands/CodingPanelControl_AddRow.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase1.2", "panels/coding.html");
//
////		task 3:
//			setProperty("Commands.Filename.Course.1.Phase1.3", "commands/WizardPanelControl_AddCell.html");
//			setProperty("Wizard.Filename.Course.1.Phase1.3", "panels/empty_wizard_panel.html");
//
//			setProperty("Commands.Filename.Course.2.Phase1.3", "commands/ModWizardPanelControl_AddCell.html");
//			setProperty("Wizard.Filename.Course.2.Phase1.3", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.3.Phase1.3", "commands/CodingPanelControl_AddCell.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase1.3", "panels/coding.html");
//
////		task 4:
//			setProperty("Commands.Filename.Course.1.Phase1.4", "commands/WizardPanelControl_RemArea.html");
//			setProperty("Wizard.Filename.Course.1.Phase1.4", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.2.Phase1.4", "commands/ModWizardPanelControl_RemArea.html");
//			setProperty("Wizard.Filename.Course.2.Phase1.4", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.3.Phase1.4", "commands/CodingPanelControl_RemArea.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase1.4", "panels/coding.html");
//
////		task 5:
//			setProperty("Commands.Filename.Course.1.Phase1.5", "commands/WizardPanelControl_RemRow.html");
//			setProperty("Wizard.Filename.Course.1.Phase1.5", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.2.Phase1.5", "commands/ModWizardPanelControl_RemRow.html");
//			setProperty("Wizard.Filename.Course.2.Phase1.5", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.3.Phase1.5", "commands/CodingPanelControl_RemRow.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase1.5", "panels/coding.html");
//
////		task 6:
//			setProperty("Commands.Filename.Course.1.Phase1.6", "commands/WizardPanelControl_RemCell.html");
//			setProperty("Wizard.Filename.Course.1.Phase1.6", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.2.Phase1.6", "commands/ModWizardPanelControl_RemCell.html");
//			setProperty("Wizard.Filename.Course.2.Phase1.6", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.3.Phase1.6", "commands/CodingPanelControl_RemCell.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase1.6", "panels/coding.html");
//
////		task 7:
//			setProperty("Commands.Filename.Course.1.Phase1.7", "commands/WizardPanelControl_MultArea.html");
//			setProperty("Wizard.Filename.Course.1.Phase1.7", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.2.Phase1.7", "commands/ModWizardPanelControl_MultArea.html");
//			setProperty("Wizard.Filename.Course.2.Phase1.7", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.3.Phase1.7", "commands/CodingPanelControl_MultArea.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase1.7", "panels/coding.html");
//
////		task 8:
//			setProperty("Commands.Filename.Course.1.Phase1.8", "commands/WizardPanelControl_MultRow.html");
//			setProperty("Wizard.Filename.Course.1.Phase1.8", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.2.Phase1.8", "commands/ModWizardPanelControl_MultRow.html");
//			setProperty("Wizard.Filename.Course.2.Phase1.8", "panels/empty_wizard_panel.html");
//	
//			setProperty("Commands.Filename.Course.3.Phase1.8", "commands/CodingPanelControl_MultRow.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase1.8", "panels/coding.html");
//
////		task 9:
//			setProperty("Commands.Filename.Course.1.Phase1.9", "commands/WizardPanelControl_MultCell.html");
//			setProperty("Wizard.Filename.Course.1.Phase1.9", "panels/empty_wizard_panel.html");
//
//			setProperty("Commands.Filename.Course.2.Phase1.9", "commands/ModWizardPanelControl_MultCell.html");
//			setProperty("Wizard.Filename.Course.2.Phase1.9", "panels/empty_wizard_panel.html");
//
//			setProperty("Commands.Filename.Course.3.Phase1.9", "commands/CodingPanelControl_MultCell.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase1.9", "panels/coding.html");
//
//
//// for phase 2: (all the same:
////		task 1:
//			setProperty("Commands.Filename.Course.1.Phase2.1", "commands/CodingPanelControl_AddArea.html");
//			setProperty("Wizard.Filename.Course.1.Phase2.1", "panels/coding.html");
//				
//			setProperty("Commands.Filename.Course.2.Phase2.1", "commands/CodingPanelControl_AddArea.html");
//			setProperty("Wizard.Filename.Course.2.Phase2.1", "panels/coding.html");
//				
//			setProperty("Commands.Filename.Course.3.Phase2.1", "commands/CodingPanelControl_AddArea.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase2.1", "panels/coding.html");
//
////		task 2:
//			setProperty("Commands.Filename.Course.1.Phase2.2", "commands/CodingPanelControl_AddRow.html");
//			setProperty("Wizard.Filename.Course.1.Phase2.2", "panels/coding.html");
//			
//			setProperty("Commands.Filename.Course.2.Phase2.2", "commands/CodingPanelControl_AddRow.html");
//			setProperty("Wizard.Filename.Course.2.Phase2.2", "panels/coding.html");
//			
//			setProperty("Commands.Filename.Course.3.Phase2.2", "commands/CodingPanelControl_AddRow.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase2.2", "panels/coding.html");
//
////		task 3:
//			setProperty("Commands.Filename.Course.1.Phase2.3", "commands/CodingPanelControl_AddCell.html");
//			setProperty("Wizard.Filename.Course.1.Phase2.3", "panels/coding.html");
//		
//			setProperty("Commands.Filename.Course.2.Phase2.3", "commands/CodingPanelControl_AddCell.html");
//			setProperty("Wizard.Filename.Course.2.Phase2.3", "panels/coding.html");
//		
//			setProperty("Commands.Filename.Course.3.Phase2.3", "commands/CodingPanelControl_AddCell.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase2.3", "panels/coding.html");
//
////		task 4:
//			setProperty("Commands.Filename.Course.1.Phase2.4", "commands/CodingPanelControl_RemArea.html");
//			setProperty("Wizard.Filename.Course.1.Phase2.4", "panels/coding.html");
//		
//			setProperty("Commands.Filename.Course.2.Phase2.4", "commands/CodingPanelControl_RemArea.html");
//			setProperty("Wizard.Filename.Course.2.Phase2.4", "panels/coding.html");
//		
//			setProperty("Commands.Filename.Course.3.Phase2.4", "commands/CodingPanelControl_RemArea.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase2.4", "panels/coding.html");
//
////		task 5:
//			setProperty("Commands.Filename.Course.1.Phase2.5", "commands/CodingPanelControl_RemRow.html");
//			setProperty("Wizard.Filename.Course.1.Phase2.5", "panels/coding.html");
//	
//			setProperty("Commands.Filename.Course.2.Phase2.5", "commands/CodingPanelControl_RemRow.html");
//			setProperty("Wizard.Filename.Course.2.Phase2.5", "panels/coding.html");
//	
//			setProperty("Commands.Filename.Course.3.Phase2.5", "commands/CodingPanelControl_RemRow.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase2.5", "panels/coding.html");
//
////		task 6:
//			setProperty("Commands.Filename.Course.1.Phase2.6", "commands/CodingPanelControl_RemCell.html");
//			setProperty("Wizard.Filename.Course.1.Phase2.6", "panels/coding.html");
//
//			setProperty("Commands.Filename.Course.2.Phase2.6", "commands/CodingPanelControl_RemCell.html");
//			setProperty("Wizard.Filename.Course.2.Phase2.6", "panels/coding.html");
//
//			setProperty("Commands.Filename.Course.3.Phase2.6", "commands/CodingPanelControl_RemCell.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase2.6", "panels/coding.html");
//
////		task 7:
//			setProperty("Commands.Filename.Course.1.Phase2.7", "commands/CodingPanelControl_MultArea.html");
//			setProperty("Wizard.Filename.Course.1.Phase2.7", "panels/coding.html");
//
//			setProperty("Commands.Filename.Course.2.Phase2.7", "commands/CodingPanelControl_MultArea.html");
//			setProperty("Wizard.Filename.Course.2.Phase2.7", "panels/coding.html");
//
//			setProperty("Commands.Filename.Course.3.Phase2.7", "commands/CodingPanelControl_MultArea.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase2.7", "panels/coding.html");
//
////		task 8:
//			setProperty("Commands.Filename.Course.1.Phase2.8", "commands/CodingPanelControl_MultRow.html");
//			setProperty("Wizard.Filename.Course.1.Phase2.8", "panels/coding.html");
//
//			setProperty("Commands.Filename.Course.2.Phase2.8", "commands/CodingPanelControl_MultRow.html");
//			setProperty("Wizard.Filename.Course.2.Phase2.8", "panels/coding.html");
//
//			setProperty("Commands.Filename.Course.3.Phase2.8", "commands/CodingPanelControl_MultRow.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase2.8", "panels/coding.html");
//
////		task 9:
//			setProperty("Commands.Filename.Course.1.Phase2.9", "commands/CodingPanelControl_MultCell.html");
//			setProperty("Wizard.Filename.Course.1.Phase2.9", "panels/coding.html");
//
//			setProperty("Commands.Filename.Course.2.Phase2.9", "commands/CodingPanelControl_MultCell.html");
//			setProperty("Wizard.Filename.Course.2.Phase2.9", "panels/coding.html");
//
//			setProperty("Commands.Filename.Course.3.Phase2.9", "commands/CodingPanelControl_MultCell.html"); 
//			setProperty("Wizard.Filename.Course.3.Phase2.9", "panels/coding.html");
//
//
//// connecting the filenames for the wizard panels for menu access names:
//
//// 		empty panel:
//			 setProperty("Wizard.Filename.Empty", "panels/empty_wizard_panel.html");
//			 			
////		coding panel:
//			 setProperty("Wizard.Filename.Coding", "panels/coding.html");
//
//// For Regular Wizard:
////		no remainder tools:		
//			 setProperty("Wizard.Filename.RemCellReg", "panels/rem_cell_reg.html");
//			 setProperty("Wizard.Filename.RemAreaReg", "panels/rem_area_reg.html");
//			 setProperty("Wizard.Filename.RemRowReg", "panels/rem_row_reg.html");
//
////		 Add a number tools:		
//			 setProperty("Wizard.Filename.AddCellReg", "panels/add_cell_reg.html");
//			 setProperty("Wizard.Filename.AddAreaReg", "panels/add_area_reg.html");
//			 setProperty("Wizard.Filename.AddRowReg", "panels/add_row_reg.html");
//
////		 Multiply by tools:		
//			 setProperty("Wizard.Filename.MultCellReg", "panels/mult_cell_reg.html");
//			 setProperty("Wizard.Filename.MultAreaReg", "panels/mult_area_reg.html");
//			 setProperty("Wizard.Filename.MultRowReg", "panels/mult_row_reg.html");
//
//// For Modified Wizard:
////		no remainder tools:		
//			 setProperty("Wizard.Filename.RemCellMod", "panels/rem_cell_mod.html");
//			 setProperty("Wizard.Filename.RemAreaMod", "panels/rem_area_mod.html");
//			 setProperty("Wizard.Filename.RemRowMod", "panels/rem_row_mod.html");
//
////		 Add a number tools:		
//			 setProperty("Wizard.Filename.AddCellMod", "panels/add_cell_mod.html");
//			 setProperty("Wizard.Filename.AddAreaMod", "panels/add_area_mod.html");
//			 setProperty("Wizard.Filename.AddRowMod", "panels/add_row_mod.html");
//
////		 Multiply by tools:		
//			 setProperty("Wizard.Filename.MultCellMod", "panels/mult_cell_mod.html");
//			 setProperty("Wizard.Filename.MultAreaMod", "panels/mult_area_mod.html");
//			 setProperty("Wizard.Filename.MultRowMod", "panels/mult_row_mod.html");
//
// Variables for the monitoring:
// Timers moved to properties file		

// moved to properties file:			setProperty("Block.Repeats.Phase2", "2");
			
			try {
				load(new FileInputStream(new File("properties")));
			} catch (FileNotFoundException e) {
				System.err.println("Could not find properties file 'properties'");
			} catch (IOException e) {
				System.err.println("Error reading properties file 'properties':\n" + e.getMessage());
			}
	}
	
	/**
	 * @see java.util.Properties#getProperty(java.lang.String)
	 */
	public String getProperty(String arg0) {
		String p = super.getProperty(arg0);
		if (p==null) 
		{
			System.err.println("Could not fine property for '" + arg0 + "'");
			p = "";
		}
		return p;
	}

	public int getIntProperty(String arg0) {
		return Integer.parseInt(getProperty(arg0));
	}

	public int[] getIntsProperty(String arg0){
		StringTokenizer st = new StringTokenizer(getProperty(arg0).trim(), " ");
		int[] args = new int[st.countTokens()];
		int i=0;
		while (st.hasMoreTokens()) 
			args[i++] = Integer.parseInt(st.nextToken());
		return args;
		
	}
	public String[] getArgsPropery(String arg0)
	{
		StringTokenizer st = new StringTokenizer(getProperty(arg0).trim(), " ");
		String[] args = new String[st.countTokens()];
		int i=0;
		while (st.hasMoreTokens()) 
			args[i++] = st.nextToken();
		return args;
	}
}
