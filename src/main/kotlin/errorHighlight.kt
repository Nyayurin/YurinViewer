package cn.yurin.languege.viewer

import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.Token

class ErrorHighlightListener(
	private val source: String,
) : BaseErrorListener() {
	private val highlights = mutableListOf<Highlight>()
	private val errors = mutableListOf<Error>()

	override fun syntaxError(
		recognizer: Recognizer<*, *>,
		offendingSymbol: Any?,
		line: Int,
		charPositionInLine: Int,
		msg: String,
		e: RecognitionException?,
	) {
		val start = source.lines().take(line - 1).sumOf { it.length + 1 } + charPositionInLine
		val end = when (offendingSymbol) {
			is Token -> offendingSymbol.stopIndex
			else -> start
		}.coerceAtLeast(start) + 1

		highlights += Highlight(start, end, YurinHighlightStyle.error)
		errors += Error(line, charPositionInLine, msg)
	}

	fun build() = highlights.toList() to errors.toList()

	data class Error(
		val line: Int,
		val charPositionInLine: Int,
		val msg: String,
	)
}