/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.bignumberslibrary;

/**
 *
 * @author chibis
 */
public class Operations {

    private Operations() {
    }

    public static int[] summ(int[] num1, int len1, int[] num2, int len2) {

	if (len1 < len2) {
	    int[] tmp = num1;
	    num1 = num2;
	    num2 = tmp;
	    int tmpLen = len1;
	    len1 = len2;
	    len2 = tmpLen;
	}

	//cause for +1 is overflowing;
	int[] result = new int[len1 + 1];
	long sum = 0;

	int i = 0;

	//adding common parts
	while (i < len2) {
	    sum = (num1[i] & Util.LONG_MASK) +
		    (num2[i] & Util.LONG_MASK) + (sum >>> 32);

	    result[i] = (int) (sum);
	    ++i;
	}

	boolean carry = (sum >>> 32 != 0);

	while (i < len1 && carry) {
	    result[i] = num1[i] + 1;
	    if (result[i] == 0) {
		carry = true;
	    } else {
		carry = false;
	    }
	    ++i;
	}

	while (i < len1) {
	    result[i] = num1[i];
	    ++i;
	}
	//Array is ended but we still have overflow
	if (carry) {
	    result[i] = 1;
	}

	return result;

    }

    public static int[] subtract(int[] big, int bigLen, int[] little, int litLen) {
	int result[] = new int[bigLen];
	long difference = 0;

	// Subtract common parts of both numbers
	int i = 0;
	while (i < litLen) {
	    difference = (big[i] & Util.LONG_MASK) -
		    (little[i] & Util.LONG_MASK) +
		    (difference >> 32);
	    result[i] = (int) difference;
	    ++i;
	}

	// Subtract remainder of longer number while borrow propagates
	boolean borrow = (difference >> 32 != 0);

	while (i < bigLen && borrow) {
	    result[i] = big[i] - 1;

	    if (result[i] == -1) {
		borrow = true;
	    } else {
		borrow = false;
	    }
	    ++i;
	}

	// Copy remainder of longer number
	while (i < bigLen) {
	    result[i] = big[i++];
	}

	return result;
    }

    public static int[] simpleMul(int[] array1, int len1, int[] array2, int len2) {

	int[] z = new int[len1 + len2];

	long carry = 0;

	int j = 0;

	for (; j < len1; ++j) {
	    long product = (array2[0] & Util.LONG_MASK) *
		    (array1[j] & Util.LONG_MASK) +
		    carry;
	    z[j] = (int) product;
	    carry = product >>> 32;
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
		carry = product >>> 32;
	    }
	    z[i + j] = (int) carry;
	}
	return z;
    }

    public static byte[] mulFFT(byte[] array1, byte[] array2) {
	Complex[] a = Convert.complexFrom(array1);
	Complex[] b = Convert.complexFrom(array2);

	Complex[] result = Fourier2.fftMultiply(a, b);

	return Convert.byteFrom(result);
    }

    public static byte[] mulFFT2(byte[] array1, byte[] array2) {
	int len = Fourier.extendLength(array1.length, array2.length);
	Complex[] a = Convert.complexFrom(array1, len);
	Complex[] b = Convert.complexFrom(array2, len);

	Complex[] afft = Fourier.iterativeFFT(a, len, false);
	Complex[] bfft = Fourier.iterativeFFT(b, len, false);

	Complex[] cfft = new Complex[len];
	for (int i = 0; i < len; ++i) {
	    cfft[i] = afft[i].mul(bfft[i]);
	}

	Complex[] c = Fourier.iterativeFFT(cfft, len, true);

	return Convert.byteFrom(c);
    }
    
    public static byte[] mulFFT3(short[] array1, short[] array2) {
	int len = Fourier.extendLength(array1.length, array2.length);
	Complex[] a = Convert.complexFrom(array1, len);
	Complex[] b = Convert.complexFrom(array2, len);

	Complex[] afft = Fourier.iterativeFFT(a, len, false);
	Complex[] bfft = Fourier.iterativeFFT(b, len, false);

	Complex[] cfft = new Complex[len];
	for (int i = 0; i < len; ++i) {
	    cfft[i] = afft[i].mul(bfft[i]);
	}

	Complex[] c = Fourier.iterativeFFT(cfft, len, true);

	short[] sh = Convert.shortFrom(c);
	return Convert.byteFrom(sh);
    }    

    public static DivisionData div(BigNumber number, BigNumber divider) {
	final long BASE = ((long)(Integer.MAX_VALUE)-(long)(Integer.MIN_VALUE)+1);
	//Simple cases was already checked in BigNumber class
	//
	// Создать временный массив U, равный A
        // Максимальный размер U на цифру больше A, с учетом
        // возможного удлинения A при нормализации
        int[] arrayOfA = number.getArrayOfBigNumber();
	int[] arrayOfU = new int[arrayOfA.length+1];
	for (int i = 0; i < arrayOfA.length; i++) {
	    arrayOfU[i] = arrayOfA[i];    
	}
	
	int n = divider.getLength();
        int m=arrayOfU.length-n;
	
	int uJ; 
        int vJ; 
	int i;
	
        long temp1;
	long temp2;
	long temp3;
	
	int scale; // коэффициент нормализации

	long qGuess;
        int r;     // догадка для частного и соответствующий остаток
        int borrow;
        int carry; // переносы

	int[] arrayOfB = divider.getArrayOfBigNumber();
	scale = (int) (BASE / ( arrayOfB[n-1] + 1 )); //???
	int[] scaleAr = {scale};
	
	if (scale>1){
	    arrayOfU=simpleMul(arrayOfU, arrayOfU.length, scaleAr, 1);
	    arrayOfB=simpleMul(arrayOfB, arrayOfB.length, scaleAr, 1);
	}

	// Главный цикл шагов деления. Каждая итерация дает очередную цифру частного.
        // vJ - текущий сдвиг B относительно U, используемый при вычитании,
        // по совместительству - индекс очередной цифры частного.
        // uJ – индекс текущей цифры U
	for (vJ = m, uJ=n+vJ; vJ>=0; --vJ, --uJ) {
	    
	    qGuess =(int)((arrayOfU[uJ] * BASE + arrayOfU[uJ - 1]) / arrayOfB[n - 1]);
	    r = (int)((arrayOfU[uJ] * BASE + arrayOfU[uJ - 1]) % arrayOfB[n - 1]);
            // Пока не будут выполнены условия уменьшать частное.
	    while (r < BASE) {
		temp2 = arrayOfB[n - 2] * qGuess;
		temp1 = r * BASE + arrayOfU[uJ - 2];
		if ((temp2 > temp1) || (qGuess == BASE)) {
		    // условия не выполнены, уменьшить qGuess
		    // и досчитать новый остаток
		    --qGuess;
		    r += b[n - 1];
		} else {
		    break;
		}
	    }

	}
	
	return null;
    }

    public static DivisionData simpleDiv(int[] num, int numLen, int divider) {
	long temp;
	int r = 0;
	int[] q = new int[numLen];
	
	for (int i = numLen - 1; i >= 0; --i) { // идти по A, начиная от старшего разряда
	    temp = r * ((long)(Integer.MAX_VALUE)-(long)(Integer.MIN_VALUE)+1) + (num[i] & Util.LONG_MASK);// r – остаток от предыдущего деления
	    q[i] = (int)(temp / divider);        // i-я цифра частного
	    r = (int)(temp - (q[i] * divider));
	}
        int[] R = {r};
	return new DivisionData(q, R);
    }
}
