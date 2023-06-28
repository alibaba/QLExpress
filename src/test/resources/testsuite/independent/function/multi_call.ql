function mc() {
    return () -> 10;
}

assert(mc()()==10);

a = {:};

function a() {
    return a;
}

a().b = 10;

assert(a.b==10);