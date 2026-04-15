package com.alibaba.qlexpress4.runtime.function;

/**
 * 实现控制流函数,函数内的参数不会在编译阶段求值,而是在运行阶段求值
 * 参数会以 QLambda 形式传入，需要手动调用 .get() 触发求值
 * Author: zimoa
 */
public interface LazyArgCustomFunction extends CustomFunction {
}
