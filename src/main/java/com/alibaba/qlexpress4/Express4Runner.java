package com.alibaba.qlexpress4;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.alibaba.qlexpress4.aparser.*;
import com.alibaba.qlexpress4.aparser.compiletimefunction.CompileTimeFunction;
import com.alibaba.qlexpress4.api.BatchAddFunctionResult;
import com.alibaba.qlexpress4.api.QLFunctionalVarargs;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.context.ExpressContext;
import com.alibaba.qlexpress4.runtime.context.MapExpressContext;
import com.alibaba.qlexpress4.runtime.context.ObjectFieldExpressContext;
import com.alibaba.qlexpress4.runtime.context.QLAliasContext;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;
import com.alibaba.qlexpress4.runtime.function.QMethodFunction;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;
import com.alibaba.qlexpress4.runtime.operator.CustomBinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.runtime.trace.ExpressionTrace;
import com.alibaba.qlexpress4.runtime.trace.QTraces;
import com.alibaba.qlexpress4.runtime.trace.TracePointTree;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.QLFunctionUtil;

/**
 * Author: DQinYuan
 */
public class Express4Runner {
    private final OperatorManager operatorManager = new OperatorManager();
    private final Map<String, Future<QCompileCache>> compileCache = new ConcurrentHashMap<>();
    private final Map<String, CustomFunction> userDefineFunction = new ConcurrentHashMap<>();
    private final Map<String, CompileTimeFunction> compileTimeFunctions = new ConcurrentHashMap<>();
    private final GeneratorScope globalScope = new GeneratorScope(null, "global", new ConcurrentHashMap<>());
    private final ReflectLoader reflectLoader;
    private final InitOptions initOptions;

    public Express4Runner(InitOptions initOptions) {
        this.initOptions = initOptions;
        this.reflectLoader = new ReflectLoader(initOptions.getSecurityStrategy(), initOptions.getExtensionFunctions(),
                initOptions.isAllowPrivateAccess());
        SyntaxTreeFactory.warmUp();
    }

    public CustomFunction getFunction(String functionName) {
        return userDefineFunction.get(functionName);
    }

    public CompileTimeFunction getCompileTimeFunction(String functionName) {
        return compileTimeFunctions.get(functionName);
    }

    /**
     * execute the script with variables set in the context, where the key corresponds to the variable name.
     * @param script
     * @param context
     * @param qlOptions
     * @return result of script
     * @throws QLException
     */
    public QLResult execute(String script, Map<String, Object> context, QLOptions qlOptions) throws QLException {
        return execute(script, new MapExpressContext(context), qlOptions);
    }

    /**
     * execute the script with variables set in the context, where the key corresponds to the field name of context object
     * @param script
     * @param context
     * @param qlOptions
     * @return
     * @throws QLException
     */
    public QLResult execute(String script, Object context, QLOptions qlOptions) throws QLException {
        return execute(script, new ObjectFieldExpressContext(context, reflectLoader), qlOptions);
    }

    /**
     * Execute the script using objects that have been annotated with the com.alibaba.qlexpress4.annotation.QLAlias.
     * The QLAlias.value will serve as the context keys for these objects.
     * Objects without the QLAlias annotation will be disregarded.
     * @param script
     * @param qlOptions
     * @param objects
     * @return
     * @throws QLException
     */
    public QLResult executeWithAliasObjects(String script, QLOptions qlOptions, Object...objects) {
        return execute(script, new QLAliasContext(objects), qlOptions);
    }

    public QLResult execute(String script, ExpressContext context, QLOptions qlOptions) {
        QLambdaTrace mainLambdaTrace;
        if (initOptions.isDebug()) {
            long start = System.currentTimeMillis();
            mainLambdaTrace = parseToLambda(script, context, qlOptions);
            initOptions.getDebugInfoConsumer().accept(
                    "Compile consume time: " + (System.currentTimeMillis() - start) + " ms"
            );
        } else {
            mainLambdaTrace = parseToLambda(script, context, qlOptions);
        }
        QLambda mainLambda = mainLambdaTrace.getqLambda();
        try {
            Object result;
            if (initOptions.isDebug()) {
                long start = System.currentTimeMillis();
                result = mainLambda.call().getResult().get();
                initOptions.getDebugInfoConsumer().accept("Execute consume time: " + (System.currentTimeMillis() - start) + " ms");
            } else {
                result = mainLambda.call().getResult().get();
            }

            return new QLResult(result, mainLambdaTrace.getTraces().getExpressionTraces());
        } catch (QLException e) {
            throw e;
        } catch (Throwable nuKnown) {
            // should not run here
            throw new RuntimeException(nuKnown);
        }
    }

    private QTraces convertPoints2QTraces(List<TracePointTree> expressionTracePoints) {
        Map<Integer, ExpressionTrace> traceMap = new HashMap<>();
        List<ExpressionTrace> expressionTraces = expressionTracePoints.stream()
                .map(tracePoint -> convertPoint2Trace(tracePoint, traceMap))
                .collect(Collectors.toList());
        return new QTraces(expressionTraces, traceMap);
    }

    private ExpressionTrace convertPoint2Trace(TracePointTree tree, Map<Integer, ExpressionTrace> traceMap) {
        if (tree.getChildren().isEmpty()) {
            ExpressionTrace result = new ExpressionTrace(
                    tree.getType(), tree.getToken(), Collections.emptyList(),
                    tree.getLine(), tree.getCol(), tree.getPosition()
            );
            traceMap.put(result.getPosition(), result);
            return result;
        }
        List<ExpressionTrace> mergedChildren = tree.getChildren()
                .stream().map(child -> convertPoint2Trace(child, traceMap))
                .collect(Collectors.toList());
        ExpressionTrace result = new ExpressionTrace(
                tree.getType(), tree.getToken(), mergedChildren,
                tree.getLine(), tree.getCol(), tree.getPosition()
        );
        traceMap.put(result.getPosition(), result);
        return result;
    }

    /**
     * get out vars(Variables that need to be passed from outside the script through context) in script
     * @param script
     * @return name of out vars
     */
    public Set<String> getOutVarNames(String script) {
        QLParser.ProgramContext programContext = parseToSyntaxTree(script);
        OutVarNamesVisitor outVarNamesVisitor = new OutVarNamesVisitor();
        programContext.accept(outVarNamesVisitor);
        return outVarNamesVisitor.getOutVars();
    }

    /**
     * get the trace tree of expression in the script (without executing the script).
     * @param script
     * @return trace trees
     */
    public List<TracePointTree> getExpressionTracePoints(String script) {
        QLParser.ProgramContext programContext = parseToSyntaxTree(script);
        TraceExpressionVisitor traceExpressionVisitor = new TraceExpressionVisitor();
        programContext.accept(traceExpressionVisitor);
        return traceExpressionVisitor.getExpressionTracePoints();
    }

    /**
     * add user defined global macro to QLExpress engine
     * @param name macro name
     * @param macroScript script for macro
     * @return true if add macro successfully. fail if macro name already exists.
     */
    public boolean addMacro(String name, String macroScript) {
        QLParser.ProgramContext macroProgram = parseToSyntaxTree(macroScript);
        QvmInstructionVisitor macroVisitor = new QvmInstructionVisitor(macroScript,
                inheritDefaultImport(), new GeneratorScope("MACRO_" + name, globalScope),
                operatorManager, QvmInstructionVisitor.Context.MACRO,
                compileTimeFunctions, initOptions.getInterpolationMode());
        macroProgram.accept(macroVisitor);
        List<QLInstruction> macroInstructions = macroVisitor.getInstructions();
        List<QLParser.BlockStatementContext> blockStatementContexts = macroProgram.blockStatements()
                .blockStatement();
        boolean lastStmtExpress = !blockStatementContexts.isEmpty() &&
                blockStatementContexts.get(blockStatementContexts.size() - 1) instanceof QLParser.ExpressionStatementContext;
        return globalScope.defineMacroIfAbsent(name, new MacroDefine(macroInstructions, lastStmtExpress));
    }

    /**
     * add user defined function to QLExpress engine
     * @param name function name
     * @param function function definition
     * @return true if add function successfully. fail if function name already exists or method is not public.
     */
    public boolean addFunction(String name, CustomFunction function) {
        CustomFunction preFunction = userDefineFunction.putIfAbsent(name, function);
        return preFunction == null;
    }

    public <T, R> boolean addFunction(String name, Function<T, R> function) {
        return addFunction(name, (qContext, parameters) -> {
            T t = parameters.size() > 0? (T) parameters.get(0).get(): null;
            return function.apply(t);
        });
    }

    public boolean addVarArgsFunction(String name, QLFunctionalVarargs functionalVarargs) {
        return addFunction(name, (qContext, parameters) -> {
            Object[] paramArr = new Object[parameters.size()];
            for (int i = 0; i < paramArr.length; i++) {
                paramArr[i] = parameters.get(i).get();
            }
            return functionalVarargs.call(paramArr);
        });
    }

    public <T> boolean addFunction(String name, Predicate<T> predicate) {
        return addFunction(name, (qContext, parameters) -> {
            T t = parameters.size() > 0? (T) parameters.get(0).get(): null;
            return predicate.test(t);
        });
    }

    public boolean addFunction(String name, Runnable runnable) {
        return addFunction(name, (qContext, parameters) -> {
            runnable.run();
            return null;
        });
    }

    public <T> boolean addFunction(String name, Consumer<T> consumer) {
        return addFunction(name, (qContext, parameters) -> {
            T t = parameters.size() > 0? (T) parameters.get(0).get(): null;
            consumer.accept(t);
            return null;
        });
    }

    /**
     * add object member method with annotation {@link com.alibaba.qlexpress4.annotation.QLFunction} as function
     * @param object object with member method with annotation {@link com.alibaba.qlexpress4.annotation.QLFunction}
     * @return succ and fail functions. fail if function name already exists or method is not public
     */
    public BatchAddFunctionResult addObjFunction(Object object) {
        return addFunctionByAnnotation(object.getClass(), object);
    }

    /**
     * add class static method with annotation {@link com.alibaba.qlexpress4.annotation.QLFunction} as function
     * @param clazz class with static method with annotation {@link com.alibaba.qlexpress4.annotation.QLFunction}
     * @return succ and fail functions. fail if function name already exists or method is not public
     */
    public BatchAddFunctionResult addStaticFunction(Class<?> clazz) {
        return addFunctionByAnnotation(clazz, null);
    }

    private BatchAddFunctionResult addFunctionByAnnotation(Class<?> clazz, Object object) {
        BatchAddFunctionResult result = new BatchAddFunctionResult();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!BasicUtil.isPublic(method)) {
                result.getFail().add(method.getName());
                continue;
            }
            if (QLFunctionUtil.containsQLFunctionForMethod(method)) {
                for (String functionName : QLFunctionUtil.getQLFunctionValue(method)) {
                    boolean addResult = addFunction(functionName, new QMethodFunction(object, method));
                    (addResult? result.getSucc(): result.getFail()).add(method.getName());
                }
            }
        }
        return result;
    }

    /**
     * add compile time function
     * @param name function name
     * @param compileTimeFunction definition
     * @return true if successful
     */
    public boolean addCompileTimeFunction(String name, CompileTimeFunction compileTimeFunction) {
        return compileTimeFunctions.putIfAbsent(name, compileTimeFunction) == null;
    }

    public QLParser.ProgramContext parseToSyntaxTree(String script) {
        return SyntaxTreeFactory.buildTree(
                script, operatorManager, initOptions.isDebug(), false,
                initOptions.getDebugInfoConsumer(), initOptions.getInterpolationMode()
        );
    }

    public QLambdaTrace parseToLambda(String script, ExpressContext context, QLOptions qlOptions) {
        QCompileCache mainLambdaDefine = qlOptions.isCache()?
                parseToDefinitionWithCache(script): parseDefinition(script);
        if (initOptions.isDebug()) {
            initOptions.getDebugInfoConsumer().accept("\nInstructions:");
            mainLambdaDefine.getQLambdaDefinition().println(0, initOptions.getDebugInfoConsumer());
        }


        QTraces qTraces = initOptions.isTraceExpression()? convertPoints2QTraces(mainLambdaDefine.getExpressionTracePoints()):
                new QTraces(null, null);

        QvmRuntime qvmRuntime = new QvmRuntime(
                qTraces, qlOptions.getAttachments(), reflectLoader, System.currentTimeMillis()
        );
        QvmGlobalScope globalScope = new QvmGlobalScope(context, userDefineFunction, qlOptions);
        QLambda qLambda = mainLambdaDefine.getQLambdaDefinition()
                .toLambda(new DelegateQContext(qvmRuntime, globalScope), qlOptions, true);
        return new QLambdaTrace(qLambda, qTraces);
    }

    /**
     * parse script with cache
     * @param script script to parse
     * @return QLambdaDefinition and TracePointTrees
     */
    public QCompileCache parseToDefinitionWithCache(String script) {
        try {
            return getParseFuture(script).get();
        } catch (Exception e) {
            Throwable compileException = e.getCause();
            throw compileException instanceof QLSyntaxException? (QLSyntaxException) compileException:
                    new RuntimeException(compileException);
        }
    }

    private Future<QCompileCache> getParseFuture(String script) {
        Future<QCompileCache> parseFuture = compileCache.get(script);
        if (parseFuture != null) {
            return parseFuture;
        }
        FutureTask<QCompileCache> parseTask = new FutureTask<>(() -> parseDefinition(script));
        Future<QCompileCache> preTask = compileCache.putIfAbsent(script, parseTask);
        if (preTask == null) {
            parseTask.run();
        }
        return parseTask;
    }

    private QCompileCache parseDefinition(String script) {
        QLParser.ProgramContext program = parseToSyntaxTree(script);
        QvmInstructionVisitor qvmInstructionVisitor = new QvmInstructionVisitor(script, inheritDefaultImport(),
                globalScope, operatorManager, compileTimeFunctions, initOptions.getInterpolationMode());
        program.accept(qvmInstructionVisitor);

        QLambdaDefinitionInner qLambdaDefinition = new QLambdaDefinitionInner("main",
                qvmInstructionVisitor.getInstructions(), Collections.emptyList(),
                qvmInstructionVisitor.getMaxStackSize());
        if (initOptions.isTraceExpression()) {
            TraceExpressionVisitor traceExpressionVisitor = new TraceExpressionVisitor();
            program.accept(traceExpressionVisitor);
            List<TracePointTree> tracePoints = traceExpressionVisitor.getExpressionTracePoints();
            return new QCompileCache(qLambdaDefinition, tracePoints);
        } else {
            return new QCompileCache(qLambdaDefinition, Collections.emptyList());
        }
    }

    private ImportManager inheritDefaultImport() {
        return new ImportManager(initOptions.getClassSupplier(), initOptions.getDefaultImport());
    }

    public <T,U,R> boolean addOperatorBiFunction(String operator, BiFunction<T,U,R> biFunction) {
        return operatorManager.addBinaryOperator(operator, (left, right) ->
                biFunction.apply((T) left.get(), (U) right.get()), QLPrecedences.MULTI
        );
    }

    /**
     * add operator with multi precedences
     * @param operator operator name
     * @param customBinaryOperator operator implement
     * @return true if add operator successfully; false if operator already exist
     */
    public boolean addOperator(String operator, CustomBinaryOperator customBinaryOperator) {
        return operatorManager.addBinaryOperator(operator, customBinaryOperator, QLPrecedences.MULTI);
    }

    /**
     * add operator
     * @param operator operator name
     * @param customBinaryOperator operator implement
     * @param precedence precedence, see {@link QLPrecedences}
     * @return true if add operator successfully; false if operator already exist
     */
    public boolean addOperator(String operator, CustomBinaryOperator customBinaryOperator, int precedence) {
        return operatorManager.addBinaryOperator(operator, customBinaryOperator, precedence);
    }

    /**
     * @param operator operator name
     * @param customBinaryOperator operator implement
     * @return true if replace operator successfully; false if default operator not exists
     */
    public boolean replaceDefaultOperator(String operator, CustomBinaryOperator customBinaryOperator) {
        return operatorManager.replaceDefaultOperator(operator, customBinaryOperator);
    }

    /**
     * add alias for keyWord, operator and function
     * @param alias must be a valid id
     * @param originToken key word in qlexpress
     * @return true if add alias successfully
     */
    public boolean addAlias(String alias, String originToken) {
        boolean addKeyWordAliasResult = operatorManager.addKeyWordAlias(alias, originToken);
        boolean addOperatorAliasResult = operatorManager.addOperatorAlias(alias, originToken);
        boolean addFunctionAliasResult = addFunctionAlias(alias, originToken);

        return addKeyWordAliasResult || addOperatorAliasResult || addFunctionAliasResult;
    }

    private boolean addFunctionAlias(String alias, String originToken) {
        CustomFunction customFunction = userDefineFunction.get(originToken);
        if (customFunction != null) {
            return userDefineFunction.putIfAbsent(alias, customFunction) == null;
        }
        return false;
    }
}
