package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.cache.QLCaches;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.parser.*;
import com.alibaba.qlexpress4.parser.tree.Program;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.lambda.QLambdaMethod;
import com.alibaba.qlexpress4.runtime.operator.CustomBinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.utils.CacheUtil;
import com.alibaba.qlexpress4.utils.PropertiesUtil;
import com.alibaba.qlexpress4.utils.QLFieldUtil;
import com.alibaba.qlexpress4.utils.QLFunctionUtil;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Author: DQinYuan
 * date 2022/1/12 2:28 下午
 */
public class Express4Runner {
    private final OperatorManager operatorManager = new OperatorManager();

    private final Map<String, QFunction> userDefineFunction = new ConcurrentHashMap<>();
    private final Map<String, QFunction> userDefineField = new ConcurrentHashMap<>();
    private final QLCaches qlCaches;

    public Express4Runner(InitOptions initOptions) {
        qlCaches = new QLCaches(CacheUtil.initConstructorCache(10, initOptions.enableUseCacheClear()),
                CacheUtil.initFieldCache(10, initOptions.enableUseCacheClear()),
                CacheUtil.initMethodCache(10, initOptions.enableUseCacheClear()),
                CacheUtil.initMethodInvokeCache(10, initOptions.enableUseCacheClear()),
                CacheUtil.initScriptCache(10, initOptions.enableUseCacheClear()));
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

    public void addFunction(String name, QFunction function) {
        userDefineFunction.put(name, function);
    }

    public <T, R> void addFunction(String name, Function<T, R> function) {
        addFunction(name, new QFunctionInner(params -> {
            R result = function.apply((T) params[0]);
            return new QResult(new DataValue(result), QResult.ResultType.RETURN);
        }));
    }

    public <T, R> void addFunction(String name, QLFunctionalVarargs<T, R> functionalVarargs, Class<?> type) {
        addFunction(name, new QFunctionInner(params -> {
            Object array = Array.newInstance(type, params.length);
            for (int i = 0; i < params.length; i++) {
                Array.set(array, i, params[i]);
            }
            R result = functionalVarargs.call((T[]) array);
            return new QResult(new DataValue(result), QResult.ResultType.RETURN);
        }));
    }

    public <T> void addFunction(String name, Predicate<T> predicate) {
        addFunction(name, new QFunctionInner(
                params -> new QResult(new DataValue(predicate.test((T) params[0])), QResult.ResultType.RETURN)));
    }

    public void addFunction(String name, Runnable runnable) {
        addFunction(name, new QFunctionInner(params -> {
            runnable.run();
            return new QResult(null, QResult.ResultType.RETURN);
        }));
    }

    public <T> void addFunction(String name, Consumer<T> consumer) {
        addFunction(name, new QFunctionInner(params -> {
            consumer.accept((T) params[0]);
            return new QResult(null, QResult.ResultType.RETURN);
        }));
    }

    public void addField(String name, QFunction function) {
        userDefineField.put(name, function);
    }

    public void addFieldByClassAnnotation(Object object) {
        addFieldByAnnotation(object.getClass(), object);
    }

    public void addFieldByObjectAnnotation(Class<?> clazz) {
        addFieldByAnnotation(clazz, clazz);
    }

    public void addFunctionByObjectAnnotation(Object object) {
        addFunctionByAnnotation(object.getClass(), object);
    }

    public void addFunctionByClassAnnotation(Class<?> clazz) {
        addFunctionByAnnotation(clazz, clazz);
    }

    public void addFunction(String name, Object obj, String methodName) {
        addFunctionByObject(name, obj, methodName);
    }

    public void addClassFunction(String name, Class<?> clazz, String methodName){
        addFunctionByClass(name, clazz, methodName);
    }

    private void addFunctionByObject(String name, Object object, String methodName) {
        List<Method> methods = PropertiesUtil.getMethodWhenAddFunction(object.getClass(), methodName);
        addFunction(name, new QFunctionInner(new QLambdaMethod(methods, object)));
    }

    private void addFunctionByClass(String name, Class<?> clazz, String methodName) {
        List<Method> methods = PropertiesUtil.getClzMethodWhenAddFunction(clazz, methodName);
        addFunction(name, new QFunctionInner(new QLambdaMethod(methods, clazz)));
    }

    private void addFunctionByAnnotation(Class<?> clazz, Object object) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (QLFunctionUtil.containsQLFunctionForMethod(method)) {
                for (String value : QLFunctionUtil.getQLFunctionValue(method)) {
                    List<Method> qlMethods = new ArrayList<>();
                    qlMethods.add(method);
                    addFunction(value, new QFunctionInner(new QLambdaMethod(qlMethods, object)));
                }
            }
        }
    }

    private void addFieldByAnnotation(Class<?> clazz, Object object) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (QLFieldUtil.containsQLFieldForMethod(method)) {
                for (String value : QLFieldUtil.getQLFieldValue(method)) {
                    List<Method> qlMethods = new ArrayList<>();
                    qlMethods.add(method);
                    addField(value, new QFunctionInner(new QLambdaMethod(qlMethods, object)));
                }
            }
        }
    }

    public Program parseToSyntaxTree(String script, QLOptions qlOptions) {
        QLParser qlParser = new QLParser(operatorManager.getOperatorPrecedenceMap(), new Scanner(script, qlOptions),
                ImportManager.buildGlobalImportManager(qlOptions.getDefaultImport()),
                DefaultClassSupplier.INSTANCE);
        return qlParser.parse();
    }

    public boolean addOperator(String operator, CustomBinaryOperator customBinaryOperator) {
        return operatorManager.addOperator(operator, customBinaryOperator, QLPrecedences.MULTI);
    }

    public boolean addOperator(String operator, CustomBinaryOperator customBinaryOperator, int priority) {
        return operatorManager.addOperator(operator, customBinaryOperator, priority);
    }

    private QLambda parseToLambda(String script, Map<String, Object> context, QLOptions qlOptions) {
        QLambdaDefinitionInner mainLambdaDefine = parseSyntaxAndInstructions(script, qlOptions);
        QvmRuntime qvmRuntime = new QvmRuntime(qlOptions.getAttachments(), qlCaches, System.currentTimeMillis());
        QvmGlobalScope globalScope = new QvmGlobalScope(context, userDefineFunction,
                qlOptions.isPolluteUserContext());
        return mainLambdaDefine.toLambda(new DelegateQContext(qvmRuntime, globalScope),
                qlOptions, true);
    }

    private QLambdaDefinitionInner parseSyntaxAndInstructions(String script, QLOptions qlOptions) {
        Program program = parseToSyntaxTree(script, qlOptions);
        if (qlOptions.isDebug()) {
            qlOptions.getDebugInfoConsumer().accept("\nAST:");
            AstPrinter astPrinter = new AstPrinter(qlOptions.getDebugInfoConsumer());
            program.accept(astPrinter, null);
        }

        QvmInstructionGenerator qvmInstructionGenerator = new QvmInstructionGenerator(operatorManager, "", script);
        program.accept(qvmInstructionGenerator, new GeneratorScope(null));

        QLambdaDefinitionInner mainLambdaDefine = new QLambdaDefinitionInner("main",
                qvmInstructionGenerator.getInstructionList(), Collections.emptyList(),
                qvmInstructionGenerator.getMaxStackSize());
        return mainLambdaDefine;
    }

    /**
     * 预热接口
     *
     * @param script
     * @param qlOptions
     * @return
     */
    public void checkSyntax(String script, QLOptions qlOptions) {
        parseSyntaxAndInstructions(script, qlOptions);
    }
}
