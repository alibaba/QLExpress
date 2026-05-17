package com.alibaba.qlexpress4.api.parsecache;

import com.alibaba.fastjson2.JSON;
import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.QLResult;
import com.alibaba.qlexpress4.api.BatchAddFunctionResult;
import com.alibaba.qlexpress4.aparser.compiletimefunction.CompileTimeFunction;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.context.MapExpressContext;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.instruction.CallConstInstruction;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SerializableParseCacheTest {
    
    @Test
    public void jsonRoundTripImportAndExecute() {
        // tag::serializableParseCache[]
        Express4Runner producer = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        SerializableParseCache cache = producer.parseToSerializableCache("price * count");
        
        String json = JSON.toJSONString(cache);
        
        // The JSON has crossed the network, and the consumer receives it.
        SerializableParseCache parsed = JSON.parseObject(json, SerializableParseCache.class);
        
        Express4Runner consumer = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        Map<String, Object> context = new HashMap<>();
        context.put("price", 5);
        context.put("count", 3);
        QLResult result = consumer.execute(parsed, context, QLOptions.DEFAULT_OPTIONS);
        assertEquals(15, result.getResult());
        // end::serializableParseCache[]
        
        assertEquals(1, cache.getModelVersion());
        assertNotNull(cache.getScriptHash());
        assertEquals("main", cache.getMain().getName());
        assertFalse(cache.getMain().getInstructions().isEmpty());
        assertNotNull(cache.getMain().getInstructions().get(0).getSource());
    }
    
    @Test
    public void loadedParseCacheCanBeReusedOnSameRunner() {
        // tag::loadedSerializableParseCache[]
        Express4Runner producer = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        SerializableParseCache cache = producer.parseToSerializableCache("x * 2 + 1");
        
        Express4Runner consumer = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        LoadedParseCache loaded = consumer.loadSerializableCache(cache);
        
        assertEquals(7,
            consumer.execute(loaded, new MapExpressContext(Collections.singletonMap("x", 3)), QLOptions.DEFAULT_OPTIONS)
                .getResult());
        assertEquals(11,
            consumer.execute(loaded, new MapExpressContext(Collections.singletonMap("x", 5)), QLOptions.DEFAULT_OPTIONS)
                .getResult());
        assertEquals(cache.getScriptHash(), loaded.getScriptHash());
        // end::loadedSerializableParseCache[]
    }
    
    @Test
    public void runtimeErrorLocationSurvivesJsonRoundTrip() {
        String script = "a = 1\n" + "missing(1, 2)";
        Express4Runner producer = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        SerializableParseCache cache = producer.parseToSerializableCache(script);
        SerializableParseCache parsed = JSON.parseObject(JSON.toJSONString(cache), SerializableParseCache.class);
        
        Express4Runner consumer = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        try {
            consumer.execute(parsed, new HashMap<>(), QLOptions.DEFAULT_OPTIONS);
            fail("deserialized cache execution should fail");
        }
        catch (QLException e) {
            assertEquals(QLErrorCodes.FUNCTION_NOT_FOUND.name(), e.getErrorCode());
            assertEquals(2, e.getLineNo());
            assertEquals(1, e.getColNo());
            assertEquals("missing", e.getErrLexeme());
            assertEquals(1, e.getDiagnostic().getRange().getStart().getLine());
            assertEquals(0, e.getDiagnostic().getRange().getStart().getCharacter());
        }
    }
    
    @Test
    public void functionsLoopsCollectionsAndCustomOperatorRoundTrip() {
        Express4Runner producer = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        producer
            .addOperator("plus2", (left, right) -> (Integer)left.get() + (Integer)right.get() + 2, QLPrecedences.ADD);
        
        SerializableParseCache cache = producer.parseToSerializableCache("function add(int a, int b) {\n"
            + "  return a plus2 b\n" + "}\n" + "total = 0\n" + "for (ele: [1, 2, 3]) {\n" + "  total = total + ele\n"
            + "}\n" + "i = 0\n" + "while (i < 2) {\n" + "  total = total + i\n" + "  i = i + 1\n" + "}\n"
            + "m = {name: \"QL\", value: total}\n" + "add(m.value, 4)");
        
        String json = JSON.toJSONString(cache);
        SerializableParseCache parsed = JSON.parseObject(json, SerializableParseCache.class);
        
        Express4Runner consumer = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        consumer
            .addOperator("plus2", (left, right) -> (Integer)left.get() + (Integer)right.get() + 2, QLPrecedences.ADD);
        assertEquals(13, consumer.execute(parsed, new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult());
    }
    
    @Test
    public void addFunctionsDefinedInSerializableCache() {
        // tag::serializableParseCacheFunctionDefinition[]
        Express4Runner producer = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        SerializableParseCache cache =
            producer.parseToSerializableCache("base = seed\n" + "function remoteAdd(a, b) {\n" + "  return a + b\n"
                + "}\n" + "function capturedBase() {\n" + "  return base\n" + "}\n");
        SerializableParseCache parsed = JSON.parseObject(JSON.toJSONString(cache), SerializableParseCache.class);
        
        Express4Runner consumer = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        MapExpressContext context = new MapExpressContext(Collections.singletonMap("seed", 9));
        BatchAddFunctionResult addResult =
            consumer.addFunctionsDefinedInScript(parsed, context, QLOptions.DEFAULT_OPTIONS);
        assertEquals(2, addResult.getSucc().size());
        assertEquals(0, addResult.getFail().size());
        assertEquals(7, consumer.execute("remoteAdd(3, 4)", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult());
        assertEquals(9, consumer.execute("capturedBase()", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult());
        
        BatchAddFunctionResult duplicateResult = consumer
            .addFunctionsDefinedInScript(consumer.loadSerializableCache(parsed), context, QLOptions.DEFAULT_OPTIONS);
        assertEquals(0, duplicateResult.getSucc().size());
        assertEquals(2, duplicateResult.getFail().size());
        // end::serializableParseCacheFunctionDefinition[]
    }
    
    @Test
    public void tracePointsAreOptionalAndRoundTripWhenExported() {
        Express4Runner traceProducer = new Express4Runner(InitOptions.builder().traceExpression(true).build());
        SerializableParseCache traced = traceProducer.parseToSerializableCache("a && (!myTest(11) || false)");
        assertNotNull(traced.getTracePoints());
        assertFalse(traced.getTracePoints().isEmpty());
        SerializableParseCache parsedTraced = JSON.parseObject(JSON.toJSONString(traced), SerializableParseCache.class);
        assertNotNull(parsedTraced.getTracePoints());
        assertFalse(parsedTraced.getTracePoints().isEmpty());
        
        Express4Runner traceConsumer = new Express4Runner(InitOptions.builder().traceExpression(true).build());
        traceConsumer.addFunction("myTest", (Predicate<Integer>)i -> i > 10);
        Map<String, Object> context = new HashMap<>();
        context.put("a", true);
        QLResult tracedResult =
            traceConsumer.execute(parsedTraced, context, QLOptions.builder().traceExpression(true).build());
        assertEquals(false, tracedResult.getResult());
        assertEquals(1, tracedResult.getExpressionTraces().size());
        assertEquals(
            "OPERATOR && false\n" + "  | VARIABLE a true\n" + "  | OPERATOR || false\n" + "      | OPERATOR ! false\n"
                + "          | FUNCTION myTest true\n" + "              | VALUE 11 11\n"
                + "      | VALUE false false\n",
            tracedResult.getExpressionTraces().get(0).toPrettyString(0));
        
        Express4Runner plainProducer = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        SerializableParseCache withoutTrace =
            JSON.parseObject(JSON.toJSONString(plainProducer.parseToSerializableCache("a && true")),
                SerializableParseCache.class);
        assertNull(withoutTrace.getTracePoints());
        QLResult plainResult =
            traceConsumer.execute(withoutTrace, context, QLOptions.builder().traceExpression(true).build());
        assertEquals(true, plainResult.getResult());
        assertNull(plainResult.getExpressionTraces());
    }
    
    @Test
    public void invalidModelsFailWithClearErrorCodes() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        SerializableParseCache unsupportedVersion = runner.parseToSerializableCache("1 + 2");
        unsupportedVersion.setModelVersion(2);
        assertParseCacheError(runner,
            unsupportedVersion,
            QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_VERSION.name());
        
        SerializableParseCache unknownOpcode = runner.parseToSerializableCache("1 + 2");
        findInstruction(unknownOpcode, "BINARY_OP").setOpcode("UNKNOWN");
        assertParseCacheError(runner,
            unknownOpcode,
            QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_INSTRUCTION.name());
        
        SerializableParseCache missingOperand = runner.parseToSerializableCache("a + 2");
        findInstruction(missingOperand, "LOAD").setOpcode("LOAD");
        findInstruction(missingOperand, "LOAD").getOperands().remove("name");
        assertParseCacheError(runner, missingOperand, QLErrorCodes.SERIALIZABLE_PARSE_CACHE_INVALID_MODEL.name());
        
        SerializableParseCache invalidConstant = runner.parseToSerializableCache("1 + 2");
        SerializableConstant constant =
            (SerializableConstant)findInstruction(invalidConstant, "CONST").getOperands().get("constant");
        constant.setType("OBJECT");
        assertParseCacheError(runner,
            invalidConstant,
            QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_CONSTANT.name());
        
        SerializableParseCache classNotFound = runner.parseToSerializableCache("new ArrayList()");
        findInstruction(classNotFound, "NEW_INSTANCE").getOperands().put("className", "no.such.Type");
        assertParseCacheError(runner, classNotFound, QLErrorCodes.SERIALIZABLE_PARSE_CACHE_CLASS_NOT_FOUND.name());
        
        SerializableParseCache operatorNotFound = runner.parseToSerializableCache("1 + 2");
        findInstruction(operatorNotFound, "BINARY_OP").getOperands().put("operator", "missing");
        assertParseCacheError(runner,
            operatorNotFound,
            QLErrorCodes.SERIALIZABLE_PARSE_CACHE_OPERATOR_NOT_FOUND.name());
    }
    
    @Test
    public void callConstInstructionIsRejectedOnExport() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        CompileTimeFunction compileTimeFunction = (functionName, arguments, operatorFactory,
            codeGenerator) -> codeGenerator.addInstruction(new CallConstInstruction(codeGenerator.getErrorReporter(),
                params -> new QResult(new DataValue(1), QResult.ResultType.RETURN), 0, functionName));
        runner.addCompileTimeFunction("CONST_CALL", compileTimeFunction);
        
        try {
            runner.parseToSerializableCache("CONST_CALL()");
            fail("CallConstInstruction export should fail");
        }
        catch (SerializableParseCacheException e) {
            assertEquals(QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_INSTRUCTION.name(), e.getErrorCode());
        }
    }
    
    private static SerializableInstruction findInstruction(SerializableParseCache cache, String opcode) {
        for (SerializableInstruction instruction : cache.getMain().getInstructions()) {
            if (opcode.equals(instruction.getOpcode())) {
                return instruction;
            }
        }
        fail("instruction not found: " + opcode);
        return null;
    }
    
    private static void assertParseCacheError(Express4Runner runner, SerializableParseCache cache, String errorCode) {
        try {
            runner.loadSerializableCache(cache);
            fail("expected parse cache import error");
        }
        catch (SerializableParseCacheException e) {
            assertEquals(errorCode, e.getErrorCode());
            assertNotNull(e.getDiagnostic());
        }
    }
}
