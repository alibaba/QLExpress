package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

public class ImportStmt extends Stmt {

    public enum ImportType {
        /**
         * import fixed on class, for example:
         *    import java.util.Function;
         */
        FIXED,
        /**
         * import by prefix path, for example:
         *    import java.util.*
         */
        PREFIX
    }


    private final ImportType importType;

    private final String path;

    /**
     * import static
     * always false now
     */
    private final boolean staticImport;

    /**
     * @param keyToken last import package symbol, for example, java.util.function.Function,  is `Function`
     * @param importType
     * @param path
     * @param staticImport
     */
    public ImportStmt(Token keyToken, ImportType importType, String path, boolean staticImport) {
        super(keyToken);
        this.importType = importType;
        this.path = path;
        this.staticImport = staticImport;
    }

    public ImportType getImportType() {
        return importType;
    }

    public String getPath() {
        return path;
    }

    public boolean isStaticImport() {
        return staticImport;
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
