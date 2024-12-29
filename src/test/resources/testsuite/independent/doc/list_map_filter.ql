l = ["a-111", "a-222", "b-333", "c-888"]
assert(l.filter(i -> i.startsWith("a-"))
        .map(i -> i.split("-")[1]) == ["111", "222"])