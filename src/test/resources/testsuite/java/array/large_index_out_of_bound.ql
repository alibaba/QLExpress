check = (values) -> {
    assert(values[1L] == 20);
    assert(values[-1] == 30);
    assert(values[2.8] == 30);
    assert(values[-1.8] == 30);
    for (index : [4294967297L, -4294967295L, 18446744073709551617,
                  -18446744073709551615,
                  new java.math.BigDecimal("4294967297.8")]) {
        assertErrorCode(() -> values[index], "INDEX_OUT_BOUND");
        assertErrorCode(() -> { values[index] = 99; }, "INDEX_OUT_BOUND");
        assert(values[1] == 20);
    }
};

check([10, 20, 30]);
check(new int[] {10, 20, 30});
check(new Integer[] {10, 20, 30});

mapping = {sentinel: 10};
mapping[4294967297L] = 42;
assert(mapping[4294967297L] == 42);
