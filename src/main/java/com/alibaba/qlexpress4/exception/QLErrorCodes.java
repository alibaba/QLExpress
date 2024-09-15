package com.alibaba.qlexpress4.exception;

/**
 * Author: DQinYuan
 */
public enum QLErrorCodes {
    // syntax error
    IMPORT_STATEMENT_NOT_AT_BEGINNING("import declaration must at beginning"),
    IMPORT_STATIC_NOT_SUPPORTED("'import static' not supported temporarily"),
    UNKNOWN_OPERATOR("unknown operator"),
    MISSING_INDEX("missing index expression"),
    INVALID_NUMBER("invalid number"),
    CLASS_NOT_FOUND("can not find class: %s"),
    // runtim error
    INVALID_INDEX("index can only be number"),
    INDEX_OUT_BOUND("index out of bound"),
    NONINDEXABLE_OBJECT("object of class %s is not indexable"),
    NONTRAVERSABLE_OBJECT("object of class %s is not traversable"),
    NULL_FIELD_ACCESS("can not access field from null"),
    NULL_METHOD_ACCESS("can not access method from null"),
    FIELD_NOT_FOUND("'%s' field not found"),
    SET_FIELD_UNKNOWN_ERROR("unknown error when setting field '%s' value"),
    GET_FIELD_UNKNOWN_ERROR("unknown error when getting field '%s' value"),
    INVOKE_METHOD_WITH_WRONG_ARGUMENTS("invoke method '%s' with wrong arguments"),
    INVOKE_METHOD_INNER_ERROR("exception from inner when invoking method '%s'"),
    INVOKE_METHOD_UNKNOWN_ERROR("unknown error when invoking method '%s'"),
    INVOKE_FUNCTION_INNER_ERROR("exception from inner when invoking function '%s', error message: %s"),
    FUNCTION_NOT_FOUND("function '%s' not found"),
    FUNCTION_TYPE_MISMATCH("symbol '%s' is not a function type"),
    INVOKE_LAMBDA_ERROR("error when invoking lambda"),
    NULL_CALL("can not call null"),
    OBJECT_NOT_CALLABLE("type '%s' is not callable"),
    METHOD_NOT_FOUND("method '%s' not found"),
    INVOKE_CONSTRUCTOR_UNKNOWN_ERROR("unknown error when invoking constructor"),
    INVOKE_CONSTRUCTOR_INNER_ERROR("exception from inner when invoking constructor"),
    NO_SUITABLE_CONSTRUCTOR("no suitable constructor for types %s"),
    EXECUTE_BLOCK_ERROR("error when executing block"),
    INCOMPATIBLE_TYPE_CAST("incompatible cast from type: %s to type: %s"),
    INVALID_CAST_TARGET("target for type cast must be a class, but accept %s"),
    SCRIPT_TIME_OUT("script exceeds timeout milliseconds, which is %d ms"),
    INCOMPATIBLE_ASSIGNMENT_TYPE("variable declared type %s, assigned with incompatible value type %s"),
    FOR_EACH_ITERABLE_REQUIRED("for-each can only be applied to iterable"),
    FOR_EACH_TYPE_MISMATCH("for-each type mismatch, required %s, but %s provided"),
    FOR_EACH_UNKNOWN_ERROR("unknown error when executing for-each"),
    FOR_INIT_ERROR("error when executing for init"),
    FOR_BODY_ERROR("error when executing for body"),
    FOR_UPDATE_ERROR("error when executing for update"),
    FOR_CONDITION_ERROR("error when executing for condition"),
    FOR_CONDITION_BOOL_REQUIRED("result of for condition must be bool"),
    WHILE_CONDITION_BOOL_REQUIRED("result of while condition must be bool"),
    WHILE_CONDITION_ERROR("error when executing while condition"),
    CONDITION_BOOL_REQUIRED("result of condition expression must be bool"),
    ARRAY_SIZE_NUM_REQUIRED("size of array must be number"),
    EXCEED_MAX_ARR_LENGTH("array length %d, exceed max allowed length %d"),
    INCOMPATIBLE_ARRAY_ITEM_TYPE("item %d with type %s incompatible with array type %s"),
    INVALID_ASSIGNMENT("value %s is not assignable"),
    EXECUTE_OPERATOR_EXCEPTION("exception when executing '%s %s %s'"),
    INVALID_ARITHMETIC(""),
    INVALID_BINARY_OPERAND("the '%s' operator can not be applied to leftType:%s with leftValue:%s and rightType:%s with rightValue:%s"),
    INVALID_UNARY_OPERAND("the '%s' operator can not be applied to type %s with value %s"),
    EXECUTE_FINAL_BLOCK_ERROR("error when executing final block in try...catch...final..."),
    EXECUTE_TRY_BLOCK_ERROR("error when executing try... block"),
    EXECUTE_CATCH_HANDLER_ERROR("error when executing handler of '%s'"),
    // user defined exception
    INVALID_ARGUMENT(""),
    BIZ_EXCEPTION(""),
    QL_THROW("qlexpress throw statement");

    private final String errorMsg;

    QLErrorCodes(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
