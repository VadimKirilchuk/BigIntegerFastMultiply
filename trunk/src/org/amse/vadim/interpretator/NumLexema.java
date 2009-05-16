package org.amse.vadim.interpretator;

import org.amse.vadim.bignumberslibrary.BigNumber;

/**
 * Number lexema.
 * An int value.
 */
public class NumLexema extends Lexema {
    // the meaning of number
    public final BigNumber value;

    // constructor
    public NumLexema(String value) {
	super(Lexema.Type.NUMBER);
	this.value = new BigNumber(value);
    }
}
