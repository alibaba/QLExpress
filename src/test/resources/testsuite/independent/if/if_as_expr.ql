a = if (11 == 11) {
  10 + 1
} else {
  20 + 2
};
b = if (a == 11) 20 else 9;
c = if (a != 11) 11 else 12;
assert(b == 20);
assert(c == 12);

assert(if (b==20) {
  a == 11
});

assert(if (b==20) a == 11);