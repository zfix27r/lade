package app.lade.parser.api

data class ParserModel(
    val raw: String,
    val entry: ParserEntryModel? = null,
    val goals: List<ParserGoalModel>? = null,
    val reminders: List<ParserReminderModel>? = null,
    val alarms: List<ParserAlarmModel>? = null,
)