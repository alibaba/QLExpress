package com.alibaba.qlexpress4.parser;

import com.alibaba.qlexpress4.DefaultClassSupplier;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.parser.tree.Program;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class AstPrinterTest {

    @Test
    public void exprTest() {
        assertEquals("Program\n" +
                "| BinaryOpExpr +\n" +
                "  | GroupExpr (\n" +
                "    | a\n" +
                "  | BinaryOpExpr *\n" +
                "    | BinaryOpExpr *\n" +
                "      | 1\n" +
                "      | 8\n" +
                "    | GroupExpr (\n" +
                "      | BinaryOpExpr -\n" +
                "        | 2\n" +
                "        | 1\n", parseScript("(a)+1*8*(2-1)"));
    }

    @Test
    public void multiStmtTest() {
        assertEquals("Program\n" +
                "| LocalVarDeclareStmt int\n" +
                "  | a\n" +
                "  | 2\n" +
                "| AssignExpr =\n" +
                "  | m\n" +
                "  | NewExpr new\n" +
                "    | HashMap\n",
                parseScript("int a = 2;m = new HashMap()"));
    }

    private String parseScript(String script) {
        StringBuilder builder = new StringBuilder();
        Consumer<String> consumer = line -> builder.append(line).append('\n');
        AstPrinter astPrinter = new AstPrinter(consumer);
        new QLParser(Collections.emptyMap(), new Scanner(script, QLOptions.DEFAULT_OPTIONS),
                ImportManager.buildGlobalImportManager(Arrays.asList(
                        ImportManager.importPack("java.lang"),
                        ImportManager.importPack("java.util"),
                        ImportManager.importCls("java.util.function.Function")
                )), DefaultClassSupplier.INSTANCE).parse().accept(astPrinter, null);
        return builder.toString();
    }

}