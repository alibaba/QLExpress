l = (x) -> x > 10? 11: 100;
assert(l(11) == 11);
assert(l(5) == 100);

l1 = (x) -> x > 100? 101: x > 50? 51: 11;
assert(l1(120) == 101);
assert(l1(99) == 51);
assert(l1(15) == 11);

l2 = x -> x > 10? x < 20? 19: 11: -9;
assert(l2(1) == -9);
assert(l2(17) == 19);
assert(l2(29) == 11);

l3 = true? a = 100: b = 200;
assert(a == 100);
assert(b == null);