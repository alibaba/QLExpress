/*
{
  "errCode": "INVALID_ASSIGNMENT"
}
*/
import com.alibaba.qlexpress4.test.property.TestEnum;

v = TestEnum.SKT.value;
assert(v == -1);
v = 10;
assert(v == 10);
TestEnum.SKT.value = 100;