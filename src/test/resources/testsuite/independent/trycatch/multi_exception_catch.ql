function f(x) {
  try {
    throw x;
  } catch (int | long i) {
    assert(i == x);
  }
}

f(1);

f(100L);

try {
  f(1.1d);
  assert(false);
} catch (double d) {
  assert(d == 1.1d);
}
