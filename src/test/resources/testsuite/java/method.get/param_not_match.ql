/*
{
  "errCode": "PARAM_NOT_MATCH"
}
*/
import com.alibaba.qlexpress4.test.property.Sample;

refer = new Sample(3)::getCount;
assert(refer(1) == 1);
