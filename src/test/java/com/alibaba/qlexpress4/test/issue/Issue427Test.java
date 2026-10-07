package com.alibaba.qlexpress4.test.issue;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import org.junit.Test;
import java.util.HashMap;
import static org.junit.Assert.*;

/**
 * Regression tests for issue #427:
 * Empty for loop body causes subsequent expressions to return null.
 * 
 * Root cause: QLambdaEmpty.call() returned QResult(NULL_VALUE, RETURN)
 * instead of QResult(NULL_VALUE, NEXT_INSTRUCTION), causing the for loop
 * to propagate a RETURN signal that terminated the entire script.
 */
public class Issue427Test {
    
    private final Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
    
    @Test
    public void emptyForLoop_shouldNotAffectSubsequentExpression() {
        // Core regression: empty for body + trailing expression
        Object result =
            runner.execute("for(int i=0;i<5;i++){} 1;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(1, result);
    }
    
    @Test
    public void emptyForLoop_conditionNeverMet_shouldNotAffectSubsequentExpression() {
        // Loop condition false from start
        Object result =
            runner.execute("for(int i=0;i<0;i++){i++;} 1;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(1, result);
    }
    
    @Test
    public void emptyWhileLoop_shouldNotAffectSubsequentExpression() {
        Object result = runner.execute("while(false){} 1;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(1, result);
    }
    
    @Test
    public void forLoopWithExplicitReturn_shouldReturnCorrectly() {
        // Explicit return inside for loop should still work
        Object result =
            runner.execute("for(int i=0;i<5;i++){return 42;} 1;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS)
                .getResult();
        assertEquals(42, result);
    }
    
    @Test
    public void emptyForEachLoop_shouldNotAffectSubsequentExpression() {
        // Empty for-each with empty collection
        Object result =
            runner.execute("a = []; for(int item : a){} 1;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(1, result);
    }
    
    @Test
    public void emptyForLoop_withSemicolonBody_shouldWork() {
        // for loop with just semicolon as body
        Object result =
            runner.execute("for(int i=0;i<5;i++){;} 1;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(1, result);
    }
    
    @Test
    public void nonEmptyForLoop_shouldStillWork() {
        // Verify normal for loops still work correctly
        Object result =
            runner.execute("a=0; for(int i=0;i<5;i++){a=a+i;} a;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS)
                .getResult();
        assertEquals(10, result); // 0+1+2+3+4 = 10
    }
    
    @Test
    public void emptyForLoop_multipleStatementsAfter() {
        // Multiple statements after empty for loop
        Object result =
            runner.execute("for(int i=0;i<3;i++){} a=10; b=20; a+b;", new HashMap<>(), QLOptions.DEFAULT_OPTIONS)
                .getResult();
        assertEquals(30, result);
    }
}
