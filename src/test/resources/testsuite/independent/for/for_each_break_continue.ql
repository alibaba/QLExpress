globalI = 0;
for (int i: [0,1,2,3,4]) {
  globalI = i;
  if (i == 2) {
    break;
  }
}
assert(globalI == 2);

sum = 0;
for (i: [0,1,2,3,4]) {
  if (i == 2) {
    continue;
  }
  sum += i;
}
assert(sum == 8);

sum = 0;
for (i: [0,1,2,3,4]) {
  if (i == 2) {
    if (i == 2) {
      continue;
    }
  }
  sum += i;
}
assert(sum == 8);

