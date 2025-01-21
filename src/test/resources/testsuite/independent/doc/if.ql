a = 11;
// if ... else ...
assert(if (a >= 0 && a < 5) {
  true
} else if (a >= 5 && a < 10) {
  false
} else if (a >= 10 && a < 15) {
  true
} == true)

// if ... then ... else ...
r = if (a == 11) then true else false
assert(r == true)