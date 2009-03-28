/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.bignumberslibrary;

import java.math.BigInteger;

/**
 *
 * @author chibis
 */
public class Util {

    public final static long LONG_MASK = 0xffffffffL;

    private Util() {
    }

    public static int compareArrays(int[] array1, int array1Len, int[] array2, int array2Len) {

	if (array1Len > array2Len) {
	    return 1;
	}
	if (array1Len < array2Len) {
	    return -1;
	}
	// Argument laddengths are equal; compare the values
	for (int i = array1Len - 1; i >= 0; --i) {
	    long b1 = array1[i] & LONG_MASK;
	    long b2 = array2[i] & LONG_MASK;
	    if (b1 > b2) {
		return 1;
	    }
	    if (b1 < b2) {
		return -1;
	    }
	}
	return 0;
    }
    //Returns pos in BigNumber Array of first non zero element 

    public static int cutLeadingZero(int[] intArray, int thisLength) {
	int length = thisLength;
        //???
	//	if (length==0) return null;
	
	int keep;

	// Find first nonzero byte
	for (keep = length - 1; keep >= 0 && intArray[keep] == 0; --keep) {
	    ;
	}

	// changing length
	length = keep + 1;
	return length;
    }

    //Deleting leading zero in byte Array
    public static byte[] cutLeadingZero(byte[] array) {
	int len=array.length;
	
	int i=0;
	
	//finding first non-zero byte
	for (i = len-1;	i >=0 && array[i]==0;i--) {	
	 ;   
	}
	int newLen = i+1;
	//Length is not changed
	if (newLen==len) return array;
	
	//cut length
	byte[] result = new byte[newLen];
	
	//copy data
	for (int j = 0; j < result.length; j++) {
	     result[j]=array[j];
	}
	
	return result;
    }

    public static byte[] reverseArray(byte[] array) {
        //???
	if(array.length==1 || array.length==0) return array;
	
	byte[] result = new byte[array.length];
        
	for (int i = 0; i <= array.length / 2; ++i) {
	    result[i] = array[array.length - 1 - i];
	    result[array.length - 1 - i] = array[i];
	}

	return result;
    }
    
    public static int[] reverseArray(int[] array) {
         
	int[] result = new int[array.length];
        
	for (int i = 0; i <= array.length / 2; ++i) {
	    result[i] = array[array.length - 1 - i];
	    result[array.length - 1 - i] = array[i];
	}   

	return result;
    }

    public static byte[] reverseCode(byte[] array){
	int len = array.length;
	
	byte[] result = new byte[len];
	
	for (int i = 0; i < len ; i++) {
	    result[i]=(byte)(~(array[i]&0xff)+1);
	}
	return result;
    }

    public static byte[] makePositive(byte[] array){
	int len = array.length;
	
	int keep=0;
	// Find first non-sign (0xff) byte of input
	for ( keep=0; keep<len && array[keep]==-1; keep++){
	    ;
	}
	
	byte[] result = new byte[len-keep];
	
	for(int i=keep ;i < len;++i ){
	    result[i-keep]=(byte)(~(array[i]&0xff));
        }
	
	return result;
    }
    
    public static int[] addOne(int[] array){
	int length = array.length;
	int[] result = new int[length];
	
	for (int i = 0; i < length; i++) {
	    result[i]=array[i]+1;
	}
	
	return result;
    }
    
    public static int[] reverseInts(int[] array){
	int len = array.length;
	
	int[] result = new int[len];
	for (int i = 0; i < len; i++) {
	    result[i]=~array[i]+1;
	}
	
	return result;
    }

    /**
     * bitLen(val) is the number of bits in val.
     */
    private static int bitLen(int w) {
        // Binary search - decision tree (5 tests, rarely 6)
        return
         (w < 1<<15 ?
          (w < 1<<7 ?
           (w < 1<<3 ?
            (w < 1<<1 ? (w < 1<<0 ? (w<0 ? 32 : 0) : 1) : (w < 1<<2 ? 2 : 3)) :
            (w < 1<<5 ? (w < 1<<4 ? 4 : 5) : (w < 1<<6 ? 6 : 7))) :
           (w < 1<<11 ?
            (w < 1<<9 ? (w < 1<<8 ? 8 : 9) : (w < 1<<10 ? 10 : 11)) :
            (w < 1<<13 ? (w < 1<<12 ? 12 : 13) : (w < 1<<14 ? 14 : 15)))) :
          (w < 1<<23 ?
           (w < 1<<19 ?
            (w < 1<<17 ? (w < 1<<16 ? 16 : 17) : (w < 1<<18 ? 18 : 19)) :
            (w < 1<<21 ? (w < 1<<20 ? 20 : 21) : (w < 1<<22 ? 22 : 23))) :
           (w < 1<<27 ?
            (w < 1<<25 ? (w < 1<<24 ? 24 : 25) : (w < 1<<26 ? 26 : 27)) :
            (w < 1<<29 ? (w < 1<<28 ? 28 : 29) : (w < 1<<30 ? 30 : 31)))));
    }

    //Положительный numBits для сдвига вправо
    //Отрицательный - влево, т.к число хранится слева направо.
    public static int[] shift(int[] src,int length,int numBits){
	//Свободные биты в старшем разряде
	int freeBits = 32-bitLen(src[length-1]); 
	int[] result = new int[length];
	//Если можно сдвинуть без увеличения размера
	//if (numBits>0 && numBits< freeBits){
	    long carry=0;
	    //просто делаем сдвиг
	    for (int i = 0; i < src.length; i++) {
		int a = (int)((src[i] & Util.LONG_MASK)<< numBits);		
		result[i] =(int)(a | (carry >>32));  
		carry=(src[i] & Util.LONG_MASK) << numBits;
	    }
   
	//}
	
	return result;
    }
}
