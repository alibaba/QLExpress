a = 11;
if (a >= 0 && a < 5) {
  assert(false);
} else if (a >= 5 && a < 10) {
  assert(false);
} else if (a >= 10 && a < 15) {
  assert(true);
}