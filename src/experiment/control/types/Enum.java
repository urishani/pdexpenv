/*
 * Created on 29/02/2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package experiment.control.types;

/**
 * @author shani
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Enum
{
	protected String getName() { return "ANONIMOUS";}
	protected String getGroup() { return "ENUM";}
	protected void setName(String pName){};
	private static int eVal;
	protected void setEnum(int v){eVal = v;}
	protected int getEnum(boolean next)
	{
		if (next) return ++eVal;
		else return eVal;
	}
	private int mNum;
	protected Enum(String pName) 
	{
		setName(pName);
		mNum = getEnum(true);
	}
	protected Enum(String pName, int v) 
	{
		setName(pName);
		setEnum(v);
		mNum = getEnum(true);
	}
	public String toString() { return getGroup()+ "_" + getName();}
	public int val() { return mNum;}
			
	public int size() { return getEnum(false); }
	public static final Enum ANY = new Enum("ANY", -1);
	public static final Enum CURRENT = new Enum("CURRENT", 0);
}
