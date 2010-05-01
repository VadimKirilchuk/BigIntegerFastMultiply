/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bignumberslibrary.fourier.ntt;

import bignumberslibrary.fourier.Fourier;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NTT {

    public static int[] convolute(int[] array1, int[] array2, int length, int modulus) {
        ModulusMath modulusMath = new ModulusMath(modulus);

        int[] firstConvolution = NTT.iterativeFNT(array1, array1.length, length, false, modulusMath);
        int[] secondConvolution = NTT.iterativeFNT(array2, array2.length, length, false, modulusMath);

        int[] result = new int[length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = modulusMath.multiply(firstConvolution[i], secondConvolution[i]);
        }

        return NTT.inverseIterativeFNT(result, length, modulusMath);
    }

    private static int[] iterativeFNT(int[] f, int len, int extLength, boolean reverseNTT, ModulusMath mm) {
        int length = extLength;           // Transform length2 n

        int w = mm.getForwardNthRoot(mm.getPrimitiveRoot(), length);     // Forward n:th root
        int[] wTable = createWTable(w, length, mm);

        return forwardFNT(f, len, wTable, length, mm);
    }

    private static int[] inverseIterativeFNT(int[] array, int length, ModulusMath mm) {
        int length2 = length;  // Transform length n


        int w = mm.getInverseNthRoot(mm.getPrimitiveRoot(), length2);     // Inverse n:th root
        int[] wTable = createWTable(w, length2, mm);

        array = backwardFNT(array, wTable, mm);
        return divideElements(array, length2, mm);
    }

    /**
     * Forward (Sande-Tukey) fast Number Theoretic Transform.
     * Data length2 must be a power of two.
     *
     * @param arrayAccess The data array to transform.
     * @param wTable Table of powers of n:th root of unity <code>w</code> modulo the current modulus.
     * @param permutationTable Table of permutation indexes, or <code>null</code> if the data should not be permuted.
     */
    private static int[] forwardFNT(int[] array, int arrayRealLen, int[] wTable, int extLength, ModulusMath mm) {
        int nn;
        int istep;
        int mmax;
        int r;

        int[] result = new int[extLength];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        //bitReverseCopy(array, arrayRealLen, result, extLength);

        nn = extLength;

        if (nn < 2) {
            throw new IllegalArgumentException("Too small length for FNT");
        }

        r = 1;
        mmax = nn >> 1;
        while (mmax > 0) {
            istep = mmax << 1;

            // Optimize first step when wr = 1

            for (int i = 0; i < nn; i += istep) {
                int j = i + mmax;
                int a = result[i];
                int b = result[j];
                result[i] = mm.add(a, b);
                result[j] = mm.subtract(a, b);
            }

            int t = r;

            for (int m = 1; m < mmax; m++) {
                for (int i = m; i < nn; i += istep) {
                    int j = i + mmax;
                    int a = result[i];
                    int b = result[j];
                    result[i] = mm.add(a, b);
                    result[j] = mm.multiply(wTable[t], mm.subtract(a, b));
                }
                t += r;
            }
            r <<= 1;
            mmax >>= 1;
        }

        return result;
    }

    /**
     * Inverse (Cooley-Tukey) fast Number Theoretic Transform.
     * Data length2 must be a power of two.
     *
     * @param arrayAccess The data array to transform.
     * @param wTable Table of powers of n:th root of unity <code>w</code> modulo the current modulus.
     * @param permutationTable Table of permutation indexes, or <code>null</code> if the data should not be permuted.
     */
    private static int[] backwardFNT(int[] array, int[] wTable, ModulusMath mm) {

        int nn;
        int istep;
        int mmax;
        int r;

        int[] result = new int[array.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = array[i];
        }


        nn = array.length;

        r = nn;
        mmax = 1;
        while (nn > mmax) {
            istep = mmax << 1;
            r >>= 1;

            // Optimize first step when w = 1

            for (int i = 0; i < nn; i += istep) {
                int j = i + mmax;
                int wTemp = result[j];
                result[j] = mm.subtract(result[i], wTemp);
                result[i] = mm.add(result[i], wTemp);
            }

            int t = r;

            for (int m = 1; m < mmax; m++) {
                for (int i = m; i < nn; i += istep) {
                    int j = i + mmax;
                    int wTemp = mm.multiply(wTable[t],result[j]);
                    result[j] = mm.subtract(result[i], wTemp);
                    result[i] = mm.add(result[i], wTemp);
                }
                t += r;
            }
            mmax = istep;
        }

        return result;
    }

    private static int[] divideElements(int[] array, int divisor, ModulusMath mm) {
        int inverseFactor = mm.divide(1, divisor);

        int length = array.length;
        int[] result = new int[length];

        for (int i = 0; i < length; i++) {
            result[i] = mm.multiply(array[i], inverseFactor);
        }

        return result;
    }

    /**
     * Create a table of powers of n:th root of unity.
     *
     * @param w The n:th root of unity modulo the current modulus.
     * @param n The table length2 (= transform length2).
     *
     * @return Table of <code>table[i]=w<sup>i</sup> mod m</code>, i = 0, ..., n-1.
     */
    public static final int[] createWTable(int w, int n, ModulusMath mm) {
        int[] wTable = new int[n];
        int wTemp = 1;

        for (int i = 0; i < n; i++) {
            wTable[i] = wTemp;
            wTemp = mm.multiply(wTemp, w);
        }

        return wTable;
    }

    /**
     * Round up to nearest power of two.
     *
     * @param length2 The input value, which must be non-negative and not greater than 2<sup>30</sup>.
     *
     * @return <code>length2</code> rounded up to the nearest power of two.
     */
    private static int round2up(int length) {

        if (length == 0) {
            return 0;
        }

//        if (Fourier.is2power(length)) {
//            return length;
//        }

        int power2Length = 1;

        while (power2Length < length) {
            power2Length = power2Length << 1;
        }

        return power2Length;
    }

    public static int getTransformLength(int currentLength) {
        return round2up(currentLength);
    }
}
