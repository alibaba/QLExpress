a = {
    func: (long c) -> c + 2,
    func: (int c) -> c + 1
};

assert(a.func(1) == 2);
assert(a.func(1L) == 3L);