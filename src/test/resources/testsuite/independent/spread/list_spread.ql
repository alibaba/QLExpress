mm = {
  "aaa": 1,
  "bbb": 2
}

assert(mm*.key==["aaa", "bbb"])
assert(mm*.value==[1, 2])

methodMaps = [
  {
    "getNum": () -> 100,
  },
  {
    "getNum": () -> 101,
  },
  {
    "getNum": () -> 102,
  }
]

assert(methodMaps*.getNum()==[100,101,102])

a = [{"c":2}, null]
assertErrorCode(() -> a*.c, "NULL_FIELD_ACCESS")
assertErrorCode(() -> notExist*.c, "NONTRAVERSABLE_OBJECT")

b = [{"get100": () -> 100}, null]
assertErrorCode(() -> b*.get100(), "NULL_METHOD_ACCESS")
assertErrorCode(() -> notExist*.get100(), "NONTRAVERSABLE_OBJECT")