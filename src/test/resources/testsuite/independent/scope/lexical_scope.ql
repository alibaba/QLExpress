String a = "lexical first";

aFactory = () -> a;

{
    String a = "runtime first-block";
    assert(aFactory() == "lexical first");
}

function testScope(a) {
    assert(aFactory() == "lexical first");
}

testScope("runtime first-function");