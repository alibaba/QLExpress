package com.alibaba.qlexpress4;

/**
 * @author 悬衡
 * date 2022/1/12 2:31 下午
 */
public class QLPrecedences {

    // = += -= &= |= *= %=
    // 0;

    // ?:
    // 1;

    // || or
    public static final int OR = 2;

    // && and
    public static final int AND = 3;

    // |
    public static final int BIT_OR = 4;

    // ^
    public static final int XOR = 5;

    // &
    public static final int BIT_AND = 6;

    // == !=
    public static final int EQUAL = 7;

    // < <= > >= instanceof
    public static final int COMPARE = 8;

    // << >> >>>
    public static final int BIT_MOVE = 9;

    // + -
    public static final int ADD = 10;

    // * / %
    public static final int MULTI = 11;

    // ! ++ -- ~ + -
    public static final int UNARY = 12;

    // ()
    public static final int GROUP = 13;

}
