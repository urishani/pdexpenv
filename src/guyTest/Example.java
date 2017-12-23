package guyTest;

import javax.swing.JFrame;

import tools.html.AbstractHtmlPanel;
import tools.html.HComponent;
import tools.html.HtmlPanel;
import experiment.EventProxy;
import experiment.EventProxy.ProxyListener;

public class Example {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Example exm = new Example();
		exm.go();
	}
	
	public void go() {
		EventProxy cmdProxy = EventProxy.getInstance("sqlCommands");
		cmdProxy.addListener(new Listener());
		HtmlPanel sqlCommand = HtmlPanel.create( new String[] {"-leftRight"}, null, cmdProxy);
		String NL = ""; //"\n";
		sqlCommand.setText(
				"<body>" +
				"   Query for [ <input type='text' fontName='JACKIE' id='display' value='0' cols='20'/>]<BR/>" + NL +
				"	<input type='button' fontName='JACKIE' id='power' value='ON'/>" +
				"	<input type='button' id='CE' value='CE'/> <br/>" + NL + 
				"   <input type='button' fontName='JACKIE' id='digit.seven' value='7'/>" + NL + 
				"	<input type='button' fontName='JACKIE' id='digit.eight' value='8'/>" + NL + 
				"	<input type='button' fontName='JACKIE' id='digit.nine' value='9'/>" + NL +
				"   <input type='button' fontName='JACKIE' id='funct.add' value='+'/><br/>" + NL +
				"   <input type='button' fontName='JACKIE' id='digit.four' value='4'/>" + NL + 
				"	<input type='button' fontName='JACKIE' id='digit.five' value='5'/>" + NL + 
				"	<input type='button' fontName='JACKIE' id='digit.six' value='6'/> " + NL +
				"   <input type='button' fontName='JACKIE' id='funct.subtract' value='-'/><br/>" + NL +
				"   <input type='button' fontName='JACKIE' id='digit.one' value=' 1 '/>" + NL + 
				"	<input type='button' fontName='JACKIE' id='digit.two' value=' 2'/>" + NL + 
				"	<input type='button' fontName='JACKIE' id='digit.three' value='3'/>" + NL +
				"   <input type='button' fontName='JACKIE' id='funct.multiply' value='*'/><br/>      " + NL +
				"   <input type='button' fontName='JACKIE' id='digit.zero' value='        0         '/>          " + NL+
				"   <input type='button' fontName='JACKIE' id='funct.divide' value='/'/><br/>" + NL +
				"   <input type='button' id='equals' value='         =         '/><br/>" + NL +
				"</body>"
		);
		JFrame frame = new JFrame();
		frame.getContentPane().add(sqlCommand);
		frame.setSize(600,800);
		frame.setVisible(true);
	}
	
	
	private class Listener implements ProxyListener {
		String mFunct = null;
		String mPreFunctionValue = null;
		boolean mCheckFunct = false;
		boolean mCheckEquals = false;

		public boolean onEvent(String pName, String pValue, AbstractHtmlPanel pPanel) {

			if (pName.startsWith("digit.")) {
				if (mCheckFunct || mCheckEquals) {
					newDigit(pValue, pPanel);
					mCheckFunct = false;
				} else {
					appendDigit(pValue, pPanel);
				}
			} else if (pName.startsWith("funct.")) {
				HComponent display = pPanel.getComponent("display");
				mPreFunctionValue = display.getText(); // remembering the previous display value
				mFunct = pValue.trim();
				mCheckFunct = true;
			} else if (pName.trim().equals("equals")) {
				HComponent display = pPanel.getComponent("display");
				String current = display.getText();
				calcEquals(mFunct, mPreFunctionValue, current, pPanel);
				mCheckEquals = true;
			} 
			return false;		
		}
		
		public void newDigit (String pValue, AbstractHtmlPanel pPanel) {
			HComponent display = pPanel.getComponent("display");
			String current = pValue.trim();
			long current_i = Long.parseLong(current);
			current = Long.toString(current_i);
			display.setText(current);
		}
		public void appendDigit (String pValue, AbstractHtmlPanel pPanel) {
			HComponent display = pPanel.getComponent("display");
			String current = display.getText();
			current += pValue.trim();
			long current_i = Long.parseLong(current);
			current = Long.toString(current_i);
			display.setText(current);
		}
		public void calcEquals (String pFunct, String pValue1, String pValue2, AbstractHtmlPanel pPanel) {
			HComponent display = pPanel.getComponent("display");
			Long value1_i = Long.parseLong(pValue1);
			Long value2_i = Long.parseLong(pValue2);
			Long result = null;
			if (pFunct.equals("+")) {
				result = value1_i + value2_i;
			} else if (pFunct.equals("-")) {
				result = value1_i - value2_i;
			}else if (pFunct.equals("*")) {
				result = value1_i * value2_i;
			}else if (pFunct.equals("/")) {
				result = value1_i / value2_i;
			}
			display.setText(Long.toString(result));
		}
	}
}
//				if (pName.startsWith("digit.")) {
//					String current = display.getText();
//					current += pValue.trim();
//					long current_i = Long.parseLong(current);
//					current = Long.toString(current_i);
//					display.setText(current);
//				} else if (pName.equals("display")) {
//					String current = display.getText();
//					long current_i = Long.parseLong(current);
//					current = Long.toString(current_i);
//					display.setText(current);
//				}