/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import junit.framework.TestCase;
import org.amse.vadim.bignumberslibrary.*;
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
	//Не работает если на конце будет ноль, который отсечётся
	Random rnd = new Random();
	
	int N = rnd.nextInt(Short.MAX_VALUE);
	byte[] byteArray = new byte[N];

	for (int i = 0; i < N; ++i) {
	    byte buff=0;
	    while(buff==0){
	        buff =(byte)(rnd.nextInt(Byte.MAX_VALUE)-2*rnd.nextInt(Byte.MAX_VALUE));
	    }
	    byteArray[i] = buff; 
	}

	BigNumber bn = new BigNumber(byteArray);
	BigInteger bi = new BigInteger(byteArray);

	byte[] byteArray1 = bn.toByteArray();
	byte[] byteArray2 = bi.toByteArray();

	assertEquals(byteArray2[0]+" !Length not same!", byteArray1.length, byteArray2.length);

	for (int i = 0; i < Math.min(byteArray1.length,byteArray2.length); ++i) {
		assertEquals("!Wrong data in array!",byteArray[i],byteArray2[i]);
	}
    }

    public void testIntConstructor() {
	//Не работает если на конце будет ноль, который отсечётся
	Random rnd = new Random();
	
	int N = rnd.nextInt(Short.MAX_VALUE);
	int[] intArray = new int[N];

	for (int i = 0; i < N; ++i) {
	    int buff=0;
	    while(buff==0){
	        buff = rnd.nextInt(Integer.MAX_VALUE)-2*rnd.nextInt(Integer.MAX_VALUE);
	    }
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

    public void testZeroConstructor() {
	BigNumber bn1 = new BigNumber();
	assertEquals("!Sign is not 0!", 0, bn1.getSign());
	assertEquals("!Array not null!", null, bn1.getArrayOfBigNumber());

	byte[] byteArray = null;
	BigNumber bn2 = new BigNumber(byteArray,1);
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
	int[] intArray = {256};

	BigNumber bn = new BigNumber(intArray);

	byte[] byteArray = bn.toByteArray();

	assertEquals("!First element not correct!", 1, byteArray[0]);
	assertEquals("!Second element not correct!", 0, byteArray[1]);

    }
}
