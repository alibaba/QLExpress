---
ruleType: Manual
description: QLExpress4 项目架构与开发指南
globs:
---
rule编写规则: https://km.sankuai.com/collabpage/2710344014

# QLExpress4 项目架构与开发指南

## 项目概述

QLExpress4 是阿里巴巴开源的嵌入式 Java 动态脚本引擎,基于 Antlr4 重写了解析引擎。它是一个轻量级、高性能的规则表达式引擎,广泛应用于电商规则配置、流程引擎、表单搭建等业务场景。

**核心特性:**
- 兼容 Java 语法,支持函数式编程
- 原生支持 JSON 语法
- 表达式计算追踪能力
- 默认安全隔离,可自定义安全策略
- 解释执行,不占用 JVM 元空间
- 支持自定义函数和操作符

## 核心入口类

### Express4Runner

主入口类: [Express4Runner.java](md:src/main/java/com/alibaba/qlexpress4/Express4Runner.java)

这是 QLExpress4 的核心执行引擎,负责:
- 脚本编译与缓存管理
- 脚本执行
- 自定义函数和操作符管理
- 宏定义管理

**关键方法:**
```java
// 执行脚本
QLResult execute(String script, Map<String, Object> context, QLOptions qlOptions)
QLResult execute(String script, ExpressContext context, QLOptions qlOptions)

// 添加自定义函数
void addFunction(String functionName, CustomFunction function)
BatchAddFunctionResult addFunctions(Object obj)

// 添加自定义操作符
void addOperator(String operatorName, CustomBinaryOperator operator)

// 添加宏定义
void addMacro(String macroName, String macroScript)
```

## 配置类

### InitOptions

初始化配置类: [InitOptions.java](md:src/main/java/com/alibaba/qlexpress4/InitOptions.java)

用于配置 Express4Runner 的全局行为:
- `classSupplier`: 类加载器供应商
- `defaultImport`: 默认导入的 Java 包
- `debug`: 是否开启调试模式
- `securityStrategy`: 安全策略(isolation/open/whitelist/blacklist)
- `extensionFunctions`: 扩展函数列表
- `allowPrivateAccess`: 是否允许访问私有字段和方法
- `interpolationMode`: 字符串插值模式
- `traceExpression`: 是否追踪表达式执行
- `selectorStart/selectorEnd`: 选择器起止标记(默认 `${` 和 `}`)

**使用示例:**
```java
InitOptions options = InitOptions.builder()
    .debug(true)
    .securityStrategy(QLSecurityStrategy.open())
    .traceExpression(true)
    .build();
Express4Runner runner = new Express4Runner(options);
```

### QLOptions

执行配置类: [QLOptions.java](md:src/main/java/com/alibaba/qlexpress4/QLOptions.java)

用于配置单次脚本执行的行为:
- `precise`: 是否使用 BigDecimal 精确计算
- `polluteUserContext`: 是否污染用户上下文
- `timeoutMillis`: 脚本执行超时时间(毫秒)
- `attachments`: 附件数据(不作为变量,仅传递给自定义函数)
- `cache`: 是否缓存编译结果
- `avoidNullPointer`: 是否避免空指针
- `maxArrLength`: 允许创建的数组最大长度
- `traceExpression`: 是否追踪表达式(需配合 InitOptions.traceExpression)
- `shortCircuitDisable`: 是否禁用逻辑运算符短路

## 核心包结构

### 1. aparser 包 - 解析器
位置: `src/main/java/com/alibaba/qlexpress4/aparser/`

负责脚本的词法分析、语法分析和 AST 生成:
- `QLParser`: 基于 Antlr4 的解析器
- `SyntaxTreeFactory`: 语法树工厂
- `ImportManager`: 导入管理器
- `QCompileCache`: 编译缓存
- `InterpolationMode`: 字符串插值模式枚举

### 2. runtime 包 - 运行时
位置: `src/main/java/com/alibaba/qlexpress4/runtime/`

脚本执行的核心运行时组件:

#### context 子包 - 上下文
- `ExpressContext`: 上下文接口
- `MapExpressContext`: 基于 Map 的上下文实现
- `ObjectFieldExpressContext`: 基于对象字段的上下文实现
- `QLAliasContext`: 别名上下文

#### instruction 子包 - 指令
包含各种虚拟机指令的实现,如:
- 算术运算指令
- 逻辑运算指令
- 方法调用指令
- 变量访问指令

#### operator 子包 - 操作符
位置: `src/main/java/com/alibaba/qlexpress4/runtime/operator/`

包含内置操作符和自定义操作符基类:
- `arithmetic/`: 算术运算符(+, -, *, /, %)
- `logic/`: 逻辑运算符(&&, ||, !)
- `compare/`: 比较运算符(==, !=, >, <, >=, <=)
- `bit/`: 位运算符(&, |, ^, ~, <<, >>)
- `CustomBinaryOperator`: 自定义二元操作符基类

#### function 子包 - 函数
- `CustomFunction`: 自定义函数基类
- `QMethodFunction`: 基于 Java 方法的函数
- `ExtensionFunction`: 扩展函数接口
- `FilterExtensionFunction`: 列表过滤扩展函数
- `MapExtensionFunction`: 列表映射扩展函数

#### trace 子包 - 追踪
- `ExpressionTrace`: 表达式追踪信息
- `TracePointTree`: 追踪点树
- `QTraces`: 追踪工具类

### 3. security 包 - 安全策略
位置: `src/main/java/com/alibaba/qlexpress4/security/`

提供多种安全策略:
- `QLSecurityStrategy`: 安全策略接口
- `StrategyIsolation`: 隔离策略(默认,不允许访问 Java 类)
- `StrategyOpen`: 开放策略(允许访问所有 Java 类)
- `StrategyWhiteList`: 白名单策略
- `StrategyBlackList`: 黑名单策略

### 4. exception 包 - 异常
位置: `src/main/java/com/alibaba/qlexpress4/exception/`

定义了各种异常类型:
- `QLException`: QLExpress 基础异常
- `QLSyntaxException`: 语法异常
- `lsp/`: LSP 相关异常

### 5. api 包 - API
位置: `src/main/java/com/alibaba/qlexpress4/api/`

提供公共 API 接口和注解:
- `QLFunctionalVarargs`: 可变参数函数式接口
- `BatchAddFunctionResult`: 批量添加函数结果

### 6. utils 包 - 工具类
位置: `src/main/java/com/alibaba/qlexpress4/utils/`

提供各种工具方法

## 开发指南

### 添加自定义函数

1. **使用 Lambda 方式:**
```java
runner.addFunction("add", (context, params) -> {
    int a = (int) params[0];
    int b = (int) params[1];
    return a + b;
});
```

2. **使用注解方式批量添加:**
```java
public class MyFunctions {
    @QLFunction
    public static int multiply(int a, int b) {
        return a * b;
    }
}
runner.addFunctions(new MyFunctions());
```

### 添加自定义操作符

```java
runner.addOperator("@@", (context, left, right) -> {
    // 自定义操作符逻辑
    return result;
});
```

### 使用附件传递数据

```java
Map<String, Object> attachments = new HashMap<>();
attachments.put("tenantId", "tenant001");

QLOptions options = QLOptions.builder()
    .attachments(attachments)
    .build();

runner.execute(script, context, options);
```

在自定义函数中获取附件:
```java
runner.addFunction("getTenant", (context, params) -> {
    String tenantId = (String) context.getAttachments().get("tenantId");
    return tenantId;
});
```

### 表达式追踪

1. **启用追踪:**
```java
InitOptions initOptions = InitOptions.builder()
    .traceExpression(true)
    .build();
Express4Runner runner = new Express4Runner(initOptions);

QLOptions qlOptions = QLOptions.builder()
    .traceExpression(true)
    .build();
```

2. **获取追踪结果:**
```java
QLResult result = runner.execute(script, context, qlOptions);
ExpressionTrace trace = result.getTrace();
// 分析追踪信息
```

### 安全策略配置

```java
// 隔离策略(默认)
QLSecurityStrategy.isolation()

// 开放策略
QLSecurityStrategy.open()

// 白名单策略
QLSecurityStrategy.whiteList(Set.of("java.lang.String", "java.util.List"))

// 黑名单策略
QLSecurityStrategy.blackList(Set.of("java.lang.System", "java.io.File"))
```

## 最佳实践

### 1. 性能优化
- 启用编译缓存: `QLOptions.builder().cache(true)`
- 复用 Express4Runner 实例
- 合理设置超时时间避免脚本死循环

### 2. 安全性
- 生产环境使用隔离策略或白名单策略
- 通过附件传递敏感信息,避免暴露给脚本
- 设置合理的超时时间和数组长度限制

### 3. 调试
- 开发环境启用 debug 模式
- 使用表达式追踪功能分析复杂规则
- 自定义 debugInfoConsumer 收集调试信息

### 4. 错误处理
- 捕获 QLException 处理脚本执行错误
- 捕获 QLSyntaxException 处理语法错误
- 提供友好的错误提示给最终用户

## 测试

测试代码位置: `src/test/java/com/alibaba/qlexpress4/`

运行测试:
```bash
mvn test
```

## 相关文档

- 项目 README: [README.adoc](md:README.adoc)
- 英文 README: [README-EN.adoc](md:README-EN.adoc)
- Maven 配置: [pom.xml](md:pom.xml)
