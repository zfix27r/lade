object ArchGuard {
    
    fun validate(files: List<SourceFile>): ValidationResult {
        val allViolations = mutableListOf<Violation>()
        
        for (file in files) {
            allViolations.addAll(ScenarioRules.check(file, files))
            allViolations.addAll(AppRules.check(file))
            allViolations.addAll(ModuleRules.check(file))
            allViolations.addAll(DatabaseRules.check(file))
        }
        
        return ValidationResult(allViolations)
    }
}