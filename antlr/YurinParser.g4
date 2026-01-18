parser grammar YurinParser;

options { tokenVocab = YurinLexer; }

// SECTION: general

yurinFile
  : NL* (packageHeader NL+)? (importHeader NL+)* (declaration NL+)* declaration? NL* EOF
  ;

packageHeader
  : Package (Identifier Dot)* Identifier
  ;

importHeader
  : Import (Identifier Dot)* Identifier
  ;

// SECTION: declaration

declaration
  : dataDeclaration
  | traitDeclaration
  | typealiasDeclaration
  | effectDeclaration
  | implDeclaration
  | functionDeclaration
  | propertyDeclaration
  ;

dataDeclaration
  : modifier* (Val | Ref)? Data Identifier typeParameters? primaryConstructorValueParameters? typeBinding? typeBody?
  ;

traitDeclaration
  : modifier* Trait Identifier typeParameters? typeBinding? typeBody?
  ;

typealiasDeclaration
  : modifier* Typealias Identifier typeParameters? Eq typeReference
  ;

effectDeclaration
  : modifier* Effect Identifier typeParameters?
  ;

implDeclaration
  : modifier* Impl typeReference Colon typeReference typeBody?
  ;

functionDeclaration
  : modifier* Fun typeParameters? receiverType? Identifier valueParameters (Colon typeReference)? functionEffects? (block | (Eq NL? expression))?
  ;

propertyDeclaration
  : modifier* (Var | Val) receiverType? Identifier (Colon typeReference)? functionEffects? (Eq NL* expression)? (NL+ propertyGetter)? (NL+ propertySetter)?
  ;

typeBinding
  : Colon NL* (typeReference Comma? NL*)* typeReference Comma?
  ;

receiverType
  : typeReference Dot
  ;

functionEffects
  : Effect NL* (typeReference Comma? NL*)* typeReference Comma?
  ;

propertyGetter
  : modifier* Get (valueParameters (block | (Eq NL? expression)))?
  ;

propertySetter
  : modifier* Set (valueParameters (block | (Eq NL? expression)))?
  ;

modifier
  : Open
  | Sealed
  | Abstract
  | Singleton
  | Operator
  | Impl
  | visibilityModifier
  ;

visibilityModifier
  : Public
  | Internal
  | Restricted
  | Private
  ;

typeBody
  : LeftBracket NL* (declaration NL*)* declaration? NL* RightBracket
  ;

typeParameters
  : LeftAngle NL* (typeParameter Comma? NL*)* typeParameter? Comma? NL* RightAngle
  ;

typeParameter
  : Identifier
  ;

typeArguments
  : LeftAngle NL* (typeArgument Comma? NL*)* typeArgument Comma? NL* RightAngle
  ;

typeArgument
  : typeReference
  ;

primaryConstructorValueParameters
  : LeftParen NL* ((Var | Val)? valueParameter Comma? NL*)* ((Var | Val) valueParameter Comma?)? NL* RightParen
  ;

lambdaValueParameters
  : NL* (valueParameter Comma? NL*)* (valueParameter Comma?)? NL*
  ;

valueParameters
  : LeftParen NL* (valueParameter Comma? NL*)* (valueParameter Comma?)? NL* RightParen
  ;

valueParameter
  : Identifier Colon typeReference
  ;

valueArguments
  : LeftParen NL* (valueArgument Comma? NL*)* (valueArgument Comma?)? NL* RightParen
  ;

valueArgument
  : (Identifier Colon)? expression
  ;

typeReference
  : Exist? typeIdentifier typeArguments? Nullable?
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
  : variableStatement
  | expression
  ;

variableStatement
  : (Var | Val) Identifier (Colon typeReference)? (Eq NL* expression)?
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
  | callableReference
  | functionLiteral
  | dataLiteral
  | collectionLiteral
  | thisExpression
  | ifExpression
  | matchExpression
  | jumpExpression
  | functionCall
  | typeIdentifier
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
  : Data typeBinding? typeBody?
  ;

collectionLiteral
  : LeftSquare NL* (expression Comma? NL*)* expression? Comma? NL* RightSquare
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
  | LeftSquare NL* (expression Comma? NL*)* expression? Comma? NL* RightSquare
  | (Dot | SafeDot | Reference) (Identifier | parenthesizedExpression | Data)
  ;

callSuffix
  : typeArguments? ((valueArguments? lambdaLiteral) | valueArguments)
  ;

lambdaLiteral
  : LeftBracket NL* (lambdaValueParameters Arrow NL*)? (statement NL*)* statement? NL* RightBracket
  ;

functionCall
  : typeIdentifier valueArguments
  ;

literal
  : IntLiteral
  | StringLiteral
  ;