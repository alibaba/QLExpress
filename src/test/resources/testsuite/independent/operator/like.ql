assert(null like null);
assertFalse(null not_like null);

assertFalse("a" like null);
assert("a" not_like null);

assert("1006" like "%6");
assert("1006" like "1%");
assert("ABCD" like "A%B%D");

assertFalse("1006" not_like "%6");
assertFalse("1006" not_like "1%");
assertFalse("ABCD" not_like "A%B%D");

// Issue #409 regression: backtracking scenarios
// v3 OperatorLike used greedy matching without backtracking,
// causing "abc" like "a%c" to return false (should be true).
// v4 uses a proper backtracking algorithm (sRecall/pRecall).
assert("abc" like "a%c");
assert("abc" not_like "a%c" == false);
assertFalse("abc" not_like "a%c");

// More backtracking edge cases
assert("aXbYc" like "a%b%c");
assert("hello_world_test" like "hello%world%test");
assert("aab" like "a%a%b");
assert("aaab" like "a%a%b");

// Trailing % edge cases from v3 issue report
assert("1%1" like "1%");
assert("anything" like "%");
assert("" like "%");
assert("" like "%%");

assertFalse("abc" like "a%d");
assertFalse("abc" like "%x%");

// error code
assertErrorCode(() -> {"ABCD" like 200}, "INVALID_BINARY_OPERAND");
assertErrorCode(() -> {200 like "200"}, "INVALID_BINARY_OPERAND");

assertErrorCode(() -> {"ABCD" not_like 200}, "INVALID_BINARY_OPERAND");
assertErrorCode(() -> {200 not_like "200"}, "INVALID_BINARY_OPERAND");