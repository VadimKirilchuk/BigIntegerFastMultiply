package org.amse.vadim.interpretator;

/**
 * Class for representation of constant
 * which implements Expression.
 * Only integer constants are supported
 */
import java.util.Iterator;
import java.util.Map;

public class Constant implements Expression, Comparable<Constant> {

    private final int value;    // More often used constants
    public final static Constant ZERO = new Constant(0);
    public final static Constant ONE = new Constant(1);

    // Constructor of constant
    public Constant(int value) {
	this.value = value;
    }
    // function to get value of constant
    public int getValue() {
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
	return (value + 29) * 37;
    }

    public String toString() {
	return ((Integer) value).toString();
    }

    // Comparable interface methods
    public int compareTo(Constant c) {
	return value - c.value;
    }

    //  Iterable interface methods
    public Iterator<Variable> iterator() {
	return new ConstIterator();
    }

    //  Expression interface methods
    public Expression dash(Variable v) {
	return ZERO;
    }

    public int evaluate(Map<Variable, Constant> context) {
	return value;
    }
}