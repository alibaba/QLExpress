assert(98 > (char)'a');
assert(98 == (char)'b');
assert(98 < (char)'c');

assert((char)'a' < 98);
assert((char)'b' == 98);
assert((char)'c' > 98);

assert(98 != (char)'a');
assert(98 <> (char)'a');
assert((char)'a' != 98);
assert((char)'a' <> 98);

assert((char)'a' < (char)'b');
assert((char)'b' == (char)'b');
assert((char)'c' > (char)'b');
