package cn.yurin.languege.viewer

import com.yurin.antlrkotlin.parsers.generated.YurinParser
import com.yurin.antlrkotlin.parsers.generated.YurinParserBaseVisitor
import org.antlr.v4.kotlinruntime.tree.TerminalNode

class SyntaxHighlightingVisitor : YurinParserBaseVisitor<Unit>() {
	private val highlights = mutableListOf<Highlight>()

	override fun visitDataDeclaration(ctx: YurinParser.DataDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlights.addIfNotNull((node as TerminalNode).symbol.toHighlight(YurinHighlightStyle.dataDeclaration))
				else -> node.accept(this)
			}
		}
	}

	override fun visitTraitDeclaration(ctx: YurinParser.TraitDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlights.addIfNotNull((node as TerminalNode).symbol.toHighlight(YurinHighlightStyle.traitDeclaration))
				else -> node.accept(this)
			}
		}
	}

	override fun visitFunctionDeclaration(ctx: YurinParser.FunctionDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlights.addIfNotNull((node as TerminalNode).symbol.toHighlight(YurinHighlightStyle.functionDeclaration))
				else -> node.accept(this)
			}
		}
	}

	override fun visitPropertyDeclaration(ctx: YurinParser.PropertyDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlights.addIfNotNull((node as TerminalNode).symbol.toHighlight(YurinHighlightStyle.propertyDeclaration))
				else -> node.accept(this)
			}
		}
	}

	override fun visitTypealiasDeclaration(ctx: YurinParser.TypealiasDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlights.addIfNotNull((node as TerminalNode).symbol.toHighlight(YurinHighlightStyle.dataDeclaration))
				else -> node.accept(this)
			}
		}
	}

	override fun visitValueParameter(ctx: YurinParser.ValueParameterContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlights.addIfNotNull((node as TerminalNode).symbol.toHighlight(YurinHighlightStyle.propertyDeclaration))
				else -> node.accept(this)
			}
		}
	}

	override fun visitValueArgument(ctx: YurinParser.ValueArgumentContext) {
		ctx.Identifier()?.symbol?.let { token -> highlights.addIfNotNull(token.toHighlight(YurinHighlightStyle.namedValueArgument)) }
		ctx.expression().accept(this)
	}

	override fun visitVariableStatement(ctx: YurinParser.VariableStatementContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlights.addIfNotNull((node as TerminalNode).symbol.toHighlight(YurinHighlightStyle.propertyDeclaration))
				else -> node.accept(this)
			}
		}
	}

	override fun visitInfixFunctionCall(ctx: YurinParser.InfixFunctionCallContext) {
		ctx.rangeExpression().forEach { node ->
			node.accept(this)
		}

		ctx.Identifier().forEach { node ->
			highlights.addIfNotNull(node.symbol.toHighlight(YurinHighlightStyle.functionCall))
		}
	}

	override fun defaultResult() {}

	fun build() = highlights.toList()

	private fun <T> MutableList<T>.addIfNotNull(element: T?) {
		if (element != null) {
			add(element)
		}
	}
}