l = ["a-111", "a-222", "b-333", "c-888"]

l2 = l.stream()
      .filter(i -> i.startsWith("a-"))
      .map(i -> i.split("-")[1])
      .collect(Collectors.toList());
assert(l2 == ["111", "222"]);