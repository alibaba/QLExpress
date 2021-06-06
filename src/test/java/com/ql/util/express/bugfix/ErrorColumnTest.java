package com.ql.util.express.bugfix;

import com.ql.util.express.ExpressRunner;
import org.junit.Test;

/**
 * @author bingo 2019-05-01
 */
public class ErrorColumnTest {
    /**
     * Previous error: java.lang.Exception: There are still words that have not completed grammatical matching: the words after 22[if:line=9,col=69]
     * Error after modification: java.lang.Exception: There are still words that have not completed the grammatical match: the words after 22[if:line=9,col=12]
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
