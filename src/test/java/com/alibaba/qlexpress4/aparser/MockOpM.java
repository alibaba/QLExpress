package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.QLPrecedences;

public class MockOpM implements ParserOperatorManager {
    @Override
    public boolean isOpType(String lexeme, OpType opType) {
        switch (opType) {
            case MIDDLE:
                switch (lexeme) {
                    case "+":
                    case "-":
                    case "/":
                    case ".*":
                    case ">>":
                    case ">>>":
                    case "==":
                    case "=":
                    case "instanceof":
                    case "&":
                    case "|":
                    case "%":
                    case ">":
                    case "*":
                    case "?.":
                    case "<<":
                    case "&&":
                    case "||":
                        return true;
                }
                return false;
            case PREFIX:
                switch (lexeme) {
                    case "++":
                    case "-":
                    case "+":
                    case "~":
                        return true;

                }
                return false;
            case SUFFIX:
                switch (lexeme) {
                    case "++":
                        return true;
                }
                return false;
            default:
                return false;
        }
    }

    @Override
    public Integer precedence(String lexeme) {
        switch (lexeme) {
            case "+":
            case "-":
                return QLPrecedences.ADD;
            case "/":
            case "%":
            case "*":
                return QLPrecedences.MULTI;
            case ".*":
            case "?.":
                return QLPrecedences.GROUP;
            case "==":
            case "instanceof":
            case ">":
                return QLPrecedences.COMPARE;
            case ">>":
            case ">>>":
            case "<<":
                return QLPrecedences.BIT_MOVE;
            case "=":
                return QLPrecedences.ASSIGN;
            case "||":
                return QLPrecedences.OR;
            case "&&":
                return QLPrecedences.AND;
            case "&":
                return QLPrecedences.BIT_AND;
            case "|":
                return QLPrecedences.BIT_OR;
            default:
                throw new IllegalStateException("unknown op");
        }
    }

    @Override
    public Integer getAlias(String lexeme) {
        return null;
    }

}
