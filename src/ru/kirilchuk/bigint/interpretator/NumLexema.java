package ru.kirilchuk.bigint.interpretator;

import ru.kirilchuk.bigint.BigInteger;

/**
 * Number lexema.
 * An int value.
 */
public class NumLexema extends Lexema {
    // the meaning of number
    public final BigInteger value;

    // constructor
    public NumLexema(String value) {
	super(Lexema.Type.NUMBER);
	this.value = new BigInteger(value);
    }
}
