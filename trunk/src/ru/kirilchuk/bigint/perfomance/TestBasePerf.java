package ru.kirilchuk.bigint.perfomance;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Random;

/**
 * @author chibis
 */
public class TestBasePerf {

    public final static long LONG_MASK = 0xffffffffL;
    private static Random rnd = new Random();
    private static long t1;
    private static long t2;

    private static void testBytes() {
        //      int minimumIterations = 20;
        //      int maxIterations = 100;
        //      int dispersionTrust = 80;
        int from = 256;
        int step = 256;
        int iterations = 6;
        PrintWriter out = null;
        try {
            out = createFile("bytes.txt");
            out.print("Bytes ");
            out.print("bytes multiplication algorithm time(ns)");
            out.println();
            byte[] b1;
            byte[] b2;
            for (int i = from; i < Math.pow(2, 14); i += step) {
                b1 = generateRandomByteArray(i);
                b2 = generateRandomByteArray(i);
                out.write(i + " ");
                for (int j = 0; j < iterations; ++j) {
                    t1 = System.nanoTime();
                    multiply(b1, b1.length, b2, b2.length);
                    t2 = System.nanoTime();
                    out.write(String.valueOf(t2 - t1) + " ");
                }
                out.println();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private static void testShorts() {
        //      int minimumIterations = 20;
        //      int maxIterations = 100;
        //      int dispersionTrust = 80;
        int from = 256;
        int step = 256;
        int iterations = 6;
        PrintWriter out = null;
        try {
            out = createFile("shorts.txt");
            out.print("Bytes ");
            out.print("shorts multiplication algorithm time(ns)");
            out.println();
            short[] b1;
            short[] b2;
            for (int i = from; i < Math.pow(2, 14); i += step) {
                b1 = generateRandomShortArray(i / 2);
                b2 = generateRandomShortArray(i / 2);
                out.write(i + " ");
                for (int j = 0; j < iterations; ++j) {
                    t1 = System.nanoTime();
                    multiply(b1, b1.length, b2, b2.length);
                    t2 = System.nanoTime();
                    out.write(String.valueOf(t2 - t1) + " ");
                }
                out.println();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private static void testInts() {
        //      int minimumIterations = 20;
        //      int maxIterations = 100;
        //      int dispersionTrust = 80;
        int from = 256;
        int step = 256;
        int iterations = 6;
        PrintWriter out = null;
        try {
            out = createFile("ints.txt");
            out.print("Bytes ");
            out.print("ints multiplication algorithm time(ns)");
            out.println();
            byte[] b1;
            byte[] b2;
            for (int i = from; i < Math.pow(2, 14); i += step) {
                b1 = generateRandomByteArray(i / 4);
                b2 = generateRandomByteArray(i / 4);
                out.write(i + " ");
                for (int j = 0; j < iterations; ++j) {
                    t1 = System.nanoTime();
                    multiply(b1, b1.length, b2, b2.length);
                    t2 = System.nanoTime();
                    out.write(String.valueOf(t2 - t1) + " ");
                }
                out.println();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private TestBasePerf() {
    }

    private static int[] multiply(int[] array1, int len1, int[] array2, int len2) {
        int[] z = new int[len1 + len2];
        long carry = 0;
        int j = 0;

        for (; j < len1; ++j) {
            long product = (array2[0] & LONG_MASK) * (array1[j] & LONG_MASK) + carry;

            z[j] = (int) product;
            carry = (product >>> 32);
        }

        z[j] = (int) carry;

        int i = 1;

        for (; i < len2; ++i) {
            carry = 0;

            for (j = 0; j < len1; ++j) {
                long product = (array2[i] & LONG_MASK) * (array1[j] & LONG_MASK) + (z[i + j] & LONG_MASK) + carry;

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
            int product = (array2[0] & 0xffff) * (array1[j] & 0xffff) + carry;

            z[j] = (short) product;
            carry = (product >>> 16);
        }

        z[j] = (short) carry;

        int i = 1;

        for (; i < len2; ++i) {
            carry = 0;

            for (j = 0; j < len1; ++j) {
                int product = (array2[i] & 0xffff) * (array1[j] & 0xffff) + (z[i + j] & 0xffff) + carry;

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
            short product = (byte) ((array2[0] & 0xff) * (array1[j] & 0xff) + carry);

            z[j] = (byte) product;
            carry = (short) (product >>> 8);
        }

        z[j] = (byte) carry;

        int i = 1;

        for (; i < len2; ++i) {
            carry = 0;

            for (j = 0; j < len1; ++j) {
                short product = (byte) ((array2[i] & 0xff) * (array1[j] & 0xff) + (z[i + j] & 0xff) + carry);

                z[i + j] = (byte) product;
                carry = (short) (product >>> 8);
            }

            z[i + j] = (byte) carry;
        }

        return z;
    }

    public static void main(String[] args) {
        testBytes();
        testShorts();
        testInts();

    }

    public static PrintWriter createFile(String fileName) throws IOException {
        File file = new File(fileName);

        if (!file.exists()) {
            file.createNewFile();
        }

        return new PrintWriter(file);
    }

    private static byte[] generateRandomByteArray(int length) {
        byte[] result = new byte[length];

        for (int i = 0; i < length; ++i) {
            result[i] = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.nextInt(Byte.MAX_VALUE));
        }

        return result;
    }

    private static short[] generateRandomShortArray(int length) {
        short[] result = new short[length];

        for (int i = 0; i < length; ++i) {
            result[i] = (short) (rnd.nextInt(Short.MAX_VALUE) - 2 * rnd.nextInt(Short.MAX_VALUE));
        }

        return result;
    }
}
