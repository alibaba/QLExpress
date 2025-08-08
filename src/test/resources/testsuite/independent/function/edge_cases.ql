// Test edge cases for function hoisting
// Test function called immediately at global level before definition
int immediateResult = callImmediately();
assert(immediateResult == 42)

// Test function in conditional blocks called before definition  
if (true) {
    assert(conditionalFunction(5) == 10)
}

// Test function in loop called before definition
for (int i = 0; i < 2; i++) {
    assert(loopFunction(i) == i * 3)
}

// Test function with same name as built-in (should work)
assert(toString(123) == "custom_123")

// Test function that returns function call result
assert(chainReturn() == 999)

// Function definitions (after all calls)
function callImmediately() {
    return 42;
}

function conditionalFunction(int x) {
    return x * 2;
}

function loopFunction(int x) {
    return x * 3;
}

function toString(int value) {
    return "custom_" + value;
}

function chainReturn() {
    return getSpecialValue();
}

function getSpecialValue() {
    return 999;
}

// Test empty function
assert(emptyFunction() == null)

function emptyFunction() {
    // Empty body
}

// Test function that just returns constant
assert(constantFunction() == "CONSTANT")

function constantFunction() {
    return "CONSTANT";
}

// Test functions with early returns
assert(earlyReturnFunction(true) == 1)
assert(earlyReturnFunction(false) == 2)

function earlyReturnFunction(boolean condition) {
    if (condition) {
        return 1;
    }
    return 2;
}

// Test function calling itself indirectly (through another function)
assert(indirectSelfCall(3) == 6)

function indirectSelfCall(int n) {
    if (n <= 0) {
        return 0;
    }
    return n + helperForIndirect(n - 1);
}

function helperForIndirect(int n) {
    return indirectSelfCall(n);
}

// Test function with multiple return types based on conditions
function dynamicReturn(int choice) {
    if (choice == 1) {
        return 100;
    } else if (choice == 2) {
        return "text";
    } else {
        return true;
    }
}

// Test the dynamic returns
assert(dynamicReturn(1) == 100)
assert(dynamicReturn(2) == "text")
assert(dynamicReturn(3) == true)