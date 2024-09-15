assertErrorCode(() -> {
    String s = "as";
    char a = s;
}, "INCOMPATIBLE_ASSIGNMENT_TYPE");

assertErrorCode(() -> {
    String s = "as";
    s = 1;
}, "INCOMPATIBLE_ASSIGNMENT_TYPE");