package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExportItem;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.instruction.detail.InstructionGoToWithCondition;
import com.ql.util.express.instruction.detail.InstructionGoToWithNotNull;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.detail.InstructionReturn;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.parse.ExpressNode;

class OperatorInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner expressRunner, InstructionSet result,
        Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot) throws Exception {
        boolean returnVal = false;
        ExpressNode[] children = node.getChildrenArray();
        int[] finishPoint = new int[children.length];
        for (int i = 0; i < children.length; i++) {
            ExpressNode tmpNode = children[i];
            boolean tmpHas = expressRunner.createInstructionSetPrivate(result, forStack, tmpNode, false);
            returnVal = returnVal || tmpHas;
            finishPoint[i] = result.getCurrentPoint();
        }

        if (node.isTypeEqualsOrChild("return")) {
            result.addInstruction(new InstructionReturn(children.length > 0).setLine(node.getLine()));
        } else {
            OperatorBase op = expressRunner.getOperatorFactory().newInstance(node);
            result.addInstruction(new InstructionOperator(op, children.length).setLine(node.getLine())
                .setLine(node.getLine()));
            if (node.isTypeEqualsOrChild("&&") && expressRunner.isShortCircuit()) {
                result.insertInstruction(finishPoint[0] + 1,
                    new InstructionGoToWithCondition(false, result.getCurrentPoint() - finishPoint[0] + 1,
                        false).setLine(node.getLine()));
            } else if (node.isTypeEqualsOrChild("||") && expressRunner.isShortCircuit()) {
                result.insertInstruction(finishPoint[0] + 1,
                    new InstructionGoToWithCondition(true, result.getCurrentPoint() - finishPoint[0] + 1,
                        false).setLine(node.getLine()));
            } else if (node.isTypeEqualsOrChild("nor")) {
                result.insertInstruction(finishPoint[0] + 1,
                    new InstructionGoToWithNotNull(result.getCurrentPoint() - finishPoint[0] + 1, false).setLine(
                        node.getLine()));
            } else if (node.isTypeEqualsOrChild("def") || node.isTypeEqualsOrChild("alias")) {
                returnVal = true;
            } else if (node.isTypeEqualsOrChild("exportDef")) {
                //添加对外的变量声明
                result.addExportDef(new ExportItem(children[1].toString(), ExportItem.TYPE_DEF, "还没有实现"));
            } else if (node.isTypeEqualsOrChild("exportAlias")) {
                result.addExportDef(new ExportItem(children[0].toString(), ExportItem.TYPE_ALIAS, "还没有实现"));
            }
        }
        return returnVal;
    }
}

