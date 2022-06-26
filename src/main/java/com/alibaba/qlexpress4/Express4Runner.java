package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.cache.*;
import com.alibaba.qlexpress4.parser.*;
import com.alibaba.qlexpress4.parser.Scanner;
import com.alibaba.qlexpress4.parser.tree.Program;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.utils.CacheUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: DQinYuan
 * date 2022/1/12 2:28 下午
 */
public class Express4Runner {

    // TODO: bingou OperatorManager?
    private Map<String, BinaryOperator> userDefineOperator = Collections.emptyMap();

    public Express4Runner(InitOptions initOptions) {
        QLConstructorCache qlConstructorCache = CacheUtil.initConstructorCache(10, initOptions.enableUseCacheClear());
        QLFunctionCache qlFunctionCache = CacheUtil.initFunctionCache(10, initOptions.enableUseCacheClear());
        QLScriptCache qlScriptCache = CacheUtil.initScriptCache(10, initOptions.enableUseCacheClear());
        QLFieldCache qlFieldCache = CacheUtil.initFieldCache(10, initOptions.enableUseCacheClear());
        QLMethodCache qlMethodCache = CacheUtil.initMethodCache(10, initOptions.enableUseCacheClear());
        QLMethodInvokeCache qlMethodInvokeCache = CacheUtil.initMethodInvokeCache(10, initOptions.enableUseCacheClear());
    }

    public Object execute(String script, Map<String, Value> context, QLOptions qlOptions) throws Exception {
        QLambda mainLambda = parseToLambda(script, context, qlOptions);
        return mainLambda.call().getResult().get();
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

    public QLambda parseToLambda(String script, Map<String, Value> context, QLOptions qlOptions) {
        Program program = parseToSyntaxTree(script, qlOptions);

        QvmInstructionGenerator qvmInstructionGenerator = new QvmInstructionGenerator("", script);
        program.accept(qvmInstructionGenerator, new GeneratorScope(null));
        QvmRuntime rootRuntime = new QvmRuntime(null,
                qlOptions.isPolluteUserContext()? context: new HashMap<>(context),
                qvmInstructionGenerator.getMaxStackSize(), System.currentTimeMillis());

        QLambdaDefinition mainLambdaDefine = new QLambdaDefinition("main",
                qvmInstructionGenerator.getInstructionList(), Collections.emptyList(),
                qvmInstructionGenerator.getMaxStackSize());
        return mainLambdaDefine.toLambda(rootRuntime, qlOptions, false);
    }
}
