/*
 * Created on Nov 1, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.html;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

import org.jdom.Attribute;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class HAttributes {
	/**
	 * mProps is the set of attributes stored in this object
	 */
	private Properties mProps= new Properties();  
	/**
	 * mStack is used to manage a push/pop methods of such attributes.
	 */
	private Stack mStack = new Stack();
	/**
	 * mPropertySets stores association of names with Properties which are defined in the input data, and
	 * can be applied at certain contexts for convenience.
	 */
	private Hashtable mPropertySets = new Hashtable();
	
	/**
	 * Initializes with attributes list of an XML item.
	 * @param pAtts attributes to initialize the Properties of this object.
	 */
	public HAttributes(Properties pAtts, Hashtable pLegals, Hashtable pIllegals)
	{
		if (pAtts == null)
			pAtts = new Properties();
		else 
			pAtts = new Properties();
		mProps = pAtts;
	}
	/**
	 * Initializes with attributes list of an XML item.
	 * @param pAtts attributes to initialize the Properties of this object.
	 */
	public HAttributes(List pAtts, Hashtable pLegals, Hashtable pIllegals)
	{
		mProps = new Properties();
		if (pAtts != null)
			merge(pAtts, pLegals, pIllegals);
	}

	/**
	 * Parses the attributes and merges them into the Properties of this object.
	 * @param pAtts new attributes to merge with current attributes.
	 * @param pLegals a Hashtabel of legal attributes to be considered. 
	 */
	private void merge(List pAtts, Hashtable pLegals, Hashtable pIllegals)
	{
		for (int i = 0; i < pAtts.size(); i++)
		{
			Attribute a = (Attribute)pAtts.get(i);
			String name = a.getName().trim().toUpperCase();
			if (name.equalsIgnoreCase("property"))
			{
				String pName = a.getValue().trim().toUpperCase();
				List prop = (List) mPropertySets.get(pName);
				if (prop == null)
				{
					new Exception("No definition for property '" + pName + "'").printStackTrace();
					continue;
				}
				merge(prop, pLegals, pIllegals);
				continue;
			}
			if (!legal(name, pLegals, pIllegals))
			{
				System.out.println("Ignored input attribute '"+ a.getName()+ "'");
				continue;
			}
			mProps.setProperty(name, a.getValue());
		}

	}
	
	public void defineProperty(List pAtts)
	{
		String id = null;
		for (int i=0; i < pAtts.size(); i++)
		{
			Attribute a = (Attribute)pAtts.get(i);
			if (a.getName().equalsIgnoreCase("ID"))
			{
				id = a.getValue();
				pAtts.remove(i);
			}
		}
		if (id != null)
			mPropertySets.put(id.trim().toUpperCase(), pAtts);
		else 
			System.err.println("Property has no id - ignored");
	}

	/**
	 * Parses the attributes and merges them into the Properties of this object,
	 * but returns the old Properties which hae been altered by this merge. That can be used in the
	 * mergePop method to reverse the action.
	 * @param pAtts new attributes to merge with current attributes.
	 * @param pLegals a HAshtabel of legal attributes to be considered. 
	 */
	public void mergePush(List pAtts, Hashtable pLegals)
	{
		mStack.push(mProps); // keep reference to old present properties for pop later on.
		mProps = new Properties(mProps); // use previous properties as default for new one.
		merge(pAtts, pLegals, null);
	}

	/**
	 * reverses the action of mergePush when using Properties off the mStack member
	 * @param pAtts
	 */
	public void mergePop()
	{
		Properties old = (Properties) mStack.pop();
		if (old == null)
		{
			new Exception("mergePop pops an empty stack").printStackTrace();
			return;
		}
		mProps = old;
	}
	
	// access methods:
	/**
	 * Checks that a certain attribute is set to either not "off", or just exists
	 * @param pAtt attribute to be checked.
	 * @return true if attribute exists and either has no value, or its
	 *   value is not "off".
	 */
	public boolean isSet(String pAtt, String pVal)
	{
		String val = mProps.getProperty(pAtt.trim().toUpperCase());
		if (val == null) return false;
		return val.equalsIgnoreCase(pVal);
	}
	/**
	 * Checks that a certain attribute is set to either not "off", or just exists
	 * @param pAtt attribute to be checked.
	 * @return true if attribute exists and either has no value, or its
	 *   value is not "off".
	 */
	public boolean isSet(String pAtt)
	{
		String val = mProps.getProperty(pAtt.trim().toUpperCase());
		if (val == null) return false;
		return !(val.equalsIgnoreCase("off")||val.equalsIgnoreCase("0"));
	}
	/**
	 * Checks that a certain attribute is included and has some value for further processing.
	 * @param pAtt attribute to be checekd.
	 * @return true if the attribute exists, and has some value that is not empty.
	 */
	public boolean hasValue(String pAtt)
	{
		String val = mProps.getProperty(pAtt.trim().toUpperCase());
		if (val == null) return false;
		return val.trim().length() > 0;
	}
	/**
	 * Gets in int value of an attribute. It is assumed the attribute has been checked earlier
	 *   with one of the checking methods. 
	 * @param pAtt attribute whose value is sought.
	 * @return 0 if attribute is missing or has no value, or value is illegal int. Actual int value
	 *   otherwise.
	 */
	public int getIntValue(String pAtt)
	{
		String val = mProps.getProperty(pAtt.trim().toUpperCase());
		if (val == null) return 0;
		int v = Integer.parseInt(val);
//		System.out.println(pAtt + "= " + v);
		return v;
	}
	/**
	 * Gets in int value of a Hex valued attribute. It is assumed the attribute has been checked earlier
	 *   with one of the checking methods. 
	 * @param pAtt attribute whose value is sought.
	 * @return 0 if attribute is missing or has no value, or value is illegal int. Actual int value
	 *   otherwise.
	 */
	public int getHexValue(String pAtt) {
		String val = mProps.getProperty(pAtt.trim().toUpperCase());
		if (val == null) return 0;
		return Integer.parseInt(val,16);
	}

	/**
	 * Returns the String value of an attribure, assuming it exists.
	 * @param pAtt attribute whose value is sought.
	 * @return an empty String is attribute is missing or has no vlaue, the actual value otherwise.
	 */
	public String getValue(String pAtt)
	{
		String val = mProps.getProperty(pAtt.trim().toUpperCase());
		return (val == null)?"":val;
	}

	/**
	 * Helper method for parsAttributes
	 * @param att An attribute whose legality in a given context is checked.
	 * @param pLegals a context in which the att legality is checked. If null, all is legal. 
	 * 	Keys are compared in uppercase to make this case insensitive. 
	 * @param pIllegals a context in which the att illegality is checked. If null, all is legal
	 * @return true if att is in pLegals, or not in pIllegals as long as they are provided as not nulls.
	 */
	private boolean legal(String att, Hashtable pLegals, Hashtable pIllegals)
    {
    	if (pLegals == null && pIllegals == null) return true; 
		boolean legal = true;
		boolean illegal = false;
    	att.toUpperCase();
    	if (pLegals != null)
    		legal = pLegals.containsKey(att);
    	if (pIllegals != null)
    		illegal = pIllegals.containsKey(att);
		return legal && !illegal;
    }
}
