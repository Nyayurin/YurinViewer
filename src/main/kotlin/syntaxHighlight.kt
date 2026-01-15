package cn.yurin.languege.viewer

import com.yurin.antlrkotlin.parsers.generated.YurinParser
import com.yurin.antlrkotlin.parsers.generated.YurinParserBaseVisitor
import org.antlr.v4.kotlinruntime.tree.TerminalNode

class SyntaxHighlightingVisitor : YurinParserBaseVisitor<Unit>() {
	private val highlights = mutableListOf<Highlight>()

	override fun visitYurinFile(ctx: YurinParser.YurinFileContext) {
		super.visitYurinFile(ctx)
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

	override fun visitImplDeclaration(ctx: YurinParser.ImplDeclarationContext) {
		super.visitImplDeclaration(ctx)
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

	override fun visitTypeAliasDeclaration(ctx: YurinParser.TypeAliasDeclarationContext) {
		ctx.children?.forEach { node ->
			when (node) {
				ctx.Identifier() -> highlights.addIfNotNull((node as TerminalNode).symbol.toHighlight(YurinHighlightStyle.dataDeclaration))
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
				ctx.Identifier() -> highlights.addIfNotNull((node as TerminalNode).symbol.toHighlight(YurinHighlightStyle.propertyDeclaration))
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
		ctx.Identifier()?.symbol?.let { token -> highlights.addIfNotNull(token.toHighlight(YurinHighlightStyle.namedValueArgument)) }
		ctx.expression().accept(this)
	}

	override fun visitExistTypeReference(ctx: YurinParser.ExistTypeReferenceContext) {
		super.visitExistTypeReference(ctx)
	}

	override fun visitTypeReference(ctx: YurinParser.TypeReferenceContext) {
		super.visitTypeReference(ctx)
	}

	override fun visitTypeIdentifier(ctx: YurinParser.TypeIdentifierContext) {
		super.visitTypeIdentifier(ctx)
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
		ctx.rangeExpression().forEach { node ->
			node.accept(this)
		}

		ctx.Identifier().forEach { node ->
			highlights.addIfNotNull(node.symbol.toHighlight(YurinHighlightStyle.functionCall))
		}
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

	override fun defaultResult() {}

	fun build() = highlights.toList()

	private fun <T> MutableList<T>.addIfNotNull(element: T?) {
		if (element != null) {
			add(element)
		}
	}
}