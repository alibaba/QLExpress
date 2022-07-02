package com.alibaba.qlexpress4.runtime;

import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.runtime.data.DataValue;

import java.util.function.Consumer;

/**
 * Author: DQinYuan
 */
public class ConstLambdaDefinition implements QLambdaDefinition {

    private final Object constValue;

    public ConstLambdaDefinition(Object constValue) {
        this.constValue = constValue;
    }

    @Override
    public QLambda toLambda(QRuntime qRuntime, QLOptions qlOptions, boolean newEnv) {
        return new ConstLambda(constValue);
    }

    @Override
    public void println(int depth, Consumer<String> debug) {

    }

    @Override
    public String getName() {
        return "LambdaReturnConst " + (constValue == null? "null": constValue.toString());
    }

    private static class ConstLambda implements QLambda {

        private final Object constValue;

        public ConstLambda(Object constValue) {
            this.constValue = constValue;
        }

        @Override
        public QResult call(Object... params) throws Exception {
            return new QResult(new DataValue(constValue), QResult.ResultType.RETURN);
        }
    }
}
