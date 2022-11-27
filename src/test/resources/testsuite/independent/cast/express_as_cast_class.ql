a = int;
assert(a == int);
b = 12L;
c = (a) b;
assert(c.class == a.class);
d = (c.class) 100.12d;
assert(d == 100);