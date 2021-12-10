package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.parse.ExpressNode;

public class MacroInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner aCompile, InstructionSet result,
        Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
        throws Exception {
        ExpressNode[] children = node.getChildren();
        String macroName = children[0].getValue();
        ExpressNode macroRoot = new ExpressNode(aCompile.getNodeTypeManager().findNodeType("FUNCTION_DEFINE"),
            "macro-" + macroName);
        for (ExpressNode tempNode : children[1].getChildren()) {
            macroRoot.addLeftChild(tempNode);
        }
        InstructionSet macroInstructionSet = aCompile.createInstructionSet(macroRoot, InstructionSet.TYPE_MARCO);
        result.addMacroDefine(macroName, new FunctionInstructionSet(macroName, "macro", macroInstructionSet));
        return false;
    }
}
