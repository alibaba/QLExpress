package com.ql.util.express.issues;


import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class Issue82ExpressNested {

    @Test
    public void expressNest() throws Exception {
        String express = "order.orderType in (0,112,21) && skuPropertySatisfied(skuList, \"sku.categoryId == 3 && sku.price == 100\")";

        ExpressRunner runner = new ExpressRunner();

        Order order = new Order();
        order.setOrderType(21);
        order.setSkuList(makeSkuList());


        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("order", order);
        context.put("skuList", order.getSkuList());

        runner.addFunctionOfClassMethod("skuPropertySatisfied", SkuFunctionExpress.class.getName(),
                "skuPropertySatisfied", new String[]{List.class.getName(), "String"}, null);


        boolean orderSatisfied = (Boolean) runner.execute(express, context, null, true, false, null);

        assertTrue(orderSatisfied);
    }

    private List<Sku> makeSkuList() {
        return Collections.singletonList(new Sku(3, 100));
    }

}
