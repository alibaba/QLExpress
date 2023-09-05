/*
{
  "qlOptions": QLOptions.builder().precise(true)
}
*/

// plus
x = 0.1 + 1.1;
assert(x instanceof BigDecimal)
assert(x == 1.2);

x = 3 + 2.2
assert(x == 5.2)
assert(x instanceof BigDecimal)

x = 2.2 + 4
assert(x instanceof BigDecimal)
assert(x == 6.2)

y = x + 1
assert(y instanceof BigDecimal)
assert(y == 7.2)

def z = y + x + 1 + 2
assert(z instanceof BigDecimal)
assert(z == 16.4)

// minus
x = 1.1 - 0.01
assert(x == 1.09)

x = 6 - 2.2
assert(x == 3.8)

x = 5.8 - 2
assert(x == 3.8)

y = x - 1
assert(y == 2.8)

// multiply
x = 3 * 2.0
assert(x == 6.0)

x = 3.0 * 2
assert(x == 6.0)

x = 3.0 * 2.0
assert(x == 6.0)

y = x * 2
assert(y == 12.0)

y = 11 * 3.333
assert(y == 36.663)

y = 3.333 * 11
assert(y == 36.663)

// divide
x = 80.0 / 4
assert(x == 20.0 , "x = " + x)

x = 80 / 4.0
assert(x == 20.0 , "x = " + x)

y = x / 2
assert(y == 10.0 , "y = " + y)
assert(y == 10 , "y = " + y)

y = 34 / 3.000; // TODO bingo 执行失败时，没有透出错误码
assert(y == 11.3333333333);

y = 34.00000000000 / 3;
assert(y == 11.3333333333); // TODO bingo 常量是否转为BigDecimal

// remainder
x = 100.0 % 3
assert(x == 1)

y = 5.5
y %= 2.0
assert(y == 1.5)

y = -5.5
y %= 2.0
assert(y == -1.5)