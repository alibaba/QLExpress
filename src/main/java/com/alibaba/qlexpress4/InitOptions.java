package com.alibaba.qlexpress4;


import com.alibaba.qlexpress4.aparser.ImportManager;
import com.alibaba.qlexpress4.aparser.InterpolationMode;
import com.alibaba.qlexpress4.runtime.function.ExtensionFunction;
import com.alibaba.qlexpress4.runtime.function.FilterExtensionFunction;
import com.alibaba.qlexpress4.runtime.function.MapExtensionFunction;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;

import java.util.ArrayList;
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

    /**
     * track the execution process of all expressions and return the path to the `execute` caller.
     * default false
     */
    private final boolean traceExpression;

    private InitOptions(ClassSupplier classSupplier,
                        List<ImportManager.QLImport> defaultImport,
                        boolean debug, Consumer<String> debugInfoConsumer,
                        QLSecurityStrategy securityStrategy,
                        List<ExtensionFunction> extensionFunctions, boolean allowPrivateAccess,
                        InterpolationMode interpolationMode, boolean traceExpression) {
        this.classSupplier = classSupplier;
        this.defaultImport = defaultImport;
        this.debug = debug;
        this.debugInfoConsumer = debugInfoConsumer;
        this.securityStrategy = securityStrategy;
        this.extensionFunctions = extensionFunctions;
        this.allowPrivateAccess = allowPrivateAccess;
        this.interpolationMode = interpolationMode;
        this.traceExpression = traceExpression;
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

    public boolean isTraceExpression() {
        return traceExpression;
    }

    public static class Builder {
        private ClassSupplier classSupplier = DefaultClassSupplier.getInstance();
        private final List<ImportManager.QLImport> defaultImport = new ArrayList<>(Arrays.asList(
                ImportManager.importPack("java.lang"),
                ImportManager.importPack("java.util"),
                ImportManager.importPack("java.math"),
                ImportManager.importPack("java.util.stream"),
                ImportManager.importPack("java.util.function")
        ));
        private boolean debug = false;
        private Consumer<String> debugInfoConsumer = System.out::println;
        private QLSecurityStrategy securityStrategy = QLSecurityStrategy.isolation();
        private final List<ExtensionFunction> extensionFunctions = new ArrayList<>(Arrays.asList(
                FilterExtensionFunction.INSTANCE,
                MapExtensionFunction.INSTANCE
        ));
        private boolean allowPrivateAccess = false;
        private InterpolationMode interpolationMode = InterpolationMode.SCRIPT;
        private boolean traceExpression = false;

        public Builder classSupplier(ClassSupplier classSupplier) {
            this.classSupplier = classSupplier;
            return this;
        }

        public Builder addDefaultImport(List<ImportManager.QLImport> defaultImport) {
            this.defaultImport.addAll(defaultImport);
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

        public Builder addExtensionFunctions(List<ExtensionFunction> extensionFunctions) {
            this.extensionFunctions.addAll(extensionFunctions);
            return this;
        }

        public Builder allowPrivateAccess(boolean allowPrivateAccess) {
            this.allowPrivateAccess = allowPrivateAccess;
            return this;
        }

        public Builder interpolationMode(InterpolationMode interpolationMode) {
            this.interpolationMode = interpolationMode;
            return this;
        }

        public Builder traceExpression(boolean traceExpression) {
            this.traceExpression = traceExpression;
            return this;
        }

        public InitOptions build() {
            return new InitOptions(classSupplier, defaultImport,
                    debug, debugInfoConsumer, securityStrategy, extensionFunctions,
                    allowPrivateAccess, interpolationMode, traceExpression);
        }
    }
}
