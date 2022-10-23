/*
{
  "errCode": "GET_FIELD_VALUE_ERROR",
  "qlOptions": QLOptions.builder().allowAccessPrivateMethod(false)
}
*/
import com.alibaba.qlexpress4.test.property.SampleForPrivate;

SampleForPrivate a = new SampleForPrivate(5);
assert(a.count == 5);