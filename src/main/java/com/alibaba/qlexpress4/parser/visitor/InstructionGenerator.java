package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.aparser.ImportManager;
import com.alibaba.qlexpress4.exception.ErrorReporter;
import com.alibaba.qlexpress4.exception.PureErrReporter;
import com.alibaba.qlexpress4.parser.ast.*;
import com.alibaba.qlexpress4.runtime.QResult;
import com.alibaba.qlexpress4.runtime.QLambdaDefinition;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.runtime.operator.BinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.unary.UnaryOperator;
import com.alibaba.qlexpress4.runtime.instruction.*;
import com.alibaba.qlexpress4.runtime.MetaClass;

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
    
    private final ImportManager importManager;
    
    public InstructionGenerator(OperatorManager operatorManager, ImportManager importManager) {
        this.operatorManager = operatorManager;
        this.importManager = importManager;
    }
    
    public InstructionGenerator(OperatorManager operatorManager) {
        this(operatorManager, null);
    }
    
    public InstructionGenerator() {
        this(new OperatorManager(), null);
    }
    
    // ==================== Statement Visitors ====================
    
    @Override
    public GenerationResult visit(BlockNode node, GenerationContext context)
        throws Exception {
        GenerationContext blockContext = context.createChildContext();
        List<QLInstruction> instructions = new ArrayList<>();
        
        List<StatementNode> statements = node.getStatements();
        int numStatements = statements.size();
        
        for (int i = 0; i < numStatements; i++) {
            StatementNode statement = statements.get(i);
            GenerationResult result = ((ASTNode)statement).accept(this, blockContext);
            instructions.addAll(result.getInstructions());
            
            // If the statement is an expression, pop its result unless it's the last statement
            if (result.isExpressionValue() && i < numStatements - 1) {
                instructions.add(new PopInstruction(PureErrReporter.INSTANCE));
            }
        }
        
        return new GenerationResult(instructions, false, 0);
    }
    
    @Override
    public GenerationResult visit(IfNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate condition expression
        GenerationResult conditionResult = ((ASTNode)node.getCondition()).accept(this, context);
        instructions.addAll(conditionResult.getInstructions());
        
        ErrorReporter errorReporter = createErrorReporter(node);
        
        // Jump to else if condition is false (pops condition from stack)
        JumpIfPopInstruction jumpIf = new JumpIfPopInstruction(errorReporter, false, -1);
        instructions.add(jumpIf);
        
        // Generate then body
        int thenStart = instructions.size();
        Node thenBody = node.getThenBody();
        GenerationResult thenResult;
        if (thenBody instanceof ExpressionNode) {
            thenResult = ((ASTNode)thenBody).accept(this, context);
            instructions.addAll(thenResult.getInstructions());
        }
        else if (thenBody instanceof BlockNode) {
            thenResult = ((ASTNode)thenBody).accept(this, context);
            instructions.addAll(thenResult.getInstructions());
        }
        else if (thenBody instanceof StatementNode) {
            thenResult = ((ASTNode)thenBody).accept(this, context);
            instructions.addAll(thenResult.getInstructions());
        }
        else {
            thenResult = new GenerationResult(Collections.emptyList(), false, 0);
        }
        
        // Jump to end after then
        JumpInstruction jump = new JumpInstruction(errorReporter, -1);
        instructions.add(jump);
        
        // Set jumpIf target (start of else)
        jumpIf.setPosition(instructions.size() - thenStart);
        
        // Generate else body (if present)
        Node elseBody = node.getElseBody();
        if (elseBody != null) {
            if (elseBody instanceof ExpressionNode) {
                GenerationResult elseResult = ((ASTNode)elseBody).accept(this, context);
                instructions.addAll(elseResult.getInstructions());
            }
            else if (elseBody instanceof BlockNode) {
                GenerationResult elseResult = ((ASTNode)elseBody).accept(this, context);
                instructions.addAll(elseResult.getInstructions());
            }
            else if (elseBody instanceof IfNode) {
                // else if - handle recursively
                GenerationResult elseResult = ((ASTNode)elseBody).accept(this, context);
                instructions.addAll(elseResult.getInstructions());
            }
            else if (elseBody instanceof StatementNode) {
                GenerationResult elseResult = ((ASTNode)elseBody).accept(this, context);
                instructions.addAll(elseResult.getInstructions());
            }
        }
        else {
            // No else body - push null as result
            instructions.add(new ConstInstruction(errorReporter, null, null));
        }
        
        // Set jump target (end of if-else)
        jump.setPosition(instructions.size() - thenStart - 1); // -1 because jump was at thenStart position
        
        return new GenerationResult(instructions, false, 0);
    }
    
    @Override
    public GenerationResult visit(WhileNode node, GenerationContext context)
        throws Exception {
        ErrorReporter errorReporter = createErrorReporter(node);
        
        // Generate condition lambda
        GenerationContext conditionContext = context.createChildContext();
        List<QLInstruction> conditionInstructions = new ArrayList<>();
        GenerationResult conditionResult = ((ASTNode)node.getCondition()).accept(this, conditionContext);
        conditionInstructions.addAll(conditionResult.getInstructions());
        // Add return instruction to return condition value
        conditionInstructions.add(new ReturnInstruction(PureErrReporter.INSTANCE, QResult.ResultType.CONTINUE, null));
        QLambdaDefinitionInner conditionLambda = new QLambdaDefinitionInner("while_condition_" + System.nanoTime(),
            conditionInstructions, Collections.emptyList(), calculateMaxStack(conditionInstructions));
        
        // Generate body lambda
        GenerationContext bodyContext = context.createChildContext();
        List<QLInstruction> bodyInstructions = new ArrayList<>();
        if (node.getBody() != null) {
            GenerationResult bodyResult = ((ASTNode)node.getBody()).accept(this, bodyContext);
            bodyInstructions.addAll(bodyResult.getInstructions());
        }
        QLambdaDefinitionInner bodyLambda = new QLambdaDefinitionInner("while_body_" + System.nanoTime(),
            bodyInstructions, Collections.emptyList(), calculateMaxStack(bodyInstructions));
        
        // Calculate max stack size
        int maxStackSize = Math.max(conditionLambda.getMaxStackSize(), bodyLambda.getMaxStackSize());
        
        // Create while instruction
        WhileInstruction instruction = new WhileInstruction(errorReporter, conditionLambda, bodyLambda, maxStackSize);
        
        return new GenerationResult(Collections.singletonList(instruction), false, 0);
    }
    
    @Override
    public GenerationResult visit(ForNode node, GenerationContext context)
        throws Exception {
        ErrorReporter errorReporter = createErrorReporter(node);
        
        // Check if this is a for-each loop
        // For-each loop has: init (VariableDeclarationNode with iterable) and NO condition, NO update
        Node init = node.getInit();
        if (init instanceof VariableDeclarationNode && node.getCondition() == null && node.getUpdate() == null) {
            VariableDeclarationNode varDecl = (VariableDeclarationNode)init;
            // For-each loop: the initial value is the iterable expression
            if (varDecl.getInitialValue() != null) {
                return generateForEachInstruction(node, varDecl, context);
            }
        }
        
        // Traditional for loop
        return generateTraditionalForInstruction(node, context);
    }
    
    private GenerationResult generateForEachInstruction(ForNode node, VariableDeclarationNode varDecl,
        GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate iterable expression (pushes the iterable on the stack)
        ExpressionNode iterableExpr = varDecl.getInitialValue();
        GenerationResult iterableResult = ((ASTNode)iterableExpr).accept(this, context);
        instructions.addAll(iterableResult.getInstructions());
        
        // Generate body lambda
        GenerationContext bodyContext = context.createChildContext();
        List<QLInstruction> bodyInstructions = new ArrayList<>();
        
        // Add variable declaration at the start of the body
        // The loop variable is passed as a parameter to the lambda
        String varName = varDecl.getVariableName();
        Class<?> varClass = Object.class; // TODO: Resolve actual type from varDecl.getTypeName()
        
        // Generate body statements
        if (node.getBody() != null) {
            GenerationResult bodyResult = ((ASTNode)node.getBody()).accept(this, bodyContext);
            bodyInstructions.addAll(bodyResult.getInstructions());
        }
        
        ErrorReporter errorReporter = createErrorReporter(node);
        QLambdaDefinitionInner.Param param = new QLambdaDefinitionInner.Param(varName, varClass);
        QLambdaDefinitionInner bodyLambda = new QLambdaDefinitionInner("foreach_body_" + System.nanoTime(),
            bodyInstructions, Collections.singletonList(param), calculateMaxStack(bodyInstructions));
        
        // Create for-each instruction
        ForEachInstruction instruction = new ForEachInstruction(errorReporter, bodyLambda, varClass, errorReporter);
        instructions.add(instruction);
        
        return new GenerationResult(instructions, false, 0);
    }
    
    private GenerationResult generateTraditionalForInstruction(ForNode node, GenerationContext context)
        throws Exception {
        ErrorReporter errorReporter = createErrorReporter(node);
        
        // Generate init lambda (if present)
        QLambdaDefinitionInner initLambda = null;
        Node init = node.getInit();
        if (init != null) {
            GenerationContext initContext = context.createChildContext();
            List<QLInstruction> initInstructions = new ArrayList<>();
            GenerationResult initResult = ((ASTNode)init).accept(this, initContext);
            initInstructions.addAll(initResult.getInstructions());
            initInstructions.add(new ReturnInstruction(PureErrReporter.INSTANCE, QResult.ResultType.CONTINUE, null));
            initLambda = new QLambdaDefinitionInner("for_init_" + System.nanoTime(), initInstructions,
                Collections.emptyList(), calculateMaxStack(initInstructions));
        }
        
        // Generate condition lambda (if present)
        QLambdaDefinitionInner conditionLambda = null;
        ExpressionNode condition = node.getCondition();
        if (condition != null) {
            GenerationContext conditionContext = context.createChildContext();
            List<QLInstruction> conditionInstructions = new ArrayList<>();
            GenerationResult conditionResult = ((ASTNode)condition).accept(this, conditionContext);
            conditionInstructions.addAll(conditionResult.getInstructions());
            conditionInstructions
                .add(new ReturnInstruction(PureErrReporter.INSTANCE, QResult.ResultType.CONTINUE, null));
            conditionLambda = new QLambdaDefinitionInner("for_condition_" + System.nanoTime(), conditionInstructions,
                Collections.emptyList(), calculateMaxStack(conditionInstructions));
        }
        
        // Generate update lambda (if present)
        QLambdaDefinitionInner updateLambda = null;
        ExpressionNode update = node.getUpdate();
        if (update != null) {
            GenerationContext updateContext = context.createChildContext();
            List<QLInstruction> updateInstructions = new ArrayList<>();
            GenerationResult updateResult = ((ASTNode)update).accept(this, updateContext);
            updateInstructions.addAll(updateResult.getInstructions());
            updateInstructions.add(new ReturnInstruction(PureErrReporter.INSTANCE, QResult.ResultType.CONTINUE, null));
            updateLambda = new QLambdaDefinitionInner("for_update_" + System.nanoTime(), updateInstructions,
                Collections.emptyList(), calculateMaxStack(updateInstructions));
        }
        
        // Generate body lambda
        GenerationContext bodyContext = context.createChildContext();
        List<QLInstruction> bodyInstructions = new ArrayList<>();
        if (node.getBody() != null) {
            GenerationResult bodyResult = ((ASTNode)node.getBody()).accept(this, bodyContext);
            bodyInstructions.addAll(bodyResult.getInstructions());
        }
        QLambdaDefinitionInner bodyLambda = new QLambdaDefinitionInner("for_body_" + System.nanoTime(),
            bodyInstructions, Collections.emptyList(), calculateMaxStack(bodyInstructions));
        
        // Calculate max stack size
        int initSize = initLambda == null ? 0 : initLambda.getMaxStackSize();
        int conditionSize = conditionLambda == null ? 0 : conditionLambda.getMaxStackSize();
        int updateSize = updateLambda == null ? 0 : updateLambda.getMaxStackSize();
        int maxStackSize =
            Math.max(initSize, Math.max(conditionSize, Math.max(updateSize, bodyLambda.getMaxStackSize())));
        
        // Create for instruction
        ForInstruction instruction = new ForInstruction(errorReporter, initLambda, conditionLambda, errorReporter,
            updateLambda, maxStackSize, bodyLambda);
        
        return new GenerationResult(Collections.singletonList(instruction), false, 0);
    }
    
    @Override
    public GenerationResult visit(SwitchNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        if (node.getCases().isEmpty()) {
            // Empty switch, push null as result
            instructions.add(new ConstInstruction(PureErrReporter.INSTANCE, null, null));
            return new GenerationResult(instructions, false, 0);
        }
        
        ErrorReporter errorReporter = createErrorReporter(node);
        
        // Generate switch value expression and store in a temporary variable
        GenerationResult valueResult = ((ASTNode)node.getValue()).accept(this, context);
        instructions.addAll(valueResult.getInstructions());
        
        String switchVarName = "@switch_" + System.nanoTime();
        instructions.add(new DefineLocalInstruction(errorReporter, switchVarName, Object.class));
        
        // Set up context to track break jump targets for this switch
        context.setProperty("inSwitch", Boolean.TRUE);
        List<JumpInstruction> switchBreakTargets = new ArrayList<>();
        context.setProperty("switchBreakTargets", switchBreakTargets);
        
        // Collect cases and group consecutive cases with empty bodies
        List<SwitchCaseNode> cases = node.getCases();
        List<SwitchCaseNode> groupedCases = new ArrayList<>();
        List<Integer> caseLabelPositions = new ArrayList<>();
        
        for (int i = 0; i < cases.size(); i++) {
            SwitchCaseNode currentCase = cases.get(i);
            List<StatementNode> statements = currentCase.getStatements();
            
            // Check if this case has a body
            boolean hasBody = statements != null && !statements.isEmpty();
            
            if (currentCase.getCondition() != null) {
                // Regular case
                if (hasBody) {
                    // This case has a body - add it as a new group
                    groupedCases.add(currentCase);
                    caseLabelPositions.add(instructions.size());
                }
                else {
                    // Empty case - check if next case has a body
                    if (i + 1 < cases.size()) {
                        SwitchCaseNode nextCase = cases.get(i + 1);
                        List<StatementNode> nextStatements = nextCase.getStatements();
                        boolean nextHasBody = nextStatements != null && !nextStatements.isEmpty();
                        
                        if (nextHasBody && nextCase.getCondition() != null) {
                            // Next case has a body - combine with current case
                            // We'll handle this by making the current case jump to the next case's body
                            groupedCases.add(currentCase);
                            caseLabelPositions.add(instructions.size());
                        }
                        else {
                            // Skip this empty case
                            continue;
                        }
                    }
                }
            }
            else {
                // Default case - always add
                groupedCases.add(currentCase);
                caseLabelPositions.add(instructions.size());
            }
        }
        
        // Generate instructions for grouped cases
        List<JumpInstruction> jumpToEndInstructions = new ArrayList<>();
        List<Integer> jumpToEndPositions = new ArrayList<>();
        List<JumpIfPopInstruction> jumpIfInstructions = new ArrayList<>();
        List<Integer> jumpIfPositions = new ArrayList<>();
        List<Integer> nextCasePositions = new ArrayList<>();
        
        for (int i = 0; i < groupedCases.size(); i++) {
            SwitchCaseNode switchCase = groupedCases.get(i);
            ExpressionNode caseCondition = switchCase.getCondition();
            
            if (caseCondition != null) {
                List<StatementNode> statements = switchCase.getStatements();
                boolean hasBody = statements != null && !statements.isEmpty();
                
                // Load switch value
                LoadInstruction loadSwitchVar = new LoadInstruction(errorReporter, switchVarName, null);
                instructions.add(loadSwitchVar);
                
                // Load case value
                GenerationResult caseConditionResult = ((ASTNode)caseCondition).accept(this, context);
                instructions.addAll(caseConditionResult.getInstructions());
                
                // Check equality using ==
                BinaryOperator equalOperator = operatorManager.getBinaryOperator("==");
                instructions.add(new OperatorInstruction(errorReporter, equalOperator, null));
                
                // Jump to next case if not equal
                JumpIfPopInstruction jumpIf = new JumpIfPopInstruction(errorReporter, false, -1);
                instructions.add(jumpIf);
                jumpIfInstructions.add(jumpIf);
                jumpIfPositions.add(instructions.size() - 1);
                
                // If this case has an empty body (fallthrough), find the next case with a body
                int targetPos;
                if (!hasBody && i + 1 < groupedCases.size()) {
                    // Find the next case with a body
                    targetPos = -1;
                    for (int j = i + 1; j < groupedCases.size(); j++) {
                        List<StatementNode> nextStatements = groupedCases.get(j).getStatements();
                        if (nextStatements != null && !nextStatements.isEmpty()) {
                            targetPos = caseLabelPositions.get(j);
                            break;
                        }
                    }
                    if (targetPos == -1) {
                        targetPos = instructions.size();
                    }
                    nextCasePositions.add(targetPos);
                }
                else {
                    nextCasePositions.add(instructions.size());
                }
                
                // Generate case body statements
                if (statements != null) {
                    for (StatementNode stmt : statements) {
                        GenerationResult stmtResult = ((ASTNode)stmt).accept(this, context);
                        instructions.addAll(stmtResult.getInstructions());
                    }
                }
                
                // Jump to end after case body
                if (hasBody) {
                    JumpInstruction jumpToEnd = new JumpInstruction(errorReporter, -1);
                    instructions.add(jumpToEnd);
                    jumpToEndInstructions.add(jumpToEnd);
                    jumpToEndPositions.add(instructions.size() - 1);
                }
            }
            else {
                // Default case - no condition check needed
                List<StatementNode> statements = switchCase.getStatements();
                if (statements != null) {
                    for (StatementNode stmt : statements) {
                        GenerationResult stmtResult = ((ASTNode)stmt).accept(this, context);
                        instructions.addAll(stmtResult.getInstructions());
                    }
                }
            }
        }
        
        // Clear the inSwitch flag
        context.setProperty("inSwitch", Boolean.FALSE);
        
        // Set jumpIf targets
        int endPosition = instructions.size();
        for (int i = 0; i < jumpIfInstructions.size(); i++) {
            JumpIfPopInstruction jumpIf = jumpIfInstructions.get(i);
            int jumpIfPosition = jumpIfPositions.get(i);
            int targetPos = nextCasePositions.get(i);
            jumpIf.setPosition(targetPos - jumpIfPosition - 1);
        }
        
        // Set all jump to end targets
        for (int i = 0; i < jumpToEndInstructions.size(); i++) {
            JumpInstruction jump = jumpToEndInstructions.get(i);
            int jumpPosition = jumpToEndPositions.get(i);
            jump.setPosition(endPosition - jumpPosition - 1);
        }
        
        // Set break statement jump targets
        for (JumpInstruction breakJump : switchBreakTargets) {
            int jumpPosition = instructions.indexOf(breakJump);
            if (jumpPosition >= 0) {
                breakJump.setPosition(endPosition - jumpPosition - 1);
            }
        }
        
        // Push null as result (switch statement doesn't produce a value)
        instructions.add(new ConstInstruction(errorReporter, null, null));
        
        return new GenerationResult(instructions, false, 0);
    }
    
    @Override
    public GenerationResult visit(TryCatchNode node, GenerationContext context)
        throws Exception {
        ErrorReporter errorReporter = createErrorReporter(node);
        
        // Generate try block lambda
        GenerationContext tryContext = context.createChildContext();
        List<QLInstruction> tryInstructions = new ArrayList<>();
        GenerationResult tryResult = ((ASTNode)node.getTryBlock()).accept(this, tryContext);
        tryInstructions.addAll(tryResult.getInstructions());
        QLambdaDefinitionInner tryLambda = new QLambdaDefinitionInner("try_block_" + System.nanoTime(), tryInstructions,
            Collections.emptyList(), calculateMaxStack(tryInstructions));
        
        // Generate catch handlers
        List<CatchClauseNode> catchClauses = node.getCatchClauses();
        List<java.util.Map.Entry<Class<?>, QLambdaDefinition>> exceptionTable = new ArrayList<>();
        
        if (catchClauses != null) {
            for (CatchClauseNode catchClause : catchClauses) {
                // Resolve exception types
                List<String> exceptionTypes = catchClause.getExceptionTypes();
                Class<?> primaryExceptionType = Exception.class; // Default to Exception
                
                if (exceptionTypes != null && !exceptionTypes.isEmpty()) {
                    // TODO: Resolve actual exception types from type names
                    // For now, use Exception.class as placeholder
                    primaryExceptionType = Exception.class;
                }
                
                // Generate catch body lambda
                GenerationContext catchContext = context.createChildContext();
                List<QLInstruction> catchInstructions = new ArrayList<>();
                
                // The exception variable is passed as a parameter to the lambda
                String exceptionVarName = catchClause.getVariableName();
                if (exceptionVarName != null) {
                    // Define local variable for the exception
                    catchInstructions.add(new DefineLocalInstruction(errorReporter, exceptionVarName, Throwable.class));
                }
                
                // Generate catch body statements
                if (catchClause.getBody() != null) {
                    GenerationResult catchBodyResult = ((ASTNode)catchClause.getBody()).accept(this, catchContext);
                    catchInstructions.addAll(catchBodyResult.getInstructions());
                }
                
                QLambdaDefinitionInner.Param param = new QLambdaDefinitionInner.Param(
                    exceptionVarName != null ? exceptionVarName : "exception", Throwable.class);
                QLambdaDefinitionInner catchLambda =
                    new QLambdaDefinitionInner("catch_" + exceptionVarName + "_" + System.nanoTime(), catchInstructions,
                        Collections.singletonList(param), calculateMaxStack(catchInstructions));
                
                exceptionTable.add(new java.util.AbstractMap.SimpleEntry<>(primaryExceptionType, catchLambda));
            }
        }
        
        // Generate finally block lambda (if present)
        QLambdaDefinitionInner finallyLambda = null;
        BlockNode finallyBlock = node.getFinallyBlock();
        if (finallyBlock != null) {
            GenerationContext finallyContext = context.createChildContext();
            List<QLInstruction> finallyInstructions = new ArrayList<>();
            GenerationResult finallyResult = ((ASTNode)finallyBlock).accept(this, finallyContext);
            finallyInstructions.addAll(finallyResult.getInstructions());
            finallyLambda = new QLambdaDefinitionInner("finally_block_" + System.nanoTime(), finallyInstructions,
                Collections.emptyList(), calculateMaxStack(finallyInstructions));
        }
        
        // Create try-catch instruction
        TryCatchInstruction instruction =
            new TryCatchInstruction(errorReporter, tryLambda, exceptionTable, finallyLambda);
        
        return new GenerationResult(Collections.singletonList(instruction), false, 0);
    }
    
    @Override
    public GenerationResult visit(ReturnNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate return value expression
        if (node.getValue() != null) {
            GenerationResult valueResult = ((ASTNode)node.getValue()).accept(this, context);
            instructions.addAll(valueResult.getInstructions());
        }
        else {
            // No return value, push null
            instructions.add(new ConstInstruction(PureErrReporter.INSTANCE, null, null));
        }
        
        // Add return instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        instructions.add(new ReturnInstruction(errorReporter, QResult.ResultType.RETURN, null));
        
        return new GenerationResult(instructions, false, 0);
    }
    
    @Override
    public GenerationResult visit(BreakNode node, GenerationContext context)
        throws Exception {
        ErrorReporter errorReporter = createErrorReporter(node);
        
        // Check if we're in a switch statement
        Boolean inSwitch = (Boolean)context.getProperty("inSwitch");
        if (inSwitch != null && inSwitch) {
            // In a switch, break should jump to the end of the switch
            JumpInstruction jumpInstruction = new JumpInstruction(errorReporter, -1);
            
            // Add this jump to the list of break targets
            // The position will be calculated after all instructions are generated
            @SuppressWarnings("unchecked")
            List<JumpInstruction> breakTargets = (List<JumpInstruction>)context.getProperty("switchBreakTargets");
            
            if (breakTargets != null) {
                breakTargets.add(jumpInstruction);
            }
            
            return new GenerationResult(Collections.singletonList(jumpInstruction), false, 0);
        }
        
        // In a loop, break returns LOOP_BREAK_RESULT
        BreakContinueInstruction instruction = new BreakContinueInstruction(errorReporter, QResult.LOOP_BREAK_RESULT);
        return new GenerationResult(Collections.singletonList(instruction), false, 0);
    }
    
    @Override
    public GenerationResult visit(ContinueNode node, GenerationContext context)
        throws Exception {
        ErrorReporter errorReporter = createErrorReporter(node);
        BreakContinueInstruction instruction =
            new BreakContinueInstruction(errorReporter, QResult.LOOP_CONTINUE_RESULT);
        return new GenerationResult(Collections.singletonList(instruction), false, 0);
    }
    
    @Override
    public GenerationResult visit(ThrowNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate exception expression
        GenerationResult exceptionResult = ((ASTNode)node.getException()).accept(this, context);
        instructions.addAll(exceptionResult.getInstructions());
        
        // Add throw instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        instructions.add(new ThrowInstruction(errorReporter));
        
        return new GenerationResult(instructions, false, 0);
    }
    
    @Override
    public GenerationResult visit(VariableDeclarationNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate initial value expression
        ExpressionNode initialValue = node.getInitialValue();
        if (initialValue != null) {
            GenerationResult valueResult = ((ASTNode)initialValue).accept(this, context);
            instructions.addAll(valueResult.getInstructions());
        }
        else {
            // No initial value, push null
            instructions.add(new ConstInstruction(PureErrReporter.INSTANCE, null, null));
        }
        
        // Add define local instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        // Note: For now, use Object.class as the type - in real implementation, resolve typeName
        Class<?> varClass = Object.class; // TODO: Resolve actual type from node.getTypeName()
        DefineLocalInstruction instruction =
            new DefineLocalInstruction(errorReporter, node.getVariableName(), varClass);
        instructions.add(instruction);
        
        return new GenerationResult(instructions, false, 0);
    }
    
    @Override
    public GenerationResult visit(AssignmentNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate value expression
        GenerationResult valueResult = ((ASTNode)node.getValue()).accept(this, context);
        instructions.addAll(valueResult.getInstructions());
        
        // For simple assignment (=), we need to store the value
        // For compound assignment (+=, -=, etc.), we need to load the target, apply operator, then store
        if ("=".equals(node.getOperator())) {
            // Simple assignment - just store the value (no separate instruction, value is on stack)
            // The target (identifier) needs to be handled by Load/Define
            // For now, we'll use LoadInstruction which creates the symbol if it doesn't exist
        }
        else {
            // Compound assignment - load target first, then apply operator
            GenerationResult targetResult = ((ASTNode)node.getTarget()).accept(this, context);
            // Insert target instructions before value instructions
            instructions.addAll(0, targetResult.getInstructions());
            
            // Apply the compound operator
            BinaryOperator operator = operatorManager.getBinaryOperator(node.getOperator());
            if (operator != null) {
                ErrorReporter errorReporter = createErrorReporter(node);
                instructions.add(new OperatorInstruction(errorReporter, operator, null));
            }
        }
        
        return new GenerationResult(instructions, true, 1); // Assignment is an expression
    }
    
    @Override
    public GenerationResult visit(TypeDeclarationNode node, GenerationContext context)
        throws Exception {
        // TODO: Implement in a future story
        throw new UnsupportedOperationException("type declaration generation not yet implemented");
    }
    
    @Override
    public GenerationResult visit(ImportNode node, GenerationContext context)
        throws Exception {
        // TODO: Implement in a future story
        throw new UnsupportedOperationException("import generation not yet implemented");
    }
    
    @Override
    public GenerationResult visit(FunctionDefinitionNode node, GenerationContext context)
        throws Exception {
        // Generate instructions for function body in a child context (new scope)
        GenerationContext functionContext = context.createChildContext();
        List<QLInstruction> bodyInstructions = new ArrayList<>();
        
        GenerationResult bodyResult = ((ASTNode)node.getBody()).accept(this, functionContext);
        bodyInstructions.addAll(bodyResult.getInstructions());
        
        // Convert parameters to QLambdaDefinitionInner.Param format
        List<QLambdaDefinitionInner.Param> params = node.getParameters()
            .stream()
            .map(p -> new QLambdaDefinitionInner.Param(p.getParameterName(), Object.class))
            .collect(Collectors.toList());
        
        // Calculate max stack size
        int maxStackSize = calculateMaxStack(bodyInstructions);
        
        // Create function definition
        String functionName = node.getFunctionName();
        QLambdaDefinitionInner functionDefinition =
            new QLambdaDefinitionInner(functionName, bodyInstructions, params, maxStackSize);
        
        // Define the function in the current context
        ErrorReporter errorReporter = createErrorReporter(node);
        DefineFunctionInstruction instruction =
            new DefineFunctionInstruction(errorReporter, functionName, functionDefinition);
        
        return new GenerationResult(Collections.singletonList(instruction), false, 0);
    }
    
    @Override
    public GenerationResult visit(MacroDefinitionNode node, GenerationContext context)
        throws Exception {
        // Macros are compile-time constructs that store instructions for later inlining
        // They don't generate runtime instructions directly
        // The macro definition is stored in the context/scope for use during compilation
        // For now, we generate no runtime instructions for macro definitions
        // TODO: Implement macro definition storage in context when needed
        return new GenerationResult(Collections.emptyList(), false, 0);
    }
    
    // ==================== Expression Visitors ====================
    
    @Override
    public GenerationResult visit(LiteralNode node, GenerationContext context)
        throws Exception {
        ErrorReporter errorReporter = createErrorReporter(node);
        ConstInstruction instruction = new ConstInstruction(errorReporter, node.getValue(), null);
        return new GenerationResult(Collections.singletonList(instruction), true, 1);
    }
    
    @Override
    public GenerationResult visit(IdentifierNode node, GenerationContext context)
        throws Exception {
        ErrorReporter errorReporter = createErrorReporter(node);
        LoadInstruction instruction = new LoadInstruction(errorReporter, node.getName(), null);
        return new GenerationResult(Collections.singletonList(instruction), true, 1);
    }
    
    @Override
    public GenerationResult visit(BinaryOpNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate left operand
        GenerationResult leftResult = ((ASTNode)node.getLeft()).accept(this, context);
        instructions.addAll(leftResult.getInstructions());
        
        // Generate right operand
        GenerationResult rightResult = ((ASTNode)node.getRight()).accept(this, context);
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
    public GenerationResult visit(UnaryOpNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate operand
        GenerationResult operandResult = ((ASTNode)node.getOperand()).accept(this, context);
        instructions.addAll(operandResult.getInstructions());
        
        // Generate unary operator instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        UnaryOperator operator;
        if (node.isPrefix()) {
            operator = operatorManager.getPrefixUnaryOperator(node.getOperator());
        }
        else {
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
    public GenerationResult visit(TernaryNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate condition
        GenerationResult conditionResult = ((ASTNode)node.getCondition()).accept(this, context);
        instructions.addAll(conditionResult.getInstructions());
        
        ErrorReporter errorReporter = createErrorReporter(node);
        
        // Jump to else if condition is false
        JumpIfInstruction jumpIf = new JumpIfInstruction(errorReporter, false, -1, null);
        instructions.add(jumpIf);
        
        // Generate then expression
        GenerationResult thenResult = ((ASTNode)node.getThenExpr()).accept(this, context);
        instructions.addAll(thenResult.getInstructions());
        
        // Jump to end after then
        JumpInstruction jump = new JumpInstruction(errorReporter, -1);
        instructions.add(jump);
        
        // Set jumpIf target (start of else)
        jumpIf.setPosition(instructions.size());
        
        // Generate else expression
        GenerationResult elseResult = ((ASTNode)node.getElseExpr()).accept(this, context);
        instructions.addAll(elseResult.getInstructions());
        
        // Set jump target (end of ternary)
        jump.setPosition(instructions.size());
        
        return new GenerationResult(instructions, true, 1);
    }
    
    @Override
    public GenerationResult visit(LambdaNode node, GenerationContext context)
        throws Exception {
        // Generate instructions for lambda body
        GenerationContext lambdaContext = context.createChildContext();
        List<QLInstruction> bodyInstructions = new ArrayList<>();
        
        // Handle lambda body (could be ExpressionNode or BlockNode)
        if (node.getBody() instanceof ExpressionNode) {
            GenerationResult bodyResult = ((ASTNode)node.getBody()).accept(this, lambdaContext);
            bodyInstructions.addAll(bodyResult.getInstructions());
            // Add return for expression body
            bodyInstructions.add(new ReturnInstruction(PureErrReporter.INSTANCE, QResult.ResultType.CONTINUE, null));
        }
        else if (node.getBody() instanceof BlockNode) {
            GenerationResult bodyResult = ((ASTNode)node.getBody()).accept(this, lambdaContext);
            bodyInstructions.addAll(bodyResult.getInstructions());
        }
        
        // Convert parameters to QLambdaDefinitionInner.Param format
        List<QLambdaDefinitionInner.Param> params = node.getParameters()
            .stream()
            .map(p -> new QLambdaDefinitionInner.Param(p.getParameterName(), Object.class))
            .collect(Collectors.toList());
        
        // Create lambda definition
        String lambdaName = "LAMBDA_" + System.nanoTime();
        QLambdaDefinitionInner lambdaDefinition = new QLambdaDefinitionInner(lambdaName, bodyInstructions, params, 0);
        
        // Load the lambda
        ErrorReporter errorReporter = createErrorReporter(node);
        LoadLambdaInstruction instruction = new LoadLambdaInstruction(errorReporter, lambdaDefinition);
        
        return new GenerationResult(Collections.singletonList(instruction), true, 1);
    }
    
    @Override
    public GenerationResult visit(MethodCallNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // If target is null, this is a direct function call (e.g., stest(9))
        // Use CallFunctionInstruction which loads the function by name from the context
        if (node.getTarget() == null) {
            // Generate arguments
            for (ExpressionNode arg : node.getArguments()) {
                GenerationResult argResult = ((ASTNode)arg).accept(this, context);
                instructions.addAll(argResult.getInstructions());
            }
            
            // Generate call function instruction
            ErrorReporter errorReporter = createErrorReporter(node);
            CallFunctionInstruction instruction =
                new CallFunctionInstruction(errorReporter, node.getMethodName(), node.getArguments().size(), null);
            instructions.add(instruction);
            
            return new GenerationResult(instructions, true, 1);
        }
        
        // Check if this is a static method call on a class
        // For example: QLOptions.builder() where QLOptions is a class
        if (node.getTarget() instanceof IdentifierNode && importManager != null) {
            IdentifierNode targetId = (IdentifierNode)node.getTarget();
            List<String> ids = new ArrayList<>();
            ids.add(targetId.getName());
            
            // Check if this single identifier can be resolved to a class
            ImportManager.LoadPartQualifiedResult result = importManager.loadPartQualified(ids);
            if (result.getCls() != null && result.getRestIndex() == 1) {
                // This is a class reference - generate MetaClass const instruction
                ErrorReporter errorReporter = createErrorReporter(targetId);
                instructions.add(new ConstInstruction(errorReporter, new MetaClass(result.getCls()), null));
            }
            else {
                // Not a class - generate load instruction
                GenerationResult targetResult = ((ASTNode)node.getTarget()).accept(this, context);
                instructions.addAll(targetResult.getInstructions());
            }
        }
        else if (node.getTarget() != null) {
            // Generate target expression (if not null - for static calls, target is null)
            GenerationResult targetResult = ((ASTNode)node.getTarget()).accept(this, context);
            instructions.addAll(targetResult.getInstructions());
        }
        
        // Generate arguments
        for (ExpressionNode arg : node.getArguments()) {
            GenerationResult argResult = ((ASTNode)arg).accept(this, context);
            instructions.addAll(argResult.getInstructions());
        }
        
        // Generate method invoke instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        MethodInvokeInstruction instruction =
            new MethodInvokeInstruction(errorReporter, node.getMethodName(), node.getArguments().size(), false); // optional = false for now
        instructions.add(instruction);
        
        return new GenerationResult(instructions, true, 1);
    }
    
    @Override
    public GenerationResult visit(ConstructorCallNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate arguments
        for (ExpressionNode arg : node.getArguments()) {
            GenerationResult argResult = ((ASTNode)arg).accept(this, context);
            instructions.addAll(argResult.getInstructions());
        }
        
        // Generate constructor call instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        // Note: We need to resolve the type name to a Class<?> object
        // For now, we'll use Object.class as a placeholder - in a real implementation,
        // this would use an ImportManager to resolve the type
        Class<?> typeClass = Object.class; // TODO: Resolve actual type
        NewInstanceInstruction instruction =
            new NewInstanceInstruction(errorReporter, typeClass, node.getArguments().size());
        instructions.add(instruction);
        
        return new GenerationResult(instructions, true, 1);
    }
    
    @Override
    public GenerationResult visit(CastNode node, GenerationContext context)
        throws Exception {
        // TODO: Implement in a future story
        throw new UnsupportedOperationException("cast expression generation not yet implemented");
    }
    
    @Override
    public GenerationResult visit(ArrayAccessNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate array expression
        GenerationResult arrayResult = ((ASTNode)node.getArray()).accept(this, context);
        instructions.addAll(arrayResult.getInstructions());
        
        // Generate index expression
        GenerationResult indexResult = ((ASTNode)node.getIndex()).accept(this, context);
        instructions.addAll(indexResult.getInstructions());
        
        // Generate index instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        IndexInstruction instruction = new IndexInstruction(errorReporter);
        instructions.add(instruction);
        
        return new GenerationResult(instructions, true, 1);
    }
    
    @Override
    public GenerationResult visit(ArraySliceNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate array expression
        GenerationResult arrayResult = ((ASTNode)node.getArray()).accept(this, context);
        instructions.addAll(arrayResult.getInstructions());
        
        // Determine the slice mode and generate appropriate instructions
        ErrorReporter errorReporter = createErrorReporter(node);
        ExpressionNode start = node.getStart();
        ExpressionNode end = node.getEnd();
        
        if (start == null && end == null) {
            // [:] - COPY mode (no indices needed)
            instructions.add(new SliceInstruction(errorReporter, SliceInstruction.Mode.COPY));
        }
        else if (start == null) {
            // [:end] - LEFT mode (only end index)
            GenerationResult endResult = ((ASTNode)end).accept(this, context);
            instructions.addAll(endResult.getInstructions());
            instructions.add(new SliceInstruction(errorReporter, SliceInstruction.Mode.LEFT));
        }
        else if (end == null) {
            // [start:] - RIGHT mode (only start index)
            GenerationResult startResult = ((ASTNode)start).accept(this, context);
            instructions.addAll(startResult.getInstructions());
            instructions.add(new SliceInstruction(errorReporter, SliceInstruction.Mode.RIGHT));
        }
        else {
            // [start:end] - BOTH mode (both indices)
            GenerationResult startResult = ((ASTNode)start).accept(this, context);
            instructions.addAll(startResult.getInstructions());
            GenerationResult endResult = ((ASTNode)end).accept(this, context);
            instructions.addAll(endResult.getInstructions());
            instructions.add(new SliceInstruction(errorReporter, SliceInstruction.Mode.BOTH));
        }
        
        return new GenerationResult(instructions, true, 1);
    }
    
    @Override
    public GenerationResult visit(ArrayLiteralNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate element expressions
        for (ExpressionNode element : node.getElements()) {
            GenerationResult elementResult = ((ASTNode)element).accept(this, context);
            instructions.addAll(elementResult.getInstructions());
        }
        
        // Generate new array instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        // Note: We need to determine the element type - for now, use Object.class
        Class<?> elementClass = Object.class; // TODO: Infer actual element type
        NewArrayInstruction instruction =
            new NewArrayInstruction(errorReporter, elementClass, node.getElements().size());
        instructions.add(instruction);
        
        return new GenerationResult(instructions, true, 1);
    }
    
    @Override
    public GenerationResult visit(MapLiteralNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate value expressions (keys must be string literals or constant expressions)
        List<String> keys = new ArrayList<>();
        for (MapEntryNode entry : node.getEntries()) {
            // Key must be a constant (string literal)
            if (entry.getKey() instanceof LiteralNode) {
                Object keyValue = ((LiteralNode)entry.getKey()).getValue();
                if (keyValue instanceof String) {
                    keys.add((String)keyValue);
                }
                else {
                    throw new UnsupportedOperationException("Map keys must be string literals");
                }
            }
            else {
                throw new UnsupportedOperationException("Map keys must be string literals");
            }
            
            // Generate value expression
            GenerationResult valueResult = ((ASTNode)entry.getValue()).accept(this, context);
            instructions.addAll(valueResult.getInstructions());
        }
        
        // Generate new map instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        NewMapInstruction instruction = new NewMapInstruction(errorReporter, keys);
        instructions.add(instruction);
        
        return new GenerationResult(instructions, true, 1);
    }
    
    @Override
    public GenerationResult visit(ListLiteralNode node, GenerationContext context)
        throws Exception {
        List<QLInstruction> instructions = new ArrayList<>();
        
        // Generate element expressions
        for (ExpressionNode element : node.getElements()) {
            GenerationResult elementResult = ((ASTNode)element).accept(this, context);
            instructions.addAll(elementResult.getInstructions());
        }
        
        // Generate new list instruction
        ErrorReporter errorReporter = createErrorReporter(node);
        NewListInstruction instruction = new NewListInstruction(errorReporter, node.getElements().size());
        instructions.add(instruction);
        
        return new GenerationResult(instructions, true, 1);
    }
    
    @Override
    public GenerationResult visit(InstanceOfNode node, GenerationContext context)
        throws Exception {
        // TODO: Implement in a future story (instanceof is not in the grammar)
        throw new UnsupportedOperationException("instanceof generation not yet implemented");
    }
    
    @Override
    public GenerationResult visit(TypeNode node, GenerationContext context)
        throws Exception {
        // TODO: Implement in US-018 or future
        throw new UnsupportedOperationException("type node generation not yet implemented");
    }
    
    // ==================== Other Visitors ====================
    
    @Override
    public GenerationResult visit(ProgramNode node, GenerationContext context)
        throws Exception {
        GenerationContext programContext = context.createChildContext();
        List<QLInstruction> instructions = new ArrayList<>();
        
        List<StatementNode> statements = node.getStatements();
        int numStatements = statements.size();
        
        for (int i = 0; i < numStatements; i++) {
            StatementNode statement = statements.get(i);
            GenerationResult result = ((ASTNode)statement).accept(this, programContext);
            instructions.addAll(result.getInstructions());
            
            // If the statement is an expression, pop its result unless it's the last statement
            if (result.isExpressionValue() && i < numStatements - 1) {
                instructions.add(new PopInstruction(PureErrReporter.INSTANCE));
            }
        }
        
        return new GenerationResult(instructions, false, 0);
    }
    
    // ==================== Helper Methods ====================
    
    private ErrorReporter createErrorReporter(ASTNode node) {
        return PureErrReporter.INSTANCE;
    }
    
    /**
     * Calculate the maximum stack size needed for a list of instructions.
     * This is a simplified estimation - the original implementation tracks
     * stack size during generation. For now, we use a conservative estimate
     * based on instruction count.
     */
    private int calculateMaxStack(List<QLInstruction> instructions) {
        // Use a simple heuristic: base stack size on instruction count
        // This is conservative but safe - actual stack usage is usually much lower
        return Math.max(10, instructions.size() / 2 + 5);
    }
}
