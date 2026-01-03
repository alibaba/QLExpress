map = {a:1, b:123}

function returnLeftValue() {
    return map.b;
}

c = returnLeftValue();
assert(c == 123);
// c's modification will not effect m.b
c = 190;
assert(map.b == 123);