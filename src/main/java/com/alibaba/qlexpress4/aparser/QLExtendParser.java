package com.alibaba.qlexpress4.aparser;

import com.alibaba.qlexpress4.aparser.ParserOperatorManager.OpType;

public class QLExtendParser extends QLParser {

    private final ParserOperatorManager opM;

    private final InterpolationMode interpolationMode;

    public QLExtendParser(AliasTokenStream input, ParserOperatorManager opM, InterpolationMode interpolationMode) {
        super(input);
        this.opM = opM;
        this.interpolationMode = interpolationMode;
    }

    @Override
    protected boolean isOpType(String lexeme, OpType opType) {
        return opM.isOpType(lexeme, opType);
    }

    @Override
    protected Integer precedence(String lexeme) {
        return opM.precedence(lexeme);
    }

    @Override
    protected InterpolationMode getInterpolationMode() {
        return interpolationMode;
    }
}
