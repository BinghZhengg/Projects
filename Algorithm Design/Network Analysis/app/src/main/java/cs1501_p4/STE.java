/**
 * Spanning Tree Edge class for CS1501 Project 4
 * @author	Dr. Farnan
 */
package cs1501_p4;

public class STE {
	
	/**
	 * One endpoint of this edge
	 */
	protected int u;

	/**
	 * The other endpoint
	 */
	protected int w;

	public static final double COPPER_SPEED = 2.3E8;  // meter per second
	public static final double OPTICAL_SPEED = 2E8;  // meter per second
	public static final String COPPER_TYPE = "copper";
	public static final String OPTICAL_TYPE = "optical";
	private String mMaterial;
	private int mBandWidth;
	private int mLength;
	private double mLatency;

	/**
	 * Basic constructor
	 */
	public STE(int v1, int v2) {
		u = v1;
		w = v2;
	}

	/**
	 * Basic constructor
	 */
	public STE(int v1, int v2, String material, int bandwidth, int length) {
		u = v1;
		w = v2;
		mMaterial = material;
		mBandWidth = bandwidth;
		mLength = length;

		/* compute the travelling time for a single package going through this edge v1<->v2 */
		if(material.equals(COPPER_TYPE))
		{
			mLatency = length/COPPER_SPEED;
		}
		else if(material.equals(OPTICAL_TYPE))
		{
			mLatency = length/OPTICAL_SPEED;
		}
		else
		{
			System.out.println("Edge type unknown, latency set to an infinite value!");
			mLatency = Double.POSITIVE_INFINITY;
		}
	}

	/**
	 * @return latency
	 */

	public double getLatency()
	{
		return mLatency;
	}

	/**
	 * @return band width
	 */
	public int getBandWidth()
	{
		return mBandWidth;
	}

	/**
	 * @return material type
	 */
	public String getMaterial()
	{
		return mMaterial;
	}

	public int either()
	{
		return u;
	}
	public int other(int pV)
	{
		if(pV == this.u) return this.w;
		else if(pV == this.w) return this.u;
		else return -1;
	}

	public boolean contains(int pV)
	{
		if((u==pV)||(w==pV)) return true;
		else return false;
	}

	/**
	 * Equality comparison, treating edges as undirected
	 */
	public boolean equals(STE o) {
		if (u == o.u && w == o.w) {
			return true;
		}
		else if (u == o.w && w == o.u) {
			return true;
		}
		else {
			return false;
		}
	}

	public String toString() {
		return "(" + String.valueOf(u) + ", " + String.valueOf(w) + ")";
	}
}
