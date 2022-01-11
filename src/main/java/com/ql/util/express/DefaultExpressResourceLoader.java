package com.ql.util.express;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.ql.util.express.exception.QLException;

public class DefaultExpressResourceLoader implements IExpressResourceLoader {
    @Override
    public String loadExpress(String expressName) throws Exception {
        expressName = expressName.replace('.', '/') + ".ql";
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(expressName);
        if (inputStream == null) {
            throw new QLException("不能找到表达式文件：" + expressName);
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String tmpStr;
        while ((tmpStr = bufferedReader.readLine()) != null) {
            stringBuilder.append(tmpStr).append("\n");
        }
        bufferedReader.close();
        inputStream.close();
        return stringBuilder.toString();
    }
}
