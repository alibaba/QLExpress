a = [1,2,3,4].filter(i -> i > 2)
assert(a == [3,4])
assert(a instanceof List)

assert([1,2].map(i -> i+2) == a)

assertErrorCode(() -> {'a':1}.filter(en -> en.value > 10), "METHOD_NOT_FOUND")