package org.amse.vadim.interpretator;

/**
 * Identificator lexema
 */
public class IdLexema extends Lexema {
    // identificator name
    public final String name;
    // constructor
    public IdLexema(String name) {
	super(Lexema.Type.IDENT);
	this.name = name;
    }
}
