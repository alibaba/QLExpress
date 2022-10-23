int a = 10;
if (a > 9) {
  a = 11;
} else {
  a = 5;
}

assert(a == 11);

b = 5;
if (a > 20) {
  b = 90;
}
assert(b == 5);

if (b==5) a = 90 else a = 900;

assert(a == 90);