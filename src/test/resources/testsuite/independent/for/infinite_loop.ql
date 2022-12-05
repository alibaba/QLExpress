i = 0;
for (;;) {
  if (i > 3) {
    break;
  }
  i++;
}
assert(i == 4);

for (j = 0; ; j++) {
  if (j > 3) {
    break;
  }
}
assert(j == 4);