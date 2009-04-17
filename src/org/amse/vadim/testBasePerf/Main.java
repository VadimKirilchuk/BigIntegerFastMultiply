/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.vadim.testBasePerf;

/**
 *
 * @author chibis
 */
public class Main {
    public static void main(String[] args) {
	//startDim is in byte! Recommended more than 1024
	//mulToEnd - how much points. (startDim*2,startDim*4,..startDim*2^mulToEnd) 
	//trustPercent - if nu<(maximumIterations-trustDispersion)/maximumIterations the result is true.
	int startDim=1024;
	int mulToEnd=2;
	int trustPercent=80;
	int maximumIterations=100;
	
	TestBasePerf.run(startDim, mulToEnd, trustPercent, maximumIterations);
    }
}
