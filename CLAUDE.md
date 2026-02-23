# QLExpress 项目架构与开发测试规范

---

# 项目架构

## 1. 项目概述

**QLExpress** 是阿里巴巴开源的一个轻量级、高性能的动态语言引擎，基于 Java 8+ 开发，专为业务规则引擎和动态表达式设计。

**重要架构变更 (2026)**: QLExpress4 已将 ANTLR4 解析器替换为手写的递归下降解析器 (`com.alibaba.qlexpress4.parser` 包)，移除了对 ANTLR4 的依赖，进一步减小了 JAR 包体积并提升了启动性能。旧的 `aparser` 包仍保留以支持一些遗留功能。

## 2. 核心组件和职责

### 2.1 主要工作流程

```
用户代码
  ↓
Express4Runner (入口点)
  ├─→ [编译阶段]
  │   ├─ SyntaxTreeFactory: 构建新语法树 (使用手写递归下降解析器)
  │   ├─ InstructionGenerator: 遍历 AST 生成指令
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
| **编译器** | `parser/` | 词法→语法→指令生成 | `SyntaxTreeFactory`, `InstructionGenerator` |
| **词法分析** | `parser/lexer/` | Token 化输入 | `QLexpressLexer`, `TokenType` |
| **语法分析** | `parser/parser/` | 构建 AST | `QLexpressParser` |
| **AST 节点** | `parser/ast/` | 语法树节点 | `ProgramNode`, 各种 `*Node` |
| **运行时** | `runtime/` | JVM 栈式虚拟机执行 | `QvmRuntime`, `QLambdaInner`, `FixedSizeStack` |
| **函数体系** | `runtime/function/` | 内置和自定义函数 | `CustomFunction`, `QMethodFunction` |
| **运算符体系** | `runtime/operator/` | 内置和自定义运算符 | `BinaryOperator`, `OperatorManager` |
| **指令集** | `runtime/instruction/` | 虚拟机指令集 (~40+ 种) | `QLInstruction` 子类 |
| **数据体系** | `runtime/data/` | 值类型和类型转换 | `Value`, `DataValue`, `ObjTypeConvertor` |
| **上下文** | `runtime/context/` | 脚本执行环境 | `ExpressContext`, 多种实现 |
| **安全策略** | `security/` | 反射安全隔离 | `QLSecurityStrategy`, 4 种策略 |
| **执行追踪** | `runtime/trace/` | 表达式执行路径记录 | `ExpressionTrace`, `TracePointTree` |

**注意**: 旧的 `aparser` 包包含 ANTLR 生成的代码和相关访问器类。这些类仍用于部分遗留功能，但新代码应使用 `parser` 包中的手写解析器。

---

## 3. 主要的包和类的组织方式

### 3.1 分层结构

```
表现层 (API):
  └─ Express4Runner, QLResult, QLOptions, InitOptions

编译层 (Parsing & Code Generation) - 新架构:
  ├─ 词法分析: QLexpressLexer (手写递归下降)
  ├─ 语法解析: QLexpressParser (手写递归下降)
  ├─ 语法树: ProgramNode, 各种 *Node (手写 AST)
  ├─ 代码生成: InstructionGenerator (访问者模式)
  ├─ 缓存: QCompileCache, FutureTask<QCompileCache>
  ├─ 工具: SyntaxTreeFactory, ASTCompiler
  └─ 访问器: InstructionGenerator, TraceGenerator, FunctionExtractor, VariableDetector, ScopeAnalyzer

编译层 (旧架构 - 遗留支持):
  ├─ aparser/QLexer.g4, aparser/QLParser.g4 (ANTLR4 语法文件)
  ├─ aparser/*Lexer, aparser/*Parser (ANTLR4 生成的解析器)
  ├─ aparser/QvmInstructionVisitor (旧版指令生成器)
  └─ 共享基础设施: ImportManager, InterpolationMode, BuiltInTypesSet

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

↓ 新编译流程
SyntaxTreeFactory
├─ 输入: 脚本字符串 + 选项
├─ 处理:
│   ├─ QLexpressLexer.tokenize() → Token 流
│   └─ QLexpressParser.parseProgram() → ProgramNode (AST)
└─ 输出: ProgramNode

ASTCompiler
├─ 输入: ProgramNode
├─ 处理:
│   ├─ InstructionGenerator.visit() → List<QLInstruction>
│   └─ TraceGenerator.visit() → List<TracePointTree>
└─ 输出: CompilationResult (lambda + trace points)

InstructionGenerator (访问器模式)
├─ 输入: ProgramNode (AST)
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

↓ 访问器 (Visitor Pattern)
ASTVisitor<R, C>
├─ InstructionGenerator: 生成 QVM 指令
├─ TraceGenerator: 生成追踪信息
├─ FunctionExtractor: 提取函数调用
├─ VariableDetector: 检测变量读写
├─ ScopeAnalyzer: 分析作用域
└─ ScriptChecker: 语法检查
```

---

## 4. 数据流示例

### 4.1 完整执行流程

```
脚本: "1 + 2 * 3"
  ↓
[编译阶段 - 新架构]
  QLexpressLexer.tokenize("1 + 2 * 3") → [INTEGER(1), PLUS, INTEGER(2), MUL, INTEGER(3)]
  QLexpressParser.parseProgram(tokens) → ProgramNode (AST)
  ASTCompiler.compile(ast) →
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

# 5. 新解析器架构

## 5.1 概述

QLExpress4 现在使用手写的递归下降解析器替代了原来的 ANTLR4 解析器。新架构具有以下优势：

- **更小的 JAR 包体积**: 移除 ANTLR4 依赖，减少约 300KB
- **更快的启动速度**: 无需加载 ANTLR4 运行时
- **更好的可维护性**: 手写解析器代码更直观，易于理解和调试
- **完整的功能覆盖**: 支持 QLExpress 的所有语法特性

## 5.2 解析器包结构

```
com.alibaba.qlexpress4.parser/
├── token/
│   ├── TokenType.java          # Token 类型枚举
│   └── Token.java                # Token 类 (包含类型、值、位置)
├── lexer/
│   └── QLexpressLexer.java       # 词法分析器
├── parser/
│   └── QLexpressParser.java      # 语法分析器
├── ast/
│   ├── ASTNode.java              # AST 节点基类
│   ├── ASTVisitor.java           # 访问者接口
│   ├── ProgramNode.java          # 程序根节点
│   ├── ExpressionNode.java       # 表达式节点标记接口
│   ├── StatementNode.java        # 语句节点标记接口
│   └── [各种具体节点].java       # 各类 AST 节点
├── visitor/
│   ├── InstructionGenerator.java # 指令生成器
│   ├── TraceGenerator.java       # 追踪信息生成器
│   ├── FunctionExtractor.java    # 函数提取器
│   ├── VariableDetector.java     # 变量检测器
│   ├── ScopeAnalyzer.java        # 作用域分析器
│   └── ScriptChecker.java        # 脚本检查器
├── SyntaxTreeFactory.java        # 语法树构建工厂
└── ASTCompiler.java              # AST 编译器
```

## 5.3 Token 类型

解析器支持以下 Token 类型：

- **关键字**: for, if, else, while, break, continue, return, switch, case, default, function, macro, import, static, new, then, class, this, try, catch, finally, throw
- **类型关键字**: byte, short, int, long, float, double, char, boolean, void
- **字面量**: INTEGER_LITERAL, FLOATING_LITERAL, STRING_LITERAL, TRUE, FALSE, NULL
- **运算符**: +, -, *, /, %, ++, --, &, |, ^, ~, <<, >>, >>>, <, >, <=, >=, ==, !=, <>, =, +=, -=, *=, /=, %=, &=, |=, ^=, <<=, >>=, >>>=, ., .*, ->, ::, ?., *., ?, :
- **分隔符**: (, ), {, }, [, ], ;, ,,
- **标识符**: ID

## 5.4 AST 节点类型

### 表达式节点
- `LiteralNode`: 字面量 (数字、字符串、布尔值、null)
- `IdentifierNode`: 标识符/变量引用
- `BinaryOpNode`: 二元运算
- `UnaryOpNode`: 一元运算
- `TernaryNode`: 三元运算 (?:)
- `LambdaNode`: Lambda 表达式
- `MethodCallNode`: 方法调用
- `ConstructorCallNode`: 构造函数调用
- `ArrayAccessNode`: 数组访问
- `ArraySliceNode`: 数组切片 [start:end]
- `ArrayLiteralNode`: 数组字面量
- `ListLiteralNode`: 列表字面量
- `MapLiteralNode`: 映射字面量
- `FieldAccessNode`: 字段访问
- `MethodReferenceNode`: 方法引用 (::)
- `InterpolatedStringNode`: 插值字符串
- `CastNode`: 类型转换
- `InstanceOfNode`: instanceof 检查
- `TypeNode`: 类型引用

### 语句节点
- `BlockNode`: 语句块
- `IfNode`: if-else 语句
- `WhileNode`: while 循环
- `ForNode`: for 循环 (传统和 for-each)
- `SwitchNode`: switch 语句
- `TryCatchNode`: try-catch-finally
- `ReturnNode`: return 语句
- `BreakNode`: break 语句
- `ContinueNode`: continue 语句
- `ThrowNode`: throw 语句
- `VariableDeclarationNode`: 变量声明
- `AssignmentNode`: 赋值语句
- `TypeDeclarationNode`: 类型声明
- `ImportNode`: import 语句
- `FunctionDefinitionNode`: 函数定义
- `MacroDefinitionNode`: 宏定义

### 辅助类
- `ProgramNode`: 程序根节点
- `ParameterNode`: 函数/lambda 参数
- `MapEntryNode`: 映射条目
- `SwitchCaseNode`: switch case
- `CatchClauseNode`: catch 子句

## 5.5 访问者模式

解析器使用访问者模式遍历 AST 并执行各种操作：

- **InstructionGenerator**: 将 AST 转换为 QVM 指令
- **TraceGenerator**: 生成表达式追踪信息
- **FunctionExtractor**: 提取所有函数调用
- **VariableDetector**: 检测变量读写
- **ScopeAnalyzer**: 分析变量作用域
- **ScriptChecker**: 检查脚本语法

## 5.6 解析流程

```
输入脚本
  ↓
QLexpressLexer.tokenize()
  → Token 流
  ↓
QLexpressParser.parseProgram()
  → ProgramNode (AST)
  ↓
(可选) 各种分析器
  - FunctionExtractor: 提取函数调用
  - VariableDetector: 检测变量使用
  - ScopeAnalyzer: 分析作用域
  - ScriptChecker: 检查语法
  ↓
InstructionGenerator.visit()
  → List<QLInstruction>
  ↓
QLambdaInner (可执行)
  ↓
运行并返回结果
```

---

# 6. 开发测试规范

你应该遵循 TDD(Test Driven Development) 的原则进行开发。

 - 先构建一个无法通过的测试用例
 - 修改项目代码以通过该测试用例
 - 补充更多边界测试用例，如果发现不通过，则继续修改项目代码
 - 用 `mvn test` 执行完整测试，确保没有影响其他功能
 - 在合适的地方添加功能文档，请同步修改中文文档和英文文档
   - 中文文档 `README-source.adoc`
   - 英文文档 `README-EN-source.adoc`

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