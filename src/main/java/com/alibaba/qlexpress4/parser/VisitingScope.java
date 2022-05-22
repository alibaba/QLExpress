package com.alibaba.qlexpress4.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class VisitingScope {

    enum SymbolType {
        MACRO,
        FUNC,
        INTERNAL_VAR,
        EXTERNAL_VAR,
        EXTERNAL_FUNC
    }

    private final VisitingScope parent;

    private final Map<String, SymbolType> symbolTypeMap;

    public VisitingScope(VisitingScope parent) {
        this.parent = parent;
        this.symbolTypeMap = new HashMap<>();
    }

    public SymbolType getSymbolType(String symbolName) {
        return symbolTypeMap.getOrDefault(symbolName,
                parent == null? null: parent.getSymbolType(symbolName));
    }

    public void put(String symbolName, SymbolType symbolType) {
        symbolTypeMap.put(symbolName, symbolType);
    }

    public boolean localContains(String symbolName) {
        return symbolTypeMap.containsKey(symbolName);
    }

    public boolean globalContains(String symbolName) {
        return symbolTypeMap.containsKey(symbolName) || (parent != null && parent.globalContains(symbolName));
    }

    public Map<String, SymbolType> getSymbolTypeMap() {
        return symbolTypeMap;
    }
}
