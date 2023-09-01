package com.alibaba.qlexpress4;


/**
 * @Author TaoKan
 * @Date 2022/5/6 下午6:32
 */
public class InitOptions {

    public static InitOptions DEFAULT_OPTIONS = InitOptions.builder().build();

    private final boolean useCacheClear;

    private final ClassSupplier classSupplier;

    /**
     * allow access private field and method
     * default false
     */
    private final boolean allowPrivateAccess;

    private InitOptions(boolean useCacheClear, ClassSupplier classSupplier, boolean allowPrivateAccess) {
        this.useCacheClear = useCacheClear;
        this.classSupplier = classSupplier;
        this.allowPrivateAccess = allowPrivateAccess;
    }

    public static InitOptions.Builder builder() {
        return new Builder();
    }

    public boolean enableUseCacheClear() {
        return useCacheClear;
    }

    public ClassSupplier classSupplier() {
        return classSupplier;
    }

    public boolean allowPrivateAccess() {
        return allowPrivateAccess;
    }

    public static class Builder {
        private boolean useCacheClear = false;
        private ClassSupplier classSupplier = DefaultClassSupplier.getInstance();
        private boolean allowPrivateAccess = false;

        public Builder useCacheClear(boolean useCacheClear) {
            this.useCacheClear = useCacheClear;
            return this;
        }

        public Builder classSupplier(ClassSupplier classSupplier) {
            this.classSupplier = classSupplier;
            return this;
        }

        public Builder allowPrivateAccess(boolean allowPrivateAccess) {
            this.allowPrivateAccess = allowPrivateAccess;
            return this;
        }

        public InitOptions build() {
            return new InitOptions(useCacheClear, classSupplier, allowPrivateAccess);
        }
    }
}
