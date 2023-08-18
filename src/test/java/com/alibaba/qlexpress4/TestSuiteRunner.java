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
import com.alibaba.qlexpress4.exception.UserDefineException;
import com.alibaba.qlexpress4.runtime.Parameters;
import com.alibaba.qlexpress4.runtime.function.QFunction;
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

    private static Express4Runner CONFIG_RUNNER = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

    private Express4Runner prepareRunner(InitOptions initOptions) {
        Express4Runner testRunner = new Express4Runner(initOptions);
        testRunner.addFunction(ASSERT_FUNCTION_NAME, new AssertFunction());
        testRunner.addFunction(ASSERT_FALSE_FUNCTION_NAME, new AssertFalseFunction());
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
        Path filePath = getTestSuiteRoot().resolve("java/property/private_member_attr_access_set.ql");
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
                (InitOptions.Builder) scriptOption.get("initOptions"));
        InitOptions initOptions = initOptionsBuilder.isPresent()?
                initOptionsBuilder.get().build():
                InitOptions.DEFAULT_OPTIONS;
        Express4Runner express4Runner = prepareRunner(initOptions);
        if (errCodeOp.isPresent()) {
            assertErrCode(express4Runner, path, qlScript, QLOptions.builder()
                .debug(debug)
                .attachments(attachments)
                .build(), errCodeOp.get(), debug);
            printOk(path);
            return;
        }
        Optional<QLOptions.Builder> optionsBuilder = scriptOptionOp.map(scriptOption ->
            (QLOptions.Builder)scriptOption.get("qlOptions"));
        QLOptions qlOptions = optionsBuilder.isPresent() ?
            optionsBuilder.get().debug(debug).attachments(attachments).build() :
            QLOptions.builder().debug(debug).attachments(attachments).build();

        try {
            express4Runner.execute(qlScript, Collections.emptyMap(), qlOptions);
            printOk(path);
        } catch (Exception e) {
            System.out.printf("%1$-95s %2$s\n", path, "error");
            throw e;
        }
    }

    private void printRunning(String path) {
        System.out.printf("%1$-98s %2$s\n", path, "running");
    }

    private void printOk(String path) {
        System.out.printf("%1$-98s %2$s\n", path, "ok");
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
            QLOptions qlOptions = QLOptions.builder()
                .defaultImport(Arrays.asList(
                    ImportManager.importCls("com.alibaba.qlexpress4.QLOptions"),
                    ImportManager.importCls("com.alibaba.qlexpress4.InitOptions")))
                .build();
            Map<String, Object> scriptOptions = (Map<String, Object>) CONFIG_RUNNER
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
                    Boolean b = (Boolean)parameters.getValue(0);
                    if (b == null || !b) {
                        throw new UserDefineException(wrap(qRuntime.attachment(), "assert fail"));
                    }
                    return null;
                case 2:
                    Boolean b0 = (Boolean)parameters.getValue(0);
                    if (b0 == null || !b0) {
                        throw new UserDefineException(wrap(qRuntime.attachment(), (String)parameters.getValue(1)));
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
                    Boolean b = (Boolean)parameters.getValue(0);
                    if (b == null || b) {
                        throw new UserDefineException(wrap(qRuntime.attachment(), "assert fail"));
                    }
                    return null;
                case 2:
                    Boolean b0 = (Boolean)parameters.getValue(0);
                    if (b0 == null || b0) {
                        throw new UserDefineException(wrap(qRuntime.attachment(), (String)parameters.getValue(1)));
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