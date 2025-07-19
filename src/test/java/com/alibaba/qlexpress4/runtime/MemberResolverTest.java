package com.alibaba.qlexpress4.runtime;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.Predicate;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class MemberResolverTest {
    
    public static class HelloMemberResolver {
        
        public HelloMemberResolver(Number i) {
            
        }
        
        public HelloMemberResolver(long i) {
            
        }
        
        public HelloMemberResolver(Long i, Runnable runnable) {
            
        }
        
        public HelloMemberResolver(long i, Runnable runnable) {
            
        }
    }
    
    @Test
    public void resolveConstructorTest() {
        Constructor<?> constructor =
            MemberResolver.resolveConstructor(HelloMemberResolver.class, new Class[] {Integer.class});
        assertEquals(long.class, constructor.getParameterTypes()[0]);
        
        Constructor<?> constructor1 =
            MemberResolver.resolveConstructor(HelloMemberResolver.class, new Class[] {Long.class, QLambda.class});
        assertEquals(Runnable.class, constructor1.getParameterTypes()[1]);
    }
    
    @Test
    public void resolvePriorityTest() {
        int result = MemberResolver.resolvePriority(new Class[] {boolean.class}, new Class[] {Boolean.class});
        assertEquals(MemberResolver.MatchPriority.UNBOX.priority, result);
        
        int result1 = MemberResolver.resolvePriority(new Class[] {}, new Class[] {});
        assertEquals(MemberResolver.MatchPriority.EQUAL.priority, result1);
    }
    
    @Test
    public void resolveStreamTest() {
        Method result = MemberResolver
            .resolveMethod(new ArrayList().stream().getClass(), "filter", new Class[] {Predicate.class}, false, false);
        assertNotNull(result);
    }
}