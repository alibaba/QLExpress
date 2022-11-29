/*
{
  "errCode": "BREAK_CONTINUE_NOT_IN_LOOP"
}
*/
macro bc {
  if (i < 5) {
    continue;
  }
}

for (int i = 0; i < 10; i++) {
  bc;
}