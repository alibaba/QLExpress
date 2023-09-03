import com.alibaba.qlexpress4.test.lambda.UserFunctionalInterface;

UserFunctionalInterface ufi = (a, b) -> a + b;
assert(ufi.lala(1,2) == 3);