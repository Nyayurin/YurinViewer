package cn.yurin.languege.viewer

import cn.yurin.languege.viewer.semantic.symbol_skeleton.*
import com.yurin.antlrkotlin.parsers.generated.YurinParser
import com.yurin.antlrkotlin.parsers.generated.YurinParserBaseVisitor

class SemanticHighlightVisitor(
	private val packageSymbol: PackageSymbol,
) : YurinParserBaseVisitor<Unit>() {
	private val highlights = mutableListOf<Highlight>()
	private val currentPackageParts = mutableListOf<String>()

	override fun visitYurinFile(ctx: YurinParser.YurinFileContext) {
		ctx.packageHeader()?.Identifier()?.forEach {
			currentPackageParts += it.text
		}
		super.visitYurinFile(ctx)
	}

	override fun visitTypeIdentifier(ctx: YurinParser.TypeIdentifierContext) {
		fun List<DeclarationSymbol>.find(name: String) = find { declaration ->
			when (declaration) {
				is NamedSymbol -> declaration.name == name
				else -> false
			}
		}

		fun findAndHighlight(startSymbol: Symbol? = null) {
			var symbol: Symbol? = startSymbol
			var packagePartIndex = 0
			ctx.Identifier().forEach { node ->
				val identifier = node.text
				when (symbol) {
					null if (packageSymbol.packageParts.getOrNull(packagePartIndex) == identifier) -> if (++packagePartIndex >= packageSymbol.packageParts.size) {
						symbol = packageSymbol
					}

					is PackageSymbol -> symbol = symbol.declarations.find(identifier) ?: return

					is DataSymbol -> symbol = when (val childSymbol = symbol.memberDeclarations.find(identifier) ?: return) {
						is FunctionSymbol,
						is PropertySymbol,
							-> when (symbol.modifiers.any { it == Modifier.Singleton }) {
							true -> childSymbol
							else -> return
						}

						else -> childSymbol
					}

					is TraitSymbol -> symbol = when (val childSymbol = symbol.memberDeclarations.find(identifier) ?: return) {
						is FunctionSymbol,
						is PropertySymbol,
							-> when (symbol.modifiers.any { it == Modifier.Singleton }) {
							true -> childSymbol
							else -> return
						}

						else -> childSymbol
					}

					else -> return
				}
				(symbol as? DeclarationSymbol)?.let { declaration ->
					val highlightStyle = when (declaration) {
						is DataSymbol -> YurinHighlightStyle.dataDeclaration
						is TraitSymbol -> YurinHighlightStyle.traitDeclaration
						is TypealiasSymbol -> YurinHighlightStyle.dataDeclaration
						is EffectSymbol -> YurinHighlightStyle.effectDeclaration
						is FunctionSymbol -> YurinHighlightStyle.functionCall
						is PropertySymbol -> YurinHighlightStyle.propertyDeclaration
						else -> null
					} ?: return
					highlights.addIfNotNull(node.symbol.toHighlight(highlightStyle))
				}
			}
		}

		if (packageSymbol.packageParts == currentPackageParts) {
			findAndHighlight(packageSymbol)
		}
		findAndHighlight()
	}

	override fun defaultResult() {}

	fun build() = highlights.toList()

	private fun <T> MutableList<T>.addIfNotNull(element: T?) {
		if (element != null) {
			add(element)
		}
	}
}