package nl.nki.responsepredictor.bool;

import java.util.Arrays;

/**
 * Represents a single boolean state. Basically, just an array but we implement
 * the Comparable operator here so we can use the states in List's and Set's
 * 
 */
public class BooleanState implements Comparable<BooleanState>{
	
	public byte[] b; // no getter to restrict to read-only access => dirty, but fast
	
	
	public BooleanState(byte ... s) {
		this.b=s;
	}
	
	/**
	 * This constructor is just for coding convenience, so we can write e.g.
	 *   new BooleanState(1,0,1);
	 *   
	 * The byte array will be casted to byte.
	 */
	public BooleanState(int ... s) {
		b=new byte[s.length];
		for (int i=0; i<s.length; i++)
			b[i]=(byte)s[i];
	}
	
	
	public int length() {
		return b.length;
	}
	

	@Override
	public int compareTo(BooleanState o) {
		// must be equally sized
		if (b.length!=o.b.length)
			throw new IllegalArgumentException("Arrays must have equal size.");
		// now the actual comparison
		int res=0;
		for (int i=0; i<o.b.length&&res==0;i++) {
			if (b[i]>o.b[i])
				res=1;
			else if (b[i]<o.b[i])
				res=-1;
		}
		return res;
	}
	
	@Override
	public boolean equals(Object obj) {
		// use compareTo above
		return compareTo((BooleanState)obj)==0;
	}
	
	@Override
	protected BooleanState clone() throws CloneNotSupportedException {
		// we just copy the array
		return new BooleanState(b.clone());
	}

	@Override
	public int hashCode() {
		// use hashing function from java
		return Arrays.hashCode(b);
	}
	
	
	@Override
	public String toString() {
		return Arrays.toString(b);
	}
	

	public static BooleanState[] createFromArray(byte[][] res) {
		BooleanState[] r = new BooleanState[res.length];
		for (int i=0; i<res.length; i++)
			r[i]=new BooleanState(res[i]);
		return r;
	}
	

	
}
