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
public class bigNumberTest extends TestCase {

    public bigNumberTest(String testName) {
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

    public void testByteConstructor() {
        Random rnd = new Random();

        int N = rnd.nextInt(Short.MAX_VALUE);

        byte[] byteArray = new byte[N];

        for (int i = 0; i < N; ++i) {
            byte buff = 0;

            buff = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.nextInt(Byte.MAX_VALUE));

            byteArray[i] = buff;
        }

        BigNumber bn = new BigNumber(byteArray);
        BigInteger bi = new BigInteger(byteArray);

        byte[] byteArray1 = bn.toByteArray();
        byte[] byteArray2 = bi.toByteArray();

        assertEquals("!Length not same!", byteArray1.length, byteArray2.length);

        for (int i = 0; i < byteArray2.length; ++i) {
            assertEquals(i + " element !Wrong data in array!", byteArray1[i], byteArray2[i]);
        }
    }

    //CHECK
    public void IntConstructor() {
        Random rnd = new Random();

        int N = rnd.nextInt(Short.MAX_VALUE);
        int[] intArray = new int[N];

        for (int i = 0; i < N; ++i) {
            int buff = 0;

            buff = rnd.nextInt(Integer.MAX_VALUE) - 2 * rnd.nextInt(Integer.MAX_VALUE);

            intArray[i] = buff;
        }

        BigNumber bn = new BigNumber(intArray);

        int[] intArray2 = bn.getArrayOfBigNumber();

        assertEquals("!Length not same!", N, intArray2.length);

        for (int i = 0; i < N; ++i) {
            if (intArray[i] != intArray2[i]) {
                fail("!Wrong data in array!");
            }
        }
    }

    //BUGS FIX
    public void ZeroConstructor() {
        BigNumber bn1 = new BigNumber();
        assertEquals("!Sign is not 0!", 0, bn1.getSign());
        assertEquals("!Array not null!", null, bn1.getArrayOfBigNumber());

        byte[] byteArray = null;
        BigNumber bn2 = new BigNumber(1, byteArray);
        assertEquals("!Sign is not 0!", 0, bn2.getSign());
        assertEquals("!Array not null!", null, bn2.getArrayOfBigNumber());

        int[] intArray = null;
        BigNumber bn3 = new BigNumber(intArray);
        assertEquals("!Sign is not 0!", 0, bn3.getSign());
        assertEquals("!Array not null!", null, bn3.getArrayOfBigNumber());

        BigNumber bn4 = new BigNumber(intArray, 0);
        assertEquals("!Sign is not 0!", 0, bn4.getSign());
        assertEquals("!Array not null!", null, bn4.getArrayOfBigNumber());
    }

    public void testBigNumberToByteArray() {
        byte[] inArray;
        byte[] outArrayOfBigNum;
        byte[] outArrayOfBigInt;
        BigNumber bn;
        BigInteger bi;


        inArray = new byte[]{0, -1, 2, 0};
        bn = new BigNumber(inArray);
        bi = new BigInteger(inArray);

        outArrayOfBigNum = bn.toByteArray();
        outArrayOfBigInt = bi.toByteArray();

        assertEquals("Length is not the same", outArrayOfBigInt.length, outArrayOfBigNum.length);

        for (int i = 0; i < outArrayOfBigInt.length; ++i) {
            assertEquals("", outArrayOfBigInt[i], outArrayOfBigNum[i]);
        }
        ////////////////////////////////
        inArray = new byte[]{-1, -2, -3};
        bn = new BigNumber(inArray);
        bi = new BigInteger(inArray);

        outArrayOfBigNum = bn.toByteArray();
        outArrayOfBigInt = bi.toByteArray();

        assertEquals("Length is not the same", outArrayOfBigInt.length, outArrayOfBigNum.length);

        for (int i = 0; i < outArrayOfBigInt.length; ++i) {
            assertEquals("", outArrayOfBigInt[i], outArrayOfBigNum[i]);
        }
        ///////////////////////////////
        inArray = new byte[]{0, -2, -3, 0};
        bn = new BigNumber(-1, inArray);
        bi = new BigInteger(-1, inArray);

        outArrayOfBigNum = bn.toByteArray();
        outArrayOfBigInt = bi.toByteArray();

        assertEquals("Length is not the same", outArrayOfBigInt.length, outArrayOfBigNum.length);

        for (int i = 0; i < outArrayOfBigInt.length; ++i) {
            assertEquals("", outArrayOfBigInt[i], outArrayOfBigNum[i]);
        }
        ///////////////////////////////
        inArray = new byte[]{-1,37,92,75,120};        
        bn = new BigNumber(inArray);
        bi = new BigInteger(inArray);

        outArrayOfBigNum = bn.toByteArray();
        outArrayOfBigInt = bi.toByteArray();

        assertEquals("Length is not the same", outArrayOfBigInt.length, outArrayOfBigNum.length);

        for (int i = 0; i < outArrayOfBigInt.length; ++i) {
            assertEquals("", outArrayOfBigInt[i], outArrayOfBigNum[i]);
        }
        //////////////RANDOM//////////
        Random rnd = new Random();
        int iter = 1000;
        System.out.println("Testing random BigNumber toByteArray()");
        while ((--iter) > 0) {
            //int N = rnd.nextInt(Short.MAX_VALUE) + 1; //+1 for non-zero length array
            int N=5;
            inArray = new byte[N];

            StringBuilder str = new StringBuilder();
            for (int i = 0; i < N; ++i) {
                byte buff = 0;
                buff = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.nextInt(Byte.MAX_VALUE));
                inArray[i] = buff;
                str.append(buff+",");
            }



            bn = new BigNumber(inArray);
            bi = new BigInteger(inArray);

            outArrayOfBigNum = bn.toByteArray();
            outArrayOfBigInt = bi.toByteArray();

            assertEquals("Length is not the same", outArrayOfBigInt.length, outArrayOfBigNum.length);

            for (int i = 0; i < outArrayOfBigInt.length; ++i) {
                assertEquals("Length= " + outArrayOfBigInt.length + "\n" + 
                        "Error index=" + i + "\n" +
                        "bInt firstElem=" + outArrayOfBigInt[1] + "\n" +
                        "bNum firstElem=" + outArrayOfBigNum[1] + "\n" +
                        "bInt lastElem=" + outArrayOfBigInt[outArrayOfBigInt.length-1] + "\n" +
                        "bNum lastElem=" + outArrayOfBigNum[outArrayOfBigNum.length-1] + "\n" +
                        "array = {" + str.toString() + "}",
                        outArrayOfBigInt[i], outArrayOfBigNum[i]);
            }
        }
    }

}
