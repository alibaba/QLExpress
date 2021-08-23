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

    public static class Node
    {
        private Node[] children;
        private String code;
        private String name;

        public Node[] getChildren() {
            return children;
        }

        public void setChildren(Node[] children) {
            this.children = children;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Node(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }


    @Test
    public void testcheckSyntax() throws Exception {

        ExpressRunner runner = new ExpressRunner(false,true);
        String express[] = new String[]{
                "request['AAAA'][0]['BBBB']",
                "request.AAAA[0]['BBBB']"
        };
        for(String exp:express) {
            runner.checkSyntax(exp);
        }
    }

    @Test
    public void testRun() throws Exception {
        String tests[] = new String[]{
                "request['AAAA'][0]['BBBB'][0]",
                "requestList[0]['AAAA'][0]['BBBB'][0]",
        };


        Map<String,Object> BBBBValue = new HashMap<String,Object>();
        BBBBValue.put("BBBB",new String[]{"0","1"});
        Object[] AAAAValue = new Object[]{BBBBValue};
        Map<String,Object> request = new HashMap<String,Object>();
        request.put("AAAA",AAAAValue);
        List<Object> requestList = new ArrayList<Object>();
        requestList.add(request);

        ExpressRunner runner = new ExpressRunner(false,true);
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();

        context.put("request",request);
        context.put("requestList",requestList);
        for(String exp:tests) {
            Object result = runner.execute(exp, context, null, false,
                    true);
            System.out.println(result);
        }
    }


}
