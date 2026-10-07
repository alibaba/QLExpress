package com.alibaba.qlexpress4.runtime.function;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QContext;
import com.alibaba.qlexpress4.runtime.QLambda;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Author: DQinYuan
 */
public class CustomFunctionLambdaArgumentTest {
    
    @Test
    public void forEachFunctionAcceptsExplicitLambdaArgument()
        throws Throwable {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        express4Runner.addFunction("FOR_EACH", new CustomFunction() {
            @Override
            public Object call(QContext qContext, Parameters parameters)
                throws Throwable {
                Iterable<?> iterable = (Iterable<?>)parameters.getValue(0);
                QLambda mapper = (QLambda)parameters.getValue(1);
                List<Object> result = new ArrayList<>();
                for (Object item : iterable) {
                    result.add(mapper.call(item).getResult().get());
                }
                return result;
            }
        });
        
        Object result = express4Runner
            .execute("FOR_EACH([1,2,3,4], item -> item + 1)", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS)
            .getResult();
        assertEquals(Arrays.asList(2, 3, 4, 5), result);
    }
}
