## qlExpress相关文档

### 最简单的调用案例 

```xml
<dependency>
  <groupId>com.taobao.util</groupId>
  <artifactId>taobao-express</artifactId>
  <version>3.0.17</version>
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

### 更多的语法介绍 
1、java的绝大多数语法

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
max = a>b?a:b;

``` 

//关于对象，类，属性，方法的调用

```
import com.ql.util.express.test.OrderQuery;
//系统自动会import java.lang.*,import java.util.*;


query = new OrderQuery();//创建class实例,会根据classLoader信息，自动补全类路径
query.setCreateDate(new Date());//设置属性
query.buyer = "张三";//调用属性,默认会转化为setBuyer("张三")
result = bizOrderDAO.query(query);//调用bean对象的方法
System.out.println(result.getId());//静态方法
 

//自定义方法与调用
function add(int a,int b){
  return a+b;
};

function sub(int a,int b){
  return a - b;
};

a=10;
return add(a,4) + sub(a,9);
 
 ```

2、自定义操作符号：addOperatorWithAlias+addOperator+addFunction

```

runner.addOperatorWithAlias("如果", "if",null);
runner.addOperatorWithAlias("则", "then",null);
runner.addOperatorWithAlias("否则", "else",null);

exp = "如果  (如果 1==2 则 false 否则 true) 则 {2+2;} 否则 {20 + 20;}";
DefaultContext<String, Object> context = new DefaultContext<String, Object>();
runner.execute(exp,nil,null,false,false,null);
 

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

ExpressRunner runner = new ExpressRunner();
DefaultContext<String, Object> context = new DefaultContext<String, Object>();
runner.addOperator("join",new JoinOperator());
Object r = runner.execute("1 join 2 join 3", context, null, false, false);
System.out.println(r);
//返回结果  [1, 2, 3]
 

class GroupOperator extends Operator {
	public GroupOperator(String aName) {
		this.name= aName;
	}
	public Object executeInner(Object[] list)throws Exception {
		Object result = Integer.valueOf(0);
		for (int i = 0; i < list.length; i++) {
			result = OperatorOfNumber.add(result, list[i],false);//根据list[i]类型（string,number等）做加法
		}
		return result;
	}
}

runner.addFunction("group", new GroupOperator("group"));
ExpressRunner runner = new ExpressRunner();
DefaultContext<String, Object> context = new DefaultContext<String, Object>();
runner.addOperator("join",new JoinOperator());
Object r = runner.execute("group(1,2,3)", context, null, false, false);
System.out.println(r);
//返回结果  6

```

 3、类的静态方法，对象的方法绑定：addFunctionOfClassMethod+addFunctionOfServiceMethod

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

String exp = “取绝对值(-100);转换为大写(\"hello world\");打印(\"你好吗？\")；contains("helloworld",\"aeiou\")”;
runner.execute(exp, context, null, false, false);

```


 4、macro 宏定义

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

 5、编译脚本，查询外部需要定义的变量，注意以下脚本int和没有int的区别

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

6、关于不定参数的使用

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

7、关于集合的快捷写法

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

 8、集合的遍历
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
