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
import org.antlr.v4.kotlinruntime.*
import org.antlr.v4.kotlinruntime.tree.ParseTreeWalker
import org.antlr.v4.kotlinruntime.tree.TerminalNode

fun getErrors(code: String): List<PositionErrorListener.Error> {
	val tokens = CommonTokenStream(YurinLexer(CharStreams.fromString(code)))
	val listener = PositionErrorListener(code)
	YurinParser(tokens).apply {
		removeErrorListeners()
		addErrorListener(listener)
	}.yurinFile()
	return listener.errors
}

fun highlight(code: String): AnnotatedString {
	val tokens = CommonTokenStream(YurinLexer(CharStreams.fromString(code)))
	val listener = PositionErrorListener(code)
	val tree = YurinParser(tokens).apply {
		removeErrorListeners()
		addErrorListener(listener)
	}.yurinFile()
	val collectListener = DeclarationCollectListener()
	ParseTreeWalker.DEFAULT.walk(collectListener, tree)
	return HighlightingVisitor(tokens, code, collectListener).apply { visit(tree) }.build()
}

class PositionErrorListener(
	private val source: String,
) : BaseErrorListener() {
	data class Error(
		val start: Int,
		val end: Int,
		val line: Int,
		val charPositionInLine: Int,
		val msg: String,
	)

	val errors = mutableListOf<Error>()

	override fun syntaxError(
		recognizer: Recognizer<*, *>,
		offendingSymbol: Any?,
		line: Int,
		charPositionInLine: Int,
		msg: String,
		e: RecognitionException?,
	) {
		val start = toOffset(source, line, charPositionInLine)
		val end = when (offendingSymbol) {
			is Token -> offendingSymbol.stopIndex + 1
			else -> start + 1
		}

		errors += Error(start, end, line, charPositionInLine, msg)
	}

	fun toOffset(text: String, line: Int, column: Int): Int {
		var l = 1
		var i = 0

		while (i < text.length && l < line) {
			if (text[i] == '\n') l++
			i++
		}

		return i + column
	}
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
	private val collectListener: DeclarationCollectListener,
) : YurinParserBaseVisitor<Unit>() {
	private val builder = AnnotatedString.Builder(source)
	private lateinit var currentPackage: String

	val currentPackageDataIdentifiers by lazy {
		collectListener.dataTypeIdentifiers
			.filter { it.first == currentPackage }
			.map { it.second }
	}

	val currentPackageTraitIdentifiers by lazy {
		collectListener.traitTypeIdentifiers
			.filter { it.first == currentPackage }
			.map { it.second }
	}

	val packagedDataIdentifiers = collectListener.dataTypeIdentifiers.map { it.first + "." + it.second }
	val packagedTraitIdentifiers = collectListener.traitTypeIdentifiers.map { it.first + "." + it.second }

	init {
		tokens.tokens.forEach { token ->
			highlightToken(token)
		}
	}

	override fun visitYurinFile(ctx: YurinParser.YurinFileContext) {
		currentPackage = ctx.packageHeader()?.Identifier()?.joinToString(".") ?: ""
		ctx.packageHeader()?.accept(this)

		ctx.importHeader().forEach { node ->
			node.accept(this)
		}

		ctx.declaration().forEach { node ->
			node.accept(this)
		}
	}

	override fun visitPackageHeader(ctx: YurinParser.PackageHeaderContext) {
		super.visitPackageHeader(ctx)
	}

	override fun visitImportHeader(ctx: YurinParser.ImportHeaderContext) {
		super.visitImportHeader(ctx)
	}

	override fun visitDeclaration(ctx: YurinParser.DeclarationContext) {
		super.visitDeclaration(ctx)
	}

	override fun visitDataDeclaration(ctx: YurinParser.DataDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, DataDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitTraitDeclaration(ctx: YurinParser.TraitDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, TraitDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitImplDeclaration(ctx: YurinParser.ImplDeclarationContext) {
		super.visitImplDeclaration(ctx)
	}

	override fun visitFunctionDeclaration(ctx: YurinParser.FunctionDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, FunctionDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitPropertyDeclaration(ctx: YurinParser.PropertyDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, PropertyDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitTypeAliasDeclaration(ctx: YurinParser.TypeAliasDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, DataDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitInheritance(ctx: YurinParser.InheritanceContext) {
		super.visitInheritance(ctx)
	}

	override fun visitPropertyGetter(ctx: YurinParser.PropertyGetterContext) {
		super.visitPropertyGetter(ctx)
	}

	override fun visitPropertySetter(ctx: YurinParser.PropertySetterContext) {
		super.visitPropertySetter(ctx)
	}

	override fun visitModifiers(ctx: YurinParser.ModifiersContext) {
		super.visitModifiers(ctx)
	}

	override fun visitModifier(ctx: YurinParser.ModifierContext) {
		super.visitModifier(ctx)
	}

	override fun visitClassBody(ctx: YurinParser.ClassBodyContext) {
		super.visitClassBody(ctx)
	}

	override fun visitTypeParameters(ctx: YurinParser.TypeParametersContext) {
		super.visitTypeParameters(ctx)
	}

	override fun visitTypeParameter(ctx: YurinParser.TypeParameterContext) {
		super.visitTypeParameter(ctx)
	}

	override fun visitTypeArguments(ctx: YurinParser.TypeArgumentsContext) {
		super.visitTypeArguments(ctx)
	}

	override fun visitTypeArgument(ctx: YurinParser.TypeArgumentContext) {
		super.visitTypeArgument(ctx)
	}

	override fun visitPrimaryConstructorValueParameters(ctx: YurinParser.PrimaryConstructorValueParametersContext) {
		super.visitPrimaryConstructorValueParameters(ctx)
	}

	override fun visitPrimaryConstructorSingleLineValueParameters(ctx: YurinParser.PrimaryConstructorSingleLineValueParametersContext) {
		super.visitPrimaryConstructorSingleLineValueParameters(ctx)
	}

	override fun visitPrimaryConstructorMultiLineValueParameters(ctx: YurinParser.PrimaryConstructorMultiLineValueParametersContext) {
		super.visitPrimaryConstructorMultiLineValueParameters(ctx)
	}

	override fun visitLambdaValueParameters(ctx: YurinParser.LambdaValueParametersContext) {
		super.visitLambdaValueParameters(ctx)
	}

	override fun visitLambdaSingleLineValueParameters(ctx: YurinParser.LambdaSingleLineValueParametersContext) {
		super.visitLambdaSingleLineValueParameters(ctx)
	}

	override fun visitLambdaMultiLineValueParameters(ctx: YurinParser.LambdaMultiLineValueParametersContext) {
		super.visitLambdaMultiLineValueParameters(ctx)
	}

	override fun visitValueParameters(ctx: YurinParser.ValueParametersContext) {
		super.visitValueParameters(ctx)
	}

	override fun visitSingleLineValueParameters(ctx: YurinParser.SingleLineValueParametersContext) {
		super.visitSingleLineValueParameters(ctx)
	}

	override fun visitMultiLineValueParameters(ctx: YurinParser.MultiLineValueParametersContext) {
		super.visitMultiLineValueParameters(ctx)
	}

	override fun visitValueParameter(ctx: YurinParser.ValueParameterContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlightToken((node as TerminalNode).symbol, PropertyDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitValueArguments(ctx: YurinParser.ValueArgumentsContext) {
		super.visitValueArguments(ctx)
	}

	override fun visitSingleLineValueArguments(ctx: YurinParser.SingleLineValueArgumentsContext) {
		super.visitSingleLineValueArguments(ctx)
	}

	override fun visitMultiLineValueArguments(ctx: YurinParser.MultiLineValueArgumentsContext) {
		super.visitMultiLineValueArguments(ctx)
	}

	override fun visitValueArgument(ctx: YurinParser.ValueArgumentContext) {
		super.visitValueArgument(ctx)
	}

	override fun visitExistTypeReference(ctx: YurinParser.ExistTypeReferenceContext) {
		super.visitExistTypeReference(ctx)
	}

	override fun visitTypeReference(ctx: YurinParser.TypeReferenceContext) {
		super.visitTypeReference(ctx)
	}

	override fun visitTypeIdentifier(ctx: YurinParser.TypeIdentifierContext) {
		var identifier = ""
		ctx.Identifier().forEach { node ->
			if (identifier.isNotEmpty()) {
				identifier += "."
			}
			identifier += node.text
			when (identifier) {
				in currentPackageDataIdentifiers,
				in packagedDataIdentifiers,
					-> highlightToken(node.symbol, DataDeclaration)

				in currentPackageTraitIdentifiers,
				in packagedTraitIdentifiers,
					-> highlightToken(node.symbol, TraitDeclaration)
			}
		}
	}

	override fun visitBlock(ctx: YurinParser.BlockContext) {
		super.visitBlock(ctx)
	}

	override fun visitStatement(ctx: YurinParser.StatementContext) {
		super.visitStatement(ctx)
	}

	override fun visitExpression(ctx: YurinParser.ExpressionContext) {
		super.visitExpression(ctx)
	}

	override fun visitDisjunction(ctx: YurinParser.DisjunctionContext) {
		super.visitDisjunction(ctx)
	}

	override fun visitConjunction(ctx: YurinParser.ConjunctionContext) {
		super.visitConjunction(ctx)
	}

	override fun visitEquality(ctx: YurinParser.EqualityContext) {
		super.visitEquality(ctx)
	}

	override fun visitComparison(ctx: YurinParser.ComparisonContext) {
		super.visitComparison(ctx)
	}

	override fun visitGenericCallLikeComparison(ctx: YurinParser.GenericCallLikeComparisonContext) {
		super.visitGenericCallLikeComparison(ctx)
	}

	override fun visitInfixOperation(ctx: YurinParser.InfixOperationContext) {
		super.visitInfixOperation(ctx)
	}

	override fun visitElvisExpression(ctx: YurinParser.ElvisExpressionContext) {
		super.visitElvisExpression(ctx)
	}

	override fun visitInfixFunctionCall(ctx: YurinParser.InfixFunctionCallContext) {
		super.visitInfixFunctionCall(ctx)
	}

	override fun visitRangeExpression(ctx: YurinParser.RangeExpressionContext) {
		super.visitRangeExpression(ctx)
	}

	override fun visitAdditiveExpression(ctx: YurinParser.AdditiveExpressionContext) {
		super.visitAdditiveExpression(ctx)
	}

	override fun visitMultiplicativeExpression(ctx: YurinParser.MultiplicativeExpressionContext) {
		super.visitMultiplicativeExpression(ctx)
	}

	override fun visitAsExpression(ctx: YurinParser.AsExpressionContext) {
		super.visitAsExpression(ctx)
	}

	override fun visitPrefixUnaryExpression(ctx: YurinParser.PrefixUnaryExpressionContext) {
		super.visitPrefixUnaryExpression(ctx)
	}

	override fun visitPostfixUnaryExpression(ctx: YurinParser.PostfixUnaryExpressionContext) {
		super.visitPostfixUnaryExpression(ctx)
	}

	override fun visitPrimaryExpression(ctx: YurinParser.PrimaryExpressionContext) {
		super.visitPrimaryExpression(ctx)
	}

	override fun visitParenthesizedExpression(ctx: YurinParser.ParenthesizedExpressionContext) {
		super.visitParenthesizedExpression(ctx)
	}

	override fun visitCallableReference(ctx: YurinParser.CallableReferenceContext) {
		super.visitCallableReference(ctx)
	}

	override fun visitFunctionLiteral(ctx: YurinParser.FunctionLiteralContext) {
		super.visitFunctionLiteral(ctx)
	}

	override fun visitDataLiteral(ctx: YurinParser.DataLiteralContext) {
		super.visitDataLiteral(ctx)
	}

	override fun visitCollectionLiteral(ctx: YurinParser.CollectionLiteralContext) {
		super.visitCollectionLiteral(ctx)
	}

	override fun visitThisExpression(ctx: YurinParser.ThisExpressionContext) {
		super.visitThisExpression(ctx)
	}

	override fun visitIfExpression(ctx: YurinParser.IfExpressionContext) {
		super.visitIfExpression(ctx)
	}

	override fun visitMatchExpression(ctx: YurinParser.MatchExpressionContext) {
		super.visitMatchExpression(ctx)
	}

	override fun visitJumpExpression(ctx: YurinParser.JumpExpressionContext) {
		super.visitJumpExpression(ctx)
	}

	override fun visitMatchSubject(ctx: YurinParser.MatchSubjectContext) {
		super.visitMatchSubject(ctx)
	}

	override fun visitMatchEntry(ctx: YurinParser.MatchEntryContext) {
		super.visitMatchEntry(ctx)
	}

	override fun visitMatchCondition(ctx: YurinParser.MatchConditionContext) {
		super.visitMatchCondition(ctx)
	}

	override fun visitRangeTest(ctx: YurinParser.RangeTestContext) {
		super.visitRangeTest(ctx)
	}

	override fun visitTypeTest(ctx: YurinParser.TypeTestContext) {
		super.visitTypeTest(ctx)
	}

	override fun visitAnonymousFunction(ctx: YurinParser.AnonymousFunctionContext) {
		super.visitAnonymousFunction(ctx)
	}

	override fun visitPostfixUnarySuffix(ctx: YurinParser.PostfixUnarySuffixContext) {
		super.visitPostfixUnarySuffix(ctx)
	}

	override fun visitCallSuffix(ctx: YurinParser.CallSuffixContext) {
		super.visitCallSuffix(ctx)
	}

	override fun visitLambdaLiteral(ctx: YurinParser.LambdaLiteralContext) {
		super.visitLambdaLiteral(ctx)
	}

	override fun visitFunctionCall(ctx: YurinParser.FunctionCallContext) {
		super.visitFunctionCall(ctx)
	}

	override fun visitPropertyCall(ctx: YurinParser.PropertyCallContext) {
		super.visitPropertyCall(ctx)
	}

	override fun visitLiteral(ctx: YurinParser.LiteralContext) {
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

	override fun defaultResult() {}
}

private val map = mapOf(
	Tokens.Package to Keyword,
	Tokens.Import to Keyword,
	Tokens.Data to Keyword,
	Tokens.Trait to Keyword,
	Tokens.Impl to Keyword,
	Tokens.Fun to Keyword,
	Tokens.Var to Keyword,
	Tokens.Val to Keyword,
	Tokens.Get to Keyword,
	Tokens.Set to Keyword,
	Tokens.Typealias to Keyword,
	Tokens.Open to Keyword,
	Tokens.Abstract to Keyword,
	Tokens.Operator to Keyword,
	Tokens.Singleton to Keyword,
	Tokens.Unsafe to Keyword,
	Tokens.Nothing to Keyword,
	Tokens.Dynamic to Keyword,
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
	DataDeclaration(0xFFE5C17C),
	TraitDeclaration(0xFF98C379),
	Identifier(0xFFABB2BF),
	PropertyDeclaration(0xFFE06C75),
}