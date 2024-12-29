a = 123
assert("hello,${a-1}" == "hello,122")

b = "test"
assert("m xx ${
  if (b like 't%') {
      'YYY'
  }
}" == "m xx YYY")