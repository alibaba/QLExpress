import com.alibaba.qlexpress4.test.constructor.HelloConstructor;
import com.alibaba.qlexpress4.test.constructor.HelloParent;
import com.alibaba.qlexpress4.test.constructor.HelloChild;

h = new HelloConstructor(new HelloParent());
assert(h.flag == 0);

h = new HelloConstructor(new HelloChild());
assert(h.flag == 1);

h = new HelloConstructor("s", "s1");
assert(h.flag == 2);

h = new HelloConstructor("s");
assert(h.flag == 3);

h = new HelloConstructor(new HelloChild(), () -> 12);
assert(h.flag == 4);

h = new HelloConstructor(new HelloParent(), () -> 12);
assert(h.flag == 5);

// var args
h = new HelloConstructor();
assert(h.flag == 2);