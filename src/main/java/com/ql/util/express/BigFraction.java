package com.ql.util.express;

import com.ql.util.express.util.FastMath;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 分数计算
 * @author zhanglei
 */
public class BigFraction extends Number implements Comparable<BigFraction> {

    /** A fraction representing "0". */
    public static final BigFraction ZERO = new BigFraction(0);

    /** A fraction representing "1". */
    public static final BigFraction ONE = new BigFraction(1);

    /** A fraction representing "2 / 1". */
    public static final BigFraction TWO = new BigFraction(2);

    /** The numerator. */
    private final BigInteger numerator;

    /** The denominator. */
    private final BigInteger denominator;

    public BigFraction(final BigInteger num) {
        this(num, BigInteger.ONE);
    }

    public BigFraction(BigInteger num, BigInteger den) {
        if (num == null){
            throw new IllegalArgumentException("numerator null is not allowed");
        }
        if (den == null){
            throw new IllegalArgumentException("denominator null is not allowed");
        }
        if (den.signum() == 0) {
            throw new IllegalArgumentException("denominator must be different from 0");
        }
        if (num.signum() == 0) {
            numerator   = BigInteger.ZERO;
            denominator = BigInteger.ONE;
        } else {

            // reduce numerator and denominator by greatest common denominator
            final BigInteger gcd = num.gcd(den);
            if (BigInteger.ONE.compareTo(gcd) < 0) {
                num = num.divide(gcd);
                den = den.divide(gcd);
            }

            // move sign to numerator
            if (den.signum() == -1) {
                num = num.negate();
                den = den.negate();
            }

            // store the values in the final fields
            numerator   = num;
            denominator = den;

        }
    }

    public BigFraction(final double value) throws IllegalArgumentException {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException("cannot convert NaN value");
        }
        if (Double.isInfinite(value)) {
            throw new IllegalArgumentException("cannot convert infinite value");
        }

        // compute m and k such that value = m * 2^k
        final long bits     = Double.doubleToLongBits(value);
        final long sign     = bits & 0x8000000000000000L;
        final long exponent = bits & 0x7ff0000000000000L;
        long m              = bits & 0x000fffffffffffffL;
        if (exponent != 0) {
            // this was a normalized number, add the implicit most significant bit
            m |= 0x0010000000000000L;
        }
        if (sign != 0) {
            m = -m;
        }
        int k = ((int) (exponent >> 52)) - 1075;
        while (((m & 0x001ffffffffffffeL) != 0) && ((m & 0x1) == 0)) {
            m >>= 1;
            ++k;
        }

        if (k < 0) {
            numerator   = BigInteger.valueOf(m);
            denominator = BigInteger.ZERO.flipBit(-k);
        } else {
            numerator   = BigInteger.valueOf(m).multiply(BigInteger.ZERO.flipBit(k));
            denominator = BigInteger.ONE;
        }

    }

    public BigFraction(final int num) {
        this(BigInteger.valueOf(num), BigInteger.ONE);
    }


    /**
     * <p>
     * Adds the value of this fraction to the passed {@link BigInteger},
     * returning the result in reduced form.
     * </p>
     *
     * @param bg
     *            the {@link BigInteger} to add, must'nt be <code>null</code>.
     * @return a <code>BigFraction</code> instance with the resulting values.
     * @throws IllegalArgumentException
     *             if the {@link BigInteger} is <code>null</code>.
     */
    public BigFraction add(final BigInteger bg) throws IllegalArgumentException {
        if (bg == null){
            throw new IllegalArgumentException("null is not allowed");
        }

        if (numerator.signum() == 0) {
            return new BigFraction(bg);
        }
        if (bg.signum() == 0) {
            return this;
        }

        return new BigFraction(numerator.add(denominator.multiply(bg)), denominator);
    }

    /**
     * <p>
     * Adds the value of this fraction to the passed {@code integer}, returning
     * the result in reduced form.
     * </p>
     *
     * @param i
     *            the {@code integer} to add.
     * @return a <code>BigFraction</code> instance with the resulting values.
     */
    public BigFraction add(final int i) {
        return add(BigInteger.valueOf(i));
    }

    /**
     * <p>
     * Adds the value of this fraction to the passed {@code long}, returning
     * the result in reduced form.
     * </p>
     *
     * @param l
     *            the {@code long} to add.
     * @return a <code>BigFraction</code> instance with the resulting values.
     */
    public BigFraction add(final long l) {
        return add(BigInteger.valueOf(l));
    }

    /**
     * <p>
     * Adds the value of this fraction to another, returning the result in
     * reduced form.
     * </p>
     *
     * @param fraction
     *            the {@link BigFraction} to add, must not be <code>null</code>.
     * @return a {@link BigFraction} instance with the resulting values.
     * @throws IllegalArgumentException if the {@link BigFraction} is {@code null}.
     */
    public BigFraction add(final BigFraction fraction) {
        if (fraction == null) {
            throw new IllegalArgumentException("fraction");
        }
        if (fraction.numerator.signum() == 0) {
            return this;
        }
        if (numerator.signum() == 0) {
            return fraction;
        }

        BigInteger num = null;
        BigInteger den = null;

        if (denominator.equals(fraction.denominator)) {
            num = numerator.add(fraction.numerator);
            den = denominator;
        } else {
            num = (numerator.multiply(fraction.denominator)).add((fraction.numerator).multiply(denominator));
            den = denominator.multiply(fraction.denominator);
        }

        if (num.signum() == 0) {
            return ZERO;
        }

        return new BigFraction(num, den);

    }

    /**
     * <p>
     * Gets the fraction as a <code>BigDecimal</code>. This calculates the
     * fraction as the numerator divided by denominator.
     * </p>
     *
     * @return the fraction as a <code>BigDecimal</code>.
     * @throws ArithmeticException
     *             if the exact quotient does not have a terminating decimal
     *             expansion.
     * @see BigDecimal
     */
    public BigDecimal bigDecimalValue() {
        return new BigDecimal(numerator).divide(new BigDecimal(denominator));
    }

    /**
     * <p>
     * Gets the fraction as a <code>BigDecimal</code> following the passed
     * rounding mode. This calculates the fraction as the numerator divided by
     * denominator.
     * </p>
     *
     * @param roundingMode
     *            rounding mode to apply. see {@link BigDecimal} constants.
     * @return the fraction as a <code>BigDecimal</code>.
     * @throws IllegalArgumentException
     *             if {@code roundingMode} does not represent a valid rounding
     *             mode.
     * @see BigDecimal
     */
    public BigDecimal bigDecimalValue(final int roundingMode) {
        return new BigDecimal(numerator).divide(new BigDecimal(denominator), roundingMode);
    }

    /**
     * <p>
     * Gets the fraction as a <code>BigDecimal</code> following the passed scale
     * and rounding mode. This calculates the fraction as the numerator divided
     * by denominator.
     * </p>
     *
     * @param scale
     *            scale of the <code>BigDecimal</code> quotient to be returned.
     *            see {@link BigDecimal} for more information.
     * @param roundingMode
     *            rounding mode to apply. see {@link BigDecimal} constants.
     * @return the fraction as a <code>BigDecimal</code>.
     * @see BigDecimal
     */
    public BigDecimal bigDecimalValue(final int scale, final int roundingMode) {
        return new BigDecimal(numerator).divide(new BigDecimal(denominator), scale, roundingMode);
    }

    /**
     * <p>
     * Compares this object to another based on size.
     * </p>
     *
     * @param object
     *            the object to compare to, must not be <code>null</code>.
     * @return -1 if this is less than {@code object}, +1 if this is greater
     *         than {@code object}, 0 if they are equal.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final BigFraction object) {
        int lhsSigNum = numerator.signum();
        int rhsSigNum = object.numerator.signum();

        if (lhsSigNum != rhsSigNum) {
            return (lhsSigNum > rhsSigNum) ? 1 : -1;
        }
        if (lhsSigNum == 0) {
            return 0;
        }

        BigInteger nOd = numerator.multiply(object.denominator);
        BigInteger dOn = denominator.multiply(object.numerator);
        return nOd.compareTo(dOn);
    }

    /**
     * <p>
     * Divide the value of this fraction by the passed {@code BigInteger},
     * ie {@code this * 1 / bg}, returning the result in reduced form.
     * </p>
     *
     * @param bg the {@code BigInteger} to divide by, must not be {@code null}
     * @return a {@link BigFraction} instance with the resulting values
     * @throws IllegalArgumentException if the {@code BigInteger} is {@code null}
     * @throws IllegalArgumentException if the fraction to divide by is zero
     */
    public BigFraction divide(final BigInteger bg) {
        if (bg == null) {
            throw new IllegalArgumentException("fraction");
        }
        if (bg.signum() == 0) {
            throw new IllegalArgumentException("denominator must be different from 0");
        }
        if (numerator.signum() == 0) {
            return ZERO;
        }
        return new BigFraction(numerator, denominator.multiply(bg));
    }

    /**
     * <p>
     * Divide the value of this fraction by the passed {@code int}, ie
     * {@code this * 1 / i}, returning the result in reduced form.
     * </p>
     *
     * @param i the {@code int} to divide by
     * @return a {@link BigFraction} instance with the resulting values
     * @throws IllegalArgumentException if the fraction to divide by is zero
     */
    public BigFraction divide(final int i) {
        return divide(BigInteger.valueOf(i));
    }

    /**
     * <p>
     * Divide the value of this fraction by the passed {@code long}, ie
     * {@code this * 1 / l}, returning the result in reduced form.
     * </p>
     *
     * @param l the {@code long} to divide by
     * @return a {@link BigFraction} instance with the resulting values
     * @throws IllegalArgumentException if the fraction to divide by is zero
     */
    public BigFraction divide(final long l) {
        return divide(BigInteger.valueOf(l));
    }

    /**
     * <p>
     * Divide the value of this fraction by another, returning the result in
     * reduced form.
     * </p>
     *
     * @param fraction Fraction to divide by, must not be {@code null}.
     * @return a {@link BigFraction} instance with the resulting values.
     * @throws IllegalArgumentException if the {@code fraction} is {@code null}.
     * @throws IllegalArgumentException if the fraction to divide by is zero
     */
    public BigFraction divide(final BigFraction fraction) {
        if (fraction == null) {
            throw new IllegalArgumentException("fraction");
        }
        if (fraction.numerator.signum() == 0) {
            throw new IllegalArgumentException("denominator must be different from 0");
        }
        if (numerator.signum() == 0) {
            return ZERO;
        }

        return multiply(fraction.reciprocal());
    }

    /**
     * <p>
     * Gets the fraction as a {@code double}. This calculates the fraction as
     * the numerator divided by denominator.
     * </p>
     *
     * @return the fraction as a {@code double}
     * @see java.lang.Number#doubleValue()
     */
    @Override
    public double doubleValue() {
        double result = numerator.doubleValue() / denominator.doubleValue();
        if (Double.isNaN(result)) {
            // Numerator and/or denominator must be out of range:
            // Calculate how far to shift them to put them in range.
            int shift = FastMath.max(numerator.bitLength(),
                    denominator.bitLength()) - FastMath.getExponent(Double.MAX_VALUE);
            result = numerator.shiftRight(shift).doubleValue() /
                    denominator.shiftRight(shift).doubleValue();
        }
        return result;
    }

    /**
     * <p>
     * Test for the equality of two fractions. If the lowest term numerator and
     * denominators are the same for both fractions, the two fractions are
     * considered to be equal.
     * </p>
     *
     * @param other
     *            fraction to test for equality to this fraction, can be
     *            <code>null</code>.
     * @return true if two fractions are equal, false if object is
     *         <code>null</code>, not an instance of {@link BigFraction}, or not
     *         equal to this fraction instance.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object other) {
        boolean ret = false;

        if (this == other) {
            ret = true;
        } else if (other instanceof BigFraction) {
            BigFraction rhs = ((BigFraction) other).reduce();
            BigFraction thisOne = this.reduce();
            ret = thisOne.numerator.equals(rhs.numerator) && thisOne.denominator.equals(rhs.denominator);
        }

        return ret;
    }

    /**
     * <p>
     * Gets the fraction as a {@code float}. This calculates the fraction as
     * the numerator divided by denominator.
     * </p>
     *
     * @return the fraction as a {@code float}.
     * @see java.lang.Number#floatValue()
     */
    @Override
    public float floatValue() {
        float result = numerator.floatValue() / denominator.floatValue();
        if (Double.isNaN(result)) {
            // Numerator and/or denominator must be out of range:
            // Calculate how far to shift them to put them in range.
            int shift = FastMath.max(numerator.bitLength(),
                    denominator.bitLength()) - FastMath.getExponent(Float.MAX_VALUE);
            result = numerator.shiftRight(shift).floatValue() /
                    denominator.shiftRight(shift).floatValue();
        }
        return result;
    }

    /**
     * <p>
     * Access the denominator as a <code>BigInteger</code>.
     * </p>
     *
     * @return the denominator as a <code>BigInteger</code>.
     */
    public BigInteger getDenominator() {
        return denominator;
    }

    /**
     * <p>
     * Access the denominator as a {@code int}.
     * </p>
     *
     * @return the denominator as a {@code int}.
     */
    public int getDenominatorAsInt() {
        return denominator.intValue();
    }

    /**
     * <p>
     * Access the denominator as a {@code long}.
     * </p>
     *
     * @return the denominator as a {@code long}.
     */
    public long getDenominatorAsLong() {
        return denominator.longValue();
    }

    /**
     * <p>
     * Access the numerator as a <code>BigInteger</code>.
     * </p>
     *
     * @return the numerator as a <code>BigInteger</code>.
     */
    public BigInteger getNumerator() {
        return numerator;
    }

    /**
     * <p>
     * Access the numerator as a {@code int}.
     * </p>
     *
     * @return the numerator as a {@code int}.
     */
    public int getNumeratorAsInt() {
        return numerator.intValue();
    }

    /**
     * <p>
     * Access the numerator as a {@code long}.
     * </p>
     *
     * @return the numerator as a {@code long}.
     */
    public long getNumeratorAsLong() {
        return numerator.longValue();
    }

    /**
     * <p>
     * Gets a hashCode for the fraction.
     * </p>
     *
     * @return a hash code value for this object.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 37 * (37 * 17 + numerator.hashCode()) + denominator.hashCode();
    }

    /**
     * <p>
     * Gets the fraction as an {@code int}. This returns the whole number part
     * of the fraction.
     * </p>
     *
     * @return the whole number fraction part.
     * @see java.lang.Number#intValue()
     */
    @Override
    public int intValue() {
        return numerator.divide(denominator).intValue();
    }

    /**
     * <p>
     * Gets the fraction as a {@code long}. This returns the whole number part
     * of the fraction.
     * </p>
     *
     * @return the whole number fraction part.
     * @see java.lang.Number#longValue()
     */
    @Override
    public long longValue() {
        return numerator.divide(denominator).longValue();
    }

    /**
     * <p>
     * Multiplies the value of this fraction by the passed
     * <code>BigInteger</code>, returning the result in reduced form.
     * </p>
     *
     * @param bg the {@code BigInteger} to multiply by.
     * @return a {@code BigFraction} instance with the resulting values.
     * @throws IllegalArgumentException if {@code bg} is {@code null}.
     */
    public BigFraction multiply(final BigInteger bg) {
        if (bg == null) {
            throw new IllegalArgumentException();
        }
        if (numerator.signum() == 0 || bg.signum() == 0) {
            return ZERO;
        }
        return new BigFraction(bg.multiply(numerator), denominator);
    }

    /**
     * <p>
     * Multiply the value of this fraction by the passed {@code int}, returning
     * the result in reduced form.
     * </p>
     *
     * @param i
     *            the {@code int} to multiply by.
     * @return a {@link BigFraction} instance with the resulting values.
     */
    public BigFraction multiply(final int i) {
        if (i == 0 || numerator.signum() == 0) {
            return ZERO;
        }

        return multiply(BigInteger.valueOf(i));
    }

    /**
     * <p>
     * Multiply the value of this fraction by the passed {@code long},
     * returning the result in reduced form.
     * </p>
     *
     * @param l
     *            the {@code long} to multiply by.
     * @return a {@link BigFraction} instance with the resulting values.
     */
    public BigFraction multiply(final long l) {
        if (l == 0 || numerator.signum() == 0) {
            return ZERO;
        }

        return multiply(BigInteger.valueOf(l));
    }

    /**
     * <p>
     * Multiplies the value of this fraction by another, returning the result in
     * reduced form.
     * </p>
     *
     * @param fraction Fraction to multiply by, must not be {@code null}.
     * @return a {@link BigFraction} instance with the resulting values.
     * @throws IllegalArgumentException if {@code fraction} is {@code null}.
     */
    public BigFraction multiply(final BigFraction fraction) {
        if (fraction == null) {
            throw new IllegalArgumentException("fraction");
        }
        if (numerator.signum() == 0 ||
                fraction.numerator.signum() == 0) {
            return ZERO;
        }
        return new BigFraction(numerator.multiply(fraction.numerator),
                denominator.multiply(fraction.denominator));
    }

    /**
     * <p>
     * Return the additive inverse of this fraction, returning the result in
     * reduced form.
     * </p>
     *
     * @return the negation of this fraction.
     */
    public BigFraction negate() {
        return new BigFraction(numerator.negate(), denominator);
    }


    /**
     * <p>
     * Return the multiplicative inverse of this fraction.
     * </p>
     *
     * @return the reciprocal fraction.
     */
    public BigFraction reciprocal() {
        return new BigFraction(denominator, numerator);
    }

    /**
     * <p>
     * Reduce this <code>BigFraction</code> to its lowest terms.
     * </p>
     *
     * @return the reduced <code>BigFraction</code>. It doesn't change anything if
     *         the fraction can be reduced.
     */
    public BigFraction reduce() {
        final BigInteger gcd = numerator.gcd(denominator);

        if (BigInteger.ONE.compareTo(gcd) < 0) {
            return new BigFraction(numerator.divide(gcd), denominator.divide(gcd));
        } else {
            return this;
        }
    }

    /**
     * <p>
     * Subtracts the value of an {@link BigInteger} from the value of this
     * {@code BigFraction}, returning the result in reduced form.
     * </p>
     *
     * @param bg the {@link BigInteger} to subtract, cannot be {@code null}.
     * @return a {@code BigFraction} instance with the resulting values.
     * @throws IllegalArgumentException if the {@link BigInteger} is {@code null}.
     */
    public BigFraction subtract(final BigInteger bg) {
        if (bg == null) {
            throw new IllegalArgumentException();
        }
        if (bg.signum() == 0) {
            return this;
        }
        if (numerator.signum() == 0) {
            return new BigFraction(bg.negate());
        }

        return new BigFraction(numerator.subtract(denominator.multiply(bg)), denominator);
    }

    /**
     * <p>
     * Subtracts the value of an {@code integer} from the value of this
     * {@code BigFraction}, returning the result in reduced form.
     * </p>
     *
     * @param i the {@code integer} to subtract.
     * @return a {@code BigFraction} instance with the resulting values.
     */
    public BigFraction subtract(final int i) {
        return subtract(BigInteger.valueOf(i));
    }

    /**
     * <p>
     * Subtracts the value of a {@code long} from the value of this
     * {@code BigFraction}, returning the result in reduced form.
     * </p>
     *
     * @param l the {@code long} to subtract.
     * @return a {@code BigFraction} instance with the resulting values.
     */
    public BigFraction subtract(final long l) {
        return subtract(BigInteger.valueOf(l));
    }

    /**
     * <p>
     * Subtracts the value of another fraction from the value of this one,
     * returning the result in reduced form.
     * </p>
     *
     * @param fraction {@link BigFraction} to subtract, must not be {@code null}.
     * @return a {@link BigFraction} instance with the resulting values
     * @throws IllegalArgumentException if the {@code fraction} is {@code null}.
     */
    public BigFraction subtract(final BigFraction fraction) {
        if (fraction == null) {
            throw new IllegalArgumentException("fraction");
        }
        if (fraction.numerator.signum() == 0) {
            return this;
        }
        if (numerator.signum() == 0) {
            return fraction.negate();
        }

        BigInteger num = null;
        BigInteger den = null;
        if (denominator.equals(fraction.denominator)) {
            num = numerator.subtract(fraction.numerator);
            den = denominator;
        } else {
            num = (numerator.multiply(fraction.denominator)).subtract((fraction.numerator).multiply(denominator));
            den = denominator.multiply(fraction.denominator);
        }
        return new BigFraction(num, den);

    }

    /**
     * <p>
     * Returns the <code>String</code> representing this fraction, ie
     * "num / dem" or just "num" if the denominator is one.
     * </p>
     *
     * @return a string representation of the fraction.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String str = null;
        if (BigInteger.ONE.equals(denominator)) {
            str = numerator.toString();
        } else if (BigInteger.ZERO.equals(numerator)) {
            str = "0";
        } else {
            str = numerator + " / " + denominator;
        }
        return str;
    }


}


