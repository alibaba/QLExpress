i = 0;
sum = 0;
// m not in scope
while (i < 4 && m == null) {
  int m = 10;
  sum += (i++);
}
assert(sum==6);