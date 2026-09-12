import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	alias(libs.plugins.android.application) apply false
	alias(libs.plugins.android.library) apply false
	alias(libs.plugins.kotlin.compose) apply false
	alias(libs.plugins.ksp) apply false
	alias(libs.plugins.hilt.android) apply false
}

subprojects {
	tasks.withType<KotlinCompile>().configureEach {
		compilerOptions {
			freeCompilerArgs.add("-Xannotation-default-target=param-property")
		}
	}
}

// ============================================================
// ArchGuard — архитектурная проверка проекта
// (реализация — buildSrc/src/main/kotlin/*.kt)
// ============================================================
tasks.register("validateArchitecture") {
	group = "verification"
	description = "Проверяет архитектуру проекта (ArchGuard)"
	doLast {
		val files = scanProjectFiles()
		val result = ArchGuard.validate(files)
		if (result.isSuccessful) {
			println("✅ ArchGuard: все проверки пройдены (${files.size} файлов)!")
		} else {
			println("❌ ArchGuard: найдено нарушений: ${result.totalViolations}\n")
			for (v in result.violations) {
				println("   📄 ${v.filePath}")
				println("   ❌ ${v.message}\n")
			}
			throw GradleException("Архитектурные нарушения: ${result.totalViolations}")
		}
	}
}
