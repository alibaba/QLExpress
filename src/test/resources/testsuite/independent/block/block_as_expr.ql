a = {
  1 + 1
} + 1;
assert(a == 3);
b = {
  String c = 'ccc';
  String d = 'ddd';
  c + '-' + d
};
assert(b == 'ccc-ddd');
f = {
  int e = 10;
  if (a > 5) {
    a + e
  } else {
    a * 2
  }
};
assert(f == 6);