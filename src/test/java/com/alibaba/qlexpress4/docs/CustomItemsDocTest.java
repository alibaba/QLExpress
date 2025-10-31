package com.alibaba.qlexpress4.docs;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLPrecedences;
import com.alibaba.qlexpress4.api.QLFunctionalVarargs;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Examples used by docs/custom-item-source.adoc
 */
public class CustomItemsDocTest {
    
    @Test
    public void addFunctionWithJavaFunctionalTest() {
        // tag::addFunctionWithJavaFunctional[]
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        // Function<T,R>
        runner.addFunction("inc", (Function<Integer, Integer>)x -> x + 1);
        // Predicate<T>
        runner.addFunction("isPos", (Predicate<Integer>)x -> x > 0);
        // Runnable
        runner.addFunction("notify", () -> {
        });
        // Consumer<T>
        runner.addFunction("print", (Consumer<Object>)System.out::println);
        
        Object r1 = runner.execute("inc(1)", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        Object r2 = runner.execute("isPos(1)", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(2, r1);
        assertEquals(true, r2);
        // end::addFunctionWithJavaFunctional[]
    }
    
    // tag::joinFunctionVarargsObj[]
    public static class JoinFunction implements QLFunctionalVarargs {
        @Override
        public Object call(Object... params) {
            return Arrays.stream(params).map(Object::toString).collect(Collectors.joining(","));
        }
    }
    // end::joinFunctionVarargsObj[]
    
    @Test
    public void addFunctionByVarargsTest() {
        // tag::addFunctionByVarargs[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        express4Runner.addVarArgsFunction("join", new JoinFunction());
        Object resultFunction =
            express4Runner.execute("join(1,2,3)", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals("1,2,3", resultFunction);
        // end::addFunctionByVarargs[]
    }
    
    @Test
    public void addOperatorWithPrecedenceTest() {
        // tag::addOperatorWithPrecedence[]
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.addOperator("?><", (left, right) -> left.get().toString() + right.get().toString(), QLPrecedences.ADD);
        Object r = runner.execute("1 ?>< 2 * 3", new HashMap<>(), QLOptions.DEFAULT_OPTIONS).getResult();
        // precedence set to ADD, so multiply first, then custom operator: "1" + "6" => "16"
        assertEquals("16", r);
        // end::addOperatorWithPrecedence[]
    }
    
    @Test
    public void replaceDefaultOperatorTest() {
        // tag::replaceDefaultOperator[]
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        boolean ok = runner.replaceDefaultOperator("+",
            (left, right) -> Double.parseDouble(left.get().toString()) + Double.parseDouble(right.get().toString()));
        assertTrue(ok);
        Object r = runner.execute("'1.2' + '2.3'", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(3.5d, r);
        // end::replaceDefaultOperator[]
    }
    
    @Test
    public void addOperatorBiFunctionTest() {
        // tag::addOperatorBiFunction[]
        Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        express4Runner.addOperatorBiFunction("join", (left, right) -> left + "," + right);
        Object resultOperator =
            express4Runner.execute("1 join 2 join 3", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals("1,2,3", resultOperator);
        // end::addOperatorBiFunction[]
    }
    
    @Test
    public void addOperatorByVarargsTest() {
        // tag::addOperatorByVarargs[]
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.addOperator("join", params -> params[0] + "," + params[1]);
        Object r = runner.execute("1 join 2", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals("1,2", r);
        // end::addOperatorByVarargs[]
    }
    
    @Test
    public void qlfunctionalvarargsAllInOneTest() {
        // tag::qlfunctionalvarargsAllInOne[]
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        QLFunctionalVarargs allInOne = params -> {
            // sum numbers no matter how many args
            double sum = 0d;
            for (Object p : params) {
                if (p instanceof Number) {
                    sum += ((Number)p).doubleValue();
                }
            }
            return sum;
        };
        
        // as function
        runner.addVarArgsFunction("sumAll", allInOne);
        // as operator
        runner.addOperator("+&", allInOne);
        // as extension function: first arg is the receiver, followed by call arguments
        runner.addExtendFunction("plusAll", Number.class, allInOne);
        
        Map<String, Object> ctx = new HashMap<>();
        Object rf = runner.execute("sumAll(1,2,3)", ctx, QLOptions.DEFAULT_OPTIONS).getResult();
        Object ro = runner.execute("1 +& 4", ctx, QLOptions.DEFAULT_OPTIONS).getResult();
        Object re = runner.execute("1.plusAll(5)", ctx, QLOptions.DEFAULT_OPTIONS).getResult();
        assertEquals(6d, rf);
        assertEquals(5d, ro);
        assertEquals(6d, re);
        // end::qlfunctionalvarargsAllInOne[]
    }
}
