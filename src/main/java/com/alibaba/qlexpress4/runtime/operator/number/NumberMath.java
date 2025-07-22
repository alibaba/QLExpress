/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package com.alibaba.qlexpress4.runtime.operator.number;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Stateless objects used to perform math on the various Number subclasses.
 * Instances are required so that polymorphic calls work properly, but each
 * subclass creates a singleton instance to minimize garbage.  All methods
 * must be thread-safe.
 *
 * The design goals of this class are as follows:
 * <ol>
 * <li>Support a 'least surprising' math model to scripting language users.  This
 * means that exact, or decimal math should be used for default calculations.  This
 * scheme assumes that by default, groovy literals with decimal points are instantiated
 * as BigDecimal objects rather than binary floating points (Float, Double).
 * <li>Do not force the appearance of exactness on a number that is by definition not
 * guaranteed to be exact.  In particular this means that if an operand in a NumberMath
 * operation is a binary floating point number, ensure that the result remains a binary floating point
 * number (i.e. never automatically promote a binary floating point number to a BigDecimal).
 * This has the effect of preserving the expectations of binary floating point users and helps performance.
 * <li>Provide an implementation that is as close as practical to the Java 1.5 BigDecimal math model which implements
 * precision based floating point decimal math (ANSI X3.274-1996 and ANSI X3.274-1996/AM 1-2000 (section 7.4).
 * </ol>
 * reference groovy source code
 */
public abstract class NumberMath {
    
    public static Number abs(Number number) {
        return getMath(number).absImpl(number);
    }
    
    public static Number add(Number left, Number right) {
        return getMath(left, right).addImpl(left, right);
    }
    
    public static Number subtract(Number left, Number right) {
        return getMath(left, right).subtractImpl(left, right);
    }
    
    public static Number multiply(Number left, Number right) {
        return getMath(left, right).multiplyImpl(left, right);
    }
    
    public static Number divide(Number left, Number right) {
        return getMath(left, right).divideImpl(left, right);
    }
    
    public static int compareTo(Number left, Number right) {
        return getMath(left, right).compareToImpl(left, right);
    }
    
    public static Number or(Number left, Number right) {
        return getMath(left, right).orImpl(left, right);
    }
    
    public static Number and(Number left, Number right) {
        return getMath(left, right).andImpl(left, right);
    }
    
    public static Number xor(Number left, Number right) {
        return getMath(left, right).xorImpl(left, right);
    }
    
    public static Number intDiv(Number left, Number right) {
        return getMath(left, right).intDivImpl(left, right);
    }
    
    // retain for backwards compatibility
    public static Number mod(Number left, Number right) {
        return getMath(left, right).modImpl(left, right);
    }
    
    public static Number remainder(Number left, Number right) {
        return getMath(left, right).remainderImpl(left, right);
    }
    
    /*
     * For this operation, consider the operands independently.  Throw an exception if the right operand
     * (shift distance) is not an integral type.  For the left operand (shift value) also require an integral
     * type, but do NOT promote from Integer to Long.  This is consistent with Java, and makes sense for the
     * shift operators.
     */
    public static Number leftShift(Number left, Number right) {
        if (isFloatingPoint(right) || isBigDecimal(right)) {
            throw new UnsupportedOperationException("Shift distance must be an integral type, but " + right + " ("
                + right.getClass().getName() + ") was supplied");
        }
        return getMath(left).leftShiftImpl(left, right);
    }
    
    /*
     * For this operation, consider the operands independently.  Throw an exception if the right operand
     * (shift distance) is not an integral type.  For the left operand (shift value) also require an integral
     * type, but do NOT promote from Integer to Long.  This is consistent with Java, and makes sense for the
     * shift operators.
     */
    public static Number rightShift(Number left, Number right) {
        if (isFloatingPoint(right) || isBigDecimal(right)) {
            throw new UnsupportedOperationException("Shift distance must be an integral type, but " + right + " ("
                + right.getClass().getName() + ") was supplied");
        }
        return getMath(left).rightShiftImpl(left, right);
    }
    
    /*
     * For this operation, consider the operands independently.  Throw an exception if the right operand
     * (shift distance) is not an integral type.  For the left operand (shift value) also require an integral
     * type, but do NOT promote from Integer to Long.  This is consistent with Java, and makes sense for the
     * shift operators.
     */
    public static Number rightShiftUnsigned(Number left, Number right) {
        if (isFloatingPoint(right) || isBigDecimal(right)) {
            throw new UnsupportedOperationException("Shift distance must be an integral type, but " + right + " ("
                + right.getClass().getName() + ") was supplied");
        }
        return getMath(left).rightShiftUnsignedImpl(left, right);
    }
    
    public static Number bitwiseNegate(Number left) {
        return getMath(left).bitwiseNegateImpl(left);
    }
    
    public static Number unaryMinus(Number left) {
        return getMath(left).unaryMinusImpl(left);
    }
    
    public static Number unaryPlus(Number left) {
        return getMath(left).unaryPlusImpl(left);
    }
    
    public static boolean isFloatingPoint(Number number) {
        return number instanceof Double || number instanceof Float;
    }
    
    public static boolean isInteger(Number number) {
        return number instanceof Integer;
    }
    
    public static boolean isShort(Number number) {
        return number instanceof Short;
    }
    
    public static boolean isByte(Number number) {
        return number instanceof Byte;
    }
    
    public static boolean isLong(Number number) {
        return number instanceof Long;
    }
    
    public static boolean isBigDecimal(Number number) {
        return number instanceof BigDecimal;
    }
    
    public static boolean isBigInteger(Number number) {
        return number instanceof BigInteger;
    }
    
    public static BigDecimal toBigDecimal(Number n) {
        if (n instanceof BigDecimal) {
            return (BigDecimal)n;
        }
        if (n instanceof BigInteger) {
            return new BigDecimal((BigInteger)n);
        }
        if (n instanceof Integer || n instanceof Long || n instanceof Byte || n instanceof Short) {
            return BigDecimal.valueOf(n.longValue());
        }
        try {
            return new BigDecimal(n.toString());
        }
        catch (NumberFormatException nfe) {
            return BigDecimal.valueOf(n.doubleValue());
        }
    }
    
    public static BigInteger toBigInteger(Number n) {
        if (n instanceof BigInteger) {
            return (BigInteger)n;
        }
        if (n instanceof Integer || n instanceof Long || n instanceof Byte || n instanceof Short) {
            return BigInteger.valueOf(n.longValue());
        }
        
        if (n instanceof Float || n instanceof Double) {
            BigDecimal bd = new BigDecimal(n.toString());
            return bd.toBigInteger();
        }
        if (n instanceof BigDecimal) {
            return ((BigDecimal)n).toBigInteger();
        }
        
        return new BigInteger(n.toString());
    }
    
    /*
     * Determine which NumberMath instance to use, given the supplied operands.  This method implements
     * the type promotion rules discussed in the documentation.  Note that by the time this method is
     * called, any Byte, Character or Short operands will have been promoted to Integer.  For reference,
     * here is the promotion matrix:
     * bD bI  D  F  L  I
     * bD bD bD  D  D bD bD
     * bI bD bI  D  D bI bI
     * D   D  D  D  D  D  D
     * F   D  D  D  D  D  D
     * L  bD bI  D  D  L  L
     * I  bD bI  D  D  L  I
     *
     * Note that for division, if either operand isFloatingPoint, the result will be floating.  Otherwise,
     * the result is BigDecimal
     */
    public static NumberMath getMath(Number left, Number right) {
        // FloatingPointMath wins according to promotion Matrix
        if (isFloatingPoint(left) || isFloatingPoint(right)) {
            return FloatingPointMath.INSTANCE;
        }
        NumberMath leftMath = getMath(left);
        NumberMath rightMath = getMath(right);
        
        if (leftMath == BigDecimalMath.INSTANCE || rightMath == BigDecimalMath.INSTANCE) {
            return BigDecimalMath.INSTANCE;
        }
        if (leftMath == BigIntegerMath.INSTANCE || rightMath == BigIntegerMath.INSTANCE) {
            return BigIntegerMath.INSTANCE;
        }
        if (leftMath == LongMath.INSTANCE || rightMath == LongMath.INSTANCE) {
            return LongMath.INSTANCE;
        }
        if (leftMath == IntegerMath.INSTANCE || rightMath == IntegerMath.INSTANCE) {
            return IntegerMath.INSTANCE;
        }
        // also for custom Number implementations
        return BigDecimalMath.INSTANCE;
    }
    
    /* package private */
    static NumberMath getMath(Number number) {
        if (isLong(number)) {
            return LongMath.INSTANCE;
        }
        if (isFloatingPoint(number)) {
            return FloatingPointMath.INSTANCE;
        }
        if (isBigDecimal(number)) {
            return BigDecimalMath.INSTANCE;
        }
        if (isBigInteger(number)) {
            return BigIntegerMath.INSTANCE;
        }
        if (isInteger(number) || isShort(number) || isByte(number)) {
            return IntegerMath.INSTANCE;
        }
        // also for custom Number implementations
        return BigDecimalMath.INSTANCE;
    }
    
    //Subclasses implement according to the type promotion hierarchy rules
    protected abstract Number absImpl(Number number);
    
    public abstract Number addImpl(Number left, Number right);
    
    public abstract Number subtractImpl(Number left, Number right);
    
    public abstract Number multiplyImpl(Number left, Number right);
    
    public abstract Number divideImpl(Number left, Number right);
    
    public abstract int compareToImpl(Number left, Number right);
    
    protected abstract Number unaryMinusImpl(Number left);
    
    protected abstract Number unaryPlusImpl(Number left);
    
    protected Number bitwiseNegateImpl(Number left) {
        throw createUnsupportedException("bitwiseNegate()", left);
    }
    
    protected Number orImpl(Number left, Number right) {
        throw createUnsupportedException("or()", left);
    }
    
    protected Number andImpl(Number left, Number right) {
        throw createUnsupportedException("and()", left);
    }
    
    protected Number xorImpl(Number left, Number right) {
        throw createUnsupportedException("xor()", left);
    }
    
    protected Number remainderImpl(Number left, Number right) {
        throw createUnsupportedException("remainder()", left);
    }
    
    protected Number modImpl(Number left, Number right) {
        throw createUnsupportedException("mod()", left);
    }
    
    protected Number intDivImpl(Number left, Number right) {
        throw createUnsupportedException("intDiv()", left);
    }
    
    protected Number leftShiftImpl(Number left, Number right) {
        throw createUnsupportedException("leftShift()", left);
    }
    
    protected Number rightShiftImpl(Number left, Number right) {
        throw createUnsupportedException("rightShift()", left);
    }
    
    protected Number rightShiftUnsignedImpl(Number left, Number right) {
        throw createUnsupportedException("rightShiftUnsigned()", left);
    }
    
    protected UnsupportedOperationException createUnsupportedException(String operation, Number left) {
        return new UnsupportedOperationException(
            "Cannot use " + operation + " on this number type: " + left.getClass().getName() + " with value: " + left);
    }
}
