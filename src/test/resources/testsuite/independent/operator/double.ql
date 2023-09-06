// plus
x = 2.1d + 2.1d
assert(x == 4.2d)

x = 3d + 2.2d
assert(x == 5.2d)

x = 2.2d + 4d
assert(x == 6.2d)

y = x + 1d
assert(y == 7.2d)

def z = y + x + 1d + 2d
assert(z == 16.4d)

// minus
x = 6d - 2.2d
assert(x == 3.8d)

x = 5.8d - 2d
assert(x == 3.8d)

y = x - 1d
assert(y == 2.8d)

// multiply
x = 3d * 2.0d
assert(x == 6.0d)

x = 3.0d * 2d
assert(x == 6.0d)

x = 3.0d * 2.0d
assert(x == 6.0d)
y = x * 2d
assert(y == 12.0d)

// divide
x = 80.0d / 4d
assert(x == 20.0d, "x = " + x)

x = 80d / 4.0d
assert(x == 20.0d, "x = " + x)

y = x / 2d
assert(y == 10.0d, "y = " + y)

// remainder
x = 100d % 3
assert(x == 1d)

y = 11d
y %= 3d
assert(y == 2d)
y = -11d
y %= 3d
assert(y == -2d)

// TODO bingo 不带空格会报错
