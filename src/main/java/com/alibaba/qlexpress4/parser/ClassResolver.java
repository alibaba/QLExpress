package com.alibaba.qlexpress4.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClassResolver {

    private final Map<String, Class<?>> fixedImport = new HashMap<>();

    private final List<String> prefixesImport;

    private final ClassLoader classLoader;

    public ClassResolver(ClassLoader classLoader, List<String> defaultPrefixImport) {
        this.classLoader = classLoader;
        this.prefixesImport = new ArrayList<>(defaultPrefixImport);
    }

    public boolean fixedImport(String importName, String qualifiedName) {
        try {
            fixedImport.put(importName, Class.forName(qualifiedName));
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public void prefixImport(String prefix) {
        prefixesImport.add(prefix);
    }

    public Class<?> loadClass(String classSimpleName) {
        Class<?> fixedImportCls = fixedImport.get(classSimpleName);
        if (fixedImportCls != null) {
            return fixedImportCls;
        }

        for (String prefix : prefixesImport) {
            try {
                return Class.forName(prefix + "." + classSimpleName, false, classLoader);
            } catch (ClassNotFoundException e) {
            }
        }

        // not found cls
        return null;
    }

}
