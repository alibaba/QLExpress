package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.annotation.QLFunction;
import com.alibaba.qlexpress4.aparser.ImportManager;
import com.alibaba.qlexpress4.aparser.InterpolationMode;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.exception.QLTimeoutException;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.context.ExpressContext;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.function.ExtensionFunction;
import com.alibaba.qlexpress4.runtime.trace.ExpressionTrace;
import com.alibaba.qlexpress4.runtime.trace.TracePointTree;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;
import com.alibaba.qlexpress4.test.function.HelloFunction;
import com.alibaba.qlexpress4.test.qlalias.Order;
import com.alibaba.qlexpress4.test.qlalias.Patient;
import com.alibaba.qlexpress4.test.qlalias.Person;
import com.alibaba.qlexpress4.test.qlalias.User;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class Express4RunnerTest {

    @Test
    public void parseToCacheTest() {
        // tag::parseToCache[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        express4Runner.parseToDefinitionWithCache("a+b");
        // end::parseToCache[]
    }

    @Test
    public void expressionTraceTest() {
        // tag::expressionTrace[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder().traceExpression(true).build());
        express4Runner.addFunction("myTest", (Predicate<Integer>) i -> i > 10);

        Map<String, Object> context = new HashMap<>();
        context.put("a", true);
        QLResult result = express4Runner.execute("a && (!myTest(11) || false)", context,
                QLOptions.builder().traceExpression(true).build());
        Assert.assertFalse((Boolean) result.getResult());

        List<ExpressionTrace> expressionTraces = result.getExpressionTraces();
        Assert.assertEquals(1, expressionTraces.size());
        ExpressionTrace expressionTrace = expressionTraces.get(0);
        Assert.assertEquals("OPERATOR && false\n" +
                "  | VARIABLE a true\n" +
                "  | OPERATOR || false\n" +
                "      | OPERATOR ! false\n" +
                "          | FUNCTION myTest true\n" +
                "              | VALUE 11 11\n" +
                "      | VALUE false false\n", expressionTrace.toPrettyString(0));

        // short circuit
        context.put("a", false);
        QLResult resultShortCircuit = express4Runner.execute("(a && true) && (!myTest(11) || false)", context,
                QLOptions.builder().traceExpression(true).build());
        Assert.assertFalse((Boolean) resultShortCircuit.getResult());
        ExpressionTrace expressionTraceShortCircuit = resultShortCircuit.getExpressionTraces().get(0);
        Assert.assertEquals("OPERATOR && false\n" +
                "  | OPERATOR && false\n" +
                "      | VARIABLE a false\n" +
                "      | VALUE true \n" +
                "  | OPERATOR || \n" +
                "      | OPERATOR ! \n" +
                "          | FUNCTION myTest \n" +
                "              | VALUE 11 \n" +
                "      | VALUE false \n", expressionTraceShortCircuit.toPrettyString(0));
        Assert.assertTrue(expressionTraceShortCircuit.getChildren().get(0).isEvaluated());
        Assert.assertFalse(expressionTraceShortCircuit.getChildren().get(1).isEvaluated());

        // in
        QLResult resultIn= express4Runner.execute("'ab' in ['cc', 'dd', 'ff']", context,
                QLOptions.builder().traceExpression(true).build());
        Assert.assertFalse((Boolean) resultIn.getResult());
        ExpressionTrace expressionTraceIn = resultIn.getExpressionTraces().get(0);
        Assert.assertEquals("OPERATOR in false\n" +
                "  | VALUE 'ab' ab\n" +
                "  | LIST [ [cc, dd, ff]\n" +
                "      | VALUE 'cc' cc\n" +
                "      | VALUE 'dd' dd\n" +
                "      | VALUE 'ff' ff\n", expressionTraceIn.toPrettyString(0));
        // end::expressionTrace[]
    }

    @Test
    public void getExpressionTracePointsTest() {
        // tag::getExpressionTracePoints[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        TracePointTree tracePointTree = express4Runner.getExpressionTracePoints("1+3+5*ab+9").get(0);
        Assert.assertEquals("OPERATOR +\n" +
                "  | OPERATOR +\n" +
                "      | OPERATOR +\n" +
                "          | VALUE 1\n" +
                "          | VALUE 3\n" +
                "      | OPERATOR *\n" +
                "          | VALUE 5\n" +
                "          | VARIABLE ab\n" +
                "  | VALUE 9\n", tracePointTree.toPrettyString(0));
        // end::getExpressionTracePoints[]

        TracePointTree tracePointTreeFunction = express4Runner
                .getExpressionTracePoints("ab && (myTest(1,2) || false)").get(0);
        Assert.assertEquals("OPERATOR &&\n" +
                "  | VARIABLE ab\n" +
                "  | OPERATOR ||\n" +
                "      | FUNCTION myTest\n" +
                "          | VALUE 1\n" +
                "          | VALUE 2\n" +
                "      | VALUE false\n", tracePointTreeFunction.toPrettyString(0));

        TracePointTree tracePointIn = express4Runner.getExpressionTracePoints("'ab' in ['cc', 'dd', 'ff']").get(0);
        Assert.assertEquals("OPERATOR in\n" +
                "  | VALUE 'ab'\n" +
                "  | LIST [\n" +
                "      | VALUE 'cc'\n" +
                "      | VALUE 'dd'\n" +
                "      | VALUE 'ff'\n", tracePointIn.toPrettyString(0));
    }

    @Test
    public void checkSyntaxTest() {
        // tag::checkSyntax[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        try {
            express4Runner.parseToSyntaxTree("a+b;\n(a+b");
            fail();
        } catch (QLSyntaxException e) {
            assertEquals(2, e.getLineNo());
            assertEquals(4, e.getColNo());
            assertEquals("SYNTAX_ERROR", e.getErrorCode());
            // <EOF> represents the end of script
            assertEquals("[Error SYNTAX_ERROR: mismatched input '<EOF>' expecting ')']\n" +
                    "[Near: a+b; (a+b<EOF>]\n" +
                    "                ^^^^^\n" +
                    "[Line: 2, Column: 4]", e.getMessage());
        }
        // end::checkSyntax[]

        try {
            express4Runner.parseToSyntaxTree("sellerId in [1001] || (sellerId not in [1001])");
            fail();
        } catch (QLSyntaxException e) {
            assertEquals("[Error SYNTAX_ERROR: mismatched input 'not' expecting ')']\n" +
                    "[Near: ...[1001] || (sellerId not in [1001])]\n" +
                    "                              ^^^\n" +
                    "[Line: 1, Column: 32]", e.getMessage());
        }
    }

    @Test
    public void addAliasTest() {
        // tag::addAlias[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        // add custom function zero
        express4Runner.addFunction("zero", (String ignore) -> 0);

        // keyword alias
        assertTrue(express4Runner.addAlias("如果", "if"));
        assertTrue(express4Runner.addAlias("则", "then"));
        assertTrue(express4Runner.addAlias("否则", "else"));
        assertTrue(express4Runner.addAlias("返回", "return"));
        // operator alias
        assertTrue(express4Runner.addAlias("大于", ">"));
        // function alias
        assertTrue(express4Runner.addAlias("零", "zero"));

        Map<String, Object> context = new HashMap<>();
        context.put("语文", 90);
        context.put("数学", 90);
        context.put("英语", 90);

        Object result = express4Runner.execute(
                "如果 (语文 + 数学 + 英语 大于 270) 则 {返回 1;} 否则 {返回 零();}",
                context, QLOptions.DEFAULT_OPTIONS
        ).getResult();
        assertEquals(0, result);
        // end::addAlias[]
    }

    @Test
    public void inTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        express4Runner.addAlias("属于", "in");
        assertTrue((Boolean) express4Runner.execute("1 属于 [1,2]", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult());
        assertFalse((Boolean) express4Runner.execute("1 属于 [3,2]", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult());
    }


    @Test
    public void cacheDocTest() {
        // tag::cacheSwitch[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        // open cache switch
        express4Runner.execute("1+2", new HashMap<>(), QLOptions.builder()
                .cache(true).build());
        // end::cacheSwitch[]
    }

    @Test
    public void docQuickStartTest() {
        // tag::firstQl[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> context = new HashMap<>();
        context.put("a", 1);
        context.put("b", 2);
        context.put("c", 3);
        Object result = express4Runner.execute("a + b * c", context, QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(7, result);
        // end::firstQl[]
    }

    @Test
    public void docAddFunctionAndOperatorTest() {
        // tag::addFunctionAndOperator[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        // custom function
        express4Runner.addVarArgsFunction("join", params ->
                Arrays.stream(params).map(Object::toString).collect(Collectors.joining(",")));
        Object resultFunction = express4Runner.execute("join(1,2,3)", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals("1,2,3", resultFunction);

        // custom operator
        express4Runner.addOperatorBiFunction("join", (left, right) -> left + "," + right);
        Object resultOperator = express4Runner.execute("1 join 2 join 3", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals("1,2,3", resultOperator);
        // end::addFunctionAndOperator[]
    }

    @Test
    public void docImportJavaTest() {
        // tag::importJavaCls[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                // open security strategy, which allows access to all Java classes within the application.
                .securityStrategy(QLSecurityStrategy.open())
                .build()
        );
        // Import Java classes using the import statement.
        Map<String, Object> params = new HashMap<>();
        params.put("a", 1);
        params.put("b", 2);
        Object result = express4Runner.execute("import com.alibaba.qlexpress4.QLImportTester;" +
                "QLImportTester.add(a,b)", params, QLOptions.DEFAULT_OPTIONS).getResult();
        Assert.assertEquals(3, result);
        // end::importJavaCls[]
    }

    @Test
    public void docDefaultImportJavaTest() {
        // tag::defaultImport[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                .addDefaultImport(
                        Collections.singletonList(ImportManager.importCls("com.alibaba.qlexpress4.QLImportTester"))
                )
                .securityStrategy(QLSecurityStrategy.open())
                .build()
        );
        Object result = express4Runner.execute("QLImportTester.add(1,2)", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        Assert.assertEquals(3, result);
        // end::defaultImport[]
    }

    @Test
    public void docTryCatchTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object result = express4Runner.execute("1 + try {\n" +
                "  100 + 1/0\n" +
                "} catch(e) {\n" +
                "  // Throw a zero-division exception\n" +
                "  11\n" +
                "}", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        Assert.assertEquals(12, result);
    }

    @Test
    public void docPreciseTest() {
        // tag::bigDecimalForPrecise[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object result = express4Runner.execute("0.1", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertTrue(result instanceof BigDecimal);
        // end::bigDecimalForPrecise[]

        // tag::preciseComparisonWithJava[]
        assertNotEquals(0.3, 0.1 + 0.2, 0.0);
        assertTrue((Boolean) express4Runner.execute("0.3==0.1+0.2", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        // end::preciseComparisonWithJava[]

        // tag::preciseSwitch[]
        Map<String, Object> context = new HashMap<>();
        context.put("a", 0.1);
        context.put("b", 0.2);
        assertFalse((Boolean) express4Runner.execute("0.3==a+b", context, QLOptions.DEFAULT_OPTIONS).getResult());
        // open precise switch
        assertTrue((Boolean) express4Runner.execute("0.3==a+b", context, QLOptions.builder().precise(true).build()).getResult());
        // end::preciseSwitch[]
    }


    @Test
    public void mapSetGetTest() {
        String script = "a = new HashMap<>();" +
                "a['aaa'] = 'bbb';" +
                "a";
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                .securityStrategy(QLSecurityStrategy.open())
                .build());
        Object result = express4Runner.execute(script, Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertTrue(result instanceof HashMap);
        assertEquals("bbb", ((HashMap<?, ?>) result).get("aaa"));
    }

    @Test
    public void shortCircuitTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        assertTrue((Boolean) express4Runner.execute("true && true && true",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        assertFalse((Boolean) express4Runner.execute("true && false && (1/0)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        assertTrue((Boolean) express4Runner.execute("a = 1+1+1+1+1+1+1+1+1;" +
                        "true && true && true",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());

        assertFalse((Boolean) express4Runner.execute("false || false || false",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        assertTrue((Boolean) express4Runner.execute("false || true || (1/0)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        assertTrue((Boolean) express4Runner.execute("(false && (1/0)) || true || (1/0)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());

        assertErrorCode(express4Runner, "true && (1/0)", "INVALID_ARITHMETIC");

        // disable short circuit test
        QLOptions disableShortCircuitOp = QLOptions.builder().shortCircuitDisable(true).build();
        assertTrue((Boolean) express4Runner.execute("false || false || true",
                Collections.emptyMap(), disableShortCircuitOp).getResult());
        assertFalse((Boolean) express4Runner.execute("(true && false) || false",
                Collections.emptyMap(), disableShortCircuitOp).getResult());
    }

    @Test
    public void disableShortCircuitTest() {
        // tag::disableShortCircuit[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        // execute when enable short circuit (default)
        // `1/0` is short-circuited by the preceding `false`, so it won't throw an error.
        assertFalse((Boolean) express4Runner.execute("false && (1/0)",
                Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult());
        try {
            // execute when disable short circuit
            express4Runner.execute("false && (1/0)",
                    Collections.emptyMap(), QLOptions.builder().shortCircuitDisable(true).build());
            fail();
        } catch (QLException e) {
            Assert.assertEquals("INVALID_ARITHMETIC", e.getErrorCode());
            Assert.assertEquals("Division by zero", e.getReason());
        }
        // end::disableShortCircuit[]
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
                Collections.emptyMap(), debugOptions).getResult();
        assertEquals(4, result);
    }

    @Test
    public void debugExample() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        QLOptions debugOptions = QLOptions.builder()
                .build();
        Object result = express4Runner.execute("1+1", Collections.emptyMap(), debugOptions).getResult();
        assertEquals(2, result);

        Object result1 = express4Runner.execute("false || true || (1/0)",
                Collections.emptyMap(), debugOptions).getResult();
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
                .execute("a = 19;a", populatedMap3, QLOptions.DEFAULT_OPTIONS).getResult());
        assertEquals(10, populatedMap3.get("a"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mapLiteralTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> result = (Map<String, Object>) express4Runner
                .execute("{a:123,'b':'test'}", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(123, result.get("a"));
        assertEquals("test", result.get("b"));
    }

    @Test
    public void classFieldTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                .securityStrategy(QLSecurityStrategy.open()).build());
        assertEquals(List.class, express4Runner.execute("List.class", Collections.emptyMap(),
                QLOptions.DEFAULT_OPTIONS).getResult());
        assertEquals(List.class, express4Runner.execute("java.util.List.class", Collections.emptyMap(),
                QLOptions.DEFAULT_OPTIONS).getResult());
    }

    @Test
    public void invalidOperatorTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        assertErrorCode(express4Runner, "1+++a", "SYNTAX_ERROR");
        assertErrorCode(express4Runner, "a abcd bb", "SYNTAX_ERROR");
        assertErrorCode(express4Runner, "import a.b v = 1", "SYNTAX_ERROR");
        assertErrorCode(express4Runner, "a.*bbb", "SYNTAX_ERROR");
    }

    @Test
    public void importNotAtBeginningTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        try {
            express4Runner.execute("a = 10;\n" +
                    "import a.b.c;", new HashMap<>(), QLOptions.builder()
                    .cache(false).build());
            fail();
        } catch (QLSyntaxException e) {
            assertEquals("SYNTAX_ERROR", e.getErrorCode());
            assertEquals("Import statement is not at the beginning of the file.", e.getReason());
        }
    }

    @Test
    public void extensionFunctionTest() {
        // tag::extensionFunction[]
        ExtensionFunction helloFunction = new ExtensionFunction() {
            @Override
            public Class<?>[] getParameterTypes() {
                return new Class[0];
            }

            @Override
            public String getName() {
                return "hello";
            }

            @Override
            public Class<?> getDeclaringClass() {
                return String.class;
            }

            @Override
            public Object invoke(Object obj, Object[] args) throws InvocationTargetException, IllegalAccessException {
                String originStr = (String) obj;
                return "Hello," + originStr;
            }
        };
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                .addExtensionFunctions(Collections.singletonList(helloFunction))
                .build());
        Object result = express4Runner.execute("'jack'.hello()", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals("Hello,jack", result);
        // end::extensionFunction[]
    }

    @Test
    public void scripTimeoutTest() {
        // tag::scripTimeout[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        try {
            express4Runner.execute("while (true) {\n" +
                    "  1+1\n" +
                    "}", Collections.emptyMap(), QLOptions.builder().timeoutMillis(10L).build());
            fail("should timeout");
        } catch (QLTimeoutException e) {
            assertEquals(QLErrorCodes.SCRIPT_TIME_OUT.name(), e.getErrorCode());
        }
        // end::scripTimeout[]

        try {
            express4Runner.execute("while (2) {\n" +
                    "  1+1\n" +
                    "}", Collections.emptyMap(), QLOptions.builder().timeoutMillis(10L).build());
            fail("should exception");
        } catch (QLTimeoutException e) {
            fail();
        } catch (QLRuntimeException e) {
            assertEquals(QLErrorCodes.WHILE_CONDITION_BOOL_REQUIRED.name(), e.getErrorCode());
        }
    }

    @Test
    public void interpolationTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> context = new HashMap<>();
        context.put("a", 1);
        QLResult result = express4Runner.execute("\"Hello,${a+1}\"", context, QLOptions.DEFAULT_OPTIONS);
        Assert.assertEquals("Hello,2", result.getResult());

        // tag::disableInterpolation[]
        Express4Runner express4RunnerDisable = new Express4Runner(
                // disable string interpolation
                InitOptions.builder().interpolationMode(InterpolationMode.DISABLE).build());
        Assert.assertEquals("Hello,${ a + 1 }", express4RunnerDisable
                .execute("\"Hello,${ a + 1 }\"", context, QLOptions.DEFAULT_OPTIONS).getResult());
        Assert.assertEquals("Hello,${lll", express4RunnerDisable
                .execute("\"Hello,${lll\"", context, QLOptions.DEFAULT_OPTIONS).getResult());
        Assert.assertEquals("Hello,aaa $ lll\"\n\b", express4RunnerDisable
                .execute("\"Hello,aaa $ lll\\\"\n\b\"", context, QLOptions.DEFAULT_OPTIONS).getResult());
        // end::disableInterpolation[]
    }

    @Test
    public void logicAndTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object result = express4Runner.execute("null && true", Collections.emptyMap(),
                QLOptions.DEFAULT_OPTIONS).getResult();
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
                {"1.1", new BigDecimal("1.1")},
                {"1.25", 1.25d},
                {"1.", 1.0}, {".1", new BigDecimal("0.1")},
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
        Object result = express4Runner.execute("1.doubleValue()", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
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
        Object result = express4Runner.execute("myAdd(1,2) + iAdd(5,6)", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(14, result);
        express4Runner.addStaticFunction(MyFunctionUtil.class);
        Object result1 = express4Runner.execute("arr3(5,9,10)[2]", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(10 ,result1);
    }

    @Test
    public void variableStartsWithWellNumber() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        HashMap<Object, Object> context = new HashMap<>();
        context.put("#cost", 10);
        Object result = express4Runner.execute("#cost + 1", context, QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(11, result);
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
        }, qlOptions).getResult();
        assertEquals(135, result);
    }

    @Test
    public void getOutVarNamesTest() {
        // tag::getOutVarNames[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Set<String> outVarNames = express4Runner.getOutVarNames("int a = 1, b = 10;\n" +
                "c = 11\n" +
                "e = a + b + c + d\n" +
                "f+e");
        Set<String> expectSet = new HashSet<>();
        expectSet.add("d");
        expectSet.add("f");
        assertEquals(expectSet, outVarNames);
        // end::getOutVarNames[]

        Set<String> outVarNames2 = express4Runner.getOutVarNames("if (true) {a = 10} else {a}");
        Set<String> expectSet2 = new HashSet<>();
        expectSet2.add("a");
        assertEquals(expectSet2, outVarNames2);

        Set<String> outVarNames3 = express4Runner.getOutVarNames("while (a>2) {a++;b=100} a+b");
        Set<String> expectSet3 = new HashSet<>();
        expectSet3.add("a");
        assertEquals(expectSet3, outVarNames3);

        express4Runner.addFunction("dd", () -> {});
        Set<String> outVarNames4 = express4Runner.getOutVarNames("cc(a,bc(2,m,1))\ndd(c)");
        Set<String> expectSet4 = new HashSet<>();
        expectSet4.add("a");
        expectSet4.add("m");
        expectSet4.add("c");
        Assert.assertEquals(expectSet4, outVarNames4);

        Set<String> outVarNames5 = express4Runner.getOutVarNames("resultSet = ''; " +
                "if (a == 11)" +
                "true");
        Set<String> expectSet5 = new HashSet<>();
        expectSet5.add("a");
        Assert.assertEquals(expectSet5, outVarNames5);
    }

    @Test
    public void addOperatorTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object result = express4Runner.execute("'1.2'+'2.3'", new HashMap<>(), QLOptions.builder()
                .cache(false).build()).getResult();
        assertEquals("1.22.3", result);
        boolean replaceResult = express4Runner.replaceDefaultOperator("+", (left, right) ->
                Double.parseDouble(left.get().toString()) + Double.parseDouble(right.get().toString()));
        assertTrue(replaceResult);
        Object result1 = express4Runner.execute("'1.2'+'2.3'", new HashMap<>(), QLOptions.builder()
                .cache(false).build()).getResult();
        assertEquals(3.5d, result1);
        express4Runner.addOperator("join", (left, right) -> left.get().toString() + right.get().toString());
        Object result2 = express4Runner.execute("1.2 join 2", new HashMap<>(), QLOptions.builder()
                .cache(false).build()).getResult();
        assertEquals("1.22", result2);

        express4Runner.addOperator(".*", (left, right) -> {
            String fieldName = (String) right.get();
            return ((List<Map<?, ?>>) left.get()).stream()
                    .map(m -> m.get(fieldName))
                    .collect(Collectors.toList());
        }, QLPrecedences.GROUP);
        Object result3 = express4Runner.execute("[{a:1}, {a:5}].*a", new HashMap<>(), QLOptions.builder()
                .cache(false).build()).getResult();
        List<Integer> expect = new ArrayList<>();
        expect.add(1);
        expect.add(5);
        assertEquals(expect, result3);

        Object result4 = express4Runner.execute("[{a:1}, {a:5}, {a:10}, {a:20}].*a[1:-1]",
                new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
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
            assertEquals("[Error INVOKE_FUNCTION_INNER_ERROR: exception from inner when invoking function 'testExp', error message: inner test]\n" +
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
                .avoidNullPointer(true).build()).getResult();
        assertEquals("a nullnull", result);
    }

    @Test
    public void atFunctionTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        express4Runner.addFunction("@", (String s) -> s + "," + s);
        Object result = express4Runner.execute("@('a')", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals("a,a", result);
    }

    @Test
    public void multilineStrNotCloseTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        try {
            express4Runner.execute("a=1;'aaa \n \n cccc", new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
            fail("should throw");
        } catch (QLException e) {
            assertEquals("unterminated string literal", e.getReason());
        }

        try {
            express4Runner.execute("\"aaa \n cccc", new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
            fail("should throw");
        } catch (QLException e) {
            assertEquals("unterminated string literal", e.getReason());
        }
    }

    @Test
    public void chineseCommaPropertyTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object result = express4Runner.execute("{'销售方地址、电话':'test'}.销售方地址、电话",
                new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals("test", result);
    }

    @Test
    public void stringEscapeTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        // "'\na
        Object result = express4Runner.execute("\"\\\"\"+'\\'\na'",
                new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals("\"'\na", result);
    }

    @Test
    public void addMacroTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                .securityStrategy(QLSecurityStrategy.open())
                .build());
        express4Runner.addMacro("mergeList", "l = [];l.addAll(a);l.addAll(b);l");
        Object result = express4Runner.execute("a=[1,2]\nb=[3,4]\nmergeList", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(express4Runner.execute("[1,2,3,4]", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult(), result);
    }

    public static class MyObj {
        public int a;
        public String b;
    }

    @Test
    public void executeWithObjContextTest() {
        MyObj myObj = new MyObj();
        myObj.a = 1;
        myObj.b = "test";

        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Object result = express4Runner.execute("a+b", myObj, QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals("1test", result);
    }

    @Test
    public void qlAliasTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                .securityStrategy(QLSecurityStrategy.open()).build());
        String[] exps = new String[] {
                "患者.birth", "1987-02-23",
                "患者.生日()", "1987-02-23",
                "患者.患者姓名", "老王",
                "患者.姓名", "老王",
                "患者.getBirth()==患者.出生年月()", "true",//方法注解
                "患者.生日()==患者.生日", "true",//get方法和字段名字一样是不冲突的
                "患者.患者姓名 + ' 今年 '+ 患者.获取年龄() +' 岁'", "老王 今年 34 岁",//任意方法的注解
                "患者.级别='低风险';return 患者.级别;", "低风险",
        };
        Person person = new Patient();
        person.setName("老王");
        person.setSex("男");
        person.setBirth("1987-02-23");
        for (int i = 0; i < exps.length; i += 2) {
            Object result = express4Runner.executeWithAliasObjects(exps[i], QLOptions.DEFAULT_OPTIONS, person).getResult();
            assertEquals(result.toString(), exps[i + 1]);
        }
    }

    @Test
    public void qlAliasDocTest() {
        // tag::qlAlias[]
        Order order = new Order();
        order.setOrderNum("OR123455");
        order.setAmount(100);

        User user = new User();
        user.setName("jack");
        user.setVip(true);

        // Calculate the Final Order Amount
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                .securityStrategy(QLSecurityStrategy.open()).build());
        Number result = (Number) express4Runner.executeWithAliasObjects("用户.是vip? 订单.金额 * 0.8 : 订单.金额",
                QLOptions.DEFAULT_OPTIONS, order, user).getResult();
        assertEquals(80, result.intValue());
        // end::qlAlias[]
    }

    @Test
    public void customComplexFunctionDocTest() {
        // tag::customComplexFunction[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        express4Runner.addFunction("hello", new HelloFunction());
        String resultJack = (String) express4Runner.execute("hello()", Collections.emptyMap(),
                // Additional information(tenant for example) can be brought into the custom function from outside via attachments
                QLOptions.builder()
                        .attachments(Collections.singletonMap("tenant", "jack"))
                        .build()).getResult();
        assertEquals("hello,jack", resultJack);
        String resultLucy = (String) express4Runner.execute("hello()", Collections.emptyMap(),
                QLOptions.builder()
                        .attachments(Collections.singletonMap("tenant", "lucy"))
                        .build()).getResult();
        assertEquals("hello,lucy", resultLucy);
        // end::customComplexFunction[]
    }

    private void assertResultEquals(Express4Runner express4Runner, String script, Object expect) {
        assertResultPredicate(express4Runner, script, result -> Objects.equals(expect, result));
    }

    private void assertResultPredicate(Express4Runner express4Runner, String script, Predicate<Object> predicate) {
        Object result = express4Runner.execute(script, Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertTrue(script, predicate.test(result));
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