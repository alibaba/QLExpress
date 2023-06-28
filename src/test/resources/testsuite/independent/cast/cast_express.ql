a = int;
assert(a == int);
b = 12L;
c = (int) b;
assert(c.class == a.class);
d = (int) 100.12d;
assert(d == 100);