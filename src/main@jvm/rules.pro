-dontobfuscate
-dontoptimize

-keep class androidx.compose.** { *; }

-dontwarn androidx.compose.desktop.DesktopTheme_jvmKt

-dontnote org.freedesktop.dbus.**