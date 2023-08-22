assert(a?.b?.c?.d == null);

try {
    assert(a?.b.c == null);
    throw new RuntimeException();
} catch (e) {
    assert(e instanceof NullPointerException);
}

mm = {cc: 123}

assert(mm?.cc == 123);
assert(mm.dd?.ee == null);
assert(mm.dd?.test() == null);