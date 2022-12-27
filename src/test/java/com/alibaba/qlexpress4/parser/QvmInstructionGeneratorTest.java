package com.alibaba.qlexpress4.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import com.alibaba.qlexpress4.DefaultClassSupplier;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.parser.tree.Program;
import com.alibaba.qlexpress4.runtime.instruction.ConstInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewArrayInstruction;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;
import com.alibaba.qlexpress4.runtime.instruction.ReturnInstruction;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class QvmInstructionGeneratorTest {

    @Test
    public void mapSetGet() {
        QvmInstructionGenerator qvmInstructionGenerator = generateInstructions("m['l'][3]");
        assertEquals(2, qvmInstructionGenerator.getMaxStackSize());

        QvmInstructionGenerator qvmInstructionGenerator1 = generateInstructions(
            "Map<String, Object> m = new HashMap<>();\n" +
                "m['l'] = [1,2,3,4];\n" +
                "m['l'][2]");
        assertEquals(5, qvmInstructionGenerator1.getMaxStackSize());
    }

    @Test
    public void newArrayOpt() {
        QvmInstructionGenerator qvmInstructionGenerator = generateInstructions("int[] a = [];");
        List<QLInstruction> instructions = qvmInstructionGenerator.getInstructionList();
        assertTrue(instructions.get(0) instanceof NewArrayInstruction);
    }

    @Test
    public void ifTest() {
        QvmInstructionGenerator qvmInstructionGenerator = generateInstructions("if (a==3) {return 1;}");
        List<QLInstruction> instructionList = qvmInstructionGenerator.getInstructionList();
        int returnIndex = findIndex(instructionList, qlInstruction -> qlInstruction instanceof ReturnInstruction);
        assertTrue(returnIndex != -1);
        assertFalse(instructionList.get(returnIndex + 1) instanceof ConstInstruction);
    }

    private int findIndex(List<QLInstruction> instructionList, Predicate<QLInstruction> test) {
        for (int i = 0; i < instructionList.size(); i++) {
            if (test.test(instructionList.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public QvmInstructionGenerator generateInstructions(String script) {
        Program program = new QLParser(Collections.emptyMap(), new Scanner(script, QLOptions.DEFAULT_OPTIONS),
            ImportManager.buildGlobalImportManager(Arrays.asList(
                ImportManager.importPack("java.lang"),
                ImportManager.importPack("java.util"),
                ImportManager.importCls("java.util.function.Function")
            )), DefaultClassSupplier.INSTANCE).parse();
        OperatorManager operatorManager = new OperatorManager();
        QvmInstructionGenerator qvmInstructionGenerator = new QvmInstructionGenerator(operatorManager, "", script);
        program.accept(qvmInstructionGenerator, new GeneratorScope(null));
        return qvmInstructionGenerator;
    }

}