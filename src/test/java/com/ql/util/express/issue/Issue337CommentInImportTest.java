package com.ql.util.express.issue;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import org.junit.Assert;
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
                + "return true;";
        ExpressRunner runner = new ExpressRunner(false, true);
        DefaultContext<String, Object> context = new DefaultContext<>();
        Object result = runner.execute(express, context, null, false, true);
        Assert.assertTrue((Boolean)result);
    }
}