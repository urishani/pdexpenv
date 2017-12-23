/*
 * Created on 07/12/2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import tools.html.HtmlPanel;
import experiment.EventProxy;
import experiment.MyProperties;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AbstractWindowsFactory {
	protected final EventProxy mProxy;
	protected final MyProperties mProperties;

	public AbstractWindowsFactory(MyProperties pProperties, EventProxy pProxy)
	{
		mProperties = pProperties;
		mProxy = pProxy;

	}
	/**
	 * @param string
	 * @return A component which represents a window header
	 */
	protected HeaderComponent createHeader(String string, String pButtons) {
		HeaderComponent label = new HeaderComponent(this, new BorderLayout());
//		Box label = new Box(BoxLayout.X_AXIS);
		label.setLayout(new BorderLayout());
		
		label.setBackground( new Color(Integer.parseInt(mProperties.getProperty("Project.Window.Header.Background"), 16)));
		JLabel tl = new JLabel(string);
		tl.setForeground( new Color(Integer.parseInt(mProperties.getProperty("Project.Window.Header.Foreground"), 16)));
//		tl.setOpaque(true);
		tl.setMaximumSize(tl.getPreferredSize());
//		System.err.println(":::::::" + tl.getMaximumSize());
		tl.setHorizontalAlignment(JLabel.LEFT);
		Component th = null;
		if (pButtons == null) 
		{
			JButton th_b = new JButton("About this window");
			th_b.setHorizontalAlignment(JButton.RIGHT);
			th_b.addActionListener(new MyActionEventListener("Help." + string));
			th_b.setMaximumSize(th_b.getPreferredSize());
			th = th_b;
		} else
		{
			HtmlPanel th_p = HtmlPanel.create(mProperties.getArgsPropery("HtmlPanel.Args"), null, mProxy); //new String[]{"-trace","-debug"});
			String bg = mProperties.getProperty("Project.Window.Header.Buttons.Background");
			if (bg != null && !bg.equals(""))
				th_p.setBackground(new Color(Integer.parseInt(bg, 16)));
			String fg = mProperties.getProperty("Project.Window.Header.Buttons.Foreground");
			if (fg != null && !fg.equals(""))
				th_p.setForeground(new Color(Integer.parseInt(fg, 16)));
			th_p.setText(pButtons);
			th = th_p;
			label.setHtmlPanel(th_p);
		}
		label.add(tl, BorderLayout.WEST);
		label.add(th, BorderLayout.EAST);
		Dimension dim = label.getPreferredSize();
		dim.width = 1000;
		label.setMaximumSize(dim); //label.getPreferredSize());
		return label;
	}

	protected WindowContainer createWindowContainer(String pName, Component pWindow)
	{
		WindowContainer tmp = new WindowContainer(BoxLayout.Y_AXIS);
// let it inherit colors from its parent.
//		tmp.setBackground(Color.CYAN); tmp.setOpaque(true);
		tmp.setAlignmentX(Component.LEFT_ALIGNMENT);
		HeaderComponent hdr = createHeader(mProperties.getProperty(pName + ".Title"), 
					mProperties.getProperty(pName + ".Title.Buttons"));
		tmp.add(hdr);
		Box tmpB = new Box(BoxLayout.X_AXIS);
		tmpB.add(pWindow);
		tmp.add(tmpB);
		tmp.setHtmlPanel(hdr.getHtmlPanel());
		return tmp;
	}

	public class MyActionEventListener implements ActionListener
	{
		private String mName;
		public MyActionEventListener(String pName)
		{ 	
			mName = pName; 
		}

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) 
		{
			mProxy.delegate(mName, ((JButton)arg0.getSource()).getText(), null);
		}
	}
	public class MyKeyListener implements KeyListener
	{
		private String mName;
		public MyKeyListener(String pName)
		{ 	
			mName = pName; 
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		public void keyPressed(KeyEvent arg0) {
//			mProxy.delegate(mName, arg0.toString(), null);
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		public void keyReleased(KeyEvent arg0) {
//			TextArea src = (TextArea) arg0.getSource();
			switch (arg0.getKeyCode())
			{
				case KeyEvent.VK_HOME:
					mProxy.delegate(mName,  "HOME", null);
					break;
				case KeyEvent.VK_END:
					mProxy.delegate(mName,  "END", null);
					break;
				case KeyEvent.VK_LEFT:
					mProxy.delegate(mName,  "<-", null);
					break;
				case KeyEvent.VK_RIGHT:
					mProxy.delegate(mName,  "->", null);
					break;
				case KeyEvent.VK_UP:
					mProxy.delegate(mName,  "^", null);
					break;
				case KeyEvent.VK_DOWN:
					mProxy.delegate(mName,  "|", null);
					break;
			}
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		public void keyTyped(KeyEvent arg0) {
//			mProxy.delegate(mName, arg0.toString(), null);
		}
	}
}

