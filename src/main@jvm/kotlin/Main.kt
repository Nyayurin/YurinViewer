import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cn.yurin.languege.viewer.App

fun main() = application {
	Window(
		title = "Yurin Viewer",
		state = rememberWindowState(),
		onCloseRequest = ::exitApplication,
	) {
		App()
	}
}
