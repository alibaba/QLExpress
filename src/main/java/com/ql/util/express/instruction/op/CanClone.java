package com.ql.util.express.instruction.op;

public interface CanClone {
    OperatorBase cloneMe(String name, String errorInfo) throws Exception;
}
