try {
    BigDecimal divisor = new BigDecimal(3);
    BigDecimal dividend = new BigDecimal(1);
    dividend.divide(divisor);
    assert(false);
} catch (ArithmeticException e) {
    assert(e != null);
}
