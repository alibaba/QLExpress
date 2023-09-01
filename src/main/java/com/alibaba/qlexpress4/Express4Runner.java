package com.alibaba.qlexpress4;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.alibaba.qlexpress4.aparser.*;
import com.alibaba.qlexpress4.api.BatchAddFunctionResult;
import com.alibaba.qlexpress4.api.QLFunctionalVarargs;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.lambda.QLambdaMethod;
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

    private final Map<String, QFunction> userDefineFunction = new ConcurrentHashMap<>();
    private final ReflectLoader reflectLoader;
    private final InitOptions initOptions;

    public Express4Runner(InitOptions initOptions) {
        this.initOptions = initOptions;
        this.reflectLoader = new ReflectLoader(initOptions.allowPrivateAccess());
    }

    public Object execute(String script, Map<String, Object> context, QLOptions qlOptions) throws QLException {
        QLambda mainLambda = parseToLambda(script, context, qlOptions);
        try {
            return mainLambda.call().getResult().get();
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
            R result = function.apply((T)params[0]);
            return new QResult(new DataValue(result), QResult.ResultType.RETURN);
        }));
    }

    public <T, R> boolean addFunction(String name, QLFunctionalVarargs functionalVarargs) {
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

    public QLGrammarParser.ProgramContext parseToSyntaxTree(String script, QLOptions qlOptions) {
        return SyntaxTreeFactory.buildTree(
                script, operatorManager, qlOptions.isDebug(),
                qlOptions.getDebugInfoConsumer()
        );
    }

    private QLambda parseToLambda(String script, Map<String, Object> context, QLOptions qlOptions) {
        QLGrammarParser.ProgramContext program = parseToSyntaxTree(script, qlOptions);

        QvmInstructionVisitor qvmInstructionVisitor = new QvmInstructionVisitor(script,
                inheritDefaultImport(qlOptions), operatorManager);
        program.accept(qvmInstructionVisitor);

        QLambdaDefinitionInner mainLambdaDefine = new QLambdaDefinitionInner("main",
                qvmInstructionVisitor.getInstructions(), Collections.emptyList(),
                qvmInstructionVisitor.getMaxStackSize());
        if (qlOptions.isDebug()) {
            qlOptions.getDebugInfoConsumer().accept("\nInstructions:");
            mainLambdaDefine.println(0, qlOptions.getDebugInfoConsumer());
        }

        QvmRuntime qvmRuntime = new QvmRuntime(qlOptions.getAttachments(), reflectLoader, System.currentTimeMillis());
        QvmGlobalScope globalScope = new QvmGlobalScope(context, userDefineFunction,
                qlOptions.isPolluteUserContext());
        return mainLambdaDefine.toLambda(new DelegateQContext(qvmRuntime, globalScope),
                qlOptions, true);
    }

    private ImportManager inheritDefaultImport(QLOptions qlOptions) {
        return new ImportManager(initOptions.classSupplier(), qlOptions.getDefaultImport());
    }

    public boolean addOperator(String operator, CustomBinaryOperator customBinaryOperator) {
        return operatorManager.addOperator(operator, customBinaryOperator, QLPrecedences.MULTI);
    }

    public boolean addOperator(String operator, CustomBinaryOperator customBinaryOperator, int priority) {
        return operatorManager.addOperator(operator, customBinaryOperator, priority);
    }
}
