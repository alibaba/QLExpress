package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.runtime.operator.Operator;

import java.util.Collections;
import java.util.Set;

/**
 * Script validation configuration class
 * Used to configure operator restriction rules during script validation
 *
 * @author QLExpress Team
 */
public class CheckOptions {

    /**
     * Operator restriction strategy
     */
    public enum OperatorStrategy {
        /**
         * No restriction - all operators are allowed
         */
        ALLOW_ALL,

        /**
         * Whitelist mode - only specified operators are allowed
         */
        WHITELIST,

        /**
         * Blacklist mode - all operators except specified ones are allowed
         */
        BLACKLIST
    }

    /**
     * Operator restriction strategy
     */
    private final OperatorStrategy strategy;

    /**
     * Operator set (meaning depends on strategy)
     * - ALLOW_ALL: ignored
     * - WHITELIST: allowed operators
     * - BLACKLIST: forbidden operators
     */
    private final Set<Operator> operators;

    /**
     * Default configuration: no restriction
     */
    public static final CheckOptions DEFAULT_OPTIONS = new CheckOptions(OperatorStrategy.ALLOW_ALL, null);

    private CheckOptions(OperatorStrategy strategy, Set<Operator> operators) {
        this.strategy = strategy;
        this.operators = operators != null ? Collections.unmodifiableSet(operators) : null;

        // Validate configuration
        if (strategy == OperatorStrategy.WHITELIST || strategy == OperatorStrategy.BLACKLIST) {
            if (operators == null || operators.isEmpty()) {
                throw new IllegalArgumentException(
                    String.format("Operator set cannot be null or empty when using %s strategy", strategy));
            }
        }
    }

    public OperatorStrategy getStrategy() {
        return strategy;
    }

    public Set<Operator> getOperators() {
        return operators;
    }

    public boolean isWhitelistMode() {
        return strategy == OperatorStrategy.WHITELIST;
    }

    public boolean isBlacklistMode() {
        return strategy == OperatorStrategy.BLACKLIST;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private OperatorStrategy strategy = OperatorStrategy.ALLOW_ALL;
        private Set<Operator> operators;

        private Builder() {
        }

        public Builder whitelist(Set<Operator> allowedOperators) {
            this.strategy = OperatorStrategy.WHITELIST;
            this.operators = allowedOperators;
            return this;
        }

        public Builder blacklist(Set<Operator> forbiddenOperators) {
            this.strategy = OperatorStrategy.BLACKLIST;
            this.operators = forbiddenOperators;
            return this;
        }

        public Builder allowAll() {
            this.strategy = OperatorStrategy.ALLOW_ALL;
            this.operators = null;
            return this;
        }

        public CheckOptions build() {
            return new CheckOptions(strategy, operators);
        }
    }
}
