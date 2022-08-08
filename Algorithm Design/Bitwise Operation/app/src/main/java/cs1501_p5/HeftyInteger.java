
/**
 * HeftyInteger for CS1501 Project 5
 * @author	Dr. Farnan
 */
		package cs1501_p5;

import java.util.Arrays;

public class HeftyInteger {

	private final byte[] ONE = {(byte) 1};
	private final byte[] ZERO = {(byte) 0};
	static final short BYTE_MASK = 0xFF;
	static final int SHORT_MASK = 0xFFFF;
	private byte[] val;
	/**
	 * Construct the HeftyInteger from a given byte array
	 * @param b the byte array that this HeftyInteger should represent
	 */
	public HeftyInteger(byte[] b) {
		val = b;
		/* stripLeadingZeroBytes only work on positive HeftyInteger for sure */
		stripLeadingZeroBytes();
	}

	/**
	 * Return this HeftyInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/**
	 * Return the number of bits in val
	 * @return length of the val bits
	 */
	public int bitLength() {
		return val.length*8;
	}

	/**
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other HeftyInteger to sum with this
	 */
	public HeftyInteger add(HeftyInteger other) {
		if(other.val.length==0)
		{
			return this;
		}

		if(other.isZero())
		{
			return this;
		}

		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		HeftyInteger res_li = new HeftyInteger(res);

		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		/* for some reason the a-a can result in val==[] */
		if(res_li.val.length == 0)
		{
			res_li.val = ZERO;
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public HeftyInteger negate() {

		if((val.length==0))
		{
			return new HeftyInteger(ZERO);
		}

		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		HeftyInteger neg_li = new HeftyInteger(neg);

		// add 1 to complete two's complement negation
		return neg_li.add(new HeftyInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other HeftyInteger to subtract from this
	 * @return difference of this and other
	 */
	public HeftyInteger subtract(HeftyInteger other) {
		return this.add(other.negate());
	}

	/**
	 * shift all bits in val[] to left for n bits
	 * @param n number of bits to shift
	 * @return this*2^n
	 */

	public HeftyInteger shiftLeft(int n)
	{
		return new HeftyInteger(shiftLeft(val,n));
	}


	private byte[] shiftLeft(byte[] value, int n) {
		if(value.length==0) value=ZERO;
		if(n==0) return value;
		if(n<0) return shiftRight(value, -n);

		int nBytes = n >>> 3;  // nBytes = n bits / 2^3
		//int nBits = n & 0x111;  // n & 7 (7=8-1)
		int nBits = n % 8;
		int byteArraySize = value.length;
		byte newByteArray[] = null;

		if (nBits == 0) {
			newByteArray = new byte[byteArraySize + nBytes];
			System.arraycopy(value, 0, newByteArray, 0, byteArraySize);
		} else {
			int i = 0;
			int nBits2 = 8 - nBits;
			byte highBits = (byte) ((value[0] & 0xFF) >>> nBits2);
			if (highBits != 0) {
				newByteArray = new byte[byteArraySize + nBytes + 1];
				newByteArray[i++] = highBits;
			} else {
				newByteArray = new byte[byteArraySize + nBytes];
			}
			int j=0;
			while (j < byteArraySize-1)
			{
				newByteArray[i++] = (byte) (value[j++] << nBits | ((value[j]& 0xFF)) >>> nBits2);
			}
			newByteArray[i] = (byte)(value[j] << nBits);
		}

		// if newByteArray[0] < 0, ie the bit[7]=1, we need to add a leading zero byte to avoid shifted value turned into negative
		if(newByteArray[0]<0)
		{
			int len = newByteArray.length;
			byte[] explandedByteArray = new byte[len+1];
			explandedByteArray[0] = (byte)0x0;
			for(int i=0;i<len;i++)
			{
				explandedByteArray[i+1] = newByteArray[i];
			}
			return explandedByteArray;
		}

		return newByteArray;
	}

	/**
	 * shift all bits in val[] to right for n bits
	 * @param n number of bits to shift
	 * @return this/2^n
	 */
	public HeftyInteger shiftRight(int n)
	{
		return new HeftyInteger(shiftRight(val,n));
	}

	/**
	 * shift all bits in val[] to right for n bits
	 * @param n number of bits to shift
	 * @return this/2^n
	 */
	private byte[] shiftRight(byte[] value, int n) {
		if(n==0) return value;
		if(n<0) return shiftLeft(value, -n);

		int nBytes = n >>> 3;
		int nBits = n % 8;
		int byteArraySize = value.length;
		byte newByteArray[] = null;

		// Special case: entire contents shifted off the end
		if (nBytes >= byteArraySize)
			return ZERO;

		if (nBits == 0) {
			int newByteArraySize = byteArraySize - nBytes;
			newByteArray = Arrays.copyOf(value, newByteArraySize);
		} else {
			int i = 0;
			byte highBits = (byte)((value[0]& 0xFF) >>> nBits);
			if (highBits != 0) {
				newByteArray = new byte[byteArraySize - nBytes];
				newByteArray[i++] = highBits;
			} else {
				newByteArray = new byte[byteArraySize - nBytes -1];
			}

			int nBits2 = 8 - nBits;
			int j=0;
			while (j < byteArraySize - nBytes - 1)
				newByteArray[i++] = (byte)((value[j++] << nBits2) | ((value[j]& 0xFF) >>> nBits));
		}
		if(newByteArray.length==0)
		{
			newByteArray=ZERO;
		}
		return newByteArray;
	}

	/**
	 * remove all leading zero bytes, if the first byte having nonzero value is a negative value,
	 * one zero byte must be kept before it to avoid incorrect resulted value.
	 */
	private void stripLeadingZeroBytes()
	{
		int keep;

		// Find first nonzero byte
		if(val.length<=1) return;

		for (keep = 0; keep < this.val.length && this.val[keep] == 0; keep++)
			;
		// if val[keep] is negative, we need to add one zero value byte in front of it to avoid bit8 become negative sign.
		if( (keep>0) && (keep<this.val.length)&&(this.val[keep]<0)) keep--;

		// Allocate new array and copy relevant part
		int newLength = val.length - keep;
		byte[] result = new byte[newLength];
		int b = val.length - 1;
		for(int i=0;i<val.length-keep;i++)
		{
			if(keep+i==-1) {};
			result[i]=val[keep+i];
		}
		val=result;
	}

	/**
	 * Compute the product of this and other using Karatsubu algorithm
	 * @param y HeftyInteger to multiply by this
	 * @return product of this and other
	 */
	public HeftyInteger karatsuba(HeftyInteger y)
	{
		if(this.isZero()||y.isZero())
		{
			return new HeftyInteger(ZERO);
		}
		int N = Math.max(this.bitLength(), y.bitLength());
		if (N <= 2000)
			return this.multiply(y);                // optimize this parameter

		// number of bits divided by 2, rounded up
		N = (N / 2) + (N % 2);

		// x = a + 2^N b,   y = c + 2^N d
		HeftyInteger b = this.shiftRight(N);
		HeftyInteger a = this.subtract(b.shiftLeft(N));
		HeftyInteger d = y.shiftRight(N);
		HeftyInteger c = y.subtract(d.shiftLeft(N));

		/* to avoid endless loop as repeating same this and y value, this happens when b and d are 0 */
		if(this.subtract(a).isZero()&&y.subtract(c).isZero())
		{
			return this.multiply(y);
		}

		// compute sub-expressions
		HeftyInteger ac = a.karatsuba(c);
		HeftyInteger bd = b.karatsuba(d);
		HeftyInteger abcd = a.add(b).karatsuba(c.add(d));

		return ac.add(abcd.subtract(ac).subtract(bd).shiftLeft(N)).add(bd.shiftLeft(2 * N));

	}

	/**
	 * Compute the product of this and other
	 * @param other HeftyInteger to multiply by this
	 * @return product of this and other
	 */

	public HeftyInteger multiply(HeftyInteger other)
	{

		int N = Math.max(this.bitLength(), other.bitLength());
		if (N > 2000)
			return this.karatsuba(other);                // optimize this parameter

		HeftyInteger a=this;
		HeftyInteger b=other;
		if(this.isNegative())
		{
			a = this.negate();
		}
		if(other.isNegative())
		{
			b=other.negate();
		}
		int signOfProduct = (this.isNegative()== other.isNegative()? 1:-1);

		byte[] x = a.getVal();
		byte[] y = b.getVal();
		int xlen = x.length;
		int ylen = y.length;

		int xstart = xlen - 1;
		int ystart = ylen - 1;

		byte[] result = new byte[xlen+ylen];

		short carry = 0;

		for (int j=ystart, k=ystart+1+xstart; j >= 0; j--, k--) {
			short product = (short) ((y[j] & 0xFF) * (x[xstart] & 0xFF) + carry);
			result[k] = (byte)product;
			carry = (short) ((product & 0xFFFF) >>> 8);
		}
		result[xstart] = (byte)carry;

		for (int i = xstart-1; i >= 0; i--) {
			carry = 0;
			for (int j=ystart, k=ystart+1+i; j >= 0; j--, k--) {
				long product = (y[j] & 0xFF) * (x[i] & 0xFF) + (result[k] & 0xFF) + carry;
				result[k] = (byte)product;
				carry = (short) ((product & 0xFFFF) >>> 8);
			}
			result[i] = (byte)carry;
		}

		HeftyInteger res = new HeftyInteger(result);
		if(signOfProduct == -1)
		{
			res = res.negate();
		}

		return res;
	}

	/**
	 * If this is zero value
	 * @return true if this is zero, false if non zero
	 */
	public boolean isZero()
	{
		for (byte b : val) {
			if (b != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compute the division of this as nominator and other as denominator with Euclidean division
	 * @param other HeftyInteger is the denominator.
	 * @return [quotient, remainder] as result of this divided by other.
	 */
	public HeftyInteger[] divide(HeftyInteger other)
	{
		HeftyInteger[] result = new HeftyInteger[2];
		if(other.isZero()) throw new IllegalArgumentException("Argument 'divisor' is 0");

		if(isNegative())
		{
			result = negate().divide(other);
			if (result[1].isZero())
			{
				result[0] = result[0].negate();
				result[1] = new HeftyInteger(ZERO);
				return result;
			}
			else
			{
				result[0] = result[0].negate();
				result[1] = result[1].negate();
				return result;
			}
		}
		else{
			if(other.isNegative()){
				result = divide(other.negate());
				result[0]=result[0].negate();
				return result;
			}
			else
			{
				return divide_unsigned_fast(other);
			}

		}
	}

	/**
	 * Compute the max bitwise left shift to have b*2^keep as close as possible to a
	 * @param a HeftyInteger is the target number
	 * @param b HeftyInteger is the number to be left shifted
	 * @return [quotient, remainder] as result of this divided by other.
	 */
	private int maxShift(HeftyInteger a, HeftyInteger b)
	{
		HeftyInteger R = a;
		if(a.subtract(b).isNegative()) return 0;
		int k = 0;

		while(!R.isNegative())
		{
			R = a.subtract(b.shiftLeft(k));
			if(R.getVal().length == 0) {
				System.out.println("Remainder has zero length byte array!");
				return k;
			};
			k++;
		}
		return k-2;
	}

	/**
	 * helper function for Euclidean division with a fast implementation
	 * @param other HeftyInteger is the denominator.
	 * @return [quotient, remainder] as result of this divided by other.
	 */
	private HeftyInteger[] divide_unsigned_fast(HeftyInteger other)
	{
		HeftyInteger[] result = new HeftyInteger[2];
		result[0]=new HeftyInteger(ZERO);
		result[1]=this;

		while(!(result[1].subtract(other).isNegative()))
		{
			int maxShift = maxShift(result[1],other);
			result[0] = result[0].add(new HeftyInteger(ONE).shiftLeft(maxShift));
			result[1] = result[1].subtract(other.shiftLeft(maxShift));
		}
		return result;

	}


	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another HeftyInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	public HeftyInteger[] XGCD(HeftyInteger other)
	{

		HeftyInteger[] result = new HeftyInteger[3];

		if(other.isZero()){
			result[0] = this;
			result[1] = new HeftyInteger(ONE);
			result[2] = new HeftyInteger(ZERO);
			return result;
		}

		HeftyInteger[] divisionResult = this.divide(other);

		result = other.XGCD(divisionResult[1]);
		HeftyInteger d = result[0];
		HeftyInteger a = result[2];

		HeftyInteger tmp1 = divisionResult[0];
		HeftyInteger tmp2 = tmp1.multiply(result[2]);
		HeftyInteger b = result[1].subtract(tmp2);
		return new HeftyInteger[]{d, a, b};

	}
}
