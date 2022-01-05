package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.detail.InstructionCallMacro;
import com.ql.util.express.instruction.detail.InstructionLoadAttr;
import com.ql.util.express.parse.ExpressNode;

public class LoadAttrInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner expressRunner, InstructionSet result,
        Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot) throws Exception {
        FunctionInstructionSet functionSet = result.getMacroDefine(node.getValue());
        if (functionSet != null) {
            //是宏定义
            result.insertInstruction(result.getCurrentPoint() + 1, new InstructionCallMacro(node.getValue()).setLine(
                node.getLine()).setLine(node.getLine()));
        } else {
            result.addInstruction(new InstructionLoadAttr(node.getValue()).setLine(node.getLine()));
            if (node.getChildrenArray().length > 0) {
                throw new QLException("表达式设置错误");
            }
        }
        return false;
    }
}
