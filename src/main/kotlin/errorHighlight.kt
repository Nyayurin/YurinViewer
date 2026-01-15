package cn.yurin.languege.viewer

import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.Token

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