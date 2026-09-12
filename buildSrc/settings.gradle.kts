// buildSrc — это отдельный встроенный билд со своими настройками репозиториев.
// Без этого файла плагины/зависимости для ArchGuard.kt ищутся только на
// gradlePluginPortal и не резолвятся в вашем окружении.
pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositories {
		google()
		mavenCentral()
	}
}