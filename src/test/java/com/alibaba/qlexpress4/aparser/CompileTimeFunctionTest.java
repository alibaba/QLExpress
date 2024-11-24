package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.aparser.compiletimefunction.CodeGenerator;
import com.alibaba.qlexpress4.aparser.compiletimefunction.CompileTimeFunction;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.runtime.*;
import com.alibaba.qlexpress4.runtime.context.MapExpressContext;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import com.alibaba.qlexpress4.runtime.instruction.*;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class CompileTimeFunctionTest {

    public static class ForEachFunction implements CompileTimeFunction {

        private static final String SCOPE_NAME = "FOR_EACH_FUNCTION";

        // load iterator
        // load lambda 第二个参数编译而来
        // load lambda 自定义, 循环 iterator, 调用 lambda 返回结果 list
        // call
        @Override
        public void createFunctionInstruction(String functionName, List<QLParser.ExpressionContext> arguments,
                                              OperatorFactory operatorFactory, CodeGenerator codeGenerator) {
            if (arguments.size() != 2) {
                throw codeGenerator.reportParseErr("INVALID_ARGUMENTS",
                        "FOREACH must hava 2 params, but accept " + arguments.size());
            }

            ErrorReporter functionErrReporter = codeGenerator.getErrorReporter();
            codeGenerator.addInstruction(new NewScopeInstruction(functionErrReporter, SCOPE_NAME));

            // load iterator
            QLParser.ExpressionContext arg0 = arguments.get(0);
            codeGenerator.addInstructionsByTree(arg0);
            codeGenerator.addInstruction(
                    new MethodInvokeInstruction(
                            codeGenerator.newReporterWithToken(arg0.getStart()),
                            "iterator", 0, false)
            );

            // load lambda
            QLParser.ExpressionContext arg1 = arguments.get(1);
            QLambdaDefinition bodyDefinition = codeGenerator.generateLambdaDefinition(arg1,
                    Collections.singletonList(new QLambdaDefinitionInner.Param("_", Object.class))
            );
            codeGenerator.addInstruction(new LoadLambdaInstruction(functionErrReporter, bodyDefinition));

            // custom lambda
            codeGenerator.addInstruction(new CallConstInstruction(functionErrReporter,
                    params -> {
                        Iterator<?> iterator = (Iterator<?>) params[0];
                        QLambda body = (QLambda) params[1];
                        List<Object> result = new ArrayList<>();
                        while (iterator.hasNext()) {
                            Object next = iterator.next();
                            result.add(body.call(next).getResult().get());
                        }
                        return new QResult(new DataValue(result), QResult.ResultType.RETURN);
                    },
                    2, "FOR_EACH"));
        }
    }

    @Test
    public void forEachCompileFunctionTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                .securityStrategy(QLSecurityStrategy.open())
                .build());
        express4Runner.addCompileTimeFunction("FOR_EACH", new ForEachFunction());
        assertNotNull(express4Runner.getCompileTimeFunction("FOR_EACH"));
        Object result = express4Runner.execute("FOR_EACH([1,2,3,4], _+1)", new HashMap<>(),
                QLOptions.DEFAULT_OPTIONS);
        assertEquals(Arrays.asList(2, 3, 4, 5), result);
    }

    public static class GenInstructionNumFunction implements CompileTimeFunction {
        @Override
        public void createFunctionInstruction(String functionName, List<QLParser.ExpressionContext> arguments,
                                              OperatorFactory operatorFactory, CodeGenerator codeGenerator) {
            QLambdaDefinition lambdaDefinition = codeGenerator.generateLambdaDefinition(
                    arguments.get(0), Collections.emptyList()
            );
            assertEquals(2, ((QLambdaDefinitionInner) lambdaDefinition).getInstructions().length);
        }
    }

    @Test
    public void genInstructionNumTest() {
        Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
                .securityStrategy(QLSecurityStrategy.open())
                .build());
        express4Runner.addCompileTimeFunction("GEN_INST_NUM", new GenInstructionNumFunction());
        express4Runner.parseToLambda("GEN_INST_NUM(1)", new MapExpressContext(new HashMap<>()), QLOptions.DEFAULT_OPTIONS);
    }
}