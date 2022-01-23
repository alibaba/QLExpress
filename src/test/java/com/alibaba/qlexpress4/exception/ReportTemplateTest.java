package com.alibaba.qlexpress4.exception;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReportTemplateTest {

    @Test
    public void reportTest() {
        String script = "if (3>1) {\n" +
                "  break 9;\n" +
                "} else {\n" +
                "  return 11;\n" +
                "}";
        String reportSrt = ReportTemplate.report(
                script, 18, 2, 8, "break",
                "break must in for/while");
        assertEquals("[Error: break must in for/while]\n" +
                "[Near: (3>1) {   break 9; } else]\n" +
                "                 ^^^^^\n" +
                "[Line: 2, Column: 3]", reportSrt);
    }

}