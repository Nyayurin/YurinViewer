@file:OptIn(ExperimentalWasmDsl::class)

import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
	kotlin("multiplatform") version "2.2.10"
	id("org.jetbrains.kotlin.plugin.compose") version "2.2.10"
	id("org.jetbrains.compose") version "1.8.2"
	id("com.strumenta.antlr-kotlin") version "1.0.9"
}

repositories {
	google()
	mavenCentral()
}

kotlin {
	jvmToolchain(21)

	jvm()

	sourceSets {
		commonMain {
			kotlin.setSrcDirs(
				listOf(
					"src/main/kotlin",
					"build/generated/compose/resourceGenerator/kotlin/commonResClass",
					"build/generated/compose/resourceGenerator/kotlin/commonMainResourceCollectors",
					"build/generated/compose/resourceGenerator/kotlin/commonMainResourceAccessors",
					"build/generatedAntlr/com/yurin/antlrkotlin/parsers/generated",
				)
			)
			resources.setSrcDirs(listOf("src/main/resources"))
		}

		jvmMain {
			kotlin.setSrcDirs(
				listOf(
					"src/main@jvm/kotlin",
					"build/generated/compose/resourceGenerator/kotlin/jvmMainResourceCollectors",
				)
			)
			resources.setSrcDirs(
				listOf(
					"src/main@jvm/resources",
					"build/generated/compose/resourceGenerator/assembledResources/jvmMain",
				)
			)
		}

		commonMain.dependencies {
			implementation(compose.runtime)
			implementation(compose.ui)
			implementation(compose.foundation)
			implementation(compose.material3)
			implementation(compose.components.resources)

			implementation("io.github.vinceglb:filekit-compose:0.8.8")
			implementation("dev.snipme:highlights:1.1.0")
			implementation ("com.strumenta:antlr-kotlin-runtime:1.0.9")
		}

		jvmMain.dependencies {
			implementation(compose.desktop.currentOs) {
				exclude("org.jetbrains.compose.material", "material")
			}
		}
	}
}

val generateKotlinGrammarSource = tasks.register<AntlrKotlinTask>("generateKotlinGrammarSource") {
	dependsOn("cleanGenerateKotlinGrammarSource")

	// ANTLR .g4 files are under {example-project}/antlr
	// Only include *.g4 files. This allows tools (e.g., IDE plugins)
	// to generate temporary files inside the base path
	source = fileTree(layout.projectDirectory.dir("antlr")) {
		include("**/*.g4")
	}

	// We want the generated source files to have this package name
	val pkgName = "com.yurin.antlrkotlin.parsers.generated"
	packageName = pkgName

	// We want visitors alongside listeners.
	// The Kotlin target language is implicit, as is the file encoding (UTF-8)
	arguments = listOf("-visitor")

	// Generated files are outputted inside build/generatedAntlr/{package-name}
	val outDir = "generatedAntlr/${pkgName.replace(".", "/")}"
	outputDirectory = layout.buildDirectory.dir(outDir).get().asFile
}

compose.desktop {
	application {
		mainClass = "MainKt"

		nativeDistributions {
			val os = System.getProperty("os.name")
			when {
				os.contains("Windows") -> targetFormats(TargetFormat.Msi, TargetFormat.Exe, TargetFormat.AppImage)
				os.contains("Linux") -> targetFormats(TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.AppImage)
				os.contains("Mac OS") -> targetFormats(TargetFormat.Dmg, TargetFormat.Pkg)
				else -> error("Unsupported OS: $os")
			}
			packageName = "Yurin Viewer"
			packageVersion = System.getenv("VERSION")
			jvmArgs("-Dfile.encoding=UTF-8")

			linux {
				modules("jdk.security.auth")
			}
		}

		buildTypes.release.proguard {
			configurationFiles.from(project.file("src/main@jvm/rules.pro"))
		}
	}
}

compose.resources {
	customDirectory(
		sourceSetName = "commonMain",
		directoryProvider = provider { layout.projectDirectory.dir("src/main/composeResources") }
	)
}