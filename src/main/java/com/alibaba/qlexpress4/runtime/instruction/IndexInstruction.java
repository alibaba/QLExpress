package com.alibaba.qlexpress4.runtime.instruction;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.QRuntime;

/**
 * @Operation: extract value with index, like a[0], m['a']
 * @Input: 2, indexable object and index
 * @Output: 1
 *
 * Author: DQinYuan
 */
public class IndexInstruction extends QLInstruction {

    public IndexInstruction(ErrorReporter errorReporter) {
        super(errorReporter);
    }

    @Override
    public void execute(QRuntime qRuntime, QLOptions qlOptions) {

    }
}
