package org.amse.vadim.interpretator;

/**
 * Number lexema.
 * An int value.
 */
public class NumLexema extends Lexema {
    // the meaning of number
    public final int value;

    // constructor
    public NumLexema(int value) {
	super(Lexema.Type.NUMBER);
	this.value = value;
    }
}
