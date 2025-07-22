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

/**
 * Long NumberMath operations
 * reference groovy source code
 */
public final class LongMath extends NumberMath {
    
    public static final LongMath INSTANCE = new LongMath();
    
    private LongMath() {
    }
    
    @Override
    public Number absImpl(Number number) {
        return Math.abs(number.longValue());
    }
    
    @Override
    public Number addImpl(Number left, Number right) {
        return left.longValue() + right.longValue();
    }
    
    @Override
    public Number subtractImpl(Number left, Number right) {
        return left.longValue() - right.longValue();
    }
    
    @Override
    public Number multiplyImpl(Number left, Number right) {
        return left.longValue() * right.longValue();
    }
    
    @Override
    public Number divideImpl(Number left, Number right) {
        return BigDecimalMath.INSTANCE.divideImpl(left, right);
    }
    
    @Override
    public int compareToImpl(Number left, Number right) {
        long leftVal = left.longValue();
        long rightVal = right.longValue();
        return Long.compare(leftVal, rightVal);
    }
    
    @Override
    public Number intDivImpl(Number left, Number right) {
        return left.longValue() / right.longValue();
    }
    
    @Override
    public Number remainderImpl(Number left, Number right) {
        return left.longValue() % right.longValue();
    }
    
    @Override
    public Number modImpl(Number left, Number right) {
        return toBigInteger(left).mod(toBigInteger(right)).longValue();
    }
    
    @Override
    public Number unaryMinusImpl(Number left) {
        return -left.longValue();
    }
    
    @Override
    public Number unaryPlusImpl(Number left) {
        return left.longValue();
    }
    
    @Override
    public Number bitwiseNegateImpl(Number left) {
        return ~left.longValue();
    }
    
    @Override
    public Number orImpl(Number left, Number right) {
        return left.longValue() | right.longValue();
    }
    
    @Override
    public Number andImpl(Number left, Number right) {
        return left.longValue() & right.longValue();
    }
    
    @Override
    public Number xorImpl(Number left, Number right) {
        return left.longValue() ^ right.longValue();
    }
    
    @Override
    public Number leftShiftImpl(Number left, Number right) {
        return left.longValue() << right.longValue();
    }
    
    @Override
    public Number rightShiftImpl(Number left, Number right) {
        return left.longValue() >> right.longValue();
    }
    
    @Override
    public Number rightShiftUnsignedImpl(Number left, Number right) {
        return left.longValue() >>> right.longValue();
    }
    
    public Number bitAndImpl(Number left, Number right) {
        return left.longValue() & right.longValue();
    }
}
