package com.ql.util.express.issue;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * @author gjx
 */
public class Issue337CommentInImportTest {
    @Test
    public void testCommentInImport() throws Exception {
        String express = ""
                + "/** 注释 **/;"
                + "import java.util.ArrayList;"
                + "import com.ql.util.express.issue.Bean337Example;"
                + "abc = new Bean337Example();"
                + "return abc.getValue();";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object result = runner.execute(express, context, null, false, true);
        TestCase.assertEquals("successful", result);
        System.out.println(result);
        System.out.println(context);
    }
}