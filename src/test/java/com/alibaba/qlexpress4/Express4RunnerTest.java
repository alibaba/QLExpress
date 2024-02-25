package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.annotation.QLFunction;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.context.ExpressContext;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;
import org.junit.Test;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class Express4RunnerTest {

    @Test
    public void mapSetGetTest() {
        String script = "a = new HashMap<>();" +
                "a['aaa'] = 'bbb';" +
                "a";
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                .securityStrategy(QLSecurityStrategy.open())
                .build());
        Object result = express4Runner.execute(script, Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
        assertTrue(result instanceof HashMap);
        assertEquals("bbb", ((HashMap<?, ?>) result).get("aaa"));
    }

    @Test
    public void shortCircuitTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        assertTrue((Boolean) express4Runner.execute("true && true && true",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));
        assertFalse((Boolean) express4Runner.execute("true && false && (1/0)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));
        assertTrue((Boolean) express4Runner.execute("a = 1+1+1+1+1+1+1+1+1;" +
                        "true && true && true",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));

        assertFalse((Boolean) express4Runner.execute("false || false || false",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));
        assertTrue((Boolean) express4Runner.execute("false || true || (1/0)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));
        assertTrue((Boolean) express4Runner.execute("(false && (1/0)) || true || (1/0)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));

        assertErrorCode(express4Runner, "true && (1/0)", "INVALID_ARITHMETIC");
    }

    @Test
    public void assignTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        assertErrorCode(express4Runner, "1 = 0", "SYNTAX_ERROR");
    }

    @Test
    public void ifTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        QLOptions debugOptions = QLOptions.builder()
                .build();
        Object result = express4Runner.execute("if (2==3) {if (2==2) 10} else 4",
                Collections.emptyMap(), debugOptions);
        assertEquals(4, result);
    }

    @Test
    public void debugExample() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        QLOptions debugOptions = QLOptions.builder()
                .build();
        Object result = express4Runner.execute("1+1", Collections.emptyMap(), debugOptions);
        assertEquals(2, result);

        Object result1 = express4Runner.execute("false || true || (1/0)",
                Collections.emptyMap(), debugOptions);
        assertTrue((Boolean) result1);
    }

    @Test
    public void populateTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        QLOptions populateOption = QLOptions.builder()
                .polluteUserContext(true).build();
        Map<String, Object> populatedMap = new HashMap<>();
        populatedMap.put("b", 10);
        express4Runner.execute("a = 11;b = a", populatedMap, populateOption);
        assertEquals(11, populatedMap.get("a"));
        assertEquals(11, populatedMap.get("b"));

        // no population
        Map<String, Object> populatedMap2 = new HashMap<>();
        express4Runner.execute("a = 11", populatedMap2, QLOptions.DEFAULT_OPTIONS);
        assertFalse(populatedMap2.containsKey("a"));

        Map<String, Object> populatedMap3 = new HashMap<>();
        populatedMap3.put("a", 10);
        assertEquals(19, express4Runner
                .execute("a = 19;a", populatedMap3, QLOptions.DEFAULT_OPTIONS));
        assertEquals(10, populatedMap3.get("a"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mapLiteralTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> result = (Map<String, Object>) express4Runner
                .execute("{a:123,'b':'test'}", new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
        assertEquals(123, result.get("a"));
        assertEquals("test", result.get("b"));
    }

    @Test
    public void classFieldTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        assertEquals(List.class, express4Runner.execute("List.class", Collections.emptyMap(),
                QLOptions.DEFAULT_OPTIONS));
        assertEquals(List.class, express4Runner.execute("java.util.List.class", Collections.emptyMap(),
                QLOptions.DEFAULT_OPTIONS));
    }

    @Test
    public void invalidOperatorTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        assertErrorCode(express4Runner, "1+++a", "UNKNOWN_OPERATOR");
        assertErrorCode(express4Runner, "a abcd bb", "UNKNOWN_OPERATOR");
        assertErrorCode(express4Runner, "import a.b v = 1", "SYNTAX_ERROR");
        assertErrorCode(express4Runner, "a.*bbb", "UNKNOWN_OPERATOR");
    }

    @Test
    public void logicAndTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object result = express4Runner.execute("null && true", Collections.emptyMap(),
                QLOptions.DEFAULT_OPTIONS);
        assertFalse((Boolean) result);
    }

    @Test
    public void numberTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object[][] scriptAndExpects = new Object[][] {
                {"12323", 12323},
                {"2147483647", 2147483647},
                {"9223372036854775807", 9223372036854775807L},
                {"18446744073709552000", new BigInteger("18446744073709552000")},
                {"1.1", 1.1},
                {"1.7976931348623157E308", Double.MAX_VALUE},
                {"1.", 1.0}, {".1", 0.1},
                {"0xfff", 4095}, {"0b11", 3}, {"072", 58},
                {"12e1", 120.0}, {"12.1E2", 1210.0},
                {"10l", 10L}, {"10L", 10L}, {"10d", 10.0}, {"10.313D", 10.313d}, {"10.2f", 10.2f}, {"10.2F", 10.2f}
        };

        for (Object[] scriptAndExpect : scriptAndExpects) {
            assertResultEquals(express4Runner, (String) scriptAndExpect[0], scriptAndExpect[1]);
        }
    }

    @Test
    public void numberAmbiguousValueTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                .securityStrategy(QLSecurityStrategy.open())
                .build());
        Object result = express4Runner.execute("1.doubleValue()", new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
        assertEquals(1d, result);
    }

    @Test
    public void errorReportColNumTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        try {
            express4Runner.execute("1+1;\n2+2;\n1+cc()", Collections.emptyMap(),
                    QLOptions.DEFAULT_OPTIONS);
        } catch (QLRuntimeException e) {
            assertEquals(2, e.getColNo());
            assertEquals(3, e.getLineNo());
        }

        try {
            express4Runner.execute("1/0", Collections.emptyMap(),
                    QLOptions.DEFAULT_OPTIONS);
        } catch (QLRuntimeException e) {
            assertEquals(1, e.getColNo());
            assertEquals(1, e.getLineNo());
        }

        try {
            express4Runner.execute("a[]", Collections.emptyMap(),
                    QLOptions.DEFAULT_OPTIONS);
        } catch (QLSyntaxException e) {
            assertEquals(1, e.getLineNo());
            assertEquals(2, e.getColNo());
        }
    }

    public static class MyFunctionUtil {
        @QLFunction({"myAdd", "iAdd"})
        public int add(int a, int b) {
            return  a + b;
        }

        @QLFunction("arr3")
        public static int[] array3(int a, int b, int c) {
            return new int[] {a, b, c};
        }
    }

    @Test
    public void addFunctionByAnnotationTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        express4Runner.addObjFunction(new MyFunctionUtil());
        Object result = express4Runner.execute("myAdd(1,2) + iAdd(5,6)", new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
        assertEquals(14, result);
        express4Runner.addStaticFunction(MyFunctionUtil.class);
        Object result1 = express4Runner.execute("arr3(5,9,10)[2]", new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
        assertEquals(10 ,result1);
    }

    @Test
    public void customExpressKeyValue() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        Map<String, Object> attachments = new HashMap<>();

        Map<String, Object> subAttachA = new HashMap<>();
        subAttachA.put("aa", 123);

        Map<String, Object> subAttachB = new HashMap<>();
        subAttachB.put("bb", 12);

        attachments.put("a", subAttachA);
        attachments.put("b", subAttachB);

        QLOptions qlOptions = QLOptions.builder()
                .attachments(attachments)
                .build();
        Object result = express4Runner.execute("${/a/aa} + ${/b/bb}", new ExpressContext() {
            @Override
            public Value get(Map<String, Object> attachments, String variableName) {
                String[] split = variableName.split("/");
                Map<String, Object> subMap = (Map<String, Object>) attachments.get(split[1]);
                return new DataValue(subMap.get(split[2]));
            }
        }, qlOptions);
        assertEquals(135, result);
    }

    @Test
    public void getOutVarNamesTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Set<String> outVarNames = express4Runner.getOutVarNames("int a = 1, b = 10;\n" +
                "c = 11\n" +
                "e = a + b + c + d\n" +
                "f+e");
        Set<String> expectSet = new HashSet<>();
        expectSet.add("d");
        expectSet.add("f");
        assertEquals(expectSet, outVarNames);

        Set<String> outVarNames2 = express4Runner.getOutVarNames("if (true) {a = 10} else {a}");
        Set<String> expectSet2 = new HashSet<>();
        expectSet2.add("a");
        assertEquals(expectSet2, outVarNames2);

        Set<String> outVarNames3 = express4Runner.getOutVarNames("while (a>2) {a++;b=100} a+b");
        Set<String> expectSet3 = new HashSet<>();
        expectSet3.add("a");
        expectSet3.add("b");
        assertEquals(expectSet3, outVarNames3);
    }

    @Test
    public void addOperatorTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object result = express4Runner.execute("'1.2'+'2.3'", new HashMap<>(), QLOptions.builder()
                .cache(false).build());
        assertEquals("1.22.3", result);
        boolean replaceResult = express4Runner.replaceDefaultOperator("+", (left, right) ->
                Double.parseDouble(left.get().toString()) + Double.parseDouble(right.get().toString()));
        assertTrue(replaceResult);
        Object result1 = express4Runner.execute("'1.2'+'2.3'", new HashMap<>(), QLOptions.builder()
                .cache(false).build());
        assertEquals(3.5d, result1);
        express4Runner.addOperator("join", (left, right) -> left.get().toString() + right.get().toString());
        Object result2 = express4Runner.execute("1.2 join 2", new HashMap<>(), QLOptions.builder()
                .cache(false).build());
        assertEquals("1.22", result2);

        express4Runner.addOperator(".*", (left, right) -> {
            String fieldName = (String) right.get();
            return ((List<Map<?, ?>>) left.get()).stream()
                    .map(m -> m.get(fieldName))
                    .collect(Collectors.toList());
        }, QLPrecedences.GROUP);
        Object result3 = express4Runner.execute("[{a:1}, {a:5}].*a", new HashMap<>(), QLOptions.builder()
                .cache(false).build());
        List<Integer> expect = new ArrayList<>();
        expect.add(1);
        expect.add(5);
        assertEquals(expect, result3);

        Object result4 = express4Runner.execute("[{a:1}, {a:5}, {a:10}, {a:20}].*a[1:-1]",
                new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
        assertEquals(Arrays.asList(5, 10), result4);

        try {
            express4Runner.execute("[{a:1}, {a:5}].*'abc'", new HashMap<>(), QLOptions.builder()
                    .cache(false).build());
        } catch (QLRuntimeException e) {
            assertEquals("INVALID_ARGUMENT", e.getErrorCode());
            assertEquals("custom e test", e.getReason());
        }
    }

    @Test
    public void methodInvokeCauseTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                .securityStrategy(QLSecurityStrategy.open())
                .build());
        try {
            express4Runner.execute("l = [];l.get(3)", new HashMap<>(), QLOptions.builder()
                    .cache(false).build());
        } catch (QLRuntimeException e) {
            assertTrue(e.getCause() instanceof IndexOutOfBoundsException);
        }
    }

    @Test
    public void innerFunctionExceptionTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        express4Runner.addFunction("testExp", () -> {throw new RuntimeException("inner test");});
        assertNotNull(express4Runner.getFunction("testExp"));
        try {
            express4Runner.execute("1+testExp()+10", new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
        } catch (QLException e) {
            assertEquals("inner test", e.getCause().getMessage());
            assertEquals("[Error FUNCTION_INNER_EXCEPTION: function 'testExp',inner test]\n" +
                    "[Near: 1+testExp()+10]\n" +
                    "         ^^^^^^^\n" +
                    "[Line: 1, Column: 2]", e.getMessage());
            assertEquals(2, e.getPos());
        }
    }

    @Test
    public void avoidNullPointerTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object result = express4Runner.execute("'a '+${a}+aa('xxx')", new HashMap<>(), QLOptions.builder()
                .avoidNullPointer(true).build());
        assertEquals("a nullnull", result);
    }

    @Test
    public void atFunctionTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        express4Runner.addFunction("@", (String s) -> s + "," + s);
        Object result = express4Runner.execute("@('a')", new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
        assertEquals("a,a", result);
    }

    @Test
    public void multilineStrNotCloseTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        try {
            express4Runner.execute("a=1;'aaa \n \n cccc", new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
            fail("should throw");
        } catch (QLException e) {
            assertEquals("invalid QuoteStringLiteral", e.getReason());
        }

        try {
            express4Runner.execute("\"aaa \n cccc", new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
            fail("should throw");
        } catch (QLException e) {
            assertEquals("invalid StringLiteral", e.getReason());
        }
    }

    @Test
    public void chineseCommaPropertyTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object result = express4Runner.execute("{'销售方地址、电话':'test'}.销售方地址、电话",
                new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
        assertEquals("test", result);
    }

    @Test
    public void stringEscapeTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        // "'\na
        Object result = express4Runner.execute("\"\\\"\"+'\\'\na'",
                new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
        assertEquals("\"'\na", result);
    }

    private void assertResultEquals(Express4Runner express4Runner, String script, Object expect) {
        assertResultPredicate(express4Runner, script, result -> Objects.equals(expect, result));
    }

    private void assertResultPredicate(Express4Runner express4Runner, String script, Predicate<Object> predicate) {
        Object result = express4Runner.execute(script, Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
        assertTrue(predicate.test(result));
    }

    private void assertErrorCode(Express4Runner express4Runner, String script, String errCode) {
        try {
            express4Runner.execute(script, Collections.emptyMap(),
                    QLOptions.DEFAULT_OPTIONS);
        } catch (QLException e) {
            assertEquals(errCode, e.getErrorCode());
        }
    }

    private void assertErrorCode(Express4Runner express4Runner, Map<String, Object> existMap,
                                 String script, String errCode) {
        try {
            express4Runner.execute(script, existMap, QLOptions.DEFAULT_OPTIONS);
            fail("no errCode:" + errCode);
        } catch (QLException e) {
            assertEquals(errCode, e.getErrorCode());
        }
    }
}