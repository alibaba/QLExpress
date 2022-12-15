for (i = 0; i < 5; i++) {
  if (i == 2) {
    break;
  }
}
assert(i == 2);

sum = 0;
for (i = 0; i < 5; i++) {
  if (i == 2) {
    continue;
  }
  sum += i;
}
assert(sum == 8);

sum = 0;
for (i = 0; i < 5; i++) {
  if (i == 2) {
    if (i == 2) {
      continue;
    }
  }
  sum += i;
}
assert(sum == 8);

