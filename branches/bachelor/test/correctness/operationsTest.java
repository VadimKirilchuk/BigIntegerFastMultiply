package correctness;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import junit.framework.TestCase;
import bignumberslibrary.*;
import java.math.BigInteger;
import java.util.Random;

/**
 *
 * @author chibis
 */
public class operationsTest extends TestCase {

    private static Random rnd = new Random();
    private static byte[] res1;
    private static byte[] res2;
    private static BigNumber bn1;
    private static BigNumber bn2;
    private static BigInteger bi1;
    private static BigInteger bi2;

    public operationsTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAdding() {
        System.out.println("Testing adding for correctness.");
        ///////determination
        byte[] byteArray1 = generateRandomByteArray(0, Short.MAX_VALUE);
        byte[] byteArray2 = generateRandomByteArray(0, Short.MAX_VALUE);

        bn1 = new BigNumber(byteArray1);
        bn2 = new BigNumber(byteArray2);
        bi1 = new BigInteger(byteArray1);
        bi2 = new BigInteger(byteArray2);
        //////////end of determination


        ////////////Summ of two positive numbers
        BigNumber resultBn = bn1.add(bn2);
        res1 = resultBn.toByteArray();

        BigInteger resultBi = bi1.add(bi2);
        res2 = resultBi.toByteArray();

        checkCorrectness(res2, res1);
        ////////end of two positive

        ///////summ of positive and negative
        //bn1
        bn2 = bn2.negate();
        //bi1
        bi2 = bi2.negate();

        resultBn = bn1.add(bn2);
        res1 = resultBn.toByteArray();

        resultBi = bi1.add(bi2);
        res2 = resultBi.toByteArray();

        checkCorrectness(res2, res1);
        //////end of positive and negative summ

        /////summ of two negative
        bn1 = bn1.negate();
        //bn2
        bi1 = bi1.negate();
        //bi2

        resultBn = bn1.add(bn2);
        res1 = resultBn.toByteArray();

        resultBi = bi1.add(bi2);
        res2 = resultBi.toByteArray();

        checkCorrectness(res2, res1);
        //////end of negative and negative summ


    }

    public void testSubtracting() {
        System.out.println("Testing subtracting for correctness.");
        ///////determination
        byte[] byteArray1 = generateRandomByteArray(0, Short.MAX_VALUE);
        byte[] byteArray2 = generateRandomByteArray(0, Short.MAX_VALUE);

        bn1 = new BigNumber(byteArray1);
        bn2 = new BigNumber(byteArray2);
        bi1 = new BigInteger(byteArray1);
        bi2 = new BigInteger(byteArray2);
        //////////end of determination


        ////////////Sub of two positive numbers
        BigNumber resultBn = bn1.sub(bn2);
        res1 = resultBn.toByteArray();

        BigInteger resultBi = bi1.subtract(bi2);
        res2 = resultBi.toByteArray();
        
        checkCorrectness(res2, res1);
        ////////end of two positive sub

        ///////sub of positive and negative
        //bn1
        bn2 = bn2.negate();
        //bi1
        bi2 = bi2.negate();

        resultBn = bn1.sub(bn2);
        res1 = resultBn.toByteArray();

        resultBi = bi1.subtract(bi2);
        res2 = resultBi.toByteArray();

        checkCorrectness(res2, res1);
        //////end of positive and negative sub

        /////sub of two negative
        bn1 = bn1.negate();
        //bn2
        bi1 = bi1.negate();
        //bi2

        resultBn = bn1.sub(bn2);
        res1 = resultBn.toByteArray();

        resultBi = bi1.subtract(bi2);
        res2 = resultBi.toByteArray();

        checkCorrectness(res2, res1);
        //////end of negative and negative summ
    }

    public static void testSimpleMultiply() {
        System.out.println("Testing simple multiply for correctness.");
        int count = 4;
        while ((count--) > 0) {
            ///////determination
            byte[] byteArray1 = generateRandomByteArray(1000, Short.MAX_VALUE);
            byte[] byteArray2 = generateRandomByteArray(1000, Short.MAX_VALUE);

            bn1 = new BigNumber(byteArray1);
            bn2 = new BigNumber(byteArray2);
            bi1 = new BigInteger(byteArray1);
            bi2 = new BigInteger(byteArray2);
            //////////end of determination

            BigNumber resultBn = bn1.multiply(bn2);
            res1 = resultBn.toByteArray();

            BigInteger resultBi = bi1.multiply(bi2);
            res2 = resultBi.toByteArray();

            checkCorrectness(res2, res1);

            ////////////Test of 2^31 mul bug on zerolength arrays
            for (int i = 0; i < byteArray1.length; i++) {
                byteArray1[i] = (byte) (-128 + rnd.nextInt(100));
            }

            for (int i = 0; i < byteArray2.length; i++) {
                byteArray2[i] = (byte) (-128 + rnd.nextInt(100));
            }

            bn1 = new BigNumber(byteArray1);
            bn2 = new BigNumber(byteArray2);
            bi1 = new BigInteger(byteArray1);
            bi2 = new BigInteger(byteArray2);

            resultBn = bn1.multiply(bn2);
            res1 = resultBn.toByteArray();

            resultBi = bi1.multiply(bi2);
            res2 = resultBi.toByteArray();

            checkCorrectness(res2, res1);
        }
    }

    public static void testMulFFT() {
        System.out.println("Testing FFT multiply for correctness.");
        int count = 4;
        while ((count--) > 0) {
            ///////determination
            byte[] byteArray1 = generateRandomByteArray(1000, Short.MAX_VALUE*16);
            byte[] byteArray2 = generateRandomByteArray(1000, Short.MAX_VALUE*16);

            bn1 = new BigNumber(byteArray1);
            bn2 = new BigNumber(byteArray2);
            bi1 = new BigInteger(byteArray1);
            bi2 = new BigInteger(byteArray2);
            //////////end of determination

            BigNumber resultBn = bn1.mulFFT2(bn2);
            res1 = resultBn.toByteArray();

            BigInteger resultBi = bi1.multiply(bi2);
            res2 = resultBi.toByteArray();

            checkCorrectness(res2, res1);
        }
    }

    public static void testKaratsuba() {
        System.out.println("Testing Karatsuba multiply for correctness.");
        int count = 10;
        while ((count--) > 0) {
            ///////determination
            byte[] byteArray1 = generateRandomByteArray(1000, Short.MAX_VALUE*16);
            byte[] byteArray2 = generateRandomByteArray(1000, Short.MAX_VALUE*16);

            bn1 = new BigNumber(byteArray1);
            bn2 = new BigNumber(byteArray2);
            bi1 = new BigInteger(byteArray1);
            bi2 = new BigInteger(byteArray2);
            //////////end of determination

            BigNumber resultBn = bn1.mulKaratsuba(bn2);
            res1 = resultBn.toByteArray();

            BigInteger resultBi = bi1.multiply(bi2);
            res2 = resultBi.toByteArray();

            checkCorrectness(res2, res1);

        }
    }

    private static byte[] generateRandomByteArray(int minLength, int maxLength){

        int len = rnd.nextInt(maxLength) + minLength;
        byte[] result = new byte[len];

        for (int i = 0; i < len; ++i) {
                result[i] = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.
                        nextInt(Byte.MAX_VALUE));
            }

        return result;
    }

    private static void checkCorrectness(byte[] bigIntRes, byte[] bigNumRes) {
        int len = bigIntRes.length;
        assertEquals("!Length not the same!", len, bigNumRes.length);

        for (int i = 0; i < len; i++) {
            assertEquals("!Wrong element!", bigIntRes[i], bigNumRes[i]);
        }
    }
}
