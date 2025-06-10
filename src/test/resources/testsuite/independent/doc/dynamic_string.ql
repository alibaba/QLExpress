a = 123
assert("hello,${a-1}" == "hello,122")

// escape $ with \$
assert("hello,\${a-1}" == "hello,\${a-1}")

b = "test"
assert("m xx ${
  if (b like 't%') {
      'YYY'
  }
}" == "m xx YYY")