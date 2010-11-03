package ru.kirilchuk.bigint;

import ru.kirilchuk.bigint.util.TransformUtilities;

/**
 * Class that represents Hartley transform.
 * 
 * @author Kirilchuk V.E.
 */
public class Hartley {

    private final static double SQRT2       = 1.4142135623730950488016887242;
    private final static double SQRT2_DIV_2 = 0.7071067811865475244008443621;

    private final static void transform(double[] src, int transformLength, double[] sineTable, int offset) {

        // End of recursion
        if (transformLength == 8) {
            double d45, d67, sd0123, dd0123;

            {
                double ss0123, ds0123, ss4567, ds4567;

                {
                    double s01, s23, d01, d23;

                    d01    = src[0 + offset] - src[1 + offset];
                    s01    = src[0 + offset] + src[1 + offset];
                    d23    = src[2 + offset] - src[3 + offset];
                    s23    = src[2 + offset] + src[3 + offset];
                    ds0123 = (s01 - s23);
                    ss0123 = (s01 + s23);
                    dd0123 = (d01 - d23);
                    sd0123 = (d01 + d23);
                }
                {
                    double s45, s67;

                    s45    = src[4 + offset] + src[5 + offset];
                    s67    = src[6 + offset] + src[7 + offset];
                    d45    = src[4 + offset] - src[5 + offset];
                    d67    = src[6 + offset] - src[7 + offset];
                    ds4567 = (s45 - s67);
                    ss4567 = (s45 + s67);
                }
                src[4 + offset] = ss0123 - ss4567;
                src[0 + offset] = ss0123 + ss4567;
                src[6 + offset] = ds0123 - ds4567;
                src[2 + offset] = ds0123 + ds4567;
            }

            d45             *= SQRT2;
            d67             *= SQRT2;
            src[5 + offset] = sd0123 - d45;
            src[1 + offset] = sd0123 + d45;
            src[7 + offset] = dd0123 - d67;
            src[3 + offset] = dd0123 + d67;

            return;
        }

        int halfLength = transformLength / 2;

        transform(src, halfLength, sineTable, offset);
        transform(src, halfLength, sineTable, offset + halfLength);

        // ///INIT_TRIGS/////
        int    power = getPow(halfLength);
        double sin0  = sineTable[power];
        double cos0  = sineTable[power + 1];

        cos0 = -2.0 * cos0 * cos0;

        double sin = sin0;
        double cos = cos0 + 1.0;

        // ///////////////////
        int quaterLength   = transformLength / 4;
        int eightherLength = transformLength / 8;

        for (int x = 1; x < eightherLength; ++x) {
            twiseButerfly(src, x, halfLength - x, cos, sin, offset, halfLength);
            twiseButerfly(src, quaterLength - x, quaterLength + x, sin, cos, offset, halfLength);

            //
            double temp = cos;

            cos = cos * cos0 - sin * sin0 + cos;
            sin = sin * cos0 + temp * sin0 + sin;
        }

        //
        simpleButterfly(src, 0, 0, 1.0, 0.0, offset, halfLength);
        simpleButterfly(src, quaterLength, quaterLength, 0.0, 1.0, offset, halfLength);

        //
        twiseButerfly(src, eightherLength, halfLength - eightherLength, SQRT2_DIV_2, SQRT2_DIV_2, offset, halfLength);
    }

    private final static void twiseButerfly(double[] src, int n1, int n2, double cos, double sin, int offset,
            int halfLen) {
        double rx, ri;

        rx = src[offset + n1 + halfLen];
        ri = src[offset + n2 + halfLen];
        ///////////////////////////////
        double cas1;
        double lx;

        cas1                       = rx * cos + ri * sin;
        lx                         = src[offset + n1];
        src[offset + n1]           = lx + cas1;
        src[offset + n1 + halfLen] = lx - cas1;
        ///////////////////////////////
        double cas2;
        double li;

        cas2                       = rx * sin - ri * cos;
        li                         = src[n2 + offset];
        src[n2 + offset]           = li + cas2;
        src[offset + n2 + halfLen] = li - cas2;
        ///////////////////////////////
    }

    private final static void simpleButterfly(double[] src, int n1, int n2, double cos, double sin, int offset,
            int halfLen) {
        double cas1 = src[offset + n1 + halfLen] * cos + src[offset + n2 + halfLen] * sin;
        double temp = src[offset + n1];

        src[offset + n1]           = temp + cas1;
        src[offset + n2 + halfLen] = temp - cas1;
    }

    private final static double[] createSinTable(int transformLength) {
        int      x      = 0;
        int      p      = 1;
        double[] result = new double[getPow(transformLength) + 3];

        while (p <= transformLength * 4) {
            result[x] = Math.sin(Math.PI / p);
            p         *= 2;
            ++x;
        }

        return result;
    }

    private final static int getPow(int transformLength) {
        int result = -1;

        while (transformLength != 0) {
            result++;
            transformLength = transformLength >>> 1;
        }

        return result;
    }

    private final static short[] normalize(double[] multiplyResult) {
        int     len           = multiplyResult.length;
        short[] result        = new short[len];
        double  invMultiplyer = 1.0 / len;
        long    carry         = 0;

        for (int i = 0; i < len; i++) {
            long buff = Math.round(multiplyResult[i] * invMultiplyer + carry);

            result[i] = (short) buff;
            carry     = (buff >>> 16);
        }

        return result;
    }

    // We need this method to calculate Hartley with Short BASE
    // Complex base in this realization is SHORT.
    public final static double[] doubleFrom(int[] src) {
        int      len         = src.length;
        double[] doubleArray = new double[len * 2];

        for (int i = 0; i < len; ++i) {
            for (int j = 0; j < 2; ++j) {
                doubleArray[i * 2 + j] = ((src[len - 1 - i] >>> (16 * j)) & 0xFFFF);
            }
        }

        return doubleArray;
    }

    public final static int[] mulFHT(double[] a, double[] b) {

        // here a and b lengths must be equal and be the power of two.
        int      len       = TransformUtilities.extendedLength(a.length, b.length);
        double[] sineTable = createSinTable(len);
        double[] afht      = new double[len];

        bitReverseCopy(a, afht, len);
        transform(afht, len, sineTable, 0);

        double[] bfht = new double[len];

        bitReverseCopy(b, bfht, len);
        transform(bfht, len, sineTable, 0);

        double[] c = new double[len];

        // building convolution
        c[0] = 0.5 * (afht[0] * (bfht[0] + bfht[0]) + afht[0] * (bfht[0] - bfht[0]));

        for (int i = 1; i < len; ++i) {
            c[i] = 0.5 * (afht[i] * (bfht[i] + bfht[len - i]) + afht[len - i] * (bfht[i] - bfht[len - i]));
        }

        double[] cfht = new double[len];

        bitReverseCopy(c, cfht, len);
        transform(cfht, len, sineTable, 0);

        short[] sh = normalize(cfht);

        // optimize do intFrom complex..
        return intFrom(sh);
    }

    private final static int rev(int num, int length) {
        int numberOfBits = getPow(length);
        int res          = 0;
        int bit          = 0;

        for (int i = 0; i < numberOfBits; ++i) {
            res = res << 1;
            bit = (num & 1);
            res = res | bit;
            num = num >> 1;
        }

        return res;
    }

    private final static void bitReverseCopy(double[] a, double[] result, int transformLength) {
        int len = a.length;
        int i   = 0;

        for (; i < len; ++i) {
            int k = rev(i, transformLength);

            result[k] = a[i];
        }

        for (; i < result.length; ++i) {
            int k = rev(i, transformLength);

            result[k] = 0.0;
        }
    }

    public static int[] intFrom(short[] src) {
        int shortLen = src.length;
        int intLen   = shortLen / 2;

        int[] result = new int[intLen];

        for (int i = intLen - 1; i >= 0; --i) {
            for (int j = 0; j < 2; ++j) {
                result[i] += ((src[(intLen - 1 - i) * 2 + j] & 0xffff) << (16 * j));
            }
        }

        return result;
    }
}
