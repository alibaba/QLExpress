int a = 10;

try {
  int a = 1000;
  throw new NullPointerException();
  assert(false);
} catch (Object o) {
  assert(a == 10);
} finally {
  assert(a == 10);
}