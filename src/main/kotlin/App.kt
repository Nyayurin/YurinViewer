package cn.yurin.languege.viewer

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import cn.yurin.languege.viewer.theme.Theme
import io.github.vinceglb.filekit.core.*
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
				var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
				var source by remember { mutableStateOf("") }
				var file by remember { mutableStateOf<PlatformFile?>(null) }
				var highlights by remember { mutableStateOf(emptyList<Highlight>()) }
				var errors by remember { mutableStateOf(emptyList<ErrorHighlightListener.Error>()) }
				var annotatedSource by remember { mutableStateOf(AnnotatedString("")) }
				remember(source) {
					val (tempHighlights, tempErrors) = highlight(source)
					highlights = tempHighlights
					errors = tempErrors
					annotatedSource = highlights.toAnnotatedString(source)
				}
				Row(
					horizontalArrangement = Arrangement.spacedBy(8.dp),
					modifier = Modifier
						.fillMaxWidth()
						.background(MaterialTheme.colorScheme.surfaceContainer)
						.padding(8.dp),
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
									source = it.readBytes().toString(Charsets.UTF_8)
								}
							}
						},
					) {
						Text("Load")
					}
					Button(
						onClick = {
							scope.launch(Dispatchers.IO) {
								file?.file?.writeText(source)
									?: FileKit.saveFile(
										bytes = source.toByteArray(Charsets.UTF_8),
										baseName = file?.baseName ?: "",
										extension = "yurin",
									)?.let {
										file = it
									}
							}
						},
					) {
						Text("Save")
					}
					Button(
						onClick = {
							scope.launch(Dispatchers.IO) {
								FileKit.saveFile(
									bytes = source.toByteArray(Charsets.UTF_8),
									baseName = file?.baseName ?: "",
									extension = "yurin",
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
									extension = "png",
								)
							}
						},
					) {
						Text("Print to image")
					}
				}
				BasicTextField(
					value = source,
					onValueChange = { source = it },
					textStyle = LocalTextStyle.current.merge(
						color = Color(0xFFABB2BF),
						fontSize = 14.sp,
						lineHeight = 1.5.em,
					),
					visualTransformation = HighlightingTransformation(annotatedSource),
					onTextLayout = { result ->
						layoutResult = result
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
						.weight(1F)
						.verticalScroll(rememberScrollState())
						.horizontalScroll(rememberScrollState())
						.drawToLayer(layer, dpSize)
						.background(Color(0xFF282c34))
						.padding(16.dp)
						.drawBehind {
							layoutResult?.let { layoutResult ->
								val textLength = layoutResult.layoutInput.text.length
								highlights.filter { it.style.underlineColor != null }.forEach { (start, end, style) ->
									val safeStart = start.coerceIn(0, textLength - 1)
									val safeEnd = (end - 2).coerceIn(safeStart, textLength - 1)

									val startRect = layoutResult.getBoundingBox(safeStart)
									val endRect = layoutResult.getBoundingBox(safeEnd)
									val startCharLength = startRect.right - startRect.left
									val endCharLength = endRect.right - endRect.left
									val startOffset = start - safeStart
									val endOffset = end - safeEnd - 1

									val y = startRect.bottom + 2.dp.toPx()

									drawLine(
										color = Color(style.underlineColor!!),
										start = Offset(startRect.left + startCharLength * startOffset, y),
										end = Offset(endRect.right + endCharLength * endOffset, y),
										strokeWidth = 1.dp.toPx(),
									)
								}
							}
						},
				)
				AnimatedContent(errors.size) {
					LazyColumn(
						modifier = Modifier
							.fillMaxWidth()
							.heightIn(max = 200.dp)
							.background(Color(0xFF21252B)),
					) {
						items(errors) { error ->
							Text(
								text = "${error.line}:${error.charPositionInLine}: ${error.msg}",
								color = Color.Red,
								modifier = Modifier.padding(start = 16.dp, bottom = 4.dp),
							)
						}
					}
				}
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