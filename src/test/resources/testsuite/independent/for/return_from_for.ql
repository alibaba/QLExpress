function test(l) {
  for (o:l) {
    if (o == 10) {
      return "find" + o;
    }
  }
}

r1 = test([3,4,10]);
assert(r1 == 'find10');

r2 = test([3,4,11]);
assert(r2 == null);