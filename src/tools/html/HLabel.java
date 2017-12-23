/*
 * Created on Oct 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.html;

import java.awt.Dimension;

import javax.swing.JLabel;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HLabel extends JLabel implements HComponent {
	/**
	 * @param string
	 */
	public HLabel(String string) {
		super(string);
	}
	public void setEnabled(boolean enable) { super.setEnabled(enable);}
	public void setVisible(boolean enable) { super.setVisible(enable);}
	public void setText(String text) { super.setText(text);}
	public String getText() { return super.getText();}
	public Dimension getSize() { return super.getSize();}
	public void setAlignmentX(float alignment) { super.setAlignmentX(alignment);}
}
