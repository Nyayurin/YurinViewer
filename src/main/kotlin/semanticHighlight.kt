package cn.yurin.languege.viewer

import com.yurin.antlrkotlin.parsers.generated.YurinParser
import com.yurin.antlrkotlin.parsers.generated.YurinParserBaseListener
import com.yurin.antlrkotlin.parsers.generated.YurinParserBaseVisitor

class DeclarationCollectListener : YurinParserBaseListener() {
	private val dataIdentifiers = mutableListOf<Pair<String, String>>()
	private val traitIdentifiers = mutableListOf<Pair<String, String>>()
	private lateinit var currentPackage: String
	private val currentSuffix = mutableListOf<String>()

	private fun enter(identifier: String): Pair<String, String> {
		val result = buildString {
			if (currentSuffix.isNotEmpty()) {
				append(currentSuffix.joinToString("."))
				append(".")
			}
			append(identifier)
		}
		currentSuffix += identifier
		return currentPackage to result
	}

	private fun exit() {
		currentSuffix.removeLast()
	}

	override fun enterYurinFile(ctx: YurinParser.YurinFileContext) {
		currentPackage = ctx.packageHeader()?.Identifier()?.joinToString(".") ?: ""
	}

	override fun enterDataDeclaration(ctx: YurinParser.DataDeclarationContext) {
		dataIdentifiers += enter(ctx.Identifier().text)
	}

	override fun exitDataDeclaration(ctx: YurinParser.DataDeclarationContext) = exit()

	override fun enterTraitDeclaration(ctx: YurinParser.TraitDeclarationContext) {
		traitIdentifiers += enter(ctx.Identifier().text)
	}

	override fun exitTraitDeclaration(ctx: YurinParser.TraitDeclarationContext) = exit()

	override fun enterTypeAliasDeclaration(ctx: YurinParser.TypeAliasDeclarationContext) {
		dataIdentifiers += enter(ctx.Identifier().text)
	}

	override fun exitTypeAliasDeclaration(ctx: YurinParser.TypeAliasDeclarationContext) = exit()

	val dataTypeIdentifiers: List<Pair<String, String>>
		get() = dataIdentifiers.toList()

	val traitTypeIdentifiers: List<Pair<String, String>>
		get() = traitIdentifiers.toList()
}

class SemanticHighlightVisitor(
	private val collectListener: DeclarationCollectListener,
) : YurinParserBaseVisitor<Unit>() {
	private val highlights = mutableListOf<Highlight>()
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

	override fun visitYurinFile(ctx: YurinParser.YurinFileContext) {
		currentPackage = ctx.packageHeader()?.Identifier()?.joinToString(".") ?: ""
		super.visitYurinFile(ctx)
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
					-> highlights.addIfNotNull(node.symbol.toHighlight(YurinHighlightStyle.dataDeclaration))

				in currentPackageTraitIdentifiers,
				in packagedTraitIdentifiers,
					-> highlights.addIfNotNull(node.symbol.toHighlight(YurinHighlightStyle.traitDeclaration))
			}
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