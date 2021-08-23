package com.ql.util.express.cfuture;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayPorpertyMixTest {

    @Test
    public void testRun() throws Exception {
        String tests[] = new String[]{
                "request['AAAA'][0]['BBBB'][0]",
                "requestList[0]['AAAA'][0]['BBBB'][0]",
                "abc = NewList(1,2,3); return abc[1]+abc[2]",
                "abc = NewMap('aa':1,'bb':2); return abc['aa'] + abc.get('bb');",
                "abc = [1,2,3]; return abc[1]+abc[2];",
        };


        Map<String,Object> BBBBValue = new HashMap<String,Object>();
        BBBBValue.put("BBBB",new String[]{"0","1"});
        Object[] AAAAValue = new Object[]{BBBBValue};
        Map<String,Object> request = new HashMap<String,Object>();
        request.put("AAAA",AAAAValue);
        List<Object> requestList = new ArrayList<Object>();
        requestList.add(request);

        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();

        context.put("request",request);
        context.put("requestList",requestList);
        for(String exp:tests) {
            Object result = runner.execute(exp, context, null, false,
                    false);
            System.out.println(result);
        }
    }


}
