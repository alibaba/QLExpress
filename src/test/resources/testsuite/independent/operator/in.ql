// left is null and right is null, TODO 冰够 需要讨论下
assertErrorCode(() -> {null in null}, "INVALID_BINARY_OPERAND")
assertFalse(null in "abc")
assertErrorCode(() -> {null in 123}, "INVALID_BINARY_OPERAND")

assertFalse(null in [1, 2, 3])
assertFalse(null in new int[]{1, 2, 3})

assertFalse(null in ["abc", "def", "ghi"])
assertFalse(null in new String[]{"abc", "def", "ghi"})

assert(1 in [1, 2, 3])
assert(1 in new int[]{1, 2, 3})

assertFalse(1 in ["1", "2", "3"])
assertFalse(1 in new String[]{"1", "2", "3"})

assert("abc" in ["abc", "def", "ghi"])
assert("abc" in new String[]{"abc", "def", "ghi"})

assertFalse("bcd" in ["abc", "def", "ghi"])
assertFalse("bcd" in new String[]{"abc", "def", "ghi"})

assert("bc" in "abc")
assert("bc" in "bcd")
assert("bc" in "abcd")
assertFalse("bc" in "ab")
assertFalse("bc" in "cd")
assertFalse("bc" in "abd")
assertFalse("bc" in "acd")