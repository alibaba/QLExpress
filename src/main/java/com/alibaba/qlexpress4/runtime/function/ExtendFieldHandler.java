package com.alibaba.qlexpress4.runtime.function;

/**
 * Custom field-access handler bound to a specific receiver type.
 * <p>
 * It extends the behaviour of {@link com.alibaba.qlexpress4.runtime.ReflectLoader#loadField}
 * so that non-standard containers (such as Flink Row, JDBC ResultSet or user-defined
 * MapLike/CollectionLike structures) can be accessed with the regular {@code obj.fieldName}
 * syntax in QL expressions.
 * <p>
 * A handler is registered against a binding class via
 * {@link com.alibaba.qlexpress4.Express4Runner#addExtendFieldHandler(Class, ExtendFieldHandler)}
 * and is only invoked when the bean is assignable to that binding class. Binding to a class
 * keeps each registration isolated (handlers cannot conflict with each other) and frees the
 * caller from dealing with low-level runtime structures: just return the raw field value.
 *
 * <p>Example —— supporting Flink Row:
 * <pre>{@code
 * runner.addExtendFieldHandler(org.apache.flink.types.Row.class,
 *     (bean, fieldName) -> ((Row) bean).getField(fieldName));
 * }</pre>
 *
 * <p>If the handler returns {@code null}, QLExpress falls back to the default Java reflection
 * field-access logic of {@code ReflectLoader}.
 *
 * @author ayasaz
 * @since QLExpress4
 */
@FunctionalInterface
public interface ExtendFieldHandler {

    /**
     * Resolve the value of {@code fieldName} from the given bean.
     *
     * @param bean      the receiver object, guaranteed to be assignable to the binding class
     * @param fieldName the field name being accessed
     * @return the raw field value, or {@code null} to fall back to the default reflection logic
     */
    Object getField(Object bean, String fieldName);
}
