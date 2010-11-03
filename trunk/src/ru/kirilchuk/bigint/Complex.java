package ru.kirilchuk.bigint;

/**
 * Class that represents complex numbers.
 * 
 * @author Kirilchuk V.E.
 */
public final class Complex {

    public static final double PI = Math.PI;
    public static final double TWO_PI = 2.0 * PI;
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
    public final static Complex cart(double re, double im) {
	return new Complex(re, im);
    }

    //---------------------------------//
    //             PUBLIC              //
    //---------------------------------//

    public Complex add(Complex z) {
	return cart(re + z.re, im + z.im);
    }

    public Complex sub(Complex z) {
	return cart(re - z.re, im - z.im);
    }

    public Complex mul(Complex z) {
	return cart((re * z.re) - (im * z.im), (re * z.im) + (im * z.re));
    }


    public Complex conjugate() {
	Complex result = new Complex(this.re, -this.im);
	return result;
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
