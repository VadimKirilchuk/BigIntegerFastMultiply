
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.bignumberslibrary;

import java.math.BigInteger;
import java.util.Random;

/**
 *
 * @author chibis
 */
public class Test {
   
    private Test(){}
    
    public static void run(){
       
	byte[] byteArray1 = {9,-115,-107};
	byte[] byteArray2 = {48,-123,-127};
	
	BigNumber bn1 = new BigNumber(byteArray1);
	BigNumber bn2 = new BigNumber(byteArray2);
	BigInteger bi1=new BigInteger(byteArray1);
	BigInteger bi2=new BigInteger(byteArray2);

	byte[] res1={0};
	byte[] res2={0};
	//////////end of determination
	

	////////////Sub of two positive numbers
	BigNumber resultBn = bn1.sub(bn2).negate();
	BigInteger resultBi=bi1.subtract(bi2);
	
	res1=resultBn.toByteArray();
	res2 = resultBi.toByteArray();
        System.out.println("");
	System.out.println("");
	for (int i = 0; i < Math.min(res1.length,res2.length); i++) {
	    System.out.println(res1[i]+"_"+res2[i]);
	}
    
	    
    }
    

    public static void mulFFT(){
	//byte[] byteArray1 = {1,-3};
	//byte[] byteArray2 = {1,-1};
	
	byte[] byteArray1 = {127,127,127,127,127,127,127,127};
	byte[] byteArray2 = {127,127,127,127,127,127,127,127};
	
	BigNumber bn1 = new BigNumber(byteArray1);
	BigNumber bn2 = new BigNumber(byteArray2);
	BigInteger bi1=new BigInteger(byteArray1);
	BigInteger bi2=new BigInteger(byteArray2);

        byte[] clearByteArray = bn1.mulFFT(bn2).toByteArray();
	byte[] clearByteArray2=bn1.mulFFT2(bn2).toByteArray();
	
	BigInteger resultBi=bi1.multiply(bi2);
	byte[] resBiArray = Util.cutLeadingZero(resultBi.toByteArray());
 /*
	for (int i = 0; i < Math.min(resBiArray.length,clearByteArray.length); i++) {
	    if(clearByteArray[i]!=resBiArray[i]) System.out.print("!!!");
	    System.out.println("FFT="+clearByteArray[i]+" Bint="+resBiArray[i]);	    
	}
*/
	for (int i = 0; i < Math.min(clearByteArray.length,clearByteArray2.length); i++) {
	    if(clearByteArray[i]!=clearByteArray2[i]) System.out.print("!!!");
	    System.out.println("FFT="+clearByteArray[i]+" Bint="+clearByteArray2[i]);	    
	}	
    }    
    
}
