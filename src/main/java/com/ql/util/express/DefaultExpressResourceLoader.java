package com.ql.util.express;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.ql.util.express.exception.QLException;

public class DefaultExpressResourceLoader implements IExpressResourceLoader {
    @Override
    public String loadExpress(String expressName) throws Exception {
        expressName = expressName.replace('.', '/') + ".ql";
        InputStream in = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(expressName);
        if (in == null) {
            throw new QLException("不能找到表达式文件：" + expressName);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String tmpStr;
        while ((tmpStr = reader.readLine()) != null) {
            builder.append(tmpStr).append("\n");
        }
        reader.close();
        in.close();
        return builder.toString();
    }
}
