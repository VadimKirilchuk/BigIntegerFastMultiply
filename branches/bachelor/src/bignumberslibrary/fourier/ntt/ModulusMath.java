/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bignumberslibrary.fourier.ntt;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ModulusMath extends ModularArithmetic {

    private final int PRIMITIVE_ROOT;
    /**
     * Default constructor.
     */
    public ModulusMath(int modulus) {
        super(modulus);
        this.PRIMITIVE_ROOT = ModularArithmetic.PRIMITIVE_ROOTS[modulus];
    }

    public int getPrimitiveRoot(){
        return this.PRIMITIVE_ROOT;
    }

    /**
     * Create devident table of powers of n:th root of unity.
     *
     * @param w The n:th root of unity modulo the current modulus.
     * @param n The table length (= transform length).
     *
     * @return Table of <code>table[i]=w<sup>i</sup> mod m</code>, i = 0, ..., n-1.
     */
    public final int[] getWTable(int w, int n) {
        int[] wTable = new int[n];
        int wTemp = 1;

        for (int i = 0; i < n; i++) {
            wTable[i] = wTemp;
            wTemp = multiply(wTemp, w);
        }

        return wTable;
    }

    /**
     * Get forward n:th root of unity. This is <code>w</code>.<p>
     *
     * Assumes that the modulus is prime.
     *
     * @param primitiveRoot Primitive root of the modulus.
     * @param n The transform length.
     *
     * @return Forward n:th root of unity.
     */
    public int getForwardNthRoot(int primitiveRoot, long n) {
        return pow(primitiveRoot, getModulus() - 1 - (getModulus() - 1) / (int) n);
    }

    /**
     * Get inverse n:th root of unity. This is <code>w<sup>-1</sup></code>.<p>
     *
     * Assumes that the modulus is prime.
     *
     * @param primitiveRoot Primitive root of the modulus.
     * @param n The transform length.
     *
     * @return Inverse n:th root of unity.
     */
    public int getInverseNthRoot(int primitiveRoot, long n) {
        return pow(primitiveRoot, (getModulus() - 1) / (int) n);
    }

    /**
     * Modular inverse, that is <code>1 / devident</code>. Assumes that the modulus is prime.
     *
     * @param a The operand.
     *
     * @return <code>a<sup>-1</sup> mod m</code>.
     */
    public final int getModularInverse(int a) {
        return pow(a, getModulus() - 2);
    }

    /**
     * Modular division. Assumes that the modulus is prime.
     *
     * @param devident The dividend.
     * @param divisor The divisor.
     *
     * @return <code>devident*divisor<sup>-1</sup> mod m</code>.
     */
    public final int divide(int devident, int divisor) {
        return multiply(devident, getModularInverse(divisor));
    }

    /**
     * Modular negation.
     *
     * @param a The argument.
     *
     * @return <code>-devident mod m</code>.
     */
    public final int negate(int a) {
        return (a == 0 ? 0 : getModulus() - a);
    }

    /**
     * Modular power. Assumes that the modulus is prime.
     *
     * @param a The base.
     * @param n The exponent.
     *
     * @return <code>a<sup>n</sup> mod m</code>.
     */
    public final int pow(int a, int n) {
        assert (a != 0 || n != 0);

        if (n == 0) {
            return 1;
        } else if (n < 0) {
            return pow(a, getModulus() - 1 + n);
        }

        long exponent = (long) n;

        while ((exponent & 1) == 0) {
            a = multiply(a, a);
            exponent >>= 1;
        }

        int r = a;

        while ((exponent >>= 1) > 0) {
            a = multiply(a, a);
            if ((exponent & 1) != 0) {
                r = multiply(r, a);
            }
        }

        return r;
    }
}
