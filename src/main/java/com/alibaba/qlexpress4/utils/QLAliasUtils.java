package com.alibaba.qlexpress4.utils;

import com.alibaba.qlexpress4.annotation.QLAlias;

public class QLAliasUtils {
    
    public static boolean matchQLAlias(String matchName, QLAlias[] qlAliases) {
        for (QLAlias alias : qlAliases) {
            for (int i = 0; i < alias.value().length; i++) {
                if (matchName.equals(alias.value()[i])) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
