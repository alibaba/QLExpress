package com.alibaba.qlexpress4.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class GeneratorScopeV2 {

    public enum ScopeType {
        BLOCK, FUNCTION, MACRO, LOOP
    }

    private final GeneratorScopeV2 parent;

    private final ScopeType scopeType;

    private Map<String, MacroDefineV2> macroInstructions;

    public GeneratorScopeV2(GeneratorScopeV2 parent, ScopeType scopeType) {
        this.parent = parent;
        this.scopeType = scopeType;
        this.macroInstructions = Collections.emptyMap();
    }

    public void defineMacro(String name, MacroDefineV2 macroDefine) {
        if (macroInstructions == Collections.<String, MacroDefineV2>emptyMap()) {
            // create only when need
            macroInstructions = new HashMap<>();
        }
        macroInstructions.put(name, macroDefine);
    }

    public MacroDefineV2 getMacroDefine(String macroName) {
        MacroDefineV2 macroDefine = macroInstructions.get(macroName);
        return macroDefine != null? macroDefine:
                parent != null? parent.getMacroDefine(macroName): null;
    }

    public ScopeType getScopeType() {
        return scopeType;
    }
}
