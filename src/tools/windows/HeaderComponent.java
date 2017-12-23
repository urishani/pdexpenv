/*
 * Created on May 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.windows;

import java.awt.LayoutManager;

import javax.swing.JPanel;

import tools.html.AbstractHtmlPanel;


public class HeaderComponent extends JPanel {
	private final AbstractWindowsFactory factory;

	private AbstractHtmlPanel mPanel = null;
	public HeaderComponent(AbstractWindowsFactory factory) {
		super();
		this.factory = factory;
	}
	public HeaderComponent(AbstractWindowsFactory factory, boolean arg0) {
		super(arg0);
		this.factory = factory;
	}
	public HeaderComponent(AbstractWindowsFactory factory, LayoutManager arg0) {
		super(arg0);
		this.factory = factory;
	}
	public HeaderComponent(AbstractWindowsFactory factory, LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
		this.factory = factory;
	}

	public void setHtmlPanel(AbstractHtmlPanel pPanel) { mPanel = pPanel; }
	public AbstractHtmlPanel getHtmlPanel() { return mPanel; }
}