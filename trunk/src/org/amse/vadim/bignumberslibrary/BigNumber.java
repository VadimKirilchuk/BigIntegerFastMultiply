/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.bignumberslibrary;

import java.math.BigInteger;

public class BigNumber {

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
	    //Нам нужен реверс, когда мы объявляем бигнам, но не нужен в 
	    //возвратах операций сложения,выччитания, умножения и т.д
	    if(reverse){
		this.intArray=Convert.intFrom(Util.reverseArray(array));
	    }else{
		this.intArray=Convert.intFrom(array);
	    }
	    this.length = this.intArray.length;
	    this.sign = (sign > 0 ? 1 : -1);
	}
    }    
    //Constructor from byteArray with SIGN
    //reverse=true
    public BigNumber(byte[] array, int sign) {
	this(array,sign,true);
    }

    //Constructor from byteArray  with sign>=0
    //С поддержкой дополнительного кода
    public BigNumber(byte[] array) {
	if (array == null || array.length == 0) {
	    this.sign = 0;
	    this.intArray = null;
	    this.length = 0;
	} else if(array[0]<0){
	    this.sign=-1;
	    this.intArray = Convert.intFrom(Util.reverseArray(Util.reverseCode(array)));
	} else {
	    this.sign=1;
	    this.intArray = Convert.intFrom(Util.reverseArray(array));
	}
	    this.length = this.intArray.length;
    }

    //MAIN Constructor from intArray with SIGN    
    //Нет реверса, т.к у бигинта нет конструкторов из байтов
    //соответственно не заботимся о совпадении.
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

	int cmp =Util.compareArrays(this.intArray, this.length, bnum.intArray, bnum.length);

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
	byte[] a=this.toByteArray(false);
	byte[] b=bnum.toByteArray(false);
	
	byte[] result = Operations.mulFFT(a,b);

	return new BigNumber(result, this.sign * bnum.sign, false);
    }

    public BigNumber div(BigNumber bnum) throws Exception {
	if (bnum.sign==0) throw new Exception("Divide by zero Exception");
	if (this.sign==0) return new BigNumber();
	if (bnum.length>this.length) return new BigNumber();
	if (bnum.length==1) {
	    int[] result = this.intArray;
            int p = bnum.intArray[0];
	    for (int i = 0; i < this.length ; ++i) {
		//result[i]=result[i]/p;
	    }
	    return new BigNumber(result,1);
	}
	return new BigNumber(Operations.div(this.intArray,this.length,bnum.intArray,bnum.length),this.sign*bnum.sign);
    }

    public BigNumber mulFFT2(BigNumber bnum) {
	if (sign == 0 || bnum.sign == 0) {
	    return new BigNumber();
	}
        
	//Doing fourier transform in bytes
	byte[] a=this.toByteArray(false);
	byte[] b=bnum.toByteArray(false);
	
	byte[] result = Operations.mulFFT2(a,b);

	return new BigNumber(result, this.sign * bnum.sign, false);
    }
	
    ///////////////////////End of operations//////////////////////////////////
    @Override
    public boolean equals(Object obj) {
	if (obj == this) {
	    return true;
	}
	if (!(obj instanceof BigNumber)) {
	    return false;
	}
	BigNumber bnum = (BigNumber) obj;

	if (bnum.sign != this.sign || bnum.length != this.length) {
	    return false;
	}
	for (int i = 0; i < this.length; i++) {
	    if (bnum.intArray[i] != this.intArray[i]) {
		return false;
	    }
	}
	return true;
    }
    
    public int compare(BigNumber bnum){
	int cmp = Util.compareArrays(this.intArray, this.length, bnum.intArray, bnum.length);
	return cmp;
    }

    public int getSign() {
	return sign;
    }

    //Just changing sign(In BigInteger it changes elements(a -> -a+1))
    public BigNumber negate() {
	//return new BigNumber(this.intArray, (-1) * sign);
	int[] result = Util.reverseCode(this.intArray,this.length);
	return new BigNumber(result,-this.sign);
    }

    public int[] getArrayOfBigNumber() {
	return this.intArray;
    }
    //////////////////Вспомогательные функции////////////////////////
    
    private byte[] toByteArray(boolean reverse) {
	int byteArrayLen = this.length * 4;
	byte[] byteArray = new byte[byteArrayLen];

	for (int i = 0; i < this.length; ++i) {
	    for (int j = 0; j < 4; ++j) {
		byteArray[i * 4 + j] = (byte) (this.intArray[i] >>> 8 * j);
	    }
	}
	
	if(reverse){
	    return Util.reverseArray(Util.cutLeadingZero(byteArray));
	}else{
	    return Util.cutLeadingZero(byteArray);
	}
    }
    //По умолчанию переворачиваем, т.к как храним наоборот.
    public byte[] toByteArray() {
	return this.toByteArray(true);
    }
    
}
