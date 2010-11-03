package ru.kirilchuk.bigint;

import ru.kirilchuk.bigint.util.TransformUtilities;

/**
 * Class that represents Fourier transform.
 *
 * @deprecated cause has bad performance and low precision due to bad realization.
 * (Not the best FFT algorithms too)
 * <p> Use Hartley Transform.
 * 
 * @author Kirilchuk V.E.
 */
public class Fourier {

    @Deprecated
    public final static Complex[] recursiveFFT(Complex[] a, int extendLength, boolean isReverse) {
        int n = extendLength;

        if (n == 1) {
            return a;
        }

        Complex[] a1 = new Complex[n / 2];
        Complex[] a2 = new Complex[n / 2];

        int s = 0;
        int i = 0;
        for ( ; i < a.length; i+=2) {
             a1[s++] = new Complex(a[i]);
        }
        for ( ; i < extendLength; i+=2) {
            a1[s++] = new Complex();
        }
        
        s = 0;
        i = 1;
        for (; i < a.length; i+=2) {
             a2[s++] = new Complex(a[i]);
        }
        for ( ; i < extendLength; i+=2) {
            a2[s++] = new Complex();
        }

        Complex[] y1     = recursiveFFT(a1, n / 2, isReverse);
        Complex[] y2     = recursiveFFT(a2, n / 2, isReverse);
        Complex[] result = new Complex[n];
        Complex   Wn;

        if (isReverse) {
            Wn = new Complex(Math.cos(Complex.TWO_PI / n), -Math.sin(Complex.TWO_PI / n));
        } else {
            Wn = new Complex(Math.cos(Complex.TWO_PI / n), Math.sin(Complex.TWO_PI / n));
        }

        Complex w = new Complex(1, 0);

        for (int k = 0; k < n / 2; ++k) {
            Complex temp = w.mul(y2[k]);
            result[k]         = y1[k].add(temp);
            result[k + n / 2] = y1[k].sub(temp);
            w                 = w.mul(Wn);
        }

        return result;
    }

    private final static int getPow(int power2Len) {
        int result = -1;
        while (power2Len != 0) {
            result++;
            power2Len = power2Len >>> 1;
        }
        
        return result;
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
    
    private final static void bitReverseCopy(Complex[] a, Complex[] result, int extendLength) {
        int len = a.length;

        int i = 0;
        for (; i < len; ++i) {
            int k = rev(i, extendLength);
            result[k] = a[i];

        }

        for (; i < result.length; ++i) {
            int k = rev(i, extendLength);
            result[k] = new Complex();
        }
    }

    @Deprecated
    public final static Complex[] iterativeFFT(Complex[] a, int extendLength, boolean isInverse) {
        Complex[] newOrder = new Complex[extendLength];

        bitReverseCopy(a, newOrder, extendLength);

        int n = extendLength;

        // //////////////TRIG_VARS///////
        int     tNdx      = 0;
        int     tLen      = 0;
        Complex pRoot     = new Complex();
        Complex root      = new Complex();
        int     direction = isInverse
                            ? -1
                            : 1;
        Complex tRight    = new Complex();
        Complex tLeft     = new Complex();

        // //////////////////////////////
        int step = 1;

        while (step < n) {
            int halfStep = step;

            step *= 2;

            // //////////////INIT_VARS///////
            tNdx     = 0;
            tLen     = halfStep;
            pRoot.re = 1;
            root.re  = Math.sin(Complex.PI / (tLen * 2));
            root.re  = -2 * root.re * root.re;
            root.im  = Math.sin(Complex.PI / tLen) * direction;

            // //////////////////////////////

            for (int b = 0; b < halfStep; ++b) {
                for (int l = b; l < n; l += step) {
                    int r = l + halfStep;

                    tLeft       = newOrder[l];
                    tRight      = newOrder[r];
                    tRight      = tRight.mul(pRoot);
                    newOrder[l] = tLeft.add(tRight);
                    newOrder[r] = tLeft.sub(tRight);
                }

                // //////////////NEXT_VAR///////////////
                if (((++tNdx) & 15) == 0) {
                    double Angle;

                    Angle    = (Complex.PI * (tNdx)) / tLen;
                    pRoot.re = Math.sin(Angle * 0.5);
                    pRoot.re = 1.0 - 2.0 * pRoot.re * pRoot.re;
                    pRoot.im = Math.sin(Angle) * (direction);
                } else {
                    Complex Temp;

                    Temp  = pRoot;
                    pRoot = pRoot.mul(root);
                    pRoot = pRoot.add(Temp);
                }

                // /////////////////////////////////////
            }
        }

        if (isInverse) {
            return normalize(newOrder, n);
        }

        return newOrder;
    }

    public final static Complex[] normalize(Complex[] result, int n) {
        double inversedDivisor = 1.0 / n;

        for (int i = 0; i < result.length; ++i) {
            result[i].re = result[i].re * inversedDivisor;

        }

        return result;
    }

    // We need this method to calculate Fourier with Short BASE
    // Complex base in this realization is SHORT.
    public final static Complex[] complexFrom(int[] src) {
        int       len          = src.length;
        Complex[] complexArray = new Complex[len * 2];

        for (int i = 0; i < len; ++i) {
            for (int j = 0; j < 2; ++j) {
                complexArray[i * 2 + j] = new Complex((src[len - i - 1] >>> (16 * j)) & 0xFFFF);
            }
        }

        return complexArray;
    }

    // This method is to make short array from complex array after
    // FFT.
    public final static short[] shortFrom(Complex[] src) {
        int     len    = src.length;
        short[] result = new short[len];
        long    carry  = 0;

        for (int i = 0; i < len; i++) {
            long buff = Math.round(src[i].re + carry);

            result[i] = (short) buff;
            carry     = (buff >>> 16);
        }

        return result;
    }

    @Deprecated
    public final static int[] mulIterativeFFT(Complex[] a, Complex[] b) {
        int len = TransformUtilities.extendedLength(a.length, b.length);
        
        Complex[] afft = Fourier.iterativeFFT(a, len, false);
        Complex[] bfft = Fourier.iterativeFFT(b, len, false);
        Complex[] cfft = new Complex[len];

        // multiplying as point-to-point in complex
        for (int i = 0; i < len; ++i) {
            cfft[i] = afft[i].mul(bfft[i]);
        }

        // doing inverse transform
        Complex[] c  = Fourier.iterativeFFT(cfft, len, true);
        short[]   sh = Fourier.shortFrom(c);

        // optimize do intFrom complex..
        return intFrom(sh);
    }

    @Deprecated
    public final static int[] mulRecursiveFFT(Complex[] a, Complex[] b) {
        int       len  = TransformUtilities.extendedLength(a.length, b.length);

        Complex[] afft = Fourier.recursiveFFT(a, len, false);
        Complex[] bfft = Fourier.recursiveFFT(b, len, false);
        Complex[] cfft = new Complex[len];

        // multiplying as point-to-point in complex
        for (int i = 0; i < len; ++i) {
            cfft[i] = afft[i].mul(bfft[i]);
        }

        // doing inverse transform
        Complex[] c = Fourier.recursiveFFT(cfft, len, true);

        Fourier.normalize(c, len);

        short[] sh = Fourier.shortFrom(c);

        // optimize do intFrom complex..
        return intFrom(sh);
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
