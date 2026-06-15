package com.alibaba.qlexpress4;

import com.alibaba.qlexpress4.runtime.Value;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link com.alibaba.qlexpress4.runtime.function.ExtendFieldHandler}.
 * They verify class-bound custom field access: a matched handler resolves the value,
 * a non-matching bean falls through to the default reflection logic, a handler returning
 * {@code null} falls back to the default logic, and binding to a super type works for subtypes.
 *
 * @author ayasaz
 */
public class ExtendFieldHandlerTest {

    /**
     * A non-standard MapLike container (a simplified model of Flink Row / Spark Row).
     * Fields are stored as String[] + Object[] and can only be read through getValue(name);
     * they are not reachable through ordinary Java reflection getters.
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

        runner.addExtendFieldHandler(RowLike.class, (bean, fieldName) -> ((RowLike) bean).getValue(fieldName));

        RowLike row = new RowLike(new String[] { "name", "age" }, new Object[] { "张三", 30 });
        Value result = runner.loadField(row, "name");
        Assert.assertEquals("张三", result.get());
    }

    @Test
    public void testCustomFieldHandlerNotMatches() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        runner.addExtendFieldHandler(RowLike.class, (bean, fieldName) -> ((RowLike) bean).getValue(fieldName));

        // a plain Java object that is not a RowLike should still go through the default reflection path.
        // String has a getter for bytes, so it works as an ordinary Java bean here.
        String hello = "hello";
        Value result = runner.loadField(hello, "bytes");
        Assert.assertNotNull(result);
        Assert.assertTrue(result.get() instanceof byte[]);
    }

    @Test
    public void testFieldHandlerReturnsNullFallsThrough() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        // always returns null -> should fall back to the default logic
        runner.addExtendFieldHandler(java.util.Map.class, (bean, fieldName) -> null);

        // the default loadField has built-in support for Map (returns MapItemValue)
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("key", "value");
        Value result = runner.loadField(map, "key");
        Assert.assertNotNull(result);
        Assert.assertEquals("value", result.get());
    }

    @Test
    public void testHandlerBoundToSuperTypeMatchesSubType() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        // bind to the super type; a subclass instance should still be dispatched to this handler
        runner.addExtendFieldHandler(RowLike.class, (bean, fieldName) -> ((RowLike) bean).getValue(fieldName));

        RowLike row = new RowLike(new String[] { "city" }, new Object[] { "杭州" }) {
        };
        Assert.assertEquals("杭州", runner.loadField(row, "city").get());
    }
}
