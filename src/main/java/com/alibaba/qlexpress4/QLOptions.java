package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.parser.ImportManager;
import com.alibaba.qlexpress4.security.SafePointStrategy;
import java.util.*;
import java.util.function.Consumer;

/**
 * Author: DQinYuan
 * date 2022/1/12 2:35 下午
 */
public class QLOptions {

    public static QLOptions DEFAULT_OPTIONS = QLOptions.builder().build();

    /**
     * precise evaluate based on BigDecimal
     * default false
     */
    private final boolean precise;

    /**
     * define global symbol in user context
     * default false
     */
    private final boolean polluteUserContext;

    /**
     * allowAccessPrivateMethod
     * default false
     */
    private final boolean allowAccessPrivateMethod;

    /**
     * custom classLoader
     * default is QLOptions' ClassLoader
     */
    private final ClassLoader classLoader;

    /**
     * script timeout millisecond, default is -1, namely time unlimited
     * <= 0, time unlimited
     * default -1
     */
    private final long timeoutMillis;

    /**
     * default import java packages for script
     * default         ImportManager.importPack("java.lang"),
     *                 ImportManager.importPack("java.util"),
     *                 ImportManager.importPack("java.math"),
     *                 ImportManager.importPack("java.util.stream")
     *                 ImportManager.importPack("java.util.function")
     */
    private final List<ImportManager.Import> defaultImport;

    /**
     * attachments will be carried to user defined function/operator/macro
     * only used to pass data, not as variable value
     *
     * default empty map
     */
    private final Map<String, Object> attachments;

    /**
     * open debug mode
     * default false
     */
    private final boolean debug;

    /**
     * avoid null pointer
     * default false
     */
    private final boolean avoidNullPointer;

    /**
     * consume all debug info, valid when debug is true
     * default is print in standard output, can not be null
     */
    private final Consumer<String> debugInfoConsumer;

    /**
     * user can choose safePoints about blacklist/whitelist
     * default is blacklist mode
     *
     */
    private final SafePointStrategy safePointStrategy;


    private QLOptions(boolean precise, boolean polluteUserContext, ClassLoader classLoader, long timeoutMillis,
                      List<ImportManager.Import> defaultImport, boolean allowAccessPrivateMethod,
                      Map<String, Object> attachments, boolean debug,
                      boolean avoidNullPointer, SafePointStrategy safePointStrategy, Consumer<String> debugInfoConsumer) {
        this.precise = precise;
        this.polluteUserContext = polluteUserContext;
        this.classLoader = classLoader;
        this.timeoutMillis = timeoutMillis;
        this.defaultImport = defaultImport;
        this.allowAccessPrivateMethod = allowAccessPrivateMethod;
        this.attachments = attachments;
        this.debug = debug;
        this.avoidNullPointer = avoidNullPointer;
        this.debugInfoConsumer = debugInfoConsumer;
        this.safePointStrategy = safePointStrategy;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isPrecise() {
        return precise;
    }

    public boolean isPolluteUserContext() {
        return polluteUserContext;
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

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isAvoidNullPointer() {
        return avoidNullPointer;
    }

    public SafePointStrategy getSafePointStrategy() {return safePointStrategy;}

    public Consumer<String> getDebugInfoConsumer() {
        return debugInfoConsumer;
    }

    public static class Builder {

        private boolean precise = false;

        private boolean polluteUserContext = false;

        private boolean allowAccessPrivateMethod;

        private ClassLoader classLoader = QLOptions.class.getClassLoader();

        private long timeoutMillis = -1;

        private Map<String, Object> attachments = Collections.emptyMap();

        private boolean debug = false;

        private boolean avoidNullPointer = false;

        private SafePointStrategy safePointStrategy = SafePointStrategy.builder().defaultSystemStrategy();

        private Consumer<String> debugInfoConsumer = System.out::println;

        private List<ImportManager.Import> defaultImport = Arrays.asList(
                ImportManager.importPack("java.lang"),
                ImportManager.importPack("java.util"),
                ImportManager.importPack("java.math"),
                ImportManager.importPack("java.util.stream"),
                ImportManager.importPack("java.util.function")
        );

        public Builder precise(boolean precise) {
            this.precise = precise;
            return this;
        }

        public Builder polluteUserContext(boolean polluteUserContext) {
            this.polluteUserContext = polluteUserContext;
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

        public Builder attachments(Map<String, Object> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Builder safePointStrategy(SafePointStrategy safePointStrategy){
            this.safePointStrategy = safePointStrategy;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder avoidNullPointer(boolean avoidNullPointer) {
            this.avoidNullPointer = avoidNullPointer;
            return this;
        }

        public Builder debugInfoConsumer(Consumer<String> debugInfoConsumer) {
            this.debugInfoConsumer = debugInfoConsumer;
            return this;
        }

        public QLOptions build() {
            return new QLOptions(precise, polluteUserContext, classLoader, timeoutMillis,
                    defaultImport, allowAccessPrivateMethod, attachments, debug, avoidNullPointer,
                    safePointStrategy, debugInfoConsumer);
        }
    }

}
