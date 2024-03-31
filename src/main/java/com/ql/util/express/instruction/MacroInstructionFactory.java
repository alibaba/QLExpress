package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.parse.ExpressNode;

public class MacroInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner expressRunner, InstructionSet result,
        Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot) throws Exception {
        ExpressNode[] children = node.getChildrenArray();
        String macroName = children[0].getValue();
        ExpressNode macroRoot = new ExpressNode(expressRunner.getNodeTypeManager().findNodeType("FUNCTION_DEFINE"),
            "macro-" + macroName);
        for (ExpressNode tempNode : children[1].getChildrenArray()) {
            macroRoot.addChild(tempNode);
        }
        InstructionSet macroInstructionSet = expressRunner.createInstructionSet(macroRoot, InstructionSet.TYPE_MACRO);
        result.addMacroDefine(macroName, new FunctionInstructionSet(macroName, "macro", macroInstructionSet));
        return false;
    }
}
