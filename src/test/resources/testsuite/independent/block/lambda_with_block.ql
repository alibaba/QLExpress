f = (x) -> {
   int e = 10;
   if (x > 5) {
     x + e
   } else {
     x * 2
   }
};
assert(f(6) == 16);
assert(f(3) == 6);