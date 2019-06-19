# QLExpress基本语法

[![Join the chat at https://gitter.im/QLExpress/Lobby](https://badges.gitter.im/QLExpress/Lobby.svg)](https://gitter.im/QLExpress/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# 一、背景介绍

由阿里的电商业务规则、表达式（布尔组合）、特殊数学公式计算（高精度）、语法分析、脚本二次定制等强需求而设计的一门动态脚本引擎解析工具。
在阿里集团有很强的影响力，同时为了自身不断优化、发扬开源贡献精神，于2012年开源。

QLExpress脚本引擎被广泛应用在阿里的电商业务场景，具有以下的一些特性:
- 1、线程安全，引擎运算过程中的产生的临时变量都是threadlocal类型。
- 2、高效执行，比较耗时的脚本编译过程可以缓存在本地机器，运行时的临时变量创建采用了缓冲池的技术，和groovy性能相当。
- 3、弱类型脚本语言，和groovy，javascript语法类似，虽然比强类型脚本语言要慢一些，但是使业务的灵活度大大增强。
- 4、安全控制,可以通过设置相关运行参数，预防死循环、高危系统api调用等情况。
- 5、代码精简，依赖最小，250k的jar包适合所有java的运行环境，在android系统的低端pos机也得到广泛运用。

# 二、依赖和调用说明

```xml
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>QLExpress</artifactId>
  <version>3.2.0</version>
</dependency>
```

```java
ExpressRunner runner = new ExpressRunner();
DefaultContext<String, Object> context = new DefaultContext<String, Object>();
context.put("a",1);
context.put("b",2);
context.put("c",3);
String express = "a+b*c";
Object r = runner.execute(express, context, null, true, false);
System.out.println(r);
```
# 三、语法介绍
## 1、操作符和java对象操作
### 普通java语法
```
//支持 +,-,*,/,<,>,<=,>=,==,!=,<>【等同于!=】,%,mod【取模等同于%】,++,--,
//in【类似sql】,like【sql语法】,&&,||,!,等操作符
//支持for，break、continue、if then else 等标准的程序控制逻辑
n=10;
for(sum=0,i=0;i<n;i++){
sum=sum+i;
}
return sum;
 


//逻辑三元操作
a=1;
b=2;
maxnum = a>b?a:b;

``` 

### 和java语法相比，要避免的一些ql写法错误
- 不支持try{}catch{}
- 不支持java8的lambda表达式
- 不支持for循环集合操作for (GRCRouteLineResultDTO item : list)
- 弱类型语言，请不要定义类型声明,更不要用Templete（Map<String,List>之类的）
- array的声明不一样
- min,max,round,print,println,like,in 都是系统默认函数的关键字，请不要作为变量名

```
//java语法：使用泛型来提醒开发者检查类型
keys = new ArrayList<String>();
deviceName2Value = new HashMap<String,String>(7);
String[] deviceNames = {"ng","si","umid","ut","mac","imsi","imei"};
int[] mins = {5,30};

//ql写法：
keys = new ArrayList();
deviceName2Value = new HashMap();
deviceNames = ["ng","si","umid","ut","mac","imsi","imei"];
mins = [5,30];


//java语法：对象类型声明
FocFulfillDecisionReqDTO reqDTO = param.getReqDTO();
//ql写法：
reqDTO = param.getReqDTO();

//java语法：数组遍历
for(GRCRouteLineResultDTO item : list) {
}
//ql写法：
for(i=0;i<list.size();i++){
item = list.get(i);
}

//java语法：map遍历
for(String key : map.keySet()) {
  System.out.println(map.get(key));
}
//ql写法：
  keySet = map.keySet();
  objArr = keySet.toArray();
  for (i=0;i<objArr.length;i++) {
  key = objArr[i];
   System.out.println(map.get(key));
  }
```

### java的对象操作

```
import com.ql.util.express.test.OrderQuery;
//系统自动会import java.lang.*,import java.util.*;


query = new OrderQuery();//创建class实例,会根据classLoader信息，自动补全类路径
query.setCreateDate(new Date());//设置属性
query.buyer = "张三";//调用属性,默认会转化为setBuyer("张三")
result = bizOrderDAO.query(query);//调用bean对象的方法
System.out.println(result.getId());//静态方法
 
```

## 2、脚本中定义function
```
function add(int a,int b){
  return a+b;
};

function sub(int a,int b){
  return a - b;
};

a=10;
return add(a,4) + sub(a,9);
 
```

## 3、扩展操作符：Operator
### 替换if then else 等关键字

```java
runner.addOperatorWithAlias("如果", "if",null);
runner.addOperatorWithAlias("则", "then",null);
runner.addOperatorWithAlias("否则", "else",null);

exp = "如果  (语文+数学+英语>270) 则 {return 1;} 否则 {return 0;}";
DefaultContext<String, Object> context = new DefaultContext<String, Object>();
runner.execute(exp,context,null,false,false,null);
```

### 如何自定义Operator
```java
//定义一个继承自com.ql.util.express.Operator的操作符
public class JoinOperator extends Operator{
	public Object executeInner(Object[] list) throws Exception {
		Object opdata1 = list[0];
		Object opdata2 = list[1];
		if(opdata1 instanceof java.util.List){
			((java.util.List)opdata1).add(opdata2);
			return opdata1;
		}else{
			java.util.List result = new java.util.ArrayList();
			result.add(opdata1);
			result.add(opdata2);
			return result;				
		}
	}
}

```
### 如何使用Operator

```
//(1)addOperator
ExpressRunner runner = new ExpressRunner();
DefaultContext<String, Object> context = new DefaultContext<String, Object>();
runner.addOperator("join",new JoinOperator());
Object r = runner.execute("1 join 2 join 3", context, null, false, false);
System.out.println(r);
//返回结果  [1, 2, 3]

//(2)replaceOperator
ExpressRunner runner = new ExpressRunner();
DefaultContext<String, Object> context = new DefaultContext<String, Object>();
runner.replaceOperator("+",new JoinOperator());
Object r = runner.execute("1 + 2 + 3", context, null, false, false);
System.out.println(r);
//返回结果  [1, 2, 3]

//(3)addFunction
ExpressRunner runner = new ExpressRunner();
DefaultContext<String, Object> context = new DefaultContext<String, Object>();
runner.addFunction("join",new JoinOperator());
Object r = runner.execute("join(1,2,3)", context, null, false, false);
System.out.println(r);
//返回结果  [1, 2, 3]

```
## 4、绑定java类或者对象的method

addFunctionOfClassMethod+addFunctionOfServiceMethod

```

public class BeanExample {
	public static String upper(String abc) {
		return abc.toUpperCase();
	}
	public boolean anyContains(String str, String searchStr) {

        char[] s = str.toCharArray();
        for (char c : s) {
            if (searchStr.contains(c+"")) {
                return true;
            }
        }
        return false;
    }
}

runner.addFunctionOfClassMethod("取绝对值", Math.class.getName(), "abs",
				new String[] { "double" }, null);
runner.addFunctionOfClassMethod("转换为大写", BeanExample.class.getName(),
				"upper", new String[] { "String" }, null);

runner.addFunctionOfServiceMethod("打印", System.out, "println",new String[] { "String" }, null);
runner.addFunctionOfServiceMethod("contains", new BeanExample(), "anyContains",
            new Class[] { String.class, String.class }, null);

String exp = “取绝对值(-100);转换为大写(\"hello world\");打印(\"你好吗？\");contains("helloworld",\"aeiou\")”;
runner.execute(exp, context, null, false, false);

```


 ## 5、macro 宏定义

```
runner.addMacro("计算平均成绩", "(语文+数学+英语)/3.0");
runner.addMacro("是否优秀", "计算平均成绩>90");
IExpressContext<String, Object> context =new DefaultContext<String, Object>();
context.put("语文", 88);
context.put("数学", 99);
context.put("英语", 95);
Object result = runner.execute("是否优秀", context, null, false, false);
System.out.println(r);
//返回结果true

```

 ## 6、编译脚本，查询外部需要定义的变量和函数。
 **注意以下脚本int和没有int的区别**

```
String express = "int 平均分 = (语文+数学+英语+综合考试.科目2)/4.0;return 平均分";
ExpressRunner runner = new ExpressRunner(true,true);
String[] names = runner.getOutVarNames(express);
for(String s:names){
 System.out.println("var : " + s);
}

//输出结果：

var : 数学
var : 综合考试
var : 英语
var : 语文
``` 

## 7、关于不定参数的使用

```
    @Test
    public void testMethodReplace() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        IExpressContext<String,Object> expressContext = new DefaultContext<String,Object>();
        runner.addFunctionOfServiceMethod("getTemplate", this, "getTemplate", new Class[]{Object[].class}, null);

        //(1)默认的不定参数可以使用数组来代替
        Object r = runner.execute("getTemplate([11,'22',33L,true])", expressContext, null,false, false);
        System.out.println(r);
        //(2)像java一样,支持函数动态参数调用,需要打开以下全局开关,否则以下调用会失败
        DynamicParamsUtil.supportDynamicParams = true;
        r = runner.execute("getTemplate(11,'22',33L,true)", expressContext, null,false, false);
        System.out.println(r);
    }
    //等价于getTemplate(Object[] params)
    public Object getTemplate(Object... params) throws Exception{
        String result = "";
        for(Object obj:params){
            result = result+obj+",";
        }
        return result;
    }
 ```

## 8、关于集合的快捷写法

```
    @Test
    public void testSet() throws Exception {
        ExpressRunner runner = new ExpressRunner(false,false);
        DefaultContext<String, Object> context = new DefaultContext<String, Object>();
        String express = "abc = NewMap(1:1,2:2); return abc.get(1) + abc.get(2);";
        Object r = runner.execute(express, context, null, false, false);
        System.out.println(r);
        express = "abc = NewList(1,2,3); return abc.get(1)+abc.get(2)";
        r = runner.execute(express, context, null, false, false);
        System.out.println(r);
        express = "abc = [1,2,3]; return abc[1]+abc[2];";
        r = runner.execute(express, context, null, false, false);
        System.out.println(r);
    }

``` 

## 9、集合的遍历
其实类似java的语法，只是ql不支持for(obj:list){}的语法，只能通过下标访问。
```java
  //遍历map
  map = new HashMap();
  map.put("a", "a_value");
  map.put("b", "b_value");
  keySet = map.keySet();
  objArr = keySet.toArray();
  for (i=0;i<objArr.length;i++) {
  key = objArr[i];
   System.out.println(map.get(key));
  }
  
```


# 四、运行参数和API列表介绍

QLExpressRunner如下图所示，从语法树分析、上下文、执行过程三个方面提供二次定制的功能扩展。

![QlExpress-detail.jpg](http://ata2-img.cn-hangzhou.img-pub.aliyun-inc.com/dec904b003aba15cbf1af2726914ddee.jpg)

## 1、属性开关
### isPrecise
```java	
	/**
	 * 是否需要高精度计算
	 */
	private boolean isPrecise = false;
```

> 高精度计算在会计财务中非常重要，java的float、double、int、long存在很多隐式转换，做四则运算和比较的时候其实存在非常多的安全隐患。
> 所以类似汇金的系统中，会有很多BigDecimal转换代码。而使用QLExpress，你只要关注数学公式本身 _订单总价 = 单价 * 数量 + 首重价格 + （ 总重量 - 首重） * 续重单价_ ，然后设置这个属性即可，所有的中间运算过程都会保证不丢失精度。

### isShortCircuit

```java
	/**
	 * 是否使用逻辑短路特性
	 */
	private boolean isShortCircuit = true;
```
在很多业务决策系统中，往往需要对布尔条件表达式进行分析输出，普通的java运算一般会通过逻辑短路来减少性能的消耗。例如规则公式：
_star>10000 and shoptype in('tmall','juhuasuan') and price between (100,900)_
假设第一个条件 _star>10000_ 不满足就停止运算。但业务系统却还是希望把后面的逻辑都能够运算一遍，并且输出中间过程，保证更快更好的做出决策。

参照单元测试:[ShortCircuitLogicTest.java](https://github.com/alibaba/QLExpress/blob/master/src/test/java/com/ql/util/express/test/logic/ShortCircuitLogicTest.java)

### isTrace

```java	
	/**
	 * 是否输出所有的跟踪信息，同时还需要log级别是DEBUG级别
	 */
	private boolean isTrace = false;
```
这个主要是是否输出脚本的编译解析过程，一般对于业务系统来说关闭之后会提高性能。

## 2、调用入参

```java
/**
 * 执行一段文本
 * @param expressString 程序文本
 * @param context 执行上下文，可以扩展为包含ApplicationContext
 * @param errorList 输出的错误信息List
 * @param isCache 是否使用Cache中的指令集,建议为true
 * @param isTrace 是否输出详细的执行指令信息，建议为false
 * @param aLog 输出的log
 * @return
 * @throws Exception
 */
	Object execute(String expressString, IExpressContext<String,Object> context,List<String> errorList, boolean isCache, boolean isTrace, Log aLog);

```

## 3、功能扩展API列表
QLExpress主要通过子类实现Operator.java提供的以下方法来最简单的操作符定义，然后可以被通过addFunction或者addOperator的方式注入到ExpressRunner中。

```java
	public abstract Object executeInner(Object[] list) throws Exception;

```

比如我们几行代码就可以实现一个功能超级强大、非常好用的join操作符:

_list = 1 join 2 join 3;_         -> [1,2,3]
_list = join(list,4,5,6);_        -> [1,2,3,4,5,6]

```java
public class JoinOperator extends Operator{
	public Object executeInner(Object[] list) throws Exception {
        java.util.List result = new java.util.ArrayList();
        Object opdata1 = list[0];
        if(opdata1 instanceof java.util.List){
           result.addAll((java.util.List)opdata1);
        }else{
            result.add(opdata1);
        }
        for(int i=1;i<list.length;i++){
           result.add(list[i]);
        }
        return result;
	}
}

```

如果你使用Operator的基类OperatorBase.java将获得更强大的能力，基本能够满足所有的要求。

### （1）function相关API

```java
//通过name获取function的定义
OperatorBase getFunciton(String name);

//通过自定义的Operator来实现类似：fun(a,b,c)
void addFunction(String name, OperatorBase op);
//fun(a,b,c) 绑定 object.function(a,b,c)对象方法
void addFunctionOfServiceMethod(String name, Object aServiceObject,
			String aFunctionName, Class<?>[] aParameterClassTypes,
			String errorInfo);
//fun(a,b,c) 绑定 Class.function(a,b,c)类方法
void addFunctionOfClassMethod(String name, String aClassName,
			String aFunctionName, Class<?>[] aParameterClassTypes,
			String errorInfo);
//给Class增加或者替换method，同时 支持a.fun(b) ，fun(a,b) 两种方法调用
//比如扩展String.class的isBlank方法:“abc”.isBlank()和isBlank("abc")都可以调用
void addFunctionAndClassMethod(String name,Class<?>bindingClass, OperatorBase op);

```

### （2）Operator相关API

提到脚本语言的操作符，优先级、运算的目数、覆盖原始的操作符(+,-,*,/等等)都是需要考虑的问题，QLExpress统统帮你搞定了。

```java
	//添加操作符号,可以设置优先级
void addOperator(String name,Operator op);
void addOperator(String name,String aRefOpername,Operator op);
	
	//替换操作符处理
OperatorBase replaceOperator(String name,OperatorBase op);
    
  //添加操作符和关键字的别名，比如 if..then..else -> 如果。。那么。。否则。。
void addOperatorWithAlias(String keyWordName, String realKeyWordName,
			String errorInfo);

```

### （3）宏定义相关API
QLExpress的宏定义比较简单，就是简单的用一个变量替换一段文本，和传统的函数替换有所区别。

```java
//比如addMacro("天猫卖家","userDO.userTag &1024 ==1024")
void addMacro(String macroName,String express) 
```

### （4）java class的相关api
QLExpress可以通过给java类增加或者改写一些method和field，比如 链式调用："list.join("1").join("2")"，比如中文属性："list.长度"。

```java
//添加类的属性字段
void addClassField(String field,Class<?>bindingClass,Class<?>returnType,Operator op);

//添加类的方法
void addClassMethod(String name,Class<?>bindingClass,OperatorBase op);
```

> 注意，这些类的字段和方法是执行器通过解析语法执行的，而不是通过字节码增强等技术，所以只在脚本运行期间生效，不会对jvm整体的运行产生任何影响，所以是绝对安全的。

### （4）语法树解析变量、函数的API

> 这些接口主要是对一个脚本内容的静态分析，可以作为上下文创建的依据，也可以用于系统的业务处理。
> 比如：计算 “a+fun1(a)+fun2(a+b)+c.getName()”
> 包含的变量:a,b,c
> 包含的函数:fun1,fun2

```java
//获取一个表达式需要的外部变量名称列表
String[] getOutVarNames(String express);

String[] getOutFunctionNames(String express);
```

### （5）语法解析校验api
脚本语法是否正确，可以通过ExpressRunner编译指令集的接口来完成。
```java
String expressString = "for(i=0;i<10;i++){sum=i+1}return sum;";
InstructionSet instructionSet = expressRunner.parseInstructionSet(expressString);
//如果调用过程不出现异常，指令集instructionSet就是可以被加载运行（execute）了！
```

### （6）指令集缓存相关的api
因为QLExpress对文本到指令集做了一个本地HashMap缓存，通常情况下一个设计合理的应用脚本数量应该是有限的，缓存是安全稳定的，但是也提供了一些接口进行管理。
```java
	//优先从本地指令集缓存获取指令集，没有的话生成并且缓存在本地
	InstructionSet getInstructionSetFromLocalCache(String expressString);
	//清除缓存
	void clearExpressCache();
```

### （7）安全风险控制
#### 7.1 防止死循环
```java
    try {
    	express = "sum=0;for(i=0;i<1000000000;i++){sum=sum+i;}return sum;";
	//可通过timeoutMillis参数设置脚本的运行超时时间:1000ms
	Object r = runner.execute(express, context, null, true, false, 1000);
	System.out.println(r);
	throw new Exception("没有捕获到超时异常");
    } catch (QLTimeOutException e) {
	System.out.println(e);
    }
```
#### 7.1 防止调用不安全的系统api
```java
    ExpressRunner runner = new ExpressRunner();
    QLExpressRunStrategy.setForbiddenInvokeSecurityRiskMethods(true);
    
    DefaultContext<String, Object> context = new DefaultContext<String, Object>();
    try {
    	express = "System.exit(1);";
	Object r = runner.execute(express, context, null, true, false);
	System.out.println(r);
	throw new Exception("没有捕获到不安全的方法");
    } catch (QLException e) {
	System.out.println(e);
    }
```

### （8）增强上下文参数Context相关的api

#### 8.1 与spring框架的无缝集成
上下文参数 IExpressContext context 非常有用，它允许put任何变量，然后在脚本中识别出来。

在实际中我们很希望能够无缝的集成到spring框架中，可以仿照下面的例子使用一个子类。

```java
public class QLExpressContext extends HashMap<String, Object> implements
		IExpressContext<String, Object> {

	private ApplicationContext context;

	//构造函数，传入context和 ApplicationContext
	public QLExpressContext(Map<String, Object> map,
                            ApplicationContext aContext) {
		super(map);
		this.context = aContext;
	}

	/**
	 * 抽象方法：根据名称从属性列表中提取属性值
	 */
	public Object get(Object name) {
		Object result = null;
		result = super.get(name);
		try {
			if (result == null && this.context != null
					&& this.context.containsBean((String) name)) {
				// 如果在Spring容器中包含bean，则返回String的Bean
				result = this.context.getBean((String) name);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	public Object put(String name, Object object) {
		return super.put(name, object);
	}

}

```

完整的demo参照 [SpringDemoTest.java](https://github.com/alibaba/QLExpress/blob/master/src/test/java/com/ql/util/express/test/spring/SpringDemoTest.java)


#### 8.2 自定义函数操作符获取原始的context控制上下文

自定义的Operator需要直接继承OperatorBase，获取到parent即可，可以用于在运行一组脚本的时候，直接编辑上下文信息，业务逻辑处理上也非常有用。

```java

public class ContextMessagePutTest {
    
    
    class OperatorContextPut extends OperatorBase {
        
        public OperatorContextPut(String aName) {
            this.name = aName;
        }
    
        @Override
        public OperateData executeInner(InstructionSetContext parent, ArraySwap list) throws Exception {
            String key = list.get(0).toString();
            Object value = list.get(1);
            parent.put(key,value);
            return null;
        }
    }
    
    @Test
    public void test() throws Exception{
        ExpressRunner runner = new ExpressRunner();
        OperatorBase op = new OperatorContextPut("contextPut");
        runner.addFunction("contextPut",op);
        String exp = "contextPut('success','false');contextPut('error','错误信息');contextPut('warning','提醒信息')";
        IExpressContext<String, Object> context = new DefaultContext<String, Object>();
        context.put("success","true");
        Object result = runner.execute(exp,context,null,false,true);
        System.out.println(result);
        System.out.println(context);
    }
}

```

附录：
[版本更新列表](VERSIONS.md)

## links for us
-  Gitter channel - Online chat room with QLExpress developers. [Gitter channel ](https://gitter.im/QLExpress/Lobby)
-  email:tianqiao@alibaba-inc.com,baoxingjie@126.com
-  wechart:371754252
-  QLExpress blogs: https://yq.aliyun.com/album/130

