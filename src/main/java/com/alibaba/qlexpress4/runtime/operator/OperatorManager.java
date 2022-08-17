package com.alibaba.qlexpress4.runtime.operator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.qlexpress4.runtime.operator.arithmetic.DivideOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MinusOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.ModOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MultiplyOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.PlusOperator;
import com.alibaba.qlexpress4.runtime.operator.assign.AssignOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseAndOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseLeftShiftOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseOrOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseRightShiftOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseRightShiftUnsignedOperator;
import com.alibaba.qlexpress4.runtime.operator.bit.BitwiseXorOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.EqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.GreaterEqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.GreaterOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.LessEqualOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.LessOperator;
import com.alibaba.qlexpress4.runtime.operator.compare.UnequalOperator;
import com.alibaba.qlexpress4.runtime.operator.logic.LogicAndOperator;
import com.alibaba.qlexpress4.runtime.operator.logic.LogicOrOperator;

/**
 * 不考虑一元操作符
 *
 * @author 冰够
 */
public class OperatorManager {
    private static final Map<String, BinaryOperator> defaultBinaryOperatorMap = new ConcurrentHashMap<>();
    private final Map<String, BinaryOperator> customBinaryOperatorMap = new ConcurrentHashMap<>();

    static {
        List<BinaryOperator> binaryOperatorList = new ArrayList<>(32);
        binaryOperatorList.add(new AssignOperator());
        binaryOperatorList.add(new PlusOperator());
        binaryOperatorList.add(new MinusOperator());
        binaryOperatorList.add(new MultiplyOperator());
        binaryOperatorList.add(new DivideOperator());
        binaryOperatorList.add(new ModOperator());
        binaryOperatorList.add(new BitwiseAndOperator());
        binaryOperatorList.add(new BitwiseOrOperator());
        binaryOperatorList.add(new BitwiseXorOperator());
        binaryOperatorList.add(new BitwiseLeftShiftOperator());
        binaryOperatorList.add(new BitwiseRightShiftOperator());
        binaryOperatorList.add(new BitwiseRightShiftUnsignedOperator());
        binaryOperatorList.add(new LogicAndOperator());
        binaryOperatorList.add(new LogicOrOperator());
        binaryOperatorList.add(new EqualOperator());
        binaryOperatorList.add(new UnequalOperator());
        binaryOperatorList.add(new GreaterOperator());
        binaryOperatorList.add(new GreaterEqualOperator());
        binaryOperatorList.add(new LessOperator());
        binaryOperatorList.add(new LessEqualOperator());
        for (BinaryOperator binaryOperator : binaryOperatorList) {
            defaultBinaryOperatorMap.put(binaryOperator.getOperator(), binaryOperator);
        }
        defaultBinaryOperatorMap.put("mod", defaultBinaryOperatorMap.get("%"));
        defaultBinaryOperatorMap.put("and", defaultBinaryOperatorMap.get("&&"));
        defaultBinaryOperatorMap.put("or", defaultBinaryOperatorMap.get("||"));
    }

    public void addOperator(String operator, BinaryOperator binaryOperator) {
        customBinaryOperatorMap.put(operator, binaryOperator);
    }

    public Map<String, Integer> getOperatorPrecedenceMap() {
        Map<String, Integer> operatorPrecedenceMap = new HashMap<>(32);
        customBinaryOperatorMap.forEach(
            (operator, binaryOperator) -> operatorPrecedenceMap.putIfAbsent(operator, binaryOperator.getPriority()));
        defaultBinaryOperatorMap.forEach(
            (operator, binaryOperator) -> operatorPrecedenceMap.putIfAbsent(operator, binaryOperator.getPriority()));
        return operatorPrecedenceMap;
    }

    /**
     * TODO 一元操作符？
     *
     * @return
     */
    public Set<String> getOperatorSet() {
        Set<String> operatorSet = new HashSet<>();
        operatorSet.addAll(customBinaryOperatorMap.keySet());
        operatorSet.addAll(defaultBinaryOperatorMap.keySet());
        return operatorSet;
    }
}
