// Test for JSONObject put method support
import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson2.JSON;
import com.alibaba.qlexpress4.test.property.Parent;
import com.alibaba.fastjson2.JSONObject;

// Test JSONObject put method (now expected to work)
p = new Parent();
p.lockStatus = 1;
p.lockStatus2 = 1;
JSONObject reportData = new JSONObject();
reportData.put("doorStatus", p.lockStatus);
reportData.put("doorStatus2", p.lockStatus2);
reportData.put("operateType", "0");
reportData.put("openType", 1);

String jsonString = JSON.toJSONString(reportData);
println("JSONObject put result: " + jsonString);

assert(reportData.get("doorStatus") == 1);
assert(reportData.get("doorStatus2") == 1);

assert(reportData.get("operateType").equals("0"));
assert(reportData.get("openType") == 1);


return reportData;