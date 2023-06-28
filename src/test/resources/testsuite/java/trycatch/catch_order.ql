f = e -> try {
  throw e;
} catch (NullPointerException n) {
  100
} catch (Exception e) {
  10
};

println(f(new NullPointerException()));
assert(f(new NullPointerException()) == 100);
assert(f(new Exception()) == 10);