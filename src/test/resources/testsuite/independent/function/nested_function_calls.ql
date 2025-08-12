// Test nested function calls with forward declarations
assert(outerFunc(5) == 25)

assert(calculateArea(3, 4) == 26) // calls getPerimeter inside - 2*(3+4) + 3*4 = 14 + 12 = 26

function outerFunc(x) {
    return innerFunc(x) * 5;
}

function innerFunc(x) {
    return x;
}

function calculateArea(width, height) {
    int perimeter = getPerimeter(width, height);
    return perimeter + (width * height);
}

function getPerimeter(w, h) {
    return 2 * (w + h);
}

// Test deeply nested calls
assert(level1(2) == 14) // level4(2)*3-1+2*2 = 6-1+2*2 = 5+2*2 = 7*2 = 14

function level1(x) {
    return level2(x) * 2;
}

function level2(x) {
    return level3(x) + 2;
}

function level3(x) {
    return level4(x) - 1;
}

function level4(x) {
    return x * 3;
}