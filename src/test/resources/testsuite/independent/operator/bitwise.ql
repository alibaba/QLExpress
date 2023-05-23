assert(true & true);
assertFalse(true & false);
assertFalse(true & null);

assertFalse(false & true);
assertFalse(false & false);
assertFalse(false & null);

assert(true | true);
assert(true | false);
assert(true | null);

assert(false | true);
assertFalse(false | false);
assertFalse(false | null);

assertFalse(true ^ true);
assert(true ^ false);
assert(true ^ null);

assert(false ^ true);
assertFalse(false ^ false);
assertFalse(false ^ null);
