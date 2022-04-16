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

    private final ClassLoader classLoader;

    /**
     * script timeout millisecond, default is -1, namely time unlimited
     * <= 0, time unlimited
     */
    private final long timeoutMillis;

    /**
     * default import java packages for script
     */
    private final List<String> defaultImport;

    private QLOptions(boolean precise, ClassLoader classLoader, long timeoutMillis, List<String> defaultImport) {
        this.precise = precise;
        this.classLoader = classLoader;
        this.timeoutMillis = timeoutMillis;
        this.defaultImport = defaultImport;
    }

    public boolean isPrecise() {
        return precise;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public List<String> getDefaultImport() {
        return defaultImport;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean precise = true;

        private ClassLoader classLoader = QLOptions.class.getClassLoader();

        private long timeoutMillis = -1;

        private List<String> defaultImport = Arrays.asList(
                "java.lang", "java.util", "java.util.stream"
        );

        public Builder precise(boolean precise) {
            this.precise = precise;
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

        public Builder timeoutMillis(long timeoutMillis) {
            this.timeoutMillis = timeoutMillis;
            return this;
        }

        public QLOptions build() {
            return new QLOptions(precise, classLoader, timeoutMillis, defaultImport);
        }
    }

}
