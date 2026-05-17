# Serializable Parse Cache 详细设计

## 1. 背景

QLExpress 现有预编译入口是 `Express4Runner.parseToLambda(String, ExpressContext, QLOptions)`，内部最终依赖 `QCompileCache`、`QLambdaDefinitionInner`、`QLInstruction`、`TracePointTree` 等运行时对象。这些对象包含 `Class<?>`、`BinaryOperator`、`UnaryOperator`、`QLambdaDefinition`、`ErrorReporter` 以及可能由 `CompileTimeFunction` 注入的自定义指令或 Java 闭包，不适合直接序列化为 JSON，也不适合作为跨机器的稳定数据契约。

本设计引入一个公开的、JSON 友好的预编译模型。生产端将脚本编译为 DTO，业务方可以使用任意 JSON 库序列化该 DTO；消费端在另一台机器上将 DTO 反序列化后，通过本机 `Express4Runner` 重新绑定类、运算符等运行环境引用，并直接执行，不再重新解析脚本。

## 2. 目标

- 提供可直接被 Jackson、Fastjson2、Gson 等 JSON 库序列化和反序列化的 JavaBean DTO。
- 反序列化后的消费端可以在本机 `Express4Runner` 上执行预编译结果。
- 序列化模型有独立的 `modelVersion`，和 QLExpress 发布版本隔离。
- 不在生产代码中新增 JSON 库依赖。
- 保留脚本原文和指令级 source 信息，使导入错误和运行错误尽量定位到原始 token。
- 支持表达式 trace 信息的可选携带。
- 对不支持的指令、常量、类、运算符和模型版本给出清晰错误。

## 3. 非目标

- V1 不支持导出任意 `CompileTimeFunction` 生成的自定义 `QLInstruction`。
- V1 不支持导出 `CallConstInstruction` 中捕获的 Java lambda、闭包或任意对象。
- V1 不承诺 `LoadedParseCache` 跨 `Express4Runner` 实例复用。
- V1 不把导入结果自动写入现有 `compileCache`。
- V1 不序列化 `InitOptions`、`QLOptions`、宏定义、默认 import、函数表、安全策略或 JSON 字符串。
- V1 不试图把 Java 对象常量通用序列化为 JSON。

## 4. 核心决策

### 4.1 JSON 边界

QLExpress 只提供 JSON 友好的 DTO，不提供 `toJson/fromJson` 字符串接口。生产代码不引入 JSON 依赖；测试可以继续使用 `fastjson2` 测试依赖验证 JSON 往返。

DTO 字段只允许使用：

- `String`
- `Boolean`
- Java 数字包装类型
- `List`
- `Map<String, Object>`
- 其他 DTO
- `null`

所有 DTO 使用 JavaBean 风格：无参构造器、getter、setter。setter 不做复杂校验，导入阶段统一校验。

### 4.2 环境绑定

DTO 是跨机器、跨进程的稳定数据边界；`LoadedParseCache` 是在某个 `Express4Runner` 上完成重绑定后的执行句柄。

导出时不固化以下运行环境对象：

- `BinaryOperator`、`UnaryOperator`
- `Class<?>`
- `CustomFunction`
- `CompileTimeFunction`
- `ReflectLoader`
- `QLSecurityStrategy`
- `ImportManager`
- `GeneratorScope`

导入时按消费端 Runner 重新绑定：

- 类名通过消费端 `ClassSupplier.loadCls(className)` 解析。
- 运算符通过消费端 `OperatorManager` 按文本和类别查找。
- 函数名在执行期通过消费端函数表或上下文解析。
- 成员访问仍由消费端 `ReflectLoader` 和安全策略控制。

生产端和消费端必须维护等价的类、运算符、函数注册环境。缺失或语义不一致时，导入或执行失败。

### 4.3 版本策略

顶层模型包含独立字段 `modelVersion`，V1 初始值为 `1`。

`modelVersion` 只在序列化模型发生不兼容变化时升级，不跟随 QLExpress 版本升级。顶层可以额外携带 `producerVersion` 作为调试元数据，但消费端不得仅因为 `producerVersion` 不同拒绝导入。

同一 `modelVersion` 下应支持跨 QLExpress 小版本导入和执行。如果某个 QLExpress 版本改变了已序列化指令的语义但没有升级 `modelVersion`，应视为实现缺陷。

### 4.4 错误定位

顶层模型必须包含 `script` 原文。导出器生成的每条 `SerializableInstruction` 必须包含 `source`。

`source` 至少包含：

- `start`: token start index
- `line`: 1-based line number，保持和 `DefaultErrReporter` 构造参数一致
- `col`: 0-based char position in line，保持和 ANTLR token 一致
- `lexeme`: token text

导入错误一般能对应到导入失败的指令。指令级错误必须优先使用该指令的 `source` 和顶层 `script` 生成 `Diagnostic`。模型级错误，例如不支持的 `modelVersion`、顶层字段缺失，使用位置 `0` 和空 snippet。

## 5. 公开 API

新增公开包：

```java
com.alibaba.qlexpress4.api.parsecache
```

入口方法仍挂在 `Express4Runner` 上。

### 5.1 导出

```java
public SerializableParseCache parseToSerializableCache(String script);
```

行为：

- 使用当前 Runner 的 `InitOptions`、默认 import、宏、编译期函数和运算符配置编译脚本。
- 将内部 `QCompileCache` 转换为 JSON 友好的 `SerializableParseCache`。
- 如果遇到 V1 不支持的指令、常量或编译期函数产物，抛出 `SerializableParseCacheException`。
- 不写入现有 `compileCache`。

### 5.2 导入

```java
public LoadedParseCache loadSerializableCache(SerializableParseCache cache);
```

行为：

- 校验 `modelVersion`。
- 校验 DTO schema、opcode、operand 类型、常量类型。
- 通过当前 Runner 解析所有 class name。
- 通过当前 Runner 绑定所有运算符。
- 重新构造内部 `QCompileCache`。
- 返回 Runner 绑定的 `LoadedParseCache`。

`LoadedParseCache` 只保证在创建它的 `Express4Runner` 上使用。不承诺跨 Runner 实例复用。

### 5.3 执行

```java
public QLambdaTrace parseToLambda(LoadedParseCache cache, ExpressContext context, QLOptions qlOptions);

public QLambdaTrace parseToLambda(SerializableParseCache cache, ExpressContext context, QLOptions qlOptions);

public QLResult execute(LoadedParseCache cache, ExpressContext context, QLOptions qlOptions);

public QLResult execute(SerializableParseCache cache, ExpressContext context, QLOptions qlOptions);

public QLResult execute(SerializableParseCache cache, Map<String, Object> context, QLOptions qlOptions);
```

行为：

- `LoadedParseCache` 路径复用已重绑定的内部 `QCompileCache`，适合高频执行。
- `SerializableParseCache` 便捷路径内部先调用 `loadSerializableCache`，再进入 `parseToLambda` 或执行。
- 执行逻辑复用现有 `parseToLambda` 后半段：构造 `QvmRuntime`、`QvmGlobalScope`、`DelegateQContext` 和 `QLambdaTrace`。
- 不自动写入或读取现有 `compileCache`。

`LoadedParseCache` 建议至少包含内部 `QCompileCache`、原始 `SerializableParseCache` 元数据和创建它的 Runner 标识。对外只暴露只读元数据，不暴露可变内部指令数组。

## 6. DTO 设计

### 6.1 `SerializableParseCache`

```java
public class SerializableParseCache {
    private int modelVersion;
    private String producerVersion;
    private String script;
    private String scriptHash;
    private SerializableLambdaDefinition main;
    private List<SerializableTracePoint> tracePoints;
}
```

字段说明：

- `modelVersion`: 必填，V1 为 `1`。
- `producerVersion`: 可选，导出端 QLExpress 版本或构建信息，仅用于排查。
- `script`: 必填，原始脚本文本。
- `scriptHash`: 可选，推荐使用稳定算法，例如 SHA-256 十六进制字符串。用于业务缓存和排查，不作为导入强制校验。
- `main`: 必填，主 lambda 定义。
- `tracePoints`: 可选，表达式 trace tree。只有导出端 `InitOptions.traceExpression=true` 时生成。

### 6.2 `SerializableLambdaDefinition`

```java
public class SerializableLambdaDefinition {
    private String name;
    private List<SerializableInstruction> instructions;
    private List<SerializableParam> params;
    private int maxStackSize;
}
```

字段说明：

- `name`: 必填，lambda 名称，例如 `main`。
- `instructions`: 必填，按执行顺序排列的指令。
- `params`: 必填，可以为空列表。参数类型使用 class name 表达。
- `maxStackSize`: 必填，恢复 `QLambdaDefinitionInner` 时使用。

空 lambda 使用显式 lambda 定义表达，或者用 importer 内部识别的 `EMPTY_LAMBDA` operand 表达。V1 推荐在 operand 中直接使用嵌套 `SerializableLambdaDefinition`，避免额外公开特殊类。

### 6.3 `SerializableParam`

```java
public class SerializableParam {
    private String name;
    private String className;
}
```

`className` 使用 JVM binary name，即 `Class.getName()`。

### 6.4 `SerializableInstruction`

```java
public class SerializableInstruction {
    private String opcode;
    private SerializableSource source;
    private Map<String, Object> operands;
}
```

字段说明：

- `opcode`: 必填，稳定指令名。不得直接使用 Java 类名作为兼容契约。
- `source`: 导出器生成时必填。手写或旧模型缺失时，导入错误降级到模型级位置。
- `operands`: 必填，可以为空 map。只允许 JSON 友好的值。

### 6.5 `SerializableSource`

```java
public class SerializableSource {
    private int start;
    private int line;
    private int col;
    private String lexeme;
}
```

### 6.6 `SerializableConstant`

常量必须带类型标签，避免 JSON 反序列化丢失 Java 数字类型。

```java
public class SerializableConstant {
    private String type;
    private Object value;
}
```

V1 支持的 `type`：

- `NULL`
- `BOOLEAN`
- `STRING`
- `CHAR`
- `INT`
- `LONG`
- `BIG_INTEGER`
- `FLOAT`
- `DOUBLE`
- `BIG_DECIMAL`
- `META_CLASS`

`META_CLASS.value` 为 class name。`CHAR.value` 为长度为 1 的字符串。

### 6.7 `SerializableTracePoint`

```java
public class SerializableTracePoint {
    private String type;
    private String token;
    private List<SerializableTracePoint> children;
    private int line;
    private int col;
    private int position;
}
```

`type` 使用 `TraceType.name()`。

## 7. Opcode 与 Operand

V1 使用统一 `SerializableInstruction`，通过 `opcode + operands` 表达不同指令。导入器维护 opcode 白名单和 operand schema。

### 7.1 基础指令

| opcode | 内部指令 | operands |
| --- | --- | --- |
| `CONST` | `ConstInstruction` | `constant: SerializableConstant`, `traceKey: Integer?` |
| `LOAD` | `LoadInstruction` | `name: String`, `traceKey: Integer?` |
| `POP` | `PopInstruction` | 无 |
| `RETURN` | `ReturnInstruction` | `resultType: String`, `traceKey: Integer?` |
| `BREAK_CONTINUE` | `BreakContinueInstruction` | `resultType: "BREAK" \| "CONTINUE"` |
| `THROW` | `ThrowInstruction` | 无 |
| `CHECK_TIMEOUT` | `CheckTimeOutInstruction` | 无 |

### 7.2 跳转和控制流片段

| opcode | 内部指令 | operands |
| --- | --- | --- |
| `JUMP` | `JumpInstruction` | `position: Integer` |
| `JUMP_IF` | `JumpIfInstruction` | `expect: Boolean`, `position: Integer`, `traceKey: Integer?` |
| `JUMP_IF_POP` | `JumpIfPopInstruction` | `expect: Boolean`, `position: Integer` |

`position` 继续使用现有运行时的相对跳转偏移。

### 7.3 运算符

| opcode | 内部指令 | operands |
| --- | --- | --- |
| `BINARY_OP` | `OperatorInstruction` | `operator: String`, `traceKey: Integer?` |
| `PREFIX_UNARY_OP` | `UnaryInstruction` | `operator: String`, `traceKey: Integer?` |
| `SUFFIX_UNARY_OP` | `UnaryInstruction` | `operator: String`, `traceKey: Integer?` |

导出时保存运算符文本和一元/二元类别，不保存运算符实现。导入时：

- `BINARY_OP` 调用 `OperatorManager.getBinaryOperator(operator)`。
- `PREFIX_UNARY_OP` 调用 `OperatorManager.getPrefixUnaryOperator(operator)`。
- `SUFFIX_UNARY_OP` 调用 `OperatorManager.getSuffixUnaryOperator(operator)`。

绑定失败抛出导入异常。

### 7.4 函数和调用

| opcode | 内部指令 | operands |
| --- | --- | --- |
| `CALL_FUNCTION` | `CallFunctionInstruction` | `functionName: String`, `argNum: Integer`, `traceKey: Integer?` |
| `CALL` | `CallInstruction` | `argNum: Integer` |
| `LOAD_LAMBDA` | `LoadLambdaInstruction` | `lambda: SerializableLambdaDefinition` |
| `DEFINE_FUNCTION` | `DefineFunctionInstruction` | `name: String`, `lambda: SerializableLambdaDefinition` |

`CallConstInstruction` 不在 V1 支持范围内。导出时遇到该指令必须失败，除非未来增加声明式 SPI。

### 7.5 作用域

| opcode | 内部指令 | operands |
| --- | --- | --- |
| `NEW_SCOPE` | `NewScopeInstruction` | `scopeName: String` |
| `CLOSE_SCOPE` | `CloseScopeInstruction` | `scopeName: String` |
| `DEFINE_LOCAL` | `DefineLocalInstruction` | `variableName: String`, `className: String` |

### 7.6 对象、数组、集合

| opcode | 内部指令 | operands |
| --- | --- | --- |
| `NEW_INSTANCE` | `NewInstanceInstruction` | `className: String`, `argNum: Integer` |
| `NEW_FILLED_INSTANCE` | `NewFilledInstanceInstruction` | `className: String`, `keys: List<String>` |
| `NEW_ARRAY` | `NewArrayInstruction` | `componentClassName: String`, `length: Integer` |
| `MULTI_NEW_ARRAY` | `MultiNewArrayInstruction` | `componentClassName: String`, `dims: Integer` |
| `NEW_LIST` | `NewListInstruction` | `initLength: Integer` |
| `NEW_MAP` | `NewMapInstruction` | `keys: List<String>` |

所有 class name 使用 `Class.getName()`，即 JVM binary name。

### 7.7 成员、索引、切片

| opcode | 内部指令 | operands |
| --- | --- | --- |
| `GET_FIELD` | `GetFieldInstruction` | `fieldName: String`, `optional: Boolean` |
| `SPREAD_GET_FIELD` | `SpreadGetFieldInstruction` | `fieldName: String` |
| `METHOD_INVOKE` | `MethodInvokeInstruction` | `methodName: String`, `argNum: Integer`, `optional: Boolean` |
| `SPREAD_METHOD_INVOKE` | `SpreadMethodInvokeInstruction` | `methodName: String`, `argNum: Integer` |
| `GET_METHOD` | `GetMethodInstruction` | `methodName: String` |
| `INDEX` | `IndexInstruction` | 无 |
| `SLICE` | `SliceInstruction` | `mode: "COPY" \| "LEFT" \| "RIGHT" \| "BOTH"` |
| `CAST` | `CastInstruction` | 无 |

### 7.8 结构化控制流

| opcode | 内部指令 | operands |
| --- | --- | --- |
| `WHILE` | `WhileInstruction` | `condition: SerializableLambdaDefinition`, `body: SerializableLambdaDefinition`, `whileScopeMaxStackSize: Integer` |
| `FOR` | `ForInstruction` | `forInit: SerializableLambdaDefinition?`, `condition: SerializableLambdaDefinition?`, `conditionSource: SerializableSource?`, `forUpdate: SerializableLambdaDefinition?`, `forScopeMaxStackSize: Integer`, `forBody: SerializableLambdaDefinition` |
| `FOR_EACH` | `ForEachInstruction` | `body: SerializableLambdaDefinition`, `itemClassName: String`, `targetSource: SerializableSource` |
| `TRY_CATCH` | `TryCatchInstruction` | `body: SerializableLambdaDefinition`, `exceptionTable: List<SerializableCatchEntry>`, `finalBody: SerializableLambdaDefinition?` |

`SerializableCatchEntry`：

```java
public class SerializableCatchEntry {
    private String exceptionClassName;
    private SerializableLambdaDefinition handler;
}
```

### 7.9 Trace 辅助指令

| opcode | 内部指令 | operands |
| --- | --- | --- |
| `TRACE_PEEK` | `TracePeekInstruction` | `traceKey: Integer?` |
| `TRACE_EVALUATED` | `TraceEvaluatedInstruction` | `traceKey: Integer?` |

### 7.10 字符串拼接

| opcode | 内部指令 | operands |
| --- | --- | --- |
| `STRING_JOIN` | `StringJoinInstruction` | `n: Integer` |

## 8. 类名编码

所有类引用保存为 `Class.getName()`：

- 普通类：`java.util.ArrayList`
- 内部类：`pkg.Outer$Inner`
- 基本类型：`int`、`long` 等，需要 importer 特殊识别，因为 `Class.forName("int")` 不可用。
- 数组类型：使用 `Class.getName()`，例如 `[I`、`[Ljava.lang.String;`，或在特定 operand 中通过 `componentClassName + dims` 表达。

导入时类解析规则：

1. 如果是基本类型名，映射到对应 primitive class。
2. 否则调用当前 Runner 的 `ClassSupplier.loadCls(className)`。
3. 如果解析失败，抛出 `SerializableParseCacheException`。
4. 成员访问安全不在导入时提前展开检查，执行时继续由 `ReflectLoader` 处理。

## 9. 导出流程

```text
script
  -> Express4Runner.parseDefinition(script)
  -> QCompileCache
  -> SerializableParseCacheExporter
  -> SerializableParseCache DTO
```

导出器职责：

- 设置 `modelVersion=1`。
- 写入 `producerVersion`、`script`、`scriptHash`。
- 遍历 `QLambdaDefinitionInner`。
- 将每条内部指令转换为 `SerializableInstruction`。
- 将指令 `ErrorReporter` 中的 token 信息转换为 `SerializableSource`。
- 将 `TracePointTree` 转换为 `SerializableTracePoint`。
- 对所有不支持的对象立即失败，不做静默降级。

为了避免反射读取 private 字段，应优先给少量运行时类补只读 getter。例如：

- `DefaultErrReporter` 增加 source 只读访问能力，或引入只读 `SourceAwareErrorReporter` 接口。
- `JumpInstruction.getPosition()`
- `JumpIfInstruction.getPosition()/isExpect()/getTraceKey()`
- `OperatorInstruction.getTraceKey()`
- `UnaryInstruction.getUnaryOperator()/getTraceKey()`
- `DefineFunctionInstruction.getName()/getLambdaDefinition()`
- 其他当前缺少 getter 的指令字段

序列化逻辑集中在 `api.parsecache` 下的 exporter/importer/registry，不把序列化方法加到每个 `QLInstruction` 子类中。

## 10. 导入流程

```text
SerializableParseCache DTO
  -> SerializableParseCacheImporter
  -> QLambdaDefinitionInner + QLInstruction[]
  -> QCompileCache
  -> LoadedParseCache
```

导入器职责：

- 校验顶层必填字段。
- 校验 `modelVersion` 是否支持。
- 校验 `main`、`instructions`、`params` 和所有 operand schema。
- 解析 class name。
- 绑定运算符。
- 重建 `DefaultErrReporter` 或等价 reporter。
- 重建嵌套 lambda、catch table 和 trace tree。
- 返回 `LoadedParseCache`。

导入器不得重新解析 `script`，否则无法满足“直接反序列化执行”的目标。

## 11. Trace 行为

导出端只有在当前 Runner `InitOptions.traceExpression=true` 时，才会生成 `tracePoints`。

消费端执行时：

- 如果 DTO 内有 `tracePoints`，且 `QLOptions.traceExpression=true`，则转换为 `QTraces` 并返回 trace。
- 如果 DTO 内没有 `tracePoints`，即使 `QLOptions.traceExpression=true`，也返回空 trace，不重新解析脚本补 trace。
- 如果消费端 Runner 的 `InitOptions.traceExpression=false`，继续保持现有语义，不返回 trace。

## 12. 异常设计

新增异常：

```java
public class SerializableParseCacheException extends QLException
```

该异常用于导入和导出阶段的模型错误，不用于脚本执行阶段的普通运行错误。

错误码加入 `QLErrorCodes`，建议包括：

- `SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_VERSION`
- `SERIALIZABLE_PARSE_CACHE_INVALID_MODEL`
- `SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_INSTRUCTION`
- `SERIALIZABLE_PARSE_CACHE_UNSUPPORTED_CONSTANT`
- `SERIALIZABLE_PARSE_CACHE_CLASS_NOT_FOUND`
- `SERIALIZABLE_PARSE_CACHE_OPERATOR_NOT_FOUND`

错误定位规则：

- 指令级错误必须使用 `SerializableInstruction.source` 和顶层 `script` 生成 `Diagnostic`。
- `FOR` 的 condition 相关导入错误使用 `conditionSource`。
- `FOR_EACH` 的 target 相关导入错误使用 `targetSource`。
- catch entry 的异常类解析失败可使用 `TRY_CATCH` 指令 source。
- 顶层模型错误使用位置 `0`。

## 13. 安全设计

反序列化输入不能视为可信输入。

导入阶段必须：

- 只接受白名单 opcode。
- 只接受白名单 constant type。
- 校验所有 operand 必填项和类型。
- 校验所有 list 元素类型。
- 解析 class name 时使用当前 Runner 的类加载入口。
- 对未知字段保持宽容，但不得让未知字段影响执行。

当前 `QLSecurityStrategy` 是成员级策略，不能在只看到 class name 时完整判断成员访问是否安全。V1 导入阶段必须保证类解析不绕过当前 Runner 的 `ClassSupplier`；具体构造器、方法、字段访问继续在执行阶段由 `ReflectLoader` 调用 `QLSecurityStrategy` 校验。如果后续引入类级安全策略，导入器必须同步调用该策略。

执行阶段继续使用当前 Runner 的：

- `ReflectLoader`
- `QLSecurityStrategy`
- `allowPrivateAccess`
- 用户函数表
- 自定义运算符
- `QLOptions`

这样远端消费端可以用自己的安全策略限制 DTO 中引用的 Java 类型和成员访问。

## 14. 与现有缓存的关系

现有 `compileCache` 使用脚本文本作为 key，存储 `Future<QCompileCache>`。序列化缓存不自动写入该缓存，原因：

- 同一脚本文本可能来自不同 `modelVersion`。
- trace 信息是否存在会影响执行返回。
- 生产端和消费端 Runner 配置可能不同。
- 自动注入会让 `execute(script, cache=true)` 行为变得隐式且难以排查。

高频场景应由业务方缓存 `SerializableParseCache` 或 `LoadedParseCache`。

## 15. 兼容性规则

### 15.1 向后兼容

同一 `modelVersion` 内允许新增可选字段。导入器应忽略未知字段，保持 JSON 库反序列化后的宽容性。

### 15.2 不兼容变更

以下变更需要升级 `modelVersion`：

- 修改已有 opcode 的语义。
- 删除已有 opcode。
- 修改已有 operand 的含义。
- 修改常量类型编码方式。
- 修改 class name 编码方式。
- 修改 trace tree 编码方式且旧 importer 无法理解。

### 15.3 QLExpress 版本

`producerVersion` 不参与强制兼容判断。消费端只按 `modelVersion` 判断是否支持导入。

## 16. 测试计划

### 16.1 单元测试

新增测试类建议放在：

```text
src/test/java/com/alibaba/qlexpress4/api/parsecache/
```

覆盖：

- 基础表达式导出、JSON 往返、导入、执行。
- 变量读取、赋值、局部变量、函数定义、lambda。
- if/else、switch、for、while、for-each、try/catch/finally。
- 数组、list、map、对象构造、字段、方法、索引、切片。
- 一元、二元、自定义运算符重绑定。
- `tracePoints` 存在和不存在两种执行路径。
- `LoadedParseCache` 高频复用。
- `SerializableParseCache` 便捷执行。

### 16.2 JSON 往返测试

使用测试依赖 `fastjson2`：

```java
SerializableParseCache cache = runner.parseToSerializableCache(script);
String json = JSON.toJSONString(cache);
SerializableParseCache parsed = JSON.parseObject(json, SerializableParseCache.class);
QLResult result = consumer.execute(parsed, context, options);
```

该测试只验证 DTO 设计可被 JSON 库处理，不把 fastjson2 引入生产代码。

### 16.3 失败测试

覆盖：

- 不支持的 `modelVersion`。
- 未知 `opcode`。
- 必填 operand 缺失。
- operand 类型错误。
- 不支持的常量类型。
- 类无法解析。
- 运算符无法绑定。
- `CallConstInstruction` 导出失败。
- 指令级导入错误能定位到对应 token。

### 16.4 回归测试

选择 `src/test/resources/testsuite` 中代表性脚本做批量导出、JSON 往返、导入、执行，结果应和直接 `execute(script, ...)` 一致。

完整验证使用：

```text
mvn test
```

## 17. 文档更新

实现该能力时需要同步更新：

- `README-source.adoc`
- `README-EN-source.adoc`

文档应包含：

- 何时使用序列化预编译缓存。
- DTO 由用户自行选择 JSON 库序列化。
- 生产端和消费端 Runner 环境必须等价。
- `LoadedParseCache` 是 Runner 绑定对象。
- V1 不支持任意编译期函数闭包导出。

## 18. 实施步骤

1. 新增 `api.parsecache` DTO、异常、`LoadedParseCache`。
2. 给 `DefaultErrReporter` 和必要运行时类补只读 getter，避免 exporter 使用反射读取 private 字段。
3. 实现 opcode registry、exporter、importer。
4. 在 `Express4Runner` 增加导出、导入、执行 API。
5. 增加 JSON 往返和失败定位测试。
6. 用测试套件代表性脚本做回归。
7. 更新中英文 README。

## 19. 开放问题

V1 暂不解决以下问题，但保留后续扩展空间：

- 是否为 `CompileTimeFunction` 设计声明式序列化 SPI。
- 是否提供可选的压缩或二进制格式。
- 是否提供模型签名或 hash 强校验。
- 是否把 `scriptHash` 算法固定为 API 契约。
- 是否支持增量加载到 Runner 的内部 `compileCache`。
