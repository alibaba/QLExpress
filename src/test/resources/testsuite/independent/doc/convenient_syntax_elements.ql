// list
l = [1,2,3]
assert(l[0]==1)
// Underlying data type of list is ArrayList in Java
assert(l instanceof ArrayList)
// map
m = {
  "aa": 10,
  "bb": {
    "cc": "cc1",
    "dd": "dd1"
  }
}
assert(m['aa']==10)
// Underlying data type of map is ArrayList in Java
assert(m instanceof LinkedHashMap)