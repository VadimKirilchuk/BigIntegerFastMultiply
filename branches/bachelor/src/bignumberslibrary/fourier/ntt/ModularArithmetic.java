/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bignumberslibrary.fourier.ntt;

/**
 * Elementary modulo arithmetic functions for <code>int</code> data.<p>
 *
 * Modular addition and subtraction are trivial, when the modNum is less
 * than 2<sup>31</sup> and overflow can be detected easily.<p>
 *
 * Modular multiplication is more complicated, and since it is usually
 * the single most time consuming operation in the whole program execution,
 * the very core of the Number Theoretic Transform (NTT), it should be
 * carefully optimized.<p>
 *
 * The obvious (but not very efficient) algorithm for multiplying two
 * <code>int</code>s and taking the remainder is<p>
 *
 * <code>(int) ((long) a * b % modNum)</code><p>
 *
 * The first observation is that since the modNum is practically
 * constant, it should be more efficient to calculate (once) the inverse
 * of the modNum, and then subsequently multiply by the inverse modNum
 * instead of dividing by the modNum.<p>
 *
 * The second observation is that to get the remainder of the division,
 * we don't necessarily need the actual result of the division (we just
 * want the remainder). So, we should discard the topmost 32 bits of the
 * full 64-bit result whenever possible, to save a few operations.<p>
 *
 * The basic approach is to get some approximation of <code>a * b / modNum</code>.
 * The approximation should be within +1 or -1 of the correct result. Then
 * calculate <code>a * b - approximateDivision * modNum</code> to get
 * the remainder. This calculation needs to use only the lowest 32 bits. As
 * the modNum is less than 2<sup>31</sup> it is easy to detect the case
 * when the approximate division was off by one (and the remainder is
 * <code>&#177;modNum</code> off).<p>
 *
 * There are different algorithms to calculate the approximate division
 * <code>a * b / modNum</code>. This implementation simply converts all
 * the operands to <code>double</code> and performs the mulciplications.
 * This requires that converting between integer types and floating point
 * types is efficient. On some platforms this may not be true; in that
 * case it can be more efficient to perform the multiplications using
 * 64-bit integer arithmetic.<p>
 *
 * To simplify the operations, we calculate the inverse modNum as
 * <code>1.0 / (modNum + 0.5)</code>. Since the modNum is assumed to be
 * prime, and a <code>double</code> has more bits for precision than an
 * <code>int</code>, the approximate result of <code>a * b / modNum</code>
 * will always be either correct or one too small (but never one too big).
 *
 * @version 1.0.2
 * @author Mikko Tommila
 * @author Kirilchuk V.E
 */
public class ModularArithmetic {

    /**
     * Moduli to be used in number theoretic transforms.
     * Allows transform lengths upto 3*2<sup>24</sup>.
     */
    public static final int MODULUS[] = {2113929217, 2013265921, 1811939329};
    /**
     * Primitive roots for the corresponding moduli.
     */
    protected static final int PRIMITIVE_ROOTS[] = {5, 31, 13};

    private int modulus;     
    private double inverseModulus;

    public ModularArithmetic(int modNum) {
        this.modulus = MODULUS[modNum];
        this.inverseModulus = this.inverseModulus = 1.0 / (this.modulus + 0.5);// Round down
    }

    public int getModulus() {
        return modulus;
    }

    /**
     * Modular multiplication.
     *
     * @param a First operand.
     * @param b Second operand.
     *
     * @return <code>a * b % modNum</code>
     */
    public final int multiply(int a, int b) {
        int r1 = a * b - (int) (this.inverseModulus * (double) a * (double) b) * this.modulus,
                r2 = r1 - this.modulus;

        return (r2 < 0 ? r1 : r2);
    }

    /**
     * Modular addition.
     *
     * @param a First operand.
     * @param b Second operand.
     *
     * @return <code>(a + b) % modNum</code>
     */
    public final int add(int a, int b) {
        int r1 = a + b,
                r2 = r1 - this.modulus;

        return (r2 < 0 ? r1 : r2);
    }

    /**
     * Modular subtraction. The result is always >= 0.
     *
     * @param a First operand.
     * @param b Second operand.
     *
     * @return <code>(a - b + modNum) % modNum</code>
     */
    public final int subtract(int a, int b) {
        int r1 = a - b,
                r2 = r1 + this.modulus;

        return (r1 < 0 ? r2 : r1);
    }
}
