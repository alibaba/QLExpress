import com.alibaba.qlexpress4.test.property.SampleEnum;

String a = "normal";
SampleEnum b = (SampleEnum) a;
assert(b.equals(SampleEnum.NORMAL));