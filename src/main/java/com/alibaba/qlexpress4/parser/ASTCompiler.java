package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.common.ImportManager;
import com.alibaba.qlexpress4.common.GeneratorScope;
import com.alibaba.qlexpress4.parser.ast.ProgramNode;
import com.alibaba.qlexpress4.parser.ast.ASTNode;
import com.alibaba.qlexpress4.parser.visitor.InstructionGenerator;
import com.alibaba.qlexpress4.parser.visitor.GenerationResult;
import com.alibaba.qlexpress4.parser.visitor.GenerationContext;
import com.alibaba.qlexpress4.parser.visitor.TraceGenerator;
import com.alibaba.qlexpress4.runtime.QLambdaDefinition;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.instruction.QLInstruction;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.runtime.trace.TracePointTree;

import java.util.Collections;
import java.util.List;

/**
 * Utility class for compiling AST nodes to executable QVM definitions.
 * <p>
 * This class provides methods to convert ProgramNode instances to
 * QLambdaDefinition objects that can be executed by the QLExpress runtime.
 * <p>
 * The compilation process consists of:
 * <ol>
 *   <li>Instruction generation: Traverse the AST and generate QVM instructions</li>
 *   <li>Trace generation: Optionally generate execution trace information</li>
 *   <li>Definition creation: Create QLambdaDefinition with instructions and metadata</li>
 * </ol>
 *
 * @author QLExpress Team
 */
public class ASTCompiler {
    
    private ASTCompiler() {
        // Static utility class
    }
    
    /**
     * Compiles a ProgramNode to a QLambdaDefinition.
     * <p>
     * This method generates QVM instructions from the AST and creates
     * a main lambda definition that can be executed.
     *
     * @param programNode     The AST to compile
     * @param operatorManager The operator manager for resolving custom operators
     * @return A QLambdaDefinition containing the compiled instructions
     * @throws Exception if compilation fails
     */
    public static QLambdaDefinition compile(ProgramNode programNode, OperatorManager operatorManager)
        throws Exception {
        return compile(programNode, operatorManager, null);
    }
    
    /**
     * Compiles a ProgramNode to a QLambdaDefinition with ImportManager support.
     * <p>
     * This method generates QVM instructions from the AST and creates
     * a main lambda definition that can be executed.
     *
     * @param programNode     The AST to compile
     * @param operatorManager The operator manager for resolving custom operators
     * @param importManager   The import manager for resolving class references
     * @return A QLambdaDefinition containing the compiled instructions
     * @throws Exception if compilation fails
     */
    public static QLambdaDefinition compile(ProgramNode programNode, OperatorManager operatorManager,
        ImportManager importManager)
        throws Exception {
        return compile(programNode, operatorManager, importManager, null);
    }
    
    /**
     * Compiles a ProgramNode to a QLambdaDefinition with ImportManager and GeneratorScope support.
     * <p>
     * This method generates QVM instructions from the AST and creates
     * a main lambda definition that can be executed.
     *
     * @param programNode     The AST to compile
     * @param operatorManager The operator manager for resolving custom operators
     * @param importManager   The import manager for resolving class references
     * @param generatorScope  The generator scope containing macro definitions
     * @return A QLambdaDefinition containing the compiled instructions
     * @throws Exception if compilation fails
     */
    public static QLambdaDefinition compile(ProgramNode programNode, OperatorManager operatorManager,
        ImportManager importManager, GeneratorScope generatorScope)
        throws Exception {
        return compile(programNode, operatorManager, importManager, generatorScope, null);
    }
    
    /**
     * Compiles a ProgramNode to a QLambdaDefinition with script source support.
     * <p>
     * This method generates QVM instructions from the AST and creates
     * a main lambda definition that can be executed. The script source
     * is used for error reporting.
     *
     * @param programNode     The AST to compile
     * @param operatorManager The operator manager for resolving custom operators
     * @param importManager   The import manager for resolving class references
     * @param generatorScope  The generator scope containing macro definitions
     * @param script          The script source code for error reporting
     * @return A QLambdaDefinition containing the compiled instructions
     * @throws Exception if compilation fails
     */
    public static QLambdaDefinition compile(ProgramNode programNode, OperatorManager operatorManager,
        ImportManager importManager, GeneratorScope generatorScope, String script)
        throws Exception {
        if (programNode == null) {
            throw new IllegalArgumentException("programNode cannot be null");
        }
        if (operatorManager == null) {
            throw new IllegalArgumentException("operatorManager cannot be null");
        }
        
        // Create instruction generator
        InstructionGenerator generator =
            new InstructionGenerator(operatorManager, importManager, generatorScope, script);
        
        // Generate instructions from AST
        GenerationResult result = ((ASTNode)programNode).accept(generator, new GenerationContext());
        
        // Extract instructions
        List<QLInstruction> instructions = result.getInstructions();
        
        // Calculate max stack size
        int maxStackSize = calculateMaxStackSize(instructions);
        
        // Create main lambda definition
        return new QLambdaDefinitionInner("main", instructions, Collections.emptyList(), maxStackSize);
    }
    
    /**
     * Compiles a ProgramNode to a QLambdaDefinition with trace points.
     * <p>
     * This method generates both QVM instructions and execution trace information
     * from the AST.
     *
     * @param programNode     The AST to compile
     * @param operatorManager The operator manager for resolving custom operators
     * @return A CompilationResult containing the lambda definition and trace points
     * @throws Exception if compilation fails
     */
    public static CompilationResult compileWithTrace(ProgramNode programNode, OperatorManager operatorManager)
        throws Exception {
        return compileWithTrace(programNode, operatorManager, null);
    }
    
    /**
     * Compiles a ProgramNode to a QLambdaDefinition with trace points and ImportManager support.
     * <p>
     * This method generates both QVM instructions and execution trace information
     * from the AST.
     *
     * @param programNode     The AST to compile
     * @param operatorManager The operator manager for resolving custom operators
     * @param importManager   The import manager for resolving class references
     * @return A CompilationResult containing the lambda definition and trace points
     * @throws Exception if compilation fails
     */
    public static CompilationResult compileWithTrace(ProgramNode programNode, OperatorManager operatorManager,
        ImportManager importManager)
        throws Exception {
        return compileWithTrace(programNode, operatorManager, importManager, null);
    }
    
    /**
     * Compiles a ProgramNode to a QLambdaDefinition with trace points, ImportManager, and GeneratorScope support.
     * <p>
     * This method generates both QVM instructions and execution trace information
     * from the AST.
     *
     * @param programNode     The AST to compile
     * @param operatorManager The operator manager for resolving custom operators
     * @param importManager   The import manager for resolving class references
     * @param generatorScope  The generator scope containing macro definitions
     * @return A CompilationResult containing the lambda definition and trace points
     * @throws Exception if compilation fails
     */
    public static CompilationResult compileWithTrace(ProgramNode programNode, OperatorManager operatorManager,
        ImportManager importManager, GeneratorScope generatorScope)
        throws Exception {
        return compileWithTrace(programNode, operatorManager, importManager, generatorScope, null);
    }
    
    /**
     * Compiles a ProgramNode to a QLambdaDefinition with trace points and script source support.
     * <p>
     * This method generates both QVM instructions and execution trace information
     * from the AST. The script source is used for error reporting.
     *
     * @param programNode     The AST to compile
     * @param operatorManager The operator manager for resolving custom operators
     * @param importManager   The import manager for resolving class references
     * @param generatorScope  The generator scope containing macro definitions
     * @param script          The script source code for error reporting
     * @return A CompilationResult containing the lambda definition and trace points
     * @throws Exception if compilation fails
     */
    public static CompilationResult compileWithTrace(ProgramNode programNode, OperatorManager operatorManager,
        ImportManager importManager, GeneratorScope generatorScope, String script)
        throws Exception {
        if (programNode == null) {
            throw new IllegalArgumentException("programNode cannot be null");
        }
        if (operatorManager == null) {
            throw new IllegalArgumentException("operatorManager cannot be null");
        }
        
        // Compile to lambda definition
        QLambdaDefinition lambdaDefinition =
            compile(programNode, operatorManager, importManager, generatorScope, script);
        
        // Generate trace points
        List<TracePointTree> tracePoints = generateTracePoints(programNode);
        
        return new CompilationResult(lambdaDefinition, tracePoints);
    }
    
    /**
     * Generates execution trace points from a ProgramNode.
     *
     * @param programNode The AST to generate trace points from
     * @return List of trace point trees
     */
    public static List<TracePointTree> generateTracePoints(ProgramNode programNode) {
        if (programNode == null) {
            throw new IllegalArgumentException("programNode cannot be null");
        }
        
        TraceGenerator traceGenerator = new TraceGenerator();
        try {
            ((ASTNode)programNode).accept(traceGenerator, null);
        }
        catch (Exception e) {
            // Trace generation should not fail compilation
            // Return empty list on error
            return Collections.emptyList();
        }
        
        return traceGenerator.getTracePoints();
    }
    
    /**
     * Calculates the maximum stack size needed for a list of instructions.
     * <p>
     * This is a simple heuristic estimation. For accurate stack size tracking,
     * the stack effect should be tracked during instruction generation.
     *
     * @param instructions The list of instructions
     * @return Estimated maximum stack size
     */
    private static int calculateMaxStackSize(List<QLInstruction> instructions) {
        // Simple heuristic: max of 10 or half the instruction count plus 5
        // This matches the heuristic used in the original QvmInstructionVisitor
        return Math.max(10, instructions.size() / 2 + 5);
    }
    
    /**
     * Compilation result containing both the lambda definition and trace points.
     * <p>
     * This class is used to return both the compiled code and execution
     * trace information from a single compilation pass.
     */
    public static class CompilationResult {
        private final QLambdaDefinition lambdaDefinition;
        
        private final List<TracePointTree> tracePoints;
        
        public CompilationResult(QLambdaDefinition lambdaDefinition, List<TracePointTree> tracePoints) {
            this.lambdaDefinition = lambdaDefinition;
            this.tracePoints = tracePoints;
        }
        
        public QLambdaDefinition getLambdaDefinition() {
            return lambdaDefinition;
        }
        
        public List<TracePointTree> getTracePoints() {
            return tracePoints;
        }
    }
}
