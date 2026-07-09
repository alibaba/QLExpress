assert('\' \\r \'' == "' \\r '")

assert('hello
world' == "hello\nworld")

a = "hello
qlexpress"
assert(a == "hello\nqlexpress")

assert("hello

qlexpress" == "hello
\nqlexpress")

// Unrecognized escape sequences should preserve both backslash and character (issue #335)
// This is critical for regex patterns embedded in string literals
assert('\d' == "\\d")
assert('\w' == "\\w")
assert('\s' == "\\s")
assert('\(' == "\\(")
assert('\.' == "\\.")

// Regex pattern in string literal should preserve all escape sequences
assert('(\d*)ch' == "(\\d*)ch")
assert('\d{3}-\d{4}' == "\\d{3}-\\d{4}")

// Recognized escapes should still work correctly
assert('\n' == "
")
assert('\t' == "	")
assert('\\' == "\\")
