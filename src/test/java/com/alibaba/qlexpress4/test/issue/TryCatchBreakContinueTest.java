package com.alibaba.qlexpress4.test.issue;

import java.util.HashMap;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for break/continue inside try-catch blocks within loops.
 *
 * Bug: TryCatchInstruction.execute() only propagated RETURN result type,
 * but not BREAK or CONTINUE. This caused break/continue inside try blocks
 * to be silently ignored when enclosed in a loop.
 */
public class TryCatchBreakContinueTest {

    private static final InitOptions INIT_OPTIONS = InitOptions.builder()
        .securityStrategy(QLSecurityStrategy.open()).build();

    @Test
    public void breakInsideTryShouldExitLoop() {
        Express4Runner runner = new Express4Runner(INIT_OPTIONS);
        // break inside try block should terminate the for loop
        String script = "result = 0;\n"
            + "for(int i = 0; i < 10; i++) {\n"
            + "  try {\n"
            + "    if (i == 3) { break; }\n"
            + "    result = result + 1;\n"
            + "  } catch(Exception e) {}\n"
            + "}\n"
            + "result;";
        Object result = runner.execute(script, new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        // Loop should execute for i=0,1,2 then break at i=3
        Assert.assertEquals(3, result);
    }

    @Test
    public void continueInsideTryShouldSkipIteration() {
        Express4Runner runner = new Express4Runner(INIT_OPTIONS);
        // continue inside try block should skip the rest of the iteration
        String script = "result = 0;\n"
            + "for(int i = 0; i < 5; i++) {\n"
            + "  try {\n"
            + "    if (i == 2 || i == 4) { continue; }\n"
            + "    result = result + i;\n"
            + "  } catch(Exception e) {}\n"
            + "}\n"
            + "result;";
        Object result = runner.execute(script, new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        // Should sum 0+1+3 = 4 (skipping i=2 and i=4)
        Assert.assertEquals(4, result);
    }

    @Test
    public void breakInsideTryWithFinallyShouldExitLoop() {
        Express4Runner runner = new Express4Runner(INIT_OPTIONS);
        // break inside try block with finally should still exit the loop
        String script = "result = 0;\n"
            + "for(int i = 0; i < 10; i++) {\n"
            + "  try {\n"
            + "    if (i == 2) { break; }\n"
            + "    result = result + 1;\n"
            + "  } catch(Exception e) {\n"
            + "  } finally {\n"
            + "  }\n"
            + "}\n"
            + "result;";
        Object result = runner.execute(script, new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        // Loop should execute for i=0,1 then break at i=2
        Assert.assertEquals(2, result);
    }

    @Test
    public void continueInsideTryWithFinallyShouldSkipIteration() {
        Express4Runner runner = new Express4Runner(INIT_OPTIONS);
        // continue inside try with finally should skip iteration
        String script = "result = 0;\n"
            + "for(int i = 0; i < 5; i++) {\n"
            + "  try {\n"
            + "    if (i == 3) { continue; }\n"
            + "    result = result + i;\n"
            + "  } catch(Exception e) {\n"
            + "  } finally {\n"
            + "  }\n"
            + "}\n"
            + "result;";
        Object result = runner.execute(script, new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        // Should sum 0+1+2+4 = 7 (skipping i=3)
        Assert.assertEquals(7, result);
    }

    @Test
    public void breakInsideCatchShouldExitLoop() {
        Express4Runner runner = new Express4Runner(INIT_OPTIONS);
        // break inside catch block should also exit the loop
        String script = "result = 0;\n"
            + "for(int i = 0; i < 10; i++) {\n"
            + "  try {\n"
            + "    if (i == 3) { throw new RuntimeException(\"stop\"); }\n"
            + "    result = result + 1;\n"
            + "  } catch(Exception e) {\n"
            + "    break;\n"
            + "  }\n"
            + "}\n"
            + "result;";
        Object result = runner.execute(script, new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        // Loop should execute for i=0,1,2 then throw at i=3, catch breaks
        Assert.assertEquals(3, result);
    }

    @Test
    public void continueInsideCatchShouldSkipIteration() {
        Express4Runner runner = new Express4Runner(INIT_OPTIONS);
        // continue inside catch block should skip to next iteration
        String script = "result = 0;\n"
            + "for(int i = 0; i < 5; i++) {\n"
            + "  try {\n"
            + "    if (i == 2) { throw new RuntimeException(\"skip\"); }\n"
            + "    result = result + i;\n"
            + "  } catch(Exception e) {\n"
            + "    continue;\n"
            + "  }\n"
            + "}\n"
            + "result;";
        Object result = runner.execute(script, new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        // i=0: result+=0=0, i=1: result+=1=1, i=2: throw->catch->continue (skip),
        // i=3: result+=3=4, i=4: result+=4=8
        Assert.assertEquals(8, result);
    }

    @Test
    public void breakInsideWhileTryShouldExitLoop() {
        Express4Runner runner = new Express4Runner(INIT_OPTIONS);
        // break inside try in a while loop
        String script = "i = 0; result = 0;\n"
            + "while(i < 10) {\n"
            + "  try {\n"
            + "    if (i == 5) { break; }\n"
            + "    result = result + 1;\n"
            + "  } catch(Exception e) {}\n"
            + "  i = i + 1;\n"
            + "}\n"
            + "result;";
        Object result = runner.execute(script, new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        // Loop should execute for i=0,1,2,3,4 then break at i=5
        Assert.assertEquals(5, result);
    }

    @Test
    public void breakInsideNestedTryShouldExitLoop() {
        Express4Runner runner = new Express4Runner(INIT_OPTIONS);
        // break inside nested try blocks
        String script = "result = 0;\n"
            + "for(int i = 0; i < 10; i++) {\n"
            + "  try {\n"
            + "    try {\n"
            + "      if (i == 4) { break; }\n"
            + "      result = result + 1;\n"
            + "    } catch(Exception e) {}\n"
            + "  } catch(Exception e) {}\n"
            + "}\n"
            + "result;";
        Object result = runner.execute(script, new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        // Should execute for i=0,1,2,3 then break at i=4
        Assert.assertEquals(4, result);
    }
}
