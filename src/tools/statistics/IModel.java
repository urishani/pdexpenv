/*
 * Created on 10/03/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.statistics;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface IModel {
	/**
	 * finds a bin in the model to match that sample and return its identity
	 * @param pSample
	 * @return
	 */
	public int match(int pSample);
	/**
	 * Maps an Object sample into a numerical 1-D range for further analysis.
	 * @param pSample
	 * @return
	 */
	public int map(Object pSample);
	/**
	 * returns the size of the model space at present.
	 * @return
	 */
	public int size();
	/**
	 * returns a string describing the dictionary of the data domain of the model
	 * @return
	 */
	public String getDictionary();
}
