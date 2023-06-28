package com.alibaba.qlexpress4.aparser;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class GeneratorScope {

    private final GeneratorScope parent;

    private final String name;

    private final Map<String, MacroDefine> macroDefineMap;

    public GeneratorScope(String name, GeneratorScope parent) {
        this.parent = parent;
        this.name = name;
        this.macroDefineMap = new HashMap<>();
    }

    public void defineMacro(String name, MacroDefine macroDefine) {
        macroDefineMap.put(name, macroDefine);
    }

    public MacroDefine getMacroInstructions(String macroName) {
        MacroDefine qlInstructions = macroDefineMap.get(macroName);
        return qlInstructions != null? qlInstructions:
                parent != null? parent.getMacroInstructions(macroName): null;
    }

    public String getName() {
        return name;
    }
}
