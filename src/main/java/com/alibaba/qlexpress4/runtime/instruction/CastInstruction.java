package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @Operation: force cast value to specified type
 * @Input: 1
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class CastInstruction extends QLInstruction {

    private final Class<?> targetClz;

    public CastInstruction(ErrorReporter errorReporter, Class<?> targetClz) {
        super(errorReporter);
        this.targetClz = targetClz;
    }
}
