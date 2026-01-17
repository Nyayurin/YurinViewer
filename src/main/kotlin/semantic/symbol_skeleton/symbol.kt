package cn.yurin.languege.viewer.semantic.symbol_skeleton

import kotlin.properties.Delegates

interface NamedSymbol {
	val name: String
}

enum class Modifier {
	Open,
	Abstract,
	Operator,
	Singleton,
}

sealed class Symbol

data class PackageSymbol(
	val packageParts: List<String>,
	val declarations: List<DeclarationSymbol>,
) : Symbol() {
	class Builder {
		val packageParts = mutableListOf<String>()
		val declarations = mutableListOf<DeclarationSymbol>()
		fun build() = PackageSymbol(packageParts.toList(), declarations.toList())
	}
}

sealed class DeclarationSymbol : Symbol()

data class DataSymbol(
	val modifiers: List<Modifier>,
	val type: Type?,
	override val name: String,
	val typeParameters: List<TypeParameterSymbol>,
	val structureExtension: List<TypeReferenceSymbol>,
	val memberDeclarations: List<DeclarationSymbol>,
) : DeclarationSymbol(), NamedSymbol {
	enum class Type {
		Value, Reference
	}

	class Builder {
		val modifiers = mutableListOf<Modifier>()
		var type: Type? = null
		lateinit var name: String
		val typeParameters = mutableListOf<TypeParameterSymbol>()
		val structureExtension = mutableListOf<TypeReferenceSymbol>()
		val memberDeclarations = mutableListOf<DeclarationSymbol>()
		fun build() = DataSymbol(modifiers.toList(), type, name, typeParameters.toList(), structureExtension.toList(), memberDeclarations.toList())
	}
}

data class TraitSymbol(
	val modifiers: List<Modifier>,
	override val name: String,
	val typeParameters: List<TypeParameterSymbol>,
	val inheritance: List<TypeReferenceSymbol>,
	val memberDeclarations: List<DeclarationSymbol>,
) : DeclarationSymbol(), NamedSymbol {
	class Builder {
		val modifiers = mutableListOf<Modifier>()
		lateinit var name: String
		val typeParameters = mutableListOf<TypeParameterSymbol>()
		val inheritance = mutableListOf<TypeReferenceSymbol>()
		val memberDeclarations = mutableListOf<DeclarationSymbol>()
		fun build() = TraitSymbol(modifiers.toList(), name, typeParameters.toList(), inheritance.toList(), memberDeclarations.toList())
	}
}

data class TypealiasSymbol(
	val modifiers: List<Modifier>,
	override val name: String,
	val typeParameters: List<TypeParameterSymbol>,
	val expandedType: TypeReferenceSymbol,
) : DeclarationSymbol(), NamedSymbol {
	class Builder {
		val modifiers = mutableListOf<Modifier>()
		lateinit var name: String
		val typeParameters = mutableListOf<TypeParameterSymbol>()
		lateinit var expandedType: TypeReferenceSymbol
		fun build() = TypealiasSymbol(modifiers.toList(), name, typeParameters.toList(), expandedType)
	}
}

data class ImplSymbol(
	val modifiers: List<Modifier>,
	val dataType: TypeReferenceSymbol,
	val traitType: TypeReferenceSymbol,
	val memberDeclarations: List<DeclarationSymbol>,
) : DeclarationSymbol() {
	class Builder {
		val modifiers = mutableListOf<Modifier>()
		lateinit var dataType: TypeReferenceSymbol
		lateinit var traitType: TypeReferenceSymbol
		val memberDeclarations = mutableListOf<DeclarationSymbol>()
		fun build() = ImplSymbol(modifiers.toList(), dataType, traitType, memberDeclarations.toList())
	}
}

data class FunctionSymbol(
	val modifiers: List<Modifier>,
	val receiverType: TypeReferenceSymbol?,
	override val name: String,
	val typeParameters: List<TypeParameterSymbol>,
	val valueParameters: List<ValueParameterSymbol>,
	val returnType: TypeReferenceSymbol?,
) : DeclarationSymbol(), NamedSymbol {
	class Builder {
		val modifiers = mutableListOf<Modifier>()
		var receiverType: TypeReferenceSymbol? = null
		lateinit var name: String
		val typeParameters = mutableListOf<TypeParameterSymbol>()
		val valueParameters = mutableListOf<ValueParameterSymbol>()
		var returnType: TypeReferenceSymbol? = null
		fun build() = FunctionSymbol(modifiers.toList(), receiverType, name, typeParameters.toList(), valueParameters.toList(), returnType)
	}
}

data class PropertySymbol(
	val modifiers: List<Modifier>,
	val isMutable: Boolean,
	val receiverType: TypeReferenceSymbol?,
	override val name: String,
	val returnType: TypeReferenceSymbol?,
) : DeclarationSymbol(), NamedSymbol {
	class Builder {
		val modifiers = mutableListOf<Modifier>()
		var isMutable: Boolean by Delegates.notNull()
		var receiverType: TypeReferenceSymbol? = null
		lateinit var name: String
		var returnType: TypeReferenceSymbol? = null
		fun build() = PropertySymbol(modifiers.toList(), isMutable, receiverType, name, returnType)
	}
}

sealed class TypeReferenceSymbol : Symbol() {
	data class Regular(
		val isExistential: Boolean,
		val identifiers: List<String>,
		val typeArguments: List<TypeReferenceSymbol>,
		val nullable: Boolean,
	) : TypeReferenceSymbol() {
		class Builder {
			var isExistential: Boolean by Delegates.notNull()
			val identifiers = mutableListOf<String>()
			val typeArguments = mutableListOf<TypeReferenceSymbol>()
			var nullable: Boolean by Delegates.notNull()
			fun build() = Regular(isExistential, identifiers.toList(), typeArguments.toList(), nullable)
		}
	}

	data object Dynamic : TypeReferenceSymbol()
	data object Nothing : TypeReferenceSymbol()
}

data class TypeParameterSymbol(
	val name: String,
) : Symbol() {
	class Builder {
		lateinit var name: String
		fun build() = TypeParameterSymbol(name)
	}
}

data class ValueParameterSymbol(
	val name: String,
	val type: TypeReferenceSymbol,
) : Symbol() {
	class Builder {
		lateinit var name: String
		lateinit var type: TypeReferenceSymbol
		fun build() = ValueParameterSymbol(name, type)
	}
}