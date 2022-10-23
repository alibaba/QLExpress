a = if (11 == 11) {
  10
} else {
  20 + 2
} + 1;
b = if (a == 11) 20 else 9;
c = if (a != 11) 11 else 12;
assert(b == 20);
assert(c == 12);

assert(if (20==20) {
  11 == 11
});

assert(if (20==20) 11 == 11);