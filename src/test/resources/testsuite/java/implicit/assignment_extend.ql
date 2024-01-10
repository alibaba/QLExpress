//double→(Double)→long(lost precise)
double d2 = 0.1d;
long l2 = d2;
assert(l2 == 0l);

//double→(Double)→int(lost precise && use max bound)
d2 = 11111111111.1d;
int i1 = d2;
assert(i1 == 2147483647);

//double→(Double)→int(lost precise && use min bound)
d2 = -11111111111.1d;
int i2 = d2;
assert(i2 == -2147483648);

//double->BigInteger
BigInteger big = d2;
assert(big == -11111111111);

//long→BigInteger
BigInteger big = 11111111111l;
assert(big == 11111111111l);

//long→BigDecimal
BigDecimal big = 11111111111l;
assert(big == 11111111111l);

//long→BigDecimal
BigDecimal big = 11111111111l;
assert(big == 11111111111l);


//long→BigDecimal
BigDecimal big = 11111111111l;
long l1 = big;
assert(l1 == 11111111111l);

//String→char
String s1 = "a";
char c1 = s1;
assert(c1 == 97);


