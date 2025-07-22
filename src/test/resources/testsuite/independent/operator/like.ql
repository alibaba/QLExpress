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

// error code
assertErrorCode(() -> {"ABCD" like 200}, "INVALID_BINARY_OPERAND");
assertErrorCode(() -> {200 like "200"}, "INVALID_BINARY_OPERAND");

assertErrorCode(() -> {"ABCD" not_like 200}, "INVALID_BINARY_OPERAND");
assertErrorCode(() -> {200 not_like "200"}, "INVALID_BINARY_OPERAND");