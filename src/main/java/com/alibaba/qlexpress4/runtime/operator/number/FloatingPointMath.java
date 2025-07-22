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
 * FloatingPoint (Double and Float) NumberMath operations
 * reference groovy source code
 */
public final class FloatingPointMath extends NumberMath {
    
    public static final FloatingPointMath INSTANCE = new FloatingPointMath();
    
    private FloatingPointMath() {
    }
    
    @Override
    public Number absImpl(Number number) {
        return Math.abs(number.doubleValue());
    }
    
    @Override
    public Number addImpl(Number left, Number right) {
        return left.doubleValue() + right.doubleValue();
    }
    
    @Override
    public Number subtractImpl(Number left, Number right) {
        return left.doubleValue() - right.doubleValue();
    }
    
    @Override
    public Number multiplyImpl(Number left, Number right) {
        return left.doubleValue() * right.doubleValue();
    }
    
    @Override
    public Number divideImpl(Number left, Number right) {
        return left.doubleValue() / right.doubleValue();
    }
    
    @Override
    public int compareToImpl(Number left, Number right) {
        return Double.compare(left.doubleValue(), right.doubleValue());
    }
    
    @Override
    public Number remainderImpl(Number left, Number right) {
        return left.doubleValue() % right.doubleValue();
    }
    
    @Override
    public Number modImpl(Number left, Number right) {
        return toBigInteger(left).mod(toBigInteger(right)).doubleValue();
    }
    
    @Override
    public Number unaryMinusImpl(Number left) {
        return -left.doubleValue();
    }
    
    @Override
    public Number unaryPlusImpl(Number left) {
        return left.doubleValue();
    }
}
