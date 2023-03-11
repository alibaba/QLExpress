a = 1 + try {
  100 + 1/0
} catch(Object e) {
  11
};

assert(a == 12);

b = 1 + try {
  100 + 1/0
} catch(Object e) {
  11
} finally {
  1000
};
assert(b == 12);