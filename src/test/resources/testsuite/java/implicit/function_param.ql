function add(int a, int b){
    return a + b;
};

//param trans
assert(add(1L,444L) == 445L);

//return object
assert(add(Integer.MAX_VALUE,444L) == 2147484091L);

//result trans
assert(add(1L,444L) == 445d);

