lexer grammar YurinLexer;

import UnicodeClasses;

// SECTION: keyword

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

Typealias
  : 'typealias'
  ;

Impl
  : 'impl'
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

This
  : 'this'
  ;

Effect
  : 'effect'
  ;

Open
  : 'open'
  ;

Sealed
  : 'sealed'
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

Exist
  : 'exist'
  ;

Private
  : 'private'
  ;

Internal
  : 'internal'
  ;

Restricted
  : 'restricted'
  ;

Public
  : 'public'
  ;

Nothing
  : 'nothing'
  ;

Dynamic
  : 'dynamic'
  ;

In
  : 'in'
  ;

NotIn
  : Not In
  ;

Is
  : 'is'
  ;

NotIs
  : Not Is
  ;

As
  : 'as'
  ;

SafeAs
  : As Nullable
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

// SECTION: punctuation

Colon
  : ':'
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

Arrow
  : '->'
  ;

// SECTION: literal

IntLiteral
  : [0-9]+
  ;

StringLiteral
  : '"' (~["\\] | '\\' .)* '"'
  ;

// SECTION: identifier

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

// SECTION: general

NL
  : '\n' | '\r' '\n'?
  ;

WS
  : [\u0020\u0009\u000C]
    -> channel(HIDDEN)
  ;

LineComment
  : '//' ~[\r\n]*
    -> channel(HIDDEN)
  ;

DelimitedComment
  : '/*' ( DelimitedComment | . )*? '*/'
    -> channel(HIDDEN)
  ;

fragment Hidden
  : WS
  | LineComment
  | DelimitedComment
  ;