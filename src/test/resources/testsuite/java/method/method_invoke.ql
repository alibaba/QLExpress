import com.alibaba.qlexpress4.test.method.TestChild;

tc = new TestChild();
assert(tc.get10() == 10);
assert(tc.get10('') == 11);
assert(tc.get1() == 1);
assert(tc.get100() == 100);