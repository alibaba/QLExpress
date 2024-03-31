package com.ql.util.express;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.*;

import com.ql.util.express.config.QLExpressTimer;
import com.ql.util.express.exception.QLCompileException;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.ForRelBreakContinue;
import com.ql.util.express.instruction.IOperateDataCache;
import com.ql.util.express.instruction.InstructionFactory;
import com.ql.util.express.instruction.OperateDataCacheImpl;
import com.ql.util.express.instruction.op.OperatorBase;
import com.ql.util.express.instruction.op.OperatorFactory;
import com.ql.util.express.instruction.op.OperatorInstanceOf;
import com.ql.util.express.instruction.op.OperatorMinMax;
import com.ql.util.express.instruction.op.OperatorPrint;
import com.ql.util.express.instruction.op.OperatorPrintln;
import com.ql.util.express.instruction.op.OperatorRound;
import com.ql.util.express.instruction.op.OperatorSelfDefineClassFunction;
import com.ql.util.express.instruction.op.OperatorSelfDefineServiceFunction;
import com.ql.util.express.parse.AppendingClassFieldManager;
import com.ql.util.express.parse.AppendingClassMethodManager;
import com.ql.util.express.parse.ExpressNode;
import com.ql.util.express.parse.ExpressPackage;
import com.ql.util.express.parse.ExpressParse;
import com.ql.util.express.parse.NodeType;
import com.ql.util.express.parse.NodeTypeManager;
import com.ql.util.express.parse.Word;

/**
 * 语法分析和计算的入口类
 *
 * @author xuannan
 */
public class ExpressRunner {

    private static final String GLOBAL_DEFINE_NAME = "全局定义";

    /**
     * 是否输出所有的跟踪信息，同时还需要log级别是DEBUG级别
     */
    private final boolean isTrace;

    /**
     * 是否使用逻辑短路特性增强质量的效率
     */
    private boolean isShortCircuit = true;

    /**
     * 是否需要高精度计算
     */
    private final boolean isPrecise;

    /**
     * 一段文本对应的指令集的缓存
     * default: ConcurrentHashMap with no eviction policy
     */
    private final Map<String, Future<InstructionSet>> expressInstructionSetCache;

    private final ExpressLoader loader;

    private final IExpressResourceLoader expressResourceLoader;

    /**
     * 语法定义的管理器
     */
    private final NodeTypeManager manager;

    /**
     * 操作符的管理器
     */
    private final OperatorFactory operatorManager;

    /**
     * 语法分析器
     */
    private final ExpressParse parse;

    /**
     * 缺省的Class查找的包管理器
     */
    final ExpressPackage rootExpressPackage = new ExpressPackage(null);

    /**
     * 线程重入次数
     */
    private final ThreadLocal<Integer> threadReentrantCount = ThreadLocal.withInitial(() -> 0);

    public AppendingClassMethodManager getAppendingClassMethodManager() {
        return appendingClassMethodManager;
    }

    private AppendingClassMethodManager appendingClassMethodManager;

    public AppendingClassFieldManager getAppendingClassFieldManager() {
        return appendingClassFieldManager;
    }

    private AppendingClassFieldManager appendingClassFieldManager;

    private final ThreadLocal<IOperateDataCache> operateDataCacheThreadLocal = ThreadLocal.withInitial(
        () -> new OperateDataCacheImpl(30));

    public IOperateDataCache getOperateDataCache() {
        return this.operateDataCacheThreadLocal.get();
    }

    public ExpressRunner() {
        this(false, false);
    }

    /**
     * @param isPrecise 是否需要高精度计算支持
     * @param isTrace   是否跟踪执行指令的过程
     */
    public ExpressRunner(boolean isPrecise, boolean isTrace) {
        this(isPrecise, isTrace, new DefaultExpressResourceLoader(), null);
    }

    /**
     * @param isPrecise
     * @param isTrace
     * @param cacheMap  user can define safe and efficient cache or use default concurrentMap
     */
    public ExpressRunner(boolean isPrecise, boolean isTrace,
                         Map<String, Future<InstructionSet>> cacheMap) {
        this(isPrecise, isTrace, new DefaultExpressResourceLoader(), null, cacheMap);
    }

    public ExpressRunner(boolean isPrecise, boolean isStrace, NodeTypeManager nodeTypeManager) {
        this(isPrecise, isStrace, new DefaultExpressResourceLoader(), nodeTypeManager);
    }

    /**
     * @param isPrecise              是否需要高精度计算支持
     * @param isTrace                是否跟踪执行指令的过程
     * @param iExpressResourceLoader 表达式的资源装载器
     */
    public ExpressRunner(boolean isPrecise, boolean isTrace, IExpressResourceLoader iExpressResourceLoader,
                         NodeTypeManager nodeTypeManager) {
        this(isPrecise, isTrace, iExpressResourceLoader,
                nodeTypeManager, null);
    }

    /**
     * @param isPrecise              是否需要高精度计算支持
     * @param isTrace                是否跟踪执行指令的过程
     * @param iExpressResourceLoader 表达式的资源装载器
     * @param cacheMap               指令集缓存, 必须是线程安全的集合
     */
    public ExpressRunner(boolean isPrecise, boolean isTrace, IExpressResourceLoader iExpressResourceLoader,
                         NodeTypeManager nodeTypeManager, Map<String, Future<InstructionSet>> cacheMap) {
        this.isTrace = isTrace;
        this.isPrecise = isPrecise;
        this.expressResourceLoader = iExpressResourceLoader;
        if (nodeTypeManager == null) {
            manager = new NodeTypeManager();
        } else {
            manager = nodeTypeManager;
        }

        if (Objects.isNull(cacheMap)) {
            expressInstructionSetCache = new ConcurrentHashMap<>();
        } else {
            expressInstructionSetCache = cacheMap;
        }
        this.operatorManager = new OperatorFactory(this.isPrecise);
        this.loader = new ExpressLoader(this);
        this.parse = new ExpressParse(manager, this.expressResourceLoader, this.isPrecise);
        rootExpressPackage.addPackage("java.lang");
        rootExpressPackage.addPackage("java.util");

        // 默认引入 java8 stream api, jdk 版本低于 8 也不会有影响, 因为是运行时动态取的
        rootExpressPackage.addPackage("java.util.stream");
        this.addSystemFunctions();
        this.addSystemOperators();
    }

    private void addSystemOperators() {
        try {
            this.addOperator("instanceof", new OperatorInstanceOf("instanceof"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addSystemFunctions() {
        this.addFunction("max", new OperatorMinMax("max"));
        this.addFunction("min", new OperatorMinMax("min"));
        this.addFunction("round", new OperatorRound("round"));
        this.addFunction("print", new OperatorPrint("print"));
        this.addFunction("println", new OperatorPrintln("println"));
    }

    /**
     * 获取语法定义的管理器
     *
     * @return
     */
    public NodeTypeManager getNodeTypeManager() {
        return this.manager;
    }

    /**
     * 获取操作符号管理器
     *
     * @return
     */
    public OperatorFactory getOperatorFactory() {
        return this.operatorManager;
    }

    public IExpressResourceLoader getExpressResourceLoader() {
        return this.expressResourceLoader;
    }

    /**
     * 添加宏定义
     * 例如： macro 宏名称 { abc(userInfo.userId);}
     *
     * @param macroName 宏名称
     * @param express   表达式，示例：abc(userInfo.userId);
     * @throws Exception
     */
    public void addMacro(String macroName, String express) throws Exception {
        String macroExpress = "macro " + macroName + " {" + express + "}";
        this.loader.parseInstructionSet(GLOBAL_DEFINE_NAME, macroExpress);
    }

    /**
     * 装载表达式，但不执行，例如一些宏定义，或者自定义函数
     *
     * @param expressName
     * @param express
     * @throws Exception
     */
    public void loadMultiExpress(String expressName, String express) throws Exception {
        if (expressName == null || expressName.trim().length() == 0) {
            expressName = GLOBAL_DEFINE_NAME;
        }
        this.loader.parseInstructionSet(expressName, express);
    }

    /**
     * 装载文件中定义的Express
     *
     * @param expressName
     * @throws Exception
     */
    public void loadExpress(String expressName) throws Exception {
        this.loader.loadExpress(expressName);
    }

    /**
     * 添加函数定义
     *
     * @param name 函数名称
     * @param op   对应的操作实现类
     */
    public void addFunction(String name, OperatorBase op) {
        this.operatorManager.addOperator(name, op);
        this.manager.addFunctionName(name);
    }

    /**
     * 添加函数定义扩展类的方法
     *
     * @param name
     * @param bindingClass
     * @param op
     */
    public void addFunctionAndClassMethod(String name, Class<?> bindingClass, OperatorBase op) {
        this.addFunction(name, op);
        this.addClassMethod(name, bindingClass, op);
    }

    /**
     * 添加类的方法
     *
     * @param field
     * @param bindingClass
     * @param op
     */
    public void addClassField(String field, Class<?> bindingClass, Operator op) {
        this.addClassField(field, bindingClass, Object.class, op);
    }

    /**
     * 添加类的方法
     *
     * @param field
     * @param bindingClass
     * @param returnType
     * @param op
     */
    public void addClassField(String field, Class<?> bindingClass, Class<?> returnType, Operator op) {
        if (this.appendingClassFieldManager == null) {
            this.appendingClassFieldManager = new AppendingClassFieldManager();
        }
        this.appendingClassFieldManager.addAppendingField(field, bindingClass, returnType, op);
    }

    /**
     * 添加类的方法
     *
     * @param name
     * @param bindingClass
     * @param op
     */
    public void addClassMethod(String name, Class<?> bindingClass, OperatorBase op) {
        if (this.appendingClassMethodManager == null) {
            this.appendingClassMethodManager = new AppendingClassMethodManager();
        }
        this.appendingClassMethodManager.addAppendingMethod(name, bindingClass, op);
    }

    /**
     * 获取函数定义，通过函数定义可以拿到参数的说明信息
     *
     * @param name 函数名称
     * @return
     */
    public OperatorBase getFunction(String name) {
        return this.operatorManager.getOperator(name);
    }

    /**
     * 添加一个类的函数定义，例如：Math.abs(double) 映射为表达式中的 "取绝对值(-5.0)"
     *
     * @param name                函数名称
     * @param className           类名称
     * @param functionName        类中的方法名称
     * @param parameterClassTypes 方法的参数类型名称
     * @param errorInfo           如果函数执行的结果是false，需要输出的错误信息
     * @throws Exception
     */
    public void addFunctionOfClassMethod(String name, String className, String functionName,
        Class<?>[] parameterClassTypes, String errorInfo) throws Exception {
        OperatorSelfDefineClassFunction operatorSelfDefineClassFunction = new OperatorSelfDefineClassFunction(name,
            className, functionName, parameterClassTypes, null, null, errorInfo);
        this.addFunction(name, operatorSelfDefineClassFunction);
    }

    /**
     * 添加一个类的函数定义，例如：Math.abs(double) 映射为表达式中的 "取绝对值(-5.0)"
     *
     * @param name                函数名称
     * @param clazz               类
     * @param functionName        类中的方法名称
     * @param parameterClassTypes 方法的参数类型名称
     * @param errorInfo           如果函数执行的结果是false，需要输出的错误信息
     * @throws Exception
     */
    public void addFunctionOfClassMethod(String name, Class<?> clazz, String functionName,
        Class<?>[] parameterClassTypes, String errorInfo) throws Exception {
        OperatorSelfDefineClassFunction operatorSelfDefineClassFunction = new OperatorSelfDefineClassFunction(name,
            clazz, functionName, parameterClassTypes, null, null, errorInfo);
        this.addFunction(name, operatorSelfDefineClassFunction);
    }

    /**
     * 添加一个类的函数定义，例如：Math.abs(double) 映射为表达式中的 "取绝对值(-5.0)"
     *
     * @param name                函数名称
     * @param className           类名称
     * @param functionName        类中的方法名称
     * @param parameterClassTypes 方法的参数类型名称
     * @param parameterDesc       方法的参数说明
     * @param parameterAnnotation 方法的参数注解
     * @param errorInfo           如果函数执行的结果是false，需要输出的错误信息
     * @throws Exception
     */
    public void addFunctionOfClassMethod(String name, String className, String functionName,
        Class<?>[] parameterClassTypes, String[] parameterDesc, String[] parameterAnnotation, String errorInfo)
        throws Exception {
        OperatorSelfDefineClassFunction operatorSelfDefineClassFunction = new OperatorSelfDefineClassFunction(name,
            className, functionName, parameterClassTypes, parameterDesc, parameterAnnotation, errorInfo);
        this.addFunction(name, operatorSelfDefineClassFunction);
    }

    /**
     * 添加一个类的函数定义，例如：Math.abs(double) 映射为表达式中的 "取绝对值(-5.0)"
     *
     * @param name           函数名称
     * @param className      类名称
     * @param functionName   类中的方法名称
     * @param parameterTypes 方法的参数类型名称
     * @param errorInfo      如果函数执行的结果是false，需要输出的错误信息
     * @throws Exception
     */
    public void addFunctionOfClassMethod(String name, String className, String functionName, String[] parameterTypes,
        String errorInfo) throws Exception {
        OperatorSelfDefineClassFunction operatorSelfDefineClassFunction = new OperatorSelfDefineClassFunction(name,
            className, functionName, parameterTypes, null, null, errorInfo);
        this.addFunction(name, operatorSelfDefineClassFunction);
    }

    /**
     * 添加一个类的函数定义，例如：Math.abs(double) 映射为表达式中的 "取绝对值(-5.0)"
     *
     * @param name                函数名称
     * @param className           类名称
     * @param functionName        类中的方法名称
     * @param parameterTypes      方法的参数类型名称
     * @param parameterDesc       方法的参数说明
     * @param parameterAnnotation 方法的参数注解
     * @param errorInfo           如果函数执行的结果是false，需要输出的错误信息
     * @throws Exception
     */
    public void addFunctionOfClassMethod(String name, String className, String functionName, String[] parameterTypes,
        String[] parameterDesc, String[] parameterAnnotation, String errorInfo) throws Exception {
        OperatorSelfDefineClassFunction operatorSelfDefineClassFunction = new OperatorSelfDefineClassFunction(name,
            className, functionName, parameterTypes, parameterDesc, parameterAnnotation, errorInfo);
        this.addFunction(name, operatorSelfDefineClassFunction);
    }

    /**
     * 用于将一个用户自己定义的对象(例如Spring对象)方法转换为一个表达式计算的函数
     *
     * @param name
     * @param serviceObject
     * @param functionName
     * @param parameterClassTypes
     * @param errorInfo
     * @throws Exception
     */
    public void addFunctionOfServiceMethod(String name, Object serviceObject, String functionName,
        Class<?>[] parameterClassTypes, String errorInfo) throws Exception {
        OperatorSelfDefineServiceFunction operatorSelfDefineServiceFunction = new OperatorSelfDefineServiceFunction(
            name, serviceObject, functionName, parameterClassTypes, null, null, errorInfo);
        this.addFunction(name, operatorSelfDefineServiceFunction);
    }

    /**
     * 用于将一个用户自己定义的对象(例如Spring对象)方法转换为一个表达式计算的函数
     *
     * @param name
     * @param serviceObject
     * @param functionName
     * @param parameterClassTypes
     * @param parameterDesc       方法的参数说明
     * @param parameterAnnotation 方法的参数注解
     * @param errorInfo
     * @throws Exception
     */
    public void addFunctionOfServiceMethod(String name, Object serviceObject, String functionName,
        Class<?>[] parameterClassTypes, String[] parameterDesc, String[] parameterAnnotation, String errorInfo)
        throws Exception {
        OperatorSelfDefineServiceFunction operatorSelfDefineServiceFunction = new OperatorSelfDefineServiceFunction(
            name, serviceObject, functionName, parameterClassTypes, parameterDesc, parameterAnnotation, errorInfo);
        this.addFunction(name, operatorSelfDefineServiceFunction);
    }

    /**
     * 用于将一个用户自己定义的对象(例如Spring对象)方法转换为一个表达式计算的函数
     *
     * @param name
     * @param serviceObject
     * @param functionName
     * @param parameterTypes
     * @param errorInfo
     * @throws Exception
     */
    public void addFunctionOfServiceMethod(String name, Object serviceObject, String functionName,
        String[] parameterTypes, String errorInfo) throws Exception {
        OperatorSelfDefineServiceFunction operatorSelfDefineServiceFunction = new OperatorSelfDefineServiceFunction(
            name, serviceObject, functionName, parameterTypes, null, null, errorInfo);
        this.addFunction(name, operatorSelfDefineServiceFunction);
    }

    public void addFunctionOfServiceMethod(String name, Object serviceObject, String functionName,
        String[] parameterTypes, String[] parameterDesc, String[] parameterAnnotation, String errorInfo)
        throws Exception {
        OperatorSelfDefineServiceFunction operatorSelfDefineServiceFunction = new OperatorSelfDefineServiceFunction(
            name, serviceObject, functionName, parameterTypes, parameterDesc, parameterAnnotation, errorInfo);
        this.addFunction(name, operatorSelfDefineServiceFunction);
    }

    /**
     * 添加操作符号，此操作符号的优先级与 "*"相同，语法形式也是  data name data
     *
     * @param name
     * @param operator
     * @throws Exception
     */
    public void addOperator(String name, Operator operator) throws Exception {
        this.addOperator(name, "*", operator);
    }

    /**
     * 添加操作符号，此操作符号与给定的参照操作符号在优先级别和语法形式上一致
     *
     * @param name            操作符号名称
     * @param refOperatorName 参照的操作符号，例如 "+","--"等
     * @param operator
     * @throws Exception
     */
    public void addOperator(String name, String refOperatorName, Operator operator) throws Exception {
        this.manager.addOperatorWithLevelOfReference(name, refOperatorName);
        this.operatorManager.addOperator(name, operator);
    }

    /**
     * 添加操作符和关键字的别名，同时对操作符可以指定错误信息。
     * 例如：addOperatorWithAlias("加","+",null)
     *
     * @param keyWordName
     * @param realKeyWordName
     * @param errorInfo
     * @throws Exception
     */
    public void addOperatorWithAlias(String keyWordName, String realKeyWordName, String errorInfo) throws Exception {
        if (errorInfo != null && errorInfo.trim().length() == 0) {
            errorInfo = null;
        }
        //添加函数别名
        if (this.manager.isFunction(realKeyWordName)) {
            this.manager.addFunctionName(keyWordName);
            this.operatorManager.addOperatorWithAlias(keyWordName, realKeyWordName, errorInfo);
            return;
        }
        NodeType realNodeType = this.manager.findNodeType(realKeyWordName);
        if (realNodeType == null) {
            throw new QLException("关键字：" + realKeyWordName + "不存在");
        }
        boolean isExist = this.operatorManager.isExistOperator(realNodeType.getName());
        if (!isExist && errorInfo != null) {
            throw new QLException(
                "关键字：" + realKeyWordName + "是通过指令来实现的，不能设置错误的提示信息，errorInfo 必须是 null");
        }
        if (!isExist || errorInfo == null) {
            //不需要新增操作符号，只需要建立一个关键子即可
            this.manager.addOperatorWithRealNodeType(keyWordName, realNodeType.getName());
        } else {
            this.manager.addOperatorWithLevelOfReference(keyWordName, realNodeType.getName());
            this.operatorManager.addOperatorWithAlias(keyWordName, realNodeType.getName(), errorInfo);
        }
    }

    /**
     * 替换操作符处理
     *
     * @param name
     */
    public OperatorBase replaceOperator(String name, OperatorBase op) {
        return this.operatorManager.replaceOperator(name, op);
    }

    public ExpressPackage getRootExpressPackage() {
        return this.rootExpressPackage;
    }

    /**
     * 清除缓存
     */
    public void clearExpressCache() {
        expressInstructionSetCache.clear();
    }

    /**
     * 根据表达式的名称进行执行
     *
     * @param name
     * @param context
     * @param errorList
     * @param isTrace
     * @param isCatchException
     * @return
     * @throws Exception
     */
    public Object executeByExpressName(String name, IExpressContext<String, Object> context, List<String> errorList,
        boolean isTrace, boolean isCatchException) throws Exception {
        return InstructionSetRunner.executeOuter(this, this.loader.getInstructionSet(name), this.loader, context,
            errorList, isTrace, isCatchException, false);
    }

    /**
     * 执行指令集
     *
     * @param instructionSet
     * @param context
     * @param errorList
     * @param isTrace
     * @param isCatchException
     * @return
     * @throws Exception
     */
    public Object execute(InstructionSet instructionSet, IExpressContext<String, Object> context,
        List<String> errorList, boolean isTrace, boolean isCatchException) throws Exception {
        return executeReentrant(instructionSet, context, errorList, isTrace, isCatchException);
    }

    /**
     * 执行一段文本
     *
     * @param expressString 程序文本
     * @param context       执行上下文
     * @param errorList     输出的错误信息List
     * @param isCache       是否使用Cache中的指令集
     * @param isTrace       是否输出详细的执行指令信息
     * @param timeoutMillis 超时毫秒时间
     * @return
     * @throws Exception
     */
    public Object execute(String expressString, IExpressContext<String, Object> context, List<String> errorList,
        boolean isCache, boolean isTrace, long timeoutMillis) throws Exception {
        //设置超时毫秒时间
        QLExpressTimer.setTimer(timeoutMillis);
        try {
            return this.execute(expressString, context, errorList, isCache, isTrace);
        } finally {
            QLExpressTimer.reset();
        }
    }

    /**
     * 执行一段文本
     *
     * @param expressString 程序文本
     * @param context       执行上下文
     * @param errorList     输出的错误信息List
     * @param isCache       是否使用Cache中的指令集
     * @param isTrace       是否输出详细的执行指令信息
     * @return
     * @throws Exception
     */
    public Object execute(String expressString, IExpressContext<String, Object> context, List<String> errorList,
        boolean isCache, boolean isTrace) throws Exception {
        InstructionSet parseResult;
        if (isCache) {
            parseResult = getInstructionSetFromLocalCache(expressString);
        } else {
            parseResult = this.parseInstructionSet(expressString);
        }
        return executeReentrant(parseResult, context, errorList, isTrace, false);
    }

    private Object executeReentrant(InstructionSet sets, IExpressContext<String, Object> iExpressContext,
        List<String> errorList, boolean isTrace, boolean isCatchException) throws Exception {
        try {
            int reentrantCount = threadReentrantCount.get() + 1;
            threadReentrantCount.set(reentrantCount);

            return reentrantCount > 1 ?
                // 线程重入
                InstructionSetRunner.execute(this, sets, this.loader, iExpressContext, errorList, isTrace,
                    isCatchException, true, false) :
                InstructionSetRunner.executeOuter(this, sets, this.loader, iExpressContext, errorList, isTrace,
                    isCatchException, false);
        } finally {
            threadReentrantCount.set(threadReentrantCount.get() - 1);
        }
    }

    /**
     * 解析一段文本，生成指令集合
     *
     * @param text
     * @return
     * @throws Exception
     */
    public InstructionSet parseInstructionSet(String text) throws Exception {
        try {
            Map<String, String> selfDefineClass = new HashMap<>();
            for (ExportItem item : this.loader.getExportInfo()) {
                if (item.getType().equals(InstructionSet.TYPE_CLASS)) {
                    selfDefineClass.put(item.getName(), item.getName());
                }
            }

            ExpressNode root = this.parse.parse(this.rootExpressPackage, text, isTrace, selfDefineClass);
            InstructionSet result = createInstructionSet(root, "main");
            if (this.isTrace) {
                System.out.println(result);
            }
            return result;
        } catch (QLCompileException e) {
            throw e;
        } catch (Exception e) {
            throw new QLCompileException("编译异常:\n" + text, e);
        }
    }

    /**
     * 输出全局定义信息
     *
     * @return
     */
    public ExportItem[] getExportInfo() {
        return this.loader.getExportInfo();
    }

    /**
     * 优先从本地指令集缓存获取指令集，没有的话生成并且缓存在本地
     *
     * @param expressString
     * @return
     * @throws Exception
     */
    public InstructionSet getInstructionSetFromLocalCache(String expressString) throws Exception {
        Future<InstructionSet> futureTask = expressInstructionSetCache.get(expressString);
        if (futureTask == null) {
            FutureTask<InstructionSet> parseTask = new FutureTask<>(() -> this.parseInstructionSet(expressString));
            futureTask = expressInstructionSetCache.putIfAbsent(expressString, parseTask);
            if (futureTask == null) {
                futureTask = parseTask;
                parseTask.run();
            }
        }
        try {
            return futureTask.get();
        } catch (Exception e) {
            Throwable originThrow = e.getCause();
            if (!(originThrow instanceof Exception)) {
                throw e;
            }
            throw (Exception) originThrow;
        }
    }

    public InstructionSet createInstructionSet(ExpressNode root, String type) throws Exception {
        InstructionSet result = new InstructionSet(type);
        createInstructionSet(root, result);
        return result;
    }

    public void createInstructionSet(ExpressNode root, InstructionSet result) throws Exception {
        Stack<ForRelBreakContinue> forStack = new Stack<>();
        createInstructionSetPrivate(result, forStack, root, true);
        if (!forStack.isEmpty()) {
            throw new QLCompileException("For处理错误");
        }
    }

    public boolean createInstructionSetPrivate(InstructionSet result, Stack<ForRelBreakContinue> forStack,
        ExpressNode node, boolean isRoot) throws Exception {
        InstructionFactory factory = InstructionFactory.getInstructionFactory(node.getInstructionFactory());
        return factory.createInstruction(this, result, forStack, node, isRoot);
    }

    /**
     * 获取一个表达式需要的外部变量名称列表
     *
     * @param express
     * @return
     * @throws Exception
     */
    public String[] getOutVarNames(String express) throws Exception {
        return this.parseInstructionSet(express).getOutAttrNames();
    }

    public String[] getOutFunctionNames(String express) throws Exception {
        return this.parseInstructionSet(express).getOutFunctionNames();
    }

    public boolean isShortCircuit() {
        return isShortCircuit;
    }

    public void setShortCircuit(boolean isShortCircuit) {
        this.isShortCircuit = isShortCircuit;
    }

    /**
     * 是否忽略charset类型的数据，而识别为string，比如'a' -》 "a"
     * 默认为不忽略，正常识别为String
     */
    public boolean isIgnoreConstChar() {
        return this.parse.isIgnoreConstChar();
    }

    public void setIgnoreConstChar(boolean ignoreConstChar) {
        this.parse.setIgnoreConstChar(ignoreConstChar);
    }

    /**
     * 提供简答的语法检查，保证可以在运行期本地环境编译成指令
     *
     * @param text
     * @return
     */
    public boolean checkSyntax(String text) {
        return checkSyntax(text, false, null);
    }

    /**
     * 提供复杂的语法检查，(比如检查自定义的java类)，不保证运行期在本地环境可以编译成指令
     *
     * @param text
     * @param mockRemoteJavaClass
     * @param remoteJavaClassNames
     * @return
     */
    public boolean checkSyntax(String text, boolean mockRemoteJavaClass, List<String> remoteJavaClassNames) {
        try {
            Map<String, String> selfDefineClass = new HashMap<>();
            for (ExportItem item : this.loader.getExportInfo()) {
                if (item.getType().equals(InstructionSet.TYPE_CLASS)) {
                    selfDefineClass.put(item.getName(), item.getName());
                }
            }
            Word[] words = this.parse.splitWords(text, isTrace, selfDefineClass);
            ExpressNode root = this.parse.parse(this.rootExpressPackage, words, text, isTrace, selfDefineClass,
                mockRemoteJavaClass);
            InstructionSet result = createInstructionSet(root, "main");
            if (this.isTrace) {
                System.out.println(result);
            }
            if (mockRemoteJavaClass && remoteJavaClassNames != null) {
                remoteJavaClassNames.addAll(Arrays.asList(result.getVirClasses()));
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
