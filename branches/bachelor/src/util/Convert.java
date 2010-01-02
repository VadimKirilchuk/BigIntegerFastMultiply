/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import util.Complex;
import util.Util;

/**
 *
 * @author chibis
 */
public class Convert {

    private Convert() {
    }
    //Convert from byteArray to intArray without leading zeroes.
    //not for complement code.
    //uses in BigNumber Constructor from bytes and Fourier with byte BASE
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
    
    //making byte array from BigNumber intArray. Not to forget
    //that bigNumberArray may have more elements, that his size!!!
    //so we need the second parametr - length(how elements are real)
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
    
    //We need this method to calculate Fourier with Short BASE
    //Not to forget that BigNumber may have more elements
    //than his size. So we need second parametr - length.
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

    //This method is needed for FFT(Simple recursive with byte BASE)
    //he is not filling complex arrays by Zeroes!!!
    //so src must be 2^n length!!!
    public static Complex[] complexFrom(byte[] src) {
	int len = src.length;
	Complex[] result = new Complex[len];
	for (int i = 0; i < len; i++) {
	    result[i] = new Complex(src[i] & 0xFF);
	}
	return result;
    }

    //This method is needed for FFT2(iterative with byte BASE)
    //it`s fill complex arrays by zeroes to extend Length.
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

    //This method is needed for FFT2(iterative with short BASE)
    //it`s fill complex arrays by zeroes to extend Length.  
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
    
    //This method is to make byte array from complex array after 
    //FFT.
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
    
    //This method is to make short array from complex array after 
    //FFT.
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
    
    //This method is to make byte Array fom short array
    //cause we need bytes for BigNumber constructor
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
