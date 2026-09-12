object ModuleRules {
    
    val title = "Модули (feature/*)"
    
    val descriptions = mapOf(
        "module_allowed_app_imports" to "Модуль может импортировать из `app` только `app.scenarios`",
        "module_no_cross_import" to "Модули не импортируют другие модули"
    )
    
    fun check(file: SourceFile): List<Violation> {
        if (!file.path.startsWith("feature/")) return emptyList()
        
        val violations = mutableListOf<Violation>()
        
        violations.addAll(checkAllowedAppImports(file))
        violations.addAll(checkNoCrossImport(file))
        
        return violations
    }
    
    private fun checkAllowedAppImports(file: SourceFile): List<Violation> {
        val violations = mutableListOf<Violation>()
        
        for (line in file.content.lines()) {
            if (line.startsWith("import ") && line.contains(".app.")) {
                if (!line.contains("app.scenarios")) {
                    violations.add(Violation(file.path, "Модуль может импортировать из app только app.scenarios: $line"))
                }
            }
        }
        
        return violations
    }
    
    private fun checkNoCrossImport(file: SourceFile): List<Violation> {
        val violations = mutableListOf<Violation>()
        val currentModule = file.path.substringAfter("feature/").substringBefore("/")
        
        for (line in file.content.lines()) {
            if (line.startsWith("import ") && line.contains("feature.")) {
                val importedModule = line.substringAfter("feature.").substringBefore(".")
                if (currentModule != importedModule) {
                    violations.add(Violation(file.path, "Модуль '$currentModule' импортирует '$importedModule'"))
                }
            }
        }
        
        return violations
    }
}