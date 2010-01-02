/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.vadim.bignumberslibrary;

/**
 *
 * @author chibis
 */
public class DivisionData {
    // A / B = q + r  
    private final int[] q;
    private final int[] r;
    
    public  DivisionData(){
	this.q = null;
	this.r = new int[1];
    }
    
    public DivisionData(int[] q, int[] r){
	this.q = q;
	this.r = r;
    }
    
    public int[] getQ(){
	return this.q;
    }
    
    public int[] getR(){
	return this.r;
    }
}
