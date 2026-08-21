import com.alibaba.qlexpress4.test.lambda.UserFunctionalInterfaceWithDefault;

// Test basic lambda proxy functionality with user-defined functional interface
UserFunctionalInterfaceWithDefault calc = (a, b) -> a + b;
assert(calc.compute(1, 2) == 3);

// Test default method delegation on lambda proxy
assert(calc.computeWithOffset(1, 2, 10) == 13);
assert(calc.computeWithOffset(5, 5, -3) == 7);

// Test toString on lambda proxy (should return "QLambdaProxy")
str = calc.toString();
assert(str == 'QLambdaProxy');

// Test hashCode on lambda proxy (should not throw)
h = calc.hashCode();
assert(h != null);

// Test equals on lambda proxy (should not throw, self-equality)
assert(calc.equals(calc));

// Test equals with different object (should return false, not throw)
UserFunctionalInterfaceWithDefault calc2 = (a, b) -> a * b;
assert(!calc.equals(calc2));

// Test hashCode consistency (same proxy should return same hashCode)
h1 = calc.hashCode();
h2 = calc.hashCode();
assert(h1 == h2);
