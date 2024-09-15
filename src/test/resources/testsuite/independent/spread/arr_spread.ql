Map[] arr = new Map[]{{"a":1},{"a":2}};
assert(arr*.a==[1,2])

Map[] arr1 = new Map[]{{"a":1},{"a":2}, null};
assertErrorCode(() -> arr1*.a, "NULL_FIELD_ACCESS")

Map[] b = new Map[]{{"get100": () -> 100}, null};
assertErrorCode(() -> b*.get100(), "NULL_METHOD_ACCESS")