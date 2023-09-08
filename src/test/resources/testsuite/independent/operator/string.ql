x = "hello " + "there"
assert(x == "hello there")

x = "hello " + 2
assert(x == "hello 2")

x = "hello " + 1.2
assert(x == "hello 1.2")

y = x + 1
assert(y == "hello 1.21")

x = "hello" + " " + "there" + " nice" + " day"
assert(x == "hello there nice day")

assert("bc" > "ab")
assert("bc" > "ab")
assert("bcd" > "ab")
assert("bcd" > "abc")