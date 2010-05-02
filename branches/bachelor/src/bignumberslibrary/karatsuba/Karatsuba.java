/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bignumberslibrary.karatsuba;

import bignumberslibrary.BigNumber;

/**
 *
 * @author Kirilchuk V.E.
 */
public class Karatsuba {

    public static BigNumber multiply(BigNumber x, BigNumber y) {

        int N = Math.max(x.bitLength(), y.bitLength());
        if (N <= 1500) {//main parameter to optimize
            return x.multiply(y); //simple BigInteger multiply
        }

        //number of bits divided by 2, rounded up
        N = N / 2 + N % 2;

        //x= a + b*2^N y= c+ d* 2^N
        BigNumber b = x.shiftRight(N);
        BigNumber a = x.sub(b.shiftLeft(N));
        BigNumber d = y.shiftRight(N);
        BigNumber c = y.sub(d.shiftLeft(N));

        //compute sub-expressions
        BigNumber ac = Karatsuba.multiply(a, c);
        BigNumber bd = Karatsuba.multiply(b, d);
        BigNumber abcd = Karatsuba.multiply(a.add(b), c.add(d));

        return ac.add(abcd.sub(ac).sub(bd).shiftLeft(N)).add(bd.shiftLeft(2*N));
    }
}
