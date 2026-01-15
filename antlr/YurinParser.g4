parser grammar YurinParser;

options { tokenVocab = YurinLexer; }

yurinFile
  : NL* (packageHeader NL+)? (importHeader NL+)* (declaration NL+)* declaration? NL* EOF
  ;

packageHeader
  : Package (Identifier Dot)* Identifier
  ;

importHeader
  : Import (Identifier Dot)* Identifier
  ;

declaration
  : dataDeclaration
  | traitDeclaration
  | implDeclaration
  | functionDeclaration
  | propertyDeclaration
  | typeAliasDeclaration
  ;

dataDeclaration
  : modifiers (Val | Ref) Data Identifier typeParameters? primaryConstructorValueParameters? inheritance? classBody?
  ;

traitDeclaration
  : modifiers Trait Identifier typeParameters? inheritance? classBody?
  ;

implDeclaration
  : modifiers Impl typeReference Colon typeReference classBody?
  ;

functionDeclaration
  : modifiers Fun typeParameters? (existTypeReference Dot)? Identifier valueParameters (Colon existTypeReference)? (block | (Eq NL? expression))?
  ;

propertyDeclaration
  : modifiers (Var | Val) (existTypeReference Dot)? Identifier (Colon existTypeReference)? (Eq NL? expression)? (NL propertyGetter)? (NL propertySetter)?
  ;

typeAliasDeclaration
  : modifiers Typealias Identifier typeParameters? Eq existTypeReference
  ;

inheritance
  : Colon existTypeReference valueArguments?
  ;

propertyGetter
  : modifiers Get (valueParameters (block | (Eq NL? expression)))?
  ;

propertySetter
  : modifiers Set (valueParameters (block | (Eq NL? expression)))?
  ;

modifiers
  : modifier*
  ;

modifier
  : Open
  | Abstract
  | Operator
  | Singleton
  ;

classBody
  : LeftBracket NL* (declaration NL*)* declaration? NL* RightBracket
  ;

typeParameters
  : LeftAngle (typeParameter Comma)* typeParameter? RightAngle
  ;

typeParameter
  : Identifier
  ;

typeArguments
  : LeftAngle (typeArgument Comma)* typeArgument RightAngle
  ;

typeArgument
  : existTypeReference
  ;

primaryConstructorValueParameters
  : primaryConstructorSingleLineValueParameters
  | primaryConstructorMultiLineValueParameters
  ;

primaryConstructorSingleLineValueParameters
  : LeftParen ((Var | Val)? valueParameter Comma)* ((Var | Val)? valueParameter)? RightParen
  ;

primaryConstructorMultiLineValueParameters
  : LeftParen NL* ((Var | Val)? valueParameter Comma? NL*)* ((Var | Val)? valueParameter)? Comma? NL* RightParen
  ;

lambdaValueParameters
  : lambdaSingleLineValueParameters
  | lambdaMultiLineValueParameters
  ;

lambdaSingleLineValueParameters
  : (valueParameter Comma)* valueParameter?
  ;

lambdaMultiLineValueParameters
  : NL* (valueParameter Comma? NL*)* (valueParameter Comma?)? NL*
  ;

valueParameters
  : singleLineValueParameters
  | multiLineValueParameters
  ;

singleLineValueParameters
  : LeftParen (valueParameter Comma)* valueParameter? RightParen
  ;

multiLineValueParameters
  : LeftParen NL (valueParameter Comma? NL)* valueParameter? Comma? NL RightParen
  ;

valueParameter
  : Identifier Colon existTypeReference
  ;

valueArguments
  : singleLineValueArguments
  | multiLineValueArguments
  ;

singleLineValueArguments
  : LeftParen (valueArgument Comma)* valueArgument? RightParen
  ;

multiLineValueArguments
  : LeftParen NL* (valueArgument Comma? NL*)* (valueArgument Comma?)? NL* RightParen
  ;

valueArgument
  : (Identifier Colon)? expression
  ;

existTypeReference
  : Exist? typeReference
  | LeftParen existTypeReference RightParen
  ;

typeReference
  : typeIdentifier typeArguments? Nullable?
  | Dynamic
  | Nothing
  | LeftParen typeReference RightParen
  ;

typeIdentifier
  : (Identifier Dot)* Identifier
  ;

block
  : LeftBracket NL* (statement NL+)* statement? NL* RightBracket
  ;

statement
  : expression
  | propertyDeclaration
  ;

expression
  : disjunction
  ;

disjunction
  : conjunction (OrOr conjunction)*
  ;

conjunction
  : equality (AndAnd equality)*
  ;

equality
  : comparison ((EqEq | NotEq | EqEqEq | NotEqEq) comparison)*
  ;

comparison
  : genericCallLikeComparison ((Greater | GreaterEq | Less | LessEq) genericCallLikeComparison)*
  ;

genericCallLikeComparison
  : infixOperation callSuffix*
  ;

infixOperation
  : elvisExpression (((In | NotIn) elvisExpression) | ((Is | NotIs) existTypeReference))*
  ;

elvisExpression
  : infixFunctionCall (Elvis infixFunctionCall)*
  ;

infixFunctionCall
  : rangeExpression (Identifier rangeExpression)*
  ;

rangeExpression
  : additiveExpression ((Range | RangeEq) additiveExpression)*
  ;

additiveExpression
  : multiplicativeExpression ((Plus | Minus) multiplicativeExpression)*
  ;

multiplicativeExpression
  : asExpression ((Multiply | Divide | Modulo) asExpression)*
  ;

asExpression
  : prefixUnaryExpression ((As | SafeAs) existTypeReference)?
  ;

prefixUnaryExpression
  : (Increment | Decrement | Plus | Minus | Not)* postfixUnaryExpression
  ;

postfixUnaryExpression
  : primaryExpression postfixUnarySuffix*
  ;

primaryExpression
  : parenthesizedExpression
  | callableReference
  | functionLiteral
  | dataLiteral
  | collectionLiteral
  | thisExpression
  | ifExpression
  | matchExpression
  | jumpExpression
  | functionCall
  | propertyCall
  | Identifier
  | literal
  ;

parenthesizedExpression
  : LeftParen expression RightParen
  ;

callableReference
  : typeReference Reference (Identifier | Data | parenthesizedExpression)
  ;

functionLiteral
  : lambdaLiteral
  | anonymousFunction
  ;

dataLiteral
  : Data inheritance? classBody?
  ;

collectionLiteral
  : LeftSquare (expression Comma)* expression? RightSquare
  ;

thisExpression
  : This
  ;

ifExpression
  : If LeftParen expression RightParen (block | expression) (Else (block | expression))?
  ;

matchExpression
  : Match matchSubject? LeftBracket NL* (matchEntry NL+)* matchEntry? NL* RightBracket
  ;

jumpExpression
  : Return expression?
  | Break
  | Continue
  ;

matchSubject
  : LeftParen expression RightParen
  ;

matchEntry
  : matchCondition Arrow (block | expression)
  | Else Arrow (block | expression)
  ;

matchCondition
  : expression
  | rangeTest
  | typeTest
  ;

rangeTest
  : (In | NotIn) expression
  ;

typeTest
  : (Is | NotIs) existTypeReference
  ;

anonymousFunction
  : Fun typeParameters? valueParameters (Colon existTypeReference)? (block | (Eq NL? expression))
  ;

postfixUnarySuffix
  : (Increment | Decrement)
  | typeArguments
  | callSuffix
  | LeftSquare (expression Comma)* expression? RightSquare
  | (Dot | SafeDot | Reference) (Identifier | parenthesizedExpression | Data)
  ;

callSuffix
  : typeArguments? ((valueArguments? lambdaLiteral) | valueArguments)
  ;

lambdaLiteral
  : LeftBracket NL* (lambdaValueParameters Arrow NL*)? (statement NL*)* statement? NL* RightBracket
  ;

functionCall
  : typeReference valueArguments
  ;

propertyCall
  : Identifier
  ;

literal
  : IntLiteral
  | StringLiteral
  ;