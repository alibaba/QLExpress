package com.alibaba.qlexpress4;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Objects;

public class ClearDfaCacheTest {
    
    private static final DecimalFormat MEM_FORMAT = new DecimalFormat("#,###.##");
    
    @Test
    public void clearDFACacheTest()
        throws URISyntaxException, IOException {
        String complexDataProcessingExpress =
            new String(Files.readAllBytes(getPerfRoot().resolve("complexDataProcessing.ql")));
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        double beforeMemoryUsed = getMemoryUsedMB();
        runner.parseToSyntaxTree(complexDataProcessingExpress);
        runner.clearDFACache();
        Assert.assertTrue(getMemoryUsedMB() <= beforeMemoryUsed + 1);
    }
    
    @Test
    public void bestPractice()
        throws URISyntaxException, IOException {
        String exampleExpress = "1+1";
        double beforeMemoryUsed = getMemoryUsedMB();
        // tag::clearDFACacheBestPractice[]
        /*
         * When the expression changes, parse it and add it to the expression cache;
         * after parsing is complete, call clearDFACache.
         */
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.parseToDefinitionWithCache(exampleExpress);
        runner.clearDFACache();
        
        /*
         * All subsequent runs of this script must enable the cache option to ensure that re-compilation does not occur.
         */
        for (int i = 0; i < 3; i++) {
            runner.execute(exampleExpress, Collections.emptyMap(), QLOptions.builder().cache(true).build());
        }
        // end::clearDFACacheBestPractice[]
        Assert.assertTrue(getMemoryUsedMB() <= beforeMemoryUsed);
    }
    
    private double getMemoryUsedMB() {
        System.gc();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        long memory = heap.getUsed();
        return memory / (1024.0 * 1024.0);
    }
    
    private Path getPerfRoot()
        throws URISyntaxException {
        return Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("perf")).toURI());
    }
}
