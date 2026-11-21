// -0.0 and 0.0 compare equal, as with Java's double comparison
negZero = -2.5d * 0
assert(negZero == 0)
assert(negZero == 0d)
assert(0d == negZero)
assertFalse(negZero != 0)
assertFalse(negZero < 0)
assertFalse(negZero > 0)
assert(negZero <= 0)
assert(negZero >= 0)
assert(negZero in [0])

negZeroF = -2.5f * 0
assert(negZeroF == 0)
assertFalse(negZeroF < 0)

// non-zero values keep their order
assert(-0.5d < 0)
assert(0.5d > negZero)
assertFalse(-0.5d == 0)
