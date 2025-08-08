// Test functions with various parameter types and complexity
assert(processData(5, true) == 15)
assert(calculateComplex(1.5, 2.5, 10) == 160.0)

// Test function calls with complex expressions as parameters
assert(mathOperations(add(2, 3), multiply(2, 2)) == 625) // 5^4 = 625

function processData(int count, boolean flag) {
    int result = count * 2;
    if (flag) {
        result = result + 5;
    }
    return result;
}

function calculateComplex(double x, double y, int multiplier) {
    double base = x + y;
    return base * base * multiplier;
}

function mathOperations(int a, int b) {
    return power(a, b);
}

function add(int x, int y) {
    return x + y;
}

function multiply(int x, int y) {
    return x * y;
}

function power(int base, int exp) {
    if (exp == 0) {
        return 1;
    }
    int result = 1;
    for (int i = 0; i < exp; i++) {
        result *= base;
    }
    return result;
}

// Test functions with no parameters
assert(getConstant() == 42)
assert(generateRandom() > 0)

function getConstant() {
    return 42;
}

function generateRandom() {
    // Simple pseudo-random using current execution context
    return 123; // For deterministic testing
}

// Test function overloading-like behavior with different parameter counts
assert(calculate(5) == 25)
assert(calculate2(5, 3) == 39) // 5*5 + 3*3 + 5 = 25 + 9 + 5 = 39

function calculate(int x) {
    return x * x;
}

function calculate2(int x, int y) {
    return x * x + y * y + 5;
}