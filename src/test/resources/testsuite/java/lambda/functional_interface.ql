Runnable r = () -> a = 8;
r.run();
assert(a == 8);

Supplier s = () -> "test";
assert(s.get() == 'test');

Consumer c = (a) -> b = a + "-te";
c.accept("ccc");
assert(b == 'ccc-te');

Function f = a -> a + 3;
assert(f.apply(1) == 4);

Function f1 = (a, b) -> a + b;
assert(f1.apply("test-") == "test-null");