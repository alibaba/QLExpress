package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.PureErrReporter;
import com.alibaba.qlexpress4.parser.ast.*;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;
import com.alibaba.qlexpress4.runtime.instruction.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * InstructionGenerator generates QVM instructions from AST nodes.
 * <p>
 * This visitor traverses the AST and produces a list of QLInstruction objects
 * that can be executed by the QLExpress virtual machine.
 * <p>
 * Context class holds the generator state during traversal.
 *
 * @author QLExpress Team
 */
public class InstructionGenerator implements ASTVisitor<GenerationResult, GenerationContext> {

    private final OperatorManager operatorManager;

    public InstructionGenerator(OperatorManager operatorManager) {
        this.operatorManager = operatorManager;
    }

    public InstructionGenerator() {
        this(new OperatorManager());
    }

    // ==================== Statement Visitors ====================

    @Override
    public GenerationResult visit(BlockNode node, GenerationContext context) throws Exception {
        GenerationContext blockContext = context.createChildContext();
        List<QLInstruction> instructions = new ArrayList<>();

        for (StatementNode statement : node.getStatements()) {
            GenerationResult result = ((ASTNode) statement).accept(this, blockContext);
            instructions.addAll(result.getInstructions());

            // If the statement is an expression, pop its result unless it's the last statement
            if (result.isExpressionValue()) {
                instructions.add(new PopInstruction(PureErrReporter.INSTANCE));
            }
        }

        return new GenerationResult(instructions, false, 0);
    }

    @Override
    public GenerationResult visit(IfNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-019
        throw new UnsupportedOperationException("if-else statement generation not yet implemented");
    }

    @Override
    public GenerationResult visit(WhileNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-019
        throw new UnsupportedOperationException("while loop generation not yet implemented");
    }

    @Override
    public GenerationResult visit(ForNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-019
        throw new UnsupportedOperationException("for loop generation not yet implemented");
    }

    @Override
    public GenerationResult visit(SwitchNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-019
        throw new UnsupportedOperationException("switch statement generation not yet implemented");
    }

    @Override
    public GenerationResult visit(TryCatchNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-019
        throw new UnsupportedOperationException("try-catch-finally generation not yet implemented");
    }

    @Override
    public GenerationResult visit(ReturnNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate return value expression
        if (node.getValue() != null) {
            GenerationResult valueResult = ((ASTNode) node.getValue()).accept(this, context);
            instructions.addAll(valueResult.getInstructions());
        } else {
            // No return value, push null
            instructions.add(new ConstInstruction(PureErrReporter.INSTANCE, null, null));
        }

        // Add return instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        instructions.add(new ReturnInstruction(errorReporter, QResult.ResultType.RETURN, null));

        return new GenerationResult(instructions, false, 0);
    }

    @Override
    public GenerationResult visit(BreakNode node, GenerationContext context) throws Exception {
        ErrorReporter errorReporter = createErrorReporter(node);
        BreakContinueInstruction instruction = new BreakContinueInstruction(errorReporter, QResult.LOOP_BREAK_RESULT);
        return new GenerationResult(Collections.singletonList(instruction), false, 0);
    }

    @Override
    public GenerationResult visit(ContinueNode node, GenerationContext context) throws Exception {
        ErrorReporter errorReporter = createErrorReporter(node);
        BreakContinueInstruction instruction = new BreakContinueInstruction(errorReporter, QResult.LOOP_CONTINUE_RESULT);
        return new GenerationResult(Collections.singletonList(instruction), false, 0);
    }

    @Override
    public GenerationResult visit(ThrowNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate exception expression
        GenerationResult exceptionResult = ((ASTNode) node.getException()).accept(this, context);
        instructions.addAll(exceptionResult.getInstructions());

        // Add throw instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        instructions.add(new ThrowInstruction(errorReporter));

        return new GenerationResult(instructions, false, 0);
    }

    @Override
    public GenerationResult visit(VariableDeclarationNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate initial value expression
        ExpressionNode initialValue = node.getInitialValue();
        if (initialValue != null) {
            GenerationResult valueResult = ((ASTNode) initialValue).accept(this, context);
            instructions.addAll(valueResult.getInstructions());
        } else {
            // No initial value, push null
            instructions.add(new ConstInstruction(PureErrReporter.INSTANCE, null, null));
        }

        // Add define local instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        // Note: For now, use Object.class as the type - in real implementation, resolve typeName
        Class<?> varClass = Object.class;  // TODO: Resolve actual type from node.getTypeName()
        DefineLocalInstruction instruction = new DefineLocalInstruction(errorReporter, node.getVariableName(), varClass);
        instructions.add(instruction);

        return new GenerationResult(instructions, false, 0);
    }

    @Override
    public GenerationResult visit(AssignmentNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate value expression
        GenerationResult valueResult = ((ASTNode) node.getValue()).accept(this, context);
        instructions.addAll(valueResult.getInstructions());

        // For simple assignment (=), we need to store the value
        // For compound assignment (+=, -=, etc.), we need to load the target, apply operator, then store
        if ("=".equals(node.getOperator())) {
            // Simple assignment - just store the value (no separate instruction, value is on stack)
            // The target (identifier) needs to be handled by Load/Define
            // For now, we'll use LoadInstruction which creates the symbol if it doesn't exist
        } else {
            // Compound assignment - load target first, then apply operator
            GenerationResult targetResult = ((ASTNode) node.getTarget()).accept(this, context);
            // Insert target instructions before value instructions
            instructions.addAll(0, targetResult.getInstructions());

            // Apply the compound operator
            BinaryOperator operator = operatorManager.getBinaryOperator(node.getOperator());
            if (operator != null) {
                ErrorReporter errorReporter = createErrorReporter(node);
                instructions.add(new OperatorInstruction(errorReporter, operator, null));
            }
        }

        return new GenerationResult(instructions, true, 1);  // Assignment is an expression
    }

    @Override
    public GenerationResult visit(TypeDeclarationNode node, GenerationContext context) throws Exception {
        // TODO: Implement in a future story
        throw new UnsupportedOperationException("type declaration generation not yet implemented");
    }

    @Override
    public GenerationResult visit(ImportNode node, GenerationContext context) throws Exception {
        // TODO: Implement in a future story
        throw new UnsupportedOperationException("import generation not yet implemented");
    }

    @Override
    public GenerationResult visit(FunctionDefinitionNode node, GenerationContext context) throws Exception {
        // TODO: Implement in a future story
        throw new UnsupportedOperationException("function definition generation not yet implemented");
    }

    @Override
    public GenerationResult visit(MacroDefinitionNode node, GenerationContext context) throws Exception {
        // TODO: Implement in a future story
        throw new UnsupportedOperationException("macro definition generation not yet implemented");
    }

    // ==================== Expression Visitors ====================

    @Override
    public GenerationResult visit(LiteralNode node, GenerationContext context) throws Exception {
        ErrorReporter errorReporter = createErrorReporter(node);
        ConstInstruction instruction = new ConstInstruction(errorReporter, node.getValue(), null);
        return new GenerationResult(Collections.singletonList(instruction), true, 1);
    }

    @Override
    public GenerationResult visit(IdentifierNode node, GenerationContext context) throws Exception {
        ErrorReporter errorReporter = createErrorReporter(node);
        LoadInstruction instruction = new LoadInstruction(errorReporter, node.getName(), null);
        return new GenerationResult(Collections.singletonList(instruction), true, 1);
    }

    @Override
    public GenerationResult visit(BinaryOpNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate left operand
        GenerationResult leftResult = ((ASTNode) node.getLeft()).accept(this, context);
        instructions.addAll(leftResult.getInstructions());

        // Generate right operand
        GenerationResult rightResult = ((ASTNode) node.getRight()).accept(this, context);
        instructions.addAll(rightResult.getInstructions());

        // Generate operator instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        BinaryOperator operator = operatorManager.getBinaryOperator(node.getOperator());
        if (operator == null) {
            throw new UnsupportedOperationException("Unknown binary operator: " + node.getOperator());
        }
        OperatorInstruction instruction = new OperatorInstruction(errorReporter, operator, null);
        instructions.add(instruction);

        return new GenerationResult(instructions, true, 1);
    }

    @Override
    public GenerationResult visit(UnaryOpNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate operand
        GenerationResult operandResult = ((ASTNode) node.getOperand()).accept(this, context);
        instructions.addAll(operandResult.getInstructions());

        // Generate unary operator instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        UnaryOperator operator;
        if (node.isPrefix()) {
            operator = operatorManager.getPrefixUnaryOperator(node.getOperator());
        } else {
            operator = operatorManager.getSuffixUnaryOperator(node.getOperator());
        }

        if (operator == null) {
            throw new UnsupportedOperationException("Unknown unary operator: " + node.getOperator());
        }

        UnaryInstruction instruction = new UnaryInstruction(errorReporter, operator, null);
        instructions.add(instruction);

        return new GenerationResult(instructions, true, 1);
    }

    @Override
    public GenerationResult visit(TernaryNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate condition
        GenerationResult conditionResult = ((ASTNode) node.getCondition()).accept(this, context);
        instructions.addAll(conditionResult.getInstructions());

        ErrorReporter errorReporter = createErrorReporter(node);

        // Jump to else if condition is false
        JumpIfInstruction jumpIf = new JumpIfInstruction(errorReporter, false, -1, null);
        instructions.add(jumpIf);

        // Generate then expression
        GenerationResult thenResult = ((ASTNode) node.getThenExpr()).accept(this, context);
        instructions.addAll(thenResult.getInstructions());

        // Jump to end after then
        JumpInstruction jump = new JumpInstruction(errorReporter, -1);
        instructions.add(jump);

        // Set jumpIf target (start of else)
        jumpIf.setPosition(instructions.size());

        // Generate else expression
        GenerationResult elseResult = ((ASTNode) node.getElseExpr()).accept(this, context);
        instructions.addAll(elseResult.getInstructions());

        // Set jump target (end of ternary)
        jump.setPosition(instructions.size());

        return new GenerationResult(instructions, true, 1);
    }

    @Override
    public GenerationResult visit(LambdaNode node, GenerationContext context) throws Exception {
        // Generate instructions for lambda body
        GenerationContext lambdaContext = context.createChildContext();
        List<QLInstruction> bodyInstructions = new ArrayList<>();

        // Handle lambda body (could be ExpressionNode or BlockNode)
        if (node.getBody() instanceof ExpressionNode) {
            GenerationResult bodyResult = ((ASTNode) node.getBody()).accept(this, lambdaContext);
            bodyInstructions.addAll(bodyResult.getInstructions());
            // Add return for expression body
            bodyInstructions.add(new ReturnInstruction(PureErrReporter.INSTANCE, QResult.ResultType.CONTINUE, null));
        } else if (node.getBody() instanceof BlockNode) {
            GenerationResult bodyResult = ((ASTNode) node.getBody()).accept(this, lambdaContext);
            bodyInstructions.addAll(bodyResult.getInstructions());
        }

        // Convert parameters to QLambdaDefinitionInner.Param format
        List<QLambdaDefinitionInner.Param> params = node.getParameters().stream()
                .map(p -> new QLambdaDefinitionInner.Param(p.getParameterName(), Object.class))
                .collect(Collectors.toList());

        // Create lambda definition
        String lambdaName = "LAMBDA_" + System.nanoTime();
        QLambdaDefinitionInner lambdaDefinition = new QLambdaDefinitionInner(
                lambdaName, bodyInstructions, params, 0);

        // Load the lambda
        ErrorReporter errorReporter = createErrorReporter(node);
        LoadLambdaInstruction instruction = new LoadLambdaInstruction(errorReporter, lambdaDefinition);

        return new GenerationResult(Collections.singletonList(instruction), true, 1);
    }

    @Override
    public GenerationResult visit(MethodCallNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate target expression (if not null - for static calls, target is null)
        if (node.getTarget() != null) {
            GenerationResult targetResult = ((ASTNode) node.getTarget()).accept(this, context);
            instructions.addAll(targetResult.getInstructions());
        }

        // Generate arguments
        for (ExpressionNode arg : node.getArguments()) {
            GenerationResult argResult = ((ASTNode) arg).accept(this, context);
            instructions.addAll(argResult.getInstructions());
        }

        // Generate method invoke instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        MethodInvokeInstruction instruction = new MethodInvokeInstruction(
                errorReporter,
                node.getMethodName(),
                node.getArguments().size(),
                false);  // optional = false for now
        instructions.add(instruction);

        return new GenerationResult(instructions, true, 1);
    }

    @Override
    public GenerationResult visit(ConstructorCallNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate arguments
        for (ExpressionNode arg : node.getArguments()) {
            GenerationResult argResult = ((ASTNode) arg).accept(this, context);
            instructions.addAll(argResult.getInstructions());
        }

        // Generate constructor call instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        // Note: We need to resolve the type name to a Class<?> object
        // For now, we'll use Object.class as a placeholder - in a real implementation,
        // this would use an ImportManager to resolve the type
        Class<?> typeClass = Object.class;  // TODO: Resolve actual type
        NewInstanceInstruction instruction = new NewInstanceInstruction(
                errorReporter,
                typeClass,
                node.getArguments().size());
        instructions.add(instruction);

        return new GenerationResult(instructions, true, 1);
    }

    @Override
    public GenerationResult visit(CastNode node, GenerationContext context) throws Exception {
        // TODO: Implement in a future story
        throw new UnsupportedOperationException("cast expression generation not yet implemented");
    }

    @Override
    public GenerationResult visit(ArrayAccessNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate array expression
        GenerationResult arrayResult = ((ASTNode) node.getArray()).accept(this, context);
        instructions.addAll(arrayResult.getInstructions());

        // Generate index expression
        GenerationResult indexResult = ((ASTNode) node.getIndex()).accept(this, context);
        instructions.addAll(indexResult.getInstructions());

        // Generate index instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        IndexInstruction instruction = new IndexInstruction(errorReporter);
        instructions.add(instruction);

        return new GenerationResult(instructions, true, 1);
    }

    @Override
    public GenerationResult visit(ArrayLiteralNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate element expressions
        for (ExpressionNode element : node.getElements()) {
            GenerationResult elementResult = ((ASTNode) element).accept(this, context);
            instructions.addAll(elementResult.getInstructions());
        }

        // Generate new array instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        // Note: We need to determine the element type - for now, use Object.class
        Class<?> elementClass = Object.class;  // TODO: Infer actual element type
        NewArrayInstruction instruction = new NewArrayInstruction(
                errorReporter,
                elementClass,
                node.getElements().size());
        instructions.add(instruction);

        return new GenerationResult(instructions, true, 1);
    }

    @Override
    public GenerationResult visit(MapLiteralNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate value expressions (keys must be string literals or constant expressions)
        List<String> keys = new ArrayList<>();
        for (MapEntryNode entry : node.getEntries()) {
            // Key must be a constant (string literal)
            if (entry.getKey() instanceof LiteralNode) {
                Object keyValue = ((LiteralNode) entry.getKey()).getValue();
                if (keyValue instanceof String) {
                    keys.add((String) keyValue);
                } else {
                    throw new UnsupportedOperationException("Map keys must be string literals");
                }
            } else {
                throw new UnsupportedOperationException("Map keys must be string literals");
            }

            // Generate value expression
            GenerationResult valueResult = ((ASTNode) entry.getValue()).accept(this, context);
            instructions.addAll(valueResult.getInstructions());
        }

        // Generate new map instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        NewMapInstruction instruction = new NewMapInstruction(errorReporter, keys);
        instructions.add(instruction);

        return new GenerationResult(instructions, true, 1);
    }

    @Override
    public GenerationResult visit(ListLiteralNode node, GenerationContext context) throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();

        // Generate element expressions
        for (ExpressionNode element : node.getElements()) {
            GenerationResult elementResult = ((ASTNode) element).accept(this, context);
            instructions.addAll(elementResult.getInstructions());
        }

        // Generate new list instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        NewListInstruction instruction = new NewListInstruction(
                errorReporter,
                node.getElements().size());
        instructions.add(instruction);

        return new GenerationResult(instructions, true, 1);
    }

    @Override
    public GenerationResult visit(InstanceOfNode node, GenerationContext context) throws Exception {
        // TODO: Implement in a future story (instanceof is not in the grammar)
        throw new UnsupportedOperationException("instanceof generation not yet implemented");
    }

    @Override
    public GenerationResult visit(TypeNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-018 or future
        throw new UnsupportedOperationException("type node generation not yet implemented");
    }

    // ==================== Other Visitors ====================

    @Override
    public GenerationResult visit(ProgramNode node, GenerationContext context) throws Exception {
        GenerationContext programContext = context.createChildContext();
        List<QLInstruction> instructions = new ArrayList<>();

        for (StatementNode statement : node.getStatements()) {
            GenerationResult result = ((ASTNode) statement).accept(this, programContext);
            instructions.addAll(result.getInstructions());

            // If the statement is an expression, pop its result unless it's the last statement
            if (result.isExpressionValue()) {
                instructions.add(new PopInstruction(PureErrReporter.INSTANCE));
            }
        }

        return new GenerationResult(instructions, false, 0);
    }

    // ==================== Helper Methods ====================

    private ErrorReporter createErrorReporter(ASTNode node) {
        return PureErrReporter.INSTANCE;
    }
}
