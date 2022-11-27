import com.alibaba.qlexpress4.test.property.SampleEnum;

String a = "NORMAL";
SampleEnum b = (SampleEnum) a;
assert(b.equals(SampleEnum.NORMAL));