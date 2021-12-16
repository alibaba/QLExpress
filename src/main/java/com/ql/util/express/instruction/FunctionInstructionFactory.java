package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;
import com.ql.util.express.parse.ExpressNode;

public class FunctionInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner expressRunner, InstructionSet result,
        Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot)
        throws Exception {
        ExpressNode[] children = node.getChildrenArray();
        if (children.length != 3) {
            throw new QLException("function 操作符需要3个操作数 ");
        }
        String functionName = children[0].getValue();
        ExpressNode[] varDefines = children[1].getChildrenArray();
        int point = 0;

        String instructionSetType;
        if (node.isTypeEqualsOrChild("class")) {
            instructionSetType = InstructionSet.TYPE_CLASS;
        } else {
            instructionSetType = InstructionSet.TYPE_FUNCTION;
        }
        InstructionSet functionSet = new InstructionSet(instructionSetType);

        while (point < varDefines.length) {
            if (!varDefines[point].isTypeEqualsOrChild("def")) {
                throw new QLException("function的参数定义错误," + varDefines[point] + "不是一个Class");
            }
            Class<?> varClass = (Class<?>)varDefines[point].getChildrenArray()[0].getObjectValue();
            String varName = varDefines[point].getChildrenArray()[1].getValue();
            OperateDataLocalVar tmpVar = new OperateDataLocalVar(varName, varClass);
            functionSet.addParameter(tmpVar);
            point = point + 1;
        }

        ExpressNode functionRoot = new ExpressNode(expressRunner.getNodeTypeManager().findNodeType("FUNCTION_DEFINE"),
            "function-" + functionName);
        for (ExpressNode expressNode : children[2].getChildrenArray()) {
            functionRoot.addChild(expressNode);
        }
        expressRunner.createInstructionSet(functionRoot, functionSet);
        result.addMacroDefine(functionName, new FunctionInstructionSet(functionName, instructionSetType, functionSet));
        return false;
    }
}
