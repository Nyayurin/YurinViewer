package cn.yurin.languege.viewer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
	return HighlightingVisitor(tokens).apply {
		visit(YurinParser(tokens).yurinFile())
	}.build()
}

class HighlightingVisitor(
	private val tokens: CommonTokenStream,
) : YurinParserBaseVisitor<Unit>() {
	private val builder = AnnotatedString.Builder()

	override fun visitClassDeclaration(ctx: YurinParser.ClassDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> appendWithHidden((node as TerminalNode).symbol, ClassDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitObjectDeclaration(ctx: YurinParser.ObjectDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> appendWithHidden((node as TerminalNode).symbol, ClassDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitTraitDeclaration(ctx: YurinParser.TraitDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> appendWithHidden((node as TerminalNode).symbol, TraitDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitImplDeclaration(ctx: YurinParser.ImplDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> appendWithHidden((node as TerminalNode).symbol, FunctionDeclaration)
				ctx.typeReference() -> {
					node as YurinParser.TypeReferenceContext
					node.children?.forEach { childNode ->
						when (childNode) {
							in node.Identifier() -> appendWithHidden((childNode as TerminalNode).symbol, TraitDeclaration)
							else -> childNode.accept(this)
						}
					}
				}
				else -> node.accept(this)
			}
		}
	}

	override fun visitFunctionDeclaration(ctx: YurinParser.FunctionDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> appendWithHidden((node as TerminalNode).symbol, FunctionDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitPropertyDeclaration(ctx: YurinParser.PropertyDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> appendWithHidden((node as TerminalNode).symbol, PropertyDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitTypeAliasDeclaration(ctx: YurinParser.TypeAliasDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> appendWithHidden((node as TerminalNode).symbol, ClassDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitTypeReference(ctx: YurinParser.TypeReferenceContext) {
		ctx.children?.forEach { node ->
			when (node) {
				in ctx.Identifier() -> appendWithHidden((node as TerminalNode).symbol, ClassDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitFunctionCall(ctx: YurinParser.FunctionCallContext) {
		ctx.children?.forEach { node ->
			when (node) {
				in ctx.Identifier() -> appendWithHidden((node as TerminalNode).symbol, FunctionCall)
				else -> node.accept(this)
			}
		}
	}

	override fun visitValueParameter(ctx: YurinParser.ValueParameterContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> appendWithHidden((node as TerminalNode).symbol, PropertyDeclaration)
				else -> node.accept(this)
			}
		}
	}

	override fun visitTerminal(node: TerminalNode) {
		appendWithHidden(node.symbol)
	}

	private fun appendWithHidden(token: Token, highlight: Highlight? = null) {
		// 先加左边的 hidden tokens
		tokens.getHiddenTokensToLeft(token.tokenIndex)?.forEach { hidden ->
			appendToken(hidden)
		}
		// 加当前 token
		appendToken(token, highlight)
		// 右边的 hidden 通常留给下一个节点处理（避免重复）
	}

	private fun appendToken(token: Token, highlight: Highlight? = null) {
		if (token.type == Token.EOF) return
		val text = token.text ?: return
		val highlight = highlight ?: map[token.type]
		when (highlight != null) {
			true -> builder.highlight(highlight) {
				append(text)
			}

			else -> builder.append(text)
		}
	}

	fun build() = builder.toAnnotatedString()

	override fun defaultResult() {}
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

private fun <R : Any> AnnotatedString.Builder.highlight(
	color: Highlight,
	block: AnnotatedString.Builder.() -> R,
): R = when (color.color) {
	null -> block()
	else -> withStyle(
		style = SpanStyle(
			color = Color(color.color),
			fontWeight = when (color.isBold) {
				true -> FontWeight.Bold
				else -> FontWeight.Normal
			},
			fontStyle = when (color.isItalic) {
				true -> FontStyle.Italic
				else -> FontStyle.Normal
			},
		),
		block = block,
	)
}