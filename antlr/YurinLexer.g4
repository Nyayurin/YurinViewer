lexer grammar YurinLexer;

import UnicodeClasses;

Package
  : 'package'
  ;

Import
  : 'import'
  ;

Data
  : 'data'
  ;

Trait
  : 'trait'
  ;

Impl
  : 'impl'
  ;

Colon
  : ':'
  ;

Fun
  : 'fun'
  ;

Var
  : 'var'
  ;

Val
  : 'val'
  ;

Ref
  : 'ref'
  ;

Get
  : 'get'
  ;

Set
  : 'set'
  ;

Typealias
  : 'typealias'
  ;

Open
  : 'open'
  ;

Abstract
  : 'abstract'
  ;

Operator
  : 'operator'
  ;

Singleton
  : 'singleton'
  ;

Unsafe
  : 'unsafe'
  ;

Nothing
  : 'nothing'
  ;

Dynamic
  : 'dynamic'
  ;

LeftBracket
  : '{'
  ;

RightBracket
  : '}'
  ;

LeftAngle
  : '<'
  ;

RightAngle
  : '>'
  ;

LeftParen
  : '('
  ;

RightParen
  : ')'
  ;

LeftSquare
  : '['
  ;

RightSquare
  : ']'
  ;

Comma
  : ','
  ;

Exist
  : 'exist'
  ;

Dot
  : '.'
  ;

SafeDot
  : '?.'
  ;

OrOr
  : '||'
  ;

Or
  : '|'
  ;

AndAnd
  : '&&'
  ;

And
  : '&'
  ;

EqEqEq
  : '==='
  ;

NotEqEq
  : '!=='
  ;

EqEq
  : '=='
  ;

NotEq
  : '!='
  ;

Eq
  : '='
  ;

Greater
  : RightAngle
  ;

GreaterEq
  : '>='
  ;

Less
  : LeftAngle
  ;

LessEq
  : '<='
  ;

In
  : 'in'
  ;

NotIn
  : '!in'
  ;

Is
  : 'is'
  ;

NotIs
  : '!is'
  ;

Elvis
  : '?:'
  ;

Range
  : '..'
  ;

RangeEq
  : '..='
  ;

Plus
  : '+'
  ;

Minus
  : '-'
  ;

Multiply
  : '*'
  ;

Divide
  : '/'
  ;

Modulo
  : '%'
  ;

As
  : 'as'
  ;

SafeAs
  : 'as?'
  ;

Increment
  : '++'
  ;

Decrement
  : '--'
  ;

Not
  : '!'
  ;

UnsafeCast
  : '!!'
  ;

Nullable
  : '?'
  ;

Reference
  : '::'
  ;

This
  : 'this'
  ;

If
  : 'if'
  ;

Else
  : 'else'
  ;

Match
  : 'match'
  ;

Return
  : 'return'
  ;

Break
  : 'break'
  ;

Continue
  : 'continue'
  ;

Arrow
  : '->'
  ;

IntLiteral
  : [0-9]+
  ;

StringLiteral
  : '"' (~["\\] | '\\' .)* '"'
  ;

NL
  : [\r\n]+
  ;

WS
  : [ \t]+ -> channel(HIDDEN)
  ;

SingleLineComment
  : '//' ~[\r\n]* -> channel(HIDDEN)
  ;

MultiLineComment
  : '/*' .*? '*/' -> channel(HIDDEN)
  ;

Identifier
  : (Letter | '_') (Letter | '_' | UnicodeDigit)*
  | '`' ~([\r\n] | '`')+ '`'
  ;

fragment UnicodeDigit: UNICODE_CLASS_ND;

fragment Letter
    : UNICODE_CLASS_LU
    | UNICODE_CLASS_LL
    | UNICODE_CLASS_LT
    | UNICODE_CLASS_LM
    | UNICODE_CLASS_LO
    ;