package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.aparser.compiletimefunction.CompileTimeFunction;
import com.alibaba.qlexpress4.api.BatchAddFunctionResult;
import com.alibaba.qlexpress4.api.QLFunctionalVarargs;
import com.alibaba.qlexpress4.common.GeneratorScope;
import com.alibaba.qlexpress4.common.ImportManager;
import com.alibaba.qlexpress4.common.MacroDefine;
import com.alibaba.qlexpress4.common.QCompileCache;
import com.alibaba.qlexpress4.exception.QLException;
import com.alibaba.qlexpress4.exception.QLSyntaxException;
import com.alibaba.qlexpress4.parser.ASTCompiler;
import com.alibaba.qlexpress4.parser.SyntaxTreeFactory;
import com.alibaba.qlexpress4.parser.ast.ProgramNode;
import com.alibaba.qlexpress4.parser.ast.StatementNode;
import com.alibaba.qlexpress4.parser.visitor.FunctionExtractor;
import com.alibaba.qlexpress4.parser.visitor.OutVariableDetector;
import com.alibaba.qlexpress4.parser.visitor.ScriptChecker;
import com.alibaba.qlexpress4.parser.visitor.VariableDetector;
import com.alibaba.qlexpress4.runtime.DelegateQContext;
import com.alibaba.qlexpress4.runtime.QLambda;
import com.alibaba.qlexpress4.runtime.QLambdaDefinition;
import com.alibaba.qlexpress4.runtime.QLambdaDefinitionInner;
import com.alibaba.qlexpress4.runtime.QLambdaTrace;
import com.alibaba.qlexpress4.runtime.QvmGlobalScope;
import com.alibaba.qlexpress4.runtime.QvmRuntime;
import com.alibaba.qlexpress4.runtime.ReflectLoader;
import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.context.ExpressContext;
import com.alibaba.qlexpress4.runtime.context.MapExpressContext;
import com.alibaba.qlexpress4.runtime.context.ObjectFieldExpressContext;
import com.alibaba.qlexpress4.runtime.context.QLAliasContext;
import com.alibaba.qlexpress4.runtime.function.CustomFunction;
import com.alibaba.qlexpress4.runtime.function.ExtensionFunction;
import com.alibaba.qlexpress4.runtime.function.QMethodFunction;
import com.alibaba.qlexpress4.runtime.operator.CustomBinaryOperator;
import com.alibaba.qlexpress4.runtime.operator.OperatorManager;
import com.alibaba.qlexpress4.runtime.trace.ExpressionTrace;
import com.alibaba.qlexpress4.runtime.trace.QTraces;
import com.alibaba.qlexpress4.runtime.trace.TracePointTree;
import com.alibaba.qlexpress4.utils.BasicUtil;
import com.alibaba.qlexpress4.utils.QLFunctionUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.alibaba.qlexpress4.parser.parser.QLexpressParser.ParseException;

/**
 * Author: DQinYuan
 */
public class Express4Runner {
    private final OperatorManager operatorManager = new OperatorManager();
    
    private final Map<String, Future<QCompileCache>> compileCache = new ConcurrentHashMap<>();
    
    private final Map<String, CustomFunction> userDefineFunction = new ConcurrentHashMap<>();
    
    private final Map<String, CompileTimeFunction> compileTimeFunctions = new ConcurrentHashMap<>();
    
    private final GeneratorScope globalScope = new GeneratorScope(null, "global", new ConcurrentHashMap<>());
    
    private final ReflectLoader reflectLoader;
    
    private final InitOptions initOptions;
    
    public Express4Runner(InitOptions initOptions) {
        this.initOptions = initOptions;
        this.reflectLoader = new ReflectLoader(initOptions.getSecurityStrategy(), initOptions.isAllowPrivateAccess());
    }
    
    public CustomFunction getFunction(String functionName) {
        return userDefineFunction.get(functionName);
    }
    
    public CompileTimeFunction getCompileTimeFunction(String functionName) {
        return compileTimeFunctions.get(functionName);
    }
    
    /**
     * Execute the script with variables set in the context; the map key corresponds to the
     * variable name referenced in the script.
     *
     * @param script     the script content to execute
     * @param context    variables for execution, keyed by variable name
     * @param qlOptions  execution options (e.g. interpolation, debug)
     * @return result of script execution and related traces
     * @throws QLException if a script or runtime error occurs
     */
    public QLResult execute(String script, Map<String, Object> context, QLOptions qlOptions)
        throws QLException {
        return execute(script, new MapExpressContext(context), qlOptions);
    }
    
    /**
     * Execute a template string by wrapping it as a dynamic string literal.
     * Template does not support newlines in this mode.
     *
     * @param template   the template text to evaluate as a dynamic string
     * @param context    variables available to the template
     * @param qlOptions  execution options
     * @return result of template evaluation
     * @throws QLException if compilation or execution fails
     */
    public QLResult executeTemplate(String template, Map<String, Object> context, QLOptions qlOptions)
        throws QLException {
        String script = wrapAsDynamicString(template);
        return execute(script, context, qlOptions);
    }
    
    private String wrapAsDynamicString(String template) {
        if (template == null) {
            return "\"\"";
        }
        String escaped = template.replace("\"", "\\\"");
        return "\"" + escaped + "\"";
    }
    
    /**
     * Execute the script with variables resolved from the fields of the given context object.
     * The variable name in the script corresponds to the field name on the object.
     *
     * @param script     the script content to execute
     * @param context    the object whose public fields/properties are exposed as variables
     * @param qlOptions  execution options
     * @return result of script execution
     * @throws QLException if a script or runtime error occurs
     */
    public QLResult execute(String script, Object context, QLOptions qlOptions)
        throws QLException {
        return execute(script, new ObjectFieldExpressContext(context, this), qlOptions);
    }
    
    /**
     * Execute the script using objects annotated with {@code @QLAlias}.
     * The {@code QLAlias.value} serves as the variable name for each object.
     * Objects without the annotation are ignored.
     *
     * @param script     the script content to execute
     * @param qlOptions  execution options
     * @param objects    objects annotated with {@code @QLAlias}
     * @return result of script execution
     * @throws QLException if a script or runtime error occurs
     */
    public QLResult executeWithAliasObjects(String script, QLOptions qlOptions, Object... objects) {
        return execute(script, new QLAliasContext(objects), qlOptions);
    }
    
    public QLResult execute(String script, ExpressContext context, QLOptions qlOptions) {
        QLambdaTrace mainLambdaTrace;
        if (initOptions.isDebug()) {
            long start = System.currentTimeMillis();
            mainLambdaTrace = parseToLambda(script, context, qlOptions);
            initOptions.getDebugInfoConsumer()
                .accept("Compile consume time: " + (System.currentTimeMillis() - start) + " ms");
        }
        else {
            mainLambdaTrace = parseToLambda(script, context, qlOptions);
        }
        QLambda mainLambda = mainLambdaTrace.getqLambda();
        try {
            Object result;
            if (initOptions.isDebug()) {
                long start = System.currentTimeMillis();
                result = mainLambda.call().getResult().get();
                initOptions.getDebugInfoConsumer()
                    .accept("Execute consume time: " + (System.currentTimeMillis() - start) + " ms");
            }
            else {
                result = mainLambda.call().getResult().get();
            }
            
            return new QLResult(result, mainLambdaTrace.getTraces().getExpressionTraces());
        }
        catch (QLException e) {
            throw e;
        }
        catch (Throwable nuKnown) {
            // should not run here
            throw new RuntimeException(nuKnown);
        }
    }
    
    private QTraces convertPoints2QTraces(List<TracePointTree> expressionTracePoints) {
        Map<Integer, ExpressionTrace> traceMap = new HashMap<>();
        List<ExpressionTrace> expressionTraces = expressionTracePoints.stream()
            .map(tracePoint -> convertPoint2Trace(tracePoint, traceMap))
            .collect(Collectors.toList());
        return new QTraces(expressionTraces, traceMap);
    }
    
    private ExpressionTrace convertPoint2Trace(TracePointTree tree, Map<Integer, ExpressionTrace> traceMap) {
        if (tree.getChildren().isEmpty()) {
            ExpressionTrace result = new ExpressionTrace(tree.getType(), tree.getToken(), Collections.emptyList(),
                tree.getLine(), tree.getCol(), tree.getPosition());
            traceMap.put(result.getPosition(), result);
            return result;
        }
        List<ExpressionTrace> mergedChildren =
            tree.getChildren().stream().map(child -> convertPoint2Trace(child, traceMap)).collect(Collectors.toList());
        ExpressionTrace result = new ExpressionTrace(tree.getType(), tree.getToken(), mergedChildren, tree.getLine(),
            tree.getCol(), tree.getPosition());
        traceMap.put(result.getPosition(), result);
        return result;
    }
    
    /**
     * Get external variables (those that must be provided via context) referenced by the script.
     *
     * @param script the script content
     * @return names of external variables referenced in the script
     */
    public Set<String> getOutVarNames(String script) {
        ProgramNode programNode;
        try {
            programNode = parseToSyntaxTree(script);
        }
        catch (ParseException e) {
            throw new RuntimeException("Failed to parse script", e);
        }

        // Use scope-aware out variable detection
        OutVariableDetector detector = new OutVariableDetector();
        try {
            return detector.detect(programNode);
        }
        catch (Exception e) {
            throw new RuntimeException("Error detecting external variables", e);
        }
    }
    
    /**
     * Get external variable attribute access paths referenced by the script.
     * NOTE: This is a simplified implementation that does not track attribute paths.
     * The full implementation would require more complex analysis.
     *
     * @param script the script content
     * @return empty set (attribute path tracking not yet implemented for new parser)
     */
    public Set<List<String>> getOutVarAttrs(String script) {
        // TODO: Implement attribute path tracking for the new parser
        // This requires tracking field access chains like a.b.c
        return Collections.emptySet();
    }
    
    /**
     * Get external functions (those that must be provided via context) referenced by the script.
     *
     * @param script the script content
     * @return names of external functions referenced in the script
     */
    public Set<String> getOutFunctions(String script) {
        ProgramNode programNode;
        try {
            programNode = parseToSyntaxTree(script);
        }
        catch (ParseException e) {
            throw new RuntimeException("Failed to parse script", e);
        }

        FunctionExtractor extractor = new FunctionExtractor();

        try {
            // Extract function calls with top-level tracking and function definitions
            FunctionExtractor.Context context = extractor.extractWithContext(programNode);
            Set<String> localDefinitions = context.getTopLevelFunctionDefinitions();

            Set<String> outFunctions = new HashSet<>();

            // Filter for direct calls that are:
            // 1. At the top level (not inside function definitions)
            // 2. Not user-defined via addFunction()
            // 3. Not defined locally in the script
            for (FunctionExtractor.FunctionCall call : context.getFunctionCalls()) {
                if (call.getType() == FunctionExtractor.FunctionCallType.DIRECT_CALL && call.isTopLevel()) {
                    String functionName = call.getName();
                    // Check if it's a built-in or user-defined function
                    if (!userDefineFunction.containsKey(functionName)
                            && !localDefinitions.contains(functionName)) {
                        outFunctions.add(functionName);
                    }
                }
            }

            return outFunctions;
        }
        catch (Exception e) {
            throw new RuntimeException("Error extracting external functions", e);
        }
    }
    
    /**
     * Get the expression trace trees for the script without executing it.
     *
     * @param script the script content
     * @return trace trees for each expression
     */
    public List<TracePointTree> getExpressionTracePoints(String script) {
        ProgramNode programNode;
        try {
            programNode = parseToSyntaxTree(script);
        }
        catch (ParseException e) {
            throw new RuntimeException("Failed to parse script", e);
        }
        return ASTCompiler.generateTracePoints(programNode);
    }
    
    /**
     * add user defined global macro to QLExpress engine
     * @param name macro name
     * @param macroScript script for macro
     * @return true if add macro successfully. fail if macro name already exists.
     */
    public boolean addMacro(String name, String macroScript) {
        return globalScope.defineMacroIfAbsent(name, parseMacroDefine(name, macroScript));
    }
    
    /**
     * add or replace user defined global macro to QLExpress engine
     * @param name macro name
     * @param macroScript script for macro
     */
    public void addOrReplaceMacro(String name, String macroScript) {
        globalScope.defineMacro(name, parseMacroDefine(name, macroScript));
    }
    
    /**
     * Parse a macro definition script and create a MacroDefine object.
     * NOTE: This implementation uses the new parser architecture.
     *
     * @param name macro name
     * @param macroScript the macro script content
     * @return MacroDefine object
     */
    private MacroDefine parseMacroDefine(String name, String macroScript) {
        try {
            // Parse the macro script to get AST
            ProgramNode macroProgram = parseToSyntaxTree(macroScript);
            
            // Compile the AST to instructions
            ImportManager importManager = inheritDefaultImport();
            QLambdaDefinition lambdaDefinition = ASTCompiler.compile(macroProgram, operatorManager, importManager, null, null);
            
            // Determine if the last statement is an expression
            List<StatementNode> statements = macroProgram.getStatements();
            boolean lastStmtExpress = !statements.isEmpty()
                && statements.get(statements.size() - 1) instanceof com.alibaba.qlexpress4.parser.ast.ExpressionNode;
            
            // Get instructions from the lambda definition
            List<com.alibaba.qlexpress4.runtime.instruction.QLInstruction> macroInstructions;
            if (lambdaDefinition instanceof QLambdaDefinitionInner) {
                com.alibaba.qlexpress4.runtime.instruction.QLInstruction[] instructionsArray =
                    ((QLambdaDefinitionInner)lambdaDefinition).getInstructions();
                macroInstructions = java.util.Arrays.asList(instructionsArray);
            }
            else {
                throw new RuntimeException("Unexpected lambda definition type: " + lambdaDefinition.getClass());
            }
            
            return new MacroDefine(macroInstructions, lastStmtExpress);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to parse macro definition: " + name, e);
        }
    }
    
    /**
     * add user defined function to QLExpress engine
     * @param name function name
     * @param function function definition
     * @return true if add function successfully. fail if function name already exists.
     */
    public boolean addFunction(String name, CustomFunction function) {
        CustomFunction preFunction = userDefineFunction.putIfAbsent(name, function);
        return preFunction == null;
    }
    
    public <T, R> boolean addFunction(String name, Function<T, R> function) {
        return addFunction(name, (qContext, parameters) -> {
            T t = parameters.size() > 0 ? (T)parameters.get(0).get() : null;
            return function.apply(t);
        });
    }
    
    public boolean addVarArgsFunction(String name, QLFunctionalVarargs functionalVarargs) {
        return addFunction(name, (qContext, parameters) -> {
            Object[] paramArr = new Object[parameters.size()];
            for (int i = 0; i < paramArr.length; i++) {
                paramArr[i] = parameters.get(i).get();
            }
            return functionalVarargs.call(paramArr);
        });
    }
    
    public <T> boolean addFunction(String name, Predicate<T> predicate) {
        return addFunction(name, (qContext, parameters) -> {
            T t = parameters.size() > 0 ? (T)parameters.get(0).get() : null;
            return predicate.test(t);
        });
    }
    
    public boolean addFunction(String name, Runnable runnable) {
        return addFunction(name, (qContext, parameters) -> {
            runnable.run();
            return null;
        });
    }
    
    public <T> boolean addFunction(String name, Consumer<T> consumer) {
        return addFunction(name, (qContext, parameters) -> {
            T t = parameters.size() > 0 ? (T)parameters.get(0).get() : null;
            consumer.accept(t);
            return null;
        });
    }
    
    /**
     * Add a user-defined function backed by a specific Java service instance method.
     *
     * @param name function name exposed in QLExpress scripts
     * @param serviceObject target service instance, must not be {@code null}
     * @param methodName Java method name on the service instance
     * @param parameterClassTypes parameter type signature of the Java method; use an empty array for no-arg methods
     * @return true if the function was added successfully; false if a function with the same name already exists
     * @throws IllegalArgumentException if {@code serviceObject} or {@code methodName} is null, or if no matching
     *                                  public method is found on the service type
     */
    public boolean addFunctionOfServiceMethod(String name, Object serviceObject, String methodName,
        Class<?>[] parameterClassTypes) {
        if (serviceObject == null) {
            throw new IllegalArgumentException("serviceObject must not be null");
        }
        if (methodName == null) {
            throw new IllegalArgumentException("methodName must not be null");
        }
        
        Class<?>[] parameterTypes = parameterClassTypes == null ? new Class<?>[0] : parameterClassTypes;
        Method method;
        try {
            method = serviceObject.getClass().getMethod(methodName, parameterTypes);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No such public method '" + methodName + "' with parameter types "
                + java.util.Arrays.toString(parameterTypes) + " on service object class '"
                + serviceObject.getClass().getName() + "'", e);
        }
        
        return addFunction(name, new QMethodFunction(serviceObject, method));
    }
    
    /**
     * execute `scriptWithFunctionDefine` and add functions defined in script
     * @param scriptWithFunctionDefine script with function define
     * @param context context when execute script
     * @param qlOptions qlOptions when execute script
     * @return succ and fail functions. fail if function name already exists
     */
    public BatchAddFunctionResult addFunctionsDefinedInScript(String scriptWithFunctionDefine, ExpressContext context,
        QLOptions qlOptions) {
        BatchAddFunctionResult batchResult = new BatchAddFunctionResult();
        QLambdaTrace mainLambdaTrace = parseToLambda(scriptWithFunctionDefine, context, qlOptions);
        try {
            Map<String, CustomFunction> functionTableInScript = mainLambdaTrace.getqLambda().getFunctionDefined();
            for (Map.Entry<String, CustomFunction> entry : functionTableInScript.entrySet()) {
                boolean addResult = addFunction(entry.getKey(), entry.getValue());
                (addResult ? batchResult.getSucc() : batchResult.getFail()).add(entry.getKey());
            }
            return batchResult;
        }
        catch (QLException e) {
            throw e;
        }
        catch (Throwable e) {
            // should not run here
            throw new RuntimeException(e);
        }
    }
    
    /**
     * add object member method with annotation {@link com.alibaba.qlexpress4.annotation.QLFunction} as function
     * @param object object with member method with annotation {@link com.alibaba.qlexpress4.annotation.QLFunction}
     * @return succ and fail functions. fail if function name already exists or method is not public
     */
    public BatchAddFunctionResult addObjFunction(Object object) {
        return addFunctionByAnnotation(object.getClass(), object);
    }
    
    /**
     * add class static method with annotation {@link com.alibaba.qlexpress4.annotation.QLFunction} as function
     * @param clazz class with static method with annotation {@link com.alibaba.qlexpress4.annotation.QLFunction}
     * @return succ and fail functions. fail if function name already exists or method is not public
     */
    public BatchAddFunctionResult addStaticFunction(Class<?> clazz) {
        return addFunctionByAnnotation(clazz, null);
    }
    
    private BatchAddFunctionResult addFunctionByAnnotation(Class<?> clazz, Object object) {
        BatchAddFunctionResult result = new BatchAddFunctionResult();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (!BasicUtil.isPublic(method)) {
                result.getFail().add(method.getName());
                continue;
            }
            if (QLFunctionUtil.containsQLFunctionForMethod(method)) {
                for (String functionName : QLFunctionUtil.getQLFunctionValue(method)) {
                    boolean addResult = addFunction(functionName, new QMethodFunction(object, method));
                    (addResult ? result.getSucc() : result.getFail()).add(method.getName());
                }
            }
        }
        return result;
    }
    
    /**
     * add compile time function
     * @param name function name
     * @param compileTimeFunction definition
     * @return true if successful
     * @deprecated Compile-time functions are not yet supported with the new parser
     */
    @Deprecated
    public boolean addCompileTimeFunction(String name, CompileTimeFunction compileTimeFunction) {
        return compileTimeFunctions.putIfAbsent(name, compileTimeFunction) == null;
    }
    
    /**
     * add extension function
     * @param extensionFunction definition of extansion function
     */
    public void addExtendFunction(ExtensionFunction extensionFunction) {
        this.reflectLoader.addExtendFunction(extensionFunction);
    }
    
    /**
     * add an extension function with variable arguments.
     * @param name the name of the extension function
     * @param bindingClass the receiver type (class)
     * @param functionalVarargs custom logic
     */
    public void addExtendFunction(String name, Class<?> bindingClass, QLFunctionalVarargs functionalVarargs) {
        this.reflectLoader.addExtendFunction(new ExtensionFunction() {
            @Override
            public Class<?>[] getParameterTypes() {
                return new Class[] {Object[].class};
            }
            
            @Override
            public boolean isVarArgs() {
                return true;
            }
            
            @Override
            public String getName() {
                return name;
            }
            
            @Override
            public Class<?> getDeclaringClass() {
                return bindingClass;
            }
            
            @Override
            public Object invoke(Object obj, Object[] args)
                throws InvocationTargetException, IllegalAccessException {
                Object[] varArgs = (Object[])args[0];
                Object[] extArgs = new Object[varArgs.length + 1];
                extArgs[0] = obj;
                System.arraycopy(varArgs, 0, extArgs, 1, varArgs.length);
                return functionalVarargs.call(extArgs);
            }
        });
    }
    
    /**
     * Parse script to AST (ProgramNode).
     * <p>
     * This method uses the new hand-written recursive descent parser.
     *
     * @param script the script content to parse
     * @return the parsed AST as a ProgramNode
     * @throws ParseException if parsing fails
     */
    public ProgramNode parseToSyntaxTree(String script)
        throws ParseException {
        return SyntaxTreeFactory.buildTree(script,
            operatorManager,
            initOptions.isDebug(),
            false,
            initOptions.getDebugInfoConsumer(),
            initOptions.getInterpolationMode(),
            initOptions.getSelectorStart(),
            initOptions.getSelectorEnd(),
            initOptions.isStrictNewLines());
    }
    
    /**
     * Check the script for syntax and security violations.
     *
     * @param script the script content to check
     * @param checkOptions validation configuration
     * @throws QLSyntaxException if a violation is detected
     */
    public void check(String script, CheckOptions checkOptions)
        throws QLSyntaxException {
        ProgramNode programNode;
        try {
            programNode = parseToSyntaxTree(script);
        }
        catch (ParseException e) {
            throw com.alibaba.qlexpress4.exception.QLException.reportScannerErr(script,
                0, // position unknown
                e.getLine(),
                e.getColumn(),
                "", // lexeme unknown
                "SYNTAX_ERROR",
                e.getMessage());
        }
        ScriptChecker scriptChecker = new ScriptChecker(checkOptions, script);
        scriptChecker.check(programNode);
    }
    
    /**
     * Check the script with default options.
     *
     * @param script the script content to check
     * @throws QLSyntaxException if a violation is detected
     */
    public void check(String script)
        throws QLSyntaxException {
        check(script, CheckOptions.DEFAULT_OPTIONS);
    }
    
    public QLambdaTrace parseToLambda(String script, ExpressContext context, QLOptions qlOptions) {
        QCompileCache mainLambdaDefine =
            qlOptions.isCache() ? parseToDefinitionWithCache(script) : parseDefinition(script);
        if (initOptions.isDebug()) {
            initOptions.getDebugInfoConsumer().accept("\nInstructions:");
            mainLambdaDefine.getQLambdaDefinition().println(0, initOptions.getDebugInfoConsumer());
        }
        
        QTraces qTraces = initOptions.isTraceExpression() && qlOptions.isTraceExpression()
            ? convertPoints2QTraces(mainLambdaDefine.getExpressionTracePoints())
            : new QTraces(null, null);
        
        QvmRuntime qvmRuntime =
            new QvmRuntime(qTraces, qlOptions.getAttachments(), reflectLoader, System.currentTimeMillis());
        QvmGlobalScope globalScope = new QvmGlobalScope(context, userDefineFunction, qlOptions);
        QLambda qLambda = mainLambdaDefine.getQLambdaDefinition()
            .toLambda(new DelegateQContext(qvmRuntime, globalScope), qlOptions, true);
        return new QLambdaTrace(qLambda, qTraces);
    }
    
    /**
     * parse script with cache
     * @param script script to parse
     * @return QLambdaDefinition and TracePointTrees
     */
    public QCompileCache parseToDefinitionWithCache(String script) {
        try {
            return getParseFuture(script).get();
        }
        catch (Exception e) {
            Throwable compileException = e.getCause();
            throw compileException instanceof QLSyntaxException ? (QLSyntaxException)compileException
                : new RuntimeException(compileException);
        }
    }
    
    public Value loadField(Object object, String fieldName) {
        return reflectLoader
            .loadField(object, fieldName, true, com.alibaba.qlexpress4.exception.PureErrReporter.INSTANCE);
    }
    
    /**
     * Clear the compilation cache.
     * This method clears the cache that stores compiled scripts for performance optimization.
     * When the cache is cleared, subsequent script executions will need to recompile the scripts,
     * which may temporarily impact performance until the cache is rebuilt.
     */
    public void clearCompileCache() {
        compileCache.clear();
    }
    
    private Future<QCompileCache> getParseFuture(String script) {
        Future<QCompileCache> parseFuture = compileCache.get(script);
        if (parseFuture != null) {
            return parseFuture;
        }
        FutureTask<QCompileCache> parseTask = new FutureTask<>(() -> parseDefinition(script));
        Future<QCompileCache> preTask = compileCache.putIfAbsent(script, parseTask);
        if (preTask == null) {
            parseTask.run();
            return parseTask;
        }
        return preTask;
    }
    
    private QCompileCache parseDefinition(String script) {
        try {
            ProgramNode program = parseToSyntaxTree(script);
            ImportManager importManager = inheritDefaultImport();

            if (initOptions.isTraceExpression()) {
                ASTCompiler.CompilationResult result =
                    ASTCompiler.compileWithTrace(program, operatorManager, importManager, globalScope, script);
                return new QCompileCache(result.getLambdaDefinition(), result.getTracePoints());
            }
            else {
                QLambdaDefinition lambdaDefinition = ASTCompiler.compile(program, operatorManager, importManager, globalScope, script);
                return new QCompileCache(lambdaDefinition, Collections.emptyList());
            }
        }
        catch (ParseException e) {
            // Convert parser exception to QLSyntaxException with proper diagnostic
            throw com.alibaba.qlexpress4.exception.QLException.reportScannerErr(script,
                0, // position unknown
                e.getLine(),
                e.getColumn(),
                "", // lexeme unknown
                "SYNTAX_ERROR",
                e.getMessage());
        }
        catch (QLSyntaxException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to parse script", e);
        }
    }
    
    private ImportManager inheritDefaultImport() {
        return new ImportManager(initOptions.getClassSupplier(), initOptions.getDefaultImport());
    }
    
    public <T, U, R> boolean addOperatorBiFunction(String operator, BiFunction<T, U, R> biFunction) {
        return operatorManager.addBinaryOperator(operator,
            (left, right) -> biFunction.apply((T)left.get(), (U)right.get()),
            QLPrecedences.MULTI);
    }
    
    public boolean addOperator(String operator, QLFunctionalVarargs functionalVarargs) {
        return addOperator(operator, (left, right) -> functionalVarargs.call(left.get(), right.get()));
    }
    
    /**
     * add operator with multi precedences
     * @param operator operator name
     * @param customBinaryOperator operator implement
     * @return true if add operator successfully; false if operator already exist
     */
    public boolean addOperator(String operator, CustomBinaryOperator customBinaryOperator) {
        return operatorManager.addBinaryOperator(operator, customBinaryOperator, QLPrecedences.MULTI);
    }
    
    /**
     * add operator
     * @param operator operator name
     * @param customBinaryOperator operator implement
     * @param precedence precedence, see {@link QLPrecedences}
     * @return true if add operator successfully; false if operator already exist
     */
    public boolean addOperator(String operator, CustomBinaryOperator customBinaryOperator, int precedence) {
        return operatorManager.addBinaryOperator(operator, customBinaryOperator, precedence);
    }
    
    /**
     * @param operator operator name
     * @param customBinaryOperator operator implement
     * @return true if replace operator successfully; false if default operator not exists
     */
    public boolean replaceDefaultOperator(String operator, CustomBinaryOperator customBinaryOperator) {
        return operatorManager.replaceDefaultOperator(operator, customBinaryOperator);
    }
    
    /**
     * add alias for keyWord, operator and function
     * @param alias must be a valid id
     * @param originToken key word in qlexpress
     * @return true if add alias successfully
     */
    public boolean addAlias(String alias, String originToken) {
        boolean addKeyWordAliasResult = operatorManager.addKeyWordAlias(alias, originToken);
        boolean addOperatorAliasResult = operatorManager.addOperatorAlias(alias, originToken);
        boolean addFunctionAliasResult = addFunctionAlias(alias, originToken);
        
        return addKeyWordAliasResult || addOperatorAliasResult || addFunctionAliasResult;
    }
    
    private boolean addFunctionAlias(String alias, String originToken) {
        CustomFunction customFunction = userDefineFunction.get(originToken);
        if (customFunction != null) {
            return userDefineFunction.putIfAbsent(alias, customFunction) == null;
        }
        return false;
    }
}
