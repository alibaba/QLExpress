import com.alibaba.qlexpress4.test.property.Parent;
import org.apache.commons.lang3.StringUtils;
p = new Parent();
p.setBirth(null);
assert(p.birth==null);
Object i = ((String)p.getBirth());
assert(i==null);
if(true) {
}
((String)p.getBirth());
return StringUtils.isNotBlank(p.getBirth())
