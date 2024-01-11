function add(int a, long b){
    return a + b;
};

//param trans
assert(add(1L,444L) == 445L);

//return object
assert(add(Integer.MAX_VALUE,444L) == 2147484091L);

//result trans
assert(add(1L,444L) == 445d);

function convertChar(char a) {
    return a;
}

assert(convertChar('a')==(char)'a');
assert(convertChar((String)'a')==(char)'a');