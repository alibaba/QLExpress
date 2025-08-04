parser grammar QLParser;

options {
    tokenVocab = QLexer;
}

@header {
    package com.alibaba.qlexpress4.aparser;
    import com.alibaba.qlexpress4.aparser.ParserOperatorManager.OpType;
    import static com.alibaba.qlexpress4.aparser.ParserOperatorManager.OpType.*;
    import static com.alibaba.qlexpress4.QLPrecedences.*;
    import static com.alibaba.qlexpress4.aparser.InterpolationMode.*;
}

@members {
    protected boolean isOpType(String lexeme, OpType opType) {
        return false;
    }
    protected Integer precedence(String lexeme) {
        return 0;
    }
    protected InterpolationMode getInterpolationMode() {
        return SCRIPT;
    }
}

// grammar

program
    : (newlines? importDeclaration)* newlines? blockStatements? EOF
    ;

blockStatements
    :   blockStatement+
    ;

newlines : NEWLINE+;

nextStatement
    : {_input.LA(1) == Token.EOF || _input.LA(1) == QLexer.RBRACE}? | ';' | NEWLINE;

blockStatement
    :   localVariableDeclaration ';' # localVariableDeclarationStatement
    |   THROW expression nextStatement # throwStatement
    |   WHILE '(' newlines? expression newlines? ')' '{' newlines? blockStatements? newlines? '}' # whileStatement
    |   FOR '(' newlines? forInit (forCondition=expression)? ';' newlines? (forUpdate=expression)? newlines? ')' '{' newlines? blockStatements? newlines? '}' # traditionalForStatement
    |   FOR '(' newlines? declType? varId ':' expression newlines? ')' '{' newlines? blockStatements? newlines? '}' # forEachStatement
    |   FUNCTION varId '(' newlines? formalOrInferredParameterList? newlines? ')' LBRACE newlines? blockStatements? newlines? RBRACE # functionStatement
    |   MACRO varId LBRACE newlines? blockStatements? newlines? RBRACE # macroStatement
    |   (BREAK | CONTINUE) nextStatement # breakContinueStatement
    |   RETURN expression? nextStatement # returnStatement
    |   (';' | NEWLINE) # emptyStatement
    |   expression nextStatement # expressionStatement
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
    :   variableDeclarator (newlines? ',' newlines? variableDeclarator)*
    ;

variableDeclarator
    :   variableDeclaratorId (EQ newlines? variableInitializer)?
    ;

variableDeclaratorId
    :   varId dims?
    ;

variableInitializer
    :   expression
    |   arrayInitializer
    ;

arrayInitializer
    :   LBRACE newlines? variableInitializerList? newlines? RBRACE
    ;

variableInitializerList
    :   variableInitializer (newlines? ',' newlines? variableInitializer)* ','?
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
    :   LT newlines? typeArgumentList? newlines? (GT | RIGHSHIFT | URSHIFT)?
    |   NOEQ
    ;

typeArgumentList
    :   typeArgument (newlines? ',' newlines? typeArgument)*
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
    :   leftHandSide assignOperator newlines? expression
    |   ternaryExpr
    ;

leftHandSide
    :   varId (newlines? pathPart)*
    ;

ternaryExpr
    :   condition=baseExpr[1] (QUESTION newlines? thenExpr=baseExpr[0] COLON newlines? elseExpr=expression)?
    ;

baseExpr [int p]
    : primary ({_input.LA(1) != Token.EOF && _input.LA(1) != QLexer.NEWLINE &&
        isOpType(_input.LT(1).getText(), MIDDLE) && precedence(_input.LT(1).getText()) >= $p}? leftAsso)*
    ;

leftAsso
    : binaryop newlines? baseExpr[precedence(_localctx.binaryop().getStart().getText()) + 1];

binaryop
    : opId | varId
    ;

// primary

primary
    : (prefixExpress)? primaryNoFixPathable (newlines? pathPart)* (suffixExpress)?
    | primaryNoFixNonPathable
    ;

prefixExpress
    : {_input.LT(1).getType() != Token.EOF && isOpType(_input.LT(1).getText(), PREFIX)}? opId
    ;

suffixExpress
    : {_input.LT(1).getType() != Token.EOF && isOpType(_input.LT(1).getText(), SUFFIX)}? opId
    ;

primaryNoFixPathable
    :   literal # constExpr
    |   '(' newlines? declType newlines? ')' primary # castExpr
    |   '(' newlines? expression newlines? ')' # groupExpr
    |   NEW varId ('.' varId)* typeArguments? '(' newlines? argumentList? newlines? ')' # newObjExpr
    |   NEW declTypeNoArr dimExprs # newEmptyArrExpr
    |   NEW declTypeNoArr dims arrayInitializer # newInitArrExpr
    |   varId # varIdExpr
    |   primitiveType # typeExpr
    |   '[' newlines? listItems? newlines? ']' # listExpr
    |   LBRACE newlines? mapEntries newlines? RBRACE # mapExpr
    |   LBRACE newlines? blockStatements? newlines? RBRACE # blockExpr
    |   SELECTOR_START SelectorVariable_VANME # contextSelectExpr
    ;

primaryNoFixNonPathable
    :   qlIf # ifExpr
    |   TRY LBRACE newlines? blockStatements? newlines? RBRACE tryCatches? (newlines? tryFinally)? # tryCatchExpr
    |   lambdaParameters ARROW newlines? ( LBRACE newlines? blockStatements? newlines? RBRACE | expression) # lambdaExpr
    ;

qlIf : IF '(' newlines? condition=expression newlines? ')' newlines? THEN? newlines? thenBody (newlines? ELSE newlines? elseBody)?;

thenBody
    : LBRACE newlines? blockStatements? newlines? RBRACE
    | expression
    | blockStatement
    ;

elseBody
    : LBRACE newlines? blockStatements? newlines? RBRACE
    // if ... else ...  if ...
    | qlIf
    | expression
    | blockStatement
    ;

listItems
    : expression (newlines? ',' newlines? expression)* ','?
    ;

dimExprs
    :   ('[' newlines? expression newlines? ']')+
    ;

tryCatches
    : tryCatch (newlines? tryCatch)*
    ;

tryCatch
    : 'catch' '(' catchParams ')' LBRACE newlines? blockStatements? newlines? RBRACE
    ;

catchParams
    : (declType ('|' declType)*)? varId
    ;

tryFinally
    : FINALLY LBRACE newlines? blockStatements? newlines? RBRACE
    ;

mapEntries
    : ':'
    | mapEntry (',' newlines? mapEntry)* ','?
    ;

mapEntry
    : mapKey newlines? ':' newlines? mapValue
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
    :   '.' varId '(' newlines? argumentList? newlines? ')' # methodInvoke
    |   OPTIONAL_CHAINING varId '(' newlines? argumentList? newlines? ')' # optionalMethodInvoke
    |   SPREAD_CHAINING varId '(' newlines? argumentList? newlines? ')' # spreadMethodInvoke
    |   '.' fieldId # fieldAccess
    |   OPTIONAL_CHAINING fieldId # optionalFieldAccess
    |   SPREAD_CHAINING fieldId # spreadFieldAccess
    |   DCOLON varId # methodAccess
    |   '(' newlines? argumentList? newlines? ')' # callExpr
    |   '[' newlines? indexValueExpr? newlines? ']' # indexExpr
    |   {isOpType(_input.LT(1).getText(), MIDDLE) && precedence(_input.LT(1).getText()) == GROUP}? opId newlines? varId # customPath
    ;

fieldId
    :   varId
    |   CLASS
    |   QuoteStringLiteral
    ;

indexValueExpr
    :   expression # singleIndex
    |   start=expression? newlines? ':' newlines? end=expression? # sliceIndex
    ;

argumentList
    :   expression (newlines? ',' newlines? expression)*
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
    : {getInterpolationMode() == DISABLE}? DOUBLE_QUOTE StaticStringCharacters? DOUBLE_QUOTE
    | DOUBLE_QUOTE (DyStrText | stringExpression)* DOUBLE_QUOTE
    ;

stringExpression
    : {getInterpolationMode() == SCRIPT}? DyStrExprStart newlines? expression newlines? RBRACE
    | {getInterpolationMode() == VARIABLE}? DyStrExprStart SelectorVariable_VANME
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
    :   formalOrInferredParameter (newlines? ',' newlines? formalOrInferredParameter)*
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