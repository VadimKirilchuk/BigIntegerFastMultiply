/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.interpretator;

import java.util.Iterator;
import java.util.Map;
import org.amse.vadim.bignumberslibrary.BigNumber;

/**
 *
 * @author chibis
 */
public class Unary implements Expression {

    private final Expression operand;
    private final String operator; //only "-" supported

    public Unary(String operation, Expression operand) {
	this.operator = operation;
	this.operand = operand;
    }

    public Expression getOperand() {
	return this.operand;
    }

    public String getOperator() {
	return operator;
    }
    
    @Override
    public boolean equals(Object o) {
	// two binary are equals if
	// operands and operations are equal.
	if (!(o instanceof Unary)) {
	    return false;
	//class cast object to Binary
	}
	Unary b = (Unary) o;

	return this.operator.equals(b.operator) &&
		this.operand.equals(b.operand);
    }
    
    @Override
    public int hashCode() {
	return 19 * this.operand.hashCode() + 37 * operator.hashCode();
    }
       
    public Expression dash(Variable v) {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    public BigNumber evaluate(Map<Variable, Constant> context) {
	// evaluating meanings of left and right operands
	BigNumber val = operand.evaluate(context);	

	if (operator.equals("-")) {
	    return val.negate();
	}
	
	throw new IllegalArgumentException();
    }

    public Iterator<Variable> iterator() {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
	return "(" + operator + operand + ")";
    }
}
