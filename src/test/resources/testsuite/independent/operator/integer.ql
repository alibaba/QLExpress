// plus
x = 2 + 2
assert(x == 4)

y = x + 1
assert(y == 5)

z = y + x + 1 + 2
assert(z == 12)

// unary plus
x = 3
y = +x
assert(y == 3)

// character plus
Character c1 = 1
Character c2 = 2

x = c2 + 2
assert(x == 4)

x = 2 + c2
assert(x == 4)

x = c2 + c2
assert(x == 4)

y = x + c1
assert(y == 5)

y = c1 + x
assert(y == 5)

z = y + x + c1 + 2
assert(z == 12)

z = y + x + 1 + c2
assert(z == 12)

z = y + x + c1 + c2
assert(z == 12)

// minus
x = 6 - 2
assert(x == 4)

y = x - 1
assert(y == 3)

// unary minus
x = 3
y = -x
assert(y == -3)

// bitwise negate
x = 3
y = ~x
assert(y == -4)

// character minus
Character c1 = 1
Character c2 = 2
Character c6 = 6

x = c6 - 2
assert(x == 4)

x = 6 - c2
assert(x == 4)

x = c6 - c2
assert(x == 4)

y = x - c1
assert(y == 3)

// multiply
x = 3 * 2
assert(x == 6)

y = x * 2
assert(y == 12)

// divide
x = 80 / 4
assert(x == 20.0)

y = x / 2
assert(y == 10.0)

// remainder
x = 100 % 3
assert(x == 1)

y = 11
y %= 3
assert(y == 2)

y = -11
y %= 3
assert(y == -2)

// and
x = 1 & 3
assert(x == 1)

// or
x = 1 | 3
assert(x == 3)

// shift operator
x = 8 >> 1
assert(x == 4)
assert(x instanceof Integer)

x = 8 << 2
assert(x == 32)
assert(x instanceof Integer)

x = 8L << 2
assert(x == 32)
assert(x instanceof Long)

x = -16 >> 4
assert(x == -1)

x = -16 >>> 4
assert(x == 0xFFFFFFF)

//Ensure that the type of the right operand (shift distance) is ignored when calculating the
//result.  This is how java works, and for these operators, it makes sense to keep that behavior.
x = Integer.MAX_VALUE << 1L
assert(x == -2)
assert(x instanceof Integer)

x = new Long(Integer.MAX_VALUE).longValue() << 1
assert(x == 0xfffffffe)
assert(x instanceof Long)