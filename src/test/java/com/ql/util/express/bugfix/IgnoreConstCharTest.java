package com.ql.util.express.bugfix;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

/**
 * Created by tianqiao on 18/1/29.
 */
public class IgnoreConstCharTest {
    
    @Test
    public void test() throws Exception{
        ExpressRunner runner = new ExpressRunner();
        runner.setIgnoreConstChar(true);
        String exp = "'1'+'2'==\"12\"";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        Object result = runner.execute(exp,context,null,false,true);
        assert ((Boolean) result);
    }
}
