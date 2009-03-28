
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
    
    public static void run2(){
        ///////determination
	Random rnd = new Random();
	int N1 = rnd.nextInt(Short.MAX_VALUE);
	int N2 = rnd.nextInt(Short.MAX_VALUE);
	
	//byte[] byteArray1 = {-40,1};
	//byte[] byteArray2 = {-97,2};

	byte[] byteArray1 = new byte[2];
	byte[] byteArray2 = new byte[2];

	for (int i = 0; i < byteArray1.length; i++) {
	    byteArray1[i]=(byte)(rnd.nextInt(Byte.MAX_VALUE)-2*rnd.nextInt(Byte.MAX_VALUE));
	}
	
	for (int i = 0; i < byteArray2.length; i++) {
	    byteArray2[i] = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.nextInt(Byte.MAX_VALUE));
	}
	
	
	for (int i = 0; i < byteArray1.length; i++) {
	    System.out.print(byteArray1[i]+"_");
	}	
	System.out.println();
	for (int i = 0; i < byteArray2.length; i++) {
	    System.out.print(byteArray2[i]+"_");
	}	
	System.out.println();
	BigNumber bn1 = new BigNumber(byteArray1);
	BigNumber bn2 = new BigNumber(byteArray2);
	BigInteger bi1=new BigInteger(byteArray1);
	BigInteger bi2=new BigInteger(byteArray2);

	byte[] res1;
	byte[] res2;
	//////////end of determination
	

	////////////Summ of two positive numbers
	BigNumber resultBn = bn1.add(bn2);
        res1=resultBn.toByteArray();
	
	BigInteger resultBi=bi1.add(bi2);
	res2=resultBi.toByteArray();	
	
	System.out.println();
	for (int i = 0; i < res1.length; i++) {
	    //if(res1[i]!= res2[i])
		System.out.print(res1[i]+ "_");
	}
	System.out.println();
	for (int i = 0; i < res2.length; i++) {
	    //if(res1[i]!= res2[i])
		System.out.print(res2[i]+ "_");
	}
    }    
    
    public static void run(){
	int[] src = {Integer.MAX_VALUE,0};
	
	src=Util.shift(src, src.length, 4);
	
	System.out.println(src[0]);
	System.out.println(src[1]);
    }
	       
}
