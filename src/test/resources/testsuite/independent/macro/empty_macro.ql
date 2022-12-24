macro empty {
}

function func() {
  1+1;
  empty;
}

assert(func() == null);