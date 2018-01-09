package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSet;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ErrorListTest {
    
    public boolean isVIP(String nick)
    {
        return nick.contains("vip_");
    }
    public Integer getUserLevel(String nick)
    {
        if(nick.contains("vip_")){
            return 3;
        }
        return 2;
    }
    
    @Test
    public void testErrorList() throws Exception {
        ExpressRunner runner = new ExpressRunner(false, false);
        runner.addFunctionOfServiceMethod("isVIP", new ErrorListTest(),
                "isVIP", new Class[]{String.class},"$1不是vip");
        runner.addFunctionOfServiceMethod("getUserLevel", new ErrorListTest(),
                "getUserLevel", new Class[]{String.class},"");
        runner.addOperatorWithAlias("大于",">","用户等级不够");
        
        runner.addOperatorWithAlias("是否VIP","isVIP","亲爱的$1,你还不是VIP用户");
        testExample(runner,"isVIP('vip_11111')");
        testExample(runner,"isVIP('common_11111')");
        
        testExample(runner,"getUserLevel('vip_11111') 大于 2");
        testExample(runner,"getUserLevel('common_11111') 大于 2");
    }
    
    public void testExample(ExpressRunner runner,String express) throws Exception {
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        List<String> errorList = new ArrayList<String>();
        Object r =  runner.execute(express,context,errorList,false,false,null);
        System.out.println(r);
        for(int i=0;i<errorList.size();i++){
            System.out.println(errorList.get(i));
        }
    }
}
