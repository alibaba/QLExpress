assertErrorCode(() -> {
    String s = "as";
    char a = s;
}, "INCOMPATIBLE_TYPE_FOR_ASSIGNMENT");

assertErrorCode(() -> {
    String s = "as";
    s = 1;
}, "INCOMPATIBLE_TYPE_FOR_ASSIGNMENT");