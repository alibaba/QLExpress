package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Test;

public class StaticMethodTest {
    
    @Test
    public void testStaticMethod() throws Exception {
        String expressArray[] = new String[]{
                "StaticUtils.ITEM_DIM_MASTER",
                "StaticUtils.isVirtualSCItem(1L)",
                StaticUtils.class.getName()+".ITEM_DIM_MASTER",
                StaticUtils.class.getName()+".isVirtualSCItem(1L)"
        };
        ExpressRunner runner = new ExpressRunner(false,true);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("StaticUtils", StaticUtils.class);
        for(String express : expressArray) {
            Object r = runner.execute(express, context, null, false,
                    true);
            System.out.println(r);
        }
    }
    
    public static class StaticUtils {
        public static long ITEM_DIM_MASTER =   0x01;
        
        public static long ITEM_DIM_VIRTUAL =  0x02;
        
        
        public static boolean isVirtualSCItem (
                Long itemDim) {
            return itemDim != null && (itemDim & 0x0f) == ITEM_DIM_VIRTUAL;
        }
    }
    
}
