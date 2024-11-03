# 背景介绍

由阿里的电商业务规则演化而来的嵌入式Java动态脚本工具，在阿里集团有很强的影响力，同时为了自身不断优化、发扬开源贡献精神，于2012年开源。

在基本的表达式计算的基础上，增加以下特色功能：

 - 灵活的自定义能力，通过 Java API 自定义函数和操作符，可以快速实现业务规则的 DSL
 - 兼容Java语法，最新的QLExpress4可以兼容Java8语法，方便Java程序员快速熟悉
 - 默认安全，脚本默认不允许和应用代码进行交互，如果需要交互，也可以自行定义安全的交互方式
 - 解释执行，不占用 JVM 元空间，可以开启缓存提升解释性能

QLExpress4 作为 QLExpress 的最新演进版本，基于 Antlr4 重写了解析引擎，将原先的优点进一步发扬光大，彻底拥抱Java8和函数式编程，在性能和表达式能力上都进行了进一步增强。
场景举例：

 - 电商优惠券规则配置：通过 QLExpress 自定义函数和操作符快速实现优惠规则 DSL，供运营人员根据需求自行动态配置
 - 表单搭建控件关联规则配置：表单搭建平台允许用户拖拽控件搭建自定义的表单，利用 QLExpress 脚本配置不同控件间的关联关系
 - 流程引擎条件规则配置
 - 广告系统计费规则配置
......

# API 快速入门

## 引入依赖

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>QLExpress4</artifactId>
    <version>4.0.0-beta</version>
</dependency>
```

## 第一个 QLExpress 程序

```java
Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
Map<String, Object> context = new HashMap<>();
context.put("a", 1);
context.put("b", 2);
context.put("c", 3);
Object result = express4Runner.execute("a + b * c", context, QLOptions.DEFAULT_OPTIONS);
assertEquals(7, result);
```

## 添加自定义函数与操作符

```java
Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
// custom function
express4Runner.addVarArgsFunction("join", params ->
        Arrays.stream(params).map(Object::toString).collect(Collectors.joining(",")));
Object resultFunction = express4Runner.execute("join(1,2,3)", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
assertEquals("1,2,3", resultFunction);

// custom operator
express4Runner.addOperatorBiFunction("join", (left, right) -> left + "," + right);
Object resultOperator = express4Runner.execute("1 join 2 join 3", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
assertEquals("1,2,3", resultOperator);
```
## 高精度计算

QLExpress 内部会用 BigDecimal 表示所有无法用 double 精确表示数字，来尽可能地表示计算精度：

> 0.1 在 double 中无法精确表示

```java
Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
Object result = express4Runner.execute("0.1", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
assertTrue(result instanceof BigDecimal);
```

通过这种方式能够解决一些计算精度问题：

比如 0.1+0.2 因为精度问题，在 Java 中是不等于 0.3 的。
而 QLExpress 能够自动识别出 0.1 和 0.2 无法用双精度精确表示，改成用 BigDecimal 表示，确保其结果等于0.3

```java
assertNotEquals(0.3, 0.1 + 0.2, 0.0);
assertTrue((Boolean) express4Runner.execute("0.3==0.1+0.2", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS));
```

除了默认的精度保证外，还提供了 `precise` 开关，打开后所有的计算都使用BigDecimal，防止外部传入的低精度数字导致的问题：

```java
Map<String, Object> context = new HashMap<>();
context.put("a", 0.1);
context.put("b", 0.2);
assertFalse((Boolean) express4Runner.execute("0.3==a+b", context, QLOptions.DEFAULT_OPTIONS));
// open precise switch
assertTrue((Boolean) express4Runner.execute("0.3==a+b", context, QLOptions.builder().precise(true).build()));
```

## 调用应用中的 Java 类

> 需要放开安全策略，不建议用于终端用户输入

假设应用中有如下的 Java 类(`com.alibaba.qlexpress4.QLImportTester`)：

```java
package com.alibaba.qlexpress4;

public class QLImportTester {

    public static int add(int a, int b) {
        return a + b;
    }

}
```

在 QLExpress 中有如下两种调用方式。

 1. 在脚本中使用 `import` 语句导入类并且使用

```java
Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
        // open security strategy, which allows access to all Java classes within the application.
        .securityStrategy(QLSecurityStrategy.open())
        .build()
);
// Import Java classes using the import statement.
Object result = express4Runner.execute("import com.alibaba.qlexpress4.QLImportTester;" +
        "QLImportTester.add(1,2)", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
Assert.assertEquals(3, result);
```

 2. 在创建 `Express4Runner` 时默认导入该类，此时脚本中就不需要额外的 `import` 语句

```java
Express4Runner express4Runner = new Express4Runner(InitOptions.builder()
        .defaultImport(
                Collections.singletonList(ImportManager.importCls("com.alibaba.qlexpress4.QLImportTester"))
        )
        .securityStrategy(QLSecurityStrategy.open())
        .build()
);
Object result = express4Runner.execute("QLImportTester.add(1,2)", Collections.emptyMap(), QLOptions.DEFAULT_OPTIONS);
Assert.assertEquals(3, result);
```

## 表达式缓存

通过 `cache` 选项可以开启表达式缓存，这样相同的表达式就不会重新编译，能够大大提升性能。

注意该缓存没有限制大小，只适合在表达式为有限数量的情况下使用：

```java
Express4Runner express4Runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
// open cache switch
express4Runner.execute("1+2", new HashMap<>(), QLOptions.builder()
        .cache(true).build());
```

# 语法入门

QLExpress4 兼容 Java8 语法的同时，也提供了很多更加灵活宽松的语法模式，帮助用户更快捷地编写表达式。

基于表达式优先的语法设计，复杂的条件判断语句也可以直接当作表达式使用。

## 变量声明

同时支持静态类型和动态类型：

 - 变量声明时不写类型，则变量是动态类型，也同时是一个赋值表达式
 - 变量声明如果写类型，则是静态类型，此时是一个变量声明语句

```java
// Dynamic Typeing
a = 1;
// Static Typing
int b = 2;
```

## 方便语法元素

列表，映射等常用语法元素在 Java 里构造过于麻烦，因此在 QLExpress 中对其进行了简化

```qlexpress
// list
l = [1,2,3]
// map
m = {
  "aa": 10,
  "bb": {
    "cc": "cc1",
    "dd": "dd1"
  }
}
```

列表的实际数据类型是 Java 中的 ArrayList，映射的底层数据类型是 LinkedHashMap

## 数字

对于未声明类型的数字，
QLExpress会根据其所属范围自动从 int, long, BigInteger, BigDecimal 等数据类型中选择一个最合适的：

```qlexpress
// true
2147483647 instanceof Integer
// true
9223372036854775807 instanceof Long
// true
18446744073709552000 instanceof BigInteger
```

因此在自定义函数或者操作符时，建议使用 Number 类型进行接收，因为数字类型是无法事先确定的。

## 分号

表达式语句可以省略结尾的分号，整个脚本的返回值就是最后一个表达式的计算结果。

以下脚本的返回值为 2：

```qlexpress
a = 1
b = 2
// last express
1+1
```

等价于以下写法：

```qlexpress
a = 1
b = 2
// return statment
return 1+1;
```

## 表达式

QLExpress 采用表达式优先的设计，其中 除了 import， return 和循环等结构外，几乎都是表达式。

if 语句也是一个表达式，以下脚本的输出为 11：

```java
if (11 == 11) {
  10
} else {
  20 + 2
} + 1;
```

try catch 结构也是一个表达式，以下脚本的输出为 12：

```java
1 + try {
  100 + 1/0
} catch(e) {
  // Throw a zero-division exception
  11
}
```

## 控制结构

### if 分支

以下脚本的输出为 true：

```qlexpress
a = 11;
if (a >= 0 && a < 5) {
  true
} else if (a >= 5 && a < 10) {
  false
} else if (a >= 10 && a < 15) {
  true
}
```

### while 循环

以下脚本的输出为 2：

```qlexpress
i = 0;
while (i < 5) {
  if (++i == 2) {
    break;
  }
}
i
```

### for 循环

以下脚本的输出为列表 `[3,4,5]`:

```qlexpress
l = [];
for (int i = 3; i < 6; i++) {
  l.add(i);
}
l
```

### for-each 循环

以下脚本的输出为 8:

```qlexpress
sum = 0;
for (i: [0,1,2,3,4]) {
  if (i == 2) {
    continue;
  }
  sum += i;
}
sum
```

### try-catch

以下脚本的输出为 11：

```qlexpress
try {
  100 + 1/0
} catch(e) {
  // Throw a zero-division exception
  11
}
```

# 附录一 QLExpress4性能提升

[QLExpress4与3性能对比](https://www.yuque.com/xuanheng-ffjti/iunlps/pgfzw46zel2xfnie?singleDoc#%20%E3%80%8AQLExpress3%E4%B8%8E4%E6%80%A7%E8%83%BD%E5%AF%B9%E6%AF%94%E3%80%8B)

总结：常见场景下，无缓存时，QLExpress4能比3有接近10倍性能提升；有缓存，也有一倍性能提升。

# 附录二 开发者联系方式

 - email:tianqiao@alibaba-inc.com,baoxingjie@126.com
 - wechat:
   - xuanheng: dqy932087612
   - binggou: pymbupt
   - linxiang: tkk33362