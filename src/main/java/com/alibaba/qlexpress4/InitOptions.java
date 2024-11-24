package com.alibaba.qlexpress4;


import com.alibaba.qlexpress4.aparser.ImportManager;
import com.alibaba.qlexpress4.aparser.InterpolationMode;
import com.alibaba.qlexpress4.runtime.function.ExtensionFunction;
import com.alibaba.qlexpress4.runtime.function.FilterExtensionFunction;
import com.alibaba.qlexpress4.runtime.function.MapExtensionFunction;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Author: TaoKan
 */
public class InitOptions {

    public static InitOptions DEFAULT_OPTIONS = InitOptions.builder().build();

    private final ClassSupplier classSupplier;

    /**
     * default import java packages for script
     * default
     * ImportManager.importPack("java.lang"),
     * ImportManager.importPack("java.util"),
     * ImportManager.importPack("java.math"),
     * ImportManager.importPack("java.util.stream")
     * ImportManager.importPack("java.util.function")
     */
    private final List<ImportManager.QLImport> defaultImport;

    /**
     * enable debug mode
     * default false
     */
    private final boolean debug;

    /**
     * consume all debug info, valid when debug is true
     * default is print in standard output, can not be null
     */
    private final Consumer<String> debugInfoConsumer;

    /**
     * qlexpress security strategy
     * default is isolation, not allow any access to java
     */
    private final QLSecurityStrategy securityStrategy;

    /**
     * qlexpress extension functions
     * default is some collection convenience functions
     */
    private final List<ExtensionFunction> extensionFunctions;

    /**
     * allow access private field and method
     * default false
     */
    private final boolean allowPrivateAccess;

    /**
     * How to manage string interpolation, for instance, "a ${t-c} b"
     * default SCRIPT
     */
    private final InterpolationMode interpolationMode;

    private InitOptions(ClassSupplier classSupplier,
                        List<ImportManager.QLImport> defaultImport,
                        boolean debug, Consumer<String> debugInfoConsumer,
                        QLSecurityStrategy securityStrategy,
                        List<ExtensionFunction> extensionFunctions, boolean allowPrivateAccess,
                        InterpolationMode interpolationMode) {
        this.classSupplier = classSupplier;
        this.defaultImport = defaultImport;
        this.debug = debug;
        this.debugInfoConsumer = debugInfoConsumer;
        this.securityStrategy = securityStrategy;
        this.extensionFunctions = extensionFunctions;
        this.allowPrivateAccess = allowPrivateAccess;
        this.interpolationMode = interpolationMode;
    }

    public static InitOptions.Builder builder() {
        return new Builder();
    }

    public List<ImportManager.QLImport> getDefaultImport() {
        return defaultImport;
    }

    public ClassSupplier getClassSupplier() {
        return classSupplier;
    }

    public boolean isDebug() {
        return debug;
    }

    public Consumer<String> getDebugInfoConsumer() {
        return debugInfoConsumer;
    }

    public QLSecurityStrategy getSecurityStrategy() {
        return securityStrategy;
    }

    public List<ExtensionFunction> getExtensionFunctions() {
        return extensionFunctions;
    }

    public boolean isAllowPrivateAccess() {
        return allowPrivateAccess;
    }

    public InterpolationMode getInterpolationMode() {
        return interpolationMode;
    }

    public static class Builder {
        private ClassSupplier classSupplier = DefaultClassSupplier.getInstance();
        private List<ImportManager.QLImport> defaultImport = Arrays.asList(
                ImportManager.importPack("java.lang"),
                ImportManager.importPack("java.util"),
                ImportManager.importPack("java.math"),
                ImportManager.importPack("java.util.stream"),
                ImportManager.importPack("java.util.function")
        );
        private boolean debug = false;
        private Consumer<String> debugInfoConsumer = System.out::println;
        private QLSecurityStrategy securityStrategy = QLSecurityStrategy.isolation();
        private List<ExtensionFunction> extensionFunctions = Arrays.asList(
                FilterExtensionFunction.INSTANCE,
                MapExtensionFunction.INSTANCE
        );
        private boolean allowPrivateAccess = false;
        private InterpolationMode interpolationMode = InterpolationMode.SCRIPT;

        public Builder classSupplier(ClassSupplier classSupplier) {
            this.classSupplier = classSupplier;
            return this;
        }

        public Builder defaultImport(List<ImportManager.QLImport> defaultImport) {
            this.defaultImport = defaultImport;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder debugInfoConsumer(Consumer<String> debugInfoConsumer) {
            this.debugInfoConsumer = debugInfoConsumer;
            return this;
        }

        public Builder securityStrategy(QLSecurityStrategy securityStrategy) {
            this.securityStrategy = securityStrategy;
            return this;
        }

        public Builder extensionFunctions(List<ExtensionFunction> extensionFunctions) {
            this.extensionFunctions = extensionFunctions;
            return this;
        }

        public Builder allowPrivateAccess(boolean allowPrivateAccess) {
            this.allowPrivateAccess = allowPrivateAccess;
            return this;
        }

        public InitOptions build() {
            return new InitOptions(classSupplier, defaultImport,
                    debug, debugInfoConsumer, securityStrategy, extensionFunctions,
                    allowPrivateAccess, interpolationMode);
        }
    }
}
