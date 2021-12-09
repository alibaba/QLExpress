package com.ql.util.express.test.rating;

import java.util.HashMap;
import java.util.Map;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

/**
 * 分成配置范例
 *
 * @author xuannan
 */
public class RatingTest {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testRating() throws Exception {
        Map logisticsOrder = new HashMap();
        Map tcOrder = new HashMap();
        Map goodsOrder = new HashMap();
        Map subjectValue = new HashMap();
        //设置物流订单信息
        logisticsOrder.put("重量", 4);
        logisticsOrder.put("仓储TP", "玄难");
        logisticsOrder.put("物流TP", "云殊");
        logisticsOrder.put("包装TP", "千绝");
        //建立计算器
        ExpressRunner runner = new ExpressRunner(true, true);
        //增加自定义函数
        runner.addFunction("费用科目", new SujectOperator("费用科目"));
        //装载分成规则rating.ql文件
        runner.loadExpress("rating");
        //设置上下文
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("物流订单", logisticsOrder);
        context.put("交易订单", tcOrder);
        context.put("仓储订单", goodsOrder);
        context.put("费用科目", subjectValue);
        //执行指令
        runner.executeByExpressName("rating", context, null, false, false, null);
        //		  runner.executeByExpressName("rating",context, null, false,false,null);
        //		while(true){
        //		  runner.executeByExpressName("rating",context, null, false,false,null);
        //		}
        //输出分成结果
        System.out.println("----------分成结果----------------");
        for (Object item : subjectValue.values()) {
            System.out.println(item);
        }
    }
}
