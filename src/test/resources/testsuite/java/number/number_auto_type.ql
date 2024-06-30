assert(2147483647 instanceof Integer);
assert(9223372036854775807 instanceof Long);
assert(18446744073709552000 instanceof BigInteger);
// 0.25 can be precisely presented with double
assert(0.25 instanceof Double);
assert(2.7976931348623157E308 instanceof BigDecimal);