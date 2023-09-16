package com.alibaba.qlexpress4.test.property;

import java.math.BigDecimal;

/**
 * Author: TaoKan
 */
public class BugFixTest1 {
    public BugFixTest1(double a){

    }

    public BugFixTest1(Number a){

    }

    public BugFixTest1(BigDecimal a){

    }

    public BugFixTest1(String a){

    }
}
