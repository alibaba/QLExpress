package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.runtime.operator.Operator;

import java.util.Collections;
import java.util.Set;

/**
 * 脚本校验配置类
 * 用于配置脚本校验时的运算符限制规则
 *
 * @author QLExpress Team
 */
public class CheckOptions {

    /**
     * 允许使用的运算符集合(白名单)
     * 如果为 null 或空,则不进行白名单限制
     */
    private final Set<Operator> allowedOperators;

    /**
     * 禁止使用的运算符集合(黑名单)
     * 如果为 null 或空,则不进行黑名单限制
     */
    private final Set<Operator> forbiddenOperators;

    /**
     * 默认配置:不限制任何运算符
     */
    public static final CheckOptions DEFAULT_OPTIONS = new CheckOptions(null, null);

    /**
     * 私有构造函数
     */
    private CheckOptions(Set<Operator> allowedOperators, Set<Operator> forbiddenOperators) {
        // 校验:白名单和黑名单不能同时设置
        if (allowedOperators != null && !allowedOperators.isEmpty()
            && forbiddenOperators != null && !forbiddenOperators.isEmpty()) {
            throw new IllegalArgumentException("不能同时设置白名单(allowedOperators)和黑名单(forbiddenOperators)");
        }

        this.allowedOperators = allowedOperators != null ?
            Collections.unmodifiableSet(allowedOperators) : null;
        this.forbiddenOperators = forbiddenOperators != null ?
            Collections.unmodifiableSet(forbiddenOperators) : null;
    }

    /**
     * 获取允许使用的运算符集合
     */
    public Set<Operator> getAllowedOperators() {
        return allowedOperators;
    }

    /**
     * 获取禁止使用的运算符集合
     */
    public Set<Operator> getForbiddenOperators() {
        return forbiddenOperators;
    }

    /**
     * 创建 Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder 类
     */
    public static class Builder {
        private Set<Operator> allowedOperators;
        private Set<Operator> forbiddenOperators;

        private Builder() {
        }

        /**
         * 设置允许使用的运算符集合(白名单)
         * 注意:白名单和黑名单不能同时设置
         *
         * @param allowedOperators 允许使用的运算符集合
         * @return Builder
         */
        public Builder allowedOperators(Set<Operator> allowedOperators) {
            this.allowedOperators = allowedOperators;
            return this;
        }

        /**
         * 设置禁止使用的运算符集合(黑名单)
         * 注意:白名单和黑名单不能同时设置
         *
         * @param forbiddenOperators 禁止使用的运算符集合
         * @return Builder
         */
        public Builder forbiddenOperators(Set<Operator> forbiddenOperators) {
            this.forbiddenOperators = forbiddenOperators;
            return this;
        }

        /**
         * 构建 CheckOptions
         *
         * @return CheckOptions
         * @throws IllegalArgumentException 如果同时设置了白名单和黑名单
         */
        public CheckOptions build() {
            return new CheckOptions(allowedOperators, forbiddenOperators);
        }
    }
}
