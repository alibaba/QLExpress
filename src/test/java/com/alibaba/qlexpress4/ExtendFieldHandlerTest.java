package com.alibaba.qlexpress4;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.qlexpress4.runtime.Value;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link com.alibaba.qlexpress4.runtime.function.ExtendFieldHandler}.
 * They verify class-bound custom field access: a matched handler resolves the value,
 * a non-matching bean falls through to the default reflection logic, a matched handler is
 * authoritative (so a {@code null} return means the field value itself is {@code null} and it wins
 * over reflection), and binding to a super type works for subtypes.
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

    /**
     * An ordinary Java bean with a public getter that is reachable through reflection.
     * Used to prove that a matched handler is authoritative and wins over the reflection path.
     */
    public static class PojoWithGetter {
        public String getStatus() {
            return "REFLECTED";
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
    public void testMatchedHandlerNullValueIsAuthoritative() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        runner.addExtendFieldHandler(RowLike.class, (bean, fieldName) -> ((RowLike) bean).getValue(fieldName));

        // the field exists in the container but its value is null: the matched handler is
        // authoritative, so we must get a non-null Value wrapping null - NOT a fall-through that
        // would end up reporting the field as missing.
        RowLike row = new RowLike(new String[] { "score" }, new Object[] { null });
        Value result = runner.loadField(row, "score");
        Assert.assertNotNull(result);
        Assert.assertNull(result.get());
    }

    @Test
    public void testMatchedHandlerWinsOverReflection() {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        // the bean has a reflective getter for "status", but a matched handler is authoritative
        // and its value must win over reflection.
        runner.addExtendFieldHandler(PojoWithGetter.class, (bean, fieldName) -> "HANDLER");

        Value result = runner.loadField(new PojoWithGetter(), "status");
        Assert.assertEquals("HANDLER", result.get());
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

    @Test
    public void extendFieldHandlerDocExample() {
        // tag::extendFieldHandler[]
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

        // RowLike is a non-standard container whose fields can only be read via getValue(name);
        // register a handler so it can be accessed with the regular obj.field syntax in scripts.
        runner.addExtendFieldHandler(RowLike.class, (bean, fieldName) -> ((RowLike) bean).getValue(fieldName));

        RowLike row = new RowLike(new String[] { "name", "age" }, new Object[] { "张三", 30 });
        Map<String, Object> context = new HashMap<>();
        context.put("row", row);

        Object name = runner.execute("row.name", context, QLOptions.DEFAULT_OPTIONS).getResult();
        Assert.assertEquals("张三", name);
        // end::extendFieldHandler[]
    }
}
