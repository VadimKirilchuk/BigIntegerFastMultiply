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
public class operationsTest extends TestCase {

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
        ///////determination
	Random rnd = new Random();
	int N1 = rnd.nextInt(Short.MAX_VALUE);
	int N2 = rnd.nextInt(Short.MAX_VALUE);

	byte[] byteArray1 = new byte[N1];
	byte[] byteArray2 = new byte[N2];

	for (int i = 0; i < byteArray1.length; i++) {
	    byteArray1[i]=(byte)(rnd.nextInt(Byte.MAX_VALUE)-2*rnd.nextInt(Byte.MAX_VALUE));
	}
	
	for (int i = 0; i < byteArray2.length; i++) {
	    byteArray2[i] = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.nextInt(Byte.MAX_VALUE));
	}
	
	BigNumber bn1 = new BigNumber(byteArray1,1);
	BigNumber bn2 = new BigNumber(byteArray2,1);
	BigInteger bi1=new BigInteger(byteArray1);
	BigInteger bi2=new BigInteger(byteArray2);

	byte[] res1;
	byte[] res2;
	//////////end of determination
	

	////////////Summ of two positive numbers
	BigNumber resultBn = bn1.add(bn2);
        res1=resultBn.toByteArray();
	
	BigInteger resultBi=bi1.add(bi2);
	res2=resultBi.toByteArray();
	
	assertEquals(res2[0]+"!Length not the same!",res1.length, res2.length);
	
	for (int i = 0; i < Math.min(res1.length,res2.length); i++) {
	    assertEquals("!Wrong element!",res1[i], res2[i]);
	}
	////////end of two positive
	/*
	///////summ of positive and negative
	//bn1
	bn2=bn2.negate();
	//bi1
	bi2=bi2.negate();
	
	resultBn=bn1.add(bn2);
	resultBi=bi1.add(bi2);
	
	//returns 1 if bn1>bn2 and -1 if bn1<bn2
	int cmp = bn1.compare(bn2);
	
	if (cmp!=0)
	    res1=resultBn.toByteArray();
	    if(cmp>0){
	    res2 = resultBi.toByteArray();
	} else if (cmp < 0) {
	    res2 = resultBi.negate().toByteArray();
	}

	assertEquals(res2[0]+"!Length not the same!", res1.length, res2.length);

	for (int i = 0; i < Math.min(res1.length, res2.length); i++) {
	    assertEquals("!Wrong element!", res1[i], res2[i]);
	}
	//////end of positive and negative summ
	
	/////summ of two negative
	bn1=bn1.negate();
	//bn2
	bi1=bi1.negate();
	//bi2

	resultBn = bn1.add(bn2);
	resultBi = bi1.add(bi2);

	res1 = resultBn.toByteArray();
	//must negate because in biginteger method toByteArray watches sign!!
	res2 = resultBi.negate().toByteArray();

	assertEquals(res2[0]+"!Length not the same!", res1.length, res2.length);

	for (int i = 0; i < Math.min(res1.length, res2.length); i++) {
	    assertEquals("!Wrong element!", res1[i], res2[i]);
	}
	//////end of negative and negative summ
	*/
	
    }

    public void testSubtracting() {
        ///////determination
	Random rnd = new Random();
	int N1 = rnd.nextInt(Short.MAX_VALUE);
	int N2 = rnd.nextInt(Short.MAX_VALUE);

	byte[] byteArray1 = new byte[N1];
	byte[] byteArray2 = new byte[N2];

	//Старший коэфицент д.б >0 иначе, бигинтеджер подумает,Что ему передали отрицательное число.
	byteArray1[0]=(byte)rnd.nextInt(Byte.MAX_VALUE);
	for (int i = 1; i < byteArray1.length; i++) {
	    byteArray1[i]=(byte)(rnd.nextInt(Byte.MAX_VALUE)-2*rnd.nextInt(Byte.MAX_VALUE));
	}
	
	byteArray2[0] = (byte) rnd.nextInt(Byte.MAX_VALUE);
	for (int i = 1; i < byteArray2.length; i++) {
	    byteArray2[i] = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.nextInt(Byte.MAX_VALUE));
	}
	
	BigNumber bn1 = new BigNumber(byteArray1,1);
	BigNumber bn2 = new BigNumber(byteArray2,1);
	BigInteger bi1=new BigInteger(byteArray1);
	BigInteger bi2=new BigInteger(byteArray2);

	byte[] res1={0};
	byte[] res2={0};
	//////////end of determination
	

	////////////Sub of two positive numbers
	BigNumber resultBn = bn1.sub(bn2);
	BigInteger resultBi=bi1.subtract(bi2);
	
	int cmp = bn1.compare(bn2);
	
	if (cmp!=0)
	    res1=resultBn.toByteArray();
	    if(cmp>0){
	    res2 = resultBi.toByteArray();
	} else if (cmp < 0) {
	    res2 =resultBi.negate().toByteArray();
	}
	
	assertEquals(res2[0]+"!Length not the same!",res1.length, res2.length);
	
	for (int i = 0; i < Math.min(res1.length,res2.length); i++) {
	    assertEquals("!Wrong element!",res1[i], res2[i]);
	}
	////////end of two positive sub

	///////sub of positive and negative
	//bn1
	bn2=bn2.negate();
	//bi1
	bi2=bi2.negate();
	
	resultBn = bn1.sub(bn2);
        res1=resultBn.toByteArray();
	
	resultBi=bi1.subtract(bi2);
	res2=resultBi.toByteArray();


	assertEquals(res2[0]+"!Length not the same!", res1.length, res2.length);

	for (int i = 0; i < Math.min(res1.length, res2.length); i++) {
	    assertEquals("!Wrong element!", res1[i], res2[i]);
	}
	//////end of positive and negative sub
	
	/////sub of two negative
	bn1=bn1.negate();
	//bn2
	bi1=bi1.negate();
	//bi2

	resultBn = bn1.sub(bn2);
	resultBi=bi1.subtract(bi2);
	
	cmp = bn1.compare(bn2);
	
	if (cmp!=0)
	    res1=resultBn.toByteArray();
	    if(cmp<0){
	    res2 = resultBi.toByteArray();
	} else if (cmp > 0) {
	    res2 =resultBi.negate().toByteArray();
	}

	assertEquals(res2[0]+"!Length not the same!", res1.length, res2.length);

	for (int i = 0; i < Math.min(res1.length, res2.length); i++) {
	    assertEquals("!Wrong element!", res1[i], res2[i]);
	}
	//////end of negative and negative summ
    }
    
    //Failing when BigInteger  toByteArray is something like (0; x; x; x; x)
    //cause BigInteger doesn`t cut this leading zero
    public static void testSimpleMultiply(){
	///////determination
	Random rnd = new Random();
	int N1 = rnd.nextInt(Byte.MAX_VALUE);
	int N2 = rnd.nextInt(Byte.MAX_VALUE);

	byte[] byteArray1 = new byte[N1];
	byte[] byteArray2 = new byte[N2];

	//Старший коэфицент д.б >0 иначе, бигинтеджер подумает,Что ему передали отрицательное число.
	byteArray1[0]=(byte)rnd.nextInt(Byte.MAX_VALUE);
	for (int i = 1; i < byteArray1.length; i++) {
	    byteArray1[i]=(byte)(rnd.nextInt(Byte.MAX_VALUE)-2*rnd.nextInt(Byte.MAX_VALUE));
	}
	
	byteArray2[0] = (byte) rnd.nextInt(Byte.MAX_VALUE);
	for (int i = 1; i < byteArray2.length; i++) {
	    byteArray2[i] = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.nextInt(Byte.MAX_VALUE));
	}
	
	BigNumber bn1 = new BigNumber(byteArray1);
	BigNumber bn2 = new BigNumber(byteArray2);
	BigInteger bi1=new BigInteger(byteArray1);
	BigInteger bi2=new BigInteger(byteArray2);

	byte[] res1;
	byte[] res2;
	//////////end of determination
	
	BigNumber resultBn= bn1.mul(bn2);
	res1=resultBn.toByteArray();
	
	BigInteger resultBi = bi1.multiply(bi2);
	res2=resultBi.toByteArray();
	
	assertEquals(res2[0]+"!Length not the same!", res1.length, res2.length);
	
	for (int i = 0; i < Math.min(res1.length, res2.length); i++) {
	    assertEquals("!Wrong element #"+i+"!", res1[i], res2[i]);
	}
	
	////////////Test of 2^31 mul
	byteArray1[0]=(byte)rnd.nextInt(Byte.MAX_VALUE);
	for (int i = 1; i < byteArray1.length; i++) {
	    byteArray1[i]=(byte)(-128+rnd.nextInt(100));
	}
	
	byteArray2[0] = (byte) rnd.nextInt(Byte.MAX_VALUE);
	for (int i = 1; i < byteArray2.length; i++) {
	    byteArray2[i] = (byte)(-128+rnd.nextInt(100));
	}
	bn1 = new BigNumber(byteArray1);
	bn2 = new BigNumber(byteArray2);
	bi1 = new BigInteger(byteArray1);
	bi2 = new BigInteger(byteArray2);
	
	resultBn= bn1.mul(bn2);
	res1=resultBn.toByteArray();
	
	resultBi = bi1.multiply(bi2);
	res2=resultBi.toByteArray();
	
	assertEquals(res2[0]+"!Length not the same!", res1.length, res2.length);
	
	for (int i = 0; i < Math.min(res1.length, res2.length); i++) {
	    assertEquals("!Wrong element #"+i+"!", res1[i], res2[i]);
	}
    }

    public static void testMulFFT(){
        ///////determination
	Random rnd = new Random();
	int N1 = rnd.nextInt(Short.MAX_VALUE/2);
	int N2 = rnd.nextInt(Short.MAX_VALUE/2);

	byte[] byteArray1 = new byte[N1];
	byte[] byteArray2 = new byte[N2];

	//Старший коэфицент д.б >0 иначе, бигинтеджер подумает,Что ему передали отрицательное число.
	byteArray1[0]=(byte)rnd.nextInt(Byte.MAX_VALUE);
	for (int i = 1; i < byteArray1.length; i++) {
	    byteArray1[i]=(byte)(rnd.nextInt(Byte.MAX_VALUE)-2*rnd.nextInt(Byte.MAX_VALUE));
	}
	
	byteArray2[0] = (byte) rnd.nextInt(Byte.MAX_VALUE);
	for (int i = 1; i < byteArray2.length; i++) {
	    byteArray2[i] = (byte) (rnd.nextInt(Byte.MAX_VALUE) - 2 * rnd.nextInt(Byte.MAX_VALUE));
	}
	
	BigNumber bn1 = new BigNumber(byteArray1);
	BigNumber bn2 = new BigNumber(byteArray2);
	BigInteger bi1=new BigInteger(byteArray1);
	BigInteger bi2=new BigInteger(byteArray2);

	byte[] res1;
	byte[] res2;
	//////////end of determination
	
	BigNumber resultBn = bn1.mulFFT2(bn2);
        res1=resultBn.toByteArray();
	
	BigInteger resultBi=bi1.multiply(bi2);
	res2=resultBi.toByteArray();
	
	
	assertEquals(res2[0]+"!Length not the same!",res1.length, res2.length);
	
	for (int i = 0; i < Math.min(res1.length,res2.length); i++) {
	    assertEquals("!Wrong element!",res1[i], res2[i]);
	}
    }
}
