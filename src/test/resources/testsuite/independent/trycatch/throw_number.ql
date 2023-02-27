try {
  throw 11;
  assert(false);
} catch(int i) {
  assert(i == 11);
}