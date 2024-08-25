function testMacroInSubScope() {
  macro add {
    int c = a + b;
  }
  int a = 1;
  int b = 10;
  add;
  return c;
}

c = testMacroInSubScope()
assert(c==11)
a = 11
b = 100
// add is not a macro in this scope
add
assert(c==11)