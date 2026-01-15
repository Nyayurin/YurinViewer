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
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.Token
import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.antlr.v4.kotlinruntime.tree.ParseTreeWalker

fun getErrors(source: String) = PositionErrorListener(source).let { listener ->
	val tokens = CommonTokenStream(YurinLexer(CharStreams.fromString(source)))
	YurinParser(tokens).apply {
		removeErrorListeners()
		addErrorListener(listener)
	}.yurinFile()
	listener.errors
}

fun highlight(source: String) = AnnotatedString.Builder(source).apply {
	val tokens = CommonTokenStream(YurinLexer(CharStreams.fromString(source)))
	val tree = YurinParser(tokens).yurinFile()
	lexicalHighlight(tokens)
	syntaxHighlight(tree)
	semanticHighlight(tree)
}.toAnnotatedString()

object HighlightingTransformation : VisualTransformation {
	override fun filter(text: AnnotatedString): TransformedText = TransformedText(
		text = highlight(text.text),
		offsetMapping = OffsetMapping.Identity,
	)
}

fun AnnotatedString.Builder.lexicalHighlight(tokens: CommonTokenStream) {
	tokens.tokens.forEach { token ->
		highlightToken(token)
	}
}

fun AnnotatedString.Builder.syntaxHighlight(tree: ParseTree) {
	SyntaxHighlightingVisitor(this).visit(tree)
}

fun AnnotatedString.Builder.semanticHighlight(tree: ParseTree) {
	val listener = DeclarationCollectListener()
	ParseTreeWalker.DEFAULT.walk(listener, tree)
	SemanticHighlightVisitor(this, listener).visit(tree)
}

fun AnnotatedString.Builder.highlightToken(token: Token, highlight: Highlight? = null) {
	if (token.type in listOf(Token.EOF, Tokens.WS, Tokens.NL)) return

	val highlight = highlight ?: map[token.type] ?: return

	val text = token.text ?: ""
	val trimStartLength = text.length - text.trimStart().length
	val trimEndLength = text.length - text.trimEnd().length
	val start = token.startIndex + trimStartLength
	val end = token.stopIndex + 1 - trimEndLength

	if (start >= 0 && end <= length && start < end) {
		addStyle(
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
	Tokens.UnsafeCast to UnsafeCast,
	Tokens.As to Keyword,
	Tokens.SafeAs to Keyword,
	Tokens.In to Keyword,
	Tokens.NotIn to Keyword,
	Tokens.Is to Keyword,
	Tokens.NotIs to Keyword,
	Tokens.Reference to Reference,

	Tokens.SingleLineComment to LineComment,
	Tokens.MultiLineComment to BlockComment,
)

enum class Highlight(
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
	UnsafeCast(0xFF61AFEF),
	NumberLiteral(0xFFD19A66),
	BlockComment(0xFF59626F, isItalic = true),
	LineComment(0xFF59626F, isItalic = true),
	DataDeclaration(0xFFE5C17C),
	TraitDeclaration(0xFF98C379),
	PropertyDeclaration(0xFFE06C75),
}