package com.alibaba.qlexpress4.api.parsecache;

import com.alibaba.qlexpress4.ClassSupplier;
import com.alibaba.qlexpress4.aparser.QCompileCache;
import com.alibaba.qlexpress4.exception.DefaultErrReporter;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.QLErrorCodes;
import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.QLambdaDefinition;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.instruction.BreakContinueInstruction;
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
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;
import com.alibaba.qlexpress4.runtime.trace.TracePointTree;
import com.alibaba.qlexpress4.runtime.trace.TraceType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SerializableParseCacheImporter {
    private final OperatorManager operatorManager;
    
    private final ClassSupplier classSupplier;
    
    private String script;
    
    public SerializableParseCacheImporter(OperatorManager operatorManager, ClassSupplier classSupplier) {
        this.operatorManager = operatorManager;
        this.classSupplier = classSupplier;
    }
    
    public LoadedParseCache load(SerializableParseCache cache, Object runnerIdentity) {
        if (cache == null) {
            throw modelError(null, QLErrorCodes.SERIALIZABLE_PARSE_CACHE_INVALID_MODEL, "cache must not be null");
        }
        script = cache.getScript();
        if (cache.getModelVersion() != SerializableParseCacheExporter.MODEL_VERSION) {
            throw modelError(null,
                QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_VERSION,
                String.format(QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_VERSION.getErrorMsg(),
                    cache.getModelVersion()));
        }
        if (cache.getScript() == null) {
            throw modelError(null, QLErrorCodes.SERIALIZABLE_PARSE_CACHE_INVALID_MODEL, "script is required");
        }
        if (cache.getMain() == null) {
            throw modelError(null, QLErrorCodes.SERIALIZABLE_PARSE_CACHE_INVALID_MODEL, "main lambda is required");
        }
        QLambdaDefinitionInner main = importLambdaDefinition(cache.getMain(), null);
        List<TracePointTree> tracePoints =
            cache.getTracePoints() == null ? Collections.emptyList() : importTracePoints(cache.getTracePoints(), null);
        return new LoadedParseCache(new QCompileCache(main, tracePoints), cache, runnerIdentity);
    }
    
    private QLambdaDefinitionInner importLambdaDefinition(Object raw, SerializableInstruction owner) {
        SerializableLambdaDefinition definition = toLambdaDefinition(raw, owner);
        if (definition == null) {
            throw invalid(owner, "lambda definition is required");
        }
        if (definition.getName() == null) {
            throw invalid(owner, "lambda name is required");
        }
        if (definition.getInstructions() == null) {
            throw invalid(owner, "lambda instructions are required");
        }
        if (definition.getParams() == null) {
            throw invalid(owner, "lambda params are required");
        }
        if (definition.getMaxStackSize() < 0) {
            throw invalid(owner, "lambda maxStackSize must not be negative");
        }
        
        List<QLambdaDefinitionInner.Param> params = new ArrayList<>(definition.getParams().size());
        for (Object param : definition.getParams()) {
            params.add(importParam(param, owner));
        }
        
        List<QLInstruction> instructions = new ArrayList<>(definition.getInstructions().size());
        for (Object instruction : definition.getInstructions()) {
            instructions.add(importInstruction(instruction, owner));
        }
        return new QLambdaDefinitionInner(definition.getName(), instructions, params, definition.getMaxStackSize());
    }
    
    private QLambdaDefinitionInner.Param importParam(Object raw, SerializableInstruction owner) {
        SerializableParam param = toParam(raw, owner);
        if (param == null) {
            throw invalid(owner, "lambda param must not be null");
        }
        if (param.getName() == null) {
            throw invalid(owner, "lambda param name is required");
        }
        if (param.getClassName() == null) {
            throw invalid(owner, "lambda param className is required");
        }
        return new QLambdaDefinitionInner.Param(param.getName(), loadClass(param.getClassName(), owner));
    }
    
    private QLInstruction importInstruction(Object raw, SerializableInstruction parent) {
        SerializableInstruction instruction = toInstruction(raw, parent);
        if (instruction == null) {
            throw invalid(parent, "instruction must not be null");
        }
        String opcode = instruction.getOpcode();
        if (opcode == null) {
            throw invalid(instruction, "opcode is required");
        }
        Map<String, Object> operands = instruction.getOperands();
        if (operands == null) {
            throw invalid(instruction, "operands are required");
        }
        ErrorReporter reporter = reporter(instruction.getSource());
        switch (opcode) {
            case "CONST":
                return new ConstInstruction(reporter,
                    importConstant(required(operands, "constant", instruction), instruction),
                    optionalInt(operands, "traceKey", instruction));
            case "LOAD":
                return new LoadInstruction(reporter, requiredString(operands, "name", instruction),
                    optionalInt(operands, "traceKey", instruction));
            case "POP":
                return new PopInstruction(reporter);
            case "RETURN":
                return new ReturnInstruction(reporter,
                    resultType(requiredString(operands, "resultType", instruction), instruction),
                    optionalInt(operands, "traceKey", instruction));
            case "BREAK_CONTINUE":
                return new BreakContinueInstruction(reporter,
                    breakContinueResult(requiredString(operands, "resultType", instruction), instruction));
            case "THROW":
                return new ThrowInstruction(reporter);
            case "CHECK_TIMEOUT":
                return new CheckTimeOutInstruction(reporter);
            case "JUMP":
                return new JumpInstruction(reporter, requiredInt(operands, "position", instruction));
            case "JUMP_IF":
                return new JumpIfInstruction(reporter, requiredBoolean(operands, "expect", instruction),
                    requiredInt(operands, "position", instruction), optionalInt(operands, "traceKey", instruction));
            case "JUMP_IF_POP":
                return new JumpIfPopInstruction(reporter, requiredBoolean(operands, "expect", instruction),
                    requiredInt(operands, "position", instruction));
            case "BINARY_OP":
                return new OperatorInstruction(reporter,
                    binaryOperator(requiredString(operands, "operator", instruction), instruction),
                    optionalInt(operands, "traceKey", instruction));
            case "PREFIX_UNARY_OP":
                return new UnaryInstruction(reporter,
                    prefixUnaryOperator(requiredString(operands, "operator", instruction), instruction),
                    optionalInt(operands, "traceKey", instruction));
            case "SUFFIX_UNARY_OP":
                return new UnaryInstruction(reporter,
                    suffixUnaryOperator(requiredString(operands, "operator", instruction), instruction),
                    optionalInt(operands, "traceKey", instruction));
            case "CALL_FUNCTION":
                return new CallFunctionInstruction(reporter, requiredString(operands, "functionName", instruction),
                    requiredInt(operands, "argNum", instruction), optionalInt(operands, "traceKey", instruction));
            case "CALL":
                return new CallInstruction(reporter, requiredInt(operands, "argNum", instruction));
            case "LOAD_LAMBDA":
                return new LoadLambdaInstruction(reporter,
                    importLambdaDefinition(required(operands, "lambda", instruction), instruction));
            case "DEFINE_FUNCTION":
                return new DefineFunctionInstruction(reporter, requiredString(operands, "name", instruction),
                    importLambdaDefinition(required(operands, "lambda", instruction), instruction));
            case "NEW_SCOPE":
                return new NewScopeInstruction(reporter, requiredString(operands, "scopeName", instruction));
            case "CLOSE_SCOPE":
                return new CloseScopeInstruction(reporter, requiredString(operands, "scopeName", instruction));
            case "DEFINE_LOCAL":
                return new DefineLocalInstruction(reporter, requiredString(operands, "variableName", instruction),
                    loadClass(requiredString(operands, "className", instruction), instruction));
            case "NEW_INSTANCE":
                return new NewInstanceInstruction(reporter,
                    loadClass(requiredString(operands, "className", instruction), instruction),
                    requiredInt(operands, "argNum", instruction));
            case "NEW_FILLED_INSTANCE":
                return new NewFilledInstanceInstruction(reporter,
                    loadClass(requiredString(operands, "className", instruction), instruction),
                    requiredStringList(operands, "keys", instruction));
            case "NEW_ARRAY":
                return new NewArrayInstruction(reporter,
                    loadClass(requiredString(operands, "componentClassName", instruction), instruction),
                    requiredInt(operands, "length", instruction));
            case "MULTI_NEW_ARRAY":
                return new MultiNewArrayInstruction(reporter,
                    loadClass(requiredString(operands, "componentClassName", instruction), instruction),
                    requiredInt(operands, "dims", instruction));
            case "NEW_LIST":
                return new NewListInstruction(reporter, requiredInt(operands, "initLength", instruction));
            case "NEW_MAP":
                return new NewMapInstruction(reporter, requiredStringList(operands, "keys", instruction));
            case "GET_FIELD":
                return new GetFieldInstruction(reporter, requiredString(operands, "fieldName", instruction),
                    requiredBoolean(operands, "optional", instruction));
            case "SPREAD_GET_FIELD":
                return new SpreadGetFieldInstruction(reporter, requiredString(operands, "fieldName", instruction));
            case "METHOD_INVOKE":
                return new MethodInvokeInstruction(reporter, requiredString(operands, "methodName", instruction),
                    requiredInt(operands, "argNum", instruction), requiredBoolean(operands, "optional", instruction));
            case "SPREAD_METHOD_INVOKE":
                return new SpreadMethodInvokeInstruction(reporter, requiredString(operands, "methodName", instruction),
                    requiredInt(operands, "argNum", instruction));
            case "GET_METHOD":
                return new GetMethodInstruction(reporter, requiredString(operands, "methodName", instruction));
            case "INDEX":
                return new IndexInstruction(reporter);
            case "SLICE":
                return new SliceInstruction(reporter,
                    sliceMode(requiredString(operands, "mode", instruction), instruction));
            case "CAST":
                return new CastInstruction(reporter);
            case "WHILE":
                return new WhileInstruction(reporter,
                    importLambdaDefinition(required(operands, "condition", instruction), instruction),
                    importLambdaDefinition(required(operands, "body", instruction), instruction),
                    requiredInt(operands, "whileScopeMaxStackSize", instruction));
            case "FOR":
                return importForInstruction(reporter, operands, instruction);
            case "FOR_EACH":
                return new ForEachInstruction(reporter,
                    importLambdaDefinition(required(operands, "body", instruction), instruction),
                    loadClass(requiredString(operands, "itemClassName", instruction), instruction),
                    reporter(requiredSource(operands, "targetSource", instruction)));
            case "TRY_CATCH":
                return new TryCatchInstruction(reporter,
                    importLambdaDefinition(required(operands, "body", instruction), instruction),
                    importExceptionTable(requiredList(operands, "exceptionTable", instruction), instruction),
                    optionalLambda(operands, "finalBody", instruction));
            case "TRACE_PEEK":
                return new TracePeekInstruction(reporter, optionalInt(operands, "traceKey", instruction));
            case "TRACE_EVALUATED":
                return new TraceEvaluatedInstruction(reporter, optionalInt(operands, "traceKey", instruction));
            case "STRING_JOIN":
                return new StringJoinInstruction(reporter, requiredInt(operands, "n", instruction));
            default:
                throw new SerializableParseCacheException(script, instruction.getSource(),
                    QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_INSTRUCTION.name(),
                    String.format(QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_INSTRUCTION.getErrorMsg(), opcode));
        }
    }
    
    private QLInstruction importForInstruction(ErrorReporter reporter, Map<String, Object> operands,
        SerializableInstruction instruction) {
        QLambdaDefinition forInit = optionalLambda(operands, "forInit", instruction);
        QLambdaDefinition condition = optionalLambda(operands, "condition", instruction);
        QLambdaDefinition forUpdate = optionalLambda(operands, "forUpdate", instruction);
        ErrorReporter conditionReporter =
            operands.containsKey("conditionSource") ? reporter(optionalSource(operands, "conditionSource", instruction))
                : reporter;
        return new ForInstruction(reporter, forInit, condition, conditionReporter, forUpdate,
            requiredInt(operands, "forScopeMaxStackSize", instruction),
            importLambdaDefinition(required(operands, "forBody", instruction), instruction));
    }
    
    private List<Map.Entry<Class<?>, QLambdaDefinition>> importExceptionTable(List<?> rawEntries,
        SerializableInstruction owner) {
        List<Map.Entry<Class<?>, QLambdaDefinition>> result = new ArrayList<>(rawEntries.size());
        for (Object rawEntry : rawEntries) {
            SerializableCatchEntry entry = toCatchEntry(rawEntry, owner);
            if (entry == null) {
                throw invalid(owner, "catch entry must not be null");
            }
            if (entry.getExceptionClassName() == null) {
                throw invalid(owner, "catch entry exceptionClassName is required");
            }
            if (entry.getHandler() == null) {
                throw invalid(owner, "catch entry handler is required");
            }
            result.add(new AbstractMap.SimpleImmutableEntry<>(loadClass(entry.getExceptionClassName(), owner),
                importLambdaDefinition(entry.getHandler(), owner)));
        }
        return result;
    }
    
    private Object importConstant(Object raw, SerializableInstruction owner) {
        SerializableConstant constant = toConstant(raw, owner);
        if (constant == null) {
            throw invalid(owner, "constant is required");
        }
        String type = constant.getType();
        Object value = constant.getValue();
        if (type == null) {
            throw unsupportedConstant(owner, "null");
        }
        switch (type) {
            case "NULL":
                return null;
            case "BOOLEAN":
                return asBoolean(value, owner, "constant.value");
            case "STRING":
                return asString(value, owner, "constant.value");
            case "CHAR":
                String charValue = asString(value, owner, "constant.value");
                if (charValue.length() != 1) {
                    throw invalid(owner, "CHAR constant value must contain exactly one character");
                }
                return charValue.charAt(0);
            case "INT":
                return asInt(value, owner, "constant.value");
            case "LONG":
                return asLong(value, owner, "constant.value");
            case "BIG_INTEGER":
                return new BigInteger(asDecimalString(value, owner, "constant.value"));
            case "FLOAT":
                return asNumber(value, owner, "constant.value").floatValue();
            case "DOUBLE":
                return asNumber(value, owner, "constant.value").doubleValue();
            case "BIG_DECIMAL":
                return new BigDecimal(asDecimalString(value, owner, "constant.value"));
            case "META_CLASS":
                return new MetaClass(loadClass(asString(value, owner, "constant.value"), owner));
            default:
                throw unsupportedConstant(owner, type);
        }
    }
    
    private List<TracePointTree> importTracePoints(List<?> rawTracePoints, SerializableInstruction owner) {
        List<TracePointTree> result = new ArrayList<>(rawTracePoints.size());
        for (Object rawTracePoint : rawTracePoints) {
            result.add(importTracePoint(rawTracePoint, owner));
        }
        return result;
    }
    
    private TracePointTree importTracePoint(Object raw, SerializableInstruction owner) {
        SerializableTracePoint tracePoint = toTracePoint(raw, owner);
        if (tracePoint == null) {
            throw invalid(owner, "trace point must not be null");
        }
        TraceType type;
        try {
            type = TraceType.valueOf(tracePoint.getType());
        }
        catch (IllegalArgumentException | NullPointerException e) {
            throw invalid(owner, "invalid trace point type: " + tracePoint.getType());
        }
        List<SerializableTracePoint> children = tracePoint.getChildren();
        return new TracePointTree(type, tracePoint.getToken(),
            children == null ? Collections.emptyList() : importTracePoints(children, owner), tracePoint.getLine(),
            tracePoint.getCol(), tracePoint.getPosition());
    }
    
    private QLambdaDefinition optionalLambda(Map<String, Object> operands, String name, SerializableInstruction owner) {
        if (!operands.containsKey(name) || operands.get(name) == null) {
            return null;
        }
        return importLambdaDefinition(operands.get(name), owner);
    }
    
    private QResult.ResultType resultType(String value, SerializableInstruction owner) {
        try {
            return QResult.ResultType.valueOf(value);
        }
        catch (IllegalArgumentException e) {
            throw invalid(owner, "invalid resultType: " + value);
        }
    }
    
    private QResult breakContinueResult(String value, SerializableInstruction owner) {
        if (QResult.ResultType.BREAK.name().equals(value)) {
            return QResult.LOOP_BREAK_RESULT;
        }
        if (QResult.ResultType.CONTINUE.name().equals(value)) {
            return QResult.LOOP_CONTINUE_RESULT;
        }
        throw invalid(owner, "BREAK_CONTINUE resultType must be BREAK or CONTINUE");
    }
    
    private SliceInstruction.Mode sliceMode(String value, SerializableInstruction owner) {
        try {
            return SliceInstruction.Mode.valueOf(value);
        }
        catch (IllegalArgumentException e) {
            throw invalid(owner, "invalid slice mode: " + value);
        }
    }
    
    private BinaryOperator binaryOperator(String operator, SerializableInstruction owner) {
        BinaryOperator result = operatorManager.getBinaryOperator(operator);
        if (result == null) {
            throw operatorNotFound(owner, operator);
        }
        return result;
    }
    
    private UnaryOperator prefixUnaryOperator(String operator, SerializableInstruction owner) {
        UnaryOperator result = operatorManager.getPrefixUnaryOperator(operator);
        if (result == null) {
            throw operatorNotFound(owner, operator);
        }
        return result;
    }
    
    private UnaryOperator suffixUnaryOperator(String operator, SerializableInstruction owner) {
        UnaryOperator result = operatorManager.getSuffixUnaryOperator(operator);
        if (result == null) {
            throw operatorNotFound(owner, operator);
        }
        return result;
    }
    
    private Class<?> loadClass(String className, SerializableInstruction owner) {
        Class<?> primitive = primitiveClass(className);
        if (primitive != null) {
            return primitive;
        }
        Class<?> result = classSupplier.loadCls(className);
        if (result == null) {
            throw new SerializableParseCacheException(script, owner == null ? null : owner.getSource(),
                QLErrorCodes.SERIALIZABLE_PARSE_CACHE_CLASS_NOT_FOUND.name(),
                String.format(QLErrorCodes.SERIALIZABLE_PARSE_CACHE_CLASS_NOT_FOUND.getErrorMsg(), className));
        }
        return result;
    }
    
    private Class<?> primitiveClass(String className) {
        if ("boolean".equals(className)) {
            return boolean.class;
        }
        if ("byte".equals(className)) {
            return byte.class;
        }
        if ("char".equals(className)) {
            return char.class;
        }
        if ("short".equals(className)) {
            return short.class;
        }
        if ("int".equals(className)) {
            return int.class;
        }
        if ("long".equals(className)) {
            return long.class;
        }
        if ("float".equals(className)) {
            return float.class;
        }
        if ("double".equals(className)) {
            return double.class;
        }
        if ("void".equals(className)) {
            return void.class;
        }
        return null;
    }
    
    private ErrorReporter reporter(SerializableSource source) {
        SerializableSource normalized = source == null ? new SerializableSource() : source;
        int line = normalized.getLine() <= 0 ? 1 : normalized.getLine();
        int col = Math.max(0, normalized.getCol()) + 1;
        return new DefaultErrReporter(script == null ? "" : script, Math.max(0, normalized.getStart()), line, col,
            normalized.getLexeme() == null ? "" : normalized.getLexeme());
    }
    
    private Object required(Map<String, Object> operands, String name, SerializableInstruction owner) {
        if (!operands.containsKey(name) || operands.get(name) == null) {
            throw invalid(owner, "operand '" + name + "' is required");
        }
        return operands.get(name);
    }
    
    private String requiredString(Map<String, Object> operands, String name, SerializableInstruction owner) {
        return asString(required(operands, name, owner), owner, "operand '" + name + "'");
    }
    
    private boolean requiredBoolean(Map<String, Object> operands, String name, SerializableInstruction owner) {
        return asBoolean(required(operands, name, owner), owner, "operand '" + name + "'");
    }
    
    private int requiredInt(Map<String, Object> operands, String name, SerializableInstruction owner) {
        return asInt(required(operands, name, owner), owner, "operand '" + name + "'");
    }
    
    private Integer optionalInt(Map<String, Object> operands, String name, SerializableInstruction owner) {
        if (!operands.containsKey(name) || operands.get(name) == null) {
            return null;
        }
        return asInt(operands.get(name), owner, "operand '" + name + "'");
    }
    
    private List<?> requiredList(Map<String, Object> operands, String name, SerializableInstruction owner) {
        Object value = required(operands, name, owner);
        if (!(value instanceof List)) {
            throw invalid(owner, "operand '" + name + "' must be a list");
        }
        return (List<?>)value;
    }
    
    private List<String> requiredStringList(Map<String, Object> operands, String name, SerializableInstruction owner) {
        List<?> values = requiredList(operands, name, owner);
        List<String> result = new ArrayList<>(values.size());
        for (Object value : values) {
            result.add(asString(value, owner, "operand '" + name + "' element"));
        }
        return result;
    }
    
    private SerializableSource requiredSource(Map<String, Object> operands, String name,
        SerializableInstruction owner) {
        return toSource(required(operands, name, owner), owner);
    }
    
    private SerializableSource optionalSource(Map<String, Object> operands, String name,
        SerializableInstruction owner) {
        if (!operands.containsKey(name) || operands.get(name) == null) {
            return null;
        }
        return toSource(operands.get(name), owner);
    }
    
    private Number asNumber(Object value, SerializableInstruction owner, String name) {
        if (!(value instanceof Number)) {
            throw invalid(owner, name + " must be a number");
        }
        return (Number)value;
    }
    
    private int asInt(Object value, SerializableInstruction owner, String name) {
        Number number = asNumber(value, owner, name);
        if (number instanceof Float || number instanceof Double) {
            double doubleValue = number.doubleValue();
            if (doubleValue != Math.rint(doubleValue)) {
                throw invalid(owner, name + " must be an integer");
            }
        }
        if (number instanceof BigDecimal) {
            try {
                return ((BigDecimal)number).intValueExact();
            }
            catch (ArithmeticException e) {
                throw invalid(owner, name + " must be an int");
            }
        }
        if (number instanceof BigInteger) {
            try {
                return ((BigInteger)number).intValueExact();
            }
            catch (ArithmeticException e) {
                throw invalid(owner, name + " must be an int");
            }
        }
        long valueLong = number.longValue();
        if (valueLong < Integer.MIN_VALUE || valueLong > Integer.MAX_VALUE) {
            throw invalid(owner, name + " must be an int");
        }
        return (int)valueLong;
    }
    
    private long asLong(Object value, SerializableInstruction owner, String name) {
        Number number = asNumber(value, owner, name);
        if (number instanceof Float || number instanceof Double) {
            double doubleValue = number.doubleValue();
            if (doubleValue != Math.rint(doubleValue)) {
                throw invalid(owner, name + " must be a long");
            }
        }
        if (number instanceof BigDecimal) {
            try {
                return ((BigDecimal)number).longValueExact();
            }
            catch (ArithmeticException e) {
                throw invalid(owner, name + " must be a long");
            }
        }
        if (number instanceof BigInteger) {
            try {
                return ((BigInteger)number).longValueExact();
            }
            catch (ArithmeticException e) {
                throw invalid(owner, name + " must be a long");
            }
        }
        return number.longValue();
    }
    
    private boolean asBoolean(Object value, SerializableInstruction owner, String name) {
        if (!(value instanceof Boolean)) {
            throw invalid(owner, name + " must be a boolean");
        }
        return (Boolean)value;
    }
    
    private String asString(Object value, SerializableInstruction owner, String name) {
        if (!(value instanceof String)) {
            throw invalid(owner, name + " must be a string");
        }
        return (String)value;
    }
    
    private String asDecimalString(Object value, SerializableInstruction owner, String name) {
        if (value instanceof String || value instanceof Number) {
            return String.valueOf(value);
        }
        throw invalid(owner, name + " must be a decimal string");
    }
    
    private SerializableLambdaDefinition toLambdaDefinition(Object raw, SerializableInstruction owner) {
        if (raw == null || raw instanceof SerializableLambdaDefinition) {
            return (SerializableLambdaDefinition)raw;
        }
        Map<String, Object> map = toObjectMap(raw, owner, "lambda definition");
        SerializableLambdaDefinition result = new SerializableLambdaDefinition();
        result.setName(optionalMapString(map, "name", owner));
        Object instructions = map.get("instructions");
        if (instructions instanceof List) {
            List<?> rawInstructions = (List<?>)instructions;
            List<SerializableInstruction> converted = new ArrayList<>(rawInstructions.size());
            for (Object rawInstruction : rawInstructions) {
                converted.add(toInstruction(rawInstruction, owner));
            }
            result.setInstructions(converted);
        }
        Object params = map.get("params");
        if (params instanceof List) {
            List<?> rawParams = (List<?>)params;
            List<SerializableParam> converted = new ArrayList<>(rawParams.size());
            for (Object rawParam : rawParams) {
                converted.add(toParam(rawParam, owner));
            }
            result.setParams(converted);
        }
        if (map.containsKey("maxStackSize")) {
            result.setMaxStackSize(asInt(map.get("maxStackSize"), owner, "lambda maxStackSize"));
        }
        return result;
    }
    
    private SerializableInstruction toInstruction(Object raw, SerializableInstruction owner) {
        if (raw == null || raw instanceof SerializableInstruction) {
            return (SerializableInstruction)raw;
        }
        Map<String, Object> map = toObjectMap(raw, owner, "instruction");
        SerializableInstruction result = new SerializableInstruction();
        result.setOpcode(optionalMapString(map, "opcode", owner));
        if (map.containsKey("source") && map.get("source") != null) {
            result.setSource(toSource(map.get("source"), owner));
        }
        Object operands = map.get("operands");
        if (operands instanceof Map) {
            result.setOperands(toObjectMap(operands, owner, "operands"));
        }
        return result;
    }
    
    private SerializableParam toParam(Object raw, SerializableInstruction owner) {
        if (raw == null || raw instanceof SerializableParam) {
            return (SerializableParam)raw;
        }
        Map<String, Object> map = toObjectMap(raw, owner, "param");
        SerializableParam result = new SerializableParam();
        result.setName(optionalMapString(map, "name", owner));
        result.setClassName(optionalMapString(map, "className", owner));
        return result;
    }
    
    private SerializableConstant toConstant(Object raw, SerializableInstruction owner) {
        if (raw == null || raw instanceof SerializableConstant) {
            return (SerializableConstant)raw;
        }
        Map<String, Object> map = toObjectMap(raw, owner, "constant");
        SerializableConstant result = new SerializableConstant();
        result.setType(optionalMapString(map, "type", owner));
        result.setValue(map.get("value"));
        return result;
    }
    
    private SerializableCatchEntry toCatchEntry(Object raw, SerializableInstruction owner) {
        if (raw == null || raw instanceof SerializableCatchEntry) {
            return (SerializableCatchEntry)raw;
        }
        Map<String, Object> map = toObjectMap(raw, owner, "catch entry");
        SerializableCatchEntry result = new SerializableCatchEntry();
        result.setExceptionClassName(optionalMapString(map, "exceptionClassName", owner));
        result.setHandler(toLambdaDefinition(map.get("handler"), owner));
        return result;
    }
    
    private SerializableTracePoint toTracePoint(Object raw, SerializableInstruction owner) {
        if (raw == null || raw instanceof SerializableTracePoint) {
            return (SerializableTracePoint)raw;
        }
        Map<String, Object> map = toObjectMap(raw, owner, "trace point");
        SerializableTracePoint result = new SerializableTracePoint();
        result.setType(optionalMapString(map, "type", owner));
        result.setToken(optionalMapString(map, "token", owner));
        if (map.containsKey("children") && map.get("children") instanceof List) {
            List<?> rawChildren = (List<?>)map.get("children");
            List<SerializableTracePoint> children = new ArrayList<>(rawChildren.size());
            for (Object child : rawChildren) {
                children.add(toTracePoint(child, owner));
            }
            result.setChildren(children);
        }
        if (map.containsKey("line")) {
            result.setLine(asInt(map.get("line"), owner, "trace point line"));
        }
        if (map.containsKey("col")) {
            result.setCol(asInt(map.get("col"), owner, "trace point col"));
        }
        if (map.containsKey("position")) {
            result.setPosition(asInt(map.get("position"), owner, "trace point position"));
        }
        return result;
    }
    
    private SerializableSource toSource(Object raw, SerializableInstruction owner) {
        if (raw == null || raw instanceof SerializableSource) {
            return (SerializableSource)raw;
        }
        Map<String, Object> map = toObjectMap(raw, owner, "source");
        SerializableSource source = new SerializableSource();
        if (map.containsKey("start")) {
            source.setStart(asInt(map.get("start"), owner, "source start"));
        }
        if (map.containsKey("line")) {
            source.setLine(asInt(map.get("line"), owner, "source line"));
        }
        if (map.containsKey("col")) {
            source.setCol(asInt(map.get("col"), owner, "source col"));
        }
        source.setLexeme(optionalMapString(map, "lexeme", owner));
        return source;
    }
    
    private String optionalMapString(Map<String, Object> map, String name, SerializableInstruction owner) {
        Object value = map.get(name);
        if (value == null) {
            return null;
        }
        return asString(value, owner, name);
    }
    
    private Map<String, Object> toObjectMap(Object raw, SerializableInstruction owner, String name) {
        if (!(raw instanceof Map)) {
            throw invalid(owner, name + " must be an object");
        }
        Map<?, ?> rawMap = (Map<?, ?>)raw;
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw invalid(owner, name + " key must be a string");
            }
            result.put((String)entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    private SerializableParseCacheException invalid(SerializableInstruction instruction, String detail) {
        return modelError(instruction == null ? null : instruction.getSource(),
            QLErrorCodes.SERIALIZABLE_PARSE_CACHE_INVALID_MODEL,
            String.format(QLErrorCodes.SERIALIZABLE_PARSE_CACHE_INVALID_MODEL.getErrorMsg(), detail));
    }
    
    private SerializableParseCacheException unsupportedConstant(SerializableInstruction instruction, String type) {
        return new SerializableParseCacheException(script, instruction == null ? null : instruction.getSource(),
            QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_CONSTANT.name(),
            String.format(QLErrorCodes.SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_CONSTANT.getErrorMsg(), type));
    }
    
    private SerializableParseCacheException operatorNotFound(SerializableInstruction instruction, String operator) {
        return new SerializableParseCacheException(script, instruction == null ? null : instruction.getSource(),
            QLErrorCodes.SERIALIZABLE_PARSE_CACHE_OPERATOR_NOT_FOUND.name(),
            String.format(QLErrorCodes.SERIALIZABLE_PARSE_CACHE_OPERATOR_NOT_FOUND.getErrorMsg(), operator));
    }
    
    private SerializableParseCacheException modelError(SerializableSource source, QLErrorCodes code, String reason) {
        return new SerializableParseCacheException(script, source, code.name(), reason);
    }
}
