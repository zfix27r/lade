package app.lade.chat.api

data class ParserGoalModel(
    val amount: String? = ParserContract.skip(),
    val unit: String? = ParserContract.skip(),
    val title: String? = ParserContract.skip(),
    val repeats: String? = ParserContract.skip(),
    val weight: String? = ParserContract.skip(),
)