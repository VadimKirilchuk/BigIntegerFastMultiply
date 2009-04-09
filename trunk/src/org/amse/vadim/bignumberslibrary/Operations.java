/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.bignumberslibrary;

/**
 *
 * @author chibis
 */
public class Operations {

    private Operations() {
    }

    public static int[] summ(int[] num1, int len1, int[] num2, int len2) {

	if (len1 < len2) {
	    int[] tmp = num1;
	    num1 = num2;
	    num2 = tmp;
	    int tmpLen = len1;
	    len1 = len2;
	    len2 = tmpLen;
	}

	//cause for +1 is overflowing;
	int[] result = new int[len1 + 1];
	long sum = 0;

	int i = 0;

	//adding common parts
	while (i < len2) {
	    sum = (num1[i] & Util.LONG_MASK) +
		    (num2[i] & Util.LONG_MASK) + (sum >>> 32);

	    result[i] = (int) (sum);
	    ++i;
	}

	boolean carry = (sum >>> 32 != 0);

	while (i < len1 && carry) {
	    result[i] = num1[i] + 1;
	    if (result[i] == 0) {
		carry = true;
	    } else {
		carry = false;
	    }
	    ++i;
	}

	while (i < len1) {
	    result[i] = num1[i];
	    ++i;
	}
	//Array is ended but we still have overflow
	if (carry) {
	    result[i] = 1;
	}

	return result;

    }

    public static int[] subtract(int[] big, int bigLen, int[] little, int litLen) {
	int result[] = new int[bigLen];
	long difference = 0;

	// Subtract common parts of both numbers
	int i = 0;
	while (i < litLen) {
	    difference = (big[i] & Util.LONG_MASK) -
		    (little[i] & Util.LONG_MASK) +
		    (difference >> 32);
	    result[i] = (int) difference;
	    ++i;
	}

	// Subtract remainder of longer number while borrow propagates
	boolean borrow = (difference >> 32 != 0);

	while (i < bigLen && borrow) {
	    result[i] = big[i] - 1;

	    if (result[i] == -1) {
		borrow = true;
	    } else {
		borrow = false;
	    }
	    ++i;
	}

	// Copy remainder of longer number
	while (i < bigLen) {
	    result[i] = big[i++];
	}

	return result;
    }

    public static int[] simpleMul(int[] array1, int len1, int[] array2, int len2) {

	int[] z = new int[len1 + len2];

	long carry = 0;

	int j = 0;

	for (; j < len1; ++j) {
	    long product = (array2[0] & Util.LONG_MASK) *
		    (array1[j] & Util.LONG_MASK) +
		    carry;
	    z[j] = (int) product;
	    carry = product >>> 32;
	}
	z[j] = (int) carry;

	int i = 1;

	for (; i < len2; ++i) {
	    carry = 0;
	    for (j = 0; j < len1; ++j) {
		long product = (array2[i] & Util.LONG_MASK) *
			(array1[j] & Util.LONG_MASK) +
			(z[i + j] & Util.LONG_MASK) +
			carry;
		z[i + j] = (int) product;
		carry = product >>> 32;
	    }
	    z[i + j] = (int) carry;
	}
	return z;
    }

    public static byte[] mulFFT(byte[] array1, byte[] array2) {
	Complex[] a = Convert.complexFrom(array1);
	Complex[] b = Convert.complexFrom(array2);

	Complex[] result = Fourier2.fftMultiply(a, b);

	return Convert.byteFrom(result);
    }

    public static byte[] mulFFT2(byte[] array1, byte[] array2) {
	int len = Fourier.extendLength(array1.length, array2.length);
	Complex[] a = Convert.complexFrom(array1, len);
	Complex[] b = Convert.complexFrom(array2, len);

	Complex[] afft = Fourier.iterativeFFT(a, len, false);
	Complex[] bfft = Fourier.iterativeFFT(b, len, false);

	Complex[] cfft = new Complex[len];
	for (int i = 0; i < len; ++i) {
	    cfft[i] = afft[i].mul(bfft[i]);
	}

	Complex[] c = Fourier.iterativeFFT(cfft, len, true);

	return Convert.byteFrom(c);
    }

    public static int[] div(int[] number, int numLen, int[] divider, int dividerLen) {
	return null;
    }
}
