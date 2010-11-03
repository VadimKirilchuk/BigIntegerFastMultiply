package ru.kirilchuk.bigint.interpretator;

/**
 * Class for representation of constant
 * which implements Expression.
 * Only integer constants are supported
 */
import java.util.Iterator;
import java.util.Map;
import ru.kirilchuk.bigint.BigInteger;

public class Constant implements Expression, Comparable<Constant> {

    private final BigInteger value;    // More often used constants
    public final static Constant ZERO = new Constant(BigInteger.ZERO);
    public final static Constant ONE = new Constant(new BigInteger("1"));

    // Constructor of constant
    public Constant(BigInteger value) {
	this.value = value;
    }
    // function to get value of constant
    public BigInteger getValue() {
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
    @Override
    public boolean equals(Object o) {
	return (o instanceof Constant) && (value == ((Constant) o).value);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    @Override
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

    public BigInteger evaluate(Map<Variable, Constant> context) {
	return value;
    }
}