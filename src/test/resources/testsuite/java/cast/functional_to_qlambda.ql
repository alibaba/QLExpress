import com.alibaba.qlexpress4.runtime.QLambda;

int a = 10;
Runnable r = () -> a = a + 8;
QLambda lambda = (QLambda)r;
lambda.run();
assert(a == 18);

Supplier s = () -> "test";
QLambda lambdaSupplier = (QLambda)s;
assert(lambdaSupplier.get() == 'test');

Consumer c = (v) -> b = v + "-te";
QLambda lambdaConsumer = (QLambda)c;
lambdaConsumer.accept("ccc");
assert(b == 'ccc-te');

Function f = e -> e + 3;
QLambda lambdaFunction = (QLambda)f;
assert(lambdaFunction.apply(1) == 4);
