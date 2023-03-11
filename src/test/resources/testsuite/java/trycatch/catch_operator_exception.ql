a = try {
  1/0
} catch(ArithmeticException e) {
  2+2
};
assert(a == 4);