package com.ql.util.express.test.rating;

import java.util.HashMap;
import java.util.Map;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

/**
 * 分成配置范例,通过动态属性来实现
 *
 * @author xuannan
 */
public class RatingWithPropertyTest {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void testRating() throws Exception {
        Map logisticsOrder = new HashMap();
        Map tcOrder = new HashMap();
        Map goodsOrder = new HashMap();
        //设置物流订单信息
        logisticsOrder.put("重量", 4);
        logisticsOrder.put("仓储TPID", "玄难");
        logisticsOrder.put("物流TPID", "云殊");
        logisticsOrder.put("包装TPID", "千绝");
        //建立计算器
        ExpressRunner runner = new ExpressRunner();
        //增加自定义函数
        runner.addFunction("费用科目", new SubjectOperator("费用科目"));
        //装载分成规则rating.ql文件
        runner.loadExpress("ratingWithProperty");
        //设置上下文
        DefaultContext<String, Object> context = new DefaultContext<>();
        context.put("物流订单", logisticsOrder);
        context.put("交易订单", tcOrder);
        context.put("仓储订单", goodsOrder);
        SubjectManager subjectManager = new SubjectManager();
        context.put("费用", subjectManager);

        runner.executeByExpressName("ratingWithProperty", context, null, false, false);
        //输出分成结果
        System.out.println("----------分成结果----------------");
        for (Object item : subjectManager.getSubjectValues()) {
            System.out.println(item);
        }
    }
}
