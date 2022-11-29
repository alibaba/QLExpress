b = () -> if (a != 100)
  return 11;
else
  return 12;
;
assert(b() == 11);