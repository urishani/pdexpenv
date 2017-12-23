/*
 * Created on Apr 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.html;

import experiment.EventProxy;
import experiment.MyProperties;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HtmlPanel extends AbstractHtmlPanel{

	private static String gPropertiesRoot = null;
	private static MyProperties gProperties = null;

	private String mIncludeRoot= ""; //File.separator;
	
	private Stack mStack = new Stack();

	public static void main(String[] argv) throws Exception {
		JFrame frame = new JFrame();
		
		HtmlPanel interpreter = HtmlPanel.create(argv);
		
		frame.getContentPane().add(interpreter);
		Document document = interpreter.parse(); 
		if (document != null)
		{
			//System.out.println("Successfully parsed");
			if (interpreter.interprete(document))
				;//System.out.println("Successfully interpreted");
			else
				System.err.println("Interpretation failed");
		} else
			System.err.println("Parse failed.");
		interpreter.prepare();
		frame.setSize(400,600);
		frame.setVisible(true);
	}

	/**
	 * @param argv
	 * @return
	 */
	public static HtmlPanel create(String[] argv) {
		return new HtmlPanel(argv);
	}

	/**
	 * @param pFileName name of XML file containing "html" desctiption of panel. 
	 * @return true for success, false otherwise.
	 */
	public boolean setFile(String pFileName)
	{
		clear();
		FileReader fr = null;
		try {
			fr = new FileReader(new File(pFileName));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot read file '"+ pFileName+"': " + e.getMessage());
		}
		Document document = parse(fr); 
		if (document != null)
		{
			//System.out.println("Successfully parsed");
			if (interprete(document))
				;//System.out.println("Successfully interpreted");
			else
			{
				System.err.println("Interpretation failed");
				return false;
			}
		} else
		{
			System.err.println("Parse failed for input file '" + pFileName + "'");
			return false;
		}
		prepare();
		return true;
	}


	public boolean setText(String string)
	{
		clear();
		Document document = parse(string); 
		if (document != null)
		{
			//System.out.println("Successfully parsed");
			if (interprete(document))
				;//System.out.println("Successfully interpreted");
			else
			{
				System.err.println("Interpretation failed");
				return false;
			}
		} else
		{
			System.err.println("Parse failed. Text: \n" + string + "\n======================");
			return false;
		}
		prepare();
		return true;
	}
	
	protected boolean interprete(Document document)
	{
		return interprete(document, new Stack(), mControls);
	}
	/**
	 * @param document
	 * @return
	 */
	protected boolean interprete(Document document, Stack pStack, List pControls) {
		pStack.push(document.getRootElement());
		return interprete(pStack, pControls);
	}
	
	protected boolean interprete (Stack pStack, List pControls)
	{
		Stack stack = pStack;
		Stack stackOfStacks = new Stack();
		stackOfStacks.push(stack);
//		ArrayList controls = new ArrayList();
		boolean first = true;
		while (!stackOfStacks.empty())
		{
			stack = (Stack)stackOfStacks.pop();
			while (!stack.empty())
			{
				Object s = stack.pop();
				
				if (s instanceof Element) {
					if (mDebug)
						System.out.println("AT: " + ((Element)s).getName());
				} else if (s instanceof Text) 
				{
					if (mDebug)
						System.out.println(
							"Text: '" + ((Text) s).getText() + "'");
					processText(((Text)s).getText(), stack, pControls);	
					continue;
				} else {
					System.out.println(
						"Ignoring element of class '" + s.getClass() + "'");
					continue;
				}

				Element e = (Element)s;
				List atts = e.getAttributes();
				List contents = e.getContent();
				boolean noText = false;
				boolean skipContents = false;
				if (e.getName().equalsIgnoreCase("include"))
				{
					String fName = null;
					for (int i = 0; i < atts.size(); i++)
					{
						Attribute a = (Attribute)atts.get(i);
						if (a.getName().equalsIgnoreCase("name"))
							fName = a.getValue();
					}
					if (fName != null)
					{
						Document d = include(fName);
						if (d != null)
							stack.push(d.getRootElement());
					}
					continue;
				} else if (e.getName().equalsIgnoreCase("property"))
				{
					mAttributes.defineProperty(atts);
				} else if (e.getName().equalsIgnoreCase("container"))
				{
					mAttributes.mergePush(atts, null) ; 
					pControls.add(generateContainerControl(atts));
					mAttributes.mergePop() ;
					noText = true;
					stackOfStacks.push(stack);
					stack = new Stack();
				} else if (e.getName().equalsIgnoreCase("grid"))
				{
					mAttributes.mergePush(atts, null) ; 
					pControls.add(generateGridControl(e));
					mAttributes.mergePop() ;
					noText = true;
					skipContents = true;
				} else if (e.getName().equalsIgnoreCase("input"))
				{
					mAttributes.mergePush(atts, null) ; 
					HComponent c = generateInputControl();
					mAttributes.mergePop() ;
					pControls.add(c);
					if (c instanceof JLabel)
						pControls.add(new Integer(CodeWord.NULL));
					noText = true;
				} else if (e.getName().equalsIgnoreCase("br"))
				{
					pControls.add(new Integer(CodeWord.NEW_LINE));
					//System.out.println("NEW_LINE");
					noText = true;
				} else if (e.getName().equalsIgnoreCase("row"))
				{
					pControls.add(new Integer(CodeWord.NEW_ROW));
					//System.out.println("NEW_LINE");
					noText = true;
				} else if (e.getName().equalsIgnoreCase("col"))
				{
					pControls.add(new Integer(CodeWord.NEW_COL));
					//System.out.println("NEW_LINE");
					noText = true;
				} else if (e.getName().equalsIgnoreCase("lt"))
				{
					appendText("<", pControls);
					noText = true;
				} else if (e.getName().equalsIgnoreCase("menu"))
				{
					JMenuBar mb = new JMenuBar();
					mb.add(interpretMenu(e));
					mb.setEnabled(true);
					pControls.add(mb);
					noText = true;
					skipContents = true;
				} else if (e.getName().equalsIgnoreCase("radio"))
				{
					JPanel p = interpretRadio(e);
					p.setEnabled(true);
					pControls.add(p);
					noText = true;
					skipContents = true;
				} else if (e.getName().equalsIgnoreCase("group"))
				{
					JPanel p = interpretGroup(e);
					p.setEnabled(true);
					pControls.add(p);
					noText = true;
					skipContents = true;
				} else
				{
					if (!e.getName().equalsIgnoreCase("body"))
						System.out.println("Ignoring element '"+e.getName()+"'. Processing its contents only");
				}
				if (mDebug) printElement(atts, contents);
				if (skipContents)
					continue;
				for (int i = contents.size()-1; i >=0; i--) 
				{
					Object child = contents.get(i);
					stack.push(child);
				}
			}
			if (!stackOfStacks.isEmpty())
			{
				
			} else 
			   ;//pControls = controls;
		}
		return true;
	}


	/**
	 * @param document
	 * @return a JMenu control of nested menues and menu items
	 */
	private JPanel interpretRadio(Element e) {
		List atts = e.getAttributes();
		JPanel panel = (JPanel)generateRadioControl(atts, JPanel.class);
		ButtonGroup group = new ButtonGroup();
		List contents = e.getContent();
		for (int i = 0; i < contents.size();i++) 
		{
			Object child = contents.get(i);
			if (!(child instanceof Element))
			{
				continue;
			} 
			if (mDebug)
			{
				System.out.println("ATR: " + ((Element)child).getName());
				printElement(((Element)child).getAttributes(), new ArrayList());				
			}
			Element el = (Element)child;
			if (el.getName().equalsIgnoreCase("RadioButton"))
			{
				JRadioButton b = (JRadioButton)generateRadioControl(el.getAttributes(), 
						  JRadioButton.class);
				group.add(b);
				panel.add(b);
			} else
			{
//				System.out.println("Unexpected element in a menu: " + e + ", ignored.");
				continue;
			}
		}
		return panel;
	}


	/**
	 * @param document
	 * @return a JMenu control of nested menues and menu items
	 */
	private JPanel interpretGroup(Element e) {
		List atts = e.getAttributes();
		JPanel panel = (JPanel)generateRadioControl(atts, JPanel.class);
		panel.setBackground(getBackground());
		panel.setForeground(getForeground());
		List contents = e.getContent();
		for (int i = 0; i < contents.size();i++) 
		{
			Object child = contents.get(i);
			if (!(child instanceof Element))
			{
				continue;
			} 
			if (mDebug)
			{
				System.out.println("GRP: " + ((Element)child).getName());
				printElement(((Element)child).getAttributes(), new ArrayList());				
			}
			Element el = (Element)child;
			if (el.getName().equalsIgnoreCase("Input"))
			{
				mAttributes.mergePush(el.getAttributes(), null) ; 
				HComponent c = generateInputControl();
				mAttributes.mergePop() ;
				panel.add((JComponent)c);
			} else
			{
//				System.out.println("Unexpected element in a menu: " + e + ", ignored.");
				continue;
			}
		}
		panel.setMaximumSize(panel.getPreferredSize());
		return panel;
	}


	/**
	 * @param document
	 * @return a JMenu control of nested menues and menu items
	 */
	private JMenu interpretMenu(Element e) {
		List atts = e.getAttributes();
		JMenu menu = (JMenu)generateMenuControl(atts, JMenu.class);
		List contents = e.getContent();
		for (int i = 0; i < contents.size();i++) 
		{
			Object child = contents.get(i);
			if (!(child instanceof Element))
			{
				continue;
			} 
			if (mDebug)
			{
				System.out.println("ATM: " + ((Element)child).getName());
				printElement(((Element)child).getAttributes(), new ArrayList());				
			}
			Element el = (Element)child;
			if (el.getName().equalsIgnoreCase("MenuItem"))
			{
				menu.add((JMenuItem)generateMenuControl(el.getAttributes(), 
						  JMenuItem.class));
			} else if (el.getName().equalsIgnoreCase("Menu"))
			{
				menu.add(interpretMenu(el));
			} else if (el.getName().equalsIgnoreCase("Separator"))
			{
				menu.addSeparator();
			} else
			{
//				System.out.println("Unexpected element in a menu: " + e + ", ignored.");
				continue;
			}
		}
		return menu;
	}


	/**
	 * @param string
	 * @param stack
	 */
	private void processText(String string, Stack stack, List pControls) 
	{
		StringTokenizer st = new StringTokenizer(string, "\n");
		while (st.hasMoreTokens())
		{
			String l = st.nextToken();
			if (l.length()==0) continue;
			appendText(l, pControls);
			if (mDebug)
				System.out.println("Appending text: '"+l+"'");
			if (st.hasMoreTokens())
			{
				pControls.add(new Integer(CodeWord.NEW_LINE)); 
				if (mDebug)
					System.out.println("NEW_LINE");
			}
		}
	}


	/**
	 * @param atts list of <cpde>Attribute</code>s
	 * @param contents list of sub <code>Element</code>s.
	 */
	private void printElement(List atts, List contents) 
	{
		for (int i = 0; i < atts.size(); i++) {
			Attribute a = (Attribute) atts.get(i);
			System.out.println(
				i + " - " + a.getName() + " : " + a.getValue());
		}

		for (int i = 0; i < contents.size(); i++) 
		{
			Object child = contents.get(i);
			if (child instanceof Element) {
				System.out.println(
					"C" + i + " - " + ((Element) child).getName());
			} else if (child instanceof Text) {
				System.out.println(
					"Text: '" + ((Text) child).getText() + "'");
			} else {
				System.out.println(
					"child of class '" + child.getClass() + "'");
			}
		}
	}


    /**
	 * @param atts list of XML <input> attributes and values.
	 * @param pClass class to generate for these attributes - Menu or MenuItem
	 * @return
	 */
	private JComponent generateMenuControl(List atts, Class pClass) 
	{
		String id = "";
		String value = "";
		boolean disabled = false;
		for (int i = 0; i < atts.size(); i++)
		{
			Attribute a = (Attribute)atts.get(i);
			if (a.getName().equalsIgnoreCase("id"))
			{
				id = a.getValue();
			} else if (a.getName().equalsIgnoreCase("disabled"))
			{
				disabled = true;
			} else if (a.getName().equalsIgnoreCase("value"))
			{
				value = a.getValue();
			} else
			{
				System.out.println("Ignored input attribute '"+ a.getName()+ "'");
			}
		}
		JMenuItem f =  null;
		value = value != null? value:"      ";
		if (pClass == JMenu.class)
			f = new JMenu(value);
		else
			f = new JMenuItem(value);
			
		f.setEnabled(!disabled);
		f.addActionListener( 
			new MyActionEventListener(id));
		Dimension m = f.getPreferredSize();
		m.width+= 10;// this is a cludge to set sizes right on output.
		f.setMaximumSize(m);
		f.setForeground(getForeground());
		mDir.put(id, f);
		return f;
	}


	/**
	 * @param atts list of XML <input> attributes and values.
	 * @return
	 */
	private JComponent generateRadioControl(List atts, Class pClass) 
	{
		String id = "";
		String value = "";
		boolean disabled = false;
		boolean vertical = true;
		for (int i = 0; i < atts.size(); i++)
		{
			Attribute a = (Attribute)atts.get(i);
			if (a.getName().equalsIgnoreCase("id"))
			{
				id = a.getValue();
			} else if (a.getName().equalsIgnoreCase("disabled"))
			{
				disabled = true;
			} else if (a.getName().equalsIgnoreCase("orientation"))
			{
				String ornt = a.getValue();
				if (ornt.equalsIgnoreCase("vertical"))
					vertical = true;
				else if (ornt.equalsIgnoreCase("horizontal"))
					vertical = false;
				else System.err.println("Illegal orientation value: '" + ornt + "'");
			} else if (a.getName().equalsIgnoreCase("value"))
			{
				value = a.getValue();
			} else
			{
				System.out.println("Ignored input attribute '"+ a.getName()+ "'");
			}
		}
		JComponent f =  null;
		value = value != null? value:"      ";
		if (pClass == JPanel.class)
			f = new JPanel((vertical?(new GridLayout(0,1)):new GridLayout(1,0)));
		else
			f = new JRadioButton(value);
			
		f.setEnabled(!disabled);
		f.setForeground(getForeground());
		if (pClass == JRadioButton.class)
			((JRadioButton)f).addActionListener( 
				new MyActionEventListener(id));
		mDir.put(id, f);
		return f;
	}


	/**
	 * @param atts list of XML <input> attributes and values.
	 * @return
	 */
	private HComponent generateInputControl() {
		String value = mAttributes.getValue("value");
		String id = mAttributes.getValue("id");
		HComponent f =  null;
		value = value != null? value:"      ";
		if (mAttributes.hasValue("timer")) // isTimer)
		{
			StringTokenizer st = new StringTokenizer(mAttributes.getValue("timer"), ",") ; //a.getValue(), ",");
			try {
				int timerSeconds = 0;
				int timerInterval = 1;
				if (st.countTokens()>0) timerSeconds = Integer.parseInt(st.nextToken());
				if (st.countTokens()>0) timerInterval = Integer.parseInt(st.nextToken());	
				Timer t = this.new Timer(timerSeconds, timerInterval, mProxy, id, this);
				t.start();
			} catch (NumberFormatException nfe) { 
				System.err.println("Illegal timer params: '" + mAttributes.getValue("timer") + "'");
			}
		}
		int rows = mAttributes.getIntValue("rows");
		int cols = mAttributes.getIntValue("cols");
		int border = mAttributes.getIntValue("border");
		int borderColor = mAttributes.getHexValue("borderColor");
		boolean isButton = false;
		if (mAttributes.isSet("type","button") || mAttributes.isSet("type","submit")) //isButton)
		{
			isButton = true;
			HButton b = new HButton(value);
			b.addActionListener( 
				new MyActionEventListener(id));
			f = b;
		}
		else if (mAttributes.isSet("type", "checkbox")) 
		{
			HCheckBox cb = new HCheckBox(value);
			if (mAttributes.isSet("checked") || mAttributes.isSet("selected"))
				cb.setSelected(true);
			f = cb;
		}
		else if (mAttributes.isSet("type", "label")) // isLabel)
		{
			HLabel l = new HLabel(value);
//			HTextField l = new HTextField(value);
//			l.setColumns(cols);
			f = l;
		} else
		{
			if (rows <= 1)
			{
				HTextField tf = new HTextField(value);
				tf.addKeyListener(
					new MyTextEventListener(id));
				tf.setColumns(cols);
				f = tf;
			}
			else
			{
				HTextArea ta = new HTextArea(value);
				ta.addKeyListener(
					new MyTextEventListener(id));
				ta.setColumns(cols);
				ta.setRows(rows);
				f = ta;
			}
		}			
		f.setAlignmentX(mAlignment);
		if (mAttributes.isSet("disabled")) //disabled) 
				f.setEnabled(false);
		if (mAttributes.isSet("hidden"))
				f.setVisible(false);
		Font font = getFont();
		if (font == null) font = f.getFont();
		int fsize = font.getSize();
		if (mAttributes.hasValue("fontSize")) // fontSize != null)
			fsize = mAttributes.getIntValue("fontSize"); // Integer.parseInt(fontSize.trim());
		int style = font.getStyle();
		if (mAttributes.isSet("bold")) //bold != null)
				style |= Font.BOLD;
			else 
				style &= ~Font.BOLD;
		if (mAttributes.isSet("italic")) //italic != null)
				style |= Font.ITALIC;
			else
				style &= ~Font.ITALIC;
		String fn = font.getName();
		if (mAttributes.hasValue("fontName"))
			font = new Font(mAttributes.getValue("fontName"), style, fsize);
		else 
			font = new Font(font.getName(), style, fsize);
		f.setFont(font);
			
		Color color = getForeground();
		Color bg = getBackground();
		if (mAttributes.hasValue("bg"))
			bg = new Color(mAttributes.getHexValue("bg"));
		f.setBackground( bg ); //new Color(Integer.parseInt(bg.trim(), 16)));
		if (mAttributes.hasValue("color"))
			color = new Color(mAttributes.getHexValue("color"));
		f.setForeground( color ) ; //new Color(Integer.parseInt(color.trim(), 16)));
		if (mAttributes.isSet("scroll")) 
		{
			mDir.put(id + ".content", f);
			f = new HScrollPane(f);
		}
		if (!isButton)
		{
			if (border > 0)
				f.setBorder(BorderFactory.createLineBorder(new Color(borderColor), border));
			else 
				f.setBorder(BorderFactory.createEmptyBorder());
		}
		f.setMaximumSize(f.getPreferredSize());
		mDir.put(id, f);
		return f;
	}


	/**
	 * @param atts list of XML <input> attributes and values.
	 * @return
	 */
	private Container generateContainerControl(List atts) {
		String id = mAttributes.getValue("id");
		Container f =  new Container();
		if (mAttributes.isSet("scroll")) 
		{
			mDir.put(id + ".content", f);
			f = new HScrollPane(f);
		} 
		mDir.put(id, f);
		return f;
	}

	/**
	 * @param atts list of XML <input> attributes and values.
	 * @return
	 */
	private Container generateGridControl(Element pElement) 
	{
		String id = mAttributes.getValue("id");
		Container f =  new Container();
		if (mAttributes.isSet("scroll")) 
		{
			mDir.put(id + ".content", f);
			f = new HScrollPane(f);
		} 
		mDir.put(id, f);
		List contents = pElement.getContent();
		int cols = mAttributes.getIntValue("cols");
		int rows = mAttributes.getIntValue("rows");
		int colscnt = 0;
		List lControls = new ArrayList();
		Stack lStack = new Stack();
		for (int i = contents.size()-1; i >=0; i--) 
			lStack.push(contents.get(i));
		interprete(lStack, lControls);

		int col = 0, row = 1;
		for (int i = 0; i < lControls.size(); i++)
		{
			Object o = lControls.get(i);
			if (o instanceof Integer)
				switch (((Integer)o).intValue())
				{
					case CodeWord.NEW_ROW:
						{ row++; col = 0; break; }
					case CodeWord.NEW_COL: col++;
				}
			else col++;
			cols = Math.max(cols, col);
		}
		rows = row;
		System.out.println("rows = " + rows + " cols = " + cols);
		f.setLayout(new GridLayout(rows, cols));
		
		// now fill them in
		col = 0; row = 0;
		int pos = 0, lastpos=0;
		for (int i = 0; i < lControls.size(); i++)
		{
			Object o = lControls.get(i);
			if (o instanceof Integer)
			{
				switch (((Integer)o).intValue())
				{
					case CodeWord.NEW_ROW:
						{ row++; col = 0; break; }
					case CodeWord.NEW_COL: col++;
				}
				continue;
			}
			else
			{
				pos = row*cols + col;
	//			System.out.println("Inserting in grid position " + pos );
				for (int p = lastpos; p < pos; p++)
					f.add(new Filler(new Dimension(), new Dimension(), new Dimension()), p);
				f.add(/*new JTextField("Kuku" + pos)); */(JComponent)o);//, pos);
				lastpos = pos;
				col++;
			}
		}

		f.invalidate();
		f.setVisible(true);
		return f;
	}


	private static boolean interpreteDebug(Document document) {
		System.out.println(document.toString());
		Stack stack = new Stack();
		stack.push(document.getRootElement());
		while (!stack.empty())
		{
			Element s = (Element)stack.pop();
			System.out.println("AT: " + s.getName());
			//System.out.println("----" + s.getText());
			//System.out.println("Contents: '" + s.getContent());

			List atts = s.getAttributes();
			for (int i=0; i < atts.size(); i++)
			{
				Attribute a = (Attribute) atts.get(i);
				System.out.println(i+" - " + a.getName() + " : "+a.getValue());
			}
			List contents = s.getContent();
			for (int i=0 ; i < contents.size() ; i++)
			{
				Object child = contents.get(i);
				if (child instanceof Element)
				{
					System.out.println("C" + i + " - " + ((Element)child).getName());
					stack.push(child);
				} else if (child instanceof Text)
				{
					System.out.println("Text: '" + ((Text)child).getText() + "'");	
				} else 
				{
					System.out.println("child of class '" + child.getClass() + "'");
				}
			}
		}
		return true;
	}


	protected Document parse()
	{
		return parse(new File(mInFile));
	}

	/**
	 * @return
	 */
	private Document parse(File PinF) {
		System.out.println("Reading input file '" + PinF.getPath() + "'");
		if (!PinF.canRead())
		{
			System.out.println("Cannot read input file '" + PinF.getPath() + "'");
			return null;
		}
		InputStreamReader inR = null;
		try {
			inR = new InputStreamReader(new FileInputStream(PinF));
		} catch (FileNotFoundException e) {
			System.out.println("Error: " + e.getMessage());
			return null;
		}
		if (inR != null)
			return parse (inR);
		else
			return null;
	}


	private Document include(String pFileName) {
		String fname = pFileName;
		if (mIncludeRoot.length() > 0) 
			fname = mIncludeRoot + File.separator + fname;
		System.out.println("Including file '" + fname + "'");
		File f = new File(fname);
		if (!f.canRead())
		{
			System.out.println("Cannot read include file '" + f.getPath() + "'");
			return null;
		}
		InputStreamReader inR = null;
		try {
			inR = new InputStreamReader(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			System.out.println("Error: " + e.getMessage());
			return null;
		}
		if (inR != null)
			return parse (inR);
		else
			return null;
	}



	/**
	 * @param inR
	 */
	private Document parse (Reader pInput) {
		Document document = null;
		SAXBuilder builder = new SAXBuilder();
		try {
			document = builder.build(pInput);
		} catch (JDOMException e) {
			System.out.println( e.getMessage());
			return null;
		}
		return document;
	}


	/**
	 * @param inString
	 */
	private Document parse(String pInputString) {
		StringReader reader = new StringReader(pInputString);
		return parse (reader);
	}



	/**
	 * @param string
	 */
	protected void appendText(String string, List pControls) {
		if (pControls.size() > 0) 
		{
			Object o = pControls.get(pControls.size()-1);
			if (o != null && o instanceof HLabel)
			{
				HLabel l = (HLabel)o;
				l.setText(l.getText() + string);
				return;
			}
		}
		HLabel l = new HLabel(string);
		l.setForeground(getForeground());
		l.setBackground(getBackground());
		l.setVisible(true);
		pControls.add(l);
	}


	public void prepare()
	{
		prepare(this, mControls);
//		show(true);
//		doLayout();
	}

	private void prepare(Box pPanel, ArrayList pControls)
	{
		int lines = 0;
		Box cLine = null;
		pPanel.setAlignmentX(mAlignment);
		if (mMargins[0] != 0)
			pPanel.add(Box.createRigidArea(new Dimension(1, mMargins[0])));
		for (int i = 0; i < pControls.size(); i++)
		{
			Object o = pControls.get(i);
			if (o == null) continue; // may be separator of input labels and text labels.
			if (mDebug)
				System.out.println(o.getClass() + " - " + o.toString());
			if (o instanceof Container)
			{   
				if (o instanceof JComponent) 
				{
					((JComponent)o).setOpaque(true);
					if (mTrace) ((JComponent)o).setBorder(BorderFactory.createLineBorder(Color.BLUE));
				}
				Container jc = (Container)o;
//				jc.setBackground(new Color(Integer.parseInt(mProperties.getProperty("Project.Window.Background"), 16)));
//				pPanel.setBackground(Color.GREEN);
//				jc.setVisible(true);
				if (cLine == null) {
					cLine = new Box(BoxLayout.X_AXIS);
					if (mTrace) cLine.setBorder(BorderFactory.createLineBorder(Color.RED));
//					JLabel l = new JLabel("$");
//					cLine.setBackground(Color.BLUE);
//					l.setOpaque(true);
//					cLine.add(l);
					if (mMargins[1] > 0)
						pPanel.add(Box.createRigidArea(new Dimension(mMargins[1],1)));

					//cLine.add(Box.createHorizontalStrut(10));
					lines ++;
					//cLine.setLayout(new BoxLayout(cLine,BoxLayout.X_AXIS));
					cLine.setAlignmentX(mAlignment);
					cLine.setVisible(true);
				} 
				if (mLeftJustify) // != Component.LEFT_ALIGNMENT)
					cLine.add(jc);
				else
					cLine.add(jc, 0);				
				//cLine.add(Box.createHorizontalGlue());
			} else if (o instanceof ArrayList)
			{
				if (i >= pControls.size())
				{
					System.err.println("Improper contents of pControls");
					return;
				}
				Object nextO = pControls.get(i+1);
				if (nextO instanceof JScrollPane)
				{
					AbstractHtmlPanel p = null;
					try {
						p = (AbstractHtmlPanel) (this.getClass().newInstance());
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					if (p != null)
					{
						//new HtmlPanel(new String[]{},null, mProxy);
						p.setProxy(mProxy);
						prepare(p, (ArrayList)o);
							((JScrollPane)nextO).add(p);
					}
				} else if (nextO instanceof HtmlPanel)
				{
					prepare((Box)nextO, (ArrayList)o);
				} else
				{
					System.err.println("Improper pControls component [" + (i+1) + "]. class= '" + nextO.getClass() + "'");
				}
			} else if (o instanceof Integer)
			{
				int code =((Integer)o).intValue(); 
				if ( code == CodeWord.NEW_LINE)
				{
					if (mMargins[3] > 0)
						cLine.add(Box.createRigidArea(new Dimension(mMargins[3],1)));
					if (cLine != null)
						pPanel.add(cLine /*, BorderLayout.WEST*/);
					cLine = null;
				} else if (code != CodeWord.NULL)
				{
					System.err.println("Illegal Code in pControls: " + o.toString());
				}
			}
		}
		if (cLine != null)
		{
			if (mMargins[2] > 0)
				cLine.add(Box.createRigidArea(new Dimension(mMargins[2],1)));
//			cLine.setMaximumSize(cLine.getPreferredSize());
			pPanel.add(cLine/*, BorderLayout.WEST*/);
		}
		if (mMargins[3] > 0)
			pPanel.add(Box.createRigidArea(new Dimension(1,mMargins[3])));
		//add(Box.createVerticalGlue());
		if (mDebug)
		{
			Properties p = getTextFields();
			Enumeration l = p.keys();
			int i = 0;
			while (l.hasMoreElements())
			{
				String id = l.nextElement().toString();
				System.out.println("i: " + id + " ["+ 
					getComponent(id).getSize() + "]");
			}
		}
		// setPreferredSize(new Dimension(100,500));
	}

	private HtmlPanel()	{ super(); }
	private HtmlPanel( String[] args){ super(args); }

	private HtmlPanel( String[] args, String pSubject, EventProxy pProxy)
	{
		super(args, pSubject, pProxy);
	}
	/* (non-Javadoc)
	 * @see tools.AbstractHtmlPanel#clear()
	 */
	protected void clear() 
	{
		super.clear();
		mStack = new Stack();
	}

	/**
	 * @param mProperties
	 * @param string
	 */
	public static void setTraceFromProperties(MyProperties pProperties, String pRoot) {
		gProperties = pProperties;
		gPropertiesRoot = pRoot;
	}

	/**
	 * @param strings
	 * @param object
	 * @param mProxy
	 * @return
	 */
	public static HtmlPanel create(String[] strings, String object, EventProxy mProxy) {
		return new HtmlPanel(strings, object, mProxy);
	}

	/**
	 * @return
	 */
	public static HtmlPanel create() {
		return new HtmlPanel();
	}

}

class CodeWord {
	private int mType = NULL;
	private String mVal = "";
	public CodeWord(int pType, String pVal)
	{
		mType = pType;
		mVal = pVal;
	}

	public static final int NULL = 0;
	public static final int TEXT = 1;
	public static final int START_INPUT = 2;
	public static final int END_INPUT= 3;
	public static final int PARAM_NAME = 4;
	public static final int PARAM_VAL = 5;
	public static final int NEW_LINE = 6;
	public static final int INPUT = 7;
	public static final int SCROLL_PANE = 8;
	public static final int CONTAINER = 9;
	public static final int NEW_ROW = 10;
	public static final int NEW_COL = 11;

	/**
	 * @return type of code word.
	 */
	public int getType() {
		return mType;
	}

	/**
	 * @param string  appends to value of code word.
	 */
	public void append(String string) {
		mVal += string;			
	}

	/**
	 * @return value of code word.
	 */
	public String getVal() {
		return mVal;
	}

	/**
	 * @param string   sets value of code word.
	 */
	public void setVal(String string) {
		mVal = string;
	}

}
	


