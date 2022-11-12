package com.ql.util.express.instruction;

import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.exception.QLCompileException;
import com.ql.util.express.instruction.detail.InstructionGoTo;
import com.ql.util.express.instruction.detail.InstructionGoToWithCondition;
import com.ql.util.express.parse.ExpressNode;

public class IfInstructionFactory extends InstructionFactory {
    @Override
    public boolean createInstruction(ExpressRunner expressRunner, InstructionSet result,
        Stack<ForRelBreakContinue> forStack, ExpressNode node, boolean isRoot) throws Exception {
        ExpressNode[] oldChildren = node.getChildrenArray();
        if (oldChildren.length < 2) {
            throw new QLCompileException("if 操作符至少需要2个操作数 ");
        } else if (oldChildren.length > 5) {
            throw new QLCompileException("if 操作符最多只有5个操作数 ");
        }
        ExpressNode[] children = new ExpressNode[3];
        int point = 0;
        for (ExpressNode oldChild : oldChildren) {
            if (oldChild.isTypeEqualsOrChild("then")
                || oldChild.isTypeEqualsOrChild("else")
                || oldChild.isTypeEqualsOrChild("?")
                || oldChild.isTypeEqualsOrChild(":")) {
                continue;
            }
            children[point] = oldChild;
            point = point + 1;
        }
        if (point == 2) {
            children[2] = new ExpressNode(expressRunner.getNodeTypeManager().findNodeType("STAT_BLOCK"), null);
        }

        //condition
        boolean r1 = expressRunner.createInstructionSetPrivate(result, forStack, children[0], false);
        InstructionGoToWithCondition goToFalseBody = new InstructionGoToWithCondition(false, 0, true);
        goToFalseBody.setLine(node.getLine());
        result.addInstruction(goToFalseBody);
        int conditionEndPoint = result.getCurrentPoint();

        //true
        boolean r2 = expressRunner.createInstructionSetPrivate(result, forStack, children[1], false);
        InstructionGoTo goToEnd = new InstructionGoTo(0);
        goToEnd.setLine(node.getLine());
        result.addInstruction(goToEnd);
        goToFalseBody.setOffset(result.getCurrentPoint() - conditionEndPoint + 1);

        int trueBodyEndPoint = result.getCurrentPoint();

        //false
        boolean r3 = expressRunner.createInstructionSetPrivate(result, forStack, children[2], false);
        goToEnd.setOffset(result.getCurrentPoint() - trueBodyEndPoint + 1);
        return r1 || r2 || r3;
    }
}
