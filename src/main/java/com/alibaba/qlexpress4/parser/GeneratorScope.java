package com.alibaba.qlexpress4.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class GeneratorScope {

    private final GeneratorScope parent;

    private Map<String, MacroDefine> macroInstructions;

    public GeneratorScope(GeneratorScope parent) {
        this.parent = parent;
        this.macroInstructions = Collections.emptyMap();
    }

    public void defineMacro(String name, MacroDefine macroDefine) {
        if (macroInstructions == Collections.<String, MacroDefine>emptyMap()) {
            // optimize performance
            macroInstructions = new HashMap<>();
        }
        macroInstructions.put(name, macroDefine);
    }

    public MacroDefine getMacroInstructions(String macroName) {
        MacroDefine qlInstructions = macroInstructions.get(macroName);
        return qlInstructions != null? qlInstructions:
                parent != null? parent.getMacroInstructions(macroName): null;
    }
}
