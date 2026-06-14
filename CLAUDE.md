# QLExpress 项目架构与开发测试规范

---

# 项目架构

## 1. 项目概述

**QLExpress** 是阿里巴巴开源的一个轻量级、高性能的动态语言引擎，基于 Java 8+ 开发，专为业务规则引擎和动态表达式设计。

## 2. 核心组件和职责

### 2.1 主要工作流程

```
用户代码
  ↓
Express4Runner (入口点)
  ├─→ [编译阶段]
  │   ├─ SyntaxTreeFactory: 构建 ANTLR 语法树
  │   ├─ QvmInstructionVisitor: 遍历语法树生成指令
  │   └─ QCompileCache: 缓存编译结果
  │
  ├─→ [运行阶段]
  │   ├─ QvmRuntime: 虚拟机运行时环境
  │   ├─ QLambdaInner: Lambda 执行体
  │   ├─ FixedSizeStack: 操作数栈
  │   └─ 指令执行引擎
  │
  └─→ [结果阶段]
      ├─ Value: 返回值包装
      └─ QLResult: 结果 + 执行追踪
```

### 2.2 核心组件详解

| 组件 | 位置 | 职责 | 关键类 |
|------|------|------|--------|
| **入口点** | 顶层 | 统一脚本执行入口 | `Express4Runner` |
| **编译器** | `aparser/` | 词法→语法→指令生成 | `SyntaxTreeFactory`, `QvmInstructionVisitor` |
| **运行时** | `runtime/` | JVM 栈式虚拟机执行 | `QvmRuntime`, `QLambdaInner`, `FixedSizeStack` |
| **函数体系** | `runtime/function/` | 内置和自定义函数 | `CustomFunction`, `QMethodFunction` |
| **运算符体系** | `runtime/operator/` | 内置和自定义运算符 | `BinaryOperator`, `OperatorManager` |
| **指令集** | `runtime/instruction/` | 虚拟机指令集 (~40+ 种) | `QLInstruction` 子类 |
| **数据体系** | `runtime/data/` | 值类型和类型转换 | `Value`, `DataValue`, `ObjTypeConvertor` |
| **上下文** | `runtime/context/` | 脚本执行环境 | `ExpressContext`, 多种实现 |
| **安全策略** | `security/` | 反射安全隔离 | `QLSecurityStrategy`, 4 种策略 |
| **执行追踪** | `runtime/trace/` | 表达式执行路径记录 | `ExpressionTrace`, `TracePointTree` |

---

## 3. 主要的包和类的组织方式

### 3.1 分层结构

```
表现层 (API):
  └─ Express4Runner, QLResult, QLOptions, InitOptions

编译层 (Parsing & Code Generation):
  ├─ 词法分析: QLexer, QLExtendLexer
  ├─ 语法解析: QLParser, QLExtendParser
  ├─ 语法树: QLParser.ProgramContext (ANTLR生成)
  ├─ 代码生成: QvmInstructionVisitor
  ├─ 缓存: QCompileCache, FutureTask<QCompileCache>
  └─ 工具: SyntaxTreeFactory, ImportManager

运行层 (Execution):
  ├─ 虚拟机: QvmRuntime, QLambdaInner, FixedSizeStack
  ├─ 上下文: ExpressContext 实现类
  ├─ 作用域: QvmGlobalScope, GeneratorScope
  ├─ 功能: CustomFunction, QMethodFunction
  ├─ 运算符: BinaryOperator, OperatorManager
  ├─ 指令: QLInstruction 子类 (~40个)
  └─ 数据: Value, DataValue, 类型转换

支撑层 (Infrastructure):
  ├─ 安全: QLSecurityStrategy, 4种策略
  ├─ 反射: ReflectLoader, MemberResolver
  ├─ 异常: QLException, 错误处理
  ├─ 追踪: ExpressionTrace, TracePointTree
  └─ 工具: BasicUtil, QLFunctionUtil
```

### 3.2 关键类依赖关系

```
Express4Runner
├─ 依赖: OperatorManager, ReflectLoader, InitOptions
├─ 包含: 编译缓存(Map<String, Future<QCompileCache>>)
├─ 包含: 用户函数(Map<String, CustomFunction>)
├─ 包含: 编译时函数(Map<String, CompileTimeFunction>)
└─ 包含: 全局作用域(GeneratorScope)

↓ 编译流程
QvmInstructionVisitor (访问器模式)
├─ 输入: QLParser.ProgramContext (ANTLR AST)
├─ 处理: 遍历语法树，生成虚拟机指令序列
└─ 输出: List<QLInstruction>

↓ 运行流程
QLambdaInner (可执行实体)
├─ 包含: List<QLInstruction> (指令序列)
├─ 包含: FixedSizeStack (操作数栈)
├─ 执行: 逐个执行指令，操控栈
└─ 返回: Object (脚本结果)

QLInstruction 子类
├─ ConstInstruction: 常量入栈
├─ LoadInstruction: 变量入栈
├─ OperatorInstruction: 运算符执行 (操作栈顶元素)
├─ CallFunctionInstruction: 函数调用
├─ JumpIfInstruction: 条件跳转
└─ ... (其他 40+ 指令类型)
```

---

## 4. 数据流示例

### 4.1 完整执行流程

```
脚本: "1 + 2 * 3"
  ↓
[编译阶段]
  QLexer.tokenize() → [1, +, 2, *, 3]
  QLParser.parse() → AST (二元运算表达式树)
  QvmInstructionVisitor.visit(AST) →
    [
      ConstInstruction(1),
      ConstInstruction(2),
      ConstInstruction(3),
      OperatorInstruction("*"),      // 优先级高，先执行
      OperatorInstruction("+")
    ]
  ↓
[运行阶段]
  QLambdaInner.execute(指令序列)
    Stack: []
    Step 1: ConstInstruction(1) → Stack: [1]
    Step 2: ConstInstruction(2) → Stack: [1, 2]
    Step 3: ConstInstruction(3) → Stack: [1, 2, 3]
    Step 4: OperatorInstruction("*") → pop(3), pop(2), push(2*3) → Stack: [1, 6]
    Step 5: OperatorInstruction("+") → pop(6), pop(1), push(1+6) → Stack: [7]
  ↓
结果: 7
```

### 4.2 函数调用流程

```
脚本: max(1, 2)
  ↓
[编译] → CallFunctionInstruction("max", 2参数)
[执行]
  Stack: [1, 2]
  CallFunctionInstruction 触发:
    1. 从 QvmGlobalScope 获取 "max" 对应的 CustomFunction
    2. 构造 Parameters([Value(1), Value(2)])
    3. CustomFunction.call(QContext, Parameters) → Value(2)
    4. 返回值入栈
  Stack: [2]
  ↓
结果: 2
```

---

# 5. 开发测试规范

你应该遵循 TDD(Test Driven Development) 的原则进行开发。

 - 先构建一个无法通过的测试用例
 - 修改项目代码以通过该测试用例
 - 补充更多边界测试用例，如果发现不通过，则继续修改项目代码
 - 用 `mvn test` 执行完整测试，确保没有影响其他功能
 - 在合适的地方添加功能文档，请同步修改中文文档和英文文档
   - 中文文档 `README-source.adoc`
   - 英文文档 `README-EN-source.adoc`
   - `README.adoc` 和 `README-EN.adoc` 由发布流程自动生成，禁止直接修改

关于脚本引擎扩展点相关的测试用例可以放置在 `com.alibaba.qlexpress4.Express4RunnerTest` 中

关于语言特性相关的测试用例则应该基于测试套件编写。

## 5.1 测试套件说明

在 `src/test/resources/testsuite` 中的测试 `.ql` 文件，被按照特性分门别类地放置在子目录中：

 - 子目录含义

```
src/test/resources/testsuite
├── independent/   # 不包含Java类引用的纯粹 QLExpress 语法测试脚本
|    ├── array     # 数组特性相关的测试脚本
|    ├── bool      # 布尔相关特性测试脚本
|    ├── ...
└─ java/           # 包含Java类引用的测试脚本
     ├── array     # 数组特性相关的测试脚本
     ├── cast      # 类型转换相关特性测试脚本
     ├── ...
```

 - 命名规范

```
<feature>_<scenario>.ql
例如：
- assignment_basic.ql              # 基本赋值
- incompatible_assignment_type.ql  # 不兼容类型赋值
- nested_function_calls.ql         # 嵌套函数调用
- complex_if_condition.ql          # 复杂条件判断
- long_one_line.ql                 # 长单行表达式
- long_one_line_simple.ql          # 简单长表达式
```

 - 测试脚本中可以使用的内置函数
   - `assert(bool)` 断言正确
   - `assertFalse(bool)` 断言错误
   - `println(object)` 打印任意变量内容
 - 脚本执行
   - `com.alibaba.qlexpress4.TestSuiteRunner.suiteTest` 对所有套件进行执行
   - `com.alibaba.qlexpress4.TestSuiteRunner.featureDebug` 输入相对路径可以对单个脚本进行执行，方便调试
