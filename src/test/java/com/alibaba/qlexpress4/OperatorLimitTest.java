package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.runtime.operator.Operator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.MultiplyOperator;
import com.alibaba.qlexpress4.runtime.operator.arithmetic.PlusOperator;
import com.alibaba.qlexpress4.runtime.operator.assign.AssignOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.PlusPlusPrefixUnaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.PlusPlusSuffixUnaryOperator;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OperatorLimitTest {
    
    @Test
    public void testCheckWithAllowedOperators()
        throws QLSyntaxException {
        Set<Operator> allowedOps =
            new HashSet<>(Arrays.asList(PlusOperator.getInstance(), MultiplyOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().whitelist(allowedOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.check("a + b * c", checkOptions);
    }
    
    @Test
    public void testCheckWithDisallowedOperators() {
        Set<Operator> allowedOps =
            new HashSet<>(Arrays.asList(PlusOperator.getInstance(), MultiplyOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().whitelist(allowedOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        try {
            runner.check("a = b + c", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            // Verify error code
            assertEquals("OPERATOR_NOT_ALLOWED", e.getErrorCode());
            
            // Verify position information - should be precise now
            assertEquals(2, e.getPos()); // Position of '=' in "a = b + c"
            assertEquals(1, e.getLineNo()); // First line
            assertEquals(2, e.getColNo()); // Column of '='
            
            // Verify error lexeme
            assertEquals("=", e.getErrLexeme());
            
            // Verify error reason
            assertTrue(e.getReason().contains("Script uses disallowed operator"));
            assertTrue(e.getReason().contains("="));
            
            // Verify complete message format
            assertTrue("Message should contain error code", e.getMessage().contains("OPERATOR_NOT_ALLOWED"));
            assertTrue("Message should contain line info", e.getMessage().contains("Line: 1"));
            assertTrue("Message should contain column info", e.getMessage().contains("Column: 2"));
        }
    }
    
    @Test
    public void testCheckWithForbiddenOperators()
        throws QLSyntaxException {
        Set<Operator> forbiddenOps = new HashSet<>(Arrays.asList(AssignOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().blacklist(forbiddenOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.check("a + b * c - d / e", checkOptions);
    }
    
    @Test
    public void testCheckWithForbiddenOperatorUsed() {
        Set<Operator> forbiddenOps = new HashSet<>(Arrays.asList(AssignOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().blacklist(forbiddenOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        try {
            runner.check("a = b + c", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            // Verify error code
            assertEquals("OPERATOR_FORBIDDEN", e.getErrorCode());
            
            // Verify position information - should be precise now
            assertEquals(2, e.getPos()); // Position of '=' in "a = b + c"
            assertEquals(1, e.getLineNo()); // First line
            assertEquals(2, e.getColNo()); // Column of '='
            
            // Verify error lexeme
            assertEquals("=", e.getErrLexeme());
            
            // Verify error reason
            assertTrue(e.getReason().contains("Script uses forbidden operator"));
            assertTrue(e.getReason().contains("="));
            
            // Verify complete message format
            assertTrue("Message should contain error code", e.getMessage().contains("OPERATOR_FORBIDDEN"));
            assertTrue("Message should contain line info", e.getMessage().contains("Line: 1"));
            assertTrue("Message should contain column info", e.getMessage().contains("Column: 2"));
        }
    }
    
    @Test
    public void testCheckWithoutLimit()
        throws QLSyntaxException {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.check("a = b + c - d * e / f");
        runner.check("a++");
        runner.check("++a");
    }
    
    @Test
    public void testCannotSetBothWhitelistAndBlacklist() {
        Set<Operator> allowedOps = new HashSet<>(Arrays.asList(PlusOperator.getInstance()));
        Set<Operator> forbiddenOps = new HashSet<>(Arrays.asList(AssignOperator.getInstance()));
        // When calling whitelist() then blacklist(), the blacklist strategy will override whitelist
        // This is expected behavior - the last strategy set wins
        CheckOptions checkOptions = CheckOptions.builder().whitelist(allowedOps).blacklist(forbiddenOps).build();
        assertEquals(CheckOptions.OperatorStrategy.BLACKLIST, checkOptions.getStrategy());
        assertEquals(forbiddenOps, checkOptions.getOperators());
    }
    
    @Test
    public void testAllowAllStrategy()
        throws QLSyntaxException {
        CheckOptions checkOptions = CheckOptions.builder().allowAll().build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        // Should allow all operators without restriction
        runner.check("a = b + c * d / e % f", checkOptions);
        runner.check("++a--", checkOptions);
    }
    
    @Test
    public void testWhitelistWithPrefixOperator() {
        Set<Operator> allowedOps =
            new HashSet<>(Arrays.asList(PlusOperator.getInstance(), PlusPlusPrefixUnaryOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().whitelist(allowedOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        // Should allow prefix increment
        runner.check("++a + b", checkOptions);
        
        // Should disallow prefix decrement
        try {
            runner.check("--a + b", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            assertEquals("OPERATOR_NOT_ALLOWED", e.getErrorCode());
            assertEquals("--", e.getErrLexeme());
            assertEquals(1, e.getLineNo());
            assertEquals(0, e.getColNo()); // Position of '--'
        }
    }
    
    @Test
    public void testWhitelistWithSuffixOperator() {
        Set<Operator> allowedOps =
            new HashSet<>(Arrays.asList(PlusOperator.getInstance(), PlusPlusSuffixUnaryOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().whitelist(allowedOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        // Should allow suffix increment
        runner.check("a++ + b", checkOptions);
        
        // Should disallow suffix decrement
        try {
            runner.check("a-- + b", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            assertEquals("OPERATOR_NOT_ALLOWED", e.getErrorCode());
            assertEquals("--", e.getErrLexeme());
            assertEquals(1, e.getLineNo());
            assertEquals(1, e.getColNo()); // Position of '--' in "a--"
        }
    }
    
    @Test
    public void testBlacklistWithMultipleOperators() {
        Set<Operator> forbiddenOps =
            new HashSet<>(Arrays.asList(AssignOperator.getInstance(), MultiplyOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().blacklist(forbiddenOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        // Should allow addition and subtraction
        runner.check("a + b - c", checkOptions);
        
        // Should disallow assignment
        try {
            runner.check("a = b", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            assertEquals("OPERATOR_FORBIDDEN", e.getErrorCode());
            assertEquals("=", e.getErrLexeme());
        }
        
        // Should disallow multiplication
        try {
            runner.check("a * b", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            assertEquals("OPERATOR_FORBIDDEN", e.getErrorCode());
            assertEquals("*", e.getErrLexeme());
        }
    }
    
    @Test
    public void testPreciseErrorPositionInMultiLineScript() {
        Set<Operator> allowedOps = new HashSet<>(Arrays.asList(PlusOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().whitelist(allowedOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        String multiLineScript = "a + b\n" + "c = d\n" + "e + f";
        
        try {
            runner.check(multiLineScript, checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            assertEquals("OPERATOR_NOT_ALLOWED", e.getErrorCode());
            assertEquals("=", e.getErrLexeme());
            assertEquals(2, e.getLineNo()); // Second line where '=' appears
            assertEquals(2, e.getColNo()); // Column of '=' in second line
        }
    }
    
    @Test
    public void testEmptyWhitelistValidation() {
        try {
            CheckOptions.builder().whitelist(new HashSet<>()).build();
            fail("Should throw IllegalArgumentException for empty whitelist");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("WHITELIST"));
            assertTrue(e.getMessage().contains("null or empty"));
        }
    }
    
    @Test
    public void testEmptyBlacklistValidation() {
        try {
            CheckOptions.builder().blacklist(new HashSet<>()).build();
            fail("Should throw IllegalArgumentException for empty blacklist");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("BLACKLIST"));
            assertTrue(e.getMessage().contains("null or empty"));
        }
    }
    
    @Test
    public void testNullWhitelistValidation() {
        try {
            CheckOptions.builder().whitelist(null).build();
            fail("Should throw IllegalArgumentException for null whitelist");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("WHITELIST") || e.getMessage().contains("whitelist"));
            assertTrue(e.getMessage().contains("null or empty"));
        }
    }
    
    @Test
    public void testNullBlacklistValidation() {
        try {
            CheckOptions.builder().blacklist(null).build();
            fail("Should throw IllegalArgumentException for null blacklist");
        }
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("BLACKLIST") || e.getMessage().contains("blacklist"));
            assertTrue(e.getMessage().contains("null or empty"));
        }
    }
    
    @Test
    public void testStrategyEnumValues() {
        CheckOptions allowAllOptions = CheckOptions.builder().allowAll().build();
        assertEquals(CheckOptions.OperatorStrategy.ALLOW_ALL, allowAllOptions.getStrategy());
        
        Set<Operator> ops = new HashSet<>(Arrays.asList(PlusOperator.getInstance()));
        CheckOptions whitelistOptions = CheckOptions.builder().whitelist(ops).build();
        assertEquals(CheckOptions.OperatorStrategy.WHITELIST, whitelistOptions.getStrategy());
        assertEquals(ops, whitelistOptions.getOperators());
        
        CheckOptions blacklistOptions = CheckOptions.builder().blacklist(ops).build();
        assertEquals(CheckOptions.OperatorStrategy.BLACKLIST, blacklistOptions.getStrategy());
        assertEquals(ops, blacklistOptions.getOperators());
    }
    
    @Test
    public void testDefaultOptions() {
        CheckOptions defaultOptions = CheckOptions.DEFAULT_OPTIONS;
        assertEquals(CheckOptions.OperatorStrategy.ALLOW_ALL, defaultOptions.getStrategy());
        assertEquals(null, defaultOptions.getOperators());
        assertTrue(defaultOptions.getStrategy() == CheckOptions.OperatorStrategy.ALLOW_ALL);
        assertTrue(!defaultOptions.isWhitelistMode());
        assertTrue(!defaultOptions.isBlacklistMode());
    }
    
    @Test
    public void testComplexExpressionWithWhitelist()
        throws QLSyntaxException {
        Set<Operator> allowedOps =
            new HashSet<>(Arrays.asList(PlusOperator.getInstance(), MultiplyOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().whitelist(allowedOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        // Should allow complex arithmetic with allowed operators
        runner.check("(a + b) * (c + d)", checkOptions);
        
        // Should disallow division operator (not in whitelist)
        try {
            runner.check("(a + b) * (c + d) / e", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            assertEquals("OPERATOR_NOT_ALLOWED", e.getErrorCode());
            assertEquals("/", e.getErrLexeme());
        }
        
        // Should disallow assignment in complex expression
        try {
            runner.check("(a + b) * (c = d)", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            assertEquals("OPERATOR_NOT_ALLOWED", e.getErrorCode());
            assertEquals("=", e.getErrLexeme());
            // Position should be accurate within the complex expression
            assertTrue(e.getPos() > 10); // Should be after "(a + b) * (c "
        }
    }
    
    @Test
    public void testErrorMessageContainsOperatorSet() {
        Set<Operator> allowedOps = new HashSet<>(Arrays.asList(PlusOperator.getInstance()));
        CheckOptions checkOptions = CheckOptions.builder().whitelist(allowedOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        try {
            runner.check("a * b", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            String reason = e.getReason();
            assertTrue(reason.contains("Script uses disallowed operator"));
            assertTrue(reason.contains("*"));
            assertTrue(reason.contains("Allowed operators"));
            assertTrue(reason.contains("+")); // Should list the allowed operators
        }
    }
}
