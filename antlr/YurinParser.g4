parser grammar YurinParser;

options { tokenVocab = YurinLexer; }

yurinFile
  : NL* (packageHeader NL+)? (importHeader NL+)* (declaration NL+)* declaration? NL* EOF
  ;

packageHeader
  : Package Identifier
  ;

importHeader
  : Import Identifier
  ;

declaration
  : classDeclaration
  | objectDeclaration
  | traitDeclaration
  | implDeclaration
  | functionDeclaration
  | propertyDeclaration
  | typeAliasDeclaration
  ;

classDeclaration
  : modifiers Class Identifier typeParameters? primaryConstructorValueParameters? inheritance? classBody?
  ;

objectDeclaration
  : modifiers Object Identifier classBody?
  ;

traitDeclaration
  : modifiers Trait Identifier typeParameters? classBody?
  ;

implDeclaration
  : modifiers Impl Identifier Colon typeReference classBody?
  ;

functionDeclaration
  : modifiers Fun typeParameters? Identifier valueParameters (Colon typeReference)? (block | (Eq NL? expression))?
  ;

propertyDeclaration
  : modifiers (Var | Val) Identifier (Colon typeReference)? (Eq NL? expression)? (NL propertyGetter)? (NL propertySetter)?
  ;

inheritance
  : Colon typeReference
  ;

propertyGetter
  : modifiers Get (valueParameters (block | (Eq NL? expression)))?
  ;

propertySetter
  : modifiers Set (valueParameters (block | (Eq NL? expression)))?
  ;

typeAliasDeclaration
  : modifiers Typealias Identifier typeParameters? Eq typeReference typeParameters?
  ;

modifiers
  : modifier*
  ;

modifier
  : Sealed
  | Operator
  ;

classBody
  : LeftBracket NL* RightBracket
  | LeftBracket NL (declaration NL)* declaration? NL RightBracket
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
  : typeReference
  ;

valueArguments
  : LeftParen (valueArgument Comma)* valueArgument RightParen
  ;

valueArgument
  : expression
  ;

primaryConstructorValueParameters
  : primaryConstructorSingleLineValueParameters
  | primaryConstructorMultiLineValueParameters
  ;

primaryConstructorSingleLineValueParameters
  : LeftParen ((Var | Val)? valueParameter Comma)* ((Var | Val)? valueParameter)? RightParen
  ;

primaryConstructorMultiLineValueParameters
  : LeftParen NL ((Var | Val)? valueParameter Comma? NL)* ((Var | Val)? valueParameter)? Comma? NL RightParen
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
  : Identifier Colon typeReference
  ;

typeReference
  : Exist? (Identifier Dot)* Identifier typeArguments?
  ;

block
  : LeftBracket NL* (statement NL*)* statement? NL* RightBracket
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
  : elvisExpression (((In | NotIn) elvisExpression) | ((Is | NotIs) typeReference))*
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
  : prefixUnaryExpression ((As | SafeAs) typeReference)?
  ;

prefixUnaryExpression
  : (Increment | Decrement | Plus | Minus | Not)* postfixUnaryExpression
  ;

postfixUnaryExpression
  : primaryExpression postfixUnarySuffix*
  ;

primaryExpression
  : parenthesizedExpression
  | literal
  | callableReference
  | functionLiteral
  | objectLiteral
  | collectionLiteral
  | thisExpression
  | ifExpression
  | matchExpression
  | jumpExpression
  | functionCall
  | propertyCall
  | Identifier
  ;

parenthesizedExpression
  : LeftParen expression RightParen
  ;

callableReference
  : (Identifier Dot)* Identifier Reference (Identifier | Class | parenthesizedExpression)
  ;

functionLiteral
  : lambdaLiteral
  | anonymousFunction
  ;

objectLiteral
  : Object inheritance? classBody?
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
  : (Is | NotIs) typeReference
  ;

anonymousFunction
  : Fun typeParameters? valueParameters (Colon typeReference)? (block | (Eq NL? expression))
  ;

postfixUnarySuffix
  : (Increment | Decrement)
  | typeArguments
  | callSuffix
  | LeftSquare (expression Comma)* expression? RightSquare
  | (Dot | SafeDot | Reference) (Identifier | parenthesizedExpression | Class)
  ;

callSuffix
  : typeArguments? ((valueArguments? lambdaLiteral) | valueArguments)
  ;

lambdaLiteral
  : LeftBracket NL* (lambdaValueParameters Arrow NL*)? (statement NL*)* statement? NL* RightBracket
  ;

lambdaValueParameters
  : singleLineValueParameters
  | multiLineValueParameters
  ;

functionCall
  : (Identifier Dot)* Identifier LeftParen NL* (expression Comma NL*)* expression? Comma? NL* RightParen
  ;

propertyCall
  : Identifier
  ;

literal
  : IntLiteral
  | StringLiteral
  ;