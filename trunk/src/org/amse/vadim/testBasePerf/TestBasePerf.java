/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.testBasePerf;

import org.amse.vadim.bignumberslibrary.Dispersion;
import org.amse.vadim.bignumberslibrary.Util;
import java.util.Random;

/**
 * @author chibis
 */
public class TestBasePerf {

    public static void run(int startDim, int mulToEnd, int trustPercent, int maximumIterations) {
        Random rnd = new Random();

        byte[] byteArray1;
        byte[] byteArray2;

        short[] shortArray1;
        short[] shortArray2;

        int[] intArray1;
        int[] intArray2;

        for (int i = startDim; i < startDim * Math.pow(2, mulToEnd); i *= 2) {
            Dispersion byteDisp = new Dispersion(30, trustPercent, maximumIterations);//if nu<(maximumIterations-trust)/maximumIterations return true;
            byteArray1 = new byte[i];
            byteArray2 = new byte[i];

            Dispersion shortDisp = new Dispersion(30, trustPercent, maximumIterations);
            shortArray1 = new short[i / 2];
            shortArray2 = new short[i / 2];

            Dispersion intDisp = new Dispersion(30, trustPercent, maximumIterations);
            intArray1 = new int[i / 4];
            intArray2 = new int[i / 4];

            for (int j = 0; j < i; ++j) {
                byteArray1[j] = (byte) rnd.nextInt(Byte.MAX_VALUE);
                byteArray2[j] = (byte) rnd.nextInt(Byte.MAX_VALUE);
            }

            for (int j = 0; j < i / 2; ++j) {
                shortArray1[j] = (short) rnd.nextInt(Short.MAX_VALUE);
                shortArray2[j] = (short) rnd.nextInt(Short.MAX_VALUE);
            }

            for (int j = 0; j < i / 4; ++j) {
                intArray1[j] = rnd.nextInt(Integer.MAX_VALUE);
                intArray2[j] = rnd.nextInt(Integer.MAX_VALUE);
            }


            System.out.println("////////////////////bytes=" + i + "////////////////////////");

            int delta = 1;

            while (!byteDisp.canTrust(delta)) {
                long t1 = System.currentTimeMillis();
                multiply(byteArray1, byteArray1.length, byteArray2, byteArray2.length);
                long t2 = System.currentTimeMillis();
                delta = (int) (t2 - t1);
            }
            System.out.println("byteBase multiplyed by " + byteDisp.getMean());

            while (!shortDisp.canTrust(delta)) {
                long t1 = System.currentTimeMillis();
                multiply(shortArray1, shortArray1.length, shortArray2, shortArray2.length);
                long t2 = System.currentTimeMillis();
                delta = (int) (t2 - t1);
            }
            System.out.println("shortBase multiplyed by " + shortDisp.getMean());

            while (!intDisp.canTrust(delta)) {
                long t1 = System.currentTimeMillis();
                multiply(intArray1, intArray1.length, intArray2, intArray2.length);
                long t2 = System.currentTimeMillis();
                delta = (int) (t2 - t1);
            }
            System.out.println("intBase multiplyed by " + intDisp.getMean());

        }
    }

    private static int[] multiply(int[] array1, int len1, int[] array2, int len2) {

        int[] z = new int[len1 + len2];

        long carry = 0;

        int j = 0;

        for (; j < len1; ++j) {
            long product = (array2[0] & Util.LONG_MASK) *
                    (array1[j] & Util.LONG_MASK) +
                    carry;
            z[j] = (int) product;
            carry = (product >>> 32);
        }
        z[j] = (int) carry;

        int i = 1;

        for (; i < len2; ++i) {
            carry = 0;
            for (j = 0; j < len1; ++j) {
                long product = (array2[i] & Util.LONG_MASK) *
                        (array1[j] & Util.LONG_MASK) +
                        (z[i + j] & Util.LONG_MASK) +
                        carry;
                z[i + j] = (int) product;
                carry = (product >>> 32);
            }
            z[i + j] = (int) carry;
        }
        return z;
    }

    private static short[] multiply(short[] array1, int len1, short[] array2, int len2) {

        short[] z = new short[len1 + len2];

        int carry = 0;

        int j = 0;

        for (; j < len1; ++j) {
            int product = (array2[0] & 0xffff) *
                    (array1[j] & 0xffff) +
                    carry;
            z[j] = (short) product;
            carry = (product >>> 16);
        }
        z[j] = (short) carry;

        int i = 1;

        for (; i < len2; ++i) {
            carry = 0;
            for (j = 0; j < len1; ++j) {
                int product = (array2[i] & 0xffff) *
                        (array1[j] & 0xffff) +
                        (z[i + j] & 0xffff) +
                        carry;
                z[i + j] = (short) product;
                carry = (product >>> 16);
            }
            z[i + j] = (short) carry;
        }
        return z;
    }

    private static byte[] multiply(byte[] array1, int len1, byte[] array2, int len2) {

        byte[] z = new byte[len1 + len2];

        short carry = 0;

        int j = 0;

        for (; j < len1; ++j) {
            short product = (byte) ((array2[0] & 0xff) *
                    (array1[j] & 0xff) +
                    carry);
            z[j] = (byte) product;
            carry = (short) (product >>> 8);
        }
        z[j] = (byte) carry;

        int i = 1;

        for (; i < len2; ++i) {
            carry = 0;
            for (j = 0; j < len1; ++j) {
                short product = (byte) ((array2[i] & 0xff) *
                        (array1[j] & 0xff) +
                        (z[i + j] & 0xff) +
                        carry);
                z[i + j] = (byte) product;
                carry = (short) (product >>> 8);
            }
            z[i + j] = (byte) carry;
        }
        return z;
    }
}