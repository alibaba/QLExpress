package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;

import java.util.Collections;
import java.util.List;

/**
 * Result of AST node generation during instruction generation.
 * <p>
 * Contains the generated instructions and metadata about the result.
 *
 * @author QLExpress Team
 */
public class GenerationResult {
    
    private final List<QLInstruction> instructions;
    
    private final boolean expressionValue;
    
    private final int stackEffect;
    
    /**
     * Creates a generation result.
     *
     * @param instructions    the generated instructions
     * @param expressionValue whether this result represents an expression value
     * @param stackEffect     the net stack effect (pushes - pops)
     */
    public GenerationResult(List<QLInstruction> instructions, boolean expressionValue, int stackEffect) {
        this.instructions = instructions != null ? instructions : Collections.emptyList();
        this.expressionValue = expressionValue;
        this.stackEffect = stackEffect;
    }
    
    /**
     * Returns the generated instructions.
     */
    public List<QLInstruction> getInstructions() {
        return instructions;
    }
    
    /**
     * Returns whether this result represents an expression value.
     * Expression values may need to be popped when used as statements.
     */
    public boolean isExpressionValue() {
        return expressionValue;
    }
    
    /**
     * Returns the net stack effect (number of values pushed minus popped).
     */
    public int getStackEffect() {
        return stackEffect;
    }
}
