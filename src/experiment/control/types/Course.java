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
public class Course extends Enum 
{
	private Course(String pName){	super(pName);}
	protected String getGroup() { return "COURSE";}
	private static int mVal= 0;
	
	/* (non-Javadoc)
	 * @see experiment.control.types.Enum#getEnum(boolean)
	 */
	protected int getEnum(boolean next) {
		if (next) return ++mVal;
		else return mVal;
	}

	/* (non-Javadoc)
	 * @see experiment.control.types.Enum#setEnum(int)
	 */
	protected void setEnum(int v) {
		mVal = v;
	}
	public static final Course ONE = new Course("ONE");
	public static final Course TWO = new Course("TWO");
	public static final Course THREE = new Course("THREE");
}