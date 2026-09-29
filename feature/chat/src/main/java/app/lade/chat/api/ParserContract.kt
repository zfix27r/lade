package app.lade.chat.api

object ParserContract {

    fun skip(): String? = null

    fun find(): String = ""

    fun found(value: String): String = value

    fun isSkip(value: String?): Boolean = value == null

    fun isFind(value: String?): Boolean = value != null && value.isEmpty()

    fun isFound(value: String?): Boolean = !value.isNullOrEmpty()
}