package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.runtime.MetaClass;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.instruction.*;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MultiplyOperator;
import com.alibaba.qlexpress4.runtime.operator.assign.AssignOperator;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class SyntaxTreeFactoryTest {

    @Test
    public void visitPathExprTestWhenMix() {
        String script = "java.util.function.Function.a.b.cc()";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        assertEquals(6, instructions.size());
        ConstInstruction constInstruction = (ConstInstruction) instructions.get(0);
        MetaClass metaClass = (MetaClass) constInstruction.getConstObj();
        assertEquals(Function.class, metaClass.getClz());
        GetFieldInstruction getFieldInstruction1 = (GetFieldInstruction) instructions.get(1);
        assertEquals("a", getFieldInstruction1.getFieldName());
        GetFieldInstruction getFieldInstruction2 = (GetFieldInstruction) instructions.get(2);
        assertEquals("b", getFieldInstruction2.getFieldName());
        MethodInvokeInstruction methodInvokeInstruction = (MethodInvokeInstruction) instructions.get(3);
        assertEquals("cc", methodInvokeInstruction.getMethodName());
    }

    @Test
    public void visitPathExprTestWhenInnerCls() {
        String script = "com.alibaba.qlexpress4.aparser.ImportManagerTest.TestImportInner.TestImportInner2.pp[m]";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        assertEquals(5, instructions.size());
        ConstInstruction constInstruction = (ConstInstruction) instructions.get(0);
        MetaClass metaClass = (MetaClass) constInstruction.getConstObj();
        assertEquals(ImportManagerTest.TestImportInner.TestImportInner2.class, metaClass.getClz());
        GetFieldInstruction getFieldInstruction = (GetFieldInstruction) instructions.get(1);
        assertEquals("pp", getFieldInstruction.getFieldName());
        LoadInstruction loadInstruction = (LoadInstruction) instructions.get(2);
        assertEquals("m", loadInstruction.getName());
        assertTrue(instructions.get(3) instanceof IndexInstruction);
    }

    @Test
    public void visitCallTest() {
        String script = "call(mm)";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        assertEquals(4, instructions.size());
        LoadInstruction loadInstruction = (LoadInstruction) instructions.get(0);
        assertEquals("mm", loadInstruction.getName());
    }

    @Test
    public void numberTest() {
        String script = "10_0_0l";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        assertEquals(2, instructions.size());
        ConstInstruction constInstruction = (ConstInstruction) instructions.get(0);
        assertEquals(1000L, constInstruction.getConstObj());
    }

    @Test
    public void macroDefineTest() {
        String script = "macro add {a+b} add;int c = 10;";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        assertEquals(7, instructions.size());
    }

    @Test
    public void stringEscapeTest() {
        // invalid escape \p will be ignored
        String script = "\"\\r\\n\\p\"";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        ConstInstruction qlInstruction = (ConstInstruction) instructions.get(0);
        String constObj = (String) qlInstruction.getConstObj();
        assertEquals("\r\n", constObj);
    }

    @Test
    public void castTest() {
        String script = "1+(int)3L";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        assertTrue(instructions.get(3) instanceof CastInstruction);
    }

    @Test
    public void cusOpTest() {
        String script = "c.*d";
        getScriptInstructions(script, InterpolationMode.VARIABLE);

        String script1 = "c>>>d";
        getScriptInstructions(script1, InterpolationMode.VARIABLE);
    }

    @Test
    public void pathPartTest() {
        String script = "assert((java.lang.Object) a == 1)";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        ConstInstruction constInstruction = (ConstInstruction) instructions.get(0);
        assertEquals(Object.class, constInstruction.getConstObj());
        LoadInstruction loadInstruction = (LoadInstruction) instructions.get(1);
        assertEquals("a", loadInstruction.getName());
        assertTrue(instructions.get(2) instanceof CastInstruction);
    }

    @Test
    public void fieldExpressionTest() {
        String script = "\"null\".equals(b)";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        assertTrue(instructions.get(0) instanceof ConstInstruction);
    }

    @Test
    public void lambdaExprBodyTest() {
        String script = "f = e -> try {\n" +
                "  throw e;\n" +
                "} catch (java.lang.NullPointerException n) {\n" +
                "  100\n" +
                "} catch (java.lang.Exception e) {\n" +
                "  10\n" +
                "};";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        LoadLambdaInstruction loadLambdaInstruction = (LoadLambdaInstruction) instructions.get(1);
        QLambdaDefinitionInner lambdaDefinition = (QLambdaDefinitionInner) loadLambdaInstruction.getLambdaDefinition();
        assertEquals(2, lambdaDefinition.getInstructions().length);
    }

    @Test
    public void lambdaBlockBodyTest() {
        String script = "f = e -> {10};";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        LoadLambdaInstruction loadLambdaInstruction = (LoadLambdaInstruction) instructions.get(1);
        QLambdaDefinitionInner lambdaDefinition = (QLambdaDefinitionInner) loadLambdaInstruction.getLambdaDefinition();
        assertEquals(2, lambdaDefinition.getInstructions().length);
        assertTrue(lambdaDefinition.getInstructions()[0] instanceof ConstInstruction);
    }

    @Test
    public void lambdaMapBodyTest() {
        String script = "f = e -> {'test': 1234};";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        LoadLambdaInstruction loadLambdaInstruction = (LoadLambdaInstruction) instructions.get(1);
        QLambdaDefinitionInner lambdaDefinition = (QLambdaDefinitionInner) loadLambdaInstruction.getLambdaDefinition();
        assertEquals(3, lambdaDefinition.getInstructions().length);
    }

    @Test
    public void newArrayTest() {
        String script = "new int[][] {new int[] {1,2}, new int[] {3,4}}";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        NewArrayInstruction newArrayInstruction = (NewArrayInstruction) instructions.get(instructions.size() - 2);
        assertEquals(Integer[].class, newArrayInstruction.getClz());
    }

    @Test
    public void instanceOfTest() {
        String script = "1 instanceof int";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        ConstInstruction constInstruction = (ConstInstruction) instructions.get(1);
        MetaClass constMeta = (MetaClass) constInstruction.getConstObj();
        assertEquals(Integer.class, constMeta.getClz());
    }

    @Test
    public void instanceOfStrArrTest() {
        String script = "1 instanceof java.lang.String[][][]";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        ConstInstruction constInstruction = (ConstInstruction) instructions.get(1);
        MetaClass constMeta = (MetaClass) constInstruction.getConstObj();
        assertEquals(String[][][].class, constMeta.getClz());
    }

    @Test
    public void instanceOfIntArrTest() {
        String script = "1 instanceof int[][][]";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        ConstInstruction constInstruction = (ConstInstruction) instructions.get(1);
        MetaClass constMeta = (MetaClass) constInstruction.getConstObj();
        assertEquals(Integer[][][].class, constMeta.getClz());
    }

    @Test
    public void bitOperatorTest() {
        assertOperator("true & true", "&");
        assertOperator("true | true", "|");
        assertOperator("2 % 3", "%");
    }

    private void assertOperator(String script, String expectOp) {
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        OperatorInstruction operatorInstruction = (OperatorInstruction) instructions.get(2);
        assertEquals(expectOp, operatorInstruction.getOperator().getOperator());
    }

    @Test
    public void opPrecedencesTest() {
        String script = "a = 1+2*3+10";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        OperatorInstruction operatorMulti = (OperatorInstruction) instructions.get(4);
        assertTrue(operatorMulti.getOperator() instanceof MultiplyOperator);
        OperatorInstruction operatorAssign = (OperatorInstruction) instructions.get(9);
        assertTrue(operatorAssign.getOperator() instanceof AssignOperator);
    }

    @Test
    public void ternaryTest() {
        String script = "l = (x) -> x > 10 ? 11 : 100";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        LoadInstruction loadInstruction = (LoadInstruction) instructions.get(0);
        assertEquals("l", loadInstruction.getName());
        assertTrue(instructions.get(1) instanceof LoadLambdaInstruction);
        OperatorInstruction operatorInstruction = (OperatorInstruction) instructions.get(2);
        assertTrue(operatorInstruction.getOperator() instanceof AssignOperator);
    }

    @Test
    public void functionInterfaceTest() {
        String script = "java.lang.Runnable r = () -> a = 8;";
        getScriptInstructions(script, InterpolationMode.VARIABLE);
    }

    @Test
    public void groupPriorityTest()  {
        String script = "a.*b.*c[2]+d.*e[1:2]";
        getScriptInstructions(script, InterpolationMode.VARIABLE);
    }

    @Test
    public void numberAmbiguousValueTest() {
        String script = "1.doubleValue()";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        assertEquals("doubleValue", ((MethodInvokeInstruction) instructions.get(1)).getMethodName());
    }

    @Test
    public void classifiedJsonTest() {
        String script = "{'@class':'java.lang.Object', 'a': 'cccc'}";
        List<QLInstruction> instructions = getScriptInstructions(script, InterpolationMode.VARIABLE);
        assertTrue(instructions.get(1) instanceof NewFilledInstanceInstruction);
    }

    @Test
    public void selectorTest() {
        List<QLInstruction> instructions = getScriptInstructions("${ TextField-AXXE } + ${v231}", InterpolationMode.SCRIPT);
        LoadInstruction loadInstruction0 = (LoadInstruction) instructions.get(0);
        assertEquals("TextField-AXXE", loadInstruction0.getName());
        LoadInstruction loadInstruction1 = (LoadInstruction) instructions.get(1);
        assertEquals("v231", loadInstruction1.getName());

        List<QLInstruction> instructions1 = getScriptInstructions("${ TextField-A} + ${v2}", InterpolationMode.VARIABLE);
        LoadInstruction loadInstruction10 = (LoadInstruction) instructions1.get(0);
        assertEquals("TextField-A", loadInstruction10.getName());
        LoadInstruction loadInstruction11 = (LoadInstruction) instructions1.get(1);
        assertEquals("v2", loadInstruction11.getName());
    }

    @Test
    public void doubleQuoteStringScriptTest() {
        List<QLInstruction> instructs = getScriptInstructions("\"a ${v-1}\"", InterpolationMode.SCRIPT);
        ConstInstruction i0 = (ConstInstruction) instructs.get(0);
        assertEquals("a ", i0.getConstObj());
        LoadInstruction i1 = (LoadInstruction) instructs.get(1);
        assertEquals("v", i1.getName());
        ConstInstruction i2 = (ConstInstruction) instructs.get(2);
        assertEquals(1, i2.getConstObj());
        OperatorInstruction i3 = (OperatorInstruction) instructs.get(3);
        assertEquals("-", i3.getOperator().getOperator());
        StringJoinInstruction i4 = (StringJoinInstruction) instructs.get(4);
        assertEquals(2, i4.getN());
    }

    @Test
    public void doubleQuoteStringScriptTest2() {
        getScriptInstructions("\"Hello ${a} ccc\"", InterpolationMode.SCRIPT);
    }

    @Test
    public void doubleQuoteStringVariableTest() {
        List<QLInstruction> instructions = getScriptInstructions("\"a ${ v-1 } b\"", InterpolationMode.VARIABLE);
        ConstInstruction i0 = (ConstInstruction) instructions.get(0);
        assertEquals("a ", i0.getConstObj());
        LoadInstruction i1 = (LoadInstruction) instructions.get(1);
        assertEquals("v-1", i1.getName());
        ConstInstruction i2 = (ConstInstruction) instructions.get(2);
        assertEquals(" b", i2.getConstObj());
        StringJoinInstruction i3 = (StringJoinInstruction) instructions.get(3);
        assertEquals(3, i3.getN());
    }

    @Test
    public void ifTest() {
        List<QLInstruction> instructions = getScriptInstructions("if (a > 0 && a < 5) {\n" +
                "  true\n" +
                "} else if (a > 5 && a < 10) {\n" +
                "  false\n" +
                "} else if (a > 10 && a < 15) {\n" +
                "  true\n" +
                "} == true", InterpolationMode.VARIABLE);
        OperatorInstruction equalInstruction = (OperatorInstruction) instructions.get(instructions.size() - 2);
        assertEquals("==", equalInstruction.getOperator().getOperator());
    }

    private List<QLInstruction> getScriptInstructions(String script, InterpolationMode interpolationMode) {
        QLParser.ProgramContext programContext = SyntaxTreeFactory.buildTree(script,
                new MockOpM(), true, false, System.out::println, interpolationMode, "${", "}");
        QvmInstructionVisitor visitor = new QvmInstructionVisitor(script);
        programContext.accept(visitor);
        return visitor.getInstructions();
    }
}