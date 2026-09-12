data class ValidationResult(
    val violations: List<Violation>
) {
    val totalViolations: Int get() = violations.size
    val isSuccessful: Boolean get() = violations.isEmpty()
}