a = [1,2,3,4,5,6];
assert(a[3:] == [4,5,6]);
assert(a[:2] == [1,2]);
assert(a[2:4] == [3,4]);
assert(a[4:10] == [5, 6]);
assert(a[-88:100] == [1,2,3,4,5,6])