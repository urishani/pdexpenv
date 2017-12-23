/*
 * Created on 07/04/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.windows;

import javax.swing.Box;

import tools.html.*;

/**
 * @author gshani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class WindowContainer extends Box {
	public WindowContainer(int pLayout)
	{
		super(pLayout);
	}
	private AbstractHtmlPanel mPanel = null;
	public void setHtmlPanel(AbstractHtmlPanel pPanel) { mPanel = pPanel; }
	public AbstractHtmlPanel getHtmlPanel() { return mPanel; }
}
