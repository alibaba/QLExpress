package com.ql.util.express.instruction.op;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class OperatorLikeTest {

    @Test
    public void matchPatternTest() {
        assertTrue(OperatorLike.matchPattern("abdbc", "a%bc"));
        assertTrue(OperatorLike.matchPattern("1006", "1006"));
        assertFalse(OperatorLike.matchPattern("1006", "6%"));
        assertTrue(OperatorLike.matchPattern("1006", "%6"));
        assertTrue(OperatorLike.matchPattern("1006", "%0%6%"));
        assertFalse(OperatorLike.matchPattern("1006", "%6%1%"));
        assertFalse(OperatorLike.matchPattern("1006", "%2%6%"));
        assertTrue(OperatorLike.matchPattern("1006", "1%"));
        assertTrue(OperatorLike.matchPattern("[1006]", "[1%]"));
        assertTrue(OperatorLike.matchPattern("acc", "a%c"));
        assertTrue(OperatorLike.matchPattern("acdc", "a%c"));
        assertFalse(OperatorLike.matchPattern("ABC", "ABE"));
        assertFalse(OperatorLike.matchPattern("ABC", "ABCD"));
        assertTrue(OperatorLike.matchPattern("ABC", "AB%"));
        assertTrue(OperatorLike.matchPattern("ABC", "ABC%"));
        assertFalse(OperatorLike.matchPattern("ABC", "%AB"));
        assertFalse(OperatorLike.matchPattern("ABC", "A%B"));
        assertTrue(OperatorLike.matchPattern("ABCD", "A%B%"));
        assertTrue(OperatorLike.matchPattern("ABCD", "A%B%D"));
        assertFalse(OperatorLike.matchPattern("CCDD", "%B%D"));
        assertTrue(OperatorLike.matchPattern("CBD", "%B%D"));
        assertTrue(OperatorLike.matchPattern("CBEED", "%B%D"));
        assertTrue(OperatorLike.matchPattern("CBEED", "%%"));
        assertTrue(OperatorLike.matchPattern("CBEED", "%%%%%%"));
        assertTrue(OperatorLike.matchPattern("CBEED", "%%%%%"));
        assertTrue(OperatorLike.matchPattern("CBEED", "%%E%%"));
        assertTrue(OperatorLike.matchPattern("CBEED", "%%E%D"));
        assertTrue(OperatorLike.matchPattern("CBEED", "%B%E%D%"));
        assertTrue(OperatorLike.matchPattern("CBEED", "%CBE%"));
    }

}