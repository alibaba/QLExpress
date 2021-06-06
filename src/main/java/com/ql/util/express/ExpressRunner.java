package com.ql.util.express;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ql.util.express.config.QLExpressTimer;
import com.ql.util.express.exception.QLCompileException;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.exception.QLTimeOutException;
import com.ql.util.express.instruction.op.*;
import com.ql.util.express.parse.*;
import com.ql.util.express.rule.Condition;
import com.ql.util.express.rule.Rule;
import com.ql.util.express.rule.RuleManager;
import com.ql.util.express.rule.RuleResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.instruction.ForRelBreakContinue;
import com.ql.util.express.instruction.IOperateDataCache;
import com.ql.util.express.instruction.InstructionFactory;
import com.ql.util.express.instruction.OperateDataCacheImpl;

/**
 * Entry class for grammatical analysis and calculation
 * @author xuannan
 *
 */
public class ExpressRunner {

	private static final Log log = LogFactory.getLog(ExpressRunner.class);
	private static final String GLOBAL_DEFINE_NAME="Global Definition";
	/**
	 * Whether to output all trace information, and the log level is also required to be DEBUG level
	 */
	private boolean isTrace = false;

	/**
	 * Whether to use the logic short circuit feature to enhance the efficiency of quality
	 */
	private boolean isShortCircuit = true;

	/**
	 * Do you need high-precision calculations
	 */
	private boolean isPrecise = false;

	/**
	 * Cache of instruction set corresponding to a piece of text
	 */
	private Map<String,InstructionSet> expressInstructionSetCache = new HashMap<String, InstructionSet>();

	/**
	 * Cache of rules corresponding to a piece of text
	 */
	private Map<String,Rule> ruleCache = new HashMap<String, Rule>();

	private ExpressLoader loader;
	private IExpressResourceLoader expressResourceLoader;
	/**
	 * Syntax definition manager
	 */
	private NodeTypeManager manager;
	/**
	 * Operator manager
	 */
	private OperatorFactory operatorManager;
	/**
	 * Syntax analyzer
	 */
	private ExpressParse parse;

	/**
	 * The default package manager for Class lookup
	 */
	ExpressPackage rootExpressPackage = new ExpressPackage(null);

	public AppendingClassMethodManager getAppendingClassMethodManager() {
		return appendingClassMethodManager;
	}

	private AppendingClassMethodManager appendingClassMethodManager;

	public AppendingClassFieldManager getAppendingClassFieldManager() {
		return appendingClassFieldManager;
	}

	private AppendingClassFieldManager appendingClassFieldManager;

	private ThreadLocal<IOperateDataCache> m_OperateDataObjectCache = new ThreadLocal<IOperateDataCache>(){
		protected IOperateDataCache initialValue() {
			return new OperateDataCacheImpl(30);
		}
	};
	public IOperateDataCache getOperateDataCache(){
		return this.m_OperateDataObjectCache.get();
	}

	public ExpressRunner(){
		this(false,false);
	}
	/**
	 *
	 * @param aIsPrecise Do you need high-precision calculation support
	 * @param aIstrace whether to track the process of executing instructions
	 */
	public ExpressRunner(boolean aIsPrecise,boolean aIstrace){
		this(aIsPrecise,aIstrace,new DefaultExpressResourceLoader(),null);
	}
	public ExpressRunner(boolean aIsPrecise,boolean aIstrace,NodeTypeManager aManager){
		this(aIsPrecise,aIstrace,new DefaultExpressResourceLoader(),aManager);
	}
	/**
	 *
	 * @param aIsPrecise Do you need high-precision calculation support
	 * @param aIstrace whether to track the process of executing instructions
	 * @param aExpressResourceLoader expression resource loader
	 */
	public ExpressRunner(boolean aIsPrecise,boolean aIstrace,IExpressResourceLoader aExpressResourceLoader,NodeTypeManager aManager){
		this.isTrace = aIstrace;
		this.isPrecise = aIsPrecise;
		this.expressResourceLoader = aExpressResourceLoader;
		if(aManager == null){
			manager = new NodeTypeManager();
		}else{
			manager = aManager;
		}
		this.operatorManager = new OperatorFactory(this.isPrecise);
		this.loader = new ExpressLoader(this);
		this.parse = new ExpressParse(manager,this.expressResourceLoader,this.isPrecise);
		rootExpressPackage.addPackage("java.lang");
		rootExpressPackage.addPackage("java.util");
		this.addSystemFunctions();
		this.addSystemOperators();
	}

	private void addSystemOperators() {
		try {
			this.addOperator("instanceof", new OperatorInstanceOf("instanceof"));
		}catch (Exception e){
			throw new RuntimeException(e);
		}
	}

	public void addSystemFunctions(){
		this.addFunction("max", new OperatorMinMax("max"));
		this.addFunction("min", new OperatorMinMax("min"));
		this.addFunction("round", new OperatorRound("round"));
		this.addFunction("print", new OperatorPrint("print"));
		this.addFunction("println", new OperatorPrintln("println"));
	}

	/**
	 * Get the grammar definition manager
	 * @return
	 */
	public NodeTypeManager getNodeTypeManager(){
		return this.manager;
	}
	/**
	 * Get operation symbol manager
	 * @return
	 */
	public OperatorFactory getOperatorFactory(){
		return this.operatorManager;
	}
	public IExpressResourceLoader getExpressResourceLoader(){
		return this.expressResourceLoader;
	}
	/**
	 * Add macro definition For example: macro 玄难 {abc(userinfo.userId);}
	 * @param macroName: Xuan Nan
	 * @param express ：abc(userinfo.userId);
	 * @throws Exception
	 */
	public void addMacro(String macroName,String express) throws Exception{
		String macroExpress = "macro "+ macroName +" {" + express + "}";
		this.loader.parseInstructionSet(GLOBAL_DEFINE_NAME,macroExpress);
	}

	/**
	 * Load expressions, but do not execute them, such as some macro definitions or custom functions
	 * @param groupName
	 * @param express
	 * @throws Exception
	 */
	public void loadMutilExpress(String groupName,String express) throws Exception{
		if(groupName == null || groupName.trim().length() ==0){
			groupName = GLOBAL_DEFINE_NAME;
		}
		this.loader.parseInstructionSet(groupName,express);
	}
	/**
	 * Express defined in the loading file
	 * @param expressName
	 * @throws Exception
	 */
	public void loadExpress(String expressName) throws Exception {
		this.loader.loadExpress(expressName);
	}
	/**
	 * Add function definition
	 * @param name function name
	 * @param op corresponding operation implementation class
	 */
	public void addFunction(String name, OperatorBase op) {
		this.operatorManager.addOperator(name, op);
		this.manager.addFunctionName(name);
	};

	/**
	 * Add function to define the method of the extended class
	 * @param name
	 * @param bindingClass
	 * @param op
	 */
	public void addFunctionAndClassMethod(String name,Class<?>bindingClass, OperatorBase op) {
		this.addFunction(name,op);
		this.addClassMethod(name,bindingClass,op);

	};

	/**
	 * Method of adding class
	 * @param field
	 * @param bindingClass
	 * @param op
	 */
	public void addClassField(String field,Class<?>bindingClass,Operator op)
	{
		this.addClassField(field,bindingClass,Object.class,op);
	}

	/**
	 * Method of adding class
	 * @param field
	 * @param bindingClass
	 * @param returnType
	 * @param op
	 */
	public void addClassField(String field,Class<?>bindingClass,Class<?>returnType,Operator op)
	{
		if(this.appendingClassFieldManager==null){
			this.appendingClassFieldManager = new AppendingClassFieldManager();
		}
		this.appendingClassFieldManager.addAppendingField(field, bindingClass,returnType,op);
	}

	/**
	 * Method of adding class
	 * @param name
	 * @param bindingClass
	 * @param op
	 */
	public void addClassMethod(String name,Class<?>bindingClass,OperatorBase op)
	{
		if(this.appendingClassMethodManager==null){
			this.appendingClassMethodManager = new AppendingClassMethodManager();
		}
		this.appendingClassMethodManager.addAppendingMethod(name, bindingClass, op);
	}
	/**
	 * Get the function definition, through the function definition you can get the parameter description information
	 * @param name function name
	 * @return
	 */
	public OperatorBase getFunciton(String name){
		return this.operatorManager.getOperator(name);
	}
	/**
	 * Add a function definition of a class, for example: Math.abs(double) is mapped to "take the absolute value (-5.0)" in the expression
	 * @param name function name
	 * @param aClassName class name
	 * @param aFunctionName method name in the class
	 * @param aParameterClassTypes method parameter type name
	 * @param errorInfo If the result of the function execution is false, the error message that needs to be output
	 * @throws Exception
	 */
	public void addFunctionOfClassMethod(String name, String aClassName,
										 String aFunctionName, Class<?>[] aParameterClassTypes,
										 String errorInfo) throws Exception {
		this.addFunction(name, new OperatorSelfDefineClassFunction(name,
				aClassName, aFunctionName, aParameterClassTypes,null,null, errorInfo));

	}

	/**
	 * Add a function definition of a class, for example: Math.abs(double) is mapped to "take the absolute value (-5.0)" in the expression
	 * @param name function name
	 * @param aClass class
	 * @param aFunctionName method name in the class
	 * @param aParameterClassTypes method parameter type name
	 * @param errorInfo If the result of the function execution is false, the error message that needs to be output
	 * @throws Exception
	 */
	public void addFunctionOfClassMethod(String name, Class<?> aClass,
										 String aFunctionName, Class<?>[] aParameterClassTypes,
										 String errorInfo) throws Exception {
		this.addFunction(name, new OperatorSelfDefineClassFunction(name,
				aClass, aFunctionName, aParameterClassTypes,null,null, errorInfo));

	}

	/**
	 * Add a function definition of a class, for example: Math.abs(double) is mapped to "take absolute value (-5.0)" in the expression
	 * @param name function name
	 * @param aClassName class name
	 * @param aFunctionName method name in the class
	 * @param aParameterClassTypes method parameter type name
	 * @param aParameterDesc method parameter description
	 * @param aParameterAnnotation method parameter annotation
	 * @param errorInfo If the result of the function execution is false, the error message that needs to be output
	 * @throws Exception
	 */
	public void addFunctionOfClassMethod(String name, String aClassName,
										 String aFunctionName, Class<?>[] aParameterClassTypes,
										 String[] aParameterDesc,String[] aParameterAnnotation,
										 String errorInfo) throws Exception {
		this.addFunction(name, new OperatorSelfDefineClassFunction(name,
				aClassName, aFunctionName, aParameterClassTypes,aParameterDesc,aParameterAnnotation, errorInfo));

	}
	/**
	 * Add a function definition of a class, for example: Math.abs(double) is mapped to "take the absolute value (-5.0)" in the expression
	 * @param name function name
	 * @param aClassName class name
	 * @param aFunctionName method name in the class
	 * @param aParameterTypes method parameter type name
	 * @param errorInfo If the result of the function execution is false, the error message that needs to be output
	 * @throws Exception
	 */
	public void addFunctionOfClassMethod(String name, String aClassName,
										 String aFunctionName, String[] aParameterTypes, String errorInfo)
			throws Exception {
		this.addFunction(name, new OperatorSelfDefineClassFunction(name,
				aClassName, aFunctionName, aParameterTypes, null,null,errorInfo));
	}
	/**
	 * Add a function definition of a class, for example: Math.abs(double) is mapped to "take the absolute value (-5.0)" in the expression
	 * @param name function name
	 * @param aClassName class name
	 * @param aFunctionName method name in the class
	 * @param aParameterTypes method parameter type name
	 * @param aParameterDesc method parameter description
	 * @param aParameterAnnotation method parameter annotation
	 * @param errorInfo If the result of the function execution is false, the error message that needs to be output
	 * @throws Exception
	 */
	public void addFunctionOfClassMethod(String name, String aClassName,
										 String aFunctionName, String[] aParameterTypes,
										 String[] aParameterDesc,String[] aParameterAnnotation,
										 String errorInfo)
			throws Exception {
		this.addFunction(name, new OperatorSelfDefineClassFunction(name,
				aClassName, aFunctionName, aParameterTypes, aParameterDesc,aParameterAnnotation,errorInfo));

	}
	/**
	 * Used to convert a user-defined object (such as a Spring object) method into an expression calculation function
	 * @param name
	 * @param aServiceObject
	 * @param aFunctionName
	 * @param aParameterClassTypes
	 * @param errorInfo
	 * @throws Exception
	 */
	public void addFunctionOfServiceMethod(String name, Object aServiceObject,
										   String aFunctionName, Class<?>[] aParameterClassTypes,
										   String errorInfo) throws Exception {
		this.addFunction(name, new OperatorSelfDefineServiceFunction(name,
				aServiceObject, aFunctionName, aParameterClassTypes,null,null, errorInfo));

	}
	/**
	 * Used to convert a user-defined object (such as a Spring object) method into an expression calculation function
	 * @param name
	 * @param aServiceObject
	 * @param aFunctionName
	 * @param aParameterClassTypes
	 * @param aParameterDesc method parameter description
	 * @param aParameterAnnotation method parameter annotation
	 * @param errorInfo
	 * @throws Exception
	 */
	public void addFunctionOfServiceMethod(String name, Object aServiceObject,
										   String aFunctionName, Class<?>[] aParameterClassTypes,
										   String[] aParameterDesc,String[] aParameterAnnotation,
										   String errorInfo) throws Exception {
		this.addFunction(name, new OperatorSelfDefineServiceFunction(name,
				aServiceObject, aFunctionName, aParameterClassTypes,aParameterDesc,aParameterAnnotation, errorInfo));

	}
	/**
	 * Used to convert a user-defined object (such as a Spring object) method into an expression calculation function
	 * @param name
	 * @param aServiceObject
	 * @param aFunctionName
	 * @param aParameterTypes
	 * @param errorInfo
	 * @throws Exception
	 */
	public void addFunctionOfServiceMethod(String name, Object aServiceObject,
										   String aFunctionName, String[] aParameterTypes, String errorInfo)
			throws Exception {
		this.addFunction(name, new OperatorSelfDefineServiceFunction(name,
				aServiceObject, aFunctionName, aParameterTypes,null,null, errorInfo));

	}
	public void addFunctionOfServiceMethod(String name, Object aServiceObject,
										   String aFunctionName, String[] aParameterTypes,
										   String[] aParameterDesc,String[] aParameterAnnotation,
										   String errorInfo)
			throws Exception {
		this.addFunction(name, new OperatorSelfDefineServiceFunction(name,
				aServiceObject, aFunctionName, aParameterTypes,aParameterDesc,aParameterAnnotation, errorInfo));

	}
	/**
	 * Add operation symbol, the priority of this operation symbol is the same as "*", the syntax form is also data name data
	 * @param name
	 * @param op
	 * @throws Exception
	 */
	public void addOperator(String name,Operator op) throws Exception {
		this.addOperator(name, "*", op);
	}
	/**
	 * Add operation symbol, this operation symbol is consistent with the given reference operation symbol in priority and grammatical form
	 * @param name operation symbol name
	 * @param aRefOpername refers to the operation symbol, such as "+", "--", etc.
	 * @param op
	 * @throws Exception
	 */
	public void addOperator(String name,String aRefOpername,Operator op) throws Exception {
		this.manager.addOperatorWithLevelOfReference(name, aRefOpername);
		this.operatorManager.addOperator(name, op);
	}

	/**
	 * Add aliases for operators and keywords, and specify error messages for operators.
	 * For example: addOperatorWithAlias("加","+",null)
	 * @param keyWordName
	 * @param realKeyWordName
	 * @param errorInfo
	 * @throws Exception
	 */
	public void addOperatorWithAlias(String keyWordName, String realKeyWordName,
									 String errorInfo) throws Exception {
		if(errorInfo != null && errorInfo.trim().length() == 0){
			errorInfo = null;
		}
//Add function alias
		if(this.manager.isFunction(realKeyWordName)){
			this.manager.addFunctionName(keyWordName);
			this.operatorManager.addOperatorWithAlias(keyWordName, realKeyWordName, errorInfo);
			return;
		}
		NodeType realNodeType = this.manager.findNodeType(realKeyWordName);
		if(realNodeType == null){
			throw new QLException("Keyword:" + realKeyWordName + "does not exist");
		}
		boolean isExist = this.operatorManager.isExistOperator(realNodeType.getName());
		if(isExist == false && errorInfo != null){
			throw new QLException("Keyword:" + realKeyWordName +" is implemented by instructions, you cannot set error prompt information, errorInfo must be null");
		}
		if(isExist == false || errorInfo == null){
//No need to add operation symbols, just create a key sub
			this.manager.addOperatorWithRealNodeType(keyWordName, realNodeType.getName());
		}else{
			this.manager.addOperatorWithLevelOfReference(keyWordName, realNodeType.getName());
			this.operatorManager.addOperatorWithAlias(keyWordName, realNodeType.getName(), errorInfo);
		}
	}
	/**
	 * Replacement operator processing
	 * @param name
	 */
	public OperatorBase replaceOperator(String name,OperatorBase op){
		return this.operatorManager.replaceOperator(name, op);
	}

	public ExpressPackage getRootExpressPackage(){
		return this.rootExpressPackage;
	}
	/**
	 * clear cache
	 */
	public void clearExpressCache() {
		synchronized (expressInstructionSetCache) {
			this.expressInstructionSetCache.clear();
		}
	}
	/**
	 * Execute according to the name of the expression
	 * @param name
	 * @param context
	 * @param errorList
	 * @param isTrace
	 * @param isCatchException
	 * @param aLog
	 * @return
	 * @throws Exception
	 */
	public Object executeByExpressName(String name,IExpressContext<String,Object> context, List<String> errorList,
									   boolean isTrace,boolean isCatchException, Log aLog) throws Exception {
		return InstructionSetRunner.executeOuter(this,this.loader.getInstructionSet(name),this.loader,context, errorList,
				isTrace,isCatchException,aLog,false);

	}

	/**
	 * Execute instruction set (compatible with the old interface, please do not manage the instruction cache yourself, use execute(InstructionSet instructionSets,...) directly
	 * To clear the cache, use the clearExpressCache() function
	 * @param instructionSets
	 * @param context
	 * @param errorList
	 * @param isTrace
	 * @param isCatchException
	 * @param aLog
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public Object execute(InstructionSet[] instructionSets,IExpressContext<String,Object> context, List<String> errorList,
						  boolean isTrace,boolean isCatchException, Log aLog) throws Exception {
		return InstructionSetRunner.executeOuter(this,instructionSets[0],this.loader,context, errorList,
				isTrace,isCatchException,aLog,false);
	}

	/**
	 * Execute instruction set
	 * @param instructionSets
	 * @param context
	 * @param errorList
	 * @param isTrace
	 * @param isCatchException
	 * @param aLog
	 * @return
	 * @throws Exception
	 */
	public Object execute(InstructionSet instructionSets,IExpressContext<String,Object> context, List<String> errorList,
						  boolean isTrace,boolean isCatchException, Log aLog) throws Exception {
		return InstructionSetRunner.executeOuter(this,instructionSets,this.loader,context, errorList,
				isTrace,isCatchException,aLog,false);
	}
	/**
	 * Execute a text
	 * @param expressString program text
	 * @param context execution context
	 * @param errorList List of error messages output
	 * @param isCache Whether to use the instruction set in Cache
	 * @param isTrace Whether to output detailed execution instruction information
	 * @param timeoutMillis timeout milliseconds
	 * @return
	 * @throws Exception
	 */
	public Object execute(String expressString, IExpressContext<String,Object> context,
						  List<String> errorList, boolean isCache, boolean isTrace,long timeoutMillis) throws Exception {
//Set the timeout in milliseconds
		QLExpressTimer.setTimer(timeoutMillis);
		try {
			return this.execute(expressString, context, errorList, isCache, isTrace, null);
		}finally {
			QLExpressTimer.reset();
		}
	}

	/**
	 * Execute a text
	 * @param expressString program text
	 * @param context execution context
	 * @param errorList List of error messages output
	 * @param isCache Whether to use the instruction set in Cache
	 * @param isTrace Whether to output detailed execution instruction information
	 * @return
	 * @throws Exception
	 */
	public Object execute(String expressString, IExpressContext<String,Object> context,
						  List<String> errorList, boolean isCache, boolean isTrace) throws Exception {
		return this.execute(expressString, context, errorList, isCache, isTrace, null);
	}
	/**
	 * Execute a text
	 * @param expressString program text
	 * @param context execution context
	 * @param errorList List of error messages output
	 * @param isCache Whether to use the instruction set in Cache
	 * @param isTrace Whether to output detailed execution instruction information
	 * @param aLog output log
	 * @return
	 * @throws Exception
	 */
	public Object execute(String expressString, IExpressContext<String,Object> context,
						  List<String> errorList, boolean isCache, boolean isTrace, Log aLog)
			throws Exception {
		InstructionSet parseResult = null;
		if (isCache == true) {
			parseResult = expressInstructionSetCache.get(expressString);
			if (parseResult == null) {
				synchronized (expressInstructionSetCache) {
					parseResult = expressInstructionSetCache.get(expressString);
					if (parseResult == null) {
						parseResult = this.parseInstructionSet(expressString);
						expressInstructionSetCache.put(expressString,
								parseResult);
					}
				}
			}
		} else {
			parseResult = this.parseInstructionSet(expressString);
		}
		return InstructionSetRunner.executeOuter(this,parseResult,this.loader,context, errorList,
				isTrace,false,aLog,false);
	}

	public RuleResult executeRule(String expressString, IExpressContext<String,Object> context, boolean isCache, boolean isTrace)
			throws Exception {
		Rule rule = null;
		if (isCache == true) {
			rule = ruleCache.get(expressString);
			if (rule == null) {
				synchronized (ruleCache) {
					rule = ruleCache.get(expressString);
					if (rule == null) {
						rule = this.parseRule(expressString);
						ruleCache.put(expressString,
								rule);
					}
				}
			}
		} else {
			rule = this.parseRule(expressString);
		}
		return RuleManager.executeRule(this,rule,context,isCache,isTrace);
	}

	static Pattern patternRule = Pattern.compile("rule[\\s]+'([^']+)'[\\s]+name[\\s]+'([^']+)'[\\ s]+");

	public Rule parseRule(String text)
			throws Exception {
		String ruleName = null;
		String ruleCode = null;
		Matcher matcher = patternRule.matcher(text);
		if(matcher.find()) {
			ruleCode = matcher.group(1);
			ruleName = matcher.group(2);
			text = text.substring(matcher.end());
		}

		Map<String,String> selfDefineClass = new HashMap<String,String> ();
		for(ExportItem item: this.loader.getExportInfo()){
			if(item.getType().equals(InstructionSet.TYPE_CLASS)){
				selfDefineClass.put(item.getName(), item.getName());
			}
		}

// Divide into two sentences to execute to save the words results
// ExpressNode root = this.parse.parse(this.rootExpressPackage,text, isTrace,selfDefineClass);

		Word[] words = this.parse.splitWords(rootExpressPackage,text,isTrace,selfDefineClass);
		ExpressNode root = this.parse.parse(rootExpressPackage,words,text,isTrace,selfDefineClass);
		Rule rule = RuleManager.createRule(root,words);
		rule.setCode(ruleCode);
		rule.setName(ruleName);
		return rule;
	}

	public Condition parseContition(String text)
			throws Exception {

		Map<String,String> selfDefineClass = new HashMap<String,String> ();
		for(ExportItem item: this.loader.getExportInfo()){
			if(item.getType().equals(InstructionSet.TYPE_CLASS)){
				selfDefineClass.put(item.getName(), item.getName());
			}
		}

		Word[] words = this.parse.splitWords(rootExpressPackage,text,isTrace,selfDefineClass);
		ExpressNode root = this.parse.parse(rootExpressPackage,words,text,isTrace,selfDefineClass);
		return RuleManager.createCondition(root,words);
	}

	/**
	 * Parse a piece of text and generate a set of instructions
	 * @param text
	 * @return
	 * @throws Exception
	 */
	public InstructionSet parseInstructionSet(String text)
			throws Exception {
		try {
			Map<String, String> selfDefineClass = new HashMap<String, String>();
			for (ExportItem item: this.loader.getExportInfo()) {
				if (item.getType().equals(InstructionSet.TYPE_CLASS)) {
					selfDefineClass.put(item.getName(), item.getName());
				}
			}

			ExpressNode root = this.parse.parse(this.rootExpressPackage, text, isTrace, selfDefineClass);
			InstructionSet result = createInstructionSet(root, "main");
			if (this.isTrace && log.isDebugEnabled()) {
				log.debug(result);
			}
			return result;
		}catch (QLCompileException e){
			throw e;
		}catch (Exception e){
			throw new QLCompileException("Compile exception:\n"+text,e);
		}
	}
	/**
	 * Output global definition information
	 * @return
	 */
	public ExportItem[] getExportInfo(){
		return this.loader.getExportInfo();
	}

	/**
	 * First obtain the instruction set from the local instruction set cache, if not, generate it and cache it locally
	 * @param expressString
	 * @return
	 * @throws Exception
	 */
	public InstructionSet getInstructionSetFromLocalCache(String expressString)
			throws Exception {
		InstructionSet parseResult = expressInstructionSetCache.get(expressString);
		if (parseResult == null) {
			synchronized (expressInstructionSetCache) {
				parseResult = expressInstructionSetCache.get(expressString);
				if (parseResult == null) {
					parseResult = this.parseInstructionSet(expressString);
					expressInstructionSetCache.put(expressString,
							parseResult);
				}
			}
		}
		return parseResult;
	}

	public InstructionSet createInstructionSet(ExpressNode root, String type)
			throws Exception {
		InstructionSet result = new InstructionSet(type);
		createInstructionSet(root, result);
		return result;
	}

	public void createInstructionSet(ExpressNode root, InstructionSet result)
			throws Exception {
		Stack<ForRelBreakContinue> forStack = new Stack<ForRelBreakContinue>();
		createInstructionSetPrivate(result, forStack, root, true);
		if (forStack.size()> 0) {
			throw new QLCompileException("For processing error");
		}
	}

	public boolean createInstructionSetPrivate(InstructionSet result,
											   Stack<ForRelBreakContinue> forStack, ExpressNode node,
											   boolean isRoot) throws Exception {
		InstructionFactory factory = InstructionFactory
				.getInstructionFactory(node.getInstructionFactory());
		boolean hasLocalVar = factory.createInstruction(this,result, forStack, node, isRoot);
		return hasLocalVar;
	}
	/**
	 * Get a list of external variable names required by an expression
	 * @param express
	 * @return
	 * @throws Exception
	 */
	public String[] getOutVarNames(String express) throws Exception{
		return this.parseInstructionSet(express).getOutAttrNames();
	}

	public String[] getOutFunctionNames(String express) throws Exception{
		return this.parseInstructionSet(express).getOutFunctionNames();
	}


	public boolean isShortCircuit() {
		return isShortCircuit;
	}
	public void setShortCircuit(boolean isShortCircuit) {
		this.isShortCircuit = isShortCircuit;
	}

	/**
	 * Whether to ignore the charset type data and recognize it as a string, such as'a'-"a"
	 * The default is not to ignore, normally recognized as String
	 */
	public boolean isIgnoreConstChar() {
		return this.parse.isIgnoreConstChar();
	}
	public void setIgnoreConstChar(boolean ignoreConstChar) {
		this.parse.setIgnoreConstChar(ignoreConstChar);
	}

	/**
	 * Provide short-answer syntax check to ensure that the local environment can be compiled into instructions during runtime
	 * @param text
	 * @return
	 */
	public boolean checkSyntax(String text)
	{
		return checkSyntax(text,false,null);
	}

	/**
	 * Provides complex syntax checking, (such as checking custom java classes), does not guarantee that the runtime can be compiled into instructions in the local environment
	 * @param text
	 * @param mockRemoteJavaClass
	 * @param remoteJavaClassNames
	 * @return
	 */
	public boolean checkSyntax(String text,boolean mockRemoteJavaClass,List<String> remoteJavaClassNames){

		try {
			Map<String, String> selfDefineClass = new HashMap<String, String>();
			for (ExportItem item: this.loader.getExportInfo()) {
				if (item.getType().equals(InstructionSet.TYPE_CLASS)) {
					selfDefineClass.put(item.getName(), item.getName());
				}
			}
			Word[] words = this.parse.splitWords(rootExpressPackage,text,isTrace,selfDefineClass);
			ExpressNode root = this.parse.parse(this.rootExpressPackage, words,text, isTrace, selfDefineClass,mockRemoteJavaClass);
			InstructionSet result = createInstructionSet(root, "main");
			if (this.isTrace && log.isDebugEnabled()) {
				log.debug(result);
			}
			if(mockRemoteJavaClass && remoteJavaClassNames!=null) {
				remoteJavaClassNames.addAll(Arrays.asList(result.getVirClasses()));
			}
			return true;
		}catch (Exception e){
			log.error("checkSyntax has Exception",e);
			return false;
		}
	}
}
