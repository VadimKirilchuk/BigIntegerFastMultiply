/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

/**
 *
 * @author chibis
 */
public class DivisionByteData {
    // A / B = q + r  
    private final byte[] q;
    private final byte[] r;
    
    public  DivisionByteData(){
	this.q = null;
	this.r = new byte[1];
    }
    
    public DivisionByteData(byte[] q, byte[] r){
	this.q = q;
	this.r = r;
    }
    
    public byte[] getQ(){
	return this.q;
    }
    
    public byte[] getR(){
	return this.r;
    }
}
