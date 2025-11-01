package com.alibaba.qlexpress4.operator;

import java.util.Set;

/**
 * Operator restriction strategy interface.
 * Defines the contract for checking whether an operator is allowed.
 *
 * Supported operators (organized by category):
 * - arithmetic: {@code +, -, *, /, %, mod, +=, -=, *=, /=, %=}
 * - assign: {@code =}
 * - bit: {@code &, |, ^, ~, <<, >>, >>>, &=, |=, ^=, <<=, >>=, >>>=}
 * - collection: {@code in, not_in}
 * - compare: {@code ==, !=, <>, <, <=, >, >=}
 * - logic: {@code &&, ||, !, and, or}
 * - string: {@code like, not_like}
 * - unary: {@code ++, --, +, - (unary)}
 * - root: {@code instanceof}
 *
 * Usage example:
 * <pre>{@code
 *   // Whitelist strategy - only allow + and *
 *   Set<String> allowed = new HashSet<>(Arrays.asList("+", "*"));
 *   OperatorCheckStrategy strategy = OperatorCheckStrategy.whitelist(allowed);
 *
 *   // Blacklist strategy - forbid assignment operator
 *   Set<String> forbidden = new HashSet<>(Arrays.asList("="));
 *   OperatorCheckStrategy strategy = OperatorCheckStrategy.blacklist(forbidden);
 *
 *   // Allow all operators
 *   OperatorCheckStrategy strategy = OperatorCheckStrategy.allowAll();
 * }</pre>
 */
public interface OperatorCheckStrategy {
    
    static OperatorCheckStrategy allowAll() {
        return DefaultOperatorCheckStrategy.getInstance();
    }
    
    static OperatorCheckStrategy whitelist(Set<String> allowedOperators) {
        return new WhiteOperatorCheckStrategy(allowedOperators);
    }
    
    static OperatorCheckStrategy blacklist(Set<String> forbiddenOperators) {
        return new BlackOperatorCheckStrategy(forbiddenOperators);
    }
    
    boolean isAllowed(String operator);
    
    Set<String> getOperators();
}
