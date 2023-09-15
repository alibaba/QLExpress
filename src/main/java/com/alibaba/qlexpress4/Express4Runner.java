package com.alibaba.qlexpress4;

import java.lang.reflect.Array;
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
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.function.QFunction;
import com.alibaba.qlexpress4.runtime.function.QLambdaFunction;
import com.alibaba.qlexpress4.runtime.function.QMethodFunction;
import com.alibaba.qlexpress4.runtime.operator.CustomBinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.QLFunctionUtil;

/**
 * Author: DQinYuan
 * date 2022/1/12 2:28 下午
 */
public class Express4Runner {
    private final OperatorManager operatorManager = new OperatorManager();
    private final Map<String, Future<QLambdaDefinitionInner>> compileCache = new ConcurrentHashMap<>();
    private final Map<String, QFunction> userDefineFunction = new ConcurrentHashMap<>();
    private final Map<String, CompileTimeFunction> compileTimeFunctions = new ConcurrentHashMap<>();
    private final ReflectLoader reflectLoader;
    private final InitOptions initOptions;

    public Express4Runner(InitOptions initOptions) {
        this.initOptions = initOptions;
        this.reflectLoader = new ReflectLoader(initOptions.getSecurityStrategy(), initOptions.allowPrivateAccess());
        SyntaxTreeFactory.warmUp();
    }

    public Object execute(String script, Map<String, Object> context, QLOptions qlOptions) throws QLException {
        return execute(script, new MapExpressContext(context), qlOptions);
    }

    public Object execute(String script, ExpressContext context, QLOptions qlOptions) {
        QLambda mainLambda;
        if (initOptions.isDebug()) {
            long start = System.currentTimeMillis();
            mainLambda = parseToLambda(script, context, qlOptions);
            System.out.println("Compile consume time: " + (System.currentTimeMillis() - start) + " ms");
        } else {
            mainLambda = parseToLambda(script, context, qlOptions);
        }
        try {
            if (initOptions.isDebug()) {
                long start = System.currentTimeMillis();
                Object result = mainLambda.call().getResult().get();
                System.out.println("Execute consume time: " + (System.currentTimeMillis() - start) + " ms");
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

    /**
     * add user defined function to QLExpress engine
     * @param name function name
     * @param function function definition
     * @return true if add function successfully. fail if function name already exists or method is not public.
     */
    public boolean addFunction(String name, QFunction function) {
        QFunction preFunction = userDefineFunction.putIfAbsent(name, function);
        return preFunction == null;
    }

    public <T, R> boolean addFunction(String name, Function<T, R> function) {
        return addFunction(name, new QLambdaFunction(params -> {
            R result = function.apply((T) params[0]);
            return new QResult(new DataValue(result), QResult.ResultType.RETURN);
        }));
    }

    public boolean addFunction(String name, QLFunctionalVarargs functionalVarargs) {
        return addFunction(name, new QLambdaFunction(params -> {
            Object array = Array.newInstance(Object.class, params.length);
            for (int i = 0; i < params.length; i++) {
                Array.set(array, i, params[i]);
            }
            Object result = functionalVarargs.call((Object[])array);
            return new QResult(new DataValue(result), QResult.ResultType.RETURN);
        }));
    }

    public <T> boolean addFunction(String name, Predicate<T> predicate) {
        return addFunction(name, new QLambdaFunction(
            params -> new QResult(new DataValue(predicate.test((T)params[0])), QResult.ResultType.RETURN)));
    }

    public boolean addFunction(String name, Runnable runnable) {
        return addFunction(name, new QLambdaFunction(params -> {
            runnable.run();
            return new QResult(null, QResult.ResultType.RETURN);
        }));
    }

    public <T> boolean addFunction(String name, Consumer<T> consumer) {
        return addFunction(name, new QLambdaFunction(params -> {
            consumer.accept((T)params[0]);
            return new QResult(null, QResult.ResultType.RETURN);
        }));
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
        return mainLambdaDefine.toLambda(new DelegateQContext(qvmRuntime, globalScope),
                qlOptions, true);
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

        QLambdaDefinitionInner mainLambdaDefine = new QLambdaDefinitionInner("main",
                qvmInstructionVisitor.getInstructions(), Collections.emptyList(),
                qvmInstructionVisitor.getMaxStackSize());
        return mainLambdaDefine;
    }

    private ImportManager inheritDefaultImport() {
        return new ImportManager(initOptions.getClassSupplier(), initOptions.getDefaultImport());
    }

    public boolean addOperator(String operator, CustomBinaryOperator customBinaryOperator) {
        return operatorManager.addOperator(operator, customBinaryOperator, QLPrecedences.MULTI);
    }
}
