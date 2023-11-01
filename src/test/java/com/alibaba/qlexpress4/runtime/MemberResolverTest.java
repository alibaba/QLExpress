package com.alibaba.qlexpress4.runtime;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.Optional;

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
        Optional<Constructor<?>> constructor = MemberResolver.resolveConstructor(
                HelloMemberResolver.class, new Class[]{Integer.class}
        );
        assertEquals(long.class, constructor.get().getParameterTypes()[0]);

        Optional<Constructor<?>> constructor1 = MemberResolver.resolveConstructor(
                HelloMemberResolver.class, new Class[]{Long.class, QLambda.class}
        );
        assertEquals(Runnable.class, constructor1.get().getParameterTypes()[1]);
    }

    @Test
    public void resolvePriorityTest() {
        int result = MemberResolver.resolvePriority(new Class[]{boolean.class}, new Class[]{Boolean.class});
        assertEquals(MemberResolver.MatchPriority.UNBOX.priority, result);

        int result1 = MemberResolver.resolvePriority(new Class[]{}, new Class[]{});
        assertEquals(MemberResolver.MatchPriority.EQUAL.priority, result1);
    }
}