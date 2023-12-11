package com.ql.util.express.bugfix;

import java.util.HashMap;

import com.ql.util.express.parse.ExpressNode;
import com.ql.util.express.parse.ExpressPackage;
import com.ql.util.express.parse.ExpressParse;
import com.ql.util.express.parse.NodeTypeManager;
import org.junit.Test;

/**
 * @author bingo
 */
public class Issue304Test {
    @Test
    public void test() throws Exception {
        boolean tempBoolean = true;
        ExpressParse expressParse = new ExpressParse(new NodeTypeManager(), null, tempBoolean);
        ExpressNode result = expressParse.parse(new ExpressPackage(null),
            // 87个左括号栈溢出，86个有时会溢出，有时不会
            "(((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((",
            tempBoolean, new HashMap<>());
        System.out.println("result = " + result);
    }
}
