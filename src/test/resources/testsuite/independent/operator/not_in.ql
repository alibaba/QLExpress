// left is null and right is null, TODO 冰够 需要讨论下
assertErrorCode(() -> {null not_in null}, "INVALID_BINARY_OPERAND")
assert(null not_in "abc")
assertErrorCode(() -> {null not_in 123}, "INVALID_BINARY_OPERAND")

assert(null not_in [1, 2, 3])
assert(null not_in new int[]{1, 2, 3})

assert(null not_in ["abc", "def", "ghi"])
assert(null not_in new String[]{"abc", "def", "ghi"})

assertFalse(1 not_in [1, 2, 3])
assertFalse(1 not_in new int[]{1, 2, 3})

assert(1 not_in ["1", "2", "3"])
assert(1 not_in new String[]{"1", "2", "3"})

assertFalse("abc" not_in ["abc", "def", "ghi"])
assertFalse("abc" not_in new String[]{"abc", "def", "ghi"})

assert("bcd" not_in ["abc", "def", "ghi"])
assert("bcd" not_in new String[]{"abc", "def", "ghi"})

assertFalse("bc" not_in "abc")
assertFalse("bc" not_in "bcd")
assertFalse("bc" not_in "abcd")
assert("bc" not_in "ab")
assert("bc" not_in "cd")
assert("bc" not_in "abd")
assert("bc" not_in "acd")