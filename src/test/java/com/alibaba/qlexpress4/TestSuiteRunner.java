package com.alibaba.qlexpress4;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.qlexpress4.aparser.ImportManager;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;

import com.alibaba.qlexpress4.security.QLSecurityStrategy;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Author: DQinYuan
 */
public class TestSuiteRunner {
    private static final String ASSERT_FUNCTION_NAME = "assert";
    private static final String ASSERT_FALSE_FUNCTION_NAME = "assertFalse";
    private static final String ASSERT_ERROR_CODE_FUNCTION_NAME = "assertErrorCode";
    private static final String PRINT_FUNCTION_NAME = "println";
    private static final String TEST_PATH_ATT = "TEST_PATH";

    private static Express4Runner CONFIG_RUNNER = new Express4Runner(InitOptions.builder()
        .securityStrategy(QLSecurityStrategy.open())
        .defaultImport(Arrays.asList(
            ImportManager.importCls("com.alibaba.qlexpress4.QLOptions"),
            ImportManager.importCls("com.alibaba.qlexpress4.InitOptions")))
        .build());

    private Express4Runner prepareRunner(InitOptions initOptions) {
        Express4Runner testRunner = new Express4Runner(initOptions);
        testRunner.addFunction(ASSERT_FUNCTION_NAME, new AssertFunction());
        testRunner.addFunction(ASSERT_FALSE_FUNCTION_NAME, new AssertFalseFunction());
        testRunner.addFunction(ASSERT_ERROR_CODE_FUNCTION_NAME, new AssertErrorCodeFunction());
        testRunner.addFunction(PRINT_FUNCTION_NAME, new PrintFunction());
        return testRunner;
    }

    @Test
    public void suiteTest() throws URISyntaxException, IOException {
        Path testSuiteRoot = getTestSuiteRoot();
        handleDirectory(testSuiteRoot, "");
    }

    @Test
    public void featureDebug() throws URISyntaxException, IOException {
        Path filePath = getTestSuiteRoot().resolve("java/method.get/method_not_found.ql");
        handleFile(filePath, filePath.toString(), true);
    }

    private void handleDirectory(Path dir, String pathPrefix) throws IOException {
        Files.list(dir).forEach(path -> {
            try {
                String newPrefix = pathPrefix + "/" + path.getFileName();
                if (Files.isDirectory(path)) {
                    handleDirectory(path, newPrefix);
                } else {
                    handleFile(path, newPrefix, false);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void handleFile(Path qlFile, String path, boolean debug) throws IOException {
        printRunning(path);
        Map<String, Object> attachments = new HashMap<>();
        attachments.put(TEST_PATH_ATT, path);

        String qlScript = new String(Files.readAllBytes(qlFile));
        // parse testsuite option first
        Optional<Map<String, Object>> scriptOptionOp = parseOption(qlScript);
        Optional<String> errCodeOp = scriptOptionOp.map(scriptOption -> (String)scriptOption.get("errCode"));
        Optional<InitOptions.Builder> initOptionsBuilder = scriptOptionOp.map(scriptOption ->
            (InitOptions.Builder)scriptOption.get("initOptions"));
        InitOptions initOptions = initOptionsBuilder.isPresent() ?
            initOptionsBuilder.get().securityStrategy(QLSecurityStrategy.open()).debug(debug).build() :
            InitOptions.builder().securityStrategy(QLSecurityStrategy.open()).debug(debug).build();
        Express4Runner express4Runner = prepareRunner(initOptions);
        Optional<QLOptions.Builder> optionsBuilder = scriptOptionOp.map(scriptOption ->
            (QLOptions.Builder)scriptOption.get("qlOptions"));
        QLOptions qlOptions = optionsBuilder.isPresent() ?
            optionsBuilder.get().attachments(attachments).build() :
            QLOptions.builder().attachments(attachments).build();
        if (errCodeOp.isPresent()) {
            long start = System.currentTimeMillis();
            assertErrCode(express4Runner, path, qlScript, qlOptions, errCodeOp.get(), debug);
            printOk(path, System.currentTimeMillis() - start);
            return;
        }

        try {
            long start = System.currentTimeMillis();
            express4Runner.execute(qlScript, Collections.emptyMap(), qlOptions);
            printOk(path, System.currentTimeMillis() - start);
        } catch (Exception e) {
            System.out.printf("%1$-95s %2$s\n", path, "error");
            throw e;
        }
    }

    private void printRunning(String path) {
        System.out.printf("%1$-98s %2$s\n", path, "running");
    }

    private void printOk(String path, long consumeTime) {
        System.out.printf("%1$-98s %2$s consume %3$dms\n", path, "ok", consumeTime);
    }

    private void assertErrCode(Express4Runner runner, String path, String qlScript, QLOptions qlOptions,
        String expectErrCode, boolean printE) {
        try {
            runner.execute(qlScript, Collections.emptyMap(), qlOptions);
        } catch (QLException qlException) {
            if (printE) {
                qlException.printStackTrace();
            }
            assertEquals(path + " error code assert fail", expectErrCode, qlException.getErrorCode());
        } catch (Exception e) {
            throw new RuntimeException(path + " unknown error", e);
        }
    }

    private Optional<Map<String, Object>> parseOption(String qlScript) {
        if (!qlScript.startsWith("/*")) {
            return Optional.empty();
        }
        int endIndex = qlScript.indexOf("*/");
        if (endIndex == -1) {
            return Optional.empty();
        }
        String configJson = qlScript.substring(2, endIndex);
        try {
            Map<String, Object> scriptOptions = (Map<String, Object>)CONFIG_RUNNER
                .execute(configJson, Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
            return Optional.of(scriptOptions);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    @Test
    public void assertTest() {
        Map<String, Object> attachment = new HashMap<>();
        attachment.put(TEST_PATH_ATT, "a/b.ql");

        QLOptions attachOptions = QLOptions.builder()
            .attachments(attachment)
            .build();
        Express4Runner express4Runner = prepareRunner(InitOptions.DEFAULT_OPTIONS);
        express4Runner.execute("assert(true)", Collections.emptyMap(), attachOptions);
        assertErrCodeAndReason(express4Runner, "assert(false)", attachOptions,
            "BIZ_EXCEPTION",
            "a/b.ql: assert fail");
        assertErrCodeAndReason(express4Runner, "assert(false, 'my test')", attachOptions,
            "BIZ_EXCEPTION", "a/b.ql: my test");
        // variable can be the same name with function
        express4Runner.execute("assert = 4;assert(assert == 4)",
            Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
    }

    private Path getTestSuiteRoot() throws URISyntaxException {
        return Paths.get(getClass().getClassLoader()
            .getResource("testsuite").toURI());
    }

    private void assertErrCodeAndReason(Express4Runner express4Runner, String script,
        QLOptions qlOptions, String errCode, String reason) {
        try {
            express4Runner.execute(script, Collections.emptyMap(), qlOptions);
        } catch (QLException e) {
            assertEquals(errCode, e.getErrorCode());
            assertEquals(reason, e.getReason());
        }
    }

    private static class PrintFunction implements CustomFunction {
        @Override
        public Object call(QContext qContext, Parameters parameters) throws Exception {
            for (int i = 0; i < parameters.size(); i++) {
                System.out.println(parameters.get(i).get());
            }
            return null;
        }
    }

    private static class AssertFunction implements CustomFunction {
        @Override
        public Object call(QContext qContext, Parameters parameters) throws Exception {
            int pSize = parameters.size();
            switch (pSize) {
                case 1:
                    Boolean b = (Boolean)parameters.getValue(0);
                    if (b == null || !b) {
                        throw new UserDefineException(wrap(qContext.attachment(), "assert fail"));
                    }
                    return null;
                case 2:
                    Boolean b0 = (Boolean)parameters.getValue(0);
                    if (b0 == null || !b0) {
                        throw new UserDefineException(wrap(qContext.attachment(), (String)parameters.getValue(1)));
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

    private static class AssertFalseFunction implements CustomFunction {
        @Override
        public Object call(QContext qContext, Parameters parameters) throws Exception {
            int pSize = parameters.size();
            switch (pSize) {
                case 1:
                    Boolean b = (Boolean)parameters.getValue(0);
                    if (b == null || b) {
                        throw new UserDefineException(wrap(qContext.attachment(), "assert fail"));
                    }
                    return null;
                case 2:
                    Boolean b0 = (Boolean)parameters.getValue(0);
                    if (b0 == null || b0) {
                        throw new UserDefineException(wrap(qContext.attachment(), (String)parameters.getValue(1)));
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

    private static class AssertErrorCodeFunction implements CustomFunction {
        @Override
        public Object call(QContext qContext, Parameters parameters) throws Throwable {
            int pSize = parameters.size();
            if (pSize != 2) {
                throw new UserDefineException(String.format("invalid pSize:%s, expected 2 parameters", pSize));
            }

            Value value = parameters.get(0);
            Value value1 = parameters.get(1);
            QLambda qLambda = (QLambda)value.get();
            try {
                qLambda.run();
                throw new UserDefineException(String.format("expectedErrorCode:%s, but no error", value1.get()));
            } catch (QLRuntimeException e) {
                if (!Objects.equals(value1.get(), e.getErrorCode())) {
                    throw new UserDefineException(
                        String.format("expectedErrorCode:%s, actualErrorCode:%s", value1.get(), e.getErrorCode()));
                }
                return null;
            }
        }
    }
}