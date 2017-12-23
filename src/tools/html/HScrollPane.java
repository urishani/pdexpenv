/*
 * Created on Oct 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.html;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HScrollPane extends JScrollPane implements HComponent {
	HComponent mContent = null;
	public HScrollPane(HComponent f) { 
		super((JComponent)f);
		mContent = f;
	}
	public HScrollPane() {	super();}
	public HScrollPane(Container f) { super(f);	}
	public void setEnabled(boolean enable) { super.setEnabled(enable);}
	public void setVisible(boolean enable) { super.setVisible(enable);}
	public void setText(String text) {
//		JTextComponent tc = (JTextComponent) super.getComponent(1);
		mContent.setText(text);
		super.getViewport().setViewPosition(new Point(0,0));
	}
	public String getText() { 
		return mContent.getText();
//		JTextComponent tc = (JTextComponent) super.getComponent(0);
//		return tc.getText();
	}
	Class c;

	public Dimension getSize() { return super.getSize();}
	public void setAlignmentX(float alignment) { super.setAlignmentX(alignment);}
}