a = [1,2,3, "123"];
assert(a == [1,2,3, "123"]);
assert(a != [1,2,3, "125"]);
assert(a[0] == 1 && a[3] == "123");
assert(a.length == 4);