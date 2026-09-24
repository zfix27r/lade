package app.lade.humanize.api

data class Humanized(
    val short: String,
    val long: String,
) {
    val best: String get() = short.ifBlank { long }
}