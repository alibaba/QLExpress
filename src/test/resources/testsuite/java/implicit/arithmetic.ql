//trans after arithmetic
byte b1 = 1;
byte b2 = 2;
int i = b1 + b2;
assert(i == 3);

//trans in arithmetic
int imax = Integer.MAX_VALUE;
int imin = Integer.MIN_VALUE;
long s = imax - (long)imin;
assert(s == 4294967295l);
