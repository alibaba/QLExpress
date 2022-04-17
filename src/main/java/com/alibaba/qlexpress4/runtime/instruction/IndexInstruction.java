package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QRuntime;

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

    @Override
    public void execute(Parameters parameters, QRuntime qRuntime, QLOptions qlOptions) {

    }
}
