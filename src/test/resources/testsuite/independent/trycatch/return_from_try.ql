function tryTest() {
    try {
        return 10;
    } catch (ignore) {
    }
    return 1000;
}

assert(tryTest() == 10)

function catchTest() {
    try {
        throw 10;
    } catch (ignore) {
        return 1000;
    }
    return 10000;
}

assert(catchTest()==1000)

function returnInsideFinally() {
    try {
        return 30;
    } catch (ignore) {
    } finally {
        return 9000;
    }
}

assert(returnInsideFinally() == 30)