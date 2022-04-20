package com.alibaba.qlexpress4;

/**
 * Comment By 冰够 Precedence > Priority ?
 *
 * Author: DQinYuan
 * date 2022/1/12 2:31 下午
 */
public class QLPrecedences {

    /**
     * Comment By 冰够，注释使用块注释
     */
    // = += -= &= |= *= /= %= <<= >>=
    public static final int ASSIGN = 0;

    // ?:
    public static final int TERNARY = 1;

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

    // in like
    public static final int IN_LIKE = 10;

    // + -
    public static final int ADD = 11;

    // * / %
    public static final int MULTI = 12;

    // ! ++ -- ~ + -
    public static final int UNARY = 13;

    // ++ -- in suffix, like i++
    public static final int UNARY_SUFFIX = 14;

    // ()
    public static final int GROUP = 15;

}
