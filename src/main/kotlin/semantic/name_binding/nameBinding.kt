package cn.yurin.languege.viewer.semantic.name_binding

import cn.yurin.languege.viewer.semantic.symbol_skeleton.PackageSymbol
import com.yurin.antlrkotlin.parsers.generated.YurinParser

fun nameBinding(syntaxTree: YurinParser.YurinFileContext, symbol: PackageSymbol) {
	val scopeTree = scopeConstruction(symbol)
	identifiersBinding()
	structuralBinding()
}

fun scopeConstruction(symbol: PackageSymbol): ScopeTree {
	return ScopeBuilder().build(symbol)
}

fun identifiersBinding() {

}

fun structuralBinding() {

}