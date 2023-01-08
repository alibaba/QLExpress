package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.DefaultClassSupplier;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.parser.tree.Program;
import com.alibaba.qlexpress4.runtime.instruction.ConstInstruction;
import com.alibaba.qlexpress4.runtime.instruction.NewArrayInstruction;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;
import com.alibaba.qlexpress4.runtime.instruction.ReturnInstruction;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class QvmInstructionGeneratorV2Test {

    @Test
    public void mapSetGet() {
        VisitorInstructions visitorInstructions = generateInstructions("m['l'][3]");
        assertEquals(2, visitorInstructions.visitor.getMaxStackSize());

        VisitorInstructions visitorInstructions2 =
                generateInstructions("Map<String, Object> m = new HashMap<>();\n" +
                        "m['l'] = [1,2,3,4];\n" +
                        "m['l'][2]");
        assertEquals(5, visitorInstructions2.visitor.getMaxStackSize());
    }

    @Test
    public void newArrayOpt() {
        VisitorInstructions visitorInstructions = generateInstructions("int[] a = [];");
        assertTrue(visitorInstructions.instructions[0] instanceof NewArrayInstruction);
    }

    @Test
    public void ifTest() {
        VisitorInstructions visitorInstructions = generateInstructions("if (a==3) {return 1;}");
        int returnIndex = findIndex(visitorInstructions.instructions,
                qlInstruction -> qlInstruction instanceof ReturnInstruction);
        assertTrue(returnIndex != -1);
        assertFalse(visitorInstructions.instructions[returnIndex + 1] instanceof ConstInstruction);
    }

    public VisitorInstructions generateInstructions(String script) {
        Program program = new QLParser(Collections.emptyMap(), new Scanner(script, QLOptions.DEFAULT_OPTIONS),
                ImportManager.buildGlobalImportManager(Arrays.asList(
                        ImportManager.importPack("java.lang"),
                        ImportManager.importPack("java.util"),
                        ImportManager.importCls("java.util.function.Function")
                )), DefaultClassSupplier.INSTANCE).parse();
        OperatorManager operatorManager = new OperatorManager();
        QvmInstructionGeneratorV2 visitor = new QvmInstructionGeneratorV2(script, operatorManager,
                new GeneratorScopeV2(null, GeneratorScopeV2.ScopeType.FUNCTION), "ROOT");
        QList<QLInstruction> instructions = program.accept(visitor, null);
        return new VisitorInstructions(instructions.toArray(
                new QLInstruction[visitor.getInstructionSize()]), visitor);
    }

    private static class VisitorInstructions {
        QLInstruction[] instructions;
        QvmInstructionGeneratorV2 visitor;

        public VisitorInstructions(QLInstruction[] instructions, QvmInstructionGeneratorV2 visitor) {
            this.instructions = instructions;
            this.visitor = visitor;
        }
    }

    private int findIndex(QLInstruction[] instructions, Predicate<QLInstruction> test) {
        for (int i = 0; i < instructions.length; i++) {
            if (test.test(instructions[i])) {
                return i;
            }
        }
        return -1;
    }
}