/*
 * Created on Oct 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.html;

import java.awt.Dimension;

import javax.swing.JButton;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HButton extends JButton implements HComponent {
	/**
	 * @param value
	 */
	public HButton(String value) {
		super(value);
	}
	public void setEnabled(boolean enable) { super.setEnabled(enable);}
	public void setVisible(boolean enable) { super.setVisible(enable);}
	public void setText(String text) { super.setText(text);}
	public String getText() { return super.getText();}
	public Dimension getSize() { return super.getSize();}
	public void setAlignmentX(float alignment) { super.setAlignmentX(alignment);}
}
