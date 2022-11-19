function returnFromIf(a) {
    int i = if (a > 10) {
      return 100;
    } else {
      if (a < 5) {
        return 1000;
      }
      return 101;
    };
}

assert(returnFromIf(11) == 100);
assert(returnFromIf(5) == 101);
assert(returnFromIf(-5) == 1000)