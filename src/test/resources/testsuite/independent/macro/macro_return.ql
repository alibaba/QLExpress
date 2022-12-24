macro test {
  1+1
}

l = () -> {
  test
};

assert(l() == 2)