package org.amse.vadim.interpretator;

/**
 * Class for representation of constant
 * which implements Expression.
 * Only integer constants are supported
 */
import java.util.Iterator;
import java.util.Map;
import org.amse.vadim.bignumberslibrary.BigNumber;

public class Constant implements Expression, Comparable<Constant> {

    private final BigNumber value;    // More often used constants
    public final static Constant ZERO = new Constant(new BigNumber());
    public final static Constant ONE = new Constant(new BigNumber("1"));

    // Constructor of constant
    public Constant(BigNumber value) {
	this.value = value;
    }
    // function to get value of constant
    public BigNumber getValue() {
	return value;
    }

    // Iterator of constant is empty iterator
    private static class ConstIterator implements Iterator<Variable> {

	public boolean hasNext() {
	    return false;
	}

	public Variable next() {
	    return null;
	}

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }

    // Object class methods
    public boolean equals(Object o) {
	return (o instanceof Constant) && (value == ((Constant) o).value);
    }

    public int hashCode() {
	return (value.getLength() + 29) * 37;
    }

    public String toString() {
	return (value.toString());
    }

    // Comparable interface methods
    public int compareTo(Constant c) {
	return value.compareTo(c.value);
    }

    //  Iterable interface methods
    public Iterator<Variable> iterator() {
	return new ConstIterator();
    }

    //  Expression interface methods
    public Expression dash(Variable v) {
	return ZERO;
    }

    public BigNumber evaluate(Map<Variable, Constant> context) {
	return value;
    }
}