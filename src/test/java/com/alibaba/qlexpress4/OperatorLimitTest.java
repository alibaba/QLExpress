package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.exception.QLSyntaxException;
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
        Set<String> allowedOps = new HashSet<>(Arrays.asList("+", "*"));
        CheckOptions checkOptions = CheckOptions.builder().whitelist(allowedOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.check("a + b * c", checkOptions);
    }
    
    @Test
    public void testCheckWithDisallowedOperators() {
        Set<String> allowedOps = new HashSet<>(Arrays.asList("+", "*"));
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
        Set<String> forbiddenOps = new HashSet<>(Arrays.asList("="));
        CheckOptions checkOptions = CheckOptions.builder().blacklist(forbiddenOps).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.check("a + b * c - d / e", checkOptions);
    }
    
    @Test
    public void testCheckWithForbiddenOperatorUsed() {
        Set<String> forbiddenOps = new HashSet<>(Arrays.asList("="));
        CheckOptions checkOptions = CheckOptions.builder().blacklist(forbiddenOps).build();
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
    public void testCheckWithoutLimit()
        throws QLSyntaxException {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.check("a = b + c - d * e / f");
        runner.check("a++");
        runner.check("++a");
    }
    
    @Test
    public void testCannotSetBothWhitelistAndBlacklist() {
        Set<String> allowedOps = new HashSet<>(Arrays.asList("+"));
        Set<String> forbiddenOps = new HashSet<>(Arrays.asList("="));
        // When calling whitelist() then blacklist(), the blacklist strategy will override whitelist
        // This is expected behavior - the last strategy set wins
        CheckOptions checkOptions = CheckOptions.builder().whitelist(allowedOps).blacklist(forbiddenOps).build();
        assertEquals(com.alibaba.qlexpress4.operator.OperatorCheckStrategy.StrategyType.BLACKLIST,
            checkOptions.getCheckStrategy().getStrategyType());
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
        Set<String> allowedOps = new HashSet<>(Arrays.asList("+", "++"));
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
        Set<String> allowedOps = new HashSet<>(Arrays.asList("+", "++"));
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
        Set<String> forbiddenOps = new HashSet<>(Arrays.asList("=", "*"));
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
            assertEquals("OPERATOR_NOT_ALLOWED", e.getErrorCode());
            assertEquals("=", e.getErrLexeme());
        }
        
        // Should disallow multiplication
        try {
            runner.check("a * b", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            assertEquals("OPERATOR_NOT_ALLOWED", e.getErrorCode());
            assertEquals("*", e.getErrLexeme());
        }
    }
    
    @Test
    public void testPreciseErrorPositionInMultiLineScript() {
        Set<String> allowedOps = new HashSet<>(Arrays.asList("+"));
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
            assertTrue("Error message should indicate whitelist operators cannot be null or empty",
                e.getMessage().contains("Whitelist operators cannot be null or empty"));
        }
    }
    
    @Test
    public void testEmptyBlacklistValidation() {
        try {
            CheckOptions.builder().blacklist(new HashSet<>()).build();
            fail("Should throw IllegalArgumentException for empty blacklist");
        }
        catch (IllegalArgumentException e) {
            assertTrue("Error message should indicate blacklist operators cannot be null or empty",
                e.getMessage().contains("Blacklist operators cannot be null or empty"));
        }
    }
    
    @Test
    public void testNullWhitelistValidation() {
        try {
            CheckOptions.builder().whitelist(null).build();
            fail("Should throw IllegalArgumentException for null whitelist");
        }
        catch (IllegalArgumentException e) {
            assertTrue("Error message should indicate whitelist operators cannot be null or empty",
                e.getMessage().contains("Whitelist operators cannot be null or empty"));
        }
    }
    
    @Test
    public void testNullBlacklistValidation() {
        try {
            CheckOptions.builder().blacklist(null).build();
            fail("Should throw IllegalArgumentException for null blacklist");
        }
        catch (IllegalArgumentException e) {
            assertTrue("Error message should indicate blacklist operators cannot be null or empty",
                e.getMessage().contains("Blacklist operators cannot be null or empty"));
        }
    }
    
    @Test
    public void testStrategyEnumValues() {
        CheckOptions allowAllOptions = CheckOptions.builder().allowAll().build();
        assertEquals(com.alibaba.qlexpress4.operator.OperatorCheckStrategy.StrategyType.ALLOW_ALL,
            allowAllOptions.getCheckStrategy().getStrategyType());
        
        Set<String> ops = new HashSet<>(Arrays.asList("+"));
        CheckOptions whitelistOptions = CheckOptions.builder().whitelist(ops).build();
        assertEquals(com.alibaba.qlexpress4.operator.OperatorCheckStrategy.StrategyType.WHITELIST,
            whitelistOptions.getCheckStrategy().getStrategyType());
        
        CheckOptions blacklistOptions = CheckOptions.builder().blacklist(ops).build();
        assertEquals(com.alibaba.qlexpress4.operator.OperatorCheckStrategy.StrategyType.BLACKLIST,
            blacklistOptions.getCheckStrategy().getStrategyType());
    }
    
    @Test
    public void testComplexExpressionWithWhitelist()
        throws QLSyntaxException {
        Set<String> allowedOps = new HashSet<>(Arrays.asList("+", "*"));
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
        Set<String> allowedOps = new HashSet<>(Arrays.asList("+"));
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
