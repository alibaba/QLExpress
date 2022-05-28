package com.alibaba.qlexpress4;


/**
 * @Author TaoKan
 * @Date 2022/5/6 下午6:32
 */
public class InitOptions {

    public static InitOptions DEFAULT_OPTIONS = InitOptions.builder().build();

    private final boolean useCacheClear;

    private InitOptions(boolean useCacheClear) {
        this.useCacheClear = useCacheClear;
    }

    public boolean enableUseCacheClear() {
        return useCacheClear;
    }

    public static InitOptions.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean useCacheClear;

        public Builder useCacheClear(boolean useCacheClear) {
            this.useCacheClear = useCacheClear;
            return this;
        }

        public InitOptions build() {
            return new InitOptions(useCacheClear);
        }
    }
}
