import com.alibaba.qlexpress4.test.lambda.TestQLambdaToFunction;
import com.alibaba.qlexpress4.runtime.QLambda;


Function f = a -> a + 3;
QLambda lambda = (QLambda)f;
assert(TestQLambdaToFunction.apply(lambda,1) == 4);