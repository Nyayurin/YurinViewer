package cn.yurin.languege.viewer.semantic.symbol_skeleton

import com.yurin.antlrkotlin.parsers.generated.YurinParser
import com.yurin.antlrkotlin.parsers.generated.YurinParserBaseVisitor

fun symbolSkeleton(syntaxTree: YurinParser.YurinFileContext): PackageSymbol {
	return SymbolSkeletonVisitor().visitYurinFile(syntaxTree)
}

class SymbolSkeletonVisitor : YurinParserBaseVisitor<Symbol?>() {
	override fun visitYurinFile(ctx: YurinParser.YurinFileContext): PackageSymbol {
		return PackageSymbol.Builder().apply {
			ctx.packageHeader()?.Identifier()?.forEach { packageParts += it.text }

			ctx.declaration().forEach { declaration ->
				declarations += visitDeclaration(declaration)
			}
		}.build()
	}

	override fun visitDeclaration(ctx: YurinParser.DeclarationContext): DeclarationSymbol {
		return super.visitDeclaration(ctx) as DeclarationSymbol
	}

	override fun visitDataDeclaration(ctx: YurinParser.DataDeclarationContext): DataSymbol {
		return DataSymbol.Builder().apply {
			ctx.modifier().forEach { modifier ->
				modifiers += modifier.toModifier()
			}

			type = when {
				ctx.Val() != null -> DataSymbol.Type.Value
				ctx.Ref() != null -> DataSymbol.Type.Reference
				else -> null
			}

			name = ctx.Identifier().text

			ctx.typeParameters()?.typeParameter()?.forEach { typeParameter ->
				typeParameters += visitTypeParameter(typeParameter)
			}

			ctx.typeBinding()?.typeReference()?.forEach { typeReference ->
				typeBindings += visitTypeReference(typeReference)
			}

			ctx.typeBody()?.declaration()?.forEach { declaration ->
				memberDeclarations += visitDeclaration(declaration)
			}
		}.build()
	}

	override fun visitTraitDeclaration(ctx: YurinParser.TraitDeclarationContext): TraitSymbol {
		return TraitSymbol.Builder().apply {
			ctx.modifier().forEach { modifier ->
				modifiers += modifier.toModifier()
			}

			name = ctx.Identifier().text

			ctx.typeParameters()?.typeParameter()?.forEach { typeParameter ->
				typeParameters += visitTypeParameter(typeParameter)
			}

			ctx.typeBinding()?.typeReference()?.forEach { typeReference ->
				typeBindings += visitTypeReference(typeReference)
			}

			ctx.typeBody()?.declaration()?.forEach { declaration ->
				memberDeclarations += visitDeclaration(declaration)
			}
		}.build()
	}

	override fun visitTypealiasDeclaration(ctx: YurinParser.TypealiasDeclarationContext): TypealiasSymbol {
		return TypealiasSymbol.Builder().apply {
			ctx.modifier().forEach { modifier ->
				modifiers += modifier.toModifier()
			}

			name = ctx.Identifier().text

			ctx.typeParameters()?.typeParameter()?.forEach { typeParameter ->
				typeParameters += visitTypeParameter(typeParameter)
			}

			expandedType = visitTypeReference(ctx.typeReference())
		}.build()
	}

	override fun visitEffectDeclaration(ctx: YurinParser.EffectDeclarationContext): EffectSymbol {
		return EffectSymbol.Builder().apply {
			ctx.modifier().forEach { modifier ->
				modifiers += modifier.toModifier()
			}

			name = ctx.Identifier().text

			ctx.typeParameters()?.typeParameter()?.forEach { typeParameter ->
				typeParameters += visitTypeParameter(typeParameter)
			}
		}.build()
	}

	override fun visitImplDeclaration(ctx: YurinParser.ImplDeclarationContext): ImplSymbol {
		return ImplSymbol.Builder().apply {
			ctx.modifier().forEach { modifier ->
				modifiers += modifier.toModifier()
			}

			dataType = visitTypeReference(ctx.typeReference(0)!!)
			traitType = visitTypeReference(ctx.typeReference(1)!!)

			ctx.typeBody()?.declaration()?.forEach { declaration ->
				memberDeclarations += visitDeclaration(declaration)
			}
		}.build()
	}

	override fun visitFunctionDeclaration(ctx: YurinParser.FunctionDeclarationContext): FunctionSymbol {
		return FunctionSymbol.Builder().apply {
			ctx.modifier().forEach { modifier ->
				modifiers += modifier.toModifier()
			}

			ctx.receiverType()?.let { receiver ->
				receiverType = visitReceiverType(receiver)
			}

			name = ctx.Identifier().text

			ctx.typeParameters()?.typeParameter()?.forEach { typeParameter ->
				typeParameters += visitTypeParameter(typeParameter)
			}

			ctx.valueParameters().valueParameter().forEach { valueParameter ->
				valueParameters += visitValueParameter(valueParameter)
			}

			ctx.typeReference()?.let { typeReference ->
				returnType = visitTypeReference(typeReference)
			}

			ctx.functionEffects()?.let { functionEffects ->
				functionEffects.typeReference().forEach { typeReference ->
					effects += visitTypeReference(typeReference)
				}
			}
		}.build()
	}

	override fun visitPropertyDeclaration(ctx: YurinParser.PropertyDeclarationContext): PropertySymbol {
		return PropertySymbol.Builder().apply {
			ctx.modifier().forEach { modifier ->
				modifiers += modifier.toModifier()
			}

			ctx.receiverType()?.let { receiver ->
				receiverType = visitReceiverType(receiver)
			}

			isMutable = ctx.Var() != null
			name = ctx.Identifier().text

			ctx.typeReference()?.let { typeReference ->
				returnType = visitTypeReference(typeReference)
			}

			ctx.functionEffects()?.let { functionEffects ->
				functionEffects.typeReference().forEach { typeReference ->
					effects += visitTypeReference(typeReference)
				}
			}
		}.build()
	}

	override fun visitReceiverType(ctx: YurinParser.ReceiverTypeContext): TypeReferenceSymbol {
		return visitTypeReference(ctx.typeReference())
	}

	override fun visitTypeParameter(ctx: YurinParser.TypeParameterContext): TypeParameterSymbol {
		return TypeParameterSymbol.Builder().apply {
			name = ctx.Identifier().text
		}.build()
	}

	override fun visitValueParameter(ctx: YurinParser.ValueParameterContext): ValueParameterSymbol {
		return ValueParameterSymbol.Builder().apply {
			name = ctx.Identifier().text
			type = visitTypeReference(ctx.typeReference())
		}.build()
	}

	override fun visitTypeReference(ctx: YurinParser.TypeReferenceContext): TypeReferenceSymbol {
		return when {
			ctx.typeReference() != null -> visitTypeReference(ctx.typeReference()!!)
			ctx.Nothing() != null -> TypeReferenceSymbol.Nothing
			ctx.Dynamic() != null -> TypeReferenceSymbol.Dynamic
			else -> TypeReferenceSymbol.Regular.Builder().apply {
				isExistential = ctx.Exist() != null

				ctx.typeIdentifier()?.Identifier()?.forEach { identifier ->
					identifiers += identifier.text
				}

				ctx.typeArguments()?.typeArgument()?.forEach { typeArgument ->
					typeArguments += visitTypeReference(typeArgument.typeReference())
				}

				nullable = ctx.Nullable() != null
			}.build()
		}
	}

	override fun defaultResult() = null

	private fun YurinParser.ModifierContext.toModifier() = when {
		Open() != null -> Modifier.Open
		Sealed() != null -> Modifier.Sealed
		Abstract() != null -> Modifier.Abstract
		Singleton() != null -> Modifier.Singleton
		Operator() != null -> Modifier.Operator
		Impl() != null -> Modifier.Impl
		visibilityModifier() != null -> visibilityModifier()!!.toModifier()
		else -> error("Unknown modifier: $text")
	}

	private fun YurinParser.VisibilityModifierContext.toModifier() = when {
		Public() != null -> Modifier.Public
		Restricted() != null -> Modifier.Restricted
		Internal() != null -> Modifier.Internal
		Private() != null -> Modifier.Private
		else -> error("Unknown modifier: $text")
	}
}