// Regression tests for issue: binary operator directly followed by a unary
// prefix (-, +, !) used to be greedily merged into one OPID token by QLexer,
// causing scripts like `10*-1` or `5+-3` to fail compilation.

// multiplicative + unary minus
assert(10*-1 == -10)
assert(10 * -1 == -10)
assert(10/-2 == -5)
assert(10%-3 == 1)
assert(-10*-1 == 10)

// additive + unary minus
assert(5+-3 == 2)
assert(5 + -3 == 2)

// multiplicative + unary plus
assert(10*+1 == 10)
assert(10/+2 == 5)
assert(10%+3 == 1)

// additive + unary plus
assert(5 + +3 == 8)

// chained operations
assert(2*-3*-4 == 24)
assert(1+-2+-3 == -4)
assert((1+2)*-3 == -9)

// boolean binary op + unary bang
assert((true && !false) == true)
assert((false || !false) == true)

// variable form
a = 4
assert(a*-2 == -8)
assert(a+-2 == 2)
assert(a/-2 == -2)
assert(a%-3 == 1)

// long / double literals
assert(10L*-1L == -10L)
assert(10.0*-1.0 == -10.0)

// assignment with unary (PR #475 scope, included here as regression)
b = -1
assert(b == -1)
c = +2
assert(c == 2)
d = !false
assert(d == true)
