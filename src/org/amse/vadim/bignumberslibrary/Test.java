
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

    private Test() {
    }

    public static void testSimpleDiv() throws Exception{
	Random rnd = new Random();
	
	byte[] byteArray1 = new byte[10];
	byte[] byteArray2 = new byte[1];

	for (int i = 0; i < byteArray1.length; i++) {
	    byteArray1[i] = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.nextInt(Byte.MAX_VALUE));
	}

	for (int i = 0; i < byteArray2.length; i++) {
	    byteArray2[i] = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.nextInt(Byte.MAX_VALUE));
	}
	
	BigNumber bn1 = new BigNumber(byteArray1);
	BigNumber bn2 = new BigNumber(byteArray2);
	BigInteger bi1 = new BigInteger(byteArray1);
	BigInteger bi2 = new BigInteger(byteArray2);
	
	byte[] result1 = bi1.divide(bi2).toByteArray();
	for (int i = 0; i < result1.length; i++) {
	    System.out.print(result1[i]+"_");
	}
	System.out.println();
	byte[] result2 = bn1.div(bn2).toByteArray();
	for (int i = 0; i < result2.length; i++) {
	    System.out.print(result2[i]+"_");
	}
    }
   
    public static void run2() {
	///////determination
	Random rnd = new Random();
	int N1 = rnd.nextInt(Short.MAX_VALUE);
	int N2 = rnd.nextInt(Short.MAX_VALUE);

	//byte[] byteArray1 = {-34,-96};
	//byte[] byteArray2 = {95,44};

	byte[] byteArray1 = new byte[2];
	byte[] byteArray2 = new byte[2];

	for (int i = 0; i < byteArray1.length; i++) {
	    byteArray1[i] = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.nextInt(Byte.MAX_VALUE));
	}

	for (int i = 0; i < byteArray2.length; i++) {
	    byteArray2[i] = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.nextInt(Byte.MAX_VALUE));
	}


	for (int i = 0; i < byteArray1.length; i++) {
	    System.out.print(byteArray1[i] + "_");
	}
	System.out.println();
	for (int i = 0; i < byteArray2.length; i++) {
	    System.out.print(byteArray2[i] + "_");
	}
	System.out.println();
	BigNumber bn1 = new BigNumber(byteArray1);
	BigNumber bn2 = new BigNumber(byteArray2);
	BigInteger bi1 = new BigInteger(byteArray1);
	BigInteger bi2 = new BigInteger(byteArray2);

	byte[] res1;
	byte[] res2;
	//////////end of determination


	////////////Summ of two positive numbers
	BigNumber resultBn = bn1.add(bn2);
	res1 = resultBn.toByteArray();

	BigInteger resultBi = bi1.add(bi2);
	res2 = resultBi.toByteArray();

	System.out.println();
	for (int i = 0; i < res1.length; i++) {
	    //if(res1[i]!= res2[i])
	    System.out.print(res1[i] + "_");
	}
	System.out.println();
	for (int i = 0; i < res2.length; i++) {
	    //if(res1[i]!= res2[i])
	    System.out.print(res2[i] + "_");
	}
    }

    public static void run3() {
	///////determination
	Random rnd = new Random();
	int N1 = rnd.nextInt(Byte.MAX_VALUE*2);
	int N2 = rnd.nextInt(Byte.MAX_VALUE*2);
        
	//int[] intArray1 = {256023456};
	//int[] intArray2 = {256045645};
	
	int[] intArray1 = new int[N1];
	int[] intArray2 = new int[N2];

	for (int i = 0; i < intArray1.length; i++) {
	    intArray1[i] = (int) (rnd.nextInt(Integer.MAX_VALUE) - 2 * rnd.nextInt(Integer.MAX_VALUE));
	}

	for (int i = 0; i < intArray2.length; i++) {
	    intArray2[i] = (int) (rnd.nextInt(Integer.MAX_VALUE) - 2 * rnd.nextInt(Integer.MAX_VALUE));
	}
         
	System.out.println(intArray1[0]);
	System.out.println(intArray2[0]);
	System.out.println();
	
	byte[] byteArray1 = Convert.byteFrom(intArray1, intArray1.length);
	byte[] byteArray2 = Convert.byteFrom(intArray2, intArray2.length);

	short[] shortArray1 = Convert.shortFrom(intArray1, intArray1.length);
	short[] shortArray2 = Convert.shortFrom(intArray2, intArray2.length);
	
	for (int i = 0; i < byteArray1.length; i++) {
	    System.out.print(byteArray1[i] + "_");
	}
	System.out.println();
	for (int i = 0; i < byteArray2.length; i++) {
	    System.out.print(byteArray2[i] + "_");
	}
	System.out.println();
	
	for (int i = 0; i < shortArray1.length; i++) {
	    System.out.print(shortArray1[i] + "_");
	}
	System.out.println();
	for (int i = 0; i < shortArray2.length; i++) {
	    System.out.print(shortArray2[i] + "_");
	}
	System.out.println();

	byte[] res1;
	byte[] res2;
	//////////end of determination
 
	res1= Operations.mulFFT2(byteArray1, byteArray2);	
	res2= Operations.mulFFT3(shortArray1, shortArray2);
	
	System.out.println();
	for (int i = 0; i < res1.length; i++) {
	    //if(res1[i]!= res2[i])
	    System.out.print(res1[i] + "_");
	}
	System.out.println();
	for (int i = 0; i < res2.length; i++) {
	    //if(res1[i]!= res2[i])
	    System.out.print(res2[i] + "_");
	}	
    }
}
