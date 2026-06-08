package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.runtime.Value;
import com.alibaba.qlexpress4.runtime.data.DataValue;
import org.junit.Assert;
import org.junit.Test;

/**
 * ExtendFieldHandler 单元测试。
 * 验证自定义字段取值处理器链：匹配返回、不匹配穿透到默认逻辑、多处理器链式调用。
 *
 * @author ayasaz
 */
public class ExtendFieldHandlerTest {

    /**
     * 模拟一个非标准 MapLike 容器（如 Flink Row、Spark Row 的简化模型）。
     * 字段通过 String[] + Object[] 存储，只能通过 getValue(name) 取值，无法通过 Java 反射 getter 直接访问。
     */
    static class RowLike {
        private final String[] fields;
        private final Object[] values;

        RowLike(String[] fields, Object[] values) {
            this.fields = fields;
            this.values = values;
        }

        Object getValue(String fieldName) {
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].equals(fieldName)) {
                    return values[i];
                }
            }
            return null;
        }
    }

    @Test
    public void testCustomFieldHandlerMatches() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        runner.addExtendFieldHandler((bean, fieldName) -> {
            if (bean instanceof RowLike) {
                Object value = ((RowLike) bean).getValue(fieldName);
                return value == null ? null : new DataValue(value);
            }
            return null;
        });

        RowLike row = new RowLike(new String[] { "name", "age" }, new Object[] { "张三", 30 });
        Value result = runner.loadField(row, "name");
        Assert.assertEquals("张三", result.get());
    }

    @Test
    public void testCustomFieldHandlerNotMatches() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        runner.addExtendFieldHandler((bean, fieldName) -> {
            if (bean instanceof RowLike) {
                Object value = ((RowLike) bean).getValue(fieldName);
                return value == null ? null : new DataValue(value);
            }
            return null;
        });

        // 非 RowLike 的普通 Java 对象仍应走默认反射路径拿到属性。
        // String 有 hashCode() 的 getter，可以作为普通 Java bean 验证。
        String hello = "hello";
        Value result = runner.loadField(hello, "bytes");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.get() instanceof byte[]);
    }

    @Test
    public void testFieldHandlerReturnsNullFallsThrough() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        // 始终返回 null —— 应回退到原有的默认逻辑
        runner.addExtendFieldHandler((bean, fieldName) -> null);

        // 默认 loadField 对 Map 有硬编码支持（返回 MapItemValue）
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("key", "value");
        Value result = runner.loadField(map, "key");
        Assert.assertNotNull(result);
        Assert.assertEquals("value", result.get());
    }

    @Test
    public void testMultipleHandlersChained() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        // Handler 1: 不匹配 RowLike（始终返回 null）
        runner.addExtendFieldHandler((bean, fieldName) -> null);

        // Handler 2: 匹配 RowLike
        runner.addExtendFieldHandler((bean, fieldName) -> {
            if (bean instanceof RowLike) {
                return new DataValue(((RowLike) bean).getValue(fieldName));
            }
            return null;
        });

        RowLike row = new RowLike(new String[] { "city" }, new Object[] { "杭州" });
        Assert.assertEquals("杭州", runner.loadField(row, "city").get());
    }
}
