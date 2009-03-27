
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
       
	byte[] byteArray1 = {-1,1};
	byte[] byteArray2 = {-4,-4};
	
	BigNumber bn1 = new BigNumber(byteArray1);
	BigNumber bn2 = new BigNumber(byteArray2);
	BigInteger bi1=new BigInteger(byteArray1);
	BigInteger bi2=new BigInteger(byteArray2);

	byte[] res1={0};
	byte[] res2={0};
	//////////end of determination

    }    
    
}
