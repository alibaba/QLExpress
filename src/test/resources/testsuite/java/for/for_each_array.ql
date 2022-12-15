i = 0;
for (ele: new int[] {1,2,3}) {
  assert(ele == (++i));
}

nestedArr = new int[][] {new int[] {1,2}, new int[] {3,4}};
j = 0;
for (arr: nestedArr) {
  for (int ele: arr) {
    assert(ele == (++j));
  }
}
