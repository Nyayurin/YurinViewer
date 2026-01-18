package cn.yurin.languege.viewer.semantic.symbol_skeleton

fun Symbol.prettyString(): String = when (this) {
	is PackageSymbol -> prettyString()
	is DeclarationSymbol -> prettyString()
	is TypeReferenceSymbol -> prettyString()
	is TypeParameterSymbol -> prettyString()
	is ValueParameterSymbol -> prettyString()
}

fun PackageSymbol.prettyString(): String = buildString {
	appendLine("PackageSymbol(")
	padding {
		append("packageParts = ")
		appendLine(packageParts.prettyString())
		append("declarations = ")
		append(declarations.prettyString())
	}
	append(")")
}

fun DeclarationSymbol.prettyString(): String = when (this) {
	is DataSymbol -> prettyString()
	is TraitSymbol -> prettyString()
	is TypealiasSymbol -> prettyString()
	is EffectSymbol -> prettyString()
	is ImplSymbol -> prettyString()
	is FunctionSymbol -> prettyString()
	is PropertySymbol -> prettyString()
}

fun DataSymbol.prettyString(): String = buildString {
	appendLine("DataSymbol(")
	padding {
		append("modifiers = ")
		appendLine(modifiers.prettyString())
		appendLine("type = $type")
		appendLine("name = $name")
		append("typeParameters = ")
		appendLine(typeParameters.prettyString())
		append("structureExtension = ")
		appendLine(typeBindings.prettyString())
		append("memberDeclarations = ")
		append(memberDeclarations.prettyString())
	}
	append(")")
}

fun TraitSymbol.prettyString(): String = buildString {
	appendLine("TraitSymbol(")
	padding {
		append("modifiers = ")
		appendLine(modifiers.prettyString())
		appendLine("name = $name")
		append("typeParameters = ")
		appendLine(typeParameters.prettyString())
		append("inheritance = ")
		appendLine(typeBindings.prettyString())
		append("memberDeclarations = ")
		append(memberDeclarations.prettyString())
	}
	append(")")
}

fun TypealiasSymbol.prettyString(): String = buildString {
	appendLine("TypeAliasSymbol(")
	padding {
		append("modifiers = ")
		appendLine(modifiers.prettyString())
		appendLine("name = $name")
		append("typeParameters = ")
		appendLine(typeParameters.prettyString())
		append("expandedType = ")
		append(expandedType.prettyString())
	}
	append(")")
}

fun EffectSymbol.prettyString(): String = buildString {
	appendLine("TypealiasSymbol(")
	padding {
		append("modifiers = ")
		appendLine(modifiers.prettyString())
		appendLine("name = $name")
		append("typeParameters = ")
		append(typeParameters.prettyString())
	}
	append(")")
}

fun ImplSymbol.prettyString(): String = buildString {
	appendLine("ImplSymbol(")
	padding {
		append("modifiers = ")
		appendLine(modifiers.prettyString())
		append("dataType = ")
		appendLine(dataType.prettyString())
		append("traitType = ")
		appendLine(traitType.prettyString())
		append("memberDeclarations = ")
		append(memberDeclarations.prettyString())
	}
	append(")")
}

fun FunctionSymbol.prettyString(): String = buildString {
	appendLine("FunctionSymbol(")
	padding {
		append("modifiers = ")
		appendLine(modifiers.prettyString())
		append("receiverType = ")
		appendLine(receiverType?.prettyString())
		appendLine("name = $name")
		append("typeParameters = ")
		appendLine(typeParameters.prettyString())
		append("valueParameters = ")
		appendLine(valueParameters.prettyString())
		append("returnType = ")
		appendLine(returnType?.prettyString())
		append("effects = ")
		append(effects.prettyString())
	}
	append(")")
}

fun PropertySymbol.prettyString(): String = buildString {
	appendLine("PropertySymbol(")
	padding {
		append("modifiers = ")
		appendLine(modifiers.prettyString())
		appendLine("isMutable = $isMutable")
		append("receiverType = ")
		appendLine(receiverType?.prettyString())
		appendLine("name = $name")
		append("returnType = ")
		appendLine(returnType?.prettyString())
		append("effects = ")
		append(effects.prettyString())
	}
	append(")")
}

fun TypeReferenceSymbol.prettyString(): String = when (this) {
	TypeReferenceSymbol.Dynamic -> "TypeReference.Dynamic"
	TypeReferenceSymbol.Nothing -> "TypeReference.Nothing"
	is TypeReferenceSymbol.Regular -> prettyString()
}

fun TypeReferenceSymbol.Regular.prettyString(): String = buildString {
	appendLine("TypeReference.Regular(")
	padding {
		appendLine("isExistential = $isExistential")
		append("identifiers = ")
		appendLine(identifiers.prettyString())
		append("typeArguments = ")
		appendLine(typeArguments.prettyString())
		append("nullable = $nullable")
	}
	append(")")
}

fun TypeParameterSymbol.prettyString(): String = buildString {
	appendLine("TypeParameterSymbol(")
	padding {
		append("name = $name")
	}
	append(")")
}

fun ValueParameterSymbol.prettyString(): String = buildString {
	appendLine("ValueParameterSymbol(")
	padding {
		appendLine("name = $name")
		append("type = ")
		append(type.prettyString())
	}
	append(")")
}

private fun <T> List<T>.prettyString(): String = buildString {
	append("[")
	if (this@prettyString.isNotEmpty()) {
		appendLine()
		padding {
			append(
				joinToString("\n") { element ->
					when (element) {
						is Symbol -> element.prettyString()
						else -> element.toString()
					}
				}
			)
		}
	}
	append("]")
}

private fun <K, V> Map<K, V>.prettyString(): String = buildString {
	append("{")
	if (this@prettyString.isNotEmpty()) {
		appendLine()
		padding {
			append(
				this@prettyString.entries.map { it.key to it.value }.joinToString("\n") { (key, value) ->
					"$key = " + when (value) {
						is Symbol -> value.prettyString()
						else -> value.toString()
					}
				}
			)
		}
	}
	append("}")
}

private fun StringBuilder.padding(block: StringBuilder.() -> Unit) {
	appendLine(buildString(block).padding(1))
}

private fun String.padding(times: Int) = prependIndent("  ".repeat(times))