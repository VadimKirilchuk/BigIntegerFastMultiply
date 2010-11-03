package ru.kirilchuk.bigint.util;

/**
 * Utility class for transformations such as Fourier transform and Hartley transform.
 *
 * @author Kirilchuk V.E.
 */
public class TransformUtilities {

    private TransformUtilities(){}

    public final static int extendedLength(int length1, int length2) {
        int n = length1 + length2;

        if (is2power(n)) {
            return n;
        } else {
            n |= n >> 1;
            n |= n >> 2;
            n |= n >> 4;
            n |= n >> 8;
            n |= n >> 16;
            return n + 1;
        }
    }

    //assumes that a is positive
    public final static boolean is2power(int a) {
        return ((a & (a - 1)) == 0);
    }
}
