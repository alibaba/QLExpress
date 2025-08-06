import com.alibaba.qlexpress4.test.property.Parent;

p = new Parent();
p.setBirth(null);
assert(p.birth==null);

p.birth = "2025-01-01";
assert(p.birth=="2025-01-01");

p.birth = null;
assert(p.birth==null);
