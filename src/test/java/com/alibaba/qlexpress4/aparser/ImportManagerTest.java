package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.DefaultClassSupplier;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class ImportManagerTest {

    public static class TestImportInner {
        public static class TestImportInner2 {

        }
    }

    @Test
    public void loadTest() {
        ImportManager importManager = new ImportManager(DefaultClassSupplier.getInstance(),
                new ArrayList<>(), new HashMap<>());
        ImportManager.LoadPartQualifiedResult result0 = importManager.loadPartQualified(Arrays.asList("Function"));
        assertNull(result0.getCls());
        importManager.addImport(ImportManager.importPack("java.util.function"));

        ImportManager.LoadPartQualifiedResult result1 = importManager.loadPartQualified(Arrays.asList("Function"));
        assertEquals(Function.class, result1.getCls());
        assertEquals(1, result1.getRestIndex());

        ImportManager.LoadPartQualifiedResult result2 = importManager.loadPartQualified(
                Arrays.asList("java", "util", "function", "Function", "a", "b"));
        assertEquals(Function.class, result2.getCls());
        assertEquals(4, result2.getRestIndex());

        ImportManager.LoadPartQualifiedResult result3 = importManager.loadPartQualified(
                Arrays.asList("com", "alibaba", "qlexpress4", "aparser",
                        "ImportManagerTest", "TestImportInner", "TestImportInner2"));
        assertEquals(TestImportInner.TestImportInner2.class, result3.getCls());

        ImportManager.LoadPartQualifiedResult result4 = importManager.loadPartQualified(
                Arrays.asList("Function", "value"));
        assertEquals(Function.class, result4.getCls());
        assertEquals(1, result4.getRestIndex());

        ImportManager.LoadPartQualifiedResult result5 = importManager.loadPartQualified(
                Arrays.asList("Function", "TT", "v"));
        assertEquals(1, result5.getRestIndex());
    }

    @Test
    public void loadInnerTest() {
        ImportManager importManager = new ImportManager(DefaultClassSupplier.getInstance(),
                new ArrayList<>(), new HashMap<>());
        importManager.addImport(ImportManager.importInnerCls(
                "com.alibaba.qlexpress4.aparser.ImportManagerTest"));
        ImportManager.LoadPartQualifiedResult result = importManager.loadPartQualified(
                Arrays.asList("TestImportInner", "TestImportInner2"));
        assertEquals(TestImportInner.TestImportInner2.class, result.getCls());

        ImportManager.LoadPartQualifiedResult result2 = importManager.loadPartQualified(
                Arrays.asList("TestImportInner", "testImportInner2"));
        assertEquals(TestImportInner.class, result2.getCls());
        assertEquals(1, result2.getRestIndex());
    }
}