function a(int value, boolean b) {
  a = value;
  return b;
}

c = a(1, false) && a(10, true);
assert(c == false);
assert(a == 1);

c = a(100, true) || a(1, false);
assert(c == true);
assert(a == 100);

d = a(1000, true) && a(10000, false);
assert(d == false);
assert(a == 10000);

e = a(11, false) || a(111, true);
assert(e == true);
assert(a == 111);

f = a(2, true) || a(3, false) && a(5, false);
assert(f == true);
assert(a == 2);