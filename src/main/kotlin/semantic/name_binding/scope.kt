package cn.yurin.languege.viewer.semantic.name_binding

import cn.yurin.languege.viewer.semantic.symbol_skeleton.DataSymbol
import cn.yurin.languege.viewer.semantic.symbol_skeleton.DeclarationSymbol
import cn.yurin.languege.viewer.semantic.symbol_skeleton.FunctionSymbol
import cn.yurin.languege.viewer.semantic.symbol_skeleton.NamedSymbol
import cn.yurin.languege.viewer.semantic.symbol_skeleton.PackageSymbol
import cn.yurin.languege.viewer.semantic.symbol_skeleton.Symbol
import cn.yurin.languege.viewer.semantic.symbol_skeleton.TraitSymbol
import cn.yurin.languege.viewer.semantic.symbol_skeleton.TypealiasSymbol

class Scope(val parent: Scope? = null) {
	private val symbols = mutableMapOf<String, Symbol>()

	fun declare(name: String, symbol: Symbol) {
		symbols[name] = symbol
	}

	fun lookup(name: String): Symbol? = symbols[name] ?: parent?.lookup(name)
}

data class ScopeTree(
	val scope: Scope,
	val parent: ScopeTree? = null,
) {
	private val children = mutableListOf<ScopeTree>()

	operator fun plusAssign(element: ScopeTree) {
		children += element
	}

	operator fun get(index: Int) = children[index]
}

class ScopeBuilder {
	private lateinit var root: ScopeTree
	private lateinit var current: ScopeTree

	fun build(symbol: PackageSymbol): ScopeTree {
		root = ScopeTree(Scope())
		current = root

		visitPackage(symbol)

		return root
	}

	fun visitPackage(symbol: PackageSymbol) {
		symbol.declarations.forEach { declaration ->
			declare(declaration)
		}

		symbol.declarations.forEach { declaration ->
			visitDeclaration(declaration)
		}
	}

	fun visitDeclaration(symbol: DeclarationSymbol) {
		when (symbol) {
			is DataSymbol -> visitData(symbol)
			is TraitSymbol -> visitTrait(symbol)
			is TypealiasSymbol -> visitTypealias(symbol)
			is FunctionSymbol -> visitFunction(symbol)
			else -> {}
		}
	}

	fun visitData(symbol: DataSymbol) {
		enter()

		symbol.typeParameters.forEach { typeParameter ->
			declare(typeParameter)
		}

		symbol.memberDeclarations.forEach { member ->
			declare(member)
		}

		symbol.memberDeclarations.forEach { member ->
			visitDeclaration(member)
		}

		exit()
	}

	fun visitTrait(symbol: TraitSymbol) {
		enter()

		symbol.typeParameters.forEach { typeParameter ->
			declare(typeParameter)
		}

		symbol.memberDeclarations.forEach { member ->
			declare(member)
		}

		symbol.memberDeclarations.forEach { member ->
			visitDeclaration(member)
		}

		exit()
	}

	fun visitTypealias(symbol: TypealiasSymbol) {
		enter()

		symbol.typeParameters.forEach { typeParameter ->
			declare(typeParameter)
		}

		exit()
	}

	fun visitFunction(symbol: FunctionSymbol) {
		enter()

		symbol.typeParameters.forEach { typeParameter ->
			declare(typeParameter)
		}

		symbol.valueParameters.forEach { valueParameter ->
			declare(valueParameter)
		}

		exit()
	}

	private fun enter() {
		val child = ScopeTree(Scope(current.scope), current)
		current += child
		current = child
	}

	private fun exit() {
		current = current.parent!!
	}

	private fun declare(symbol: Symbol) {
		if (symbol is NamedSymbol) {
			current.scope.declare(symbol.name, symbol)
		}
	}

	private fun declare(name: String, symbol: Symbol) {
		current.scope.declare(name, symbol)
	}
}