macro m {
  c = a + b;
}

a = 1;
b = 2;
m;
assert(c == 3);
b = 10;
m;
assert(c == 11);