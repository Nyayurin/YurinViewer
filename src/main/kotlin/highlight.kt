package cn.yurin.languege.viewer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import cn.yurin.languege.viewer.semantic.symbol_skeleton.symbolSkeleton
import com.yurin.antlrkotlin.parsers.generated.YurinLexer
import com.yurin.antlrkotlin.parsers.generated.YurinLexer.Tokens
import com.yurin.antlrkotlin.parsers.generated.YurinParser
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.Token

data class Highlight(
	val start: Int,
	val end: Int,
	val style: HighlightStyle,
)

data class HighlightStyle(
	val color: Long? = null,
	val isBold: Boolean = false,
	val isItalic: Boolean = false,
	val underlineColor: Long? = null,
)

fun highlight(source: String): Pair<List<Highlight>, List<ErrorHighlightListener.Error>> {
	val tokens = CommonTokenStream(YurinLexer(CharStreams.fromString(source)))
	val errorListener = ErrorHighlightListener(source)
	val tree = YurinParser(tokens).apply {
		removeErrorListeners()
		addErrorListener(errorListener)
	}.yurinFile()
	val lexicalHighlights = lexicalHighlight(tokens)
	val syntaxHighlights = syntaxHighlight(tree)
	val semanticHighlights = semanticHighlight(tree)
	val (errorHighlights, errorList) = errorListener.build()
	return lexicalHighlights + syntaxHighlights + semanticHighlights + errorHighlights to errorList
}

fun lexicalHighlight(tokens: CommonTokenStream): List<Highlight> {
	return tokens.tokens.mapNotNull { it.toHighlight() }
}

fun syntaxHighlight(tree: YurinParser.YurinFileContext): List<Highlight> {
	return SyntaxHighlightingVisitor().apply { visit(tree) }.build()
}

fun semanticHighlight(tree: YurinParser.YurinFileContext): List<Highlight> {
	val packageSymbol = symbolSkeleton(tree)
	return SemanticHighlightVisitor(packageSymbol).apply { visit(tree) }.build()
}

fun Token.toHighlight(style: HighlightStyle? = null): Highlight? {
	if (type in listOf(Token.EOF, Tokens.WS, Tokens.NL)) return null

	val style = style ?: highlightStyleMap[type] ?: return null

	val text = text ?: ""
	val trimStartLength = text.length - text.trimStart().length
	val trimEndLength = text.length - text.trimEnd().length
	val start = startIndex + trimStartLength
	val end = stopIndex + 1 - trimEndLength

	return Highlight(
		start = start,
		end = end,
		style = style,
	)
}

fun List<Highlight>.toAnnotatedString(source: String) = AnnotatedString.Builder(source).apply {
	forEach { highlightToken(it) }
}.toAnnotatedString()

fun AnnotatedString.Builder.highlightToken(highlight: Highlight) {
	val (start, end, style) = highlight
	if (style.color == null && !style.isBold && !style.isItalic) return

	if (start >= 0 && end <= length && start < end) {
		addStyle(
			style = SpanStyle(
				color = style.color?.let { Color(it) } ?: Color.Unspecified,
				fontWeight = when (style.isBold) {
					true -> FontWeight.Bold
					else -> FontWeight.Normal
				},
				fontStyle = when (style.isItalic) {
					true -> FontStyle.Italic
					else -> FontStyle.Normal
				},
			),
			start = start,
			end = end,
		)
	}
}

private val highlightStyleMap = mapOf(
	// General
	Tokens.LineComment to YurinHighlightStyle.lineComment,
	Tokens.DelimitedComment to YurinHighlightStyle.blockComment,
	// Keyword
	Tokens.Package to YurinHighlightStyle.keyword,
	Tokens.Import to YurinHighlightStyle.keyword,
	Tokens.Data to YurinHighlightStyle.keyword,
	Tokens.Trait to YurinHighlightStyle.keyword,
	Tokens.Typealias to YurinHighlightStyle.keyword,
	Tokens.Impl to YurinHighlightStyle.keyword,
	Tokens.Fun to YurinHighlightStyle.keyword,
	Tokens.Var to YurinHighlightStyle.keyword,
	Tokens.Val to YurinHighlightStyle.keyword,
	Tokens.Ref to YurinHighlightStyle.keyword,
	Tokens.Get to YurinHighlightStyle.keyword,
	Tokens.Set to YurinHighlightStyle.keyword,
	Tokens.This to YurinHighlightStyle.keyword,
	Tokens.Effect to YurinHighlightStyle.keyword,
	Tokens.Open to YurinHighlightStyle.keyword,
	Tokens.Sealed to YurinHighlightStyle.keyword,
	Tokens.Abstract to YurinHighlightStyle.keyword,
	Tokens.Operator to YurinHighlightStyle.keyword,
	Tokens.Singleton to YurinHighlightStyle.keyword,
	Tokens.Exist to YurinHighlightStyle.keyword,
	Tokens.Private to YurinHighlightStyle.keyword,
	Tokens.Internal to YurinHighlightStyle.keyword,
	Tokens.Restricted to YurinHighlightStyle.keyword,
	Tokens.Public to YurinHighlightStyle.keyword,
	Tokens.Nothing to YurinHighlightStyle.keyword,
	Tokens.Dynamic to YurinHighlightStyle.keyword,
	Tokens.In to YurinHighlightStyle.keyword,
	Tokens.NotIn to YurinHighlightStyle.keyword,
	Tokens.Is to YurinHighlightStyle.keyword,
	Tokens.NotIs to YurinHighlightStyle.keyword,
	Tokens.As to YurinHighlightStyle.keyword,
	Tokens.SafeAs to YurinHighlightStyle.keyword,
	Tokens.If to YurinHighlightStyle.keyword,
	Tokens.Else to YurinHighlightStyle.keyword,
	Tokens.Match to YurinHighlightStyle.keyword,
	Tokens.Return to YurinHighlightStyle.keyword,
	Tokens.Break to YurinHighlightStyle.keyword,
	Tokens.Continue to YurinHighlightStyle.keyword,
	// Punctuation
	Tokens.Colon to YurinHighlightStyle.colon,
	Tokens.Dot to YurinHighlightStyle.dot,
	Tokens.SafeDot to YurinHighlightStyle.safeDot,
	Tokens.Comma to YurinHighlightStyle.comma,
	Tokens.LeftBracket to YurinHighlightStyle.bracket,
	Tokens.RightBracket to YurinHighlightStyle.bracket,
	Tokens.LeftParen to YurinHighlightStyle.paren,
	Tokens.RightParen to YurinHighlightStyle.paren,
	Tokens.LeftSquare to YurinHighlightStyle.square,
	Tokens.RightSquare to YurinHighlightStyle.square,
	Tokens.LeftAngle to YurinHighlightStyle.operator,
	Tokens.RightAngle to YurinHighlightStyle.operator,
	Tokens.Eq to YurinHighlightStyle.operator,
	Tokens.NotEq to YurinHighlightStyle.operator,
	Tokens.EqEq to YurinHighlightStyle.operator,
	Tokens.Greater to YurinHighlightStyle.operator,
	Tokens.GreaterEq to YurinHighlightStyle.operator,
	Tokens.Less to YurinHighlightStyle.operator,
	Tokens.LessEq to YurinHighlightStyle.operator,
	Tokens.Plus to YurinHighlightStyle.operator,
	Tokens.Minus to YurinHighlightStyle.operator,
	Tokens.Multiply to YurinHighlightStyle.operator,
	Tokens.Divide to YurinHighlightStyle.operator,
	Tokens.Modulo to YurinHighlightStyle.operator,
	Tokens.And to YurinHighlightStyle.operator,
	Tokens.Or to YurinHighlightStyle.operator,
	Tokens.AndAnd to YurinHighlightStyle.operator,
	Tokens.OrOr to YurinHighlightStyle.operator,
	Tokens.Not to YurinHighlightStyle.operator,
	Tokens.Elvis to YurinHighlightStyle.operator,
	Tokens.Range to YurinHighlightStyle.operator,
	Tokens.RangeEq to YurinHighlightStyle.operator,
	Tokens.Arrow to YurinHighlightStyle.arrow,
	Tokens.Nullable to YurinHighlightStyle.nullableMarker,
	Tokens.UnsafeCast to YurinHighlightStyle.unsafeCast,
	Tokens.Reference to YurinHighlightStyle.reference,

	// Literal
	Tokens.StringLiteral to YurinHighlightStyle.stringLiteral,
	Tokens.IntLiteral to YurinHighlightStyle.numberLiteral,
)

object YurinHighlightStyle {
	val keyword = HighlightStyle(0xFFC679DD, isItalic = true)
	val dataDeclaration = HighlightStyle(0xFFE5C17C)
	val traitDeclaration = HighlightStyle(0xFF98C379)
	val effectDeclaration = HighlightStyle(0xFF4EC9B0)
	val functionDeclaration = HighlightStyle(0xFF61AEEF)
	val propertyDeclaration = HighlightStyle(0xFFE06C75)
	val functionCall = HighlightStyle(0xFF61AEEF)
	val namedValueArgument = HighlightStyle(0xFFD19A66)
	val square = HighlightStyle(0xFFA6B2C0)
	val colon = HighlightStyle(0xFF61AFEF)
	val paren = HighlightStyle(0xFFA6B2C0)
	val bracket = HighlightStyle(0xFFA6B2C0)
	val dot = HighlightStyle(0xFFA6B2C0)
	val operator = HighlightStyle(0xFF61AFEF)
	val comma = HighlightStyle(0xFFA6B2C0)
	val stringLiteral = HighlightStyle(0xFF98C379)
	val nullableMarker = HighlightStyle(0xFF61AFEF)
	val reference = HighlightStyle(0xFF61AFEF)
	val safeDot = HighlightStyle(0xFFA6B2C0)
	val arrow = HighlightStyle(0xFF61AFEF)
	val unsafeCast = HighlightStyle(0xFF61AFEF)
	val numberLiteral = HighlightStyle(0xFFD19A66)
	val blockComment = HighlightStyle(0xFF59626F, isItalic = true)
	val lineComment = HighlightStyle(0xFF59626F, isItalic = true)

	val error = HighlightStyle(underlineColor = 0xFFFF0000)
}