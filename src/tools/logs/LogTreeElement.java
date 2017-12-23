/*
 * Created on Aug 31, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package tools.logs;

import java.util.Vector;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class LogTreeElement {
	LogTreeElement mElements[] = null;
	Log.LogEvent mEvents[];
	int mStart;
	int mLast;
	int mPos;
	int mIndex = -1;
	boolean mIgnore = false;
	String mName = "noName";
	public String toString() { return mName; }
	public boolean ignore() { return mIgnore; }
	public int countChildren() {
		if (mElements == null)
			buildSubtree();
		return mElements.length; 
	}
	public LogTreeElement getChildAt(int index) {
		if (index < countChildren()) 
			return mElements[index];
		else return null;
	}
	public int indexOf(LogTreeElement pElement)
	{
		for (int i = 0; i < countChildren(); i++ )
			if (mElements[i] == pElement) return i;
		return -1;
	}
	public void buildSubtree()
	{
		Vector children = new Vector(10);
		LogTreeElement e;
		while ((e = scanForChildren()) != null)
			if (!e.ignore())
			{
				e.setIndex(children.size());
				children.add(e);
			}
		mElements = (LogTreeElement[]) children.toArray(new LogTreeElement[0]);
	}

	public void setIndex(int i) { mIndex = i; }
	public int getIndex() { return mIndex; }
	/**
	 * @return a new tree element scanned from present position, and 
	 * updates mPos to the event element following that posision.
	 * If mPos is passed end of list, retrns a null.
	 */
	abstract LogTreeElement scanForChildren();
	public LogTreeElement(Log.LogEvent pEvents[], int pStart)
	{
		mEvents = pEvents;
		mPos = mStart = pStart;
	}
	public LogTreeElement()	{
		//System.out.println(getClass() + "called");
		mEvents = null;
		mPos = mStart = -1;
	}
}
