package com.alibaba.qlexpress4.api.parsecache;

import com.alibaba.qlexpress4.aparser.QCompileCache;
import com.alibaba.qlexpress4.exception.DefaultErrReporter;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.QLambdaDefinition;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionEmpty;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.instruction.BreakContinueInstruction;
import com.alibaba.qlexpress4.runtime.instruction.CallConstInstruction;
import com.alibaba.qlexpress4.runtime.instruction.CallFunctionInstruction;
import com.alibaba.qlexpress4.runtime.instruction.CallInstruction;
import com.alibaba.qlexpress4.runtime.instruction.CastInstruction;
import com.alibaba.qlexpress4.runtime.instruction.CheckTimeOutInstruction;
import com.alibaba.qlexpress4.runtime.instruction.CloseScopeInstruction;
import com.alibaba.qlexpress4.runtime.instruction.ConstInstruction;
import com.alibaba.qlexpress4.runtime.instruction.DefineFunctionInstruction;
import com.alibaba.qlexpress4.runtime.instruction.DefineLocalInstruction;
import com.alibaba.qlexpress4.runtime.instruction.ForEachInstruction;
import com.alibaba.qlexpress4.runtime.instruction.ForInstruction;
import com.alibaba.qlexpress4.runtime.instruction.GetFieldInstruction;
import com.alibaba.qlexpress4.runtime.instruction.GetMethodInstruction;
import com.alibaba.qlexpress4.runtime.instruction.IndexInstruction;
import com.alibaba.qlexpress4.runtime.instruction.JumpIfInstruction;
import com.alibaba.qlexpress4.runtime.instruction.JumpIfPopInstruction;
import com.alibaba.qlexpress4.runtime.instruction.JumpInstruction;
import com.alibaba.qlexpress4.runtime.instruction.LoadInstruction;
import com.alibaba.qlexpress4.runtime.instruction.LoadLambdaInstruction;
import com.alibaba.qlexpress4.runtime.instruction.MethodInvokeInstruction;
import com.alibaba.qlexpress4.runtime.instruction.MultiNewArrayInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewArrayInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewFilledInstanceInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewInstanceInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewListInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewMapInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewScopeInstruction;
import com.alibaba.qlexpress4.runtime.instruction.OperatorInstruction;
import com.alibaba.qlexpress4.runtime.instruction.PopInstruction;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;
import com.alibaba.qlexpress4.runtime.instruction.ReturnInstruction;
import com.alibaba.qlexpress4.runtime.instruction.SliceInstruction;
import com.alibaba.qlexpress4.runtime.instruction.SpreadGetFieldInstruction;
import com.alibaba.qlexpress4.runtime.instruction.SpreadMethodInvokeInstruction;
import com.alibaba.qlexpress4.runtime.instruction.StringJoinInstruction;
import com.alibaba.qlexpress4.runtime.instruction.ThrowInstruction;
import com.alibaba.qlexpress4.runtime.instruction.TraceEvaluatedInstruction;
import com.alibaba.qlexpress4.runtime.instruction.TracePeekInstruction;
import com.alibaba.qlexpress4.runtime.instruction.TryCatchInstruction;
import com.alibaba.qlexpress4.runtime.instruction.UnaryInstruction;
import com.alibaba.qlexpress4.runtime.instruction.WhileInstruction;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;
import com.alibaba.qlexpress4.runtime.trace.TracePointTree;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SerializableParseCacheExporter {
    static final int MODEL_VERSION = 1;
    
    private final String script;
    
    private final OperatorManager operatorManager;
    
    private final boolean includeTracePoints;
    
    public SerializableParseCacheExporter(String script, OperatorManager operatorManager, boolean includeTracePoints) {
        this.script = script;
        this.operatorManager = operatorManager;
        this.includeTracePoints = includeTracePoints;
    }
    
    public SerializableParseCache export(QCompileCache compileCache) {
        SerializableParseCache result = new SerializableParseCache();
        result.setModelVersion(MODEL_VERSION);
        result.setProducerVersion(SerializableParseCacheExporter.class.getPackage().getImplementationVersion());
        result.setScript(script);
        result.setScriptHash(sha256(script));
        result.setMain(exportLambdaDefinition(compileCache.getQLambdaDefinition(), null));
        if (includeTracePoints) {
            result.setTracePoints(exportTracePoints(compileCache.getExpressionTracePoints()));
        }
        return result;
    }
    
    private SerializableLambdaDefinition exportLambdaDefinition(QLambdaDefinition definition, QLInstruction owner) {
        SerializableLambdaDefinition result = new SerializableLambdaDefinition();
        if (definition instanceof QLambdaDefinitionInner) {
            QLambdaDefinitionInner inner = (QLambdaDefinitionInner)definition;
            result.setName(inner.getName());
            result.setMaxStackSize(inner.getMaxStackSize());
            result.setParams(exportParams(inner.getParamsType()));
            List<SerializableInstruction> instructions = new ArrayList<>(inner.getInstructions().length);
            for (QLInstruction instruction : inner.getInstructions()) {
                instructions.add(exportInstruction(instruction));
            }
            result.setInstructions(instructions);
            return result;
        }
        if (definition == QLambdaDefinitionEmpty.INSTANCE || "EmptyLambdaDefinition".equals(definition.getName())) {
            result.setName(definition.getName());
            result.setMaxStackSize(0);
            result.setParams(Collections.emptyList());
            result.setInstructions(Collections.emptyList());
            return result;
        }
        throw unsupportedInstruction(owner, "lambda definition " + definition.getClass().getName());
    }
    
    private List<SerializableParam> exportParams(List<QLambdaDefinitionInner.Param> params) {
        List<SerializableParam> result = new ArrayList<>(params.size());
        for (QLambdaDefinitionInner.Param param : params) {
            SerializableParam dto = new SerializableParam();
            dto.setName(param.getName());
            dto.setClassName(className(param.getClazz()));
            result.add(dto);
        }
        return result;
    }
    
    private SerializableInstruction exportInstruction(QLInstruction instruction) {
        if (instruction instanceof CallConstInstruction) {
            throw unsupportedInstruction(instruction, instruction.getClass().getSimpleName());
        }
        Map<String, Object> operands = new LinkedHashMap<>();
        String opcode;
        if (instruction instanceof ConstInstruction) {
            ConstInstruction constInstruction = (ConstInstruction)instruction;
            opcode = "CONST";
            operands.put("constant", exportConstant(constInstruction.getConstObj(), instruction));
            putOptional(operands, "traceKey", constInstruction.getTraceKey());
        }
        else if (instruction instanceof LoadInstruction) {
            LoadInstruction loadInstruction = (LoadInstruction)instruction;
            opcode = "LOAD";
            operands.put("name", loadInstruction.getName());
            putOptional(operands, "traceKey", loadInstruction.getTraceKey());
        }
        else if (instruction instanceof PopInstruction) {
            opcode = "POP";
        }
        else if (instruction instanceof ReturnInstruction) {
            ReturnInstruction returnInstruction = (ReturnInstruction)instruction;
            opcode = "RETURN";
            operands.put("resultType", returnInstruction.getResultType().name());
            putOptional(operands, "traceKey", returnInstruction.getTraceKey());
        }
        else if (instruction instanceof BreakContinueInstruction) {
            opcode = "BREAK_CONTINUE";
            QResult.ResultType resultType = ((BreakContinueInstruction)instruction).getResult().getResultType();
            if (resultType != QResult.ResultType.BREAK && resultType != QResult.ResultType.CONTINUE) {
                throw unsupportedInstruction(instruction, "break/continue result type " + resultType);
            }
            operands.put("resultType", resultType.name());
        }
        else if (instruction instanceof ThrowInstruction) {
            opcode = "THROW";
        }
        else if (instruction instanceof CheckTimeOutInstruction) {
            opcode = "CHECK_TIMEOUT";
        }
        else if (instruction instanceof JumpInstruction) {
            opcode = "JUMP";
            operands.put("position", ((JumpInstruction)instruction).getPosition());
        }
        else if (instruction instanceof JumpIfInstruction) {
            JumpIfInstruction jumpIfInstruction = (JumpIfInstruction)instruction;
            opcode = "JUMP_IF";
            operands.put("expect", jumpIfInstruction.isExpect());
            operands.put("position", jumpIfInstruction.getPosition());
            putOptional(operands, "traceKey", jumpIfInstruction.getTraceKey());
        }
        else if (instruction instanceof JumpIfPopInstruction) {
            JumpIfPopInstruction jumpIfPopInstruction = (JumpIfPopInstruction)instruction;
            opcode = "JUMP_IF_POP";
            operands.put("expect", jumpIfPopInstruction.isExpect());
            operands.put("position", jumpIfPopInstruction.getPosition());
        }
        else if (instruction instanceof OperatorInstruction) {
            OperatorInstruction operatorInstruction = (OperatorInstruction)instruction;
            opcode = "BINARY_OP";
            operands.put("operator", operatorInstruction.getOperator().getOperator());
            putOptional(operands, "traceKey", operatorInstruction.getTraceKey());
        }
        else if (instruction instanceof UnaryInstruction) {
            UnaryInstruction unaryInstruction = (UnaryInstruction)instruction;
            UnaryOperator unaryOperator = unaryInstruction.getUnaryOperator();
            opcode = unaryOpcode(unaryOperator, instruction);
            operands.put("operator", unaryOperator.getOperator());
            putOptional(operands, "traceKey", unaryInstruction.getTraceKey());
        }
        else if (instruction instanceof CallFunctionInstruction) {
            CallFunctionInstruction callFunctionInstruction = (CallFunctionInstruction)instruction;
            opcode = "CALL_FUNCTION";
            operands.put("functionName", callFunctionInstruction.getFunctionName());
            operands.put("argNum", callFunctionInstruction.getArgNum());
            putOptional(operands, "traceKey", callFunctionInstruction.getTraceKey());
        }
        else if (instruction instanceof CallInstruction) {
            opcode = "CALL";
            operands.put("argNum", ((CallInstruction)instruction).getArgNum());
        }
        else if (instruction instanceof LoadLambdaInstruction) {
            opcode = "LOAD_LAMBDA";
            operands.put("lambda",
                exportLambdaDefinition(((LoadLambdaInstruction)instruction).getLambdaDefinition(), instruction));
        }
        else if (instruction instanceof DefineFunctionInstruction) {
            DefineFunctionInstruction defineFunctionInstruction = (DefineFunctionInstruction)instruction;
            opcode = "DEFINE_FUNCTION";
            operands.put("name", defineFunctionInstruction.getName());
            operands.put("lambda",
                exportLambdaDefinition(defineFunctionInstruction.getLambdaDefinition(), instruction));
        }
        else if (instruction instanceof NewScopeInstruction) {
            opcode = "NEW_SCOPE";
            operands.put("scopeName", ((NewScopeInstruction)instruction).getScopeName());
        }
        else if (instruction instanceof CloseScopeInstruction) {
            opcode = "CLOSE_SCOPE";
            operands.put("scopeName", ((CloseScopeInstruction)instruction).getScopeName());
        }
        else if (instruction instanceof DefineLocalInstruction) {
            DefineLocalInstruction defineLocalInstruction = (DefineLocalInstruction)instruction;
            opcode = "DEFINE_LOCAL";
            operands.put("variableName", defineLocalInstruction.getVariableName());
            operands.put("className", className(defineLocalInstruction.getDefineClz()));
        }
        else if (instruction instanceof NewInstanceInstruction) {
            NewInstanceInstruction newInstanceInstruction = (NewInstanceInstruction)instruction;
            opcode = "NEW_INSTANCE";
            operands.put("className", className(newInstanceInstruction.getNewClz()));
            operands.put("argNum", newInstanceInstruction.getArgNum());
        }
        else if (instruction instanceof NewFilledInstanceInstruction) {
            NewFilledInstanceInstruction newFilledInstanceInstruction = (NewFilledInstanceInstruction)instruction;
            opcode = "NEW_FILLED_INSTANCE";
            operands.put("className", className(newFilledInstanceInstruction.getNewCls()));
            operands.put("keys", new ArrayList<>(newFilledInstanceInstruction.getKeys()));
        }
        else if (instruction instanceof NewArrayInstruction) {
            NewArrayInstruction newArrayInstruction = (NewArrayInstruction)instruction;
            opcode = "NEW_ARRAY";
            operands.put("componentClassName", className(newArrayInstruction.getClz()));
            operands.put("length", newArrayInstruction.getLength());
        }
        else if (instruction instanceof MultiNewArrayInstruction) {
            MultiNewArrayInstruction multiNewArrayInstruction = (MultiNewArrayInstruction)instruction;
            opcode = "MULTI_NEW_ARRAY";
            operands.put("componentClassName", className(multiNewArrayInstruction.getClz()));
            operands.put("dims", multiNewArrayInstruction.getDims());
        }
        else if (instruction instanceof NewListInstruction) {
            opcode = "NEW_LIST";
            operands.put("initLength", ((NewListInstruction)instruction).getInitLength());
        }
        else if (instruction instanceof NewMapInstruction) {
            opcode = "NEW_MAP";
            operands.put("keys", new ArrayList<>(((NewMapInstruction)instruction).getKeys()));
        }
        else if (instruction instanceof GetFieldInstruction) {
            GetFieldInstruction getFieldInstruction = (GetFieldInstruction)instruction;
            opcode = "GET_FIELD";
            operands.put("fieldName", getFieldInstruction.getFieldName());
            operands.put("optional", getFieldInstruction.isOptional());
        }
        else if (instruction instanceof SpreadGetFieldInstruction) {
            opcode = "SPREAD_GET_FIELD";
            operands.put("fieldName", ((SpreadGetFieldInstruction)instruction).getFieldName());
        }
        else if (instruction instanceof MethodInvokeInstruction) {
            MethodInvokeInstruction methodInvokeInstruction = (MethodInvokeInstruction)instruction;
            opcode = "METHOD_INVOKE";
            operands.put("methodName", methodInvokeInstruction.getMethodName());
            operands.put("argNum", methodInvokeInstruction.getArgNum());
            operands.put("optional", methodInvokeInstruction.isOptional());
        }
        else if (instruction instanceof SpreadMethodInvokeInstruction) {
            SpreadMethodInvokeInstruction spreadMethodInvokeInstruction = (SpreadMethodInvokeInstruction)instruction;
            opcode = "SPREAD_METHOD_INVOKE";
            operands.put("methodName", spreadMethodInvokeInstruction.getMethodName());
            operands.put("argNum", spreadMethodInvokeInstruction.getArgNum());
        }
        else if (instruction instanceof GetMethodInstruction) {
            opcode = "GET_METHOD";
            operands.put("methodName", ((GetMethodInstruction)instruction).getMethodName());
        }
        else if (instruction instanceof IndexInstruction) {
            opcode = "INDEX";
        }
        else if (instruction instanceof SliceInstruction) {
            opcode = "SLICE";
            operands.put("mode", ((SliceInstruction)instruction).getMode().name());
        }
        else if (instruction instanceof CastInstruction) {
            opcode = "CAST";
        }
        else if (instruction instanceof WhileInstruction) {
            WhileInstruction whileInstruction = (WhileInstruction)instruction;
            opcode = "WHILE";
            operands.put("condition", exportLambdaDefinition(whileInstruction.getCondition(), instruction));
            operands.put("body", exportLambdaDefinition(whileInstruction.getBody(), instruction));
            operands.put("whileScopeMaxStackSize", whileInstruction.getWhileScopeMaxStackSize());
        }
        else if (instruction instanceof ForInstruction) {
            ForInstruction forInstruction = (ForInstruction)instruction;
            opcode = "FOR";
            putOptional(operands, "forInit", exportNullableLambdaDefinition(forInstruction.getForInit(), instruction));
            putOptional(operands,
                "condition",
                exportNullableLambdaDefinition(forInstruction.getCondition(), instruction));
            putOptional(operands, "conditionSource", sourceOf(forInstruction.getConditionErrorReporter()));
            putOptional(operands,
                "forUpdate",
                exportNullableLambdaDefinition(forInstruction.getForUpdate(), instruction));
            operands.put("forScopeMaxStackSize", forInstruction.getForScopeMaxStackSize());
            operands.put("forBody", exportLambdaDefinition(forInstruction.getForBody(), instruction));
        }
        else if (instruction instanceof ForEachInstruction) {
            ForEachInstruction forEachInstruction = (ForEachInstruction)instruction;
            opcode = "FOR_EACH";
            operands.put("body", exportLambdaDefinition(forEachInstruction.getBody(), instruction));
            operands.put("itemClassName", className(forEachInstruction.getItCls()));
            operands.put("targetSource", sourceOf(forEachInstruction.getTargetErrorReporter()));
        }
        else if (instruction instanceof TryCatchInstruction) {
            TryCatchInstruction tryCatchInstruction = (TryCatchInstruction)instruction;
            opcode = "TRY_CATCH";
            operands.put("body", exportLambdaDefinition(tryCatchInstruction.getBody(), instruction));
            operands.put("exceptionTable", exportExceptionTable(tryCatchInstruction, instruction));
            putOptional(operands,
                "finalBody",
                exportNullableLambdaDefinition(tryCatchInstruction.getFinalBody(), instruction));
        }
        else if (instruction instanceof TracePeekInstruction) {
            opcode = "TRACE_PEEK";
            putOptional(operands, "traceKey", ((TracePeekInstruction)instruction).getTraceKey());
        }
        else if (instruction instanceof TraceEvaluatedInstruction) {
            opcode = "TRACE_EVALUATED";
            putOptional(operands, "traceKey", ((TraceEvaluatedInstruction)instruction).getTraceKey());
        }
        else if (instruction instanceof StringJoinInstruction) {
            opcode = "STRING_JOIN";
            operands.put("n", ((StringJoinInstruction)instruction).getN());
        }
        else {
            throw unsupportedInstruction(instruction, instruction.getClass().getName());
        }
        
        SerializableInstruction result = new SerializableInstruction();
        result.setOpcode(opcode);
        result.setSource(sourceOf(instruction.getErrorReporter()));
        result.setOperands(operands);
        return result;
    }
    
    private SerializableLambdaDefinition exportNullableLambdaDefinition(QLambdaDefinition definition,
        QLInstruction owner) {
        return definition == null ? null : exportLambdaDefinition(definition, owner);
    }
    
    private List<SerializableCatchEntry> exportExceptionTable(TryCatchInstruction instruction, QLInstruction owner) {
        List<SerializableCatchEntry> result = new ArrayList<>(instruction.getExceptionTable().size());
        for (Map.Entry<Class<?>, QLambdaDefinition> entry : instruction.getExceptionTable()) {
            SerializableCatchEntry catchEntry = new SerializableCatchEntry();
            catchEntry.setExceptionClassName(className(entry.getKey()));
            catchEntry.setHandler(exportLambdaDefinition(entry.getValue(), owner));
            result.add(catchEntry);
        }
        return result;
    }
    
    private SerializableConstant exportConstant(Object value, QLInstruction owner) {
        SerializableConstant constant = new SerializableConstant();
        if (value == null) {
            constant.setType("NULL");
            return constant;
        }
        if (value instanceof Boolean) {
            constant.setType("BOOLEAN");
            constant.setValue(value);
            return constant;
        }
        if (value instanceof String) {
            constant.setType("STRING");
            constant.setValue(value);
            return constant;
        }
        if (value instanceof Character) {
            constant.setType("CHAR");
            constant.setValue(String.valueOf(value));
            return constant;
        }
        if (value instanceof Integer) {
            constant.setType("INT");
            constant.setValue(value);
            return constant;
        }
        if (value instanceof Long) {
            constant.setType("LONG");
            constant.setValue(value);
            return constant;
        }
        if (value instanceof BigInteger) {
            constant.setType("BIG_INTEGER");
            constant.setValue(value.toString());
            return constant;
        }
        if (value instanceof Float) {
            constant.setType("FLOAT");
            constant.setValue(value);
            return constant;
        }
        if (value instanceof Double) {
            constant.setType("DOUBLE");
            constant.setValue(value);
            return constant;
        }
        if (value instanceof BigDecimal) {
            constant.setType("BIG_DECIMAL");
            constant.setValue(value.toString());
            return constant;
        }
        if (value instanceof MetaClass) {
            constant.setType("META_CLASS");
            constant.setValue(className(((MetaClass)value).getClz()));
            return constant;
        }
        if (value instanceof Class) {
            constant.setType("META_CLASS");
            constant.setValue(className((Class<?>)value));
            return constant;
        }
        throw new SerializableParseCacheException(script, sourceOf(owner == null ? null : owner.getErrorReporter()),
            QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_CONSTANT.name(),
            String.format(QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_CONSTANT.getErrorMsg(),
                value.getClass().getName()));
    }
    
    private List<SerializableTracePoint> exportTracePoints(List<TracePointTree> tracePoints) {
        if (tracePoints == null) {
            return Collections.emptyList();
        }
        List<SerializableTracePoint> result = new ArrayList<>(tracePoints.size());
        for (TracePointTree tracePoint : tracePoints) {
            result.add(exportTracePoint(tracePoint));
        }
        return result;
    }
    
    private SerializableTracePoint exportTracePoint(TracePointTree tracePoint) {
        SerializableTracePoint result = new SerializableTracePoint();
        result.setType(tracePoint.getType().name());
        result.setToken(tracePoint.getToken());
        result.setLine(tracePoint.getLine());
        result.setCol(tracePoint.getCol());
        result.setPosition(tracePoint.getPosition());
        result.setChildren(exportTracePoints(tracePoint.getChildren()));
        return result;
    }
    
    private String unaryOpcode(UnaryOperator unaryOperator, QLInstruction instruction) {
        String operator = unaryOperator.getOperator();
        if (operatorManager.getPrefixUnaryOperator(operator) == unaryOperator) {
            return "PREFIX_UNARY_OP";
        }
        if (operatorManager.getSuffixUnaryOperator(operator) == unaryOperator) {
            return "SUFFIX_UNARY_OP";
        }
        throw unsupportedInstruction(instruction, "unary operator " + operator);
    }
    
    private SerializableSource sourceOf(ErrorReporter errorReporter) {
        SerializableSource source = new SerializableSource();
        if (errorReporter instanceof DefaultErrReporter) {
            DefaultErrReporter defaultErrReporter = (DefaultErrReporter)errorReporter;
            source.setStart(defaultErrReporter.getTokenStartPos());
            source.setLine(defaultErrReporter.getLine());
            source.setCol(Math.max(0, defaultErrReporter.getCol() - 1));
            source.setLexeme(defaultErrReporter.getLexeme());
            return source;
        }
        source.setStart(0);
        source.setLine(1);
        source.setCol(0);
        source.setLexeme("");
        return source;
    }
    
    private SerializableParseCacheException unsupportedInstruction(QLInstruction instruction, String instructionName) {
        return new SerializableParseCacheException(script,
            sourceOf(instruction == null ? null : instruction.getErrorReporter()),
            QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_INSTRUCTION.name(), String
                .format(QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_INSTRUCTION.getErrorMsg(), instructionName));
    }
    
    private static void putOptional(Map<String, Object> operands, String key, Object value) {
        if (value != null) {
            operands.put(key, value);
        }
    }
    
    private static String className(Class<?> clazz) {
        return clazz.getName();
    }
    
    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes =
                digest.digest((value == null ? "" : value).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                builder.append(String.format("%02x", b & 0xff));
            }
            return builder.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
