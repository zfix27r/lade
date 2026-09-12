object DatabaseRules {

    val title = "База данных (:core:database)"

    val descriptions = mapOf(
        "database_no_android_imports" to "Модуль БД не должен импортировать Android-специфичные классы (кроме Room)",
        "database_no_feature_imports" to "Модуль БД не должен импортировать фичи (feature/*)"
    )

    fun check(file: SourceFile): List<Violation> {
        if (!file.path.startsWith("core/database/")) return emptyList()

        val violations = mutableListOf<Violation>()

        violations.addAll(checkNoAndroidImports(file))
        violations.addAll(checkNoFeatureImports(file))

        return violations
    }

    private fun checkNoAndroidImports(file: SourceFile): List<Violation> {
        val violations = mutableListOf<Violation>()
        val allowedAndroidImports = listOf(
            "androidx.room",
            "androidx.sqlite"
        )

        for (line in file.content.lines()) {
            if (line.startsWith("import ") && line.contains("android.")) {
                val isAllowed = allowedAndroidImports.any { line.contains(it) }
                if (!isAllowed) {
                    violations.add(Violation(file.path, "Модуль БД не должен импортировать Android-классы (кроме Room): $line"))
                }
            }
        }

        return violations
    }

    private fun checkNoFeatureImports(file: SourceFile): List<Violation> {
        val violations = mutableListOf<Violation>()

        for (line in file.content.lines()) {
            if (line.startsWith("import ") && line.contains("feature.")) {
                violations.add(Violation(file.path, "Модуль БД не должен импортировать фичи: $line"))
            }
        }

        return violations
    }
}