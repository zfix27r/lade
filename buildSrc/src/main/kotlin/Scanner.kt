import java.io.File

// Сканирует исходный код проекта и возвращает файлы с нормализованными
// путями (всегда "/") — чтобы правила работали одинаково на Windows и Linux/macOS.
fun scanProjectFiles(): List<SourceFile> {
    val rootDir = File(System.getProperty("user.dir"))
    val scanRoots = listOf(
        "app/src/main/java",
        "feed/src/main/java",
        "database/src/main/java",
        "feature",
    )

    val files = mutableListOf<SourceFile>()

    for (scanPath in scanRoots) {
        val dir = File(rootDir, scanPath)
        if (!dir.isDirectory) continue

        dir.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { file ->
                val relativePath = file.relativeTo(dir).path.replace('\\', '/')
                files.add(SourceFile(relativePath, file.readText()))
            }
    }

    return files
}