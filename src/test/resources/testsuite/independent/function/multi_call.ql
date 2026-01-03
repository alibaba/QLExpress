a = {:};

function a() {
    return a;
}

a().b = 10;

assert(a.b==10);