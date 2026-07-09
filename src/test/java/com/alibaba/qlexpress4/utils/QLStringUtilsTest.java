package com.alibaba.qlexpress4.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link QLStringUtils}, covering escape sequence handling
 * in string literals.
 *
 * Related: GitHub issue #335 - unrecognized escape sequences like \d, \w
 * were silently dropped instead of being preserved.
 */
public class QLStringUtilsTest {

    // --- Recognized escape sequences should produce their standard values ---

    @Test
    public void recognizedEscape_newline() {
        assertEquals("\n", QLStringUtils.parseStringEscapeStartEnd("\\n", 0, 2));
    }

    @Test
    public void recognizedEscape_tab() {
        assertEquals("\t", QLStringUtils.parseStringEscapeStartEnd("\\t", 0, 2));
    }

    @Test
    public void recognizedEscape_carriageReturn() {
        assertEquals("\r", QLStringUtils.parseStringEscapeStartEnd("\\r", 0, 2));
    }

    @Test
    public void recognizedEscape_backslash() {
        assertEquals("\\", QLStringUtils.parseStringEscapeStartEnd("\\\\", 0, 2));
    }

    @Test
    public void recognizedEscape_singleQuote() {
        assertEquals("'", QLStringUtils.parseStringEscapeStartEnd("\\'", 0, 2));
    }

    @Test
    public void recognizedEscape_doubleQuote() {
        assertEquals("\"", QLStringUtils.parseStringEscapeStartEnd("\\\"", 0, 2));
    }

    @Test
    public void recognizedEscape_backspace() {
        assertEquals("\b", QLStringUtils.parseStringEscapeStartEnd("\\b", 0, 2));
    }

    @Test
    public void recognizedEscape_formFeed() {
        assertEquals("\f", QLStringUtils.parseStringEscapeStartEnd("\\f", 0, 2));
    }

    @Test
    public void recognizedEscape_dollarSign() {
        assertEquals("$", QLStringUtils.parseStringEscapeStartEnd("\\$", 0, 2));
    }

    // --- Unrecognized escape sequences should be preserved as-is (issue #335) ---

    @Test
    public void unrecognizedEscape_d() {
        // \d is commonly used in regex patterns
        assertEquals("\\d", QLStringUtils.parseStringEscapeStartEnd("\\d", 0, 2));
    }

    @Test
    public void unrecognizedEscape_w() {
        // \w is commonly used in regex patterns
        assertEquals("\\w", QLStringUtils.parseStringEscapeStartEnd("\\w", 0, 2));
    }

    @Test
    public void unrecognizedEscape_s() {
        // \s is commonly used in regex patterns
        assertEquals("\\s", QLStringUtils.parseStringEscapeStartEnd("\\s", 0, 2));
    }

    @Test
    public void unrecognizedEscape_D() {
        assertEquals("\\D", QLStringUtils.parseStringEscapeStartEnd("\\D", 0, 2));
    }

    @Test
    public void unrecognizedEscape_W() {
        assertEquals("\\W", QLStringUtils.parseStringEscapeStartEnd("\\W", 0, 2));
    }

    @Test
    public void unrecognizedEscape_S() {
        assertEquals("\\S", QLStringUtils.parseStringEscapeStartEnd("\\S", 0, 2));
    }

    @Test
    public void unrecognizedEscape_parenthesis() {
        assertEquals("\\(", QLStringUtils.parseStringEscapeStartEnd("\\(", 0, 2));
    }

    @Test
    public void unrecognizedEscape_dot() {
        assertEquals("\\.", QLStringUtils.parseStringEscapeStartEnd("\\.", 0, 2));
    }

    @Test
    public void unrecognizedEscape_asterisk() {
        assertEquals("\\*", QLStringUtils.parseStringEscapeStartEnd("\\*", 0, 2));
    }

    @Test
    public void unrecognizedEscape_plus() {
        assertEquals("\\+", QLStringUtils.parseStringEscapeStartEnd("\\+", 0, 2));
    }

    @Test
    public void unrecognizedEscape_digit() {
        // \1 through \9 are backreferences in regex
        assertEquals("\\1", QLStringUtils.parseStringEscapeStartEnd("\\1", 0, 2));
    }

    // --- Complex patterns mixing recognized and unrecognized escapes ---

    @Test
    public void regexPattern_digitMatcher() {
        // '(\d*)ch' should produce (\d*)ch
        assertEquals("(\\d*)ch", QLStringUtils.parseStringEscapeStartEnd("(\\d*)ch", 0, 8));
    }

    @Test
    public void regexPattern_phoneNumber() {
        // '\d{3}-\d{4}' should produce \d{3}-\d{4}
        assertEquals("\\d{3}-\\d{4}", QLStringUtils.parseStringEscapeStartEnd("\\d{3}-\\d{4}", 0, 12));
    }

    @Test
    public void mixedEscapes_recognizedAndUnrecognized() {
        // 'hello\n\d+' should produce hello\n\d+  (newline + literal \d+)
        assertEquals("hello\n\\d+", QLStringUtils.parseStringEscapeStartEnd("hello\\n\\d+", 0, 10));
    }

    @Test
    public void noEscapes_plainString() {
        assertEquals("hello world", QLStringUtils.parseStringEscapeStartEnd("hello world", 0, 11));
    }

    @Test
    public void emptyString() {
        assertEquals("", QLStringUtils.parseStringEscapeStartEnd("", 0, 0));
    }
}
