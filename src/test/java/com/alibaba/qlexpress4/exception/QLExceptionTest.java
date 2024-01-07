package com.alibaba.qlexpress4.exception;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class QLExceptionTest {

    @Test
    public void reportTest() {
        String script = "if (3>1) {\n" +
                "  break 9;\n" +
                "} else {\n" +
                "  return 11;\n" +
                "}";
        QLSyntaxException qlSyntaxException = QLException.reportScannerErr(script, 13, 2, 3,
                "break", "BREAK_MUST_IN_FOR_OR_WHILE", "break must in for/while");
        assertEquals("[Error BREAK_MUST_IN_FOR_OR_WHILE: break must in for/while]\n" +
                "[Near: if (3>1) {   break 9; } else {   retur...]\n" +
                "                    ^^^^^\n" +
                "[Line: 2, Column: 3]", qlSyntaxException.getMessage());
    }

}