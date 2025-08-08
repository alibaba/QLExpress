// Test function scoping and parameter shadowing
int outerVariable = 10;

// Test function with same parameter name as global variable
assert(testScoping(5) == 15)

// Test that global variable is not affected
assert(outerVariable == 10)

function testScoping(int outerVariable) {
    // Parameter shadows global variable
    return outerVariable + 10;
}

// Test nested scoping with local variables
assert(nestedScoping(3) == 18)

function nestedScoping(int x) {
    int localVar = x * 2; // 6
    if (localVar > 5) {
        int innerVar = localVar * 2; // 12
        return innerVar + 6; // 18
    }
    return localVar;
}

// Test functions that call other functions with same parameter names
assert(chainedScoping(2) == 14)

function chainedScoping(int value) {
    return helperFunction(value + 1);
}

function helperFunction(int value) {
    // Different 'value' parameter
    return value * 4 + 2; // (2+1) * 4 + 2 = 14
}

// Test function with multiple parameters having local scope
assert(multipleParams(1, 2, 3) == 12)

function multipleParams(int a, int b, int c) {
    int sum = a + b + c;
    int doubled = sum * 2;
    return doubled;
}

// Test function that modifies parameters (local copies)
int originalValue = 5;
assert(modifyParameter(originalValue) == 25)
assert(originalValue == 5) // Original should remain unchanged

function modifyParameter(int param) {
    param = param * 5; // Modifying local copy
    return param;
}

// Test function with conditional blocks and local variables
assert(conditionalScoping(true, 10) == 30)
assert(conditionalScoping(false, 10) == 0) // 10 - 10 = 0

function conditionalScoping(boolean condition, int base) {
    int result = base;
    if (condition) {
        int bonus = 20;
        result = result + bonus;
    } else {
        int penalty = 10;
        result = result - penalty;
    }
    return result;
}

// Test loops with local scope
assert(loopScoping(3) == 6)

function loopScoping(int count) {
    int total = 0;
    for (int i = 1; i <= count; i++) {
        int squared = i; // Local to loop iteration
        total += squared;
    }
    return total; // 1 + 2 + 3 = 6
}