assert(null in null);
assertFalse(null in "abc");
assertFalse(null in 123);

assertFalse(null not_in null);
assert(null not_in "abc");
assert(null not_in 123);

assertFalse(null in [1, 2, 3]);
assertFalse(null in new int[]{1, 2, 3});

assert(null not_in [1, 2, 3]);
assert(null not_in new int[]{1, 2, 3});

assertFalse(null in ["abc", "def", "ghi"]);
assertFalse(null in new String[]{"abc", "def", "ghi"});

assert(null not_in ["abc", "def", "ghi"]);
assert(null not_in new String[]{"abc", "def", "ghi"});

assert(1 in [1, 2, 3]);
assert(1 in new int[]{1, 2, 3});

assertFalse(1 not_in [1, 2, 3]);
assertFalse(1 not_in new int[]{1, 2, 3});

assertFalse(1 in ["1", "2", "3"]);
assertFalse(1 in new String[]{"1", "2", "3"});

assert(1 not_in ["1", "2", "3"]);
assert(1 not_in new String[]{"1", "2", "3"});

assert("abc" in ["abc", "def", "ghi"]);
assert("abc" in new String[]{"abc", "def", "ghi"});

assertFalse("abc" not_in ["abc", "def", "gcpghi"]);
assertFalse("abc" not_in new String[]{"abc", "def", "ghi"});

assertFalse("bcd" in ["abc", "def", "ghi"]);
assertFalse("bcd" in new String[]{"abc", "def", "ghi"});

assert("bcd" not_in ["abc", "def", "ghi"]);
assert("bcd" not_in new String[]{"abc", "def", "ghi"});

assert("bc" in "abc");
assert("bc" in "bcd");
assert("bc" in "abcd");
assertFalse("bc" in "ab");
assertFalse("bc" in "cd");
assertFalse("bc" in "abd");
assertFalse("bc" in "acd");

assertFalse("bc" not_in "abc");
assertFalse("bc" not_in "bcd");
assertFalse("bc" not_in "abcd");
assert("bc" not_in "ab");
assert("bc" not_in "cd");
assert("bc" not_in "abd");
assert("bc" not_in "acd");