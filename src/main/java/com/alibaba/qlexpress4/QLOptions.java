package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.parser.ImportManager;

import java.util.Arrays;
import java.util.List;

/**
 * Author: DQinYuan
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


    private final ClassLoader classLoader;

    /**
     * script timeout millisecond, default is -1, namely time unlimited
     * <= 0, time unlimited
     */
    private final long timeoutMillis;

    /**
     * default import java packages for script
     */
    private final List<ImportManager.Import> defaultImport;

    private QLOptions(boolean precise, ClassLoader classLoader, long timeoutMillis,
                      List<ImportManager.Import> defaultImport, boolean allowAccessPrivateMethod) {
        this.precise = precise;
        this.classLoader = classLoader;
        this.timeoutMillis = timeoutMillis;
        this.defaultImport = defaultImport;
        this.allowAccessPrivateMethod = allowAccessPrivateMethod;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isPrecise() {
        return precise;
    }

    public boolean enableAllowAccessPrivateMethod() {
        return allowAccessPrivateMethod;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public List<ImportManager.Import> getDefaultImport() {
        return defaultImport;
    }

    public static class Builder {

        private boolean precise = true;

        private boolean allowAccessPrivateMethod;

        private ClassLoader classLoader = QLOptions.class.getClassLoader();

        private long timeoutMillis = -1;

        private List<ImportManager.Import> defaultImport = Arrays.asList(
                ImportManager.importPack("java.lang"),
                ImportManager.importPack("java.util"),
                ImportManager.importPack("java.util.stream")
        );

        public Builder precise(boolean precise) {
            this.precise = precise;
            return this;
        }

        public Builder allowAccessPrivateMethod(boolean allowAccessPrivateMethod) {
            this.allowAccessPrivateMethod = allowAccessPrivateMethod;
            return this;
        }

        public Builder classLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Builder defaultImport(List<ImportManager.Import> defaultImport) {
            this.defaultImport = defaultImport;
            return this;
        }

        public Builder timeoutMillis(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
            return this;
        }

        public QLOptions build() {
            return new QLOptions(precise, classLoader, timeoutMillis, defaultImport, allowAccessPrivateMethod);
        }
    }

}
