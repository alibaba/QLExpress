a = 123;
b = "test"

assert("Hello ${a} ${b } ccc" == "Hello 123 test ccc");
// $ escape
assert("Hello \${a bb cc" == 'Hello ${a bb cc')
// selector variable
assert(${a} == 123)

assert("${a-1}" == "122")

assert("m xx ${
  if (b like 't%') {
      'YYY'
  }
}" == "m xx YYY")

assert("m xx ${
  if (b like 't%') {
      "YYY"
  }
}" == "m xx YYY")

// nest interpolation
assert("m xx ${
  if (b like 't%') {
      "YY${b}Y"
  }
}" == "m xx YYtestY")

assert("m xx ${
  if (b like 'mm%') {
      'YYY'
  }
}" == "m xx null")


