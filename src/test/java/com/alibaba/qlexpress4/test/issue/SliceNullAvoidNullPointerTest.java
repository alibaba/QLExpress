package com.alibaba.qlexpress4.test.issue;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.exception.QLRuntimeException;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Regression test: SliceInstruction crashes on null operand in RIGHT ([start:])
 * and COPY ([:]) modes even when avoidNullPointer is enabled.
 *
 * Root cause: indexAbleLen() is called before the null check in these modes,
 * and indexAbleLen() throws NONINDEXABLE_OBJECT for null regardless of
 * avoidNullPointer. IndexInstruction and GetFieldInstruction handle this
 * correctly, but SliceInstruction did not.
 */
public class SliceNullAvoidNullPointerTest {

    private final Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);

    @Test
    public void sliceRightMode_nullWithAvoidNullPointer_shouldReturnNull() {
        // a[5:] where a is null -- RIGHT mode
        HashMap<String, Object> context = new HashMap<>();
        context.put("a", null);
        QLOptions options = QLOptions.builder().avoidNullPointer(true).build();

        Object result = runner.execute("a[5:]", context, options).getResult();
        assertNull(result);
    }

    @Test
    public void sliceCopyMode_nullWithAvoidNullPointer_shouldReturnNull() {
        // a[:] where a is null -- COPY mode
        HashMap<String, Object> context = new HashMap<>();
        context.put("a", null);
        QLOptions options = QLOptions.builder().avoidNullPointer(true).build();

        Object result = runner.execute("a[:]", context, options).getResult();
        assertNull(result);
    }

    @Test
    public void sliceLeftMode_nullWithAvoidNullPointer_shouldReturnNull() {
        // a[:3] where a is null -- LEFT mode (already worked, included for completeness)
        HashMap<String, Object> context = new HashMap<>();
        context.put("a", null);
        QLOptions options = QLOptions.builder().avoidNullPointer(true).build();

        Object result = runner.execute("a[:3]", context, options).getResult();
        assertNull(result);
    }

    @Test
    public void sliceBothMode_nullWithAvoidNullPointer_shouldReturnNull() {
        // a[1:3] where a is null -- BOTH mode (already worked, included for completeness)
        HashMap<String, Object> context = new HashMap<>();
        context.put("a", null);
        QLOptions options = QLOptions.builder().avoidNullPointer(true).build();

        Object result = runner.execute("a[1:3]", context, options).getResult();
        assertNull(result);
    }

    @Test(expected = QLRuntimeException.class)
    public void sliceRightMode_nullWithoutAvoidNullPointer_shouldThrow() {
        HashMap<String, Object> context = new HashMap<>();
        context.put("a", null);
        runner.execute("a[5:]", context, QLOptions.DEFAULT_OPTIONS).getResult();
    }

    @Test(expected = QLRuntimeException.class)
    public void sliceCopyMode_nullWithoutAvoidNullPointer_shouldThrow() {
        HashMap<String, Object> context = new HashMap<>();
        context.put("a", null);
        runner.execute("a[:]", context, QLOptions.DEFAULT_OPTIONS).getResult();
    }

    @Test
    public void sliceRightMode_normalList_shouldWork() {
        HashMap<String, Object> context = new HashMap<>();
        context.put("a", List.of(1, 2, 3, 4, 5));
        QLOptions options = QLOptions.builder().avoidNullPointer(true).build();

        Object result = runner.execute("a[2:]", context, options).getResult();
        assertTrue(result instanceof List);
        assertEquals(List.of(3, 4, 5), result);
    }

    @Test
    public void chainedSlice_nullWithAvoidNullPointer_shouldReturnNull() {
        // null.field[3:] -- tests chaining with field access + slice
        HashMap<String, Object> context = new HashMap<>();
        context.put("a", null);
        QLOptions options = QLOptions.builder().avoidNullPointer(true).build();

        Object result = runner.execute("a.items[3:]", context, options).getResult();
        assertNull(result);
    }
}
