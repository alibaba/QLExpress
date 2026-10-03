// Test: 'in' and 'not_in' operators with primitive arrays returned by Java methods.
// String.getBytes() returns a real byte[] (not Byte[]), which previously
// caused ClassCastException in the 'in' operator due to (Object[]) cast.

bytes = "ABC".getBytes();

// primitive byte array 'in' checks
assert(65 in bytes);
assert(66 in bytes);
assert(67 in bytes);
assertFalse(0 in bytes);
assertFalse(99 in bytes);

// 'not_in' with primitive byte array
assert(65 not_in "abc".getBytes());
assertFalse(65 not_in bytes);

// null handling with primitive array
assertFalse(null in bytes);
assert(null not_in bytes);
