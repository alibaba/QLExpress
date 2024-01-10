//pointer in array
int[] array = new int[]{1,2,3};
Long[] newArray = new Long[]{array[0], array[1], array[2]};
assert(newArray[0] == 1);
assert(newArray[1] == 2);
assert(newArray[2] == 3);