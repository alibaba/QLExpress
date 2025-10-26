package com.alibaba.qlexpress4.operator;

import java.util.Set;

/**
 * Operator restriction strategy interface
 * Defines the contract for checking if an operator is allowed
 *
 * Supported operators (organized by directory):
 * - arithmetic: +, -, *, /, %, mod, +=, -=, *=, /=, %=
 * - assign: =
 * - bit: &, |, ^, ~, <<, >>, >>>, &=, |=, ^=, <<=, >>=, >>>=
 * - collection: in, not_in
 * - compare: ==, !=, <>, <, <=, >, >=
 * - logic: &&, ||, !, and, or
 * - string: like, not_like
 * - unary: ++, --, +, - (unary)
 * - root: instanceof
 *
 * Usage example:
 * <pre>
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
 * </pre>
 *
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
    
    StrategyType getStrategyType();
    
    enum StrategyType {
        ALLOW_ALL, WHITELIST, BLACKLIST
    }
}
