/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bignumberslibrary;

import util.Complex;

/**
 *
 * @author chibis
 */
public class Fourier {

    public static Complex[] recursiveFFT(Complex[] a, int trueLength, int extendLength) {
        int n = extendLength;
        if (n == 1) {
            return a;
        }
        Complex Wn = new Complex(Math.cos(Complex.TWO_PI / n), Math.sin(Complex.TWO_PI / n));
        Complex w = new Complex(1, 0);
        Complex[] a1 = new Complex[n / 2];
        Complex[] a2 = new Complex[n / 2];
        for (int i = 0, k = 0, s = 0; i < trueLength; i++) {
            if (i % 2 == 0) {
                a1[k++] = new Complex(a[i]);
            } else {
                a2[s++] = new Complex(a[i]);
            }
        }
        Complex[] y1 = recursiveFFT(a1, trueLength / 2, n / 2);
        Complex[] y2 = recursiveFFT(a2, trueLength / 2, n / 2);
        Complex[] result = new Complex[n];
        for (int k = 0; k < n / 2; ++k) {
            result[k] = y1[k].add(w.mul(y2[k]));
            result[k + n / 2] = y1[k].sub(w.mul(y2[k]));
            w = w.mul(Wn);
        }
        return result;
    }

    private static int getPow(int length) {
        int result = -1;
        while (length != 0) {
            result++;
            length = length >>> 1;
        }
        return result;
    }

    private static int rev(int num, int length) {
        int numberOfBits = getPow(length);
        int res = 0;
        int bit = 0;

        for (int i = 0; i < numberOfBits; ++i) {
            res = res << 1;
            bit = (num & 1);
            res = res | bit;
            num = num >> 1;
        }

        return res;
    }

    private static void bitReverseCopy(Complex[] a, Complex[] result, int extendLength) {
        int len = a.length;

        for (int i = 0; i < result.length; ++i) {
            int k = rev(i, extendLength);
            if (i < len) {
                result[k] = a[i];
            } else {
                result[k] = new Complex();
            }
        }

    }

    public static Complex[] iterativeFFT(Complex[] a, int extendLength, boolean reverseFFT) {

        Complex[] newOrder = new Complex[extendLength];
        bitReverseCopy(a, newOrder, extendLength);

        int n = extendLength;

        ////////////////TRIG_VARS///////
        int tNdx = 0;
        int tLen = 0;

        Complex pRoot = new Complex();
        Complex root = new Complex();
        int direction = reverseFFT ? -1 : 1;
        Complex tRight = new Complex();
        Complex tLeft = new Complex();
        ////////////////////////////////

        int step = 1;

        while (step < n) {
            int halfStep = step;
            step *= 2;

            ////////////////INIT_VARS///////
            tNdx = 0;
            tLen = halfStep;
            pRoot.re = 1;
            root.re = Math.sin(Complex.PI / (tLen * 2));
            root.re = -2 * root.re() * root.re();
            root.im = Math.sin(Complex.PI / tLen) * direction;
            ////////////////////////////////

            for (int b = 0; b < halfStep; ++b) {

                for (int l = b; l < n; l += step) {
                    int r = l + halfStep;
                    tLeft = newOrder[l];
                    tRight = newOrder[r];
                    tRight = tRight.mul(pRoot);
                    newOrder[l] = tLeft.add(tRight);
                    newOrder[r] = tLeft.sub(tRight);
                }
                ////////////////NEXT_VAR///////////////
                if (((++tNdx) & 15) == 0) {
                    double Angle;
                    Angle = (Complex.PI * (tNdx)) / tLen;
                    pRoot.re = Math.sin(Angle * 0.5);
                    pRoot.re = 1.0 - 2.0 * pRoot.re() * pRoot.re();
                    pRoot.im = Math.sin(Angle) * (direction);
                } else {
                    Complex Temp;
                    Temp = pRoot;
                    pRoot = pRoot.mul(root);
                    pRoot = pRoot.add(Temp);
                }
            ///////////////////////////////////////
            }
        }

        if (reverseFFT) {
            return normalize(newOrder, n);
        }

        return newOrder;
    }

    private static Complex[] normalize(Complex[] result, int n) {
        for (int i = 0; i < result.length; ++i) {
            result[i].re = result[i].re() / n;
        }
        return result;
    }

    public static Complex[] interpolation(Complex[] a) {
        return iterativeFFT(a, a.length, true);
    }

    private static boolean is2power(int a) {
        if (a < 2) {
            return false;
        }
        return ((a & (a - 1)) == 0) ? true : false;
    }

    public static int extendLength(int length1, int length2) {
        int n = length1 + length2;
        while (!is2power(n)) {
            n++;
        }
        return n;
    }
}

