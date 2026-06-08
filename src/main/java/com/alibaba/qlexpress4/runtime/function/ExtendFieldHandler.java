package com.alibaba.qlexpress4.runtime.function;

import com.alibaba.qlexpress4.runtime.Value;

/**
 * 自定义字段取值处理器。
 * 用于扩展 {@link com.alibaba.qlexpress4.runtime.ReflectLoader#loadField} 的行为，
 * 支持对非标准容器（如 Flink Row、JDBC ResultSet、自定义 MapLike/CollectionLike 等）进行属性取值。
 * 用户通过 {@link com.alibaba.qlexpress4.Express4Runner#addExtendFieldHandler} 注入到运行时。
 *
 * <p>使用示例 —— 支持 Flink Row:
 * <pre>{@code
 * runner.addExtendFieldHandler((bean, fieldName) -> {
 *     if (bean instanceof org.apache.flink.types.Row) {
 *         Row row = (Row) bean;
 *         return new DataValue(row.getField(fieldName));
 *     }
 *     return null; // 返回 null 表示当前处理器无法处理，继续走下一个
 * });
 * }</pre>
 *
 * <p>处理器按注册顺序依次调用：若某个处理器返回 null，日志层面意为"不匹配"，继续尝试下一个。
 * 第一个返回非 null {@code Value} 的处理器将消费本次取值请求，后续处理器不再执行。
 * 如果所有处理器均返回 null，则回退到 {@code ReflectLoader} 原有的 Java 反射取值逻辑。
 *
 * @author ayasaz
 * @since QLExpress4
 */
@FunctionalInterface
public interface ExtendFieldHandler {

    /**
     * 根据字段名从 bean 中取值。
     *
     * @param bean      当前对象（可能为任意类型，包括非标准容器）
     * @param fieldName 字段名
     * @return 取值结果，或 null 表示当前处理器不匹配该 bean 类型（交由下一个处理器或默认反射路径继续处理）
     */
    Value load(Object bean, String fieldName);
}
