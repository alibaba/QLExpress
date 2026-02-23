package com.alibaba.qlexpress4.parser.visitor;

import com.alibaba.qlexpress4.parser.ast.*;
import com.alibaba.qlexpress4.runtime.trace.TracePointTree;
import com.alibaba.qlexpress4.runtime.trace.TraceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * TraceGenerator visitor generates execution trace information from AST.
 * <p>
 * This visitor traverses the AST and produces a list of TracePointTree objects
 * that can be used for execution tracing and debugging. Each trace point contains
 * information about the type of operation, the source location, and child trace points.
 * <p>
 * The trace points are used to:
 * <ul>
 *   <li>Track expression evaluation during runtime</li>
 *   <li>Provide debugging information for errors</li>
 *   <li>Enable execution path visualization</li>
 * </ul>
 */
public class TraceGenerator implements ASTVisitor<TracePointTree, Void> {
    
    /**
     * The list of all trace points generated during AST traversal.
     */
    private final List<TracePointTree> tracePoints = new ArrayList<>();
    
    /**
     * Gets the list of trace points generated from the AST.
     *
     * @return the list of trace points
     */
    public List<TracePointTree> getTracePoints() {
        return tracePoints;
    }
    
    // ==================== Statement Visitors ====================
    
    @Override
    public TracePointTree visit(BlockNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        for (StatementNode statement : node.getStatements()) {
            TracePointTree child = acceptNode(statement);
            if (child != null) {
                children.add(child);
            }
        }
        return newPoint(TraceType.BLOCK, children, "{", node);
    }
    
    @Override
    public TracePointTree visit(IfNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        children.add(acceptNode(node.getCondition()));
        children.add(acceptNode(node.getThenBody()));
        
        Node elseBody = node.getElseBody();
        if (elseBody != null) {
            children.add(acceptNode(elseBody));
        }
        
        tracePoints.add(newPoint(TraceType.IF, children, "if", node));
        return null;
    }
    
    @Override
    public TracePointTree visit(WhileNode node, Void context) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT, Collections.emptyList(), "while", node);
        tracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visit(ForNode node, Void context) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT, Collections.emptyList(), "for", node);
        tracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visit(SwitchNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        
        // Add switch value expression
        ExpressionNode value = node.getValue();
        if (value != null) {
            TracePointTree valueTrace = acceptNode(value);
            if (valueTrace != null) {
                children.add(valueTrace);
            }
        }
        
        // Add case expressions and bodies
        for (SwitchCaseNode caseNode : node.getCases()) {
            // Case label expression
            ExpressionNode caseCondition = caseNode.getCondition();
            if (caseCondition != null) {
                TracePointTree caseTrace = acceptNode(caseCondition);
                if (caseTrace != null) {
                    children.add(caseTrace);
                }
            }
            // Case body
            for (StatementNode stmt : caseNode.getStatements()) {
                TracePointTree bodyTrace = acceptNode(stmt);
                if (bodyTrace != null) {
                    children.add(bodyTrace);
                }
            }
        }
        
        return newPoint(TraceType.SWITCH, children, "switch", node);
    }
    
    @Override
    public TracePointTree visit(TryCatchNode node, Void context) {
        TracePointTree tracePoint = newPoint(TraceType.PRIMARY, Collections.emptyList(), "try", node);
        tracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visit(ReturnNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        ExpressionNode value = node.getValue();
        if (value != null) {
            TracePointTree valueTrace = acceptNode(value);
            if (valueTrace != null) {
                children.add(valueTrace);
            }
        }
        TracePointTree tracePoint = newPoint(TraceType.RETURN, children, "return", node);
        tracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visit(BreakNode node, Void context) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT, Collections.emptyList(), "break", node);
        tracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visit(ContinueNode node, Void context) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT, Collections.emptyList(), "continue", node);
        tracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visit(ThrowNode node, Void context) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT, Collections.emptyList(), "throw", node);
        tracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visit(VariableDeclarationNode node, Void context) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT,
            Collections.emptyList(),
            node.getTypeName() != null ? node.getTypeName() : "var",
            node);
        tracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visit(AssignmentNode node, Void context) {
        TracePointTree targetTrace = acceptNode(node.getTarget());
        TracePointTree valueTrace = acceptNode(node.getValue());
        return newPoint(TraceType.OPERATOR, Arrays.asList(targetTrace, valueTrace), node.getOperator(), node);
    }
    
    @Override
    public TracePointTree visit(TypeDeclarationNode node, Void context) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT,
            Collections.emptyList(),
            node.getTypeName() != null ? node.getTypeName() : "class",
            node);
        tracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visit(ImportNode node, Void context) {
        TracePointTree tracePoint = newPoint(TraceType.STATEMENT, Collections.emptyList(), "import", node);
        tracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visit(FunctionDefinitionNode node, Void context) {
        String functionName = node.getFunctionName() != null ? node.getFunctionName() : "function";
        TracePointTree tracePoint = newPoint(TraceType.DEFINE_FUNCTION, Collections.emptyList(), functionName, node);
        tracePoints.add(tracePoint);
        return null;
    }
    
    @Override
    public TracePointTree visit(MacroDefinitionNode node, Void context) {
        String macroName = node.getMacroName() != null ? node.getMacroName() : "macro";
        TracePointTree tracePoint = newPoint(TraceType.DEFINE_MACRO, Collections.emptyList(), macroName, node);
        tracePoints.add(tracePoint);
        return null;
    }
    
    // ==================== Expression Visitors ====================
    
    @Override
    public TracePointTree visit(LiteralNode node, Void context) {
        return newPoint(TraceType.VALUE, Collections.emptyList(), String.valueOf(node.getValue()), node);
    }
    
    @Override
    public TracePointTree visit(InterpolatedStringNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        // For each segment, generate a trace point
        for (Object segment : node.getSegments()) {
            if (segment instanceof String) {
                children.add(newPoint(TraceType.VALUE, Collections.emptyList(), (String)segment, node));
            }
            else if (segment instanceof ExpressionNode) {
                TracePointTree childTrace = acceptNode((ExpressionNode)segment);
                if (childTrace != null) {
                    children.add(childTrace);
                }
            }
        }
        return newPoint(TraceType.VALUE, children, "interpolated string", node);
    }
    
    @Override
    public TracePointTree visit(IdentifierNode node, Void context) {
        return newPoint(TraceType.VARIABLE, Collections.emptyList(), node.getName(), node);
    }
    
    @Override
    public TracePointTree visit(BinaryOpNode node, Void context) {
        TracePointTree leftTrace = acceptNode(node.getLeft());
        TracePointTree rightTrace = acceptNode(node.getRight());
        return newPoint(TraceType.OPERATOR, Arrays.asList(leftTrace, rightTrace), node.getOperator(), node);
    }
    
    @Override
    public TracePointTree visit(UnaryOpNode node, Void context) {
        TracePointTree operandTrace = acceptNode(node.getOperand());
        return newPoint(TraceType.OPERATOR, Collections.singletonList(operandTrace), node.getOperator(), node);
    }
    
    @Override
    public TracePointTree visit(TernaryNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>(3);
        children.add(acceptNode(node.getCondition()));
        children.add(acceptNode(node.getThenExpr()));
        children.add(acceptNode(node.getElseExpr()));
        return newPoint(TraceType.OPERATOR, children, "?", node);
    }
    
    @Override
    public TracePointTree visit(LambdaNode node, Void context) {
        return newPoint(TraceType.PRIMARY, Collections.emptyList(), "->", node);
    }
    
    @Override
    public TracePointTree visit(MethodReferenceNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        
        // Add target
        TracePointTree targetTrace = acceptNode(node.getTarget());
        if (targetTrace != null) {
            children.add(targetTrace);
        }
        
        return newPoint(TraceType.METHOD, children, node.getMethodName() + "::", node);
    }
    
    @Override
    public TracePointTree visit(FieldAccessNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        
        // Add target
        TracePointTree targetTrace = acceptNode(node.getTarget());
        if (targetTrace != null) {
            children.add(targetTrace);
        }
        
        String token = (node.isOptional() ? "?." : ".") + node.getFieldName();
        return newPoint(TraceType.FIELD, children, token, node);
    }
    
    @Override
    public TracePointTree visit(MethodCallNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        
        // Add target if present
        ExpressionNode target = node.getTarget();
        if (target != null) {
            TracePointTree targetTrace = acceptNode(target);
            if (targetTrace != null) {
                children.add(targetTrace);
            }
        }
        
        // Add arguments
        for (ExpressionNode arg : node.getArguments()) {
            TracePointTree argTrace = acceptNode(arg);
            if (argTrace != null) {
                children.add(argTrace);
            }
        }
        
        String methodName = node.getMethodName();
        TraceType type = (target == null) ? TraceType.FUNCTION : TraceType.METHOD;
        return newPoint(type, children, methodName != null ? methodName : "call", node);
    }
    
    @Override
    public TracePointTree visit(ConstructorCallNode node, Void context) {
        return newPoint(TraceType.PRIMARY,
            Collections.emptyList(),
            node.getTypeName() != null ? node.getTypeName() : "new",
            node);
    }
    
    @Override
    public TracePointTree visit(CastNode node, Void context) {
        // Cast expressions are traced as their expression
        return acceptNode(node.getExpression());
    }
    
    @Override
    public TracePointTree visit(ArrayAccessNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        
        TracePointTree arrayTrace = acceptNode(node.getArray());
        if (arrayTrace != null) {
            children.add(arrayTrace);
        }
        
        TracePointTree indexTrace = acceptNode(node.getIndex());
        if (indexTrace != null) {
            children.add(indexTrace);
        }
        
        return newPoint(TraceType.OPERATOR, children, "[", node);
    }
    
    @Override
    public TracePointTree visit(ArraySliceNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        
        TracePointTree arrayTrace = acceptNode(node.getArray());
        if (arrayTrace != null) {
            children.add(arrayTrace);
        }
        
        if (node.getStart() != null) {
            TracePointTree startTrace = acceptNode(node.getStart());
            if (startTrace != null) {
                children.add(startTrace);
            }
        }
        
        if (node.getEnd() != null) {
            TracePointTree endTrace = acceptNode(node.getEnd());
            if (endTrace != null) {
                children.add(endTrace);
            }
        }
        
        return newPoint(TraceType.OPERATOR, children, "[", node);
    }
    
    @Override
    public TracePointTree visit(ArrayLiteralNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        for (ExpressionNode element : node.getElements()) {
            TracePointTree elementTrace = acceptNode(element);
            if (elementTrace != null) {
                children.add(elementTrace);
            }
        }
        return newPoint(TraceType.PRIMARY, children, "new", node);
    }
    
    @Override
    public TracePointTree visit(MapLiteralNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        for (MapEntryNode entry : node.getEntries()) {
            TracePointTree keyTrace = acceptNode(entry.getKey());
            TracePointTree valueTrace = acceptNode(entry.getValue());
            if (keyTrace != null) {
                children.add(keyTrace);
            }
            if (valueTrace != null) {
                children.add(valueTrace);
            }
        }
        return newPoint(TraceType.MAP, children, "{:", node);
    }
    
    @Override
    public TracePointTree visit(ListLiteralNode node, Void context) {
        List<TracePointTree> children = new ArrayList<>();
        for (ExpressionNode element : node.getElements()) {
            TracePointTree elementTrace = acceptNode(element);
            if (elementTrace != null) {
                children.add(elementTrace);
            }
        }
        return newPoint(TraceType.LIST, children, "[", node);
    }
    
    @Override
    public TracePointTree visit(InstanceOfNode node, Void context) {
        TracePointTree expressionTrace = acceptNode(node.getExpression());
        return newPoint(TraceType.OPERATOR, Collections.singletonList(expressionTrace), "instanceof", node);
    }
    
    @Override
    public TracePointTree visit(TypeNode node, Void context) {
        return newPoint(TraceType.VALUE,
            Collections.emptyList(),
            node.getTypeName() != null ? node.getTypeName() : "type",
            node);
    }
    
    @Override
    public TracePointTree visit(ProgramNode node, Void context) {
        // Visit all top-level statements to collect trace points
        for (StatementNode statement : node.getStatements()) {
            TracePointTree trace = acceptNode(statement);
            if (trace != null) {
                tracePoints.add(trace);
            }
        }
        return null;
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Safely visits a node and returns the trace point.
     * Handles any exceptions that may occur during visiting.
     */
    private TracePointTree acceptNode(Node node) {
        if (node == null) {
            return null;
        }
        try {
            return ((ASTNode)node).accept(this, null);
        }
        catch (Exception e) {
            // Log error but continue processing
            return null;
        }
    }
    
    /**
     * Creates a new TracePointTree with the given type, children, and token.
     */
    private TracePointTree newPoint(TraceType type, List<TracePointTree> children, String token, ASTNode node) {
        return new TracePointTree(type, token, children, node.getLine(), node.getColumn(), 0);
    }
}
