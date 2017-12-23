/*
 * Created on 07/03/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.statistics;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;
import java.util.Stack;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Statistics implements Serializable{
	private Stack mData = new Stack();
	private IModel mModel = null;
	int[] mHistogram = null;
	public Statistics(IModel pModel)
	{
		mModel = pModel;
	}
	public void registerSample(Object pEvent)
	{
		mData.push(pEvent);
		mHistogram = null;
	}
	private int[] getHistogram()
	{
		if (mHistogram != null)
			return mHistogram;
			
		for (int i=0; i < mData.size(); i++)
		{
			Object oV = mData.get(i);
			mModel.map(oV);
		}
		mHistogram = new int[mModel.size()+1];
		for (int j = 0; j < mHistogram.length; j++)
			mHistogram[j]= 0;
		for (int i=0; i < mData.size(); i++)
		{
			Object oV = mData.get(i);
			int v = mModel.map(oV);
			int j = mModel.match(v);
			mHistogram[j]++;	
		}
		return mHistogram;
	}
	
	public void reset()
	{
		mData.clear();
		mHistogram = null;
	}
	
	public String toString()
	{
		String r = "";
		String x = "";
		String[] v = new String[10];
		getHistogram();
		int max = 0;
		for (int i=0; i< mHistogram.length; i++)
			if (mHistogram[i] > max)
				max = mHistogram[i];
		double f = 1.0;
		if (max > 0)
			f = 10.0/max;
		for (int j=0; j < v.length; j++)
		{
			v[j]="";
			for (int i=0; i < mHistogram.length; i++)
			{
				if (mHistogram[i]*f > j)
					v[j]+= " X  ";
				else
					v[j]+= "    ";
			}
		}
		for (int j= v.length-1; j>=0; j--)
			r+= v[j]+"\n";
		for (int i=0; i < mHistogram.length; i++)
		{
			r+= "---|";
		}
		r+= "\n";
		for (int i=0; i < mHistogram.length; i++)
		{
			String x1 = Integer.toString(i);
			x1 = (" " + x1 + "   ").substring(0,4); 
			r += x1;
		}
		r+= "\n";
			
		return r;
	}
	
	public static void main(String[] args)
	{
System.err.println("Using discrete model:");		
		IModel model = new DiscreteModel();
		Statistics s = new Statistics(model);
		String[] data = {"asdw", "treer", "ewds", "iyty", "nbft", "rvgds", "rkiuy", "tbfs"};
		Random r = new Random(new Date().getTime());
		for (int i=0 ; i<1000; i++)
		{
			int v = r.nextInt(data.length);
			s.registerSample(data[v]);
		}
		System.out.println("histogram:\n"+s.toString());
		System.out.println("Dictionary: \n" + model.getDictionary());

System.err.println("Using group model:");		
		s = new Statistics(model=GroupsModel.createGroupsModel(new int[][]{{1,3},{3,8},{8,12}}));
		for (int i=0 ; i<1000; i++)
		{
			int v = r.nextInt(11) +1;
			s.registerSample(new Integer(v));
		}
		System.out.println("histogram:\n"+s.toString());
		System.out.println("Dictionary: \n" + model.getDictionary());
		
		 
	}
	/**
	 * 
	 */
	public int size() {
		return mData.size();
	}

}
