package cn.yurin.languege.viewer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import cn.yurin.languege.viewer.Highlight.*
import com.yurin.antlrkotlin.parsers.generated.YurinLexer
import com.yurin.antlrkotlin.parsers.generated.YurinLexer.Tokens
import com.yurin.antlrkotlin.parsers.generated.YurinParser
import com.yurin.antlrkotlin.parsers.generated.YurinParserBaseVisitor
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.tree.TerminalNode

fun highlight(code: String): AnnotatedString {
	val tokens = CommonTokenStream(YurinLexer(CharStreams.fromString(code)))
	val tree = YurinParser(tokens).yurinFile()
	return HighlightingVisitor(tokens, code).apply { visit(tree) }.build()
}

object HighlightingTransformation : VisualTransformation {
	override fun filter(text: AnnotatedString): TransformedText = TransformedText(
		text = highlight(text.text),
		offsetMapping = OffsetMapping.Identity,
	)
}

class HighlightingVisitor(
	tokens: CommonTokenStream,
	source: String,
) : YurinParserBaseVisitor<Result<Unit>>() {
	private val builder = AnnotatedString.Builder(source)

	init {
		tokens.tokens.forEach { token ->
			highlightToken(token)
		}
	}

	override fun visitYurinFile(ctx: YurinParser.YurinFileContext): Result<Unit> = runCatching {
		ctx.packageHeader()?.accept(this)
		ctx.importHeader().forEach { node ->
			node.accept(this)
		}
		ctx.declaration().forEach { node ->
			node.accept(this)
		}
	}

	override fun visitPackageHeader(ctx: YurinParser.PackageHeaderContext): Result<Unit> = runCatching {
		super.visitPackageHeader(ctx)
	}

	override fun visitImportHeader(ctx: YurinParser.ImportHeaderContext): Result<Unit> = runCatching {
		super.visitImportHeader(ctx)
	}

	override fun visitDeclaration(ctx: YurinParser.DeclarationContext): Result<Unit> = runCatching {
		super.visitDeclaration(ctx)
	}

	override fun visitClassDeclaration(ctx: YurinParser.ClassDeclarationContext): Result<Unit> = runCatching {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, ClassDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitObjectDeclaration(ctx: YurinParser.ObjectDeclarationContext): Result<Unit> = runCatching {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, ClassDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitTraitDeclaration(ctx: YurinParser.TraitDeclarationContext): Result<Unit> = runCatching {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, TraitDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitImplDeclaration(ctx: YurinParser.ImplDeclarationContext): Result<Unit> = runCatching {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, FunctionDeclaration)
				ctx.typeReference() -> {
					node as YurinParser.TypeReferenceContext
					node.children?.forEach { childNode ->
						when (childNode) {
							in node.Identifier() -> highlightToken((childNode as TerminalNode).symbol, TraitDeclaration)
							else -> childNode.accept(this)
						}
					}
				}

				else -> node.accept(this)
			}
		}
	}

	override fun visitFunctionDeclaration(ctx: YurinParser.FunctionDeclarationContext): Result<Unit> = runCatching {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, FunctionDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitPropertyDeclaration(ctx: YurinParser.PropertyDeclarationContext): Result<Unit> = runCatching {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, PropertyDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitTypeAliasDeclaration(ctx: YurinParser.TypeAliasDeclarationContext): Result<Unit> = runCatching {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, ClassDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitInheritance(ctx: YurinParser.InheritanceContext): Result<Unit> = runCatching {
		super.visitInheritance(ctx)
	}

	override fun visitPropertyGetter(ctx: YurinParser.PropertyGetterContext): Result<Unit> = runCatching {
		super.visitPropertyGetter(ctx)
	}

	override fun visitPropertySetter(ctx: YurinParser.PropertySetterContext): Result<Unit> = runCatching {
		super.visitPropertySetter(ctx)
	}

	override fun visitModifiers(ctx: YurinParser.ModifiersContext): Result<Unit> = runCatching {
		super.visitModifiers(ctx)
	}

	override fun visitModifier(ctx: YurinParser.ModifierContext): Result<Unit> = runCatching {
		super.visitModifier(ctx)
	}

	override fun visitClassBody(ctx: YurinParser.ClassBodyContext): Result<Unit> = runCatching {
		super.visitClassBody(ctx)
	}

	override fun visitTypeParameters(ctx: YurinParser.TypeParametersContext): Result<Unit> = runCatching {
		super.visitTypeParameters(ctx)
	}

	override fun visitTypeParameter(ctx: YurinParser.TypeParameterContext): Result<Unit> = runCatching {
		super.visitTypeParameter(ctx)
	}

	override fun visitTypeArguments(ctx: YurinParser.TypeArgumentsContext): Result<Unit> = runCatching {
		super.visitTypeArguments(ctx)
	}

	override fun visitTypeArgument(ctx: YurinParser.TypeArgumentContext): Result<Unit> = runCatching {
		super.visitTypeArgument(ctx)
	}

	override fun visitPrimaryConstructorValueParameters(ctx: YurinParser.PrimaryConstructorValueParametersContext): Result<Unit> = runCatching {
		super.visitPrimaryConstructorValueParameters(ctx)
	}

	override fun visitPrimaryConstructorSingleLineValueParameters(ctx: YurinParser.PrimaryConstructorSingleLineValueParametersContext): Result<Unit> = runCatching {
		super.visitPrimaryConstructorSingleLineValueParameters(ctx)
	}

	override fun visitPrimaryConstructorMultiLineValueParameters(ctx: YurinParser.PrimaryConstructorMultiLineValueParametersContext): Result<Unit> = runCatching {
		super.visitPrimaryConstructorMultiLineValueParameters(ctx)
	}

	override fun visitLambdaValueParameters(ctx: YurinParser.LambdaValueParametersContext): Result<Unit> = runCatching {
		super.visitLambdaValueParameters(ctx)
	}

	override fun visitValueParameters(ctx: YurinParser.ValueParametersContext): Result<Unit> = runCatching {
		super.visitValueParameters(ctx)
	}

	override fun visitSingleLineValueParameters(ctx: YurinParser.SingleLineValueParametersContext): Result<Unit> = runCatching {
		super.visitSingleLineValueParameters(ctx)
	}

	override fun visitMultiLineValueParameters(ctx: YurinParser.MultiLineValueParametersContext): Result<Unit> = runCatching {
		super.visitMultiLineValueParameters(ctx)
	}

	override fun visitValueParameter(ctx: YurinParser.ValueParameterContext): Result<Unit> = runCatching {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, PropertyDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitValueArguments(ctx: YurinParser.ValueArgumentsContext): Result<Unit> = runCatching {
		super.visitValueArguments(ctx)
	}

	override fun visitValueArgument(ctx: YurinParser.ValueArgumentContext): Result<Unit> = runCatching {
		super.visitValueArgument(ctx)
	}

	override fun visitExistTypeReference(ctx: YurinParser.ExistTypeReferenceContext): Result<Unit> = runCatching {
		super.visitExistTypeReference(ctx)
	}

	override fun visitTypeReference(ctx: YurinParser.TypeReferenceContext): Result<Unit> = runCatching {
		ctx.children?.forEach { node ->
			when (node) {
				in ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, ClassDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitBlock(ctx: YurinParser.BlockContext): Result<Unit> = runCatching {
		super.visitBlock(ctx)
	}

	override fun visitStatement(ctx: YurinParser.StatementContext): Result<Unit> = runCatching {
		super.visitStatement(ctx)
	}

	override fun visitExpression(ctx: YurinParser.ExpressionContext): Result<Unit> = runCatching {
		super.visitExpression(ctx)
	}

	override fun visitDisjunction(ctx: YurinParser.DisjunctionContext): Result<Unit> = runCatching {
		super.visitDisjunction(ctx)
	}

	override fun visitConjunction(ctx: YurinParser.ConjunctionContext): Result<Unit> = runCatching {
		super.visitConjunction(ctx)
	}

	override fun visitEquality(ctx: YurinParser.EqualityContext): Result<Unit> = runCatching {
		super.visitEquality(ctx)
	}

	override fun visitComparison(ctx: YurinParser.ComparisonContext): Result<Unit> = runCatching {
		super.visitComparison(ctx)
	}

	override fun visitGenericCallLikeComparison(ctx: YurinParser.GenericCallLikeComparisonContext): Result<Unit> = runCatching {
		super.visitGenericCallLikeComparison(ctx)
	}

	override fun visitInfixOperation(ctx: YurinParser.InfixOperationContext): Result<Unit> = runCatching {
		super.visitInfixOperation(ctx)
	}

	override fun visitElvisExpression(ctx: YurinParser.ElvisExpressionContext): Result<Unit> = runCatching {
		super.visitElvisExpression(ctx)
	}

	override fun visitInfixFunctionCall(ctx: YurinParser.InfixFunctionCallContext): Result<Unit> = runCatching {
		super.visitInfixFunctionCall(ctx)
	}

	override fun visitRangeExpression(ctx: YurinParser.RangeExpressionContext): Result<Unit> = runCatching {
		super.visitRangeExpression(ctx)
	}

	override fun visitAdditiveExpression(ctx: YurinParser.AdditiveExpressionContext): Result<Unit> = runCatching {
		super.visitAdditiveExpression(ctx)
	}

	override fun visitMultiplicativeExpression(ctx: YurinParser.MultiplicativeExpressionContext): Result<Unit> = runCatching {
		super.visitMultiplicativeExpression(ctx)
	}

	override fun visitAsExpression(ctx: YurinParser.AsExpressionContext): Result<Unit> = runCatching {
		super.visitAsExpression(ctx)
	}

	override fun visitPrefixUnaryExpression(ctx: YurinParser.PrefixUnaryExpressionContext): Result<Unit> = runCatching {
		super.visitPrefixUnaryExpression(ctx)
	}

	override fun visitPostfixUnaryExpression(ctx: YurinParser.PostfixUnaryExpressionContext): Result<Unit> = runCatching {
		super.visitPostfixUnaryExpression(ctx)
	}

	override fun visitPrimaryExpression(ctx: YurinParser.PrimaryExpressionContext): Result<Unit> = runCatching {
		super.visitPrimaryExpression(ctx)
	}

	override fun visitParenthesizedExpression(ctx: YurinParser.ParenthesizedExpressionContext): Result<Unit> = runCatching {
		super.visitParenthesizedExpression(ctx)
	}

	override fun visitCallableReference(ctx: YurinParser.CallableReferenceContext): Result<Unit> = runCatching {
		super.visitCallableReference(ctx)
	}

	override fun visitFunctionLiteral(ctx: YurinParser.FunctionLiteralContext): Result<Unit> = runCatching {
		super.visitFunctionLiteral(ctx)
	}

	override fun visitObjectLiteral(ctx: YurinParser.ObjectLiteralContext): Result<Unit> = runCatching {
		super.visitObjectLiteral(ctx)
	}

	override fun visitCollectionLiteral(ctx: YurinParser.CollectionLiteralContext): Result<Unit> = runCatching {
		super.visitCollectionLiteral(ctx)
	}

	override fun visitThisExpression(ctx: YurinParser.ThisExpressionContext): Result<Unit> = runCatching {
		super.visitThisExpression(ctx)
	}

	override fun visitIfExpression(ctx: YurinParser.IfExpressionContext): Result<Unit> = runCatching {
		super.visitIfExpression(ctx)
	}

	override fun visitMatchExpression(ctx: YurinParser.MatchExpressionContext): Result<Unit> = runCatching {
		super.visitMatchExpression(ctx)
	}

	override fun visitJumpExpression(ctx: YurinParser.JumpExpressionContext): Result<Unit> = runCatching {
		super.visitJumpExpression(ctx)
	}

	override fun visitMatchSubject(ctx: YurinParser.MatchSubjectContext): Result<Unit> = runCatching {
		super.visitMatchSubject(ctx)
	}

	override fun visitMatchEntry(ctx: YurinParser.MatchEntryContext): Result<Unit> = runCatching {
		super.visitMatchEntry(ctx)
	}

	override fun visitMatchCondition(ctx: YurinParser.MatchConditionContext): Result<Unit> = runCatching {
		super.visitMatchCondition(ctx)
	}

	override fun visitRangeTest(ctx: YurinParser.RangeTestContext): Result<Unit> = runCatching {
		super.visitRangeTest(ctx)
	}

	override fun visitTypeTest(ctx: YurinParser.TypeTestContext): Result<Unit> = runCatching {
		super.visitTypeTest(ctx)
	}

	override fun visitAnonymousFunction(ctx: YurinParser.AnonymousFunctionContext): Result<Unit> = runCatching {
		super.visitAnonymousFunction(ctx)
	}

	override fun visitPostfixUnarySuffix(ctx: YurinParser.PostfixUnarySuffixContext): Result<Unit> = runCatching {
		super.visitPostfixUnarySuffix(ctx)
	}

	override fun visitCallSuffix(ctx: YurinParser.CallSuffixContext): Result<Unit> = runCatching {
		super.visitCallSuffix(ctx)
	}

	override fun visitLambdaLiteral(ctx: YurinParser.LambdaLiteralContext): Result<Unit> = runCatching {
		super.visitLambdaLiteral(ctx)
	}

	override fun visitFunctionCall(ctx: YurinParser.FunctionCallContext): Result<Unit> = runCatching {
		super.visitFunctionCall(ctx)
	}

	override fun visitPropertyCall(ctx: YurinParser.PropertyCallContext): Result<Unit> = runCatching {
		super.visitPropertyCall(ctx)
	}

	override fun visitLiteral(ctx: YurinParser.LiteralContext): Result<Unit> = runCatching {
		super.visitLiteral(ctx)
	}

	private fun highlightToken(token: Token, highlight: Highlight? = null) {
		if (token.type == Token.EOF) return
		val highlight = highlight ?: map[token.type] ?: return
		val start = token.startIndex
		val end = token.stopIndex + 1
		if (start >= 0 && end <= builder.length && start < end) {
			builder.addStyle(
				style = SpanStyle(
					color = highlight.color?.let { Color(it) } ?: Color.Unspecified,
					fontWeight = when (highlight.isBold) {
						true -> FontWeight.Bold
						else -> FontWeight.Normal
					},
					fontStyle = when (highlight.isItalic) {
						true -> FontStyle.Italic
						else -> FontStyle.Normal
					},
				),
				start = start,
				end = end,
			)
		}
	}

	fun build() = builder.toAnnotatedString()

	override fun defaultResult(): Result<Unit> = Result.success(Unit)
}

private val map = mapOf(
	Tokens.Package to Keyword,
	Tokens.Import to Keyword,
	Tokens.Class to Keyword,
	Tokens.Object to Keyword,
	Tokens.Trait to Keyword,
	Tokens.Impl to Keyword,
	Tokens.Fun to Keyword,
	Tokens.Var to Keyword,
	Tokens.Val to Keyword,
	Tokens.Get to Keyword,
	Tokens.Set to Keyword,
	Tokens.Typealias to Keyword,
	Tokens.Sealed to Keyword,
	Tokens.Operator to Keyword,
	Tokens.If to Keyword,
	Tokens.Else to Keyword,
	Tokens.Match to Keyword,
	Tokens.Return to Keyword,
	Tokens.Break to Keyword,
	Tokens.Continue to Keyword,
	Tokens.Exist to Keyword,
	Tokens.This to Keyword,

	Tokens.StringLiteral to StringLiteral,

	Tokens.IntLiteral to NumberLiteral,

	Tokens.Colon to Colon,
	Tokens.Dot to Dot,
	Tokens.SafeDot to SafeDot,
	Tokens.Comma to Comma,
	Tokens.LeftBracket to Bracket,
	Tokens.RightBracket to Bracket,
	Tokens.LeftParen to Paren,
	Tokens.RightParen to Paren,
	Tokens.LeftSquare to Square,
	Tokens.RightSquare to Square,
	Tokens.LeftAngle to Operator,
	Tokens.RightAngle to Operator,
	Tokens.Eq to Operator,
	Tokens.NotEq to Operator,
	Tokens.EqEq to Operator,
	Tokens.Greater to Operator,
	Tokens.GreaterEq to Operator,
	Tokens.Less to Operator,
	Tokens.LessEq to Operator,
	Tokens.Plus to Operator,
	Tokens.Minus to Operator,
	Tokens.Multiply to Operator,
	Tokens.Divide to Operator,
	Tokens.Modulo to Operator,
	Tokens.And to Operator,
	Tokens.Or to Operator,
	Tokens.AndAnd to Operator,
	Tokens.OrOr to Operator,
	Tokens.Not to Operator,
	Tokens.Arrow to Arrow,
	Tokens.Elvis to Operator,
	Tokens.Range to Operator,
	Tokens.RangeEq to Operator,
	Tokens.Nullable to NullableMarker,
	Tokens.As to Keyword,
	Tokens.SafeAs to Keyword,
	Tokens.In to Keyword,
	Tokens.NotIn to Keyword,
	Tokens.Is to Keyword,
	Tokens.NotIs to Keyword,
	Tokens.Reference to Reference,

	Tokens.SingleLineComment to LineComment,
	Tokens.MultiLineComment to BlockComment,

	Tokens.Identifier to Identifier,
)

private enum class Highlight(
	val color: Long?,
	val isBold: Boolean = false,
	val isItalic: Boolean = false,
) {
	Keyword(0xFFC679DD, isItalic = true),
	FunctionDeclaration(0xFF61AEEF),
	FunctionCall(0xFF61AEEF),
	NamedValueArgument(0xFFD19A66),
	Square(0xFFA6B2C0),
	Colon(0xFF61AFEF),
	Semicolon(0XFFA6B2C0),
	Paren(0xFFA6B2C0),
	Bracket(0xFFA6B2C0),
	Dot(0xFFA6B2C0),
	Operator(0xFF61AFEF),
	Comma(0xFFA6B2C0),
	StringLiteral(0xFF98C379),
	NullableMarker(0xFF61AFEF),
	Reference(0xFF61AFEF),
	SafeDot(0xFFA6B2C0),
	Arrow(0xFF61AFEF),
	NotNullAssertion(0xFF61AFEF),
	NumberLiteral(0xFFD19A66),
	BlockComment(0xFF59626F, isItalic = true),
	LineComment(0xFF59626F, isItalic = true),
	ClassDeclaration(0xFFE5C17C),
	TraitDeclaration(0xFF98C379),
	Identifier(0xFFABB2BF),
	PropertyDeclaration(0xFFE06C75),
}