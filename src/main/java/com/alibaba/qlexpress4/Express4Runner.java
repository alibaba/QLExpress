package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.cache.*;
import com.alibaba.qlexpress4.parser.*;
import com.alibaba.qlexpress4.parser.Scanner;
import com.alibaba.qlexpress4.parser.tree.Program;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.data.lambda.*;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.utils.*;

import org.apache.commons.lang.exception.ExceptionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Author: DQinYuan
 * date 2022/1/12 2:28 下午
 */
public class Express4Runner {
    private final OperatorManager operatorManager = new OperatorManager();

    private final Map<String, QFunction> userDefineFunction = new ConcurrentHashMap<>();
    private final Map<String, QFunction> userDefineField = new ConcurrentHashMap<>();

    public Express4Runner(InitOptions initOptions) {
        QLConstructorCache qlConstructorCache = CacheUtil.initConstructorCache(10, initOptions.enableUseCacheClear());
        QLScriptCache qlScriptCache = CacheUtil.initScriptCache(10, initOptions.enableUseCacheClear());
        QLFieldCache qlFieldCache = CacheUtil.initFieldCache(10, initOptions.enableUseCacheClear());
        QLMethodCache qlMethodCache = CacheUtil.initMethodCache(10, initOptions.enableUseCacheClear());
        QLMethodInvokeCache qlMethodInvokeCache = CacheUtil.initMethodInvokeCache(10,
            initOptions.enableUseCacheClear());
    }

    public Object execute(String script, Map<String, Object> context, QLOptions qlOptions) throws QLException {
        QLambda mainLambda = parseToLambda(script, context, qlOptions);
        try {
            return mainLambda.call().getResult().get();
        } catch (QLException e) {
            throw e;
        } catch (Exception nuKnown) {
            // should not run here
            throw new RuntimeException(nuKnown);
        }
    }

    public void addFunction(String name, QFunction function) {
        userDefineFunction.put(name, function);
    }

    public <T, R> void addFunction(String name, Function<T, R> function) {
        addFunction(name, new QFunctionInner(params -> {
            R result = function.apply((T)params[0]);
            return new QResult(new DataValue(result, result.getClass()), QResult.ResultType.RETURN);
        }));
    }

    public <T, R> void addFunction(String name, QLFunctionalVarargs<T, R> functionalVarargs, Class<?> type) {
        addFunction(name, new QFunctionInner(params -> {
            Object array = Array.newInstance(type, params.length);
            for (int i = 0; i < params.length; i++) {
                Array.set(array, i, params[i]);
            }
            R result = functionalVarargs.call((T[])array);
            return new QResult(new DataValue(result, result.getClass()), QResult.ResultType.RETURN);
        }));
    }

    public <T> void addFunction(String name, Predicate<T> predicate) {
        addFunction(name, new QFunctionInner(
            params -> new QResult(new DataValue(predicate.test((T)params[0])), QResult.ResultType.RETURN)));
    }

    public void addFunction(String name, Runnable runnable) {
        addFunction(name, new QFunctionInner(params -> {
            runnable.run();
            return new QResult(null, QResult.ResultType.RETURN);
        }));
    }

    public <T> void addFunction(String name, Consumer<T> consumer) {
        addFunction(name, new QFunctionInner(params -> {
            consumer.accept((T)params[0]);
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
        if (obj instanceof Class) {
            addFunctionByClass(name, (Class<?>)obj, methodName);
        } else {
            addFunctionByObject(name, obj, methodName);
        }
    }

    private void addFunctionByObject(String name, Object object, String methodName) {
        List<Method> methods = PropertiesUtil.getMethod(object.getClass(), methodName, false);
        addFunction(name, new QFunctionInner(new QLambdaMethod(methods, object, false)));
    }

    private void addFunctionByClass(String name, Class<?> clazz, String methodName) {
        List<Method> methods = PropertiesUtil.getClzMethod(clazz, methodName, false);
        addFunction(name, new QFunctionInner(new QLambdaMethod(methods, clazz, false)));
    }

    private void addFunctionByAnnotation(Class<?> clazz, Object object) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (QLFunctionUtil.containsQLFunctionForMethod(method)) {
                for (String value : QLFunctionUtil.getQLFunctionValue(method)) {
                    List<Method> qlMethods = new ArrayList<>();
                    qlMethods.add(method);
                    addFunction(value, new QFunctionInner(new QLambdaMethod(qlMethods, object, false)));
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
                    addField(value, new QFunctionInner(new QLambdaMethod(qlMethods, object, false)));
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

    private QLambda parseToLambda(String script, Map<String, Object> context, QLOptions qlOptions) {
        Program program = parseToSyntaxTree(script, qlOptions);
        if (qlOptions.isDebug()) {
            qlOptions.getDebugInfoConsumer().accept("\nAST:");
            AstPrinter astPrinter = new AstPrinter(qlOptions.getDebugInfoConsumer());
            program.accept(astPrinter, null);
        }

        QvmInstructionGenerator qvmInstructionGenerator = new QvmInstructionGenerator("", script);
        program.accept(qvmInstructionGenerator, new GeneratorScope(null));
        QRuntime rootRuntime = new QvmRootRuntime(context, userDefineFunction,
            qlOptions.getAttachments(), qlOptions.isPolluteUserContext(), System.currentTimeMillis());

        QLambdaDefinitionInner mainLambdaDefine = new QLambdaDefinitionInner("main",
            qvmInstructionGenerator.getInstructionList(), Collections.emptyList(),
            qvmInstructionGenerator.getMaxStackSize());
        if (qlOptions.isDebug()) {
            qlOptions.getDebugInfoConsumer().accept("\nInstructions:");
            mainLambdaDefine.println(0, qlOptions.getDebugInfoConsumer());
        }
        return mainLambdaDefine.toLambda(rootRuntime, qlOptions, true);
    }
}
