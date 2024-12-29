i = 0;
while (i < 5) {
  if (++i == 2) {
    break;
  }
}
assert(i==2)