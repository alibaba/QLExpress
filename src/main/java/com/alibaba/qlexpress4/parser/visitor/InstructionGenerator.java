package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.PureErrReporter;
import com.alibaba.qlexpress4.parser.ast.*;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;
import com.alibaba.qlexpress4.runtime.instruction.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        // TODO: Implement in US-019
        throw new UnsupportedOperationException("return statement generation not yet implemented");
    }

    @Override
    public GenerationResult visit(BreakNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-019
        throw new UnsupportedOperationException("break statement generation not yet implemented");
    }

    @Override
    public GenerationResult visit(ContinueNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-019
        throw new UnsupportedOperationException("continue statement generation not yet implemented");
    }

    @Override
    public GenerationResult visit(ThrowNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-019
        throw new UnsupportedOperationException("throw statement generation not yet implemented");
    }

    @Override
    public GenerationResult visit(VariableDeclarationNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-019
        throw new UnsupportedOperationException("variable declaration generation not yet implemented");
    }

    @Override
    public GenerationResult visit(AssignmentNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-019
        throw new UnsupportedOperationException("assignment generation not yet implemented");
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
        // TODO: Implement in US-018
        throw new UnsupportedOperationException("lambda expression generation not yet implemented");
    }

    @Override
    public GenerationResult visit(MethodCallNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-018
        throw new UnsupportedOperationException("method call generation not yet implemented");
    }

    @Override
    public GenerationResult visit(ConstructorCallNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-018
        throw new UnsupportedOperationException("constructor call generation not yet implemented");
    }

    @Override
    public GenerationResult visit(CastNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-018
        throw new UnsupportedOperationException("cast expression generation not yet implemented");
    }

    @Override
    public GenerationResult visit(ArrayAccessNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-018
        throw new UnsupportedOperationException("array access generation not yet implemented");
    }

    @Override
    public GenerationResult visit(ArrayLiteralNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-018
        throw new UnsupportedOperationException("array literal generation not yet implemented");
    }

    @Override
    public GenerationResult visit(MapLiteralNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-018
        throw new UnsupportedOperationException("map literal generation not yet implemented");
    }

    @Override
    public GenerationResult visit(ListLiteralNode node, GenerationContext context) throws Exception {
        // TODO: Implement in US-018
        throw new UnsupportedOperationException("list literal generation not yet implemented");
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
