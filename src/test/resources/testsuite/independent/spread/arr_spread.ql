Map[] arr = new Map[]{{"a":1},{"a":2}};
assert(arr*.a==[1,2])

Map[] arr1 = new Map[]{{"a":1},{"a":2}, null};
assertErrorCode(() -> arr1*.a, "GET_FIELD_FROM_NULL")

Map[] b = new Map[]{{"get100": () -> 100}, null};
assertErrorCode(() -> b*.get100(), "GET_METHOD_FROM_NULL")