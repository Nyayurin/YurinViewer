package cn.yurin.languege.viewer

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class HighlightingTransformation(private val annotatedString: AnnotatedString) : VisualTransformation {
	override fun filter(text: AnnotatedString): TransformedText = TransformedText(
		text = annotatedString,
		offsetMapping = OffsetMapping.Identity,
	)
}