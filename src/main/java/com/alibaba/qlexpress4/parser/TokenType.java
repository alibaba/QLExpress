package com.alibaba.qlexpress4.parser;

public enum TokenType {
    // identifier, variable or function name
    ID,
    // middle operator
    // +
    ADD,
    // +=
    ADD_ASSIGN,
    // -
    SUB,
    // -=
    SUB_ASSIGN,
    // *
    MUL,
    // *=
    MUL_ASSIGN,
    // /
    DIV,
    // /=
    DIV_ASSIGN,
    // %
    MOD,
    // %=
    MOD_ASSIGN,
    // =
    ASSIGN,
    // ==
    EQUAL,
    // !=
    NOTEQUAL,
    // &&
    AND,
    // &
    BITAND,
    // &=
    AND_ASSIGN,
    // ||
    OR,
    // |
    BITOR,
    // |=
    OR_ASSIGN,
    // <
    LT,
    // <=
    LE,
    // <<
    LSHIFT,
    // <<=
    LSHIFT_ASSIGN,
    // >
    GT,
    // >=
    GE,
    // >>
    RSHIFT,
    // >>=
    RSHIFT_ASSIGN,
    // >>>
    URSHIFT,
    // >>>=
    URSHIFT_ASSIGN,
    // ->
    ARROW,

    // before unary operator
    // ^
    CARET,
    // ~
    TILDE,
    // !
    BANG,

    // after unary operator
    // ++
    INC,
    // --
    DEC,

    // Separator
    // (
    LPAREN,
    // )
    RPAREN,
    // {
    LBRACE,
    // }
    RBRACE,
    // [
    LBRACK,
    // ]
    RBRACK,
    // ?
    QUESTION,
    // :
    COLON,
    // ;
    SEMI,
    // ,
    COMMA,
    // .
    DOT,

    NUMBER,
    STRING,
    // key word: for,if,else,in,while,break,continue,null etc.
    KEY_WORD,
    // built-in type: byte,short,int,long
    // float, double
    // boolean,char
    TYPE
}
