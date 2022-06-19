package com.alibaba.qlexpress4.parser.tree;

import com.alibaba.qlexpress4.parser.Token;

import java.util.List;

/**
 * Author: DQinYuan
 */
public class TryCatch extends Expr {

    private final Block body;

    /**
     * Nullable
     */
    private final Block tryFinal;

    private final List<CatchClause> tryCatch;

    public TryCatch(Token keyToken, Block body, Block tryFinal,
                    List<CatchClause> tryCatch) {
        super(keyToken);
        this.body = body;
        this.tryFinal = tryFinal;
        this.tryCatch = tryCatch;
    }

    public Block getBody() {
        return body;
    }

    public Block getTryFinal() {
        return tryFinal;
    }

    public List<CatchClause> getTryCatch() {
        return tryCatch;
    }

    public static class CatchClause {
        private final List<DeclType> exceptions;
        private final Identifier variable;
        private final Block body;

        public CatchClause(List<DeclType> exceptions, Identifier variable, Block body) {
            this.exceptions = exceptions;
            this.variable = variable;
            this.body = body;
        }

        public List<DeclType> getExceptions() {
            return exceptions;
        }

        public Identifier getVariable() {
            return variable;
        }

        public Block getBody() {
            return body;
        }
    }

    @Override
    public <R, C> R accept(QLProgramVisitor<R, C> visitor, C context) {
        return visitor.visit(this, context);
    }
}
