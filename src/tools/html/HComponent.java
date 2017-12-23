/*
 * Created on Oct 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.html;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.border.Border;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface HComponent {
	public void setEnabled(boolean enable);
	public void setVisible(boolean visible);
	public void setText(String text);
	public String getText();
	public Dimension getSize();
	public void setAlignmentX(float alignment);
	public Font getFont();
	public void setFont(Font font);
	public void setBackground(Color bg);
	public void setForeground(Color color);
	public void setBorder(Border border);
	public Dimension getPreferredSize();
	public void setMaximumSize(Dimension preferredSize);
}
