/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.bignumberslibrary;

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

    //В бигинтеджере 
    // непонятно как так получается
    // (-4 -4 )=0000 0011 0000 0100
    //т.е первая четвёрка перевелась не так как вторая...
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
}
