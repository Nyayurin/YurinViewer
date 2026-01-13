package cn.yurin.languege.viewer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.yurin.antlrkotlin.parsers.generated.YurinLexer
import com.yurin.antlrkotlin.parsers.generated.YurinLexer.Tokens
import com.yurin.antlrkotlin.parsers.generated.YurinParser
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import cn.yurin.languege.viewer.Highlight.*
import org.antlr.v4.kotlinruntime.Token

fun highlight(code: String): AnnotatedString {
	val lexer = YurinLexer(CharStreams.fromString(code))
	val tokens = CommonTokenStream(lexer)
	YurinParser(tokens).yurinFile()
	return buildAnnotatedString {
		tokens.tokens.forEach { token ->
			if (token.type == Token.EOF) return@forEach
			val text = token.text ?: return@forEach
			val highlight = map[token.type]
			if (highlight != null) {
				highlight(highlight) {
					append(text)
				}
			} else {
				append(text)
			}
		}
	}
}

private val map = mapOf(
	Tokens.Package to Orange,
	Tokens.Import to Orange,
	Tokens.Class to Orange,
	Tokens.Object to Orange,
	Tokens.Trait to Orange,
	Tokens.Impl to Orange,
	Tokens.Fun to Orange,
	Tokens.Var to Orange,
	Tokens.Val to Orange,
	Tokens.Get to Orange,
	Tokens.Set to Orange,
	Tokens.Typealias to Orange,
	Tokens.Sealed to Orange,
	Tokens.Operator to Orange,
	Tokens.If to Orange,
	Tokens.Else to Orange,
	Tokens.Match to Orange,
	Tokens.Return to Orange,
	Tokens.Break to Orange,
	Tokens.Continue to Orange,
	Tokens.Exist to Orange,
	Tokens.This to Orange,

	Tokens.StringLiteral to StringGreen,

	Tokens.IntLiteral to NumberBlue,

	Tokens.Colon to PunctGrayBlue,
	Tokens.Dot to PunctGrayBlue,
	Tokens.SafeDot to PunctGrayBlue,
	Tokens.Comma to PunctGrayBlue,
	Tokens.LeftBracket to PunctGrayBlue,
	Tokens.RightBracket to PunctGrayBlue,
	Tokens.LeftParen to PunctGrayBlue,
	Tokens.RightParen to PunctGrayBlue,
	Tokens.LeftSquare to PunctGrayBlue,
	Tokens.RightSquare to PunctGrayBlue,
	Tokens.LeftAngle to PunctGrayBlue,
	Tokens.RightAngle to PunctGrayBlue,
	Tokens.Eq to PunctGrayBlue,
	Tokens.NotEq to PunctGrayBlue,
	Tokens.EqEq to PunctGrayBlue,
	Tokens.Greater to PunctGrayBlue,
	Tokens.GreaterEq to PunctGrayBlue,
	Tokens.Less to PunctGrayBlue,
	Tokens.LessEq to PunctGrayBlue,
	Tokens.Plus to PunctGrayBlue,
	Tokens.Minus to PunctGrayBlue,
	Tokens.Multiply to PunctGrayBlue,
	Tokens.Divide to PunctGrayBlue,
	Tokens.Modulo to PunctGrayBlue,
	Tokens.And to PunctGrayBlue,
	Tokens.Or to PunctGrayBlue,
	Tokens.AndAnd to PunctGrayBlue,
	Tokens.OrOr to PunctGrayBlue,
	Tokens.Not to PunctGrayBlue,
	Tokens.Arrow to PunctGrayBlue,
	Tokens.Elvis to PunctGrayBlue,
	Tokens.Range to PunctGrayBlue,
	Tokens.RangeEq to PunctGrayBlue,
	Tokens.As to PunctGrayBlue,
	Tokens.SafeAs to PunctGrayBlue,
	Tokens.In to PunctGrayBlue,
	Tokens.NotIn to PunctGrayBlue,
	Tokens.Is to PunctGrayBlue,
	Tokens.NotIs to PunctGrayBlue,
	Tokens.Reference to PunctGrayBlue,

	Tokens.SingleLineComment to CommentGreenGray,
	Tokens.MultiLineComment to CommentGreenGray,

	Tokens.Identifier to DefaultText,
)

private enum class Highlight(val color: Long) {
	Orange(0xFFCC7832),
	StringGreen(0xFF6A8759),
	NumberBlue(0xFF6897BB),
	PunctGrayBlue(0xFFA9B7C6),
	CommentGreenGray(0xFF7F9F7F),
	DefaultText(0xFFE8E8E8),
}

private fun <R : Any> AnnotatedString.Builder.highlight(
	color: Highlight,
	block: AnnotatedString.Builder.() -> R,
): R = withStyle(SpanStyle(color = Color(color.color)), block)