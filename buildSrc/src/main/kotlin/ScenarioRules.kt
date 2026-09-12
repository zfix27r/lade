object ScenarioRules {
    
    val title = "Сценарии (app/scenarios)"
    
    val descriptions = mapOf(
        "scenario_is_object" to "Сценарий — это `object` (не `class`)",
        "scenario_methods_named" to "Публичные методы: `step1`...`step6`, `stepResult`",
        "stepResult_exists" to "Метод `stepResult` обязателен",
        "scenario_methods_one_param" to "Каждый метод принимает ровно 1 параметр",
        "scenario_methods_return_unit" to "Все методы возвращают `Unit`",
        "scenario_methods_one_line" to "Тело метода — одна строка (вызов приватного метода)",
        "scenario_models_exist" to "Для каждого `stepX` есть модель `StepXModel.kt` в той же папке",
        "scenario_max_methods" to "Не более 7 методов (включая `stepResult`)"
    )
    
    fun check(file: SourceFile, allFiles: List<SourceFile>): List<Violation> {
        if (!file.path.matches(Regex("""app/scenarios/.*Scenario\.kt"""))) {
            return emptyList()
        }
        
        val violations = mutableListOf<Violation>()
        
        violations.addAll(checkIsObject(file))
        violations.addAll(checkMethodsNamed(file))
        violations.addAll(checkStepResultExists(file))
        violations.addAll(checkMethodsOneParam(file))
        violations.addAll(checkMethodsReturnUnit(file))
        violations.addAll(checkMethodsOneLine(file))
        violations.addAll(checkModelsExist(file, allFiles))
        violations.addAll(checkMaxMethods(file))
        
        return violations
    }
    
    private fun checkIsObject(file: SourceFile): List<Violation> {
        val pattern = Regex("""class\s+\w+Scenario""")
        return if (pattern.containsMatchIn(file.content)) {
            listOf(Violation(file.path, "Сценарий должен быть object, а не class"))
        } else emptyList()
    }
    
    private fun checkMethodsNamed(file: SourceFile): List<Violation> {
        val expected = listOf("step1", "step2", "step3", "step4", "step5", "step6", "stepResult")
        val actual = Regex("""fun\s+(step\d+|stepResult)\s*\(""").findAll(file.content)
            .map { it.groupValues[1] }
            .toList()
        
        val missing = expected.filter { it !in actual }
        return if (missing.isNotEmpty()) {
            listOf(Violation(file.path, "Отсутствуют методы: ${missing.joinToString(", ")}"))
        } else emptyList()
    }
    
    private fun checkStepResultExists(file: SourceFile): List<Violation> {
        return if (!Regex("""fun\s+stepResult\s*\(""").containsMatchIn(file.content)) {
            listOf(Violation(file.path, "Обязательный метод stepResult отсутствует"))
        } else emptyList()
    }
    
    private fun checkMethodsOneParam(file: SourceFile): List<Violation> {
        val violations = mutableListOf<Violation>()
        val pattern = Regex("""fun\s+(step\d+|stepResult)\s*\(([^)]*)\)""")
        for (match in pattern.findAll(file.content)) {
            val params = match.groupValues[2].trim()
            val count = if (params.isEmpty()) 0 else params.split(",").size
            if (count != 1) {
                violations.add(Violation(file.path, "Метод '${match.groupValues[1]}' принимает $count параметров (должен быть 1)"))
            }
        }
        return violations
    }
    
    private fun checkMethodsReturnUnit(file: SourceFile): List<Violation> {
        val violations = mutableListOf<Violation>()
        val pattern = Regex("""fun\s+(step\d+|stepResult)\s*\([^)]*\)\s*:\s*(\w+)""")
        for (match in pattern.findAll(file.content)) {
            val returnType = match.groupValues[2]
            if (returnType != "Unit") {
                violations.add(Violation(file.path, "Метод '${match.groupValues[1]}' возвращает '$returnType' (должен быть Unit)"))
            }
        }
        return violations
    }
    
    private fun checkMethodsOneLine(file: SourceFile): List<Violation> {
        val violations = mutableListOf<Violation>()
        val pattern = Regex("""fun\s+(step\d+|stepResult)\s*\([^)]*\)\s*\{([^}]*)\}""")
        for (match in pattern.findAll(file.content)) {
            val body = match.groupValues[2].trim()
            val lines = body.split("\n").filter { it.isNotBlank() }
            if (lines.size != 1) {
                violations.add(Violation(file.path, "Метод '${match.groupValues[1]}' содержит ${lines.size} строк (должна быть 1)"))
            }
        }
        return violations
    }
    
    private fun checkModelsExist(file: SourceFile, allFiles: List<SourceFile>): List<Violation> {
        val violations = mutableListOf<Violation>()
        val dir = file.path.substringBeforeLast("/")
        val pattern = Regex("""fun\s+(step\d+|stepResult)\s*\((\w+)\s*:\s*(\w+)\)""")
        
        for (match in pattern.findAll(file.content)) {
            val modelName = match.groupValues[3]
            val expectedPath = "$dir/${modelName}.kt"
            if (allFiles.none { it.path == expectedPath }) {
                violations.add(Violation(file.path, "Модель '$modelName' не найдена в $dir"))
            }
        }
        return violations
    }
    
    private fun checkMaxMethods(file: SourceFile): List<Violation> {
        val count = Regex("""fun\s+(step\d+|stepResult)\s*\(""").findAll(file.content).count()
        return if (count > 7) {
            listOf(Violation(file.path, "Сценарий содержит $count методов (максимум 7)"))
        } else emptyList()
    }
}