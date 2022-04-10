package com.alibaba.qlexpress4;

import java.util.Arrays;
import java.util.List;

/**
 * @author 悬衡
 * date 2022/1/12 2:35 下午
 */
public class QLOptions {

    public static QLOptions DEFAULT_OPTIONS = QLOptions.builder().build();

    /**
     * precise evaluate based on BigDecimal
     * default true
     */
    private final boolean precise;

    /**
     * allowAccessPrivateMethod default false
     */
    private final boolean allowAccessPrivateMethod;

    /**
     * useCacheClear default false
     */
    private final boolean useCacheClear;


    private final ClassLoader classLoader;

    /**
     * default import java packages for script
     */
    private final List<String> defaultImport;

    private QLOptions(boolean precise, ClassLoader classLoader, List<String> defaultImport, boolean allowAccessPrivateMethod, boolean useCacheClear) {
        this.precise = precise;
        this.classLoader = classLoader;
        this.defaultImport = defaultImport;
        this.allowAccessPrivateMethod = allowAccessPrivateMethod;
        this.useCacheClear = useCacheClear;
    }

    public boolean isPrecise() {
        return precise;
    }


    public boolean isUseCacheClear() {
        return useCacheClear;
    }

    public boolean isAllowAccessPrivateMethod() {
        return allowAccessPrivateMethod;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public List<String> getDefaultImport() {
        return defaultImport;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean precise = true;

        private boolean allowAccessPrivateMethod;

        private boolean useCacheClear;

        private ClassLoader classLoader = QLOptions.class.getClassLoader();

        private List<String> defaultImport = Arrays.asList(
                "java.lang", "java.util", "java.util.stream"
        );

        public Builder precise(boolean precise) {
            this.precise = precise;
            return this;
        }

        public Builder allowAccessPrivateMethod(boolean allowAccessPrivateMethod) {
            this.allowAccessPrivateMethod = allowAccessPrivateMethod;
            return this;
        }

        public Builder useCacheClear(boolean useCacheClear) {
            this.useCacheClear = useCacheClear;
            return this;
        }


        public Builder classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Builder defaultImport(List<String> defaultImport) {
            this.defaultImport = defaultImport;
            return this;
        }

        public QLOptions build() {
            return new QLOptions(precise, classLoader, defaultImport, allowAccessPrivateMethod,useCacheClear);
        }
    }

}
