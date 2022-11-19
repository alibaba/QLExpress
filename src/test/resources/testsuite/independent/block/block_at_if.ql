a = 1;
b = if (a < 5) {
  {
    a + 10
  }
} else {
  {
    a * 10
  }
};
assert(b == 11);

