import com.alibaba.qlexpress4.test.stream.STObject;

l = [new STObject("aa"), new STObject("bb")].stream().map(STObject::getPayload).collect(Collectors.toList())

println(l)
assert(l == ["aa", "bb"])