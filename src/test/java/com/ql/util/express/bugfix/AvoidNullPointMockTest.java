package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.config.QLExpressRunStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AvoidNullPointMockTest {
    
    
    public class DemoObject{
        private String code;
        private DemoObject parent;
    
        public String getCode() {
            return code;
        }
    
        public void setCode(String code) {
            this.code = code;
        }
    
        public DemoObject getParent() {
            return parent;
        }
    
        public void setParent(DemoObject parent) {
            this.parent = parent;
        }
    }
    
    @Before
    public void before()        
    {
        QLExpressRunStrategy.setAvoidNullPointer(true);
    }
    @After
    public void after()
    {
        QLExpressRunStrategy.setAvoidNullPointer(false);
    }
    @Test
    public void testNullPoint() throws Exception{
        
        ExpressRunner runner = new ExpressRunner(false,true);
        String[] explist = new String[]{
                "x in(1,2,3)",
                "demo.code",
                "demo.parent.code",
                "demo.parent.getCode()",
                "demo.getParent().getCode()",
                "demo.getParent().getCode() in (1,2,3)",
        };
        for(String exp:explist) {
            IExpressContext<String, Object> context = new DefaultContext<String, Object>();
            System.out.println(exp);
            ((DefaultContext<String, Object>) context).put("demo",new DemoObject());
            Object result = runner.execute(exp, context, null, true, false);
            System.out.println(result);
        }
    }
}
