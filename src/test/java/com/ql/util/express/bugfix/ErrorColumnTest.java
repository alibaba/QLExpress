package com.ql.util.express.bugfix;

import com.ql.util.express.ExpressRunner;
import org.junit.Test;

/**
 * @author bingo 2019-05-01
 */
public class ErrorColumnTest {
    /**
     * 之前的错误：java.lang.Exception: 还有单词没有完成语法匹配：22[if:line=9,col=69] 之后的单词
     * 修改后错误：java.lang.Exception: 还有单词没有完成语法匹配：22[if:line=9,col=12] 之后的单词
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        try {
            String expressName = "bugfix/error-column";
            ExpressRunner expressRunner = new ExpressRunner(false, true);
            expressRunner.loadExpress(expressName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
