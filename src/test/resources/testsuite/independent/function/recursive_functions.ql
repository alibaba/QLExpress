// Test recursive functions with forward declarations
assert(factorial(5) == 120)
assert(fibonacci(6) == 8)
assert(gcd(48, 18) == 6)

// Forward references to recursive functions
int result1 = factorial(4);
assert(result1 == 24)

int result2 = fibonacci(7);
assert(result2 == 13)

function factorial(n) {
    if (n <= 1) {
        return 1;
    }
    return n * factorial(n - 1);
}

function fibonacci(n) {
    if (n <= 1) {
        return n;
    }
    return fibonacci(n - 1) + fibonacci(n - 2);
}

function gcd(a, b) {
    if (b == 0) {
        return a;
    }
    return gcd(b, a % b);
}

// Test mutually recursive functions called before definition
assert(isEven(10) == true)
assert(isOdd(10) == false)
assert(isEven(7) == false)
assert(isOdd(7) == true)

function isEven(n) {
    if (n == 0) {
        return true;
    }
    return isOdd(n - 1);
}

function isOdd(n) {
    if (n == 0) {
        return false;
    }
    return isEven(n - 1);
}