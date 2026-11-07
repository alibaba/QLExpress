// || operator: null should be treated as false, consistent with && and !
assert(true || true)
assert(true || false)
assert(false || true)
assertFalse(false || false)

// || with null operands: null is treated as false
assert(true || null)
assert(null || true)
assertFalse(false || null)
assertFalse(null || false)
assertFalse(null || null)

// 'or' keyword: same behavior as ||
assert(true or true)
assert(true or false)
assert(false or true)
assertFalse(false or false)

// 'or' with null operands
assert(true or null)
assert(null or true)
assertFalse(false or null)
assertFalse(null or false)
assertFalse(null or null)

// consistency with ! (not): !null == true, so null || true should be true
assert(!null)
assert(!null || false)
assert(false || !null)
