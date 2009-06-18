package org.amse.vadim.bignumberslibrary;
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
     *The zeroth element of this array is the least-significant int of BigNumber
     * array.  The array must be "minimal" in that the most-significant
     * int ({@code intArray[length-1]}) must be non-zero.  This is necessary to
     * ensure that there is exactly one representation for each BigNumber
     * value.  Note that this implies that the BigNumber zero has a
     * zero-length intArray.
     */
    private int[] intArray;
    /**
     * The length of BigNumber array.
     * If we don`t need some higher ints more(example: leading zeroes)
     * we just changing length instead of recreate new
     * smaller array.
     */
    private int length;

    //---------------------------------//
    //           CONSTRUCTORS          //
    //---------------------------------//

    /**
     * Constructor from bytes with optional reverse and sign indication
     * Does not support complement code.
     */
    private BigNumber(byte[] array, int sign, boolean reverse) {
        if (array == null || array.length == 0) {
            this.sign = 0;
            this.intArray = null;
            this.length = 0;
        } else {
            //reverse in first determination only. Not in operations.
            if (reverse) {
                this.intArray = Convert.intFrom(Util.reverseArray(array));
            } else {
                this.intArray = Convert.intFrom(array);
            }
            this.length = this.intArray.length;
            this.sign = (sign > 0 ? 1 : -1);
        }
    }
  
    /**
     *Constructor from byteArray with sign that using
     * more common constructor BigNumber(byte[] array, int sign, boolean reverse)
     * In this reverse=true.
     */
    public BigNumber(byte[] array, int sign) {
        this(array, sign, true);
    }

    //Constructor like in BigInteger. With support of complement code
    public BigNumber(byte[] array) {
        if (array == null || array.length == 0) {
            this.sign = 0;
            this.intArray = null;
            this.length = 0;
        } else if (array[0] < 0) {
            this.sign = -1;
            this.intArray = Util.addOne(Convert.intFrom(Util.reverseArray(Util.makePositive(array))));

        } else {
            this.sign = 1;
            this.intArray = Convert.intFrom(Util.reverseArray(array));
        }
        this.length = this.intArray.length;
    }

    //only BigNumber(not compatable with BigInteger) constructor.
    public BigNumber(int[] array, int sign) {
        if (array == null || array.length == 0) {
            this.sign = 0;
            this.intArray = null;
            this.length = 0;
        } else {
            this.intArray = array;
            this.length = Util.cutLeadingZero(this.intArray, this.intArray.length);
            this.sign = (sign > 0 ? 1 : -1);
        }
    }

    //Constructor from intArray  with sign>=0
    //not compatible with BigInteger constructors
    public BigNumber(int[] array) {
        this(array, 1);
    }

    //constructor of zero BigNumber
    public BigNumber() {
        this.sign = 0;
        this.intArray = null;
        this.length = 0;
    }

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

    public BigNumber add(BigNumber bnum) {

        if (bnum.length == 0 || bnum.sign == 0) {
            return this;
        }
        if (this.length == 0 || this.sign == 0) {
            return bnum;
        }
        if (this.sign == bnum.sign) {
            return new BigNumber(Operations.summ(this.intArray, this.length, bnum.intArray, bnum.length), this.sign);
        }

        int cmp = Util.compareArrays(this.intArray, this.length, bnum.intArray, bnum.length);

        //If arrays are equal and signs are opposite
        if (cmp == 0) {
            return new BigNumber();
        }

        int[] resultArray = null;

        resultArray = (cmp > 0 ? Operations.subtract(this.intArray, this.length, bnum.intArray, bnum.length)
                : Operations.subtract(bnum.intArray, bnum.length, this.intArray, this.length));

        //Leading Zero will be cutted in constructor

        return new BigNumber(resultArray, cmp * this.sign);
    }

    public BigNumber sub(BigNumber bnum) {

        if (bnum.length == 0 || bnum.sign == 0) {
            return this;
        }
        if (this.length == 0 || this.sign == 0) {
            return bnum.negate();
        }

        if (bnum.sign != sign) {
            return new BigNumber(Operations.summ(this.intArray, this.length, bnum.intArray, bnum.length),
                    this.sign);
        }

        int cmp = Util.compareArrays(this.intArray, this.length, bnum.intArray, bnum.length);

        //if arrays are equal and signs are opposite
        if (cmp == 0) {
            return new BigNumber();
        }

        int[] resultArray = null;

        resultArray = (cmp > 0 ? Operations.subtract(this.intArray, this.length, bnum.intArray, bnum.length)
                : Operations.subtract(bnum.intArray, bnum.length, this.intArray, this.length));

        //Leading Zero will be cutted in constructor

        return new BigNumber(resultArray, cmp * this.sign);

    }

    public BigNumber mul(BigNumber bnum) {
        if (sign == 0 || bnum.sign == 0) {
            return new BigNumber();
        }

        int[] result = Operations.simpleMul(this.intArray, this.length,
                bnum.intArray, bnum.length);

        return new BigNumber(result, this.sign * bnum.sign);
    }
   public BigNumber mulFFT(BigNumber bnum) {
        if (sign == 0 || bnum.sign == 0) {
            return new BigNumber();
        }

        //Doing fourier transform in bytes
        byte[] a = this.toByteArray(false);
        byte[] b = bnum.toByteArray(false);

        byte[] result = Operations.mulByteFFT(a, b);

        return new BigNumber(result, this.sign * bnum.sign, false);
    }

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

    //Divide like "/" - not like "/ + %"
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        //equals with BigInteger
        //BigInteger has one bug when he doesn`t cut leading zero
        //so sometimes equals work wrong!!!
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
            for (int i = 0; i < bnLen; i++) {
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
        for (int i = 0; i < this.length; i++) {
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

    public int getSign() {
        return sign;
    }

    //Just changing sign
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
    //////////////////Вспомогательные функции////////////////////////

    private byte[] toByteArray(boolean reverse) {
        int[] array = this.intArray;
        int len = this.length;
        //leading null bytes in int
        int freeBits = 32 - Util.bitLen(array[len - 1]);
        //BUG
        int freeBytes = freeBits % 8 == 0 ? freeBits / 8 - 1 : freeBits / 8;

        if (this.sign == -1) {
            array = Util.reverseInts(this.intArray);
        }
        //BUG
        int byteArrayLen = (len - 1) * 4 + (4 - freeBytes);
        byte[] byteArray = new byte[byteArrayLen];

        for (int i = 0; i < len - 1; ++i) {
            for (int j = 0; j < 4; ++j) {
                byteArray[i * 4 + j] = (byte) (array[i] >>> 8 * j);
            }
        }

        for (int j = 0; j < 4 - freeBytes; ++j) {
            byteArray[((len - 1) * 4) + j] = (byte) (array[len - 1] >>> 8 * j);
        }


        if (reverse) {
            return Util.reverseArray(Util.cutLeadingZero(byteArray));
        } else {
            return Util.cutLeadingZero(byteArray);
        }
    }
    //reverse by default

    public byte[] toByteArray() {
        return this.toByteArray(true);
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
        for (int i = 0; i < result.length(); i++) {
            res.append(result.charAt(result.length() - 1 - i));
        }

        return res.toString();
    }
}
