package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.exception.QLCompileException;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.op.OperatorField;
import com.ql.util.express.parse.ExpressNode;

public class FieldCallInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner expressRunner, InstructionSet result,
        Stack<ForRelBreakContinue> forStack,
        ExpressNode node, boolean isRoot) throws Exception {
        ExpressNode[] children = node.getChildrenArray();

        //处理对象
        boolean returnValue = expressRunner.createInstructionSetPrivate(result, forStack, children[0], false);

        //处理属性名称
        if (!"CONST_STRING".equalsIgnoreCase(children[1].getNodeType().getName())) {
            throw new QLCompileException("对象属性名称不是字符串常量:" + children[1]);
        }

        String fieldName = (String)children[1].getObjectValue();

        OperatorBase op = new OperatorField(fieldName);
        result.addInstruction(new InstructionOperator(op, 1).setLine(node.getLine()));
        return returnValue;
    }
}
