package com.alibaba.qlexpress4.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: DQinYuan
 */
public class BatchAddFunctionResult {

    private final List<String> succ;
    private final List<String> fail;

    public BatchAddFunctionResult() {
        this.succ = new ArrayList<>();
        this.fail = new ArrayList<>();
    }

    public List<String> getSucc() {
        return succ;
    }

    public List<String> getFail() {
        return fail;
    }
}
