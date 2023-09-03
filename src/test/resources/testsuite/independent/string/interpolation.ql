a = 123;
b = "test"

assert("Hello ${a} ${b } ccc" == "Hello 123 test ccc");
assert("Hello ${a bb cc" == "Hello ${a bb cc")
assert(${a} == 123)