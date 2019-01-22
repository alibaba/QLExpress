package com.ql.util.express.bugfix;

import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by tianqiao on 18/2/26.
 */
public class CheckySyntaxTest {
    
    
    @Test
    public void testCheckySyntax0() throws Exception{
        ExpressRunner runner = new ExpressRunner(false,true);
        String[] expList = new String[]{
                "import java.text.SimpleDateFormat;new SimpleDateFormat()",
                "a = new java.text.SimpleDateFormat()",
        };
        
        for(String exp :expList) {
            Assert.assertTrue(runner.checkSyntax(exp));
        }
        for(String exp :expList) {
            ArrayList<String> mockClasses = new ArrayList<String>();
            Assert.assertTrue(runner.checkSyntax(exp,true,mockClasses));
            System.out.println("未识别的java类列表:"+mockClasses);
        }
    }
    
    @Test
    public void testCheckySyntax() throws Exception{
        ExpressRunner runner = new ExpressRunner(false,true);
        String[] expList = new String[]{
                "import com.taobao.ABC;a = new ABC();",
                "import com.taobao.*;a = new ABC();",
                "import com.taobao.ABC;a = new com.taobao.ABC();",
                "a = new com.taobao.ABC();if('new'.equals(op)){System.out.println('ok');}"
        };
        
        for(String exp :expList) {
            ArrayList<String> mockClasses = new ArrayList<String>();
            Assert.assertTrue(runner.checkSyntax(exp,true,mockClasses));
            System.out.println("未识别的java类列表:"+mockClasses);
        }
    }
    
}
