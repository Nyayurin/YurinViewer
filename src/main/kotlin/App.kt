package cn.yurin.languege.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import cn.yurin.languege.viewer.theme.Theme
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

@Composable
fun App() {
	val scope = rememberCoroutineScope()
	Theme {
		Surface(
			color = Color(0xFF282c34),
			modifier = Modifier.fillMaxSize(),
		) {
			Column {
				val layer = rememberGraphicsLayer()
				var dpSize by remember { mutableStateOf(IntSize.Zero) }
				val density = LocalDensity.current
				var highlightedCode by remember { mutableStateOf<AnnotatedString?>(null) }
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier
						.fillMaxWidth()
						.background(MaterialTheme.colorScheme.surfaceContainer)
						.padding(8.dp)
				) {
					Button(
						onClick = {
							scope.launch(Dispatchers.IO) {
								val file = FileKit.pickFile(
									type = PickerType.File(extensions = listOf("yurin")),
									mode = PickerMode.Single,
								)
								file?.let {
									val content = it.readBytes().toString(Charsets.UTF_8)
									highlightedCode = highlight(content)
								}
							}
						},
					) {
						Text("Select source file")
					}
					Button(
						onClick = {
							scope.launch(Dispatchers.IO) {
								val bitmap = layer.toImageBitmap()
								val byteArray = bitmap.toByteArray()
								FileKit.saveFile(
									bytes = byteArray,
									baseName = "output",
									extension = "png"
								)
							}
						},
					) {
						Text("Save to image")
					}
				}
				highlightedCode?.let { code ->
					SelectionContainer {
						Text(
							text = code,
							fontSize = 14.sp,
							lineHeight = 1.5.em,
							modifier = Modifier
								.verticalScroll(rememberScrollState())
								.horizontalScroll(rememberScrollState())
								.onSizeChanged {
									dpSize = with(density) {
										IntSize(
											width = it.width.toDp().value.toInt(),
											height = it.height.toDp().value.toInt()
										)
									}
								}
								.drawToLayer(layer, dpSize)
								.background(Color(0xFF282c34))
								.padding(16.dp),
						)
					}
				}
			}
		}
	}
}

fun Modifier.drawToLayer(layer: GraphicsLayer, size: IntSize) = this.drawWithContent {
	val rawSize = this.size
	layer.record(size) {
		val scaleX = size.width.toFloat() / rawSize.width
		val scaleY = size.height.toFloat() / rawSize.height
		withTransform({ scale(scaleX, scaleY, Offset(0F, 0F)) }) {
			this@drawWithContent.drawContent()
		}
	}
	drawLayer(layer)
	drawContent()
}

fun ImageBitmap.toByteArray(): ByteArray {
	ByteArrayOutputStream().use { stream ->
		ImageIO.write(toAwtImage(), "png", stream)
		stream.flush()
		return stream.toByteArray()
	}
}