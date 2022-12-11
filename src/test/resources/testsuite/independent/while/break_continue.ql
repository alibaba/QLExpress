i = 0;
while (i < 5) {
  if (++i == 2) {
    break;
  }
}
assert(i == 2);

sum = 0;
i = 0;
while (i < 5) {
  if (i == 2) {
    i += 1;
    continue;
  }
  sum += i++;
}
assert(sum == 8);