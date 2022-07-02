package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.parser.*;
import com.alibaba.qlexpress4.parser.Scanner;
import com.alibaba.qlexpress4.parser.tree.Program;
import com.alibaba.qlexpress4.runtime.*;
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
        CacheUtil.initCache(10, initOptions.enableUseCacheClear());
    }

    public Object execute(String script, Map<String, Value> context, QLOptions qlOptions) throws QLException {
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

    private QLambda parseToLambda(String script, Map<String, Value> context, QLOptions qlOptions) {
        Program program = parseToSyntaxTree(script, qlOptions);
        if (qlOptions.isDebug()) {
            qlOptions.getDebugInfoConsumer().accept("\nAST:");
            AstPrinter astPrinter = new AstPrinter(qlOptions.getDebugInfoConsumer());
            program.accept(astPrinter, null);
        }

        QvmInstructionGenerator qvmInstructionGenerator = new QvmInstructionGenerator("", script);
        program.accept(qvmInstructionGenerator, new GeneratorScope(null));
        QvmRuntime rootRuntime = new QvmRuntime(null,
                qlOptions.isPolluteUserContext()? context: new HashMap<>(context),
                qvmInstructionGenerator.getMaxStackSize(), System.currentTimeMillis());

        QLambdaDefinitionInner mainLambdaDefine = new QLambdaDefinitionInner("main",
                qvmInstructionGenerator.getInstructionList(), Collections.emptyList(),
                qvmInstructionGenerator.getMaxStackSize());
        if (qlOptions.isDebug()) {
            qlOptions.getDebugInfoConsumer().accept("\nInstructions:");
            mainLambdaDefine.println(0, qlOptions.getDebugInfoConsumer());
        }
        return mainLambdaDefine.toLambda(rootRuntime, qlOptions, false);
    }
}
