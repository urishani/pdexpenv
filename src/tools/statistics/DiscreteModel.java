/*
 * Created on 10/03/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.statistics;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DiscreteModel implements IModel, Serializable {

	Hashtable mDir = new Hashtable();
	/* (non-Javadoc)
	 * @see tools.statistics.IModel#match(int)
	 */
	public int match(int pSample) {
		return pSample;
	}
	/* (non-Javadoc)
	 * @see tools.statistics.IModel#map(java.lang.Object)
	 */
	public int map(Object pSample) {
		int v = mDir.size();
		Integer iV = (Integer)mDir.get(pSample);
		if (iV == null)
			mDir.put(pSample, new Integer(v));
		else
			v = iV.intValue();
		return v;
	}
	/* (non-Javadoc)
	 * @see tools.statistics.IModel#size()
	 */
	public int size() {
		return mDir.size();
	}
	/**
	 * @return
	 */
	public String getDictionary() 
	{
		String r = "";
		// lets sort it
		Hashtable invert = new Hashtable();
		Set l = new TreeSet();
		Enumeration en = mDir.keys();
		while (en.hasMoreElements())
		{
			Object o = en.nextElement();
			Object v = mDir.get(o);
			invert.put(v,o);
			l.add(v);
		}
		for (Iterator i= l.iterator(); i.hasNext();)
		{
			Object v = i.next();
			Object o = invert.get(v);
			r+= v.toString() + " \t- " +o.toString()+ "\n";	
		}
		return r + mDir.size() + " \t- Others\n";
	}

}
