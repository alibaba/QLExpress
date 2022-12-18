exprLambda = () -> 12;
assert(exprLambda() == 12);

blockLambda = () -> {
  return 6 + 6;
};
assert(blockLambda() == 12);