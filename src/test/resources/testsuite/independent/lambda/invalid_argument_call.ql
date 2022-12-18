/*
{
  "errCode": "INVALID_ARGUMENT"
}
*/
a = () -> {
  return (int c) -> c + 1;
};

a()("abc");