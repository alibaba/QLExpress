/*
{
  "errCode": "GET_FIELD_VALUE_CAN_NOT_ACCESS",
  "qlOptions": QLOptions.builder().allowAccessPrivateMethod(false)
}
*/
import com.alibaba.qlexpress4.test.property.SampleForPrivate;

SampleForPrivate a = new SampleForPrivate(4);
a.count = 5;
assert(a.count == 5);