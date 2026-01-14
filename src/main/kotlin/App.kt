package cn.yurin.languege.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import cn.yurin.languege.viewer.theme.Theme
import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import io.github.vinceglb.filekit.core.PlatformFile
import io.github.vinceglb.filekit.core.baseName
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
				var code by remember { mutableStateOf("") }
				var file by remember { mutableStateOf<PlatformFile?>(null) }
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
								val selectFile = FileKit.pickFile(
									type = PickerType.File(extensions = listOf("yurin")),
									mode = PickerMode.Single,
								)
								selectFile?.let {
									file = it
									code = it.readBytes().toString(Charsets.UTF_8)
								}
							}
						},
					) {
						Text("Load")
					}
					Button(
						onClick = {
							scope.launch(Dispatchers.IO) {
								file?.file?.writeText(code)
							}
						},
					) {
						Text("Save")
					}
					Button(
						onClick = {
							scope.launch(Dispatchers.IO) {
								FileKit.saveFile(
									bytes = code.toByteArray(Charsets.UTF_8),
									baseName = file?.baseName ?: "",
									extension = "yurin"
								)?.let {
									file = it
								}
							}
						},
					) {
						Text("Save as")
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
						Text("Print to image")
					}
				}
				BasicTextField(
					value = code,
					onValueChange = { code = it },
					textStyle = LocalTextStyle.current.merge(
						color = Color(0xFFABB2BF),
						fontSize = 14.sp,
						lineHeight = 1.5.em,
					),
					visualTransformation = HighlightingTransformation,
					onTextLayout = { result ->
						val lineCount = result.lineCount
						val bottom = result.getLineBottom(lineCount - 1).toInt()
						var end = 0
						for (i in 0 until lineCount) {
							result.getLineRight(i).let {
								if (it > end) end = it.toInt()
							}
						}
						val padding = with(density) {
							32.dp.toPx().toInt()
						}
						dpSize = IntSize(
							width = end + padding,
							height = bottom + padding,
						)
					},
					cursorBrush = SolidColor(Color.White),
					modifier = Modifier
						.fillMaxSize()
						.verticalScroll(rememberScrollState())
						.horizontalScroll(rememberScrollState())
						.drawToLayer(layer, dpSize)
						.background(Color(0xFF282c34))
						.padding(16.dp),
				)
			}
		}
	}
}

fun Modifier.drawToLayer(layer: GraphicsLayer, size: IntSize) = this.drawWithContent {
	layer.record(size) {
		this@drawWithContent.drawContent()
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