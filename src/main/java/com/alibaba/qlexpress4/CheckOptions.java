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

    /**
     * Private constructor
     */
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

    /**
     * Get operator restriction strategy
     */
    public OperatorStrategy getStrategy() {
        return strategy;
    }

    /**
     * Get operator set
     */
    public Set<Operator> getOperators() {
        return operators;
    }

    /**
     * Check if using whitelist mode
     */
    public boolean isWhitelistMode() {
        return strategy == OperatorStrategy.WHITELIST;
    }

    /**
     * Check if using blacklist mode
     */
    public boolean isBlacklistMode() {
        return strategy == OperatorStrategy.BLACKLIST;
    }

    /**
     * Create Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class
     */
    public static class Builder {
        private OperatorStrategy strategy = OperatorStrategy.ALLOW_ALL;
        private Set<Operator> operators;

        private Builder() {
        }

        /**
         * Set whitelist mode with allowed operators
         *
         * @param allowedOperators allowed operator set
         * @return Builder
         */
        public Builder whitelist(Set<Operator> allowedOperators) {
            this.strategy = OperatorStrategy.WHITELIST;
            this.operators = allowedOperators;
            return this;
        }

        /**
         * Set blacklist mode with forbidden operators
         *
         * @param forbiddenOperators forbidden operator set
         * @return Builder
         */
        public Builder blacklist(Set<Operator> forbiddenOperators) {
            this.strategy = OperatorStrategy.BLACKLIST;
            this.operators = forbiddenOperators;
            return this;
        }

        /**
         * Set no restriction mode (allow all operators)
         *
         * @return Builder
         */
        public Builder allowAll() {
            this.strategy = OperatorStrategy.ALLOW_ALL;
            this.operators = null;
            return this;
        }

        /**
         * Build CheckOptions
         *
         * @return CheckOptions
         * @throws IllegalArgumentException if configuration is invalid
         */
        public CheckOptions build() {
            return new CheckOptions(strategy, operators);
        }
    }
}
