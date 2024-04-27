a = try {
    throw 10;
} catch (int a) {
    100
} catch (int b) {
    1000
}

assert(a == 100)