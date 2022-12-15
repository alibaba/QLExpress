assert(0 == 0);
assert(512 == 512);
assert(512 == 512L);
assert(512 == 512F);
assert(512 == 512D);

assert(512L == 512L);
assert(512L == 512F);
assert(512L == 512D);

assert(512F == 512F);
assert(512F == 512D);

assert(512D == 512D);

assertFalse(512 == 513);

// assertFalse(97 == 'b');
// 单字符解析为了字符串"a"
// assert('a' == 97);
// assert(new Character('a') == 97);