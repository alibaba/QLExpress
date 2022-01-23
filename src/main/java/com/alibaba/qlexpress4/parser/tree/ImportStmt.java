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
     * @param keyToken last import package symbol, for example, java.util.function.Function,  is `Function`
     * @param importType
     * @param path
     */
    public ImportStmt(Token keyToken, ImportType importType, String path) {
        super(keyToken);
        this.importType = importType;
        this.path = path;
    }

    public ImportType getImportType() {
        return importType;
    }

    public String getPath() {
        return path;
    }
}
