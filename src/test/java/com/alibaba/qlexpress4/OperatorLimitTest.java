package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.operator.OperatorCheckStrategy;
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
        // tag::operatorCheckStrategyExample[]
        // Create a whitelist of allowed operators
        Set<String> allowedOps = new HashSet<>(Arrays.asList("+", "*"));
        
        // Configure check options with operator whitelist
        CheckOptions checkOptions = 
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.whitelist(allowedOps)).build();
        
        // Create runner and check script with custom options
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.check("a + b * c", checkOptions);  // This will pass as + and * are allowed
        // end::operatorCheckStrategyExample[]
    }
    
    @Test
    public void testCheckWithDisallowedOperators() {
        Set<String> allowedOps = new HashSet<>(Arrays.asList("+", "*"));
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.whitelist(allowedOps)).build();
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
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.blacklist(forbiddenOps)).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        runner.check("a + b * c - d / e", checkOptions);
    }
    
    @Test
    public void testCheckWithForbiddenOperatorUsed() {
        Set<String> forbiddenOps = new HashSet<>(Arrays.asList("="));
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.blacklist(forbiddenOps)).build();
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
    public void testWhitelistWithPrefixOperator() {
        Set<String> allowedOps = new HashSet<>(Arrays.asList("+", "++"));
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.whitelist(allowedOps)).build();
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
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.whitelist(allowedOps)).build();
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
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.blacklist(forbiddenOps)).build();
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
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.whitelist(allowedOps)).build();
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
    public void testEmptyWhitelistValidation()
        throws QLSyntaxException {
        // Empty whitelist means no operators are allowed
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.whitelist(new HashSet<>())).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        // Any operator should be disallowed
        try {
            runner.check("a + b", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            assertEquals("OPERATOR_NOT_ALLOWED", e.getErrorCode());
            assertEquals("+", e.getErrLexeme());
        }
    }
    
    @Test
    public void testEmptyBlacklistValidation()
        throws QLSyntaxException {
        // Empty blacklist means all operators are allowed
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.blacklist(new HashSet<>())).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        // All operators should be allowed
        runner.check("a = b + c * d / e % f", checkOptions);
        runner.check("++a--", checkOptions);
    }
    
    @Test
    public void testNullWhitelistValidation()
        throws QLSyntaxException {
        // Null whitelist is treated as empty set, meaning no operators are allowed
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.whitelist(null)).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        // Any operator should be disallowed
        try {
            runner.check("a + b", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            assertEquals("OPERATOR_NOT_ALLOWED", e.getErrorCode());
            assertEquals("+", e.getErrLexeme());
        }
    }
    
    @Test
    public void testNullBlacklistValidation()
        throws QLSyntaxException {
        // Null blacklist is treated as empty set, meaning all operators are allowed
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.blacklist(null)).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        // All operators should be allowed
        runner.check("a = b + c * d / e % f", checkOptions);
        runner.check("++a--", checkOptions);
    }
    
    @Test
    public void testComplexExpressionWithWhitelist()
        throws QLSyntaxException {
        Set<String> allowedOps = new HashSet<>(Arrays.asList("+", "*"));
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.whitelist(allowedOps)).build();
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
        CheckOptions checkOptions =
            CheckOptions.builder().operatorCheckStrategy(OperatorCheckStrategy.whitelist(allowedOps)).build();
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        
        try {
            runner.check("a * b", checkOptions);
            fail("Should throw QLSyntaxException");
        }
        catch (QLSyntaxException e) {
            String reason = e.getReason();
            assertTrue(reason.contains("Script uses disallowed operator"));
            assertTrue(reason.contains("*"));
            // Verify that the error message contains information about the operator
            assertTrue(reason.contains("+") || reason.contains("allowed"));
        }
    }
}
