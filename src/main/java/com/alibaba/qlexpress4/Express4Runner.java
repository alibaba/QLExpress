package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.annotation.QLField;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.cache.*;
import com.alibaba.qlexpress4.parser.*;
import com.alibaba.qlexpress4.parser.Scanner;
import com.alibaba.qlexpress4.parser.tree.Program;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.data.lambda.QLambdaMethod;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.utils.CacheUtil;
import com.alibaba.qlexpress4.utils.QLAliasUtil;
import com.alibaba.qlexpress4.utils.QLFieldUtil;
import com.alibaba.qlexpress4.utils.QLFunctionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Author: DQinYuan
 * date 2022/1/12 2:28 下午
 */
public class Express4Runner {

    // TODO: bingou OperatorManager?
    private Map<String, BinaryOperator> userDefineOperator = Collections.emptyMap();

    private final Map<String, QFunction> userDefineFunction = new ConcurrentHashMap<>();
    private final Map<String, QFunction> userDefineField = new ConcurrentHashMap<>();

    public Express4Runner(InitOptions initOptions) {
        QLConstructorCache qlConstructorCache = CacheUtil.initConstructorCache(10, initOptions.enableUseCacheClear());
        QLScriptCache qlScriptCache = CacheUtil.initScriptCache(10, initOptions.enableUseCacheClear());
        QLFieldCache qlFieldCache = CacheUtil.initFieldCache(10, initOptions.enableUseCacheClear());
        QLMethodCache qlMethodCache = CacheUtil.initMethodCache(10, initOptions.enableUseCacheClear());
        QLMethodInvokeCache qlMethodInvokeCache = CacheUtil.initMethodInvokeCache(10, initOptions.enableUseCacheClear());
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

    public void addField(String name, QFunction function) {
        userDefineField.put(name, function);
    }

    public void addFieldByClassAnnotation(Object object) {
        addFieldByAnnotation(object.getClass(),object);
    }

    public void addFieldByObjectAnnotation(Class<?> clazz) {
        addFieldByAnnotation(clazz,clazz);
    }

    public void addFunctionByObjectAnnotation(Object object) {
        addFunctionByAnnotation(object.getClass(), object);
    }

    public void addFunctionByClassAnnotation(Class<?> clazz) {
        addFunctionByAnnotation(clazz, clazz);
    }


    private void addFunctionByAnnotation(Class<?> clazz, Object object){
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (QLFunctionUtil.containsQLFunctionForMethod(method)) {
                for (String value : QLFunctionUtil.getQLFunctionValue(method)) {
                    List<Method> qlMethods = new ArrayList<>();
                    qlMethods.add(method);
                    addFunction(value, new QFunctionInner(new QLambdaMethod(qlMethods,object,true)));
                }
            }
        }
    }

    private void addFieldByAnnotation(Class<?> clazz, Object object){
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (QLFieldUtil.containsQLFieldForMethod(method)) {
                for (String value : QLFieldUtil.getQLFieldValue(method)) {
                    List<Method> qlMethods = new ArrayList<>();
                    qlMethods.add(method);
                    addField(value, new QFunctionInner(new QLambdaMethod(qlMethods,object,true)));
                }
            }
        }
    }

    public Program parseToSyntaxTree(String script, QLOptions qlOptions) {
        Map<String, Integer> userDefineOperatorPrecedence = userDefineOperator.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        en -> en.getValue().getPriority()
                ));

        QLParser qlParser = new QLParser(userDefineOperatorPrecedence, new Scanner(script, qlOptions),
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
