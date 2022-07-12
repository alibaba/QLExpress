package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QFunction;
import com.alibaba.qlexpress4.runtime.QRuntime;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Author: DQinYuan
 */
public class TestSuiteRunner {

    private static final String ASSERT_FUNCTION_NAME = "assert";
    private static final String TEST_PATH_ATT = "TEST_PATH";

    @Test
    public void suiteTest() {

    }

    @Test
    public void assertTest() {
        Map<String, Object> attachment = new HashMap<>();
        attachment.put(TEST_PATH_ATT, "a/b.ql");

        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        express4Runner.addFunction(ASSERT_FUNCTION_NAME, new AssertFunction());
        QLOptions attachOptions = QLOptions.builder()
                .attachments(attachment)
                .build();
        express4Runner.execute("assert(true)", Collections.emptyMap(), attachOptions);
        assertErrCodeAndReason(express4Runner, "assert(false)", attachOptions,
                "CALL_FUNCTION_BIZ_EXCEPTION",
                "a/b.ql: assert fail");
        assertErrCodeAndReason(express4Runner, "assert(false, 'my test')", attachOptions,
                "CALL_FUNCTION_BIZ_EXCEPTION", "a/b.ql: my test");
        // variable can be the same name with function
        express4Runner.execute("assert = 4;assert(assert == 4)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
    }

    private void assertErrCodeAndReason(Express4Runner express4Runner, String script,
                                        QLOptions qlOptions,
                                        String errCode, String reason) {
        try {
            express4Runner.execute(script, Collections.emptyMap(),
                    qlOptions);
        } catch (QLException e) {
            assertEquals(errCode, e.getErrorCode());
            assertEquals(reason, e.getReason());
        }
    }

    private static class AssertFunction implements QFunction {
        @Override
        public Object call(QRuntime qRuntime, Parameters parameters) throws Exception {
            int pSize = parameters.size();
            switch (pSize) {
                case 1:
                    Boolean b = (Boolean) parameters.getValue(0);
                    if (b == null || !b) {
                        throw new UserDefineException(wrap(qRuntime.attachment(),
                                "assert fail"));
                    }
                    return null;
                case 2:
                    Boolean b0 = (Boolean) parameters.getValue(0);
                    if (b0 == null || !b0) {
                        throw new UserDefineException(wrap(qRuntime.attachment(),
                                (String) parameters.getValue(1)));
                    }
                    return null;
                default:
                    throw new UserDefineException("invalid parameter size");
            }
        }

        private String wrap(Map<String, Object> attachments, String originErrInfo) {
            return attachments.get(TEST_PATH_ATT) + ": " + originErrInfo;
        }
    }
}
