package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.exception.ErrorReporter;

/**
 * @Operation: extract value with index, like a[0], m['a']
 * @Input: 1
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class IndexInstruction extends QLInstruction {

    private final Object index;

    public IndexInstruction(ErrorReporter errorReporter, Object index) {
        super(errorReporter);
        this.index = index;
    }
}
