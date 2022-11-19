dict = {a: 123, b: 234, c:[1,2,3]};
map = new HashMap();
map.put('a', 123);
map.put('b', 234);
map.put('c', [1,2,3]);
assert(dict == map);
assert(dict.get('a') == 123);
assert(dict.a == 123);
assert(dict['a'] == 123);