package ru.kirilchuk.bigint.util;

/**
 * Utility class for some bit constants and operations.
 *
 * @author Kirilchuk V.E.
 */
public class BitUtilities {

    private BitUtilities(){}

    /**
     * This mask is used to obtain the value of an int as if it were unsigned.
     */
    public final static long LONG_MASK = 0xffffffffL;
}
