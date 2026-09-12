// Точка входа для ручного запуска валидатора из IDE (Run 'MainKt').
// В сборке проверку запускает задача :validateArchitecture (см. корневой build.gradle.kts).
fun main() {
    val files = scanProjectFiles() // или мок-данные

    val result = ArchGuard.validate(files)

    if (result.isSuccessful) {
        println("✅ Все проверки пройдены! (${files.size} файлов)")
    } else {
        println("❌ Найдено нарушений: ${result.totalViolations}\n")
        for (v in result.violations) {
            println("   📄 ${v.filePath}")
            println("   ❌ ${v.message}\n")
        }
    }
}