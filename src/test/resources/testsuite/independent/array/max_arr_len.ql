/*
{
  "qlOptions": QLOptions.builder().maxArrLength(10)
}
*/

try {
    a = new int[10]
    a = new int[1][2][10][10][9]
} catch(o) {
    assert(false);
}

assertErrorCode(() -> new int[11], "EXCEED_MAX_ARR_LENGTH")
assertErrorCode(() -> new int[1][13][3], "EXCEED_MAX_ARR_LENGTH")