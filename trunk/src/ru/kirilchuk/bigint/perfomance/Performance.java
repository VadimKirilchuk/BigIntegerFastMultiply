package ru.kirilchuk.bigint.perfomance;

import ru.kirilchuk.bigint.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Random;

/**
 * Utility class just for measuring speed of multiplication of improved BigInteger.
 *
 * @author Kirilchuk V.E.
 */
public class Performance {
    private static Random     rnd = new Random();
    private static byte[]     b1;
    private static byte[]     b2;
    private static BigInteger num1;
    private static BigInteger num2;

    private static long t1;
    private static long t2;

    private Performance() {}

    public static void main(String[] args) {
        int         from       = 256;
        int         iterations = 10000;
        PrintWriter out        = null;

        try {
            out = createFile("multiply.txt");

            // logarithmic curve
            for (int i = from; i <= Math.pow(2, 12); i += 256) {
                long res = 0;

                for (int j = 0; j < iterations; ++j) {
                    b1 = generateRandomByteArray(i);
                    b2 = generateRandomByteArray(i);
                    
                    num1 = new BigInteger(b1);
                    num2 = new BigInteger(b2);
                    t1   = System.nanoTime();
                    num1.multiply(num2);
                    t2  = System.nanoTime();
                    res += t2 - t1;
                }

                out.write(String.valueOf(res / iterations));
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

    private static PrintWriter createFile(String fileName) throws IOException {
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
}
