package com.alibaba.qlexpress4.utils;

import java.io.StringWriter;

public class VarIdUtils {
    
    public static String parseBackTicVarIdText(String backTikVarId) {
        StringWriter sw = new StringWriter();
        boolean hasPrevBackTic = false;
        for (int i = 1; i < backTikVarId.length() - 1; i++) {
            char c = backTikVarId.charAt(i);
            if (c == '`') {
                if (!hasPrevBackTic) {
                    hasPrevBackTic = true;
                }
                else {
                    sw.write(c);
                    hasPrevBackTic = false;
                }
            }
            else if (hasPrevBackTic) {
                throw new IllegalStateException("'`' in varId must be escaped by '``'");
            }
            else {
                sw.write(c);
            }
            
        }
        return sw.toString();
    }
}
