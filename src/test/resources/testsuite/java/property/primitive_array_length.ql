// Test: .length field access on primitive arrays returned by Java methods
// String.getBytes() returns a real byte[] (not Byte[]), which previously
// caused ClassCastException in loadField due to ((Object[])bean).length.

bytes = "hello".getBytes();
assert(bytes.length == 5);

emptyBytes = "".getBytes();
assert(emptyBytes.length == 0);

// Also test with char[] from String.toCharArray()
chars = "world".toCharArray();
assert(chars.length == 5);
