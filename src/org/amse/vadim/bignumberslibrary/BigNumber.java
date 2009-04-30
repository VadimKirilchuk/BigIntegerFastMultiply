/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.bignumberslibrary;

import java.math.BigInteger;

public class BigNumber implements Comparable<BigNumber>{

    // zero-BigNumber have sign=0 and null intArray
    // younger bit is intArray[0]
    private int sign;
    private int[] intArray;
    private int length;

    //---------------------------------//
    //           CONSTRUCTORS          //
    //---------------------------------//
    //constructor from bytes with optional reverse
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
    //Constructor from byteArray with SIGN
    //reverse=true
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

	byte[] result = Operations.mulFFT(a, b);

	return new BigNumber(result, this.sign * bnum.sign, false);
    }

    public BigNumber mulFFT2(BigNumber bnum) {
	if (sign == 0 || bnum.sign == 0) {
	    return new BigNumber();
	}

	//Doing fourier transform in bytes
	byte[] a = this.toByteArray(false);
	byte[] b = bnum.toByteArray(false);

	byte[] result = Operations.mulFFT2(a, b);

	return new BigNumber(result, this.sign * bnum.sign, false);
    }
    
    public BigNumber mulFFT3(BigNumber bnum) {
	if (sign == 0 || bnum.sign == 0) {
	    return new BigNumber();
	}

	//Doing fourier transform in shorts
	short[] a = Convert.shortFrom(this.intArray,this.length);
	short[] b = Convert.shortFrom(bnum.intArray,bnum.length);

	byte[] result = Operations.mulFFT3(a, b);

	return new BigNumber(result, this.sign * bnum.sign, false);
    }
    //Not Supported yet!!!
    //Divide like "/" - not like "/ + %"
    public BigNumber div(BigNumber bnum) throws Exception{
	return new BigNumber(this.divide(bnum).getQ(),this.sign*bnum.sign);
    }
    
    private DivisionData divide(BigNumber bnum) throws Exception {
	if (bnum.sign == 0) {
	    throw new Exception("Divide by zero Exception");
	}
	if (this.sign == 0) {
	    return new DivisionData(null, null);
	}
	if (bnum.length > this.length) {
	    return new DivisionData(null, bnum.intArray);
	}
	
	DivisionData resData;
		
	if (bnum.length == 1) {
	    resData = Operations.simpleDiv(this.intArray, this.length, bnum.intArray[0]);
	    return resData;
	}
	
	//resData = Operations.div(this,bnum);
	
	return null;
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
	return new BigNumber(result,this.sign);
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
	return new BigNumber(result,this.sign);
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

    public int[] getArrayOfBigNumber() {
	int[] result = new int[this.length];
	for (int i = 0; i < result.length; i++) {
	    result[i]=this.intArray[i];
	}
	return result;
    }
    
    public int getLength(){
	return this.length;
    }
    //////////////////Вспомогательные функции////////////////////////
    private byte[] toByteArray(boolean reverse) {
	int[] array = this.intArray;
	int len = this.length;
	//leading null bytes in int
	int freeBits = 32 - Util.bitLen(array[len - 1]);
	int freeBytes = freeBits % 8 == 0 ? freeBits / 8 - 1 : freeBits / 8;

	if (this.sign == -1) {
	    array = Util.reverseInts(this.intArray);
	}

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
}
