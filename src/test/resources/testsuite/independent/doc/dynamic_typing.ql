// Dynamic Typeing
a = 1;
a = "1";
// Static Typing
int b = 2;
// throw QLException with error code INCOMPATIBLE_ASSIGNMENT_TYPE when assign with incompatible type String
assertErrorCode(() -> b = "1", "INCOMPATIBLE_ASSIGNMENT_TYPE")

