f = npe -> try {
  npe();
  return 10;
} catch(NullPointerException n) {
  return 100;
};

assert(f(() -> {a=null;a.b;}) == 100);

assert(f(() -> {a=null;a::b;}) == 100);

assert(f(null) == 100);

assert(f(() -> {
  a= null;
  a.b();
}) == 100);