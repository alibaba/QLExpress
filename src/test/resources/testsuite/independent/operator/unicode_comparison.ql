// Unicode comparison operators (issue #414)
// ≠ (U+2260, NOT EQUAL TO) - alias for !=
// ≥ (U+2265, GREATER-THAN OR EQUAL TO) - alias for >=
// ≤ (U+2264, LESS-THAN OR EQUAL TO) - alias for <=

// Basic inequality with ≠
assert(1 ≠ 2);
assertFalse(1 ≠ 1);
assert(3 ≠ 4);
assertFalse(0 ≠ 0);

// Greater than or equal with ≥
assert(2 ≥ 1);
assert(2 ≥ 2);
assertFalse(1 ≥ 2);
assert(100 ≥ 99);
assert(100 ≥ 100);
assertFalse(99 ≥ 100);

// Less than or equal with ≤
assert(1 ≤ 2);
assert(2 ≤ 2);
assertFalse(2 ≤ 1);
assert(99 ≤ 100);
assert(100 ≤ 100);
assertFalse(100 ≤ 99);

// Unicode operators with variables
a = 5;
b = 10;
assert(a ≤ b);
assert(b ≥ a);
assert(a ≠ b);
assertFalse(a ≥ b);
assertFalse(b ≤ a);
assertFalse(a ≠ a);

// Unicode operators mixed with regular operators
assert(1 ≤ 2 && 2 ≥ 1);
assert(1 ≤ 2 || 1 ≥ 2);
assert(3 ≥ 3 && 3 ≠ 4);

// Unicode operators with floating point numbers
assert(1.5 ≤ 2.5);
assert(2.5 ≥ 1.5);
assert(1.5 ≠ 2.5);
assertFalse(1.5 ≥ 2.5);
assertFalse(2.5 ≤ 1.5);

// Unicode operators with negative numbers
assert(-1 ≤ 0);
assert(0 ≥ -1);
assert(-1 ≠ 0);
assert(-5 ≤ -1);
assert(-1 ≥ -5);

// Unicode operators in conditional expressions
x = 10;
if (x ≥ 10) {
    result1 = "big";
} else {
    result1 = "small";
}
assert(result1 == "big");

if (x ≤ 5) {
    result2 = "small";
} else {
    result2 = "big";
}
assert(result2 == "big");

if (x ≠ 10) {
    result3 = "different";
} else {
    result3 = "same";
}
assert(result3 == "same");
