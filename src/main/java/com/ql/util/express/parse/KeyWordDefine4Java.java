package com.ql.util.express.parse;

public class KeyWordDefine4Java {
    public final String[] splitWord = {
        // 位操作
        "^", "~", "&", "|", "<<", ">>",

        //四则运算
        "+", "-", "*", "/", "%", "++", "--",

        //分隔符号
        ".", ",", ":", ";", "(", ")", "{", "}", "[", "]", "?",

        //Boolean运算符号
        "!", "<", ">", "<=", ">=", "==", "!=", "&&", "||",

        "=", "/**", "**/",

        // lambda 表达式
        "->"
    };
    public final String[] keyWords = new String[] {
        "mod", "nor", "in",
        "for", "if", "when", "then", "else", "exportAlias", "alias",
        "break", "continue", "return", "macro", "function",
        "def", "exportDef", "new", "array", "anonymousNewArray",
        "like", "class", "VClass",
        "cast"
    };
    public final String[] nodeTypeDefines = new String[] {
        "ID:TYPE=WORDDEF",
        "EOF:TYPE=WORDDEF",
        "FUNCTION_NAME:TYPE=WORDDEF",
        "FUNCTION_DEFINE:TYPE=WORDDEF",
        "LEFT_BRACKET:TYPE=WORDDEF,DEFINE=(",
        "RIGHT_BRACKET:TYPE=WORDDEF,DEFINE=)",

        "XOR:TYPE=WORDDEF,DEFINE=^",
        "MAYBE:TYPE=WORDDEF,DEFINE=|",
        "OR:TYPE=WORDDEF,DEFINE=||",
        "LEFT_COMMENT:TYPE=WORDDEF,DEFINE=/**",
        "RIGHT_COMMENT:TYPE=WORDDEF,DEFINE=**/",
        "ARROW:TYPE=WORDDEF,DEFINE=->",
        "MULTI:TYPE=WORDDEF,DEFINE=*",

        "CONST_BYTE:TYPE=WORDDEF",
        "CONST_SHORT:TYPE=WORDDEF",
        "CONST_INTEGER:TYPE=WORDDEF",
        "CONST_LONG:TYPE=WORDDEF",
        "CONST_FLOAT:TYPE=WORDDEF",
        "CONST_DOUBLE:TYPE=WORDDEF",
        "CONST_NUMBER:TYPE=WORDDEF,DEFINE=CONST_BYTE|CONST_SHORT|CONST_INTEGER|CONST_LONG|CONST_FLOAT|CONST_DOUBLE",
        "CONST_CHAR:TYPE=WORDDEF",
        "CONST_STRING:TYPE=WORDDEF",
        "CONST_BOOLEAN:TYPE=WORDDEF",
        "CONST_CLASS:TYPE=WORDDEF",
        "CONST:TYPE=WORDDEF,DEFINE=CONST_NUMBER|CONST_CHAR|CONST_STRING|CONST_BOOLEAN|CONST_CLASS",

        "CHILD_EXPRESS:TYPE=EXPRESS,DEFINE=LEFT_BRACKET->CHILD_EXPRESS^$(RIGHT_BRACKET~|(EXPRESS$(,~$EXPRESS)"
            + "*$RIGHT_BRACKET~))",
        "[]:TYPE=EXPRESS,DEFINE=[~$EXPRESS*$]~#[]",

        "OP_LEVEL1:TYPE=OPERATOR,DEFINE=~|!",
        "OP_LEVEL2:TYPE=OPERATOR,DEFINE=++|--",
        "OP_LEVEL3:TYPE=OPERATOR,DEFINE=&|MAYBE|XOR|<<|>>",
        "OP_LEVEL4:TYPE=OPERATOR,DEFINE=*|/|mod|%",
        "OP_LEVEL5:TYPE=OPERATOR,DEFINE=+|-",
        "OP_LEVEL6:TYPE=OPERATOR,DEFINE=in|like",
        "OP_LEVEL7:TYPE=OPERATOR,DEFINE=>|>=|<|<=|==|!=",
        "OP_LEVEL8:TYPE=OPERATOR,DEFINE=&&",
        "OP_LEVEL9:TYPE=OPERATOR,DEFINE=OR|nor",

        "OP_LIST:TYPE=GROUP,DEFINE=OP_LEVEL1|OP_LEVEL2|OP_LEVEL3|OP_LEVEL4|OP_LEVEL5|OP_LEVEL6|OP_LEVEL7|OP_LEVEL8"
            + "|OP_LEVEL9|=|LEFT_BRACKET|RIGHT_BRACKET|[|]|{|}",

        "PARAMETER_LIST:TYPE=STATEMENT,DEFINE=LEFT_BRACKET~$(RIGHT_BRACKET~|(EXPRESS$(,~$EXPRESS)*$RIGHT_BRACKET~))",

        "VAR_DEFINE:TYPE=EXPRESS,DEFINE=(CONST_CLASS|VClass->CONST_STRING)$(([$])#[])*$ID->CONST_STRING#def",
        "EXPORT_VAR_DEFINE:TYPE=EXPRESS,DEFINE=exportDef^$CONST_CLASS$ID->CONST_STRING",
        "NEW_OBJECT:TYPE=EXPRESS,DEFINE=new->NEW_OBJECT^$CONST_CLASS$PARAMETER_LIST",
        "NEW_ARRAY:TYPE=EXPRESS,DEFINE=new->NEW_ARRAY^$CONST_CLASS$([]*)",
        "ANONY_NEW_ARRAY:TYPE=EXPRESS,DEFINE=[->anonymousNewArray^$(]~|(EXPRESS$(,~$EXPRESS)*$]~))",

        "NEW_VIR_OBJECT:TYPE=EXPRESS,DEFINE=new->NEW_VIR_OBJECT^$VClass->CONST_STRING$PARAMETER_LIST",

        "OPDATA:TYPE=EXPRESS,DEFINE=ANONY_NEW_ARRAY|VAR_DEFINE|EXPORT_VAR_DEFINE|NEW_OBJECT|NEW_ARRAY|NEW_VIR_OBJECT"
            + "|CHILD_EXPRESS|CONST|ID",

        "FIELD_CALL:TYPE=EXPRESS,DEFINE= .->FIELD_CALL^$(ID->CONST_STRING|class->CONST_STRING)",
        "METHOD_CALL:TYPE=EXPRESS,DEFINE=.->METHOD_CALL^$(ID->CONST_STRING|FUNCTION_NAME->CONST_STRING)$PARAMETER_LIST",
        "OBJECT_CALL:TYPE=EXPRESS,DEFINE=((COMMENT~)*$OPDATA$(COMMENT~)*)$(METHOD_CALL|FIELD_CALL)^*",

        "FUNCTION_CALL:TYPE=EXPRESS,DEFINE=(ID->CONST_STRING|FUNCTION_NAME->CONST_STRING)$PARAMETER_LIST#FUNCTION_CALL",

        "ARRAY_CALL:TYPE=EXPRESS,DEFINE=ARRAY_CALL_PRE$ARRAY_CALL_POST^*",
        "ARRAY_CALL_PRE:TYPE=EXPRESS,DEFINE=(FUNCTION_CALL|OBJECT_CALL)$([->ARRAY_CALL^$EXPRESS$]~)^*",
        "ARRAY_CALL_POST:TYPE=EXPRESS,DEFINE=(METHOD_CALL|FIELD_CALL)$([->ARRAY_CALL^$EXPRESS$]~)^*",

        "CAST_CALL:TYPE=EXPRESS,DEFINE=(LEFT_BRACKET~$CONST_CLASS$RIGHT_BRACKET~#cast)^*$((LAMBDA#LAMBDA)|ARRAY_CALL)",
        "EXPRESS_OP_L1:TYPE=EXPRESS,DEFINE=OP_LEVEL1^*$CAST_CALL",
        "EXPRESS_OP_L2:TYPE=EXPRESS,DEFINE=EXPRESS_OP_L1$OP_LEVEL2^*",
        "EXPRESS_OP_L3:TYPE=EXPRESS,DEFINE=EXPRESS_OP_L2$(OP_LEVEL3^$EXPRESS_OP_L2)^*",
        "EXPRESS_OP_L4:TYPE=EXPRESS,DEFINE=EXPRESS_OP_L3$(OP_LEVEL4^$EXPRESS_OP_L3)^*",
        "EXPRESS_OP_L5:TYPE=EXPRESS,DEFINE=EXPRESS_OP_L4$(OP_LEVEL5^$EXPRESS_OP_L4)^*",
        "EXPRESS_OP_L6:TYPE=EXPRESS,DEFINE=EXPRESS_OP_L5$(OP_LEVEL6^$EXPRESS_OP_L5)^*",
        "EXPRESS_OP_L7:TYPE=EXPRESS,DEFINE=EXPRESS_OP_L6$(OP_LEVEL7^$EXPRESS_OP_L6)^*",
        "EXPRESS_OP_L8:TYPE=EXPRESS,DEFINE=EXPRESS_OP_L7$(OP_LEVEL8^$EXPRESS_OP_L7)^*",
        "EXPRESS_OP_L9:TYPE=EXPRESS,DEFINE=EXPRESS_OP_L8$(OP_LEVEL9^$EXPRESS_OP_L8)^*",
        "EXPRESS_COMPUTER:TYPE=EXPRESS,DEFINE=EXPRESS_OP_L9",

        "EXPRESS_JUDGEANDSET:TYPE=EXPRESS,DEFINE=EXPRESS_COMPUTER$"
            + "(?->EXPRESS_JUDGEANDSET^$EXPRESS_COMPUTER$:~$EXPRESS_COMPUTER)^{0:1}",
        "EXPRESS_KEY_VALUE:TYPE=EXPRESS,DEFINE=EXPRESS_JUDGEANDSET$(:->EXPRESS_KEY_VALUE^$"
            + "(EXPRESS_JUDGEANDSET|STAT_BLOCK))^{0:1}",
        "EXPRESS_ASSIGN:TYPE=EXPRESS,DEFINE=EXPRESS_KEY_VALUE$(=^$EXPRESS_KEY_VALUE)^*",

        "EXPRESS_RETURN:TYPE=EXPRESS,DEFINE=return^$EXPRESS_ASSIGN",
        "BREAK_CALL:TYPE=EXPRESS,DEFINE=break^",
        "CONTINUE_CALL:TYPE=EXPRESS,DEFINE=continue^",
        "ALIAS_CALL:TYPE=EXPRESS,DEFINE=alias^$ID->CONST_STRING$EXPRESS_ASSIGN",
        "EXPORT_ALIAS_CALL:TYPE=EXPRESS,DEFINE=exportAlias^$ID->CONST_STRING$EXPRESS_ASSIGN",

        "OP_CALL:TYPE=EXPRESS,DEFINE=(ID->CONST_STRING|FUNCTION_NAME->CONST_STRING)$(EXPRESS$(,~$EXPRESS)*)"
            + "#FUNCTION_CALL",

        "EXPRESS:TYPE=EXPRESS,DEFINE=BREAK_CALL|CONTINUE_CALL|EXPRESS_RETURN|ALIAS_CALL|EXPORT_ALIAS_CALL"
            + "|EXPRESS_ASSIGN|OP_CALL",

        "STAT_SEMICOLON:TYPE=STATEMENT,DEFINE=;~|(EXPRESS$(EOF|;)~#STAT_SEMICOLON)",

        "STAT_IFELSE:TYPE=STATEMENT,DEFINE=(if|when->if)^$EXPRESS$then$(STAT_BLOCK|STATEMENT|EXPRESS)$else$"
            + "(STAT_BLOCK|STATEMENT)",
        "STAT_IF:TYPE=STATEMENT,DEFINE=(if|when->if)^$EXPRESS$then$(STAT_BLOCK|STATEMENT)",
        "STAT_IFELSE_JAVA:TYPE=STATEMENT,DEFINE=(if|when->if)^$CHILD_EXPRESS$(STAT_BLOCK|STATEMENT|EXPRESS)$else$"
            + "(STAT_BLOCK|STATEMENT)",
        "STAT_IF_JAVA:TYPE=STATEMENT,DEFINE=(if|when->if)^$CHILD_EXPRESS$(STAT_BLOCK|STATEMENT)",

        "PARAMETER_DEFINE:TYPE=STATEMENT,DEFINE=LEFT_BRACKET->CHILD_EXPRESS^$(RIGHT_BRACKET~|(VAR_DEFINE$(,"
            + "~$VAR_DEFINE)*$RIGHT_BRACKET~))",

        "STAT_FOR:TYPE=STATEMENT,DEFINE=for^$(LEFT_BRACKET~$STATEMENT{0:2}$EXPRESS$RIGHT_BRACKET~#CHILD_EXPRESS)"
            + "$STAT_BLOCK$;~*",
        "STAT_MACRO:TYPE=STATEMENT,DEFINE=macro^$ID->CONST_STRING$STAT_BLOCK$;~*",
        "STAT_FUNCTION:TYPE=STATEMENT,DEFINE=function^$ID->CONST_STRING$PARAMETER_DEFINE$STAT_BLOCK$;~*",
        "STAT_CLASS:TYPE=STATEMENT,DEFINE=class^$VClass->CONST_STRING$PARAMETER_DEFINE$STAT_BLOCK$;~*",

        "COMMENT:TYPE=BLOCK,DEFINE=LEFT_COMMENT$(RIGHT_COMMENT@)*$RIGHT_COMMENT#COMMENT",

        "STATEMENT:TYPE=STATEMENT,DEFINE=COMMENT|STAT_IFELSE|STAT_IF|STAT_IFELSE_JAVA|STAT_IF_JAVA|STAT_FOR"
            + "|STAT_MACRO|STAT_FUNCTION|STAT_CLASS|STAT_SEMICOLON",
        "LAMBDA:TYPE=EXPRESS,DEFINE=(LAMBDA_PARAMETER_DEFINE|ID)$ARROW~$LAMBDA_BODY",
        "LAMBDA_BODY:TYPE=BLOCK,DEFINE=STAT_BLOCK|EXPRESS",
        "LAMBDA_PARAMETER_DEFINE:TYPE=STATEMENT,DEFINE=LEFT_BRACKET->CHILD_EXPRESS^$(RIGHT_BRACKET~|"
            + "(ID->CONST_STRING$(,~$ID->CONST_STRING)*$RIGHT_BRACKET~))",
        "STAT_BLOCK:TYPE=BLOCK,DEFINE={->STAT_BLOCK^$STAT_LIST$}~",
        "STAT_LIST:TYPE=BLOCK,DEFINE=(STAT_BLOCK|STATEMENT)*",
        "PROGRAM:TYPE=BLOCK,DEFINE=STAT_LIST#STAT_BLOCK",
    };
    public final String[][] instructionFactoryMapping = {
        {"^,~,!,++,--,&,|,<<,>>,*,/,mod,%,+,-,like,>,>=,<,<=,==,!=,&&,||,nor,=,return,alias,exportAlias,ARRAY_CALL",
            "com.ql.util.express.instruction.OperatorInstructionFactory"},
        {"in", "com.ql.util.express.instruction.InInstructionFactory"},
        {"exportDef", "com.ql.util.express.instruction.OperatorInstructionFactory"},
        {"ID", "com.ql.util.express.instruction.LoadAttrInstructionFactory"},
        {"CONST,CONST_CLASS", "com.ql.util.express.instruction.ConstDataInstructionFactory"},
        {"[],STAT_SEMICOLON,STAT_BLOCK,FUNCTION_DEFINE,CHILD_EXPRESS",
            "com.ql.util.express.instruction.BlockInstructionFactory"},
        {"def", "com.ql.util.express.instruction.DefineInstructionFactory"},
        {"NEW_OBJECT,NEW_ARRAY,anonymousNewArray", "com.ql.util.express.instruction.NewInstructionFactory"},
        {"FIELD_CALL", "com.ql.util.express.instruction.FieldCallInstructionFactory"},
        {"METHOD_CALL", "com.ql.util.express.instruction.MethodCallInstructionFactory"},
        {"cast", "com.ql.util.express.instruction.CastInstructionFactory"},
        {"break", "com.ql.util.express.instruction.BreakInstructionFactory"},
        {"continue", "com.ql.util.express.instruction.ContinueInstructionFactory"},
        {"FUNCTION_CALL", "com.ql.util.express.instruction.CallFunctionInstructionFactory"},
        {"if,EXPRESS_JUDGEANDSET", "com.ql.util.express.instruction.IfInstructionFactory"},
        {"for", "com.ql.util.express.instruction.ForInstructionFactory"},
        {"function,class", "com.ql.util.express.instruction.FunctionInstructionFactory"},
        {"macro", "com.ql.util.express.instruction.MacroInstructionFactory"},
        {"NEW_VIR_OBJECT", "com.ql.util.express.instruction.NewVClassInstructionFactory"},
        {"COMMENT", "com.ql.util.express.instruction.NullInstructionFactory"},
        {"EXPRESS_KEY_VALUE", "com.ql.util.express.instruction.KeyValueInstructionFactory"},
        {"LAMBDA", "com.ql.util.express.instruction.LambdaInstructionFactory"}
    };
}
