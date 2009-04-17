/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.bignumberslibrary;

/**
 *
 * @author chibis
 */
public class Convert {

    private Convert() {
    }
    //Convert from byteArray to intArray without leading zeroes.
    public static int[] intFrom(byte[] src) {
	int keep;

	// Find first nonzero byte
	for (keep = src.length - 1; keep >= 0 && src[keep] == 0; keep--) {
	    ;
	}

	// Allocate new array 
	int intArrayLength = keep / 4 + 1;
	int[] result = new int[intArrayLength];

	//pointer to higher byte
	int b = keep;

	//filling intArray
	for (int i = 0; i < intArrayLength; ++i) {
	    result[i] = (src[keep - b] & 0xff);
	    b--;
	    int bytesRemaining = b + 1;
	    int bytesToTransfer = Math.min(3, bytesRemaining);
	    for (int j = 0; j < bytesToTransfer; ++j) {
		//reversing bytes in int from leftright to rightleft
		int buff = src[keep - b];
		--b;
		result[i] += ((buff & 0xff) << 8 * (j + 1));
	    }
	}
	return result;
    }

    public static byte[] byteFrom(int[] src, int length) {
	int byteArrayLen = length * 4;
	byte[] byteArray = new byte[byteArrayLen];

	for (int i = 0; i < length; ++i) {
	    for (int j = 0; j < 4; ++j) {
		byteArray[i * 4 + j] = (byte) (src[i] >>> 8 * j);
	    }
	}
	return Util.cutLeadingZero(byteArray);
    }
    
    public static short[] shortFrom(int[] src, int length) {
	int shortArrayLen = length * 2;
	short[] shortArray = new short[shortArrayLen];

	for (int i = 0; i < length; ++i) {
	    for (int j = 0; j < 2; ++j) {
		shortArray[i * 2 + j] = (short) (src[i] >>> 16 * j);
	    }
	}
	return shortArray;
    }    

    public static Complex[] complexFrom(byte[] src) {
	int len = src.length;
	Complex[] result = new Complex[len];
	for (int i = 0; i < len; i++) {
	    result[i] = new Complex(src[i] & 0xFF);
	}
	return result;
    }

    public static Complex[] complexFrom(byte[] src, int length) {
	int len = length;
	Complex[] result = new Complex[len];

	int srcLen = src.length;
	for (int i = 0; i < len; i++) {
	    if (i < srcLen) {
		result[i] = new Complex(src[i] & 0xFF);
	    } else {
		result[i] = new Complex();
	    }
	}

	return result;
    }

    public static Complex[] complexFrom(short[] src, int length) {
	int len = length;
	Complex[] result = new Complex[len];

	int srcLen = src.length;
	for (int i = 0; i < len; i++) {
	    if (i < srcLen) {
		result[i] = new Complex(src[i] & 0xFFFF);
	    } else {
		result[i] = new Complex();
	    }
	}

	return result;
    }    
    
    public static byte[] byteFrom(Complex[] src) {
	int len = src.length;

	byte[] result = new byte[len];

	int carry = 0;

	for (int i = 0; i < len; i++) {
	    int buff = (int) Math.round(src[i].re() + carry);
	    result[i] = (byte) buff;
	    carry = (buff >>> 8);
	}
	return Util.cutLeadingZero(result);
    }
    
    public static short[] shortFrom(Complex[] src) {
	int len = src.length;

	short[] result = new short[len];

	long carry = 0;
	
	for (int i = 0; i < len; i++) {
	    long buff =  Math.round(src[i].re() + carry);
	    result[i] = (short) buff;
	    carry = (buff >>> 16);
	}
	return result;
    }
    
    public static byte[] byteFrom(short[] src) {
	int byteArrayLen = src.length * 2;
	byte[] byteArray = new byte[byteArrayLen];

	for (int i = 0; i < src.length; ++i) {
	    for (int j = 0; j < 2; ++j) {
		byteArray[i * 2 + j] = (byte) (src[i] >>> 8 * j);
	    }
	}
	return Util.cutLeadingZero(byteArray);
    }    
}
