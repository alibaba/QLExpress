assert('\' \\r \'' == "' \\r '")

assert('hello
world' == "hello\nworld")

a = "hello
qlexpress"
assert(a == "hello\nqlexpress")

assert("hello

qlexpress" == "hello
\nqlexpress")