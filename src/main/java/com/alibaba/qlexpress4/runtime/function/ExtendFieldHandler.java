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
 * keeps each registration isolated and frees the caller from dealing with low-level runtime
 * structures: just return the raw field value.
 * <p>
 * Once the bean matches the binding class the handler is <em>authoritative</em> for that bean's
 * fields: whatever it returns is taken as the field value, so returning {@code null} means the
 * field value itself is {@code null} (it does <b>not</b> fall back to Java reflection). This is
 * how the two cases below are distinguished:
 * <ul>
 *     <li>the bean type is not bound to any handler &rarr; the default reflection logic applies;</li>
 *     <li>the bean type is bound but the field value is {@code null} &rarr; {@code null} is returned.</li>
 * </ul>
 * If a bound container should signal "field does not exist" rather than yield {@code null}, throw
 * an exception from the handler.
 *
 * <p>Example —— supporting Flink Row:
 * <pre>{@code
 * runner.addExtendFieldHandler(org.apache.flink.types.Row.class,
 *     (bean, fieldName) -> ((Row) bean).getField(fieldName));
 * }</pre>
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
     * @return the raw field value; {@code null} means the field value itself is {@code null}
     */
    Object getField(Object bean, String fieldName);
}
