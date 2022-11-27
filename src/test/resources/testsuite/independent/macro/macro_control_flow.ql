macro control {
  if (i > 3) {
    return;
  }
};

t = -1;
forBody = (i) -> {
  control;
  t = i;
};

forBody(10);
assert(t == -1);
forBody(2);
assert(t == 2);