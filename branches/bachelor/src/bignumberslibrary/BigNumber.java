package bignumberslibrary;

import util.Convert;
import util.DivisionData;
import util.Util;
import java.math.BigInteger;

/**
 * All operations behave as if
 * BigNumbers were represented in two's-complement notation (like Java's
 * primitive integer types).  BigNumber provides analogues to all of Java's
 * primitive integer operators.
 * @see BigInteger
 * @author chibis
 */
public class BigNumber implements Comparable<BigNumber> {

    /**
     * The signum of this BigNumber: -1 for negative, 0 for zero, or
     * 1 for positive.  Note that the BigNumber zero <i>must</i> have
     * a signum of 0.  This is necessary to ensures that there is exactly one
     * representation for each BigNumber value.
     */
    private int sign;
    /**
     * The zeroth element of this array is the least-significant int of BigNumber
     * array.  The array must be "minimal" in that the most-significant
     * int ({@code intArray[length-1]}) must be non-zero.  This is necessary to
     * ensure that there is exactly one representation for each BigNumber
     * value.  Note that this implies that the BigNumber zero has a
     * zero-length intArray.
     */
    private int[] intArray;
    /**
     * The number of significant elements in BigNumber array.
     */
    private int length;

    //---------------------------------//
    //           CONSTRUCTORS          //
    //---------------------------------//
    /**
     * Private constructor from bytes with sign indication and optional reverse.
     *
     * @param array byte array in big-endian notation to make BigNumber from
     * @param sign BigNumber`s sign
     * @param reverse option to reverse bytes order in array before making BigNumber
     * @throws NumberFormatException if sign is not one of the three
     *         legal values (-1, 0, and 1), or sign is 0 and
     *         array contains one or more non-zero bytes.
     * @throws NullPointerException if gived array is null
     */
    private BigNumber(byte[] array, int sign, boolean reverse) {
        if (sign < -1 || sign > 1) {
            throw (new NumberFormatException("Invalid sign. Must be one of {-1, 0, 1}"));
        }

        //reverse in first determination only. Not in operations.
        if (reverse) {
            this.intArray = Convert.intFrom(Util.reverseArray(array));
        } else {
            this.intArray = Convert.intFrom(array);
        }

        if (this.intArray.length == 0) {
            this.sign = 0;
        } else {
            if (sign == 0) {
                throw (new NumberFormatException("Non-zero length array but sign is zero"));
            }
            this.sign = sign;
        }
        this.length = this.intArray.length;
    }

    /**
     * Translates byte array into BigNumber.
     * The magnitude is a byte array in big-endian notation:
     * the most significant byte is the zeroth element.
     * A zero-length  array is permissible, and will
     * result in a BigNumber value of 0, whether signum is -1, 0 or 1.
     *
     * The sign is represented as an integer signum value: -1 for
     * negative, 0 for zero, or 1 for positive.
     *
     * @param  sign BigNumber`s sign (-1 for negative, 0 for zero, 1
     *         for positive).
     * @param  array source byte array in big-endian notation to make BigNumber from
     * @throws NumberFormatException if sign is not one of the three
     *         legal values (-1, 0, and 1), or sign is 0 and
     *         array contains one or more non-zero bytes.
     * @throws NullPointerException if gived array is null
     */
    public BigNumber(int sign, byte[] array) {//reverse=true
        this(array, sign, true);
    }

    /**
     * Main constructor. Works like the same in BigInteger class.
     * If first byte less than zero it thinks, that BigNumber is non positive.
     * (will set sign=-1 and store data in complement code)
     *
     * @see BigInteger
     *
     * @param array source byte array in big-endian notation to make BigNumber from
     * @throws NumberFormatException if gived array is zero-length
     * @throws NullPointerException if gived array is null
     */
    public BigNumber(byte[] array) {//always reverse, cause must work like BigInteger constructor but have reversed array.
        if (array.length == 0) {
            throw new NumberFormatException("Zero length BigNumber");
        }

        if (array[0] < 0) { //reversing code
            this.sign = -1;
            this.intArray =
                    Util.addOne(Convert.intFrom(Util.reverseComplementCodeArray(array)));
        } else {//positive non-zero BigNumber
            this.intArray = Convert.intFrom(Util.reverseArray(array));
            this.sign = (this.intArray.length == 0 ? 0 : 1);
        }
        this.length = this.intArray.length;
    }

    /**
     * Constructor for BigNumber from int array in little-endian notation, it means
     * that zeroth element of array is the least significant.
     *
     * Interprets null and zero-length arrays as zero BigNumber ignoring sign.
     *
     * @param array source int array in little-endian notation.
     * @param sign BigNumber`s sign
     */
    public BigNumber(int[] array, int sign) {
        if (sign < -1 || sign > 1) {
            throw (new NumberFormatException("Invalid sign. Must be one of {-1, 0, 1}"));
        }

        if (array == null || array.length == 0) {
            this.sign = 0;
            this.intArray = new int[]{0};
            this.length = 0;
        } else {
            this.intArray = array;
            this.length = Util.cutLeadingZero(this.intArray, this.intArray.length);
            if (this.length == 0) {//if after cutting zeroes we get zero length array
                this.sign = 0;
            } else {
                this.sign = (sign > 0 ? 1 : -1);
            }
        }
    }

    /**
     * Constructor of BigNumber in little-endian notation.
     * It is same as BigNumber(int[] array, int sign), except sign is 1 by default.
     * <p>
     * Does not support non positive array`s(with last byte less than zero), interprets them as
     * positive BigNumber in complement code.
     * <p>
     * To make BigNumber non positive use <code>bigNumber.negate()</code> or create BigNumber using
     * constructor from int array with sign.
     *
     * @param array source int array in little-endian notation to make BIgNUmber from
     */
    public BigNumber(int[] array) {
        this(array, 1);
    }

    /**
     * Constructor of zero BigNumber.
     */
    public BigNumber() {
        this.sign = 0;
        this.intArray = new int[]{0};
        this.length = 0;
    }

    /**
     *
     * @param str
     * @deprecated until testing.
     */
    public BigNumber(String str) {
        int sign = 1;
        int len;
        if (str.charAt(0) == '-') {
            sign = -1;
            len = str.length() - 1;
        } else {
            len = str.length();
        }

        byte[] ar = new byte[len];
        int f;
        if (sign == 1) {
            f = 0;
        } else {
            f = 1;
        }

        for (int i = f; i < len; i++) {
            ar[len - 1 - i] = (byte) (str.charAt(i) - '0');
        }

        StringBuilder binRepresentation = Util.binRepr(ar);
        int end = binRepresentation.length();
        int resLen = end / 8;

        byte[] result = new byte[resLen];

        for (int i = 0; i < end;) {
            int bits = 0;
            int k = i / 8;
            while (bits < 8) {
                result[k] = (byte) (result[k] << 1);
                result[k] = (byte) (result[k] + (binRepresentation.charAt(i) - '0'));
                i++;
                bits++;
            }
        }
        result = Util.reverseArray(result);

        this.intArray = Convert.intFrom(result);
        this.sign = sign;
        this.length = this.intArray.length;
    }
//////////////////////////////////End of Constructors///////////////////////////////

    //---------------------------------//
    //           OPERATIONS            //
    //---------------------------------//
    /**
     * Returns a BigNumber whose value is {@code (this + bnum)}.
     *
     * @param  bnum value to be added to this BigInteger.
     * @return {@code this + bnum}
     */
    public BigNumber add(BigNumber bnum) {

        if (bnum.length == 0 || bnum.sign == 0) {
            return this;
        }

        if (this.length == 0 || this.sign == 0) {
            return bnum;
        }

        if (this.sign == bnum.sign) {
            return new BigNumber(Operations.summ(this.intArray, this.length, bnum.intArray,
                    bnum.length), this.sign);
        }

        int cmp = Util.compareArrays(this.intArray, this.length, bnum.intArray, bnum.length);

        //If arrays are equal and signs are opposite
        if (cmp == 0) {
            return new BigNumber();
        }

        int[] resultArray = null;

        resultArray = (cmp > 0
                ? Operations.subtract(this.intArray, this.length, bnum.intArray, bnum.length)
                : Operations.subtract(bnum.intArray, bnum.length, this.intArray, this.length));

        //Leading Zero will be cutted in constructor
        return new BigNumber(resultArray, cmp * this.sign);
    }

    /**
     * Returns a BigNumber whose value is {@code (this - bnum)}.
     *
     * @param  bnum value to be subtracted from this BigNumber.
     * @return {@code this - bnum}
     */
    public BigNumber sub(BigNumber bnum) {

        if (bnum.length == 0 || bnum.sign == 0) {
            return this;
        }

        if (this.length == 0 || this.sign == 0) {
            return bnum.negate();
        }

        if (bnum.sign != sign) {
            return new BigNumber(Operations.summ(this.intArray, this.length, bnum.intArray,
                    bnum.length), this.sign);
        }

        int cmp = Util.compareArrays(this.intArray, this.length, bnum.intArray, bnum.length);

        //if arrays are equal and signs are opposite
        if (cmp == 0) {
            return new BigNumber();
        }

        int[] resultArray = null;

        resultArray = (cmp > 0
                ? Operations.subtract(this.intArray, this.length, bnum.intArray, bnum.length)
                : Operations.subtract(bnum.intArray, bnum.length, this.intArray, this.length));

        //Leading Zero will be cutted in constructor

        return new BigNumber(resultArray, cmp * this.sign);
    }

    /**
     * Returns a BigNumber whose value is {@code (this * bnum)}.
     *
     * @param  bnum value to be multiplied by this BigInteger.
     * @return {@code this * bnum}
     */
    public BigNumber multiply(BigNumber bnum) {
        if (sign == 0 || bnum.sign == 0) {
            return new BigNumber();
        }

        int[] result = Operations.simpleMul(this.intArray, this.length, bnum.intArray, bnum.length);

        return new BigNumber(result, this.sign * bnum.sign);
    }

    /**
     * Returns a BigNumber whose value is {@code (this * bnum)}.
     * Using FFT algoritm.(Slow version that use byte base)
     *
     * @param  bnum value to be multiplied by this BigInteger.
     * @return {@code this * bnum}
     */
    public BigNumber mulFFT(BigNumber bnum) {
        if (sign == 0 || bnum.sign == 0) {
            return new BigNumber();
        }

        //Doing fourier transform in bytes
        byte[] a = this.toByteArray();
        byte[] b = bnum.toByteArray();

        byte[] result = Operations.mulByteFFT(a, b);

        return new BigNumber(result, this.sign * bnum.sign, false);
    }

    /**
     * Returns a BigNumber whose value is {@code (this * bnum)}.
     * Using FFT algoritm.(Version that use short base)
     *
     * @param  bnum value to be multiplied by this BigInteger.
     * @return {@code this * bnum}
     */
    public BigNumber mulFFT2(BigNumber bnum) {
        if (sign == 0 || bnum.sign == 0) {
            return new BigNumber();
        }

        //Doing fourier transform in shorts
        short[] a = Convert.shortFrom(this.intArray, this.length);
        short[] b = Convert.shortFrom(bnum.intArray, bnum.length);

        byte[] result = Operations.mulShortFFT(a, b);

        return new BigNumber(result, this.sign * bnum.sign, false);
    }

    /**
     * Divide like "/" - not like "/ + %"
     * @deprecated until fixed bugs and tested
     * @param bnum
     * @return
     * @throws Exception
     */
    public BigNumber div(BigNumber bnum) throws Exception {
        return new BigNumber(this.divide(bnum).getQ(), this.sign * bnum.sign);
    }

    private DivisionData divide(BigNumber bnum) throws Exception {
        if (bnum.sign == 0) {
            throw new Exception("Divide by zero Exception");
        }
        if (this.sign == 0) {
            return new DivisionData(null, null);
        }
        if (bnum.length > this.length) {
            return new DivisionData(null, this.intArray);
        }

        DivisionData resData;

        if (bnum.length == 1) {
            resData = Operations.simpleDiv(this.intArray, this.length, bnum.intArray[0]);
            return resData;
        }

        resData = Operations.div(this, bnum);
        return resData;
    }
    ///////////////////////End of operations//////////////////////////////////

    /**
     * @deprecated until tests
     * @param n
     * @return
     */
    public BigNumber shiftLeft(int n) {
        if (this.sign == 0) {
            return new BigNumber();
        }
        if (n == 0) {
            return this;
        }
        if (n < 0) {
            return shiftRight(-n);
        }
        int[] result = Util.leftShift(this.intArray, this.length, n);
        return new BigNumber(result, this.sign);
    }

    /**
     * @deprecated until tests
     * @param n
     * @return
     */
    public BigNumber shiftRight(int n) {
        if (this.sign == 0) {
            return new BigNumber();
        }
        if (n == 0) {
            return this;
        }
        if (n < 0) {
            return shiftLeft(-n);
        }

        int[] result = Util.rightShift(this.intArray, this.length, n);
        return new BigNumber(result, this.sign);
    }

    /**
     * Compares this BigNumber with the specified Object for equality.
     *
     * @param  x Object to which this BigInteger is to be compared.
     * @return {@code true} if and only if the specified Object is a
     *         BigInteger or BigNumber whose value is numerically equal to this BigNumber.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof BigInteger) {
            BigInteger bint = (BigInteger) obj;
            byte[] bi = bint.toByteArray();
            byte[] bn = this.toByteArray();
            int biLen = bi.length;
            int bnLen = bn.length;

            if (biLen != bnLen) {
                return false;
            }
            //if elements not the same
            for (int i = 0; i< bnLen; i++) {
                if (bi[i] != bn[i]) {
                    return false;
                }
            }
            return true;
        }

        if (!(obj instanceof BigNumber)) {
            return false;
        }

        BigNumber bnum = (BigNumber) obj;
        //if lengths or signs differ
        if (bnum.sign != this.sign || bnum.length != this.length) {
            return false;
        }
        //if elements not the same
        for (int i = 0; i< this.length; i++) {
            if (bnum.intArray[i] != this.intArray[i]) {
                return false;
            }
        }
        return true;
    }

    //returns 1 if this>bnum
    //returns -1 if this<bnum
    //returns 0 if elements are the same
    public int compareTo(BigNumber bnum) {
        int cmp = Util.compareArrays(this.intArray, this.length, bnum.intArray, bnum.length);
        return cmp;
    }

    /**
     * Returns BigNumber`s sign.
     * @return BigNumber`s sign
     */
    public int getSign() {
        return sign;
    }

    /**
     * Returning new BigNumber with opposite sign.
     * @return new BigNumber with opposite sign.
     */
    public BigNumber negate() {
        return new BigNumber(intArray, -sign);
    }

    //returning only real part
    //remember array may have elements
    //at indexes more than length!!
    public int[] getArrayOfBigNumber() {
        int[] result = new int[this.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = this.intArray[i];
        }
        return result;
    }

    public int getLength() {
        return this.length;
    }

    //////////////////Some additional stuff////////////////////////
    public byte[] toByteArray() {
        int byteLen = bitLength() / 8 + 1;

        byte[] byteArray = new byte[byteLen];

        //reverse operations
        for (int i = byteLen - 1, bytesCopied = 4, nextInt = 0, intIndex = 0; i >= 0; i--) {
            if (bytesCopied == 4) {
                nextInt = getInt(intIndex++);
                bytesCopied = 1;
            } else {
                nextInt >>>= 8;
                bytesCopied++;
            }
            byteArray[i] = (byte) nextInt;
        }
        return byteArray;
    }

    /**
     * Returns the specified int of the little-endian two's complement
     * representation (int[0] is the least significant).  The int number can
     * be arbitrarily high (values are logically preceded by infinitely many
     * sign ints).
     */
    private int getInt(int n) {
        if (n < 0) {
            return 0;
        }

        if (n >= this.length) {
            return signInt();
        }

        int magInt = intArray[n];
        return (this.sign >= 0 ? magInt : (n <= firstNonzeroIntNum() ? -magInt : ~magInt));
    } 
    
    /* Returns an int of sign bits */
    private int signInt() {
        return (this.sign < 0 ? -1 : 0);
    }

    /**
     * Returns the index of the int that contains the first nonzero int in the
     * little-endian binary representation of the magnitude (int 0 is the
     * least significant). If the magnitude is zero, return value is undefined.
     */
    private int firstNonzeroIntNum() {
        // Search for the first nonzero int
        int firstNonzeroIntNum;

        int i;
        for (i = 0; i  < this.length && this.intArray[i] == 0; i++) {
            ;
        }

        return i;
    }

    public int bitLength() {
        /*
         * Initialize bitLength field the first time this method is executed.
         * This method depends on the atomicity of int modifies; without
         * this guarantee, it would have to be synchronized.
         */
        int bitLength;

        if (this.sign == 0) {
            bitLength = 0;
        } else {
            // Calculate the bit length of the magnitude
            int magBitLength =
                    ((this.length - 1) << 5) + Util.bitLen(this.intArray[this.length - 1]);
            if (this.sign < 0) {
                // Check if magnitude is a power of two
                boolean pow2 = (bitCnt(this.intArray[this.length - 1]) == 1);
                for (int i = this.length - 2; i
                        >= 0 && pow2; i--) {
                    pow2 = (this.intArray[i] == 0);
                }
                bitLength = (pow2 ? magBitLength - 1 : magBitLength);
            } else {
                bitLength = magBitLength;
            }
        }
        return bitLength;
    }

    private static int bitCnt(int val) {
        val -= (0xaaaaaaaa & val) >>> 1;
        val = (val & 0x33333333) + ((val >>> 2) & 0x33333333);
        val = val + (val >>> 4) & 0x0f0f0f0f;
        val += val >>> 8;
        val += val >>> 16;
        return val & 0xff;
    }

    public String toStr() throws Exception {
        if (this.sign == 0) {
            return "0";
        }

        StringBuilder result = new StringBuilder();

        int[] dvdr = {10};
        BigNumber divider = new BigNumber(dvdr);
        DivisionData dd = new DivisionData();

        dd = this.divide(divider);

        if (dd.getQ() == null) {
            return new String("" + dd.getR()[0]);
        }

        int[] q = dd.getQ();
        while (!(q.length == 1 && q[0] == 0)) {
            result.append(dd.getR()[0]);
            dd = new BigNumber(q).divide(divider);
            q = dd.getQ();
        }
        result.append(dd.getR()[0]);

        StringBuilder res = new StringBuilder();
        if (this.sign == -1) {
            res.append("-");
        }
        for (int i = 0; i< result.length(); i++) {
            res.append(result.charAt(result.length() - 1 - i));
        }

        return res.toString();
    }
}
