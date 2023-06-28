assert(true && true);
assertFalse(true && false);
assertFalse(true && null);

assertFalse(false && true);
assertFalse(false && false);
assertFalse(false && null);

assertFalse(null && true);
assertFalse(null && false);
assertFalse(null && null);
