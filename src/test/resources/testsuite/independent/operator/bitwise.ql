assert(true & true);
assertFalse(true & false);
assertFalse(true & null);

assertFalse(false & true);
assertFalse(false & false);
assertFalse(false & null);

assert(true | true);
assert(true | false);
assert(true | null);

assert(false | true);
assertFalse(false | false);
assertFalse(false | null);

assertFalse(true ^ true);
assert(true ^ false);
assert(true ^ null);

assert(false ^ true);
assertFalse(false ^ false);
assertFalse(false ^ null);

// bitwise shift
a = 4;
b = -4;
assert(a << 1 == 8);
assert(a << 2 == 16);
assert(a >> 1 == 2);
assert(a >> 2 == 1);
assert(a >>> 1 == 2);
assert(a >>> 2 == 1);
assert(b << 1 == -8);
assert(b << 2 == -16);
assert(b >> 1 == -2);
assert(b >> 2 == -1);
assert(b >>> 1 == 0x7FFFFFFE);
assert(b >>> 2 == 0x3FFFFFFF);

assertErrorCode(()-> {8.0F >> 2}, "OPERATOR_EXECUTE_EXCEPTION")
assertErrorCode(()-> {8 >> 2.0}, "OPERATOR_EXECUTE_EXCEPTION")

// bitwise shift equal
a = 4;
a <<= 1;
assert(a == 8);
a <<= 2;
assert(a == 32);
a >>= 1;
assert(a == 16);
a >>= 2;
assert(a == 4);

b = -4;
b <<= 1;
assert(b == -8);
b <<= 2;
assert(b == -32);
b >>= 1;
assert(b == -16);
b >>= 2;
assert(b == -4);

b = -4;
b >>>= 1;
assert(b == 0x7FFFFFFE);
b = -8;
b >>>= 2;
assert(b == 0x3FFFFFFE);

// bitwise and
a = 13;
assert((a & 3) == 1); // 0x0000000D & 0x00000003
assert((a & 7) == 5); // 0x0000000D & 0x00000007
b = -13;
assert((b & 3) == 3); // 0xFFFFFFF3 & 0x00000003
assert((b & 7) == 3); // 0xFFFFFFF3 & 0x00000007

// bitwise and equals
a = 13;
a &= 3;
assert(a == 1); // 0x0000000D & 0x00000003

a &= 4;
assert(a == 0); // 0x00000001 & 0x00000004

b = -13;
b &= 3;
assert(b == 3); // 0xFFFFFFF3 & 0x00000003

b &= 7;
assert(b == 3); // 0x00000003 & 0x00000007

// bitwise or
a = 13;
assert((a | 8) == 13);   // 0x0000000D | 0x00000008
assert((a | 16) == 29);  // 0x0000000D | 0x00000010
b = -13;
assert((b | 8) == -5);   // 0xFFFFFFF3 | 0x00000008
assert((b | 16) == -13); // 0xFFFFFFF3 | 0x00000010

// bitwise or equal
a = 13;
a |= 2;
assert(a == 15); // 0x0000000D | 0x00000002
a |= 16;
assert(a == 31); // 0x0000000F | 0x0000001F
b = -13;
b |= 8;
assert(b == -5); // 0xFFFFFFF3 | 0x00000008
b |= 1;
assert(b == -5); // 0xFFFFFFFB | 0x00000001

// bitwise xor
a = 13;
assert((a ^ 10) == 7); // 0x0000000D ^ 0x0000000A = 0x000000007
assert((a ^ 15) == 2); // 0x0000000D ^ 0x0000000F = 0x000000002
b = -13;
assert((b ^ 10) == -7); // 0xFFFFFFF3 ^ 0x0000000A = 0xFFFFFFF9
assert((b ^ 15) == -4); // 0xFFFFFFF3 ^ 0x0000000F = 0xFFFFFFFC

// bitwise xor equal
a = 13;
a ^= 8;
assert(a == 5); // 0x0000000D ^ 0x00000008 = 0x000000005
a ^= 16
assert(a == 21); // 0x00000005 ^ 0x00000010 = 0x000000015
b = -13;
b ^= 8;
assert(b == -5); // 0xFFFFFFF3 ^ 0x00000008 = 0xFFFFFFFB
b ^= 16;
assert(b == -21); // 0xFFFFFFFB ^ 0x00000010 = 0xFFFFFFEB

// bitwise negation
assert(~1 == -2); // ~0x00000001 = 0xFFFFFFFE
assert(~(-1) == 0); // ~0xFFFFFFFF = 0x00000000
assert(~(~5) == 5); // ~~0x00000005 = ~0xFFFFFFFA = 0xFFFFFFF5
a = 13;
assert(~a == -14); // ~0x0000000D = 0xFFFFFFF2
assert(~(~a) == 13); // ~~0x0000000D = ~0xFFFFFFF2 = 0x0000000D
assert(-(~a) == 14); // -~0x0000000D = -0xFFFFFFF2 = 0x0000000E