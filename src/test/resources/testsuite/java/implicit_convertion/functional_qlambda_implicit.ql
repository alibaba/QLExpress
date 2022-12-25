import com.alibaba.qlexpress4.test.function.TestFunctionToQLambda;
import com.alibaba.qlexpress4.runtime.QLambda;

int a = 10;
Runnable r = () -> a = a + 8;
assert(TestFunctionToQLambda.runnable(r) == 18);

