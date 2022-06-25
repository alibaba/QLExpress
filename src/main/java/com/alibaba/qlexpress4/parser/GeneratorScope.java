package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: DQinYuan
 */
public class GeneratorScope {

    private final GeneratorScope parent;

    private Map<String, List<QLInstruction>> macroInstructions;

    public GeneratorScope(GeneratorScope parent) {
        this.parent = parent;
        this.macroInstructions = Collections.emptyMap();
    }

    public void defineMacro(String name, List<QLInstruction> instructions) {
        if (macroInstructions == Collections.<String, List<QLInstruction>>emptyMap()) {
            // optimize performance
            macroInstructions = new HashMap<>();
        }
        macroInstructions.put(name, instructions);
    }

    public List<QLInstruction> getMacroInstructions(String macroName) {
        List<QLInstruction> qlInstructions = macroInstructions.get(macroName);
        return qlInstructions != null? qlInstructions:
                parent != null? parent.getMacroInstructions(macroName): null;
    }
}
