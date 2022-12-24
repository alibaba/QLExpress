macro bc {
  if (i < 5) {
    continue;
  }
}

s = 0;
for (int i = 0; i < 10; i++) {
  bc;
  s++;
}
assert(s == 5)