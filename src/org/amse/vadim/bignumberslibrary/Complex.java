package org.amse.vadim.bignumberslibrary;

public class Complex {

    protected static final double PI = Math.PI;
    protected static final double TWO_PI = 2.0 * PI;
    public double re;
    public double im;

    //---------------------------------//
    //           CONSTRUCTORS          //
    //---------------------------------//
    public Complex() {
	this(0.0, 0.0);
    }

    public Complex(double re) {
	this(re, 0.0);
    }

    public Complex(double re, double im) {
	this.re = re;
	this.im = im;
    }

    public Complex(Complex z) {
	this.re = z.re;
	this.im = z.im;
    }

    //---------------------------------//
    //             STATIC              //
    //---------------------------------//
    public static Complex cart(double re, double im) {
	return new Complex(re, im);
    }

    //---------------------------------//
    //             PUBLIC              //
    //---------------------------------//
    public double re() {
	return re;
    }

    public double im() {
	return im;
    }

    public Complex add(Complex z) {
	return cart(re + z.re, im + z.im);
    }

    public Complex sub(Complex z) {
	return cart(re - z.re, im - z.im);
    }

    public Complex mul(Complex z) {
	return cart((re * z.re) - (im * z.im), (re * z.im) + (im * z.re));
    }

    public Complex mul(double alpha) {
	return new Complex(alpha * re, alpha * im);
    }

    public Complex div(Complex z) {
	Complex result = new Complex(this);
	div(result, z.re, z.im);
	return result;
    }

    public Complex conjugate() {
	Complex result = new Complex(this.re, -this.im);
	return result;
    }

    static private void div(Complex z, double x, double y) {
	// Adapted from
	// "Numerical Recipes in Fortran 77: The Art of Scientific Computing"
	// (ISBN 0-521-43064-X)

	double zRe, zIm;
	double scalar;

	if (Math.abs(x) >= Math.abs(y)) {
	    scalar = 1.0 / (x + y * (y / x));

	    zRe = scalar * (z.re + z.im * (y / x));
	    zIm = scalar * (z.im - z.re * (y / x));

	} else {
	    scalar = 1.0 / (x * (x / y) + y);

	    zRe = scalar * (z.re * (x / y) + z.im);
	    zIm = scalar * (z.im * (x / y) - z.re);
	}//endif

	z.re = zRe;
	z.im = zIm;
    }

    @Override
    public String toString() {
	StringBuffer result = new StringBuffer("(");
	result.append(re);

	if (im < 0.0) {                    // ...remembering NaN & Infinity
	    result.append(" - ").append(-im);
	} else if (1.0 / im == Double.NEGATIVE_INFINITY) {
	    result.append(" - ").append(0.0);
	} else {
	    result.append(" + ").append(+im);
	}

	result.append("i)");
	return result.toString();
    }
}
