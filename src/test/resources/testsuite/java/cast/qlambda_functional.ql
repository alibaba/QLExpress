import com.alibaba.qlexpress4.runtime.QLambda;

int a = 10;
Runnable r = () -> a = a + 8;
QLambda lambda = (QLambda)r;
assert(lambda() == 18);
Runnable rs = (Runnable)lambda;
assert(rs() == 26);
Consumer rsc = (Consumer)lambda;
assert(rsc.get() == 34);

Function f = a -> a + 3;
QLambda lambda = (QLambda)f;
assert(lambda.apply(1) == 4);
Function fs = (Function)lambda;
assert(fs.apply(1) == 4);


