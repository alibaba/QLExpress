package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class SyntaxTreeFactoryPerfTest {
    
    @Test
    public void complexIfTestWithProfile()
        throws URISyntaxException, IOException {
        String complexIfConditionExpress =
            new String(Files.readAllBytes(getPerfRoot().resolve("complex_if_condition.ql")));
        long start = System.currentTimeMillis();
        SyntaxTreeFactory.buildTree(complexIfConditionExpress,
            new OperatorManager(),
            false,
            false,
            System.out::println,
            InterpolationMode.SCRIPT,
            "${",
            "}",
            true);
        long costMs = System.currentTimeMillis() - start;
        Assert.assertTrue(costMs < 1000L);
    }
    
    @Test
    public void longOneLineSimpleProfile()
        throws URISyntaxException, IOException {
        String complexIfConditionExpress =
            new String(Files.readAllBytes(getPerfRoot().resolve("long_one_line_simple.ql")));
        long start = System.currentTimeMillis();
        SyntaxTreeFactory.buildTree(complexIfConditionExpress,
            new OperatorManager(),
            false,
            false,
            System.out::println,
            InterpolationMode.SCRIPT,
            "${",
            "}",
            true); // Use strictNewLines=false for performance
        long costMs = System.currentTimeMillis() - start;
        Assert.assertTrue("Compilation should complete within 1 seconds", costMs < 1000L);
    }
    
    @Test
    public void longOneLineProfile()
        throws URISyntaxException, IOException {
        String complexIfConditionExpress = new String(Files.readAllBytes(getPerfRoot().resolve("long_one_line.ql")));
        long start = System.currentTimeMillis();
        SyntaxTreeFactory.buildTree(complexIfConditionExpress,
            new OperatorManager(),
            false,
            false,
            System.out::println,
            InterpolationMode.SCRIPT,
            "${",
            "}",
            true); // Use strictNewLines=false for performance
        long costMs = System.currentTimeMillis() - start;
        Assert.assertTrue("Compilation should complete within 1 seconds", costMs < 1000L);
    }
    
    @Test
    public void longOneLineFormatProfile()
        throws URISyntaxException, IOException {
        String complexIfConditionExpress =
            new String(Files.readAllBytes(getPerfRoot().resolve("long_one_line_format.ql")));
        long start = System.currentTimeMillis();
        SyntaxTreeFactory.buildTree(complexIfConditionExpress,
            new OperatorManager(),
            false,
            false,
            System.out::println,
            InterpolationMode.SCRIPT,
            "${",
            "}",
            true); // Use strictNewLines=false for performance
        long costMs = System.currentTimeMillis() - start;
        Assert.assertTrue("Compilation should complete within 1 seconds", costMs < 1000L);
    }
    
    private Path getPerfRoot()
        throws URISyntaxException {
        return Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("perf")).toURI());
    }
    
}
