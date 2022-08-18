package com.alibaba.qlexpress4.runtime.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.qlexpress4.runtime.operator.arithmetic.DivideAssignOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.DivideOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MinusAssignOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MinusOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.ModAssignOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.ModOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MultiplyAssignOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MultiplyOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.PlusAssignOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.PlusOperator;
import com.alibaba.qlexpress4.runtime.operator.assign.AssignOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseAndAssignOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseAndOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseInvertOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseLeftShiftAssignOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseLeftShiftOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseOrAssignOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseOrOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseRightShiftAssignOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseRightShiftOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseRightShiftUnsignedAssignOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseRightShiftUnsignedOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseXorAssignOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseXorOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.EqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.GreaterEqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.GreaterOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.LessEqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.LessOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.UnequalOperator;
import com.alibaba.qlexpress4.runtime.operator.in.InOperator;
import com.alibaba.qlexpress4.runtime.operator.logic.LogicAndOperator;
import com.alibaba.qlexpress4.runtime.operator.logic.LogicNotOperator;
import com.alibaba.qlexpress4.runtime.operator.logic.LogicOrOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.MinusMinusPrefixUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.MinusMinusSuffixUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.MinusUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.PlusPlusPrefixUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.PlusPlusSuffixUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.PlusUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;

/**
 * 不考虑一元操作符
 *
 * @author 冰够
 */
public class OperatorManager {
    private static final Map<String, BinaryOperator> DEFAULT_BINARY_OPERATOR_MAP = new ConcurrentHashMap<>(64);
    private static final Map<String, UnaryOperator> DEFAULT_PREFIX_UNARY_OPERATOR_MAP = new ConcurrentHashMap<>(8);
    private static final Map<String, UnaryOperator> DEFAULT_SUFFIX_UNARY_OPERATOR_MAP = new ConcurrentHashMap<>(8);

    static {
        List<BinaryOperator> binaryOperatorList = new ArrayList<>(64);
        binaryOperatorList.add(AssignOperator.getInstance());
        binaryOperatorList.add(PlusOperator.getInstance());
        binaryOperatorList.add(PlusAssignOperator.getInstance());
        binaryOperatorList.add(MinusOperator.getInstance());
        binaryOperatorList.add(MinusAssignOperator.getInstance());
        binaryOperatorList.add(MultiplyOperator.getInstance());
        binaryOperatorList.add(MultiplyAssignOperator.getInstance());
        binaryOperatorList.add(DivideOperator.getInstance());
        binaryOperatorList.add(DivideAssignOperator.getInstance());
        binaryOperatorList.add(ModOperator.getInstance("%"));
        binaryOperatorList.add(ModAssignOperator.getInstance());
        binaryOperatorList.add(ModOperator.getInstance("mod"));
        binaryOperatorList.add(BitwiseAndOperator.getInstance());
        binaryOperatorList.add(BitwiseAndAssignOperator.getInstance());
        binaryOperatorList.add(BitwiseOrOperator.getInstance());
        binaryOperatorList.add(BitwiseOrAssignOperator.getInstance());
        binaryOperatorList.add(BitwiseXorOperator.getInstance());
        binaryOperatorList.add(BitwiseXorAssignOperator.getInstance());
        binaryOperatorList.add(BitwiseLeftShiftOperator.getInstance());
        binaryOperatorList.add(BitwiseLeftShiftAssignOperator.getInstance());
        binaryOperatorList.add(BitwiseRightShiftOperator.getInstance());
        binaryOperatorList.add(BitwiseRightShiftAssignOperator.getInstance());
        binaryOperatorList.add(BitwiseRightShiftUnsignedOperator.getInstance());
        binaryOperatorList.add(BitwiseRightShiftUnsignedAssignOperator.getInstance());
        binaryOperatorList.add(LogicAndOperator.getInstance("&&"));
        binaryOperatorList.add(LogicAndOperator.getInstance("and"));
        binaryOperatorList.add(LogicOrOperator.getInstance("||"));
        binaryOperatorList.add(LogicOrOperator.getInstance("or"));
        binaryOperatorList.add(EqualOperator.getInstance());
        binaryOperatorList.add(UnequalOperator.getInstance());
        binaryOperatorList.add(GreaterOperator.getInstance());
        binaryOperatorList.add(GreaterEqualOperator.getInstance());
        binaryOperatorList.add(LessOperator.getInstance());
        binaryOperatorList.add(LessEqualOperator.getInstance());
        binaryOperatorList.add(InOperator.getInstance());
        for (BinaryOperator binaryOperator : binaryOperatorList) {
            DEFAULT_BINARY_OPERATOR_MAP.put(binaryOperator.getOperator(), binaryOperator);
        }

        List<UnaryOperator> prefixUnaryOperatorList = new ArrayList<>(8);
        prefixUnaryOperatorList.add(PlusUnaryOperator.getInstance());
        prefixUnaryOperatorList.add(MinusUnaryOperator.getInstance());
        prefixUnaryOperatorList.add(PlusPlusPrefixUnaryOperator.getInstance());
        prefixUnaryOperatorList.add(MinusMinusPrefixUnaryOperator.getInstance());
        prefixUnaryOperatorList.add(BitwiseInvertOperator.getInstance());
        prefixUnaryOperatorList.add(LogicNotOperator.getInstance());
        for (UnaryOperator unaryOperator : prefixUnaryOperatorList) {
            DEFAULT_PREFIX_UNARY_OPERATOR_MAP.put(unaryOperator.getOperator(), unaryOperator);
        }

        List<UnaryOperator> suffixUnaryOperatorList = new ArrayList<>(8);
        suffixUnaryOperatorList.add(PlusPlusSuffixUnaryOperator.getInstance());
        suffixUnaryOperatorList.add(MinusMinusSuffixUnaryOperator.getInstance());
        for (UnaryOperator unaryOperator : suffixUnaryOperatorList) {
            DEFAULT_SUFFIX_UNARY_OPERATOR_MAP.put(unaryOperator.getOperator(), unaryOperator);
        }
    }

    private final Map<String, BinaryOperator> customBinaryOperatorMap = new ConcurrentHashMap<>();

    public void addOperator(String operator, BinaryOperator binaryOperator) {
        customBinaryOperatorMap.put(operator, binaryOperator);
    }

    public Map<String, Integer> getOperatorPrecedenceMap() {
        Map<String, Integer> operatorPrecedenceMap = new HashMap<>(64);
        customBinaryOperatorMap.forEach(
            (operator, binaryOperator) -> operatorPrecedenceMap.putIfAbsent(operator, binaryOperator.getPriority()));
        DEFAULT_BINARY_OPERATOR_MAP.forEach(
            (operator, binaryOperator) -> operatorPrecedenceMap.putIfAbsent(operator, binaryOperator.getPriority()));
        return operatorPrecedenceMap;
    }

    /**
     * 获取二元操作符
     *
     * @param operatorLexeme +, =, *, /
     * @return
     */
    public BinaryOperator getBinaryOperator(String operatorLexeme) {
        BinaryOperator customBinaryOperator = customBinaryOperatorMap.get(operatorLexeme);
        if (customBinaryOperator != null) {
            return customBinaryOperator;
        }

        return DEFAULT_BINARY_OPERATOR_MAP.get(operatorLexeme);
    }

    /**
     * like --1 ++1 !true ~1 ^1
     *
     * @param operatorLexeme ++, --
     * @return
     */
    public UnaryOperator getPrefixUnaryOperator(String operatorLexeme) {
        return DEFAULT_PREFIX_UNARY_OPERATOR_MAP.get(operatorLexeme);
    }

    /**
     * like 1-- 1++
     *
     * @param operatorLexeme ++, --
     * @return
     */
    public UnaryOperator getSuffixUnaryOperator(String operatorLexeme) {
        return DEFAULT_SUFFIX_UNARY_OPERATOR_MAP.get(operatorLexeme);
    }
}
