package ru.kirilchuk.bigint;

import static ru.kirilchuk.bigint.util.BitUtilities.*;
import java.util.Arrays;

/*
 * Portions Copyright 1996-2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

/*
 * Portions Copyright (c) 1995  Colin Plumb.  All rights reserved.
 */
/**
 * Immutable arbitrary-precision integers.  All operations behave as if
 * BigIntegers were represented in two's-complement notation (like Java's
 * primitive integer types).  BigInteger provides analogues to all of Java's
 * primitive integer operators, and all relevant methods from java.lang.Math.
 * Additionally, BigInteger provides operations for modular arithmetic, GCD
 * calculation, primality testing, prime generation, bit manipulation,
 * and a few other miscellaneous operations.
 *
 * <p>Semantics of arithmetic operations exactly mimic those of Java's integer
 * arithmetic operators, as defined in <i>The Java Language Specification</i>.
 * For example, division by zero throws an {@code ArithmeticException}, and
 * division of a negative by a positive yields a negative (or zero) remainder.
 * All of the details in the Spec concerning overflow are ignored, as
 * BigIntegers are made as large as necessary to accommodate the results of an
 * operation.
 *
 * <p>Semantics of shift operations extend those of Java's shift operators
 * to allow for negative shift distances.  A right-shift with a negative
 * shift distance results in a left shift, and vice-versa.  The unsigned
 * right shift operator ({@code >>>}) is omitted, as this operation makes
 * little sense in combination with the "infinite word size" abstraction
 * provided by this class.
 *
 * <p>Semantics of bitwise logical operations exactly mimic those of Java's
 * bitwise integer operators.  The binary operators ({@code and},
 * {@code or}, {@code xor}) implicitly perform sign extension on the shorter
 * of the two operands prior to performing the operation.
 *
 * <p>Comparison operations perform signed integer comparisons, analogous to
 * those performed by Java's relational and equality operators.
 *
 * <p>Modular arithmetic operations are provided to compute residues, perform
 * exponentiation, and compute multiplicative inverses.  These methods always
 * return a non-negative result, between {@code 0} and {@code (modulus - 1)},
 * inclusive.
 *
 * <p>Bit operations operate on a single bit of the two's-complement
 * representation of their operand.  If necessary, the operand is sign-
 * extended so that it contains the designated bit.  None of the single-bit
 * operations can produce a BigInteger with a different sign from the
 * BigInteger being operated on, as they affect only a single bit, and the
 * "infinite word size" abstraction provided by this class ensures that there
 * are infinitely many "virtual sign bits" preceding each BigInteger.
 *
 * <p>For the sake of brevity and clarity, pseudo-code is used throughout the
 * descriptions of BigInteger methods.  The pseudo-code expression
 * {@code (i + j)} is shorthand for "a BigInteger whose value is
 * that of the BigInteger {@code i} plus that of the BigInteger {@code j}."
 * The pseudo-code expression {@code (i == j)} is shorthand for
 * "{@code true} if and only if the BigInteger {@code i} represents the same
 * value as the BigInteger {@code j}."  Other pseudo-code expressions are
 * interpreted similarly.
 *
 * <p>All methods and constructors in this class throw
 * {@code NullPointerException} when passed
 * a null object reference for any input parameter.
 *
 * @see     BigDecimal
 * @author  Josh Bloch
 * @author  Michael McCloskey
 * @since JDK1.1
 */
public class BigInteger implements Comparable<BigInteger> {

    /**
     * Additions and changes to BigInteger
     * @author Kirilchuk V.E.
     */

    /**
     * The threshold value for using Karatsuba multiplication.  If the number
     * of ints in both mag arrays are greater than this number, then
     * Karatsuba multiplication will be used. This value is found
     * experimentally to work well.
     */
    private static final int KARATSUBA_THRESHOLD = 64;

    /**
     * The threshold value for using 3-way Toom-Cook multiplication.
     * If the number of ints in both mag arrays are greater than this number,
     * then Toom-Cook multiplication will be used. This value is found
     * experimentally to work well.
     */
    private static final int TOOM_COOK_THRESHOLD = 100;

    /**
     * The threshold value for using FHT multiplication.
     * If the number of ints in both mag arrays are greater than this number,
     * then FHT multiplication will be used. This value is found
     * experimentally to work well.
     */
    private static final int FHT_THRESHOLD = 400;

    /**
     * The maximal length of number in ints for using FHT multiplication.
     * If the number of ints in both mag arrays are greater than this number,
     * then FHT multiplication will be incorrect.
     */
    private static final int FHT_LIMIT = Short.MAX_VALUE >> 2;

    public BigInteger multiply(BigInteger val) {
        if ((val.signum == 0) || (signum == 0)) {
            return ZERO;
        }

        int xlen = mag.length;
        int ylen = val.mag.length;

        if ((xlen < KARATSUBA_THRESHOLD) || (ylen < KARATSUBA_THRESHOLD)) {
            int[] result = multiplyToLen(mag, xlen, val.mag, ylen, null);

            result = trustedStripLeadingZeroInts(result);

            return new BigInteger(result, (signum == val.signum ? 1 : -1));
        } else if ((xlen < TOOM_COOK_THRESHOLD) && (ylen < TOOM_COOK_THRESHOLD)) {
            return multiplyKaratsuba(this, val);
        } else if ((xlen < FHT_THRESHOLD) && (ylen < FHT_THRESHOLD)) {
            return multiplyToomCook3(this, val);
        } else if ((xlen < FHT_LIMIT) && (ylen < FHT_LIMIT)) {
            return multiplyFHT(this, val);
        } else {
            return multiplyToomCook3(this, val);
        }
    }

    /**
     * Multiplies two BigIntegers using the Karatsuba multiplication
     * algorithm.  This is a recursive divide-and-conquer algorithm which is
     * more efficient for large numbers than what is commonly called the
     * "grade-school" algorithm used in multiplyToLen.  If the numbers to be
     * multiplied have length n, the "grade-school" algorithm has an
     * asymptotic complexity of O(n^2).  In contrast, the Karatsuba algorithm
     * has complexity of O(n^(log2(3))), or O(n^1.585).  It achieves this
     * increased performance by doing 3 multiplies instead of 4 when
     * evaluating the product.  As it has some overhead, should be used when
     * both numbers are larger than a certain threshold (found
     * experimentally).
     *
     * See:  http://en.wikipedia.org/wiki/Karatsuba_algorithm
     */
    private final BigInteger multiplyKaratsuba(BigInteger x, BigInteger y) {
        int N = Math.max(x.bitLength(), y.bitLength());

        if (N <= 2000) {             // main parameter to optimize
            int[] result = multiplyToLen(x.mag, x.mag.length, y.mag, y.mag.length, null);
            return new BigInteger(trustedStripLeadingZeroInts(result), x.signum * y.signum);
        }

        // number of bits divided by 2, rounded up
        N = N / 2 + N % 2;

        // x = a + b*2^N y = c + d*2^N
        BigInteger b = x.shiftRight(N);
        BigInteger a = x.subtract(b.shiftLeft(N));
        BigInteger d = y.shiftRight(N);
        BigInteger c = y.subtract(d.shiftLeft(N));

        // compute sub-expressions
        BigInteger ac = multiplyKaratsuba(a, c);
        BigInteger bd = multiplyKaratsuba(b, d);
        BigInteger abcd = multiplyKaratsuba(a.add(b), c.add(d));

        // getting result
        BigInteger result = ac.add(abcd.subtract(ac).subtract(bd).shiftLeft(N)).add(bd.shiftLeft(2 * N));

        return result;
    }

    /**
     * Multiplies two BigIntegers using a 3-way Toom-Cook multiplication
     * algorithm.  This is a recursive divide-and-conquer algorithm which is
     * more efficient for large numbers than Karatsuba multiplication algorithm
     * and "grade-school" algorithm used in multiplyToLen.
     * It achieves this increased asymptotic O(n^1.465)
     * performance by breaking each number into three parts and by doing 5
     * multiplies instead of 9 when evaluating the product.  Due to overhead
     * (additions, shifts, and one division) in the Toom-Cook algorithm, it
     * should only be used when both numbers are larger than a certain
     * threshold (found experimentally).  This threshold is generally larger
     * than that for Karatsuba multiplication, so this algorithm is generally
     * only used when numbers become significantly larger.
     *
     * The algorithm used is the "optimal" 3-way Toom-Cook algorithm outlined
     * by Marco Bodrato.
     *
     *  See: http://bodrato.it/toom-cook
     *       http://bodrato.it/papers/#WAIFI2007
     *
     * "Towards Optimal Toom-Cook Multiplication for Univariate and
     * Multivariate Polynomials in Characteristic 2 and 0." by Marco BODRATO;
     * In C.Carlet and B.Sunar, Eds., "WAIFI'07 proceedings", p. 116-133,
     * LNCS #4547. Springer, Madrid, Spain, June 21-22, 2007.
     *
     */
    private static final BigInteger multiplyToomCook3(BigInteger a, BigInteger b) {
        int alen = a.mag.length;
        int blen = b.mag.length;
        int largest = Math.max(alen, blen);

        // k is the size (in ints) of the lower-order slices.
        int k = (largest + 2) / 3;    // Equal to ceil(largest/3)

        // r is the size (in ints) of the highest-order slice.
        int r = largest - 2 * k;

        // Obtain slices of the numbers. a2 and b2 are the most significant
        // bits of the numbers a and b, and a0 and b0 the least significant.
        BigInteger a0, a1, a2, b0, b1, b2;

        a2 = getToomSlice(a, k, r, 0, largest);
        a1 = getToomSlice(a, k, r, 1, largest);
        a0 = getToomSlice(a, k, r, 2, largest);
        b2 = getToomSlice(b, k, r, 0, largest);
        b1 = getToomSlice(b, k, r, 1, largest);
        b0 = getToomSlice(b, k, r, 2, largest);

        // compute sub expressions
        BigInteger v0, v1, v2, vm1, vinf, t1, t2, tm1, da1, db1;

        v0 = a0.multiply(b0);
        da1 = a2.add(a0);
        db1 = b2.add(b0);
        vm1 = da1.subtract(a1).multiply(db1.subtract(b1));
        da1 = da1.add(a1);
        db1 = db1.add(b1);
        v1 = da1.multiply(db1);
        v2 = da1.add(a2).shiftLeft(1).subtract(a0).multiply(db1.add(b2).shiftLeft(1).subtract(b0));
        vinf = a2.multiply(b2);

        /*
         *  The algorithm requires two divisions by 2 and one by 3.
         *  All divisions are known to be exact, that is, they do not produce
         *  remainders, and all results are positive.  The divisions by 2 are
         *  implemented as right shifts which are relatively efficient, leaving
         *  only an exact division by 3, which is done by a specialized
         *  linear-time algorithm.
         */
        t2 = exactDivideBy3(v2.subtract(vm1));
        tm1 = v1.subtract(vm1).shiftRight(1);
        t1 = v1.subtract(v0);
        t2 = t2.subtract(t1).shiftRight(1);
        t1 = t1.subtract(tm1).subtract(vinf);
        t2 = t2.subtract(vinf.shiftLeft(1));
        tm1 = tm1.subtract(t2);

        // Number of bits to shift left.
        int ss = k * 32;

        // getting result
        BigInteger result =
                vinf.shiftLeft(ss).add(t2).shiftLeft(ss).add(t1).shiftLeft(ss).add(tm1).shiftLeft(ss).add(v0);

        if (a.signum != b.signum) {
            return result.negate();
        } else {
            return result;
        }
    }

    /**
     * Returns a slice of a BigInteger for use in Toom-Cook multiplication.
     *   @param lowerSize The size of the lower-order bit slices.
     *   @param upperSize The size of the higher-order bit slices.
     *   @param slice The index of which slice is requested, which must be a
     *       number from 0 to size-1.  Slice 0 is the highest-order bits,
     *       and slice size-1 are the lowest-order bits.
     *       Slice 0 may be of different size than the other slices.
     *   @param fullsize The size of the larger integer array, used to align
     *       slices to the appropriate position when multiplying different-sized
     *       numbers.
     */
    private static final BigInteger getToomSlice(BigInteger num, int lowerSize, int upperSize, int slice, int fullsize) {
        int start, end, sliceSize, len, offset;

        len = num.mag.length;
        offset = fullsize - len;

        if (slice == 0) {
            start = 0 - offset;
            end = upperSize - 1 - offset;
        } else {
            start = upperSize + (slice - 1) * lowerSize - offset;
            end = start + lowerSize - 1;
        }

        if (start < 0) {
            start = 0;
        }

        if (end < 0) {
            return ZERO;
        }

        sliceSize = (end - start) + 1;

        if (sliceSize <= 0) {
            return ZERO;
        }

        // While performing Toom-Cook, all slices are positive and
        // the sign is adjusted when the final number is composed.
        if ((start == 0) && (sliceSize >= len)) {
            return num.abs();
        }

        int intSlice[] = new int[sliceSize];

        System.arraycopy(num.mag, start, intSlice, 0, sliceSize);

        return new BigInteger(trustedStripLeadingZeroInts(intSlice), 1);
    }

    /**
     * Does an exact division (that is, the remainder is known to be zero)
     * of the specified number by 3.  This is used in Toom-Cook
     * multiplication.  This is an efficient algorithm that runs in linear
     * time.  If the argument is not exactly divisible by 3, results are
     * undefined.  Note that this is expected to be called with positive
     * arguments only.
     */
    private static final BigInteger exactDivideBy3(BigInteger num) {
        int len = num.mag.length;
        int[] result = new int[len];
        long x, w, q, borrow;

        borrow = 0L;

        for (int i = len - 1; i >= 0; i--) {
            x = (num.mag[i] & LONG_MASK);
            w = x - borrow;

            if (borrow > x) {
                borrow = 1L;
            } else {
                borrow = 0L;
            }

            // 0xAAAAAAAB is the modular inverse of 3 (mod 2^32).  Thus,
            // the effect of this is to divide by 3 (mod 2^32).
            // This is much faster than division on most architectures.
            q = (w * 0xAAAAAAABL) & LONG_MASK;
            result[i] = (int) q;

            // Now check the borrow. The second check can of course be
            // eliminated if the first fails.
            if (q >= 0x55555556L) {
                borrow++;

                if (q >= 0xAAAAAAABL) {
                    borrow++;
                }
            }
        }

        result = trustedStripLeadingZeroInts(result);

        return new BigInteger(result, num.signum);
    }

    @Deprecated
    private static final BigInteger multiplyIterativeFFT(BigInteger a, BigInteger b) {
        if ((a.signum == 0) || (b.signum == 0)) {
            return ZERO;
        }

        // Doing fourier transform in shorts
        Complex[] m1 = Fourier.complexFrom(a.mag);
        Complex[] m2 = Fourier.complexFrom(b.mag);
        int[] result = Fourier.mulIterativeFFT(m1, m2);

        return new BigInteger(trustedStripLeadingZeroInts(result), a.signum * b.signum);
    }

    private static final BigInteger multiplyFHT(BigInteger a, BigInteger b) {
        if ((a.signum == 0) || (b.signum == 0)) {
            return ZERO;
        }

        // Doing hartley transform in shorts
        double[] m1 = Hartley.doubleFrom(a.mag);
        double[] m2 = Hartley.doubleFrom(b.mag);
        int[] result = Hartley.mulFHT(m1, m2);

        return new BigInteger(trustedStripLeadingZeroInts(result), a.signum * b.signum);
    }
    // /////////////////////////End of additions/////////////////////////////////////

    /**
     * The BigInteger constant zero.
     *
     * @since   1.2
     */
    public static final BigInteger ZERO         = new BigInteger(new int[0], 0);
    public static final BigInteger NEGATIVE_ONE = new BigInteger(new int[]{1}, -1);

    /**
     * The bitLength of this BigInteger, as returned by bitLength(), or -1
     * (either value is acceptable).
     *
     * @serial
     * @see #bitLength()
     */
    private int bitLength = -1;

    /**
     * The index of the lowest-order int in the magnitude of this BigInteger
     * that contains a nonzero int, or -2 (either value is acceptable).  The
     * least significant int has int-number 0, the next int in order of
     * increasing significance has int-number 1, and so forth.
     */
    private int firstNonzeroIntNum = -2;

    /**
     * The magnitude of this BigInteger, in <i>big-endian</i> order: the
     * zeroth element of this array is the most-significant int of the
     * magnitude.  The magnitude must be "minimal" in that the most-significant
     * int ({@code mag[0]}) must be non-zero.  This is necessary to
     * ensure that there is exactly one representation for each BigInteger
     * value.  Note that this implies that the BigInteger zero has a
     * zero-length mag array.
     */
    int[] mag;

    /**
     * The signum of this BigInteger: -1 for negative, 0 for zero, or
     * 1 for positive.  Note that the BigInteger zero <i>must</i> have
     * a signum of 0.  This is necessary to ensures that there is exactly one
     * representation for each BigInteger value.
     *
     * @serial
     */
    int signum;

    // Constructors
    /**
     * Translates a byte array containing the two's-complement binary
     * representation of a BigInteger into a BigInteger.  The input array is
     * assumed to be in <i>big-endian</i> byte-order: the most significant
     * byte is in the zeroth element.
     *
     * @param  val big-endian two's-complement binary representation of
     *         BigInteger.
     * @throws NumberFormatException {@code val} is zero bytes long.
     */
    public BigInteger(byte[] val) {
        if (val.length == 0) {
            throw new NumberFormatException("Zero length BigInteger");
        }

        if (val[0] < 0) {
            mag = makePositive(val);
            signum = -1;
        } else {
            mag = stripLeadingZeroBytes(val);
            signum = ((mag.length == 0)
                    ? 0
                    : 1);
        }
    }

    /**
     * This private constructor translates an int array containing the
     * two's-complement binary representation of a BigInteger into a
     * BigInteger. The input array is assumed to be in <i>big-endian</i>
     * int-order: the most significant int is in the zeroth element.
     */
    private BigInteger(int[] val) {
        if (val.length == 0) {
            throw new NumberFormatException("Zero length BigInteger");
        }

        if (val[0] < 0) {
            mag = makePositive(val);
            signum = -1;
        } else {
            mag = trustedStripLeadingZeroInts(val);
            signum = ((mag.length == 0)
                    ? 0
                    : 1);
        }
    }

    /**
     * Constructs a BigInteger with the specified value, which may not be zero.
     */
    private BigInteger(long val) {
        if (val < 0) {
            signum = -1;
            val = -val;
        } else {
            signum = 1;
        }

        int highWord = (int) (val >>> 32);

        if (highWord == 0) {
            mag = new int[1];
            mag[0] = (int) val;
        } else {
            mag = new int[2];
            mag[0] = highWord;
            mag[1] = (int) val;
        }
    }

    /**
     * This private constructor is for internal use and assumes that its
     * arguments are correct.
     */
    private BigInteger(byte[] magnitude, int signum) {
        this.signum = ((magnitude.length == 0)
                ? 0
                : signum);
        this.mag = stripLeadingZeroBytes(magnitude);
    }

    /**
     * Translates the sign-magnitude representation of a BigInteger into a
     * BigInteger.  The sign is represented as an integer signum value: -1 for
     * negative, 0 for zero, or 1 for positive.  The magnitude is a byte array
     * in <i>big-endian</i> byte-order: the most significant byte is in the
     * zeroth element.  A zero-length magnitude array is permissible, and will
     * result in a BigInteger value of 0, whether signum is -1, 0 or 1.
     *
     * @param  signum signum of the number (-1 for negative, 0 for zero, 1
     *         for positive).
     * @param  magnitude big-endian binary representation of the magnitude of
     *         the number.
     * @throws NumberFormatException {@code signum} is not one of the three
     *         legal values (-1, 0, and 1), or {@code signum} is 0 and
     *         {@code magnitude} contains one or more non-zero bytes.
     */
    public BigInteger(int signum, byte[] magnitude) {
        this.mag = stripLeadingZeroBytes(magnitude);

        if ((signum < -1) || (signum > 1)) {
            throw (new NumberFormatException("Invalid signum value"));
        }

        if (this.mag.length == 0) {
            this.signum = 0;
        } else {
            if (signum == 0) {
                throw (new NumberFormatException("signum-magnitude mismatch"));
            }

            this.signum = signum;
        }
    }

    /**
     * A constructor for internal use that translates the sign-magnitude
     * representation of a BigInteger into a BigInteger. It checks the
     * arguments and copies the magnitude so this constructor would be
     * safe for external use.
     */
    public BigInteger(int signum, int[] magnitude) {
        this.mag = stripLeadingZeroInts(magnitude);

        if ((signum < -1) || (signum > 1)) {
            throw (new NumberFormatException("Invalid signum value"));
        }

        if (this.mag.length == 0) {
            this.signum = 0;
        } else {
            if (signum == 0) {
                throw (new NumberFormatException("signum-magnitude mismatch"));
            }

            this.signum = signum;
        }
    }

    /**
     * This private constructor differs from its public cousin
     * with the arguments reversed in two ways: it assumes that its
     * arguments are correct, and it doesn't copy the magnitude array.
     */
    public BigInteger(int[] magnitude, int signum) {
        this.signum = ((magnitude.length == 0) ? 0 : signum);
        this.mag = magnitude;
    }

    /**
     * Translates the decimal String representation of a BigInteger into a
     * BigInteger.  The String representation consists of an optional minus
     * sign followed by a sequence of one or more decimal digits.  The
     * character-to-digit mapping is provided by {@code Character.digit}.
     * The String may not contain any extraneous characters (whitespace, for
     * example).
     *
     * @param val decimal String representation of BigInteger.
     * @throws NumberFormatException {@code val} is not a valid representation
     *         of a BigInteger.
     * @see    Character#digit
     */
    public BigInteger(String val) {
        this(val, 10);
    }

    /**
     * Translates the String representation of a BigInteger in the
     * specified radix into a BigInteger.  The String representation
     * consists of an optional minus followed by a sequence of one or
     * more digits in the specified radix.  The character-to-digit
     * mapping is provided by {@code Character.digit}.  The String may
     * not contain any extraneous characters (whitespace, for
     * example).
     *
     * @param val String representation of BigInteger.
     * @param radix radix to be used in interpreting {@code val}.
     * @throws NumberFormatException {@code val} is not a valid representation
     *         of a BigInteger in the specified radix, or {@code radix} is
     *         outside the range from {@link Character#MIN_RADIX} to
     *         {@link Character#MAX_RADIX}, inclusive.
     * @see    Character#digit
     */
    public BigInteger(String val, int radix) {
        int cursor = 0, numDigits;
        int len = val.length();

        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
            throw new NumberFormatException("Radix out of range");
        if (val.length() == 0)
            throw new NumberFormatException("Zero length BigInteger");

        // Check for at most one leading sign
        signum = 1;
        int index = val.lastIndexOf('-');
        if (index != -1) {
            if (index == 0 ) {
                if (val.length() == 1)
                    throw new NumberFormatException("Zero length BigInteger");
                signum = -1;
                cursor = 1;
            } else {
                throw new NumberFormatException("Illegal embedded sign character");
            }
        }

        // Skip leading zeros and compute number of digits in magnitude
        while (cursor < len &&
               Character.digit(val.charAt(cursor), radix) == 0)
            cursor++;
        if (cursor == len) {
            signum = 0;
            mag = ZERO.mag;
            return;
        } else {
            numDigits = len - cursor;
        }

        // Pre-allocate array of expected size. May be too large but can
        // never be too small. Typically exact.
        int numBits = (int)(((numDigits * bitsPerDigit[radix]) >>> 10) + 1);
        int numWords = (numBits + 31) /32;
        mag = new int[numWords];

        // Process first (potentially short) digit group
        int firstGroupLen = numDigits % digitsPerInt[radix];
        if (firstGroupLen == 0)
            firstGroupLen = digitsPerInt[radix];
        String group = val.substring(cursor, cursor += firstGroupLen);
        mag[mag.length - 1] = Integer.parseInt(group, radix);
        if (mag[mag.length - 1] < 0)
            throw new NumberFormatException("Illegal digit");

        // Process remaining digit groups
        int superRadix = intRadix[radix];
        int groupVal = 0;
        while (cursor < val.length()) {
            group = val.substring(cursor, cursor += digitsPerInt[radix]);
            groupVal = Integer.parseInt(group, radix);
            if (groupVal < 0)
                throw new NumberFormatException("Illegal digit");
            destructiveMulAdd(mag, superRadix, groupVal);
        }
        // Required for cases where the array was overallocated.
        mag = trustedStripLeadingZeroInts(mag);
    }

    /**
     * This private constructor is for internal use in converting
     * from a MutableBigInteger object into a BigInteger.
     */
    BigInteger(MutableBigInteger val, int sign) {
        if (val.offset > 0 || val.value.length != val.intLen) {
            mag = new int[val.intLen];
            for(int i=0; i<val.intLen; i++)
                mag[i] = val.value[val.offset+i];
        } else {
            mag = val.value;
        }

        this.signum = (val.intLen == 0) ? 0 : sign;
    }

    public BigInteger multiplyOld(BigInteger val) {
        if ((val.signum == 0) || (signum == 0)) {
            return ZERO;
        }

        int[] result = multiplyToLen(mag, mag.length, val.mag, val.mag.length, null);

        result = trustedStripLeadingZeroInts(result);

        return new BigInteger(result, (signum == val.signum) ? 1 : -1);
    }

    // Arithmetic Operations
    /**
     * Returns a BigInteger whose value is {@code (this + val)}.
     *
     * @param  val value to be added to this BigInteger.
     * @return {@code this + val}
     */
    public BigInteger add(BigInteger val) {
        int[] resultMag;

        if (val.signum == 0) {
            return this;
        }

        if (signum == 0) {
            return val;
        }

        if (val.signum == signum) {
            return new BigInteger(add(mag, val.mag), signum);
        }

        int cmp = intArrayCmp(mag, val.mag);

        if (cmp == 0) {
            return ZERO;
        }

        resultMag = ((cmp > 0)
                ? subtract(mag, val.mag)
                : subtract(val.mag, mag));
        resultMag = trustedStripLeadingZeroInts(resultMag);

        return new BigInteger(resultMag, cmp * signum);
    }

    // Multiply x array times word y in place, and add word z
    private static void destructiveMulAdd(int[] x, int y, int z) {
        // Perform the multiplication word by word
        long ylong = y & LONG_MASK;
        long zlong = z & LONG_MASK;
        int len = x.length;

        long product = 0;
        long carry = 0;
        for (int i = len-1; i >= 0; i--) {
            product = ylong * (x[i] & LONG_MASK) + carry;
            x[i] = (int)product;
            carry = product >>> 32;
        }

        // Perform the addition
        long sum = (x[len-1] & LONG_MASK) + zlong;
        x[len-1] = (int)sum;
        carry = sum >>> 32;
        for (int i = len-2; i >= 0; i--) {
            sum = (x[i] & LONG_MASK) + carry;
            x[i] = (int)sum;
            carry = sum >>> 32;
        }
    }

    /**
     * Adds the contents of the int arrays x and y. This method allocates
     * a new int array to hold the answer and returns a reference to that
     * array.
     */
    private static int[] add(int[] x, int[] y) {

        // If x is shorter, swap the two arrays
        if (x.length < y.length) {
            int[] tmp = x;

            x = y;
            y = tmp;
        }

        int xIndex = x.length;
        int yIndex = y.length;
        int result[] = new int[xIndex];
        long sum = 0;

        // Add common parts of both numbers
        while (yIndex > 0) {
            sum = (x[--xIndex] & LONG_MASK) + (y[--yIndex] & LONG_MASK) + (sum >>> 32);
            result[xIndex] = (int) sum;
        }

        // Copy remainder of longer number while carry propagation is required
        boolean carry = (sum >>> 32 != 0);

        while ((xIndex > 0) && carry) {
            carry = ((result[--xIndex] = x[xIndex] + 1) == 0);
        }

        // Copy remainder of longer number
        while (xIndex > 0) {
            result[--xIndex] = x[xIndex];
        }

        // Grow result if necessary
        if (carry) {
            int newLen = result.length + 1;
            int temp[] = new int[newLen];

            for (int i = 1; i < newLen; i++) {
                temp[i] = result[i - 1];
            }

            temp[0] = 0x01;
            result = temp;
        }

        return result;
    }

    /**
     * Returns a BigInteger whose value is {@code (this - val)}.
     *
     * @param  val value to be subtracted from this BigInteger.
     * @return {@code this - val}
     */
    public BigInteger subtract(BigInteger val) {
        int[] resultMag;

        if (val.signum == 0) {
            return this;
        }

        if (signum == 0) {
            return val.negate();
        }

        if (val.signum != signum) {
            return new BigInteger(add(mag, val.mag), signum);
        }

        int cmp = intArrayCmp(mag, val.mag);

        if (cmp == 0) {
            return ZERO;
        }

        resultMag = ((cmp > 0)
                ? subtract(mag, val.mag)
                : subtract(val.mag, mag));
        resultMag = trustedStripLeadingZeroInts(resultMag);

        return new BigInteger(resultMag, cmp * signum);
    }

    /**
     * Subtracts the contents of the second int arrays (little) from the
     * first (big).  The first int array (big) must represent a larger number
     * than the second.  This method allocates the space necessary to hold the
     * answer.
     */
    private static int[] subtract(int[] big, int[] little) {
        int bigIndex = big.length;
        int result[] = new int[bigIndex];
        int littleIndex = little.length;
        long difference = 0;

        // Subtract common parts of both numbers
        while (littleIndex > 0) {
            difference = (big[--bigIndex] & LONG_MASK) - (little[--littleIndex] & LONG_MASK) + (difference >> 32);
            result[bigIndex] = (int) difference;
        }

        // Subtract remainder of longer number while borrow propagates
        boolean borrow = (difference >> 32 != 0);

        while ((bigIndex > 0) && borrow) {
            borrow = ((result[--bigIndex] = big[bigIndex] - 1) == -1);
        }

        // Copy remainder of longer number
        while (bigIndex > 0) {
            result[--bigIndex] = big[bigIndex];
        }

        return result;
    }

    /**
     * Multiplies int arrays x and y to the specified lengths and places
     * the result into z.
     */
    public int[] multiplyToLen(int[] x, int xlen, int[] y, int ylen, int[] z) {
        int xstart = xlen - 1;
        int ystart = ylen - 1;

        if ((z == null) || (z.length < (xlen + ylen))) {
            z = new int[xlen + ylen];
        }

        long carry = 0;

        for (int j = ystart, k = ystart + 1 + xstart; j >= 0; j--, k--) {
            long product = (y[j] & LONG_MASK) * (x[xstart] & LONG_MASK) + carry;

            z[k] = (int) product;
            carry = product >>> 32;
        }

        z[xstart] = (int) carry;

        for (int i = xstart - 1; i >= 0; i--) {
            carry = 0;

            for (int j = ystart, k = ystart + 1 + i; j >= 0; j--, k--) {
                long product = (y[j] & LONG_MASK) * (x[i] & LONG_MASK) + (z[k] & LONG_MASK) + carry;

                z[k] = (int) product;
                carry = product >>> 32;
            }

            z[i] = (int) carry;
        }

        return z;
    }

    /**
     * Returns a BigInteger whose value is the absolute value of this
     * BigInteger.
     *
     * @return {@code abs(this)}
     */
    public BigInteger abs() {
        return ((signum >= 0)
                ? this
                : this.negate());
    }

    /**
     * Returns a BigInteger whose value is {@code (-this)}.
     *
     * @return {@code -this}
     */
    public BigInteger negate() {
        return new BigInteger(this.mag, -this.signum);
    }

    /**
     * Returns the signum function of this BigInteger.
     *
     * @return -1, 0 or 1 as the value of this BigInteger is negative, zero or
     *         positive.
     */
    public int signum() {
        return this.signum;
    }

    // Shift Operations
    /**
     * Returns a BigInteger whose value is {@code (this << n)}.
     * The shift distance, {@code n}, may be negative, in which case
     * this method performs a right shift.
     * (Computes <tt>floor(this * 2<sup>n</sup>)</tt>.)
     *
     * @param  n shift distance, in bits.
     * @return {@code this << n}
     * @see #shiftRight
     */
    public BigInteger shiftLeft(int n) {
        if (signum == 0) {
            return ZERO;
        }

        if (n == 0) {
            return this;
        }

        if (n < 0) {
            return shiftRight(-n);
        }

        int nInts = n >>> 5;
        int nBits = n & 0x1f;
        int magLen = mag.length;
        int newMag[] = null;

        if (nBits == 0) {
            newMag = new int[magLen + nInts];

            for (int i = 0; i < magLen; i++) {
                newMag[i] = mag[i];
            }
        } else {
            int i = 0;
            int nBits2 = 32 - nBits;
            int highBits = mag[0] >>> nBits2;

            if (highBits != 0) {
                newMag = new int[magLen + nInts + 1];
                newMag[i++] = highBits;
            } else {
                newMag = new int[magLen + nInts];
            }

            int j = 0;

            while (j < magLen - 1) {
                newMag[i++] = mag[j++] << nBits | mag[j] >>> nBits2;
            }

            newMag[i] = mag[j] << nBits;
        }

        return new BigInteger(newMag, signum);
    }
    

    /**
     * Returns a BigInteger whose value is {@code (this >> n)}.  Sign
     * extension is performed.  The shift distance, {@code n}, may be
     * negative, in which case this method performs a left shift.
     * (Computes <tt>floor(this / 2<sup>n</sup>)</tt>.)
     *
     * @param  n shift distance, in bits.
     * @return {@code this >> n}
     * @see #shiftLeft
     */
    public BigInteger shiftRight(int n) {
        if (n == 0) {
            return this;
        }

        if (n < 0) {
            return shiftLeft(-n);
        }

        int nInts = n >>> 5;
        int nBits = n & 0x1f;
        int magLen = mag.length;
        int newMag[] = null;

        // Special case: entire contents shifted off the end
        if (nInts >= magLen) {
            return ((signum >= 0)
                    ? ZERO
                    : NEGATIVE_ONE);
        }

        if (nBits == 0) {
            int newMagLen = magLen - nInts;

            newMag = new int[newMagLen];

            for (int i = 0; i < newMagLen; i++) {
                newMag[i] = mag[i];
            }
        } else {
            int i = 0;
            int highBits = mag[0] >>> nBits;

            if (highBits != 0) {
                newMag = new int[magLen - nInts];
                newMag[i++] = highBits;
            } else {
                newMag = new int[magLen - nInts - 1];
            }

            int nBits2 = 32 - nBits;
            int j = 0;

            while (j < magLen - nInts - 1) {
                newMag[i++] = (mag[j++] << nBits2) | (mag[j] >>> nBits);
            }
        }

        if (signum < 0) {

            // Find out whether any one-bits were shifted off the end.
            boolean onesLost = false;

            for (int i = magLen - 1, j = magLen - nInts; (i >= j) && !onesLost; i--) {
                onesLost = (mag[i] != 0);
            }

            if (!onesLost && (nBits != 0)) {
                onesLost = (mag[magLen - nInts - 1] << (32 - nBits) != 0);
            }

            if (onesLost) {
                newMag = javaIncrement(newMag);
            }
        }

        return new BigInteger(newMag, signum);
    }

    int[] javaIncrement(int[] val) {
        int lastSum = 0;

        for (int i = val.length - 1; (i >= 0) && (lastSum == 0); i--) {
            lastSum = (val[i] += 1);
        }

        if (lastSum == 0) {
            val = new int[val.length + 1];
            val[0] = 1;
        }

        return val;
    }

    // Miscellaneous Bit Operations
    /**
     * Returns the number of bits in the minimal two's-complement
     * representation of this BigInteger, <i>excluding</i> a sign bit.
     * For positive BigIntegers, this is equivalent to the number of bits in
     * the ordinary binary representation.  (Computes
     * {@code (ceil(log2(this < 0 ? -this : this+1)))}.)
     *
     * @return number of bits in the minimal two's-complement
     *         representation of this BigInteger, <i>excluding</i> a sign bit.
     */
    public int bitLength() {

        /*
         * Initialize bitLength field the first time this method is executed.
         * This method depends on the atomicity of int modifies; without
         * this guarantee, it would have to be synchronized.
         */
        if (bitLength == -1) {
            if (signum == 0) {
                bitLength = 0;
            } else {

                // Calculate the bit length of the magnitude
                int magBitLength = ((mag.length - 1) << 5) + bitLen(mag[0]);

                if (signum < 0) {

                    // Check if magnitude is a power of two
                    boolean pow2 = (bitCnt(mag[0]) == 1);

                    for (int i = 1; (i < mag.length) && pow2; i++) {
                        pow2 = (mag[i] == 0);
                    }

                    bitLength = (pow2
                            ? magBitLength - 1
                            : magBitLength);
                } else {
                    bitLength = magBitLength;
                }
            }
        }

        return bitLength;
    }

    /**
     * bitLen(val) is the number of bits in val.
     */
    static int bitLen(int w) {
        // Binary search - decision tree (5 tests, rarely 6)
        return
         (w < 1<<15 ?
          (w < 1<<7 ?
           (w < 1<<3 ?
            (w < 1<<1 ? (w < 1<<0 ? (w<0 ? 32 : 0) : 1) : (w < 1<<2 ? 2 : 3)) :
            (w < 1<<5 ? (w < 1<<4 ? 4 : 5) : (w < 1<<6 ? 6 : 7))) :
           (w < 1<<11 ?
            (w < 1<<9 ? (w < 1<<8 ? 8 : 9) : (w < 1<<10 ? 10 : 11)) :
            (w < 1<<13 ? (w < 1<<12 ? 12 : 13) : (w < 1<<14 ? 14 : 15)))) :
          (w < 1<<23 ?
           (w < 1<<19 ?
            (w < 1<<17 ? (w < 1<<16 ? 16 : 17) : (w < 1<<18 ? 18 : 19)) :
            (w < 1<<21 ? (w < 1<<20 ? 20 : 21) : (w < 1<<22 ? 22 : 23))) :
           (w < 1<<27 ?
            (w < 1<<25 ? (w < 1<<24 ? 24 : 25) : (w < 1<<26 ? 26 : 27)) :
            (w < 1<<29 ? (w < 1<<28 ? 28 : 29) : (w < 1<<30 ? 30 : 31)))));
    }

    static int bitCnt(int val) {
        val -= (0xaaaaaaaa & val) >>> 1;
        val = (val & 0x33333333) + ((val >>> 2) & 0x33333333);
        val = val + (val >>> 4) & 0x0f0f0f0f;
        val += val >>> 8;
        val += val >>> 16;

        return val & 0xff;
    }

    // Comparison Operations
    /**
     * Compares this BigInteger with the specified BigInteger.  This
     * method is provided in preference to individual methods for each
     * of the six boolean comparison operators ({@literal <}, ==,
     * {@literal >}, {@literal >=}, !=, {@literal <=}).  The suggested
     * idiom for performing these comparisons is: {@code
     * (x.compareTo(y)} &lt;<i>op</i>&gt; {@code 0)}, where
     * &lt;<i>op</i>&gt; is one of the six comparison operators.
     *
     * @param  val BigInteger to which this BigInteger is to be compared.
     * @return -1, 0 or 1 as this BigInteger is numerically less than, equal
     *         to, or greater than {@code val}.
     */
    public int compareTo(BigInteger val) {
        return ((signum == val.signum)
                ? signum * intArrayCmp(mag, val.mag)
                : ((signum > val.signum)
                ? 1
                : -1));
    }

    /*
     * Returns -1, 0 or +1 as big-endian unsigned int array arg1 is
     * less than, equal to, or greater than arg2.
     */
    private static int intArrayCmp(int[] arg1, int[] arg2) {
        if (arg1.length < arg2.length) {
            return -1;
        }

        if (arg1.length > arg2.length) {
            return 1;
        }

        // Argument lengths are equal; compare the values
        for (int i = 0; i < arg1.length; i++) {
            long b1 = arg1[i] & LONG_MASK;
            long b2 = arg2[i] & LONG_MASK;

            if (b1 < b2) {
                return -1;
            }

            if (b1 > b2) {
                return 1;
            }
        }

        return 0;
    }

    /**
     * Compares this BigInteger with the specified Object for equality.
     *
     * @param  x Object to which this BigInteger is to be compared.
     * @return {@code true} if and only if the specified Object is a
     *         BigInteger whose value is numerically equal to this BigInteger.
     */
    @Override
    public boolean equals(Object x) {

        // This test is just an optimization, which may or may not help
        if (x == this) {
            return true;
        }

        if (!(x instanceof BigInteger)) {
            return false;
        }

        BigInteger xInt = (BigInteger) x;

        if ((xInt.signum != signum) || (xInt.mag.length != mag.length)) {
            return false;
        }

        for (int i = 0; i < mag.length; i++) {
            if (xInt.mag[i] != mag[i]) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Arrays.hashCode(this.mag);
        hash = 61 * hash + this.signum;
        return hash;
    }

    /**
     * Returns the minimum of this BigInteger and {@code val}.
     *
     * @param  val value with which the minimum is to be computed.
     * @return the BigInteger whose value is the lesser of this BigInteger and
     *         {@code val}.  If they are equal, either may be returned.
     */
    public BigInteger min(BigInteger val) {
        return ((compareTo(val) < 0)
                ? this
                : val);
    }

    /**
     * Returns the maximum of this BigInteger and {@code val}.
     *
     * @param  val value with which the maximum is to be computed.
     * @return the BigInteger whose value is the greater of this and
     *         {@code val}.  If they are equal, either may be returned.
     */
    public BigInteger max(BigInteger val) {
        return ((compareTo(val) > 0)
                ? this
                : val);
    }

    /**
     * Returns a byte array containing the two's-complement
     * representation of this BigInteger.  The byte array will be in
     * <i>big-endian</i> byte-order: the most significant byte is in
     * the zeroth element.  The array will contain the minimum number
     * of bytes required to represent this BigInteger, including at
     * least one sign bit, which is {@code (ceil((this.bitLength() +
     * 1)/8))}.  (This representation is compatible with the
     * {@link #BigInteger(byte[]) (byte[])} constructor.)
     *
     * @return a byte array containing the two's-complement representation of
     *         this BigInteger.
     * @see    #BigInteger(byte[])
     */
    public byte[] toByteArray() {
        int byteLen = bitLength() / 8 + 1;
        byte[] byteArray = new byte[byteLen];

        for (int i = byteLen - 1, bytesCopied = 4, nextInt = 0, intIndex = 0; i >= 0; i--) {
            if (bytesCopied == 4) {
                nextInt = getInt(intIndex++);
                bytesCopied = 1;
            } else {
                nextInt >>>= 8;
                bytesCopied++;
            }

            byteArray[i] = (byte) nextInt;
        }

        return byteArray;
    }

    /**
     * Converts this BigInteger to an {@code int}.  This
     * conversion is analogous to a <a
     * href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#25363"><i>narrowing
     * primitive conversion</i></a> from {@code long} to
     * {@code int} as defined in the <a
     * href="http://java.sun.com/docs/books/jls/html/">Java Language
     * Specification</a>: if this BigInteger is too big to fit in an
     * {@code int}, only the low-order 32 bits are returned.
     * Note that this conversion can lose information about the
     * overall magnitude of the BigInteger value as well as return a
     * result with the opposite sign.
     *
     * @return this BigInteger converted to an {@code int}.
     */
    public int intValue() {
        int result = 0;

        result = getInt(0);

        return result;
    }

    /**
     * Converts this BigInteger to a {@code long}.  This
     * conversion is analogous to a <a
     * href="http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html#25363"><i>narrowing
     * primitive conversion</i></a> from {@code long} to
     * {@code int} as defined in the <a
     * href="http://java.sun.com/docs/books/jls/html/">Java Language
     * Specification</a>: if this BigInteger is too big to fit in a
     * {@code long}, only the low-order 64 bits are returned.
     * Note that this conversion can lose information about the
     * overall magnitude of the BigInteger value as well as return a
     * result with the opposite sign.
     *
     * @return this BigInteger converted to a {@code long}.
     */
    public long longValue() {
        long result = 0;

        for (int i = 1; i >= 0; i--) {
            result = (result << 32) + (getInt(i) & LONG_MASK);
        }

        return result;
    }

    /**
     * Returns a copy of the input array stripped of any leading zero bytes.
     */
    private static int[] stripLeadingZeroInts(int val[]) {
        int byteLength = val.length;
        int keep;

        // Find first nonzero byte
        for (keep = 0; (keep < val.length) && (val[keep] == 0); keep++);

        int result[] = new int[val.length - keep];

        for (int i = 0; i < val.length - keep; i++) {
            result[i] = val[keep + i];
        }

        return result;
    }

    /**
     * Returns the input array stripped of any leading zero bytes.
     * Since the source is trusted the copying may be skipped.
     */
    public static int[] trustedStripLeadingZeroInts(int val[]) {
        int byteLength = val.length;
        int keep;

        // Find first nonzero byte
        for (keep = 0; (keep < val.length) && (val[keep] == 0); keep++);

        // Only perform copy if necessary
        if (keep > 0) {
            int result[] = new int[val.length - keep];

            for (int i = 0; i < val.length - keep; i++) {
                result[i] = val[keep + i];
            }

            return result;
        }

        return val;
    }

    /**
     * Returns a copy of the input array stripped of any leading zero bytes.
     */
    private static int[] stripLeadingZeroBytes(byte a[]) {
        int byteLength = a.length;
        int keep;

        // Find first nonzero byte
        for (keep = 0; (keep < a.length) && (a[keep] == 0); keep++);

        // Allocate new array and copy relevant part of input array
        int intLength = ((byteLength - keep) + 3) / 4;
        int[] result = new int[intLength];
        int b = byteLength - 1;

        for (int i = intLength - 1; i >= 0; i--) {
            result[i] = a[b--] & 0xff;

            int bytesRemaining = b - keep + 1;
            int bytesToTransfer = Math.min(3, bytesRemaining);

            for (int j = 8; j <= 8 * bytesToTransfer; j += 8) {
                result[i] |= ((a[b--] & 0xff) << j);
            }
        }

        return result;
    }

    /**
     * Takes an array a representing a negative 2's-complement number and
     * returns the minimal (no leading zero bytes) unsigned whose value is -a.
     */
    private static int[] makePositive(byte a[]) {
        int keep, k;
        int byteLength = a.length;

        // Find first non-sign (0xff) byte of input
        for (keep = 0; (keep < byteLength) && (a[keep] == -1); keep++);

        /*
         *  Allocate output array.  If all non-sign bytes are 0x00, we must
         * allocate space for one extra output byte.
         */
        for (k = keep; (k < byteLength) && (a[k] == 0); k++);

        int extraByte = (k == byteLength)
                ? 1
                : 0;
        int intLength = ((byteLength - keep + extraByte) + 3) / 4;
        int result[] = new int[intLength];

        /*
         *  Copy one's complement of input into output, leaving extra
         * byte (if it exists) == 0x00
         */
        int b = byteLength - 1;

        for (int i = intLength - 1; i >= 0; i--) {
            result[i] = a[b--] & 0xff;

            int numBytesToTransfer = Math.min(3, b - keep + 1);

            if (numBytesToTransfer < 0) {
                numBytesToTransfer = 0;
            }

            for (int j = 8; j <= 8 * numBytesToTransfer; j += 8) {
                result[i] |= ((a[b--] & 0xff) << j);
            }

            // Mask indicates which bits must be complemented
            int mask = -1 >>> (8 * (3 - numBytesToTransfer));

            result[i] = ~result[i] & mask;
        }

        // Add one to one's complement to generate two's complement
        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = (int) ((result[i] & LONG_MASK) + 1);

            if (result[i] != 0) {
                break;
            }
        }

        return result;
    }

    /**
     * Takes an array a representing a negative 2's-complement number and
     * returns the minimal (no leading zero ints) unsigned whose value is -a.
     */
    private static int[] makePositive(int a[]) {
        int keep, j;

        // Find first non-sign (0xffffffff) int of input
        for (keep = 0; (keep < a.length) && (a[keep] == -1); keep++);

        /*
         *  Allocate output array.  If all non-sign ints are 0x00, we must
         * allocate space for one extra output int.
         */
        for (j = keep; (j < a.length) && (a[j] == 0); j++);

        int extraInt = ((j == a.length)
                ? 1
                : 0);
        int result[] = new int[a.length - keep + extraInt];

        /*
         *  Copy one's complement of input into output, leaving extra
         * int (if it exists) == 0x00
         */
        for (int i = keep; i < a.length; i++) {
            result[i - keep + extraInt] = ~a[i];
        }

        // Add one to one's complement to generate two's complement
        for (int i = result.length - 1; ++result[i] == 0; i--);

        return result;
    }

    /* Returns an int of sign bits */
    private int signInt() {
        return (signum < 0)
                ? -1
                : 0;
    }

    /**
     * Returns the specified int of the little-endian two's complement
     * representation (int 0 is the least significant).  The int number can
     * be arbitrarily high (values are logically preceded by infinitely many
     * sign ints).
     */
    private int getInt(int n) {
        if (n < 0) {
            return 0;
        }

        if (n >= mag.length) {
            return signInt();
        }

        int magInt = mag[mag.length - n - 1];

        return ((signum >= 0)
                ? magInt
                : ((n <= firstNonzeroIntNum())
                ? -magInt
                : ~magInt));
    }

    /**
     * Returns the index of the int that contains the first nonzero int in the
     * little-endian binary representation of the magnitude (int 0 is the
     * least significant). If the magnitude is zero, return value is undefined.
     */
    private int firstNonzeroIntNum() {

        /*
         * Initialize firstNonzeroIntNum field the first time this method is
         * executed. This method depends on the atomicity of int modifies;
         * without this guarantee, it would have to be synchronized.
         */
        if (firstNonzeroIntNum == -2) {

            // Search for the first nonzero int
            int i;

            for (i = mag.length - 1; (i >= 0) && (mag[i] == 0); i--);
            firstNonzeroIntNum = mag.length - i - 1;
        }

        return firstNonzeroIntNum;
    }

    /**
     * Returns the String representation of this BigInteger in the
     * given radix.  If the radix is outside the range from {@link
     * Character#MIN_RADIX} to {@link Character#MAX_RADIX} inclusive,
     * it will default to 10 (as is the case for
     * {@code Integer.toString}).  The digit-to-character mapping
     * provided by {@code Character.forDigit} is used, and a minus
     * sign is prepended if appropriate.  (This representation is
     * compatible with the {@link #BigInteger(String, int) (String,
     * int)} constructor.)
     *
     * @param  radix  radix of the String representation.
     * @return String representation of this BigInteger in the given radix.
     * @see    Integer#toString
     * @see    Character#forDigit
     * @see    #BigInteger(java.lang.String, int)
     */
    public String toString(int radix) {
        if (signum == 0) {
            return "0";
        }
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            radix = 10;
        }

        // Compute upper bound on number of digit groups and allocate space
        int maxNumDigitGroups = (4 * mag.length + 6) / 7;
        String digitGroup[] = new String[maxNumDigitGroups];

        // Translate number to string, a digit group at a time
        BigInteger tmp = this.abs();
        int numGroups = 0;
        while (tmp.signum != 0) {
            BigInteger d = longRadix[radix];

            MutableBigInteger q = new MutableBigInteger(),
                              r = new MutableBigInteger(),
                              a = new MutableBigInteger(tmp.mag),
                              b = new MutableBigInteger(d.mag);
            a.divide(b, q, r);
            BigInteger q2 = new BigInteger(q, tmp.signum * d.signum);
            BigInteger r2 = new BigInteger(r, tmp.signum * d.signum);

            digitGroup[numGroups++] = Long.toString(r2.longValue(), radix);
            tmp = q2;
        }

        // Put sign (if any) and first digit group into result buffer
        StringBuilder buf = new StringBuilder(numGroups * digitsPerLong[radix] + 1);
        if (signum < 0) {
            buf.append('-');
        }
        buf.append(digitGroup[numGroups - 1]);

        // Append remaining digit groups padded with leading zeros
        for (int i = numGroups - 2; i >= 0; i--) {
            // Prepend (any) leading zeros for this digit group
            int numLeadingZeros = digitsPerLong[radix] - digitGroup[i].length();
            if (numLeadingZeros != 0) {
                buf.append(zeros[numLeadingZeros]);
            }
            buf.append(digitGroup[i]);
        }
        return buf.toString();
    }

    /* zero[i] is a string of i consecutive zeros. */
    private static String zeros[] = new String[64];

    static {
        zeros[63] =
                "000000000000000000000000000000000000000000000000000000000000000";
        for (int i = 0; i < 63; i++) {
            zeros[i] = zeros[63].substring(0, i);
        }
    }

    /*
     * These two arrays are the integer analogue of above.
     */
    private static int digitsPerInt[] = {0, 0, 30, 19, 15, 13, 11,
        11, 10, 9, 9, 8, 8, 8, 8, 7, 7, 7, 7, 7, 7, 7, 6, 6, 6, 6,
        6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 5};

    private static int intRadix[] = {0, 0,
        0x40000000, 0x4546b3db, 0x40000000, 0x48c27395, 0x159fd800,
        0x75db9c97, 0x40000000, 0x17179149, 0x3b9aca00, 0xcc6db61,
        0x19a10000, 0x309f1021, 0x57f6c100, 0xa2f1b6f,  0x10000000,
        0x18754571, 0x247dbc80, 0x3547667b, 0x4c4b4000, 0x6b5a6e1d,
        0x6c20a40,  0x8d2d931,  0xb640000,  0xe8d4a51,  0x1269ae40,
        0x17179149, 0x1cb91000, 0x23744899, 0x2b73a840, 0x34e63b41,
        0x40000000, 0x4cfa3cc1, 0x5c13d840, 0x6d91b519, 0x39aa400
    };

    private static int digitsPerLong[] = {0, 0,
        62, 39, 31, 27, 24, 22, 20, 19, 18, 18, 17, 17, 16, 16, 15, 15, 15, 14,
        14, 14, 14, 13, 13, 13, 13, 13, 13, 12, 12, 12, 12, 12, 12, 12, 12};

    private static BigInteger longRadix[] = {null, null,
        valueOf(0x4000000000000000L), valueOf(0x383d9170b85ff80bL),
        valueOf(0x4000000000000000L), valueOf(0x6765c793fa10079dL),
        valueOf(0x41c21cb8e1000000L), valueOf(0x3642798750226111L),
        valueOf(0x1000000000000000L), valueOf(0x12bf307ae81ffd59L),
        valueOf(0xde0b6b3a7640000L), valueOf(0x4d28cb56c33fa539L),
        valueOf(0x1eca170c00000000L), valueOf(0x780c7372621bd74dL),
        valueOf(0x1e39a5057d810000L), valueOf(0x5b27ac993df97701L),
        valueOf(0x1000000000000000L), valueOf(0x27b95e997e21d9f1L),
        valueOf(0x5da0e1e53c5c8000L), valueOf(0xb16a458ef403f19L),
        valueOf(0x16bcc41e90000000L), valueOf(0x2d04b7fdd9c0ef49L),
        valueOf(0x5658597bcaa24000L), valueOf(0x6feb266931a75b7L),
        valueOf(0xc29e98000000000L), valueOf(0x14adf4b7320334b9L),
        valueOf(0x226ed36478bfa000L), valueOf(0x383d9170b85ff80bL),
        valueOf(0x5a3c23e39c000000L), valueOf(0x4e900abb53e6b71L),
        valueOf(0x7600ec618141000L), valueOf(0xaee5720ee830681L),
        valueOf(0x1000000000000000L), valueOf(0x172588ad4f5f0981L),
        valueOf(0x211e44f7d02c1000L), valueOf(0x2ee56725f06e5c71L),
        valueOf(0x41c21cb8e1000000L)};

    /**
     * Returns a BigInteger whose value is equal to that of the
     * specified {@code long}.  This "static factory method" is
     * provided in preference to a ({@code long}) constructor
     * because it allows for reuse of frequently used BigIntegers.
     *
     * @param  val value of the BigInteger to return.
     * @return a BigInteger with the specified value.
     */
    public static BigInteger valueOf(long val) {
        // If -MAX_CONSTANT < val < MAX_CONSTANT, return stashed constant
        if (val == 0) {
            return ZERO;
        }
        if (val > 0 && val <= MAX_CONSTANT) {
            return posConst[(int) val];
        } else if (val < 0 && val >= -MAX_CONSTANT) {
            return negConst[(int) -val];
        }

        return new BigInteger(val);
    }

    /**
     * Initialize static constant array when class is loaded.
     */
    private final static int MAX_CONSTANT = 16;
    private static BigInteger posConst[] = new BigInteger[MAX_CONSTANT+1];
    private static BigInteger negConst[] = new BigInteger[MAX_CONSTANT+1];
    static {
        for (int i = 1; i <= MAX_CONSTANT; i++) {
            int[] magnitude = new int[1];
            magnitude[0] = i;
            posConst[i] = new BigInteger(magnitude,  1);
            negConst[i] = new BigInteger(magnitude, -1);
        }
    }

    static int trailingZeroCnt(int val) {
        // Loop unrolled for performance
        int byteVal = val & 0xff;
        if (byteVal != 0)
            return trailingZeroTable[byteVal];

        byteVal = (val >>> 8) & 0xff;
        if (byteVal != 0)
            return trailingZeroTable[byteVal] + 8;

        byteVal = (val >>> 16) & 0xff;
        if (byteVal != 0)
            return trailingZeroTable[byteVal] + 16;

        byteVal = (val >>> 24) & 0xff;
        return trailingZeroTable[byteVal] + 24;
    }

    /*
     * trailingZeroTable[i] is the number of trailing zero bits in the binary
     * representation of i.
     */
    final static byte trailingZeroTable[] = {
      -25, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        7, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0,
        4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0};

    // bitsPerDigit in the given radix times 1024
    // Rounded up to avoid underallocation.
    private static long bitsPerDigit[] = { 0, 0,
        1024, 1624, 2048, 2378, 2648, 2875, 3072, 3247, 3402, 3543, 3672,
        3790, 3899, 4001, 4096, 4186, 4271, 4350, 4426, 4498, 4567, 4633,
        4696, 4756, 4814, 4870, 4923, 4975, 5025, 5074, 5120, 5166, 5210,
                                           5253, 5295};

    // shifts a up to len left n bits assumes no leading zeros, 0<=n<32
    static void primitiveLeftShift(int[] a, int len, int n) {
        if (len == 0 || n == 0)
            return;

        int n2 = 32 - n;
        for (int i=0, c=a[i], m=i+len-1; i<m; i++) {
            int b = c;
            c = a[i+1];
            a[i] = (b << n) | (c >>> n2);
        }
        a[len-1] <<= n;
    }

    /**
     * Returns the decimal String representation of this BigInteger.
     * The digit-to-character mapping provided by
     * {@code Character.forDigit} is used, and a minus sign is
     * prepended if appropriate.  (This representation is compatible
     * with the {@link #BigInteger(String) (String)} constructor, and
     * allows for String concatenation with Java's + operator.)
     *
     * @return decimal String representation of this BigInteger.
     * @see    Character#forDigit
     * @see    #BigInteger(java.lang.String)
     */
    @Override
    public String toString() {
        return toString(10);
    }
}
