package com.ql.util.express.issues;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;

import java.util.List;

public class SkuFunctionExpress {
    public boolean skuPropertySatisfied(List<Sku> skuList, String exressNested) {
        ExpressRunner runner = new ExpressRunner();

        for (Sku sku: skuList) {
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();
            context.put("sku", sku);

            try {
                boolean expressNestedResult = (Boolean) runner.execute(exressNested, context, null,
                        true, false, null);

                if (expressNestedResult) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return false;
    }
}
