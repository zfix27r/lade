object AppRules {
    
    val title = "Структура app"
    
    val descriptions = mapOf(
        "app_allowed_folders" to "В `app` разрешены только папки: `scenarios`, `di`, `navigation`"
    )
    
    fun check(file: SourceFile): List<Violation> {
        if (!file.path.startsWith("app/")) return emptyList()
        
        val violations = mutableListOf<Violation>()
        
        //violations.addAll(checkAllowedFolders(file))
        
        return violations
    }
    
    private fun checkAllowedFolders(file: SourceFile): List<Violation> {
        val allowedFolders = listOf("scenarios", "di", "navigation")
        val isInAllowedFolder = allowedFolders.any { file.path.startsWith("app/$it/") }
        
        return if (!isInAllowedFolder) {
            listOf(Violation(file.path, "Файл в запрещенной папке. Разрешены только: ${allowedFolders.joinToString(", ")}"))
        } else {
            emptyList()
        }
    }
}