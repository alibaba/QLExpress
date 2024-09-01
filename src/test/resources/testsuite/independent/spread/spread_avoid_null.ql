/*
{
  "qlOptions": QLOptions.builder().avoidNullPointer(true)
}
*/
a = [{"c":2}, null]
assert(a*.c==[2, null])
assert(notExist*.c==null)
assert(notExist*.c()==null)

b = [{"get100": () -> 100}, null]
assert(b*.get100()==[100, null])

Map[] arr1 = new Map[]{{"a":1},{"a":2}, null};
assert(arr1*.a==[1,2,null])

Map[] brr = new Map[]{{"get100": () -> 100}, null};
assert(brr*.get100()==[100,null])