/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.bignumberslibrary;

import java.math.BigInteger;

/**
 *
 * @author Chibis
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws Exception {
	//TestBasePerf.run(1024,6, 70,200);
	//TestBigNumberPerf.runAll();
	//Test.mulFFT();
	//Test.run3();
	//new View();
	//TestBigNumberPerf.runAll();
	int[] ar = {1,65536};
	BigNumber bn = new BigNumber(ar);
	int[] res=bn.shiftLeft(96).getArrayOfBigNumber();
	for (int i = 0; i < res.length; i++) {
	    System.out.println(res[i]);
	}
    }
}