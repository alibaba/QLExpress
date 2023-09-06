// assign
BigInteger bi;
bi = (byte) 20;
assert(bi instanceof BigInteger);
assert(bi == 20);

bi = (short) 20
assert(bi instanceof BigInteger)
assert(bi == 20)

bi = (int) 20
assert(bi instanceof BigInteger)
assert(bi == 20)

bi = (long) 20
assert(bi instanceof BigInteger)
assert(bi == 20)

bi = (float) 0.5f
assert(bi instanceof BigInteger)
assert(bi == 0)

bi = (double) 0.5d
assert(bi instanceof BigInteger)
assert(bi == 0)

bi = 10.5G
assert(bi instanceof BigInteger)
assert(bi == 10)

// double d = 1000 TODO bingo 两种方式为啥有区别
double d;
d = 1000;
d *= d
d *= d
d *= d
assert((long)d != d)
assert((BigInteger) d == d)

// plus
x = BigInteger.valueOf(2) + BigInteger.valueOf(3)
assert(x instanceof BigInteger)
assert(x == 5)

// multiply
x = BigInteger.valueOf(2) * BigInteger.valueOf(3)
assert(x instanceof BigInteger)
assert(x == 6)

// remainder
x = BigInteger.valueOf(100) % 3
assert(x == 1)

y = BigInteger.valueOf(11)
y %= 3
assert(y == 2)
y = BigInteger.valueOf(-11)
y %= 3
assert(y == -2)