/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.bignumberslibrary;

/**
 *
 * @author chibis
 */
public class Dispersion {

    private int count;
    private final double trustPercent;
    private final int minimumElements;
    private final int maximumIterations;
    private double M;
    private double sqrM;
    private double D;

    public Dispersion(int minimumElements, double trustPercent, int maximumIterations) {
	this.count = 0;
	this.trustPercent = trustPercent;
	this.M = 0;
	this.D = 0;
	this.minimumElements = minimumElements;
	this.maximumIterations=maximumIterations;
    }

    public boolean canTrust(int delta) {
	++count;

	//double oldM=this.M;
	this.M = (M * (count - 1) + delta) / count;

	if (count == 1) {
	    this.sqrM = this.M * this.M;
	}

	this.sqrM = (sqrM * (count - 1) + delta * delta) / count;

	this.D = this.sqrM - this.M * this.M;
	/*
	D=this.D+oldM*oldM;
	D=D*count-1;
	D=D+delta*delta;
	D=D/count;
	D=D-this.M*this.M;
	 */

	if (count < minimumElements) {
	    return false;
	}
	
	if (count==maximumIterations){
	    System.out.println("(Maximum iterations was reached");
	    if (this.getNu()<1) {
		System.out.println("trustPercent is "+ 100*(1-this.getNu())+")");
	    }else{
		System.out.println("Sequence does not converge)");		
	    }
	    return true;
	}

	double nu = Math.sqrt(D) / M;

	if (nu < (100 - this.trustPercent) / 100) {
	    return true;
	}
	return false;
    }

    public double getDispersion() {
	return D;
    }

    public double getMean() {
	return M;
    }
    
    public double getNu() {
	return (Math.sqrt(this.D))/this.M;
    }
}
