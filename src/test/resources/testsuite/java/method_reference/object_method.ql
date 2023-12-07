import com.alibaba.qlexpress4.test.property.Sample;

refer = new Sample(3)::getCount;
assert(refer() == 3);
assert(new Sample(3).getCount() == 3);