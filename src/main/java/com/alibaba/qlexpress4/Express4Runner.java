package com.alibaba.qlexpress4;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.alibaba.qlexpress4.aparser.*;
import com.alibaba.qlexpress4.aparser.compiletimefunction.CompileTimeFunction;
import com.alibaba.qlexpress4.api.BatchAddFunctionResult;
import com.alibaba.qlexpress4.api.QLFunctionalVarargs;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.context.ExpressContext;
import com.alibaba.qlexpress4.runtime.context.MapExpressContext;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;
import com.alibaba.qlexpress4.runtime.function.QMethodFunction;
import com.alibaba.qlexpress4.runtime.operator.CustomBinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.QLFunctionUtil;

/**
 * Author: DQinYuan
 */
public class Express4Runner {
    private final OperatorManager operatorManager = new OperatorManager();
    private final Map<String, Future<QLambdaDefinitionInner>> compileCache = new ConcurrentHashMap<>();
    private final Map<String, CustomFunction> userDefineFunction = new ConcurrentHashMap<>();
    private final Map<String, CompileTimeFunction> compileTimeFunctions = new ConcurrentHashMap<>();
    private final ReflectLoader reflectLoader;
    private final InitOptions initOptions;

    public Express4Runner(InitOptions initOptions) {
        this.initOptions = initOptions;
        this.reflectLoader = new ReflectLoader(initOptions.getSecurityStrategy(), initOptions.getExtensionFunctions(),
                initOptions.allowPrivateAccess());
        SyntaxTreeFactory.warmUp();
    }

    public CustomFunction getFunction(String functionName) {
        return userDefineFunction.get(functionName);
    }

    public CompileTimeFunction getCompileTimeFunction(String functionName) {
        return compileTimeFunctions.get(functionName);
    }

    public Object execute(String script, Map<String, Object> context, QLOptions qlOptions) throws QLException {
        return execute(script, new MapExpressContext(context), qlOptions);
    }

    public Object execute(String script, ExpressContext context, QLOptions qlOptions) {
        QLambda mainLambda;
        if (initOptions.isDebug()) {
            long start = System.currentTimeMillis();
            mainLambda = parseToLambda(script, context, qlOptions);
            initOptions.getDebugInfoConsumer().accept(
                    "Compile consume time: " + (System.currentTimeMillis() - start) + " ms"
            );
        } else {
            mainLambda = parseToLambda(script, context, qlOptions);
        }
        try {
            if (initOptions.isDebug()) {
                long start = System.currentTimeMillis();
                Object result = mainLambda.call().getResult().get();
                initOptions.getDebugInfoConsumer().accept("Execute consume time: " + (System.currentTimeMillis() - start) + " ms");
                return result;
            } else {
                return mainLambda.call().getResult().get();
            }
        } catch (QLException e) {
            throw e;
        } catch (Throwable nuKnown) {
            // should not run here
            throw new RuntimeException(nuKnown);
        }
    }

    public Set<String> getOutVarNames(String script) {
        QLGrammarParser.ProgramContext programContext = parseToSyntaxTree(script);
        OutVarNamesVisitor outVarNamesVisitor = new OutVarNamesVisitor();
        programContext.accept(outVarNamesVisitor);
        return outVarNamesVisitor.getOutVars();
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

    public boolean addFunction(String name, QLFunctionalVarargs functionalVarargs) {
        return addFunction(name, (qContext, parameters) -> {
            Object[] paramArr = new Object[parameters.size()];
            for (int i = 0; i < paramArr.length; i++) {
                paramArr[i] = parameters.get(i);
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
                result.getSucc().add(method.getName());
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

    public QLGrammarParser.ProgramContext parseToSyntaxTree(String script) {
        return SyntaxTreeFactory.buildTree(
                script, operatorManager, initOptions.isDebug(), false,
                initOptions.getDebugInfoConsumer()
        );
    }

    public QLambda parseToLambda(String script, ExpressContext context, QLOptions qlOptions) {
        QLambdaDefinitionInner mainLambdaDefine = qlOptions.isCache()?
                parseDefinitionWithCache(script): parseDefinition(script);
        if (initOptions.isDebug()) {
            initOptions.getDebugInfoConsumer().accept("\nInstructions:");
            mainLambdaDefine.println(0, initOptions.getDebugInfoConsumer());
        }

        QvmRuntime qvmRuntime = new QvmRuntime(qlOptions.getAttachments(), reflectLoader, System.currentTimeMillis());
        QvmGlobalScope globalScope = new QvmGlobalScope(context, userDefineFunction, qlOptions);
        return mainLambdaDefine.toLambda(new DelegateQContext(qvmRuntime, globalScope), qlOptions, true);
    }

    private QLambdaDefinitionInner parseDefinitionWithCache(String script) {
        try {
            return getParseFuture(script).get();
        } catch (Exception e) {
            Throwable compileException = e.getCause();
            throw compileException instanceof QLSyntaxException? (QLSyntaxException) compileException:
                    new RuntimeException(compileException);
        }
    }

    private Future<QLambdaDefinitionInner> getParseFuture(String script) {
        Future<QLambdaDefinitionInner> parseFuture = compileCache.get(script);
        if (parseFuture != null) {
            return parseFuture;
        }
        FutureTask<QLambdaDefinitionInner> parseTask = new FutureTask<>(() -> parseDefinition(script));
        Future<QLambdaDefinitionInner> preTask = compileCache.putIfAbsent(script, parseTask);
        if (preTask == null) {
            parseTask.run();
        }
        return parseTask;
    }

    private QLambdaDefinitionInner parseDefinition(String script) {
        QLGrammarParser.ProgramContext program = parseToSyntaxTree(script);
        QvmInstructionVisitor qvmInstructionVisitor = new QvmInstructionVisitor(script,
                inheritDefaultImport(), operatorManager, compileTimeFunctions);
        program.accept(qvmInstructionVisitor);

        return new QLambdaDefinitionInner("main",
                qvmInstructionVisitor.getInstructions(), Collections.emptyList(),
                qvmInstructionVisitor.getMaxStackSize());
    }

    private ImportManager inheritDefaultImport() {
        return new ImportManager(initOptions.getClassSupplier(), initOptions.getDefaultImport());
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
}
