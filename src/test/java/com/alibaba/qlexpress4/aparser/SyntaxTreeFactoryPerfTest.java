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
    
    @Test(timeout = 1000)
    public void complexIfTestWithProfile()
        throws URISyntaxException, IOException {
        String complexIfConditionExpress =
            new String(Files.readAllBytes(getPerfRoot().resolve("complex_if_condition.ql")));
        SyntaxTreeFactory.buildTree(complexIfConditionExpress,
            new OperatorManager(),
            false,
            System.out::println,
            InterpolationMode.SCRIPT,
            "${",
            "}",
            true);
    }
    
    @Test(timeout = 1000)
    public void longOneLineSimpleProfile()
        throws URISyntaxException, IOException {
        String complexIfConditionExpress =
            new String(Files.readAllBytes(getPerfRoot().resolve("long_one_line_simple.ql")));
        SyntaxTreeFactory.buildTree(complexIfConditionExpress,
            new OperatorManager(),
            false,
            System.out::println,
            InterpolationMode.SCRIPT,
            "${",
            "}",
            true);
    }
    
    @Test(timeout = 1000)
    public void longOneLineProfile()
        throws URISyntaxException, IOException {
        String complexIfConditionExpress = new String(Files.readAllBytes(getPerfRoot().resolve("long_one_line.ql")));
        SyntaxTreeFactory.buildTree(complexIfConditionExpress,
            new OperatorManager(),
            false,
            System.out::println,
            InterpolationMode.SCRIPT,
            "${",
            "}",
            true);
    }
    
    @Test(timeout = 2000)
    public void longOneLineFormatProfile()
        throws URISyntaxException, IOException {
        String complexIfConditionExpress =
            new String(Files.readAllBytes(getPerfRoot().resolve("long_one_line_format.ql")));
        SyntaxTreeFactory.buildTree(complexIfConditionExpress,
            new OperatorManager(),
            false,
            System.out::println,
            InterpolationMode.SCRIPT,
            "${",
            "}",
            true);
    }
    
    @Test(timeout = 1000)
    public void oneLineIfTest() {
        String express =
            "Y_D_ / (if (( item in  [ 'S45C','SUS304' ]   &&  Y_D_ < 65 )) { 1500; } else if (( item in  [ 'S45C','SUS304' ]   &&  Y_D_ < 80  &&  Y_D_ >= 65 )) { 1200; }  else if (( item in  [ 'S45C','SUS304' ]   &&  Y_D_ < 80  &&  Y_D_ >= 65 )) { 1200; } else if (( item in  [ 'S45C','SUS304' ]   &&  Y_D_ < 100  &&  Y_D_ >= 80 )) { 1000; } else if (( item in  [ 'S45C','SUS304' ]   &&  Y_D_ < 120  &&  Y_D_ >= 100 )) { 800; } else if (( item in  [ 'S45C','SUS304' ]   &&  Y_D_ < 150  &&  Y_D_ >= 120 )) { 600; } else if (( item in  [ 'S45C','SUS304' ]   &&  Y_D_ < 180  &&  Y_D_ >= 150 )) { 400; } else if (( item in  [ 'S45C','SUS304' ]   &&  Y_D_ < 200  &&  Y_D_ >= 180 )) { 300; } else if (( item in  [ 'S45C','SUS304' ]   &&  Y_D_ <= 230  &&  Y_D_ >= 200 )) { 200; } else if (( item in  [ '金','POM' ]   &&  Y_D_ < 65 )) { 2000; } else if (( item in  [ '金','POM' ]   &&  Y_D_ < 80  &&  Y_D_ >= 65 )) { 1500; } else if (( item in  [ '金','POM' ]   &&  Y_D_ < 100  &&  Y_D_ >= 80 )) { 1200; } else if (( item in  [ '金','POM' ]   &&  Y_D_ < 150  &&  Y_D_ >= 100 )) { 1000; } else if (( item in  [ '金','POM' ]   &&  Y_D_ < 200  &&  Y_D_ >= 150 )) { 800; } else if (( item in  [ '金','POM' ]   &&  Y_D_ <= 230  &&  Y_D_ >= 200 )) { 600; } else { null } )";
        SyntaxTreeFactory.buildTree(express,
            new OperatorManager(),
            false,
            System.out::println,
            InterpolationMode.SCRIPT,
            "${",
            "}",
            true);
    }
    
    private Path getPerfRoot()
        throws URISyntaxException {
        return Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("perf")).toURI());
    }
    
}
