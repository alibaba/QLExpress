sum = 0;
for (i: [0,1,2,3,4]) {
  if (i == 2) {
    continue;
  }
  sum += i;
}
assert(sum==8)