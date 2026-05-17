# 需求

支持返回序列化的脚本预编译结果。在远端另一台机器（通常是安卓客户端）上的QLExpress，能够将其直接反序列化通过com.alibaba.qlexpress4.Express4Runner.addFunctionsDefinedInScript将其中函数加载。

# 要求

- 暂时只需要考虑远程addFunctionsDefinedInScript场景，不需要考虑执行
- 该模型需要有版本信息，版本和QLExpress版本隔离，当涉及不兼容改动式才需要升级
- 内存极致优化，用户常见脚本超过4000行，客户端通常是安卓，性能较差，应该避免序列化结果占用太多内存（可能要放弃json）。如果最后的表示比较抽象，需要提供函数将抽象表达翻译成人类易读结果