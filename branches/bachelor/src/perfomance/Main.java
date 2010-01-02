/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package perfomance;

/**
 *
 * @author chibis
 */
public class Main {
    public static void main(String[] args) {
	int startDim = 1024 ;
	int mulToEnd = 4;
	int trustPercent = 80;	
	int maximumIterations = 100;
	
	TestBasePerf.run(startDim, mulToEnd, trustPercent, maximumIterations);
    }
}
