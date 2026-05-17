# 需求

支持返回可直接序列化为json的预编译结果。

并且在远端另一台机器上的QLExpress，能够将其直接反序列化执行。

# 要求

 - 当前已有预编译接口com.alibaba.qlexpress4.Express4Runner.parseToLambda,但是对序列化为json不太友好
 - 该模型需要有版本信息，版本和QLExpress版本隔离，当涉及不兼容改动式才需要升级