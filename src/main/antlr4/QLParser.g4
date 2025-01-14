parser grammar QLParser;

options {
    tokenVocab = QLexer;
}

@header {
    package com.alibaba.qlexpress4.aparser;
    import static com.alibaba.qlexpress4.aparser.ParserOperatorManager.OpType.*;
    import static com.alibaba.qlexpress4.QLPrecedences.*;
    import static com.alibaba.qlexpress4.aparser.InterpolationMode.*;
}

@members {
    ParserOperatorManager opM;
    InterpolationMode interpolationMode;
    public QLParser(TokenStream input, ParserOperatorManager opM, InterpolationMode interpolationMode) {    // custom constructor
        this(input);
        this.opM = opM;
        this.interpolationMode = interpolationMode;
    }
}

// grammar

program
    : importDeclaration* blockStatements? EOF
    ;

blockStatements
    :   blockStatement+
    ;

blockStatement
    :   localVariableDeclaration ';' # localVariableDeclarationStatement
    |   THROW expression ';' # throwStatement
    |   WHILE '(' expression ')' blockStatement # whileStatement
    |   FOR '(' forInit (forCondition=expression)? ';' (forUpdate=expression)? ')' blockStatement # traditionalForStatement
    |   FOR '(' declType? varId ':' expression ')' blockStatement # forEachStatement
    |   FUNCTION varId '(' formalOrInferredParameterList? ')' '{' blockStatements? RBRACE # functionStatement
    |   MACRO varId '{' blockStatements? RBRACE # macroStatement
    |   (BREAK | CONTINUE) ';' # breakContinueStatement
    |   RETURN expression? ';' # returnStatement
    |   ';' # emptyStatement
    |   expression ';'? # expressionStatement
    ;

localVariableDeclaration
    :   declType variableDeclaratorList
    ;

forInit
    : localVariableDeclaration ';'
    | expression ';'
    | ';'
    ;

variableDeclaratorList
    :   variableDeclarator (',' variableDeclarator)*
    ;

variableDeclarator
    :   variableDeclaratorId (EQ variableInitializer)?
    ;

variableDeclaratorId
    :   varId dims?
    ;

variableInitializer
    :   expression
    |   arrayInitializer
    ;

arrayInitializer
    :   LBRACE variableInitializerList? ','? RBRACE
    ;

variableInitializerList
    :   variableInitializer (',' variableInitializer)*
    ;

// decl type

declType
    :   primitiveType dims?
    |   clsType dims?
    ;

declTypeNoArr
    : primitiveType
    | clsType
    ;

primitiveType
    :   'byte'
    |   'short'
    |   'int'
    |   'long'
    |   'float'
    |   'double'
    |   'boolean'
    |   'char'
    ;

referenceType
    :   clsType dims?
    |   primitiveType dims
    ;

dims
    :   LBRACK RBRACK (LBRACK RBRACK)*
    ;

clsTypeNoTypeArguments
    :   varId ('.' varId)*
    ;

clsType
    :   varId ('.' varId)* typeArguments?
    ;

typeArguments
    :   LT typeArgumentList? (GT | RIGHSHIFT | URSHIFT)?
    |   NOEQ
    ;

typeArgumentList
    :   typeArgument (',' typeArgument)*
    ;

typeArgument
    :   referenceType
    |   wildcard
    ;

wildcard
    :   '?' wildcardBounds?
    ;

wildcardBounds
    :   'extends' referenceType
    |   'super' referenceType
    ;

// expression

expression
    :   leftHandSide assignOperator expression
    |   ternaryExpr
    ;

leftHandSide
    :   varId (pathPart)*
    ;

ternaryExpr
    :   condition=baseExpr[1] (QUESTION thenExpr=baseExpr[0] COLON elseExpr=expression)?
    ;

baseExpr [int p]
    : primary ({_input.LT(1).getType() != Token.EOF &&
        opM.isOpType(_input.LT(1).getText(), MIDDLE) && opM.precedence(_input.LT(1).getText()) >= $p}? leftAsso)*
    ;

leftAsso
    : binaryop baseExpr[opM.precedence(_input.LT(-1).getText()) + 1];

binaryop
    : opId | varId
    ;

// primary

primary
    : (prefixExpress)? primaryNoFix (pathPart)* (suffixExpress)?
    ;

prefixExpress
    : {_input.LT(1).getType() != Token.EOF && opM.isOpType(_input.LT(1).getText(), PREFIX)}? opId
    ;

suffixExpress
    : {_input.LT(1).getType() != Token.EOF && opM.isOpType(_input.LT(1).getText(), SUFFIX)}? opId
    ;

primaryNoFix
    :   literal # constExpr
    |   '(' declType ')' primary # castExpr
    |   '(' expression ')' # groupExpr
    |   NEW varId ('.' varId)* typeArguments? '(' argumentList? ')' # newObjExpr
    |   NEW declTypeNoArr dimExprs # newEmptyArrExpr
    |   NEW declTypeNoArr dims arrayInitializer # newInitArrExpr
    |   lambdaParameters ARROW ( '{' blockStatements? RBRACE | expression) # lambdaExpr
    |   varId # varIdExpr
    |   primitiveType # typeExpr
    |   '[' listItems? ']' # listExpr
    |   '{' mapEntries RBRACE # mapExpr
    |   '{' blockStatements? RBRACE # blockExpr
    |   IF '(' condition=expression ')' THEN? thenBody=ifBody (ELSE elseBody=ifBody)? # ifExpr
    |   TRY '{' blockStatements? RBRACE tryCatches? tryFinally? # tryCatchExpr
    |   SELECTOR_START SelectorVariable_VANME RBRACE # contextSelectExpr
    ;

ifBody
    :   '{' blockStatements? RBRACE
    |   blockStatement
    ;

listItems
    : expression (',' expression)*
    ;

dimExprs
    :   '[' expression ']' ('[' expression ']')*
    ;

tryCatches
    : tryCatch tryCatch*
    ;

tryCatch
    : 'catch' '(' catchParams ')' '{' blockStatements? RBRACE
    ;

catchParams
    : (declType ('|' declType)*)? varId
    ;

tryFinally
    : FINALLY '{' blockStatements? RBRACE
    ;

mapEntries
    : ':'
    | mapEntry (',' mapEntry)* ','?
    ;

mapEntry
    : mapKey ':' mapValue
    ;

mapValue
    : {_input.LT(-2).getText().equals("'@class'")}? QuoteStringLiteral # clsValue
    | expression # eValue
    ;

mapKey
    : idMapKey # idKey
    | doubleQuoteStringLiteral # stringKey
    | QuoteStringLiteral # quoteStringKey
    ;

idMapKey
    :   varId
    |   FOR
    |   IF
    |   ELSE
    |   WHILE
    |   BREAK
    |   CONTINUE
    |   RETURN
    |   FUNCTION
    |   MACRO
    |   IMPORT
    |   STATIC
    |   NEW
    |   BYTE
    |   SHORT
    |   INT
    |   LONG
    |   FLOAT
    |   DOUBLE
    |   CHAR
    |   BOOL
    |   NULL
    |   TRUE
    |   FALSE
    |   EXTENDS
    |   SUPER
    |   TRY
    |   CATCH
    |   FINALLY
    |   THROW
    |   CLASS
    |   THIS
    ;

pathPart
    :   '.' varId '(' argumentList? ')' # methodInvoke
    |   OPTIONAL_CHAINING varId '(' argumentList? ')' # optionalMethodInvoke
    |   SPREAD_CHAINING varId '(' argumentList? ')' # spreadMethodInvoke
    |   '.' fieldId # fieldAccess
    |   OPTIONAL_CHAINING fieldId # optionalFieldAccess
    |   SPREAD_CHAINING fieldId # spreadFieldAccess
    |   DCOLON varId # methodAccess
    |   '(' argumentList? ')' # callExpr
    |   '[' indexValueExpr? ']' # indexExpr
    |   {opM.isOpType(_input.LT(1).getText(), MIDDLE) && opM.precedence(_input.LT(1).getText()) == GROUP}? opId varId # customPath
    ;

fieldId
    :   varId
    |   CLASS
    |   QuoteStringLiteral
    ;

indexValueExpr
    :   expression # singleIndex
    |   start=expression? ':' end=expression? # sliceIndex
    ;

argumentList
    :   expression (',' expression)*
    ;

literal
    :   IntegerLiteral
    |   FloatingPointLiteral
    |   IntegerOrFloatingLiteral
    |   boolenLiteral
    |   QuoteStringLiteral
    |   doubleQuoteStringLiteral
    |   NULL
    ;

doubleQuoteStringLiteral
    : DOUBLE_QUOTE_OPEN (DyStrText | stringExpression)* DOUBLE_QUOTE_CLOSE
    ;

stringExpression
    : {interpolationMode == SCRIPT}? DyStrExprStart expression RBRACE
    | {interpolationMode == VARIABLE}? DyStrExprStart SelectorVariable_VANME RBRACE
    ;

boolenLiteral
    :   TRUE
    |   FALSE
    ;

lambdaParameters
    :   varId
    |   '(' formalOrInferredParameterList? ')'
    ;

formalOrInferredParameterList
    :   formalOrInferredParameter (',' formalOrInferredParameter)*
    ;

formalOrInferredParameter
    :   declType? varId
    ;

// import (not support import static now)

importDeclaration
    //  import xxx
    :   IMPORT varId ('.' varId)* ';' # importCls
    // import .*
    |   IMPORT varId ('.' varId)* (DOT MUL | DOTMUL) ';' # importPack
    ;

// id

assignOperator
    :   EQ
    |   RIGHSHIFT_ASSGIN
    |   URSHIFT_ASSGIN
    |   LSHIFT_ASSGIN
    |   ADD_ASSIGN
    |   SUB_ASSIGN
    |   AND_ASSIGN
    |   OR_ASSIGN
    |   MUL_ASSIGN
    |   MOD_ASSIGN
    |   DIV_ASSIGN
    |   XOR_ASSIGN
    ;

opId
    :   GT
    |   LT
    |   GE
    |   LE
    |   BANG
    |   TILDE
    |   ADD
    |   SUB
    |   MUL
    |   DIV
    |   INC
    |   DEC
    |   DOTMUL
    |   NOEQ
    |   RIGHSHIFT
    |   URSHIFT
    |   LEFTSHIFT
    |   BIT_AND
    |   BIT_OR
    |   MOD
    |   CARET
    |   assignOperator
    |   OPID
    ;

varId
    : ID
    | FUNCTION
    ;