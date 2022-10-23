/*
{
  "qlOptions": QLOptions.builder().avoidNullPointer(true)
}
*/
assert(a.b == null);
assert(a.b.c == null);
assert(a.b() == null);
assert(a.b().c.d() == null);
assert(a::b == null);
assert(a.b.c.mm() == null);
assert(a.b.c.mm()() == null);
assert(mmm() == null)