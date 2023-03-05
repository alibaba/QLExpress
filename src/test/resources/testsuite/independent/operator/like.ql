assertFalse(null like null);
assertFalse("a" like null);
assert("1006" like "%6");
assert("1006" like "1%");
assert("ABCD" like "A%B%D");