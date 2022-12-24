List b = [1,2,3];
int[] ba = (int[]) b;
assert(ba[0] == 1);
assert(ba[1] == 2);
assert(ba[2] == 3);
assert(ba.class == int[].class);

List l = [1, "c", {a: 10}];
Object[] la = (Object[]) l;
assert(la[0] == 1);
assert(la[1] == "c");
assert(la[2] == {a: 10});
assert(la.class == Object[].class);

List l1 = [{a: 10}, {b: 20}, {c: 30}];
Map<String, Integer>[] l1a = (Map<String, Integer>[]) l1;
assert(l1a[0] == {a: 10});
assert(l1a[1] == {b: 20});
assert(l1a[2] == {c: 30});
assert(l1a.class == Map[].class);