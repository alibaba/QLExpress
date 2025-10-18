package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.CheckOptions;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.runtime.operator.Operator;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 运算符校验访问器
 * 遍历语法树,在遍历过程中对运算符进行限制校验
 *
 * 核心设计:校验逻辑完全在 Visitor 内部实现,通过 accept() 方法触发
 *
 * @author QLExpress Team
 */
public class OperatorVisitor extends QLParserBaseVisitor<Void> {

    /**
     * 允许使用的运算符集合(白名单)
     * 如果为 null 或空,则不进行白名单限制
     */
    private final Set<Operator> allowedOperators;

    /**
     * 允许使用的运算符字符串集合(用于快速查找)
     */
    private final Set<String> allowedOperatorStrings;

    /**
     * 禁止使用的运算符集合(黑名单)
     * 如果为 null 或空,则不进行黑名单限制
     */
    private final Set<Operator> forbiddenOperators;

    /**
     * 禁止使用的运算符字符串集合(用于快速查找)
     */
    private final Set<String> forbiddenOperatorStrings;

    /**
     * 构造函数
     * @param checkOptions 校验配置,包含运算符白名单和黑名单
     */
    public OperatorVisitor(CheckOptions checkOptions) {
        if (checkOptions == null) {
            checkOptions = CheckOptions.DEFAULT_OPTIONS;
        }

        this.allowedOperators = checkOptions.getAllowedOperators();
        this.forbiddenOperators = checkOptions.getForbiddenOperators();

        // 将 Operator 对象转换为字符串集合,用于快速查找
        this.allowedOperatorStrings = (allowedOperators != null && !allowedOperators.isEmpty()) ?
            allowedOperators.stream()
                .map(Operator::getOperator)
                .collect(Collectors.toSet()) : null;

        this.forbiddenOperatorStrings = (forbiddenOperators != null && !forbiddenOperators.isEmpty()) ?
            forbiddenOperators.stream()
                .map(Operator::getOperator)
                .collect(Collectors.toSet()) : null;
    }

    /**
     * 检查运算符是否被允许,如果不允许则立即抛出异常
     *
     * @param operatorString 运算符字符串
     * @throws QLSyntaxException 如果运算符不在白名单中或在黑名单中
     */
    private void checkOperator(String operatorString) throws QLSyntaxException {
        // 1. 如果配置了白名单,检查运算符是否在白名单中
        if (allowedOperatorStrings != null && !allowedOperatorStrings.isEmpty()) {
            if (!allowedOperatorStrings.contains(operatorString)) {
                // 立即抛出异常,中断遍历
                String reason = String.format("脚本使用了不允许的运算符: %s。允许的运算符: %s",
                    operatorString, allowedOperatorStrings);
                throw QLException.reportScannerErr("", 0, 0, 0, operatorString, "OPERATOR_NOT_ALLOWED", reason);
            }
        }

        // 2. 如果配置了黑名单,检查运算符是否在黑名单中
        if (forbiddenOperatorStrings != null && !forbiddenOperatorStrings.isEmpty()) {
            if (forbiddenOperatorStrings.contains(operatorString)) {
                // 立即抛出异常,中断遍历
                String reason = String.format("脚本使用了被禁止的运算符: %s。禁止的运算符: %s",
                    operatorString, forbiddenOperatorStrings);
                throw QLException.reportScannerErr("", 0, 0, 0, operatorString, "OPERATOR_FORBIDDEN", reason);
            }
        }
    }

    /**
     * 访问二元运算符表达式(包括算术、逻辑、比较等运算符)
     */
    @Override
    public void visitLeftAsso(QLParser.LeftAssoContext ctx) {
        // 获取运算符
        QLParser.BinaryopContext binaryopContext = ctx.binaryop();
        if (binaryopContext != null) {
            String operator = binaryopContext.getText();
            checkOperator(operator);  // 在这里进行校验,可能抛出异常
        }

        // 继续遍历子节点
        return super.visitLeftAsso(ctx);
    }

    /**
     * 访问前缀一元运算符表达式(如 !、-、++、-- 等)
     */
    @Override
    public void visitPrefixExpress(QLParser.PrefixExpressContext ctx) {
        // 获取前缀运算符
        if (ctx.opId() != null) {
            String operator = ctx.opId().getText();
            checkOperator(operator);  // 在这里进行校验,可能抛出异常
        }

        return super.visitPrefixExpress(ctx);
    }

    /**
     * 访问后缀一元运算符表达式(如 ++、-- 等)
     */
    @Override
    public void visitSuffixExpress(QLParser.SuffixExpressContext ctx) {
        // 获取后缀运算符
        if (ctx.opId() != null) {
            String operator = ctx.opId().getText();
            checkOperator(operator);
        }

        return super.visitSuffixExpress(ctx);
    }

    /**
     * 访问表达式(包含赋值运算符)
     */
    @Override
    public void visitExpression(QLParser.ExpressionContext ctx) {
        // 检查赋值运算符
        if (ctx.assignOperator() != null) {
            checkOperator(ctx.assignOperator().getText());
        }

        return super.visitExpression(ctx);
    }
}
