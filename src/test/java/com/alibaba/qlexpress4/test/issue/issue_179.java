package com.alibaba.qlexpress4.test.issue;

import com.alibaba.qlexpress4.runtime.data.instruction.LikeStateMachine;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author TaoKan
 * @Date 2022/12/4 上午9:20
 */
public class issue_179 extends IssueBase {

    @Test
    public void test22() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%%';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test23() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%%%%%%';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test24() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%%%%%';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

     @Test
    public void test25() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%%E%%';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test0() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '1006';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test1() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '6%';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
    @Test
    public void test2() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '%6';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test6() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '1%';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test7() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '%1';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
        @Test
    public void test11() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABC' like 'ABE';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }

    @Test
    public void test12() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABC' like 'ABCD';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }

        @Test
    public void test13() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABC' like 'AB%';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test14() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABC' like 'ABC%';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test15() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABC' like '%AB';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }


    @Test
    public void test28() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%CBE%';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test3() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '%0%6%';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test4() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '%6%1%';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
    @Test
    public void test5() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '1006' like '%2%6%';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
    @Test
    public void test8() {
        Map<String, Object> context = new HashMap<>();
        String script = "return '[1006]' like '[1%]';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test9() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'acc' like 'a%c';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test10() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'acdc' like 'a%c';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }


    @Test
    public void test16() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABC' like 'A%B';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
    @Test
    public void test17() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABCD' like 'A%B%';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test18() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'ABCD' like 'A%B%D';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test19() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CCDD' like '%B%D';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }

    @Test
    public void test94() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'BEEED' like '%B%D';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }
    @Test
    public void test90() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'BEEE' like '%B%D';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
    @Test
    public void test92() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'BE' like '%B%D';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }
    @Test
    public void test91() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'BD' like '%B%D';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test20() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBD' like '%B%D';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test21() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%B%D';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }


    @Test
    public void test26() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%%E%D';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test27() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%B%E%D%';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test40() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%B%E%DE%';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }

    @Test
    public void test41() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEED' like '%B%EF%DE%';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }

    @Test
    public void test33() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'CBEEF' like '%B%E%D%';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }

    @Test
    public void test29() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'abdbcd' like 'a%bc';";
        Object result = execute(script, context);
        Assert.assertTrue(!(Boolean)result);
    }

    @Test
    public void test30() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'abcdbcdbcd' like 'a%bcd';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    @Test
    public void test36() {
        Map<String, Object> context = new HashMap<>();
        String script = "return 'abcdbcdbcd' like 'a%b%%cd';";
        Object result = execute(script, context);
        Assert.assertTrue((Boolean)result);
    }

    /**
     * 左右型-长
     */
    @Test
    public void testBenchMark1() throws InterruptedException {
        Thread.sleep(1000L);
        String pattern = "bccs%das";
        String target = "bccsdass";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        long sa = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            matchPattern(target,pattern);
        }
        System.out.println(System.currentTimeMillis() - sa);
        long s = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            likeStateMachine.match((String) target);
        }
        System.out.println(System.currentTimeMillis() - s);
    }

    @Test
    public void testBenchMark1RightMachine(){
        String pattern = "bccs%das";
        String target = "bccsdass";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        Assert.assertTrue((Boolean)likeStateMachine.match((String) target));
    }

    /**
     * 这个case还有问题
     */
    @Test
    public void testBenchMark1RightNormal(){
        String pattern = "bccs%das";
        String target = "bccsdass";
        Assert.assertTrue((Boolean)matchPattern(target,pattern));
    }
/**
 * 左右型-短
 */
    @Test
    public void testBenchMark2() throws InterruptedException {
        Thread.sleep(1000L);
        String pattern = "b%ac";
        String target = "bac";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        long sa = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            matchPattern(target,pattern);
        }
        System.out.println(System.currentTimeMillis() - sa);
        long s = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            likeStateMachine.match((String) target);
        }
        System.out.println(System.currentTimeMillis() - s);
    }

    @Test
    public void testBenchMark2RightMachine(){
        String pattern = "b%ac";
        String target = "bac";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        Assert.assertTrue((Boolean)likeStateMachine.match((String) target));
    }
    @Test
    public void testBenchMark2RightNormal(){
        String pattern = "b%ac";
        String target = "bac";
        Assert.assertTrue((Boolean)matchPattern(target,pattern));
    }
    /**
     * 左右型-短-回溯
     */
    @Test
    public void testBenchMark3() throws InterruptedException {
        Thread.sleep(1000L);
        String pattern = "b%ac";
        String target = "bacac";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        long sa = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            matchPattern(target,pattern);
        }
        System.out.println(System.currentTimeMillis() - sa);
        long s = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            likeStateMachine.match((String) target);
        }
        System.out.println(System.currentTimeMillis() - s);
    }

    @Test
    public void testBenchMark3RightMachine(){
        String pattern = "b%ac";
        String target = "bacac";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        Assert.assertTrue((Boolean)likeStateMachine.match((String) target));
    }
    @Test
    public void testBenchMark3RightNormal(){
        String pattern = "b%ac";
        String target = "bacac";
        Assert.assertTrue((Boolean)matchPattern(target,pattern));
    }

    /**
     * 左右型-长-回溯
     */
    @Test
    public void testBenchMark4() throws InterruptedException {
        Thread.sleep(1000L);
        String pattern = "be%acd";
        String target = "beacdacdacdacd";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        long sa = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            matchPattern(target,pattern);
        }
        System.out.println(System.currentTimeMillis() - sa);
        long s = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            likeStateMachine.match((String) target);
        }
        System.out.println(System.currentTimeMillis() - s);
    }

    @Test
    public void testBenchMark4RightMachine(){
        String pattern = "be%acd";
        String target = "beacdacdacdacd";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        Assert.assertTrue((Boolean)likeStateMachine.match((String) target));
    }
    @Test
    public void testBenchMark4RightNormal(){
        String pattern = "be%acd";
        String target = "beacdacdacdacd";
        Assert.assertTrue((Boolean)matchPattern(target,pattern));
    }

    /**
     * 双右型-长
     */
    @Test
    public void testBenchMark5() throws InterruptedException {
        Thread.sleep(1000L);
        String pattern = "%be%acd";
        String target = "cdacbecdacdacd";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        long sa = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            matchPattern(target,pattern);
        }
        System.out.println(System.currentTimeMillis() - sa);
        long s = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            likeStateMachine.match((String) target);
        }
        System.out.println(System.currentTimeMillis() - s);
    }

    @Test
    public void testBenchMark5RightMachine(){
        String pattern = "%be%acd";
        String target = "cdacbecdacdacd";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        Assert.assertTrue((Boolean)likeStateMachine.match((String) target));
    }
    @Test
    public void testBenchMark5RightNormal(){
        String pattern = "%be%acd";
        String target = "cdacbecdacdacd";
        Assert.assertTrue((Boolean)matchPattern(target,pattern));
    }

    /**
     * 双右型-短
     */
    @Test
    public void testBenchMark6() throws InterruptedException {
        Thread.sleep(1000L);
        String pattern = "%e%ac";
        String target = "baceac";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        long sa = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            matchPattern(target,pattern);
        }
        System.out.println(System.currentTimeMillis() - sa);
        long s = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            likeStateMachine.match((String) target);
        }
        System.out.println(System.currentTimeMillis() - s);
    }

    @Test
    public void testBenchMark6RightMachine(){
        String pattern = "%e%ac";
        String target = "baceac";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        Assert.assertTrue((Boolean)likeStateMachine.match((String) target));
    }
    @Test
    public void testBenchMark6RightNormal(){
        String pattern = "%e%ac";
        String target = "baceac";
        Assert.assertTrue((Boolean)matchPattern(target,pattern));
    }

    /**
     * 左双型-短
     */
    @Test
    public void testBenchMark7() throws InterruptedException {
        Thread.sleep(1000L);
        String pattern = "e%ac%";
        String target = "eacad";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        long sa = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            matchPattern(target,pattern);
        }
        System.out.println(System.currentTimeMillis() - sa);
        long s = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            likeStateMachine.match((String) target);
        }
        System.out.println(System.currentTimeMillis() - s);
    }

    @Test
    public void testBenchMark7RightMachine(){
        String pattern = "e%ac%";
        String target = "eacad";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        Assert.assertTrue((Boolean)likeStateMachine.match((String) target));
    }
    @Test
    public void testBenchMark7RightNormal(){
        String pattern = "e%ac%";
        String target = "eacad";
        Assert.assertTrue((Boolean)matchPattern(target,pattern));
    }

    /**
     * 左双型-长
     */
    @Test
    public void testBenchMark8() throws InterruptedException {
        Thread.sleep(1000L);
        String pattern = "%efgee%acttsssc";
        String target = "bsaefgeeeeacttsssc";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        long sa = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            matchPattern(target,pattern);
        }
        System.out.println(System.currentTimeMillis() - sa);
        long s = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            likeStateMachine.match((String) target);
        }
        System.out.println(System.currentTimeMillis() - s);
    }

    @Test
    public void testBenchMark8RightMachine(){
        String pattern = "%efgee%acttsssc";
        String target = "bsaefgeeeeacttsssc";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        Assert.assertTrue((Boolean)likeStateMachine.match((String) target));
    }
    @Test
    public void testBenchMark8RightNormal(){
        String pattern = "%efgee%acttsssc";
        String target = "bsaefgeeeeacttsssc";
        Assert.assertTrue((Boolean)matchPattern(target,pattern));
    }
    /**
     * 双双型-短
     */
    @Test
    public void testBenchMark9() throws InterruptedException {
        Thread.sleep(1000L);
        String pattern = "%e%ac%";
        String target = "eaeac";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        long sa = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            matchPattern(target,pattern);
        }
        System.out.println(System.currentTimeMillis() - sa);
        long s = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            likeStateMachine.match((String) target);
        }
        System.out.println(System.currentTimeMillis() - s);
    }


    @Test
    public void testBenchMark9RightMachine(){
        String pattern = "%e%ac%";
        String target = "eaeac";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        Assert.assertTrue((Boolean)likeStateMachine.match((String) target));
    }
    @Test
    public void testBenchMark9RightNormal(){
        String pattern = "%e%ac%";
        String target = "eaeac";
        Assert.assertTrue((Boolean)matchPattern(target,pattern));
    }
    /**
     * 双双型-长
     */
    @Test
    public void testBenchMark10() throws InterruptedException {
        Thread.sleep(1000L);
        String pattern = "%efgee%acttsssc%";
        String target = "bsaefgeeeeacttsssctt";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        long sa = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            matchPattern(target,pattern);
        }
        System.out.println(System.currentTimeMillis() - sa);
        long s = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            likeStateMachine.match((String) target);
        }
        System.out.println(System.currentTimeMillis() - s);
    }


    @Test
    public void testBenchMark10RightMachine(){
        String pattern = "%efgee%acttsssc%";
        String target = "bsaefgeeeeacttsssctt";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        Assert.assertTrue((Boolean)likeStateMachine.match((String) target));
    }
    @Test
    public void testBenchMark10RightNormal(){
        String pattern = "%efgee%acttsssc%";
        String target = "bsaefgeeeeacttsssctt";
        Assert.assertTrue((Boolean)matchPattern(target,pattern));
    }

    /**
     * 多双型-短
     */
    @Test
    public void testBenchMark11() throws InterruptedException {
        Thread.sleep(1000L);
        String pattern = "%e%ac%ef%a";
        String target = "eaeac52efa";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        long sa = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            matchPattern(target,pattern);
        }
        System.out.println(System.currentTimeMillis() - sa);
        long s = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            likeStateMachine.match((String) target);
        }
        System.out.println(System.currentTimeMillis() - s);
    }


    @Test
    public void testBenchMark11RightMachine(){
        String pattern = "e%ac%ef%a";
        String target = "eaeac52efa";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        Assert.assertTrue((Boolean)likeStateMachine.match((String) target));
    }
    @Test
    public void testBenchMark11RightNormal(){
        String pattern = "e%ac%ef%a";
        String target = "eaeac52efa";
        Assert.assertTrue((Boolean)matchPattern(target,pattern));
    }

    /**
     * 多双型-长
     */
    @Test
    public void testBenchMark12() throws InterruptedException {
        Thread.sleep(1000L);
        String pattern = "efgee%acttsssc%eeeee%weee";
        String target = "efgeeeeacttsssceeeee55sweee";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        long sa = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            matchPattern(target,pattern);
        }
        System.out.println(System.currentTimeMillis() - sa);
        long s = System.currentTimeMillis();
        for(int i = 0; i < 100000000; i++){
            likeStateMachine.match((String) target);
        }
        System.out.println(System.currentTimeMillis() - s);
    }



    @Test
    public void testBenchMark12RightMachine(){
        String pattern = "efgee%acttsssc%eeeee%weee";
        String target = "efgeeeeacttsssceeeee55sweee";
        LikeStateMachine likeStateMachine = LikeStateMachine.builder().loadPattern((String)pattern).build();
        Assert.assertTrue((Boolean)likeStateMachine.match((String) target));
    }
    @Test
    public void testBenchMark12RightNormal(){
        String pattern = "efgee%acttsssc%eeeee%weee";
        String target = "efgeeeeacttsssceeeee55sweee";
        Assert.assertTrue((Boolean)matchPattern(target,pattern));
    }


    private static boolean matchPattern(String s, String pattern) {
        int sPointer = 0, pPointer = 0;
        int sLen = s.length(), pLen = pattern.length();
        int sRecall = -1, pRecall = -1;
        while (sPointer < sLen) {
            if (pPointer < pLen && (s.charAt(sPointer) == pattern.charAt(pPointer))) {
                sPointer++;
                pPointer++;
            } else if (pPointer < pLen && pattern.charAt(pPointer) == '%') {
                sRecall = sPointer;
                pRecall = pPointer;
                pPointer++;
            } else if (sRecall >= 0) {
                sPointer = ++sRecall;
                pPointer = pRecall + 1;
            } else {
                return false;
            }
        }
        while (pPointer < pLen && pattern.charAt(pPointer) == '%') {
            pPointer++;
        }
        return pPointer == pLen;
    }
}
