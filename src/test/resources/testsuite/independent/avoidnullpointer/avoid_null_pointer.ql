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
assert(mmm() == null)
assert(a.n.c[2]==null)
assert(a.n.c[1:4]==null)