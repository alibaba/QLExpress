package com.alibaba.qlexpress4;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.alibaba.fastjson2.JSONException;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.parser.ImportManager;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.QFunction;
import com.alibaba.qlexpress4.runtime.QRuntime;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Author: DQinYuan
 */
public class TestSuiteRunner {

    private static final String ASSERT_FUNCTION_NAME = "assert";
    private static final String ASSERT_FALSE_FUNCTION_NAME = "assertFalse";
    private static final String PRINT_FUNCTION_NAME = "println";
    private static final String TEST_PATH_ATT = "TEST_PATH";

    private Express4Runner testRunner;

    @Before
    public void before() {
        this.testRunner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        testRunner.addFunction(ASSERT_FUNCTION_NAME, new AssertFunction());
        testRunner.addFunction(ASSERT_FALSE_FUNCTION_NAME, new AssertFalseFunction());
        testRunner.addFunction(PRINT_FUNCTION_NAME, new PrintFunction());
    }

    @Test
    public void suiteTest() throws URISyntaxException, IOException {
        Path testSuiteRoot = getTestSuiteRoot();
        handleDirectory(testSuiteRoot, "");
    }

    @Test
    public void featureDebug() throws URISyntaxException, IOException {
        Path filePath = getTestSuiteRoot().resolve("independent/while/condition_not_bool.ql");
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
        Map<String, Object> attachments = new HashMap<>();
        attachments.put(TEST_PATH_ATT, path);

        String qlScript = new String(Files.readAllBytes(qlFile));
        // parse testsuite option first
        Optional<Map<String, Object>> scriptOptionOp = parseOption(qlScript);
        Optional<String> errCodeOp = scriptOptionOp.map(scriptOption -> (String) scriptOption.get("errCode"));
        if (errCodeOp.isPresent()) {
            assertErrCode(path, qlScript, QLOptions.builder()
                    .debug(debug)
                    .attachments(attachments)
                    .build(), errCodeOp.get());
            printOk(path);
            return;
        }
        Optional<QLOptions.Builder> optionsBuilder = scriptOptionOp.map(scriptOption ->
                (QLOptions.Builder) scriptOption.get("qlOptions"));
        QLOptions qlOptions = optionsBuilder.isPresent()?
                optionsBuilder.get().debug(debug).attachments(attachments).build():
                QLOptions.builder().debug(debug).attachments(attachments).build();

        try {
            testRunner.execute(qlScript, Collections.emptyMap(), qlOptions);
            printOk(path);
        } catch (Exception e) {
            System.out.printf("%1$-95s %2$s\n",path, "error");
            throw e;
        }
    }

    private void printOk(String path) {
        System.out.printf("%1$-98s %2$s\n",path, "ok");
    }

    private void assertErrCode(String path, String qlScript, QLOptions qlOptions, String expectErrCode) {
        try {
            testRunner.execute(qlScript, Collections.emptyMap(), qlOptions);
        } catch (QLException qlException) {
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
            QLOptions qlOptions = QLOptions.builder()
                    .defaultImport(Collections.singletonList(
                            ImportManager.importCls("com.alibaba.qlexpress4.QLOptions")))
                    .build();
            Map<String, Object> scriptOptions = (Map<String, Object>) testRunner
                    .execute(configJson, Collections.emptyMap(), qlOptions);
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
        testRunner.execute("assert(true)", Collections.emptyMap(), attachOptions);
        assertErrCodeAndReason(testRunner, "assert(false)", attachOptions,
                "CALL_FUNCTION_BIZ_EXCEPTION",
                "a/b.ql: assert fail");
        assertErrCodeAndReason(testRunner, "assert(false, 'my test')", attachOptions,
                "CALL_FUNCTION_BIZ_EXCEPTION", "a/b.ql: my test");
        // variable can be the same name with function
        testRunner.execute("assert = 4;assert(assert == 4)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
    }

    private Path getTestSuiteRoot() throws URISyntaxException {
        return Paths.get(getClass().getClassLoader()
                .getResource("testsuite").toURI());
    }

    private void assertErrCodeAndReason(Express4Runner express4Runner, String script,
                                        QLOptions qlOptions,
                                        String errCode, String reason) {
        try {
            express4Runner.execute(script, Collections.emptyMap(), qlOptions);
        } catch (QLException e) {
            assertEquals(errCode, e.getErrorCode());
            assertEquals(reason, e.getReason());
        }
    }

    private static class PrintFunction implements QFunction {
        @Override
        public Object call(QRuntime qRuntime, Parameters parameters) throws Exception {
            for (int i = 0; i < parameters.size(); i++) {
                System.out.println(parameters.get(i).get());
            }
            return null;
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
                        throw new UserDefineException(wrap(qRuntime.attachment(), "assert fail"));
                    }
                    return null;
                case 2:
                    Boolean b0 = (Boolean) parameters.getValue(0);
                    if (b0 == null || !b0) {
                        throw new UserDefineException(wrap(qRuntime.attachment(), (String) parameters.getValue(1)));
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

    private static class AssertFalseFunction implements QFunction {
        @Override
        public Object call(QRuntime qRuntime, Parameters parameters) throws Exception {
            int pSize = parameters.size();
            switch (pSize) {
                case 1:
                    Boolean b = (Boolean) parameters.getValue(0);
                    if (b == null || b) {
                        throw new UserDefineException(wrap(qRuntime.attachment(), "assert fail"));
                    }
                    return null;
                case 2:
                    Boolean b0 = (Boolean) parameters.getValue(0);
                    if (b0 == null || b0) {
                        throw new UserDefineException(wrap(qRuntime.attachment(), (String) parameters.getValue(1)));
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
