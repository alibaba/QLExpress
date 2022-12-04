l = [];
for (int i = 3; i < 6; i++) {
  l.add(i);
}
assert(l == [3,4,5]);
assert(i == null);

l1 = [];
for (j = 10; j > 8; j--) {
  l1.add(j);
}
assert(l1 == [10, 9]);
assert(j == 8);

// scope test; h not in for condition scope
for (m = 0; m < 5 && h == null; m++) {
  int h = 5;
}
assert(m == 5);