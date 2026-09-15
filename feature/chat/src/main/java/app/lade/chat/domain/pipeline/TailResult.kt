package app.lade.chat.domain.pipeline

import app.lade.chat.domain.ChatActual
import java.time.LocalDate
import java.time.LocalTime

data class TailResult(
	val date: LocalDate,
	val startTime: LocalTime? = null,
	val endTime: LocalTime? = null,
	val durationMin: Int? = null,
	val title: String,
	val actuals: List<ChatActual> = emptyList(),
)