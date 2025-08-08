// Test mixed function and variable declarations with forward references
int globalVar = computeInitialValue();
int message = formatMessage(42, getValue());

assert(globalVar == 100)
assert(message == 92) // 42 + 50 = 92

// Variables referencing functions before they're defined
int result1 = doubleValue(25);
assert(result1 == 50)

function computeInitialValue() {
    return 100;
}

function formatMessage(int prefix, int value) {
    return prefix + value;
}

function getValue() {
    return 50;
}

function doubleValue(int x) {
    return x * 2;
}

// Test functions that modify and return based on global state
int counter = 0;

function increment() {
    counter = counter + 1;
    return counter;
}

function getCounterValue() {
    return counter;
}

// Test the counter functions
int first = increment();   // Should be 1
int second = increment();  // Should be 2
int current = getCounterValue(); // Should be 2

assert(first == 1)
assert(second == 2) 
assert(current == 2)

// Test simple calculation with constants
function simpleCalculation() {
    return 10 * 30; // 300
}

assert(simpleCalculation() == 300)