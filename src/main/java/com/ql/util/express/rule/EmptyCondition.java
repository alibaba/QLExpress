package com.ql.util.express.rule;

/**
 * <p>
 * used for else statement <br>
 * Created by hongkai.wang on 2020/11/12.
 * </p>
 */
public class EmptyCondition extends Condition {
    public static EmptyCondition INSTANCE = new EmptyCondition();

    @Override
    public String getText() {
        return "";
    }

    @Override
    public String toString() {
        return "empty condition";
    }
}
