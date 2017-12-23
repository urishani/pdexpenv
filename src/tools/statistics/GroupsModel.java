/*
 * Created on 10/03/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.statistics;

import java.io.Serializable;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GroupsModel implements IModel, Serializable {

	public interface IGroup
	{
		public boolean match(int pSample);
//		public String toString();
	}
	private IGroup[] mGroups;
	/**
	 * 
	 */
	public GroupsModel(IGroup[] pGroups) {
		mGroups = pGroups;
	}

	/* (non-Javadoc)
	 * @see tools.statistics.IModel#match(int)
	 */
	public int match(int pSample) {
		int i= 0;
		for (; i< mGroups.length; i++)
		   if (mGroups[i].match(pSample)) return i;
		return i;
	}
	public int map(Object pSample)
	{
		if (pSample instanceof Integer)
			return ((Integer)pSample).intValue();
		return 0;
	}

	/* (non-Javadoc)
	 * @see tools.statistics.IModel#size()
	 */
	public int size() {
		return mGroups.length;
	}

	/* (non-Javadoc)
	 * @see tools.statistics.IModel#getDictionary()
	 */
	public String getDictionary() {
		String r= "";
		for (int i=0; i < mGroups.length; i++)
		{
			r += i  + " \t- " + mGroups[i].toString()+ "\n";
		}
		return r + mGroups.length + " \t- Others\n";
	}
	
	private static class GI implements GroupsModel.IGroup, Serializable
	{
		int[] mRange;
		public GI(int[] pRange){mRange = pRange;}
		public boolean match(int pSample){return pSample>=mRange[0] && pSample <mRange[1];}
		public String toString(){return "["+mRange[0]+"-"+mRange[1]+")";}
	}
	
	public static IModel createGroupsModel(int[][] pGroups)
	{
		GI[] groups = new GI[pGroups.length];
		for (int i=0; i< groups.length; i++)
			groups[i]= new GI(pGroups[i]);
		return new GroupsModel(groups);
	}


}
