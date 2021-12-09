package com.ql.util.express.cfuture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import org.junit.Assert;
import org.junit.Test;

public class ArrayPorpertyMixTest {

    @Test
    public void testRun() throws Exception {
        String tests[] = new String[] {
            "request['AAAA'][0]['BBBB'][0]", "0",
            "requestList[0]['AAAA'][0]['BBBB'][0]", "0",
            "requestList[0]['AAAA'][0]['BBBB'][0]='10';return requestList[0]['AAAA'][0]['BBBB'][0];", "10",
            "abc = NewList(1,2,3); return abc[1]+abc[2]", "5",
            "abc = NewList(1,2,3); abc[1]=0;return abc[1]+abc[2]", "3",
            "abc = NewMap('aa':1,'bb':2); return abc['aa'] + abc.get('bb');", "3",
            "abc = NewMap('aa':1,'bb':2); abc['aa']='aa';return abc['aa'] + abc.get('bb');", "aa2",
            "abc = [1,2,3]; return abc[1]+abc[2];", "5",
            "abc = [1,2,3]; abc[1]=0;return abc[1]+abc[2];", "3"
        };

        Map<String, Object> BBBBValue = new HashMap<>();
        BBBBValue.put("BBBB", new String[] {"0", "1"});
        Object[] AAAAValue = new Object[] {BBBBValue};
        Map<String, Object> request = new HashMap<>();
        request.put("AAAA", AAAAValue);
        List<Object> requestList = new ArrayList<>();
        requestList.add(request);

        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String, Object> context = new DefaultContext<>();

        context.put("request", request);
        context.put("requestList", requestList);
        for (int i = 0; i < tests.length; i += 2) {
            Object result = runner.execute(tests[i], context, null, false,
                false);
            System.out.println(result);
            Assert.assertEquals(result.toString(), tests[i + 1]);
        }
    }

}
