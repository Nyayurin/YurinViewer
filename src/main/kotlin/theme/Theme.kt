package cn.yurin.languege.viewer.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import yurinviewer.generated.resources.MapleMono_NF_CN_Regular
import yurinviewer.generated.resources.Res

@Composable
fun Theme(content: @Composable () -> Unit) {
	val defaultTypography = MaterialTheme.typography
	var typography by remember { mutableStateOf<Typography?>(null) }

	run {
		val fontFamily = FontFamily(
			Font(
				resource = Res.font.MapleMono_NF_CN_Regular,
				weight = FontWeight.Normal,
				style = FontStyle.Normal,
			)
		)
		typography = Typography(
			defaultTypography.displayLarge.copy(fontFamily = fontFamily),
			defaultTypography.displayMedium.copy(fontFamily = fontFamily),
			defaultTypography.displaySmall.copy(fontFamily = fontFamily),
			defaultTypography.headlineLarge.copy(fontFamily = fontFamily),
			defaultTypography.headlineMedium.copy(fontFamily = fontFamily),
			defaultTypography.headlineSmall.copy(fontFamily = fontFamily),
			defaultTypography.titleLarge.copy(fontFamily = fontFamily),
			defaultTypography.titleMedium.copy(fontFamily = fontFamily),
			defaultTypography.titleSmall.copy(fontFamily = fontFamily),
			defaultTypography.bodyLarge.copy(fontFamily = fontFamily),
			defaultTypography.bodyMedium.copy(fontFamily = fontFamily),
			defaultTypography.bodySmall.copy(fontFamily = fontFamily),
			defaultTypography.labelLarge.copy(fontFamily = fontFamily),
			defaultTypography.labelMedium.copy(fontFamily = fontFamily),
			defaultTypography.labelSmall.copy(fontFamily = fontFamily),
		)
	}
	MaterialTheme(
		colorScheme = darkColorScheme(),
		typography = typography ?: defaultTypography,
		content = content
	)
}