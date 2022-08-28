function setA(value) {
    a = value;
}

a = 10;
assert(a == 10);
setA(10000);
assert(a == 10000);