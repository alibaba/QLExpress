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

    public GeneratorScope(GeneratorScope parent, String name, Map<String, MacroDefine> macroDefineMap) {
        this.parent = parent;
        this.name = name;
        this.macroDefineMap = macroDefineMap;
    }

    public GeneratorScope(String name, GeneratorScope parent) {
        this.parent = parent;
        this.name = name;
        this.macroDefineMap = new HashMap<>();
    }

    /**
     * @param name macro name
     * @param macroDefine macro definition
     * @return true if define macro successfully. fail if macro name already exists
     */
    public boolean defineMacroIfAbsent(String name, MacroDefine macroDefine) {
        return macroDefineMap.putIfAbsent(name, macroDefine) == null;
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
