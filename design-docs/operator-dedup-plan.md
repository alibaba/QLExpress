# 运算符类去重优化方案

## Context

QLExpress 中 `runtime/operator/` 目录下有约 45 个运算符类文件，其中约 **29 个类** (~1100 行代码) 是完全的模式重复。每个类都是 `BaseBinaryOperator` 的子类，遵循同样的单例 + 委托模式：

```java
// 每个类都是这个模板的复制粘贴，唯一区别是 operator 字符串、优先级常量、委托的方法名
public class XxxOperator extends BaseBinaryOperator {
    private static final XxxOperator INSTANCE = new XxxOperator();
    private XxxOperator() {}
    public static XxxOperator getInstance() { return INSTANCE; }
    @Override public String getOperator() { return "xxx"; }
    @Override public int getPriority() { return QLPrecedences.XXX; }
    @Override public Object execute(...) { return baseMethod(left, right, ...); }
}
```

**关键前提（已验证）**：
- 运算符全程通过字符串名查找（`OperatorManager.getBinaryOperator(lexeme)`），无 `instanceof` 类型检查
- 序列化缓存也按字符串名存储（`SerializableParseCacheExporter` line 192），反序列化时按名查找（`SerializableParseCacheImporter` line 427）
- 因此可以安全地消除具体类，改用匿名类/工厂方法

---

## 核心思路

**给 `BaseBinaryOperator` 增加 name/priority 构造器，然后将 29 个重复的运算符类内联为 OperatorManager 中的匿名子类实例。** 保留有自定义逻辑的 6 个类不变。

### 涉及的 29 个去重类

| 分类 | 类名 | 运算符 | 委托方法 |
|------|------|--------|---------|
| 算术 | PlusOperator | `+` | `plus()` |
| 算术 | MinusOperator | `-` | `minus()` |
| 算术 | MultiplyOperator | `*` | `multiply()` |
| 算术 | DivideOperator | `/` | `divide()` |
| 位运算 | BitwiseAndOperator | `&` | `bitwiseAnd()` |
| 位运算 | BitwiseOrOperator | `\|` | `bitwiseOr()` |
| 位运算 | BitwiseXorOperator | `^` | `bitwiseXor()` |
| 位移 | BitwiseLeftShiftOperator | `<<` | `leftShift()` |
| 位移 | BitwiseRightShiftOperator | `>>` | `rightShift()` |
| 位移 | BitwiseRightShiftUnsignedOperator | `>>>` | `rightShiftUnsigned()` |
| 比较 | EqualOperator | `==` | `equals()` |
| 比较 | GreaterOperator | `>` | `compare() > 0` + nullCheck |
| 比较 | GreaterEqualOperator | `>=` | `compare() >= 0` + nullCheck |
| 比较 | LessOperator | `<` | `compare() < 0` + nullCheck |
| 比较 | LessEqualOperator | `<=` | `compare() <= 0` + nullCheck |
| 集合 | InOperator | `in` | `in()` |
| 集合 | NotInOperator | `notin` | `!in()` |
| 字符串 | LikeOperator | `like` | `like()` |
| 字符串 | NotLikeOperator | `notlike` | `!like()` |
| 赋值 | PlusAssignOperator | `+=` | `plus()` + set |
| 赋值 | MinusAssignOperator | `-=` | `minus()` + set |
| 赋值 | MultiplyAssignOperator | `*=` | `multiply()` + set |
| 赋值 | DivideAssignOperator | `/=` | `divide()` + set |
| 赋值 | RemainderAssignOperator | `%=` | `remainder()` + set |
| 位赋值 | BitwiseAndAssignOperator | `&=` | `bitwiseAnd()` + set |
| 位赋值 | BitwiseOrAssignOperator | `\|=` | `bitwiseOr()` + set |
| 位赋值 | BitwiseXorAssignOperator | `^=` | `bitwiseXor()` + set |
| 位移赋值 | BitwiseLeftShiftAssignOperator | `<<=` | `leftShift()` + set |
| 位移赋值 | BitwiseRightShiftAssignOperator | `>>=` | `rightShift()` + set |
| 位移赋值 | BitwiseRightShiftUnsignedAssignOperator | `>>>=` | `rightShiftUnsigned()` + set |

### 保留的 6 个有自定义逻辑的类

| 类名 | 原因 |
|------|------|
| `AssignOperator` `=` | 直接 set 值，无计算委托 |
| `LogicAndOperator` `&&`/`and` | 自定义 null→false 逻辑，多名称 |
| `LogicOrOperator` `\|\|`/`or` | 自定义逻辑，多名称 |
| `InstanceOfOperator` `instanceof` | 完全自定义（MetaClass 处理） |
| `UnequalOperator` `!=`/`<>` | 多名称 + 委托 `!equals()` |
| `RemainderOperator` `%` | 多名称模式（预留 `mod`） |

---

## 实现步骤

### Step 1: 修改 `BaseBinaryOperator`，增加 name/priority 构造器

**文件**: `src/main/java/com/alibaba/qlexpress4/runtime/operator/base/BaseBinaryOperator.java`

- 添加 `private final String operator` 和 `private final int priority` 字段
- 添加 `protected BaseBinaryOperator(String operator, int priority)` 构造器
- 将 `getOperator()` 和 `getPriority()` 实现移到基类（默认返回字段值）
- 保留无参构造器（供保留的自定义子类使用，如 `AssignOperator`）

### Step 2: 更新保留的自定义子类

修改保留的 6 个类，让它们继续使用无参构造器，自己实现 `getOperator()`/`getPriority()`。

### Step 3: 重构 `OperatorManager` 静态初始化

将静态块中对 `XxxOperator.getInstance()` 的调用全部替换为匿名子类：

```java
// 旧: binaryOperatorList.add(PlusOperator.getInstance());
// 新:
binaryOperatorList.add(new BaseBinaryOperator("+", QLPrecedences.ADD) {
    @Override public Object execute(Value l, Value r, QRuntime rt, QLOptions o, ErrorReporter e) {
        return plus(l, r, o, e);
    }
});
```

对于赋值运算符，inline 为：
```java
binaryOperatorList.add(new BaseBinaryOperator("+=", QLPrecedences.ASSIGN) {
    @Override public Object execute(Value l, Value r, QRuntime rt, QLOptions o, ErrorReporter e) {
        assertLeftValue(l, e);
        LeftValue lv = (LeftValue) l;
        Object result = plus(l, r, o, e);
        lv.set(result, e);
        return result;
    }
});
```

对于比较运算符（带 null check）：
```java
binaryOperatorList.add(new BaseBinaryOperator(">", QLPrecedences.COMPARE) {
    @Override public Object execute(Value l, Value r, QRuntime rt, QLOptions o, ErrorReporter e) {
        if (o.isAvoidNullPointer() && (l.get() == null || r.get() == null)) return false;
        return compare(l, r, e) > 0;
    }
});
```

### Step 4: 删除 29 个冗余类文件

删除以下目录下的对应文件：
- `operator/arithmetic/`: PlusOperator, MinusOperator, MultiplyOperator, DivideOperator, PlusAssignOperator, MinusAssignOperator, MultiplyAssignOperator, DivideAssignOperator, RemainderAssignOperator
- `operator/bit/`: BitwiseAndOperator, BitwiseOrOperator, BitwiseXorOperator, BitwiseAndAssignOperator, BitwiseOrAssignOperator, BitwiseXorAssignOperator
- `operator/bit/`: BitwiseLeftShiftOperator, BitwiseRightShiftOperator, BitwiseRightShiftUnsignedOperator, BitwiseLeftShiftAssignOperator, BitwiseRightShiftAssignOperator, BitwiseRightShiftUnsignedAssignOperator
- `operator/compare/`: EqualOperator, GreaterOperator, GreaterEqualOperator, LessOperator, LessEqualOperator
- `operator/collection/`: InOperator, NotInOperator
- `operator/string/`: LikeOperator, NotLikeOperator

### Step 5: 验证

```bash
mvn test
```

确保 TestSuiteRunner 全部通过，无编译错误。

---

## 预期收益

| 指标 | 优化前 | 优化后 |
|------|--------|--------|
| 运算符类文件数 | ~45 | ~16（减少 29 个） |
| 代码行数 | ~1100 行重复 | ~200 行内联注册 |
| OperatorManager.java | ~322 行 | ~420 行 |
| 新增运算符成本 | 新建 40 行类文件 | 添加 5 行匿名类 |

## 风险

- **极低**：运算符始终通过字符串名查找，不依赖具体类
- **序列化兼容**：已验证序列化缓存按字符串名而非类名存储
- **回滚容易**：删除的文件可通过 git 恢复
