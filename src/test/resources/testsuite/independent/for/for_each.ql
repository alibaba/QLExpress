i = 0;
l = [1,2,3];
for (ele : l) {
  assert(l[i++] == ele);
}

j = 0;
for (int ele : l) {
  assert(l[j++] == ele);
}