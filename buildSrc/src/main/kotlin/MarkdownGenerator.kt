fun generateMarkdown(ruleSets: List<Pair<String, Map<String, String>>>): String {
    val sb = StringBuilder()
    sb.appendLine("# Архитектурные правила ArchGuard\n")
    
    for ((title, descriptions) in ruleSets) {
        sb.appendLine("## $title")
        sb.appendLine("| ID | Правило |")
        sb.appendLine("|---|---|")
        for ((id, desc) in descriptions) {
            sb.appendLine("| `$id` | $desc |")
        }
        sb.appendLine()
    }
    
    return sb.toString()
}