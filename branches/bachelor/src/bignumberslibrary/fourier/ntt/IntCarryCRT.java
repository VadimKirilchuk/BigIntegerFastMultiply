/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bignumberslibrary.fourier.ntt;

import java.math.BigInteger;

/**
 *
 * @author Kirilchuk V.E.
 */
public class IntCarryCRT extends IntCRTMath {

    /**
     * Creates a carry-CRT object using the specified radix.
     *
     * @param radix The radix that will be used.
     */
    public IntCarryCRT(int radix) {
        super(radix);
    }

    /**
     * Calculate the final result of a three-NTT convolution.<p>
     *
     * Performs a Chinese Remainder Theorem (CRT) on each element
     * of the three result data sets to get the result of each element
     * modulo the product of the three moduli. Then it calculates the carries
     * to get the final result.<p>
     *
     * Note that the return value's initial word may be zero or non-zero,
     * depending on how large the result is.<p>
     *
     * Assumes that <code>MODULUS[0] > MODULUS[1] > MODULUS[2]</code>.
     *
     * @param resultMod0 The result modulo <code>MODULUS[0]</code>.
     * @param resultMod1 The result modulo <code>MODULUS[1]</code>.
     * @param resultMod2 The result modulo <code>MODULUS[2]</code>.
     * @param resultSize The number of elements needed in the final result.
     *
     * @return The final result with the CRT performed and the carries calculated.
     */
    public int[] carryCRT(int[] src0, int[] src1, int[] src2, int resultSize) {

        int size = src0.length;

        int[] dst = new int[resultSize];

        int[] carryResult = new int[3],
                sum = new int[3],
                tmp = new int[3];

        int i = size;
        for (; i > 0; i--) {

            int y0 = MATH_MOD_0.multiply(T0, src0[i - 1]),
                    y1 = MATH_MOD_1.multiply(T1, src1[i - 1]),
                    y2 = MATH_MOD_2.multiply(T2, src2[i - 1]);

            multiply(M12, y0, sum);
            multiply(M02, y1, tmp);

            if (add(tmp, sum) != 0 ||
                    compare(sum, M012) >= 0) {
                subtract(M012, sum);
            }

            multiply(M01, y2, tmp);

            if (add(tmp, sum) != 0 ||
                    compare(sum, M012) >= 0) {
                subtract(M012, sum);
            }

            add(sum, carryResult);

            int result = divide(carryResult);

            //if (i < resultSize) {
            dst[i-1] = result;
            //}
        }

        //dst[i] = carryResult[2];

        return dst;
    }
    private static final ModulusMath MATH_MOD_0,  MATH_MOD_1,  MATH_MOD_2;
    private static final int T0,  T1,  T2;
    private static final int[] M01,  M02,  M12,  M012;


    static {
        MATH_MOD_0 = new ModulusMath(0);
        MATH_MOD_1 = new ModulusMath(1);
        MATH_MOD_2 = new ModulusMath(2);

        // Probably sub-optimal, but it's a one-time operation

        BigInteger base = BigInteger.valueOf(Math.abs((long) MAX_POWER_OF_TWO_BASE)), // In int case the base is 0x80000000
                m0 = BigInteger.valueOf((long) ModularArithmetic.MODULUS[0]),
                m1 = BigInteger.valueOf((long) ModularArithmetic.MODULUS[1]),
                m2 = BigInteger.valueOf((long) ModularArithmetic.MODULUS[2]),
                m01 = m0.multiply(m1),
                m02 = m0.multiply(m2),
                m12 = m1.multiply(m2);

        T0 = m12.modInverse(m0).intValue();
        T1 = m02.modInverse(m1).intValue();
        T2 = m01.modInverse(m2).intValue();

        M01 = new int[2];
        M02 = new int[2];
        M12 = new int[2];
        M012 = new int[3];

        BigInteger[] qr = m01.divideAndRemainder(base);
        M01[0] = qr[0].intValue();
        M01[1] = qr[1].intValue();

        qr = m02.divideAndRemainder(base);
        M02[0] = qr[0].intValue();
        M02[1] = qr[1].intValue();

        qr = m12.divideAndRemainder(base);
        M12[0] = qr[0].intValue();
        M12[1] = qr[1].intValue();

        qr = m0.multiply(m12).divideAndRemainder(base);
        M012[2] = qr[1].intValue();
        qr = qr[0].divideAndRemainder(base);
        M012[0] = qr[0].intValue();
        M012[1] = qr[1].intValue();
    }
}

