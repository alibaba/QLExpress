# QLExpress

[![Join the chat at https://gitter.im/QLExpress/Lobby](https://badges.gitter.im/QLExpress/Lobby.svg)](https://gitter.im/QLExpress/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

# 1. Background introduction
  A dynamic script engine parsing tool designed by Ali's strong requirements for e-commerce business rules, expressions (Boolean combinations), special mathematical formula calculations (high precision), syntax analysis, and secondary script customization. It has a strong influence in Alibaba Group. At the same time, in order to continuously optimize and carry forward the spirit of open source contribution, it was open sourced in 2012.
  The QLExpress script engine is widely used in Ali's e-commerce business scenarios and has the following characteristics:
   1. Thread safety. Temporary variables generated during engine operation are all threadlocal.
   2. Efficient execution. The time-consuming script compilation process can be cached on the local machine. The temporary variable creation at runtime uses the buffer pool technology, which is equivalent to groovy performance.
   3. Weak-type scripting language, similar to groovy and javascript, although slower than strong-type scripting language, it greatly enhances the flexibility of the business.
   4. Security control can prevent dead loops and high-risk system API calls by setting relevant operating parameters.
   5. The code is streamlined and the dependency is minimal. The 250k jar package is suitable for all java operating environments and is also widely used in the low-end pos machines of the android system.


# 2. Dependency

```xml
<dependency>
  <groupId>com.alibaba</groupId>
  <artifactId>QLExpress</artifactId>
  <version>3.2.0</version>
</dependency>
```

# 3. How to call in Java
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
# 4. Grammar

### QLExpress Vs Java - Differences
- Does not support try{}catch{}
- Comments currently only support /** **/, single-line comments are not supported //
- Does not support lambda expressions in java8
- Does not support for loop collection operation for (GRCRouteLineResultDTO item: list)
- Weakly typed languages, please do not define type declarations, let alone use Templete (Map<String,List> and the like)
- The declaration of array is different
- min, max, round, print, println, like, in are all keywords of the system default functions, please do not use them as variable names
