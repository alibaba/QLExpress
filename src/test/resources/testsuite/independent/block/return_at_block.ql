function returnAtBlock(a) {
    int i = if (a > 10) {
      {
        return 100;
      }
    } else {
      if (a < 5) {
        {return 1000;}
      }
      {return 101;}
    };
    10000
}

assert(returnAtBlock(11) == 100);
assert(returnAtBlock(5) == 101);
assert(returnAtBlock(-5) == 1000)