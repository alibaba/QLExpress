macro add {
  c = a + b;
}

a = 1;
b = 2;
add;
assert(c == 3);
b = 10;
add;
assert(c == 11);
// variable has the same name with macro
add = 100;
a = 3;
add;
assert(c == 13);
assert(add == 100);

// expression auto return
function macroReturn(a, b) {
  add
}

assert(macroReturn(6,7)==13)