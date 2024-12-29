assert(try {
    100 + 1/0
} catch(e) {
    // Throw a zero-division exception
    11
} == 11)