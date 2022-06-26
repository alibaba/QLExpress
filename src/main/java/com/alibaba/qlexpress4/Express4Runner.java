package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.parser.*;
import com.alibaba.qlexpress4.parser.Scanner;
import com.alibaba.qlexpress4.parser.tree.Program;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: DQinYuan
 * date 2022/1/12 2:28 下午
 */
public class Express4Runner {

    // TODO: bingou OperatorManager?
    private Map<String, BinaryOperator> userDefineOperator;

    public Object execute(String script, Map<String, Value> context, QLOptions qlOptions) throws Exception {
        Program program = parseSyntaxTree(script, qlOptions);

        QvmInstructionGenerator qvmInstructionGenerator = new QvmInstructionGenerator("", script);
        program.accept(qvmInstructionGenerator, new GeneratorScope(null));
        QvmRuntime rootRuntime = new QvmRuntime(null, context, qvmInstructionGenerator.getMaxStackSize(),
                System.currentTimeMillis());

        QLambdaDefinition mainLambdaDefine = new QLambdaDefinition("main",
                qvmInstructionGenerator.getInstructionList(), Collections.emptyList(),
                qvmInstructionGenerator.getMaxStackSize());
        QLambda mainLambda = mainLambdaDefine.toLambda(rootRuntime, qlOptions, false);
        return mainLambda.call().getResult().get();
    }

    public Program parseSyntaxTree(String script, QLOptions qlOptions) {
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
}
