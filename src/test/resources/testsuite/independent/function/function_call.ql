function add(int a, int b) {
    return a+b;
}

assert(add(1,1)==2)

function sub(a, b) {
    return a-b;
}

assert(sub(3,1)==2)

assertErrorCode(() -> {add(1, "2")}, "INVALID_ARGUMENT")

assert(check(3,1)==false)

function check(a, b) {
    return a < b;
}
