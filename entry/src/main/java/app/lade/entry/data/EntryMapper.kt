package app.lade.entry.data

import app.lade.database.entry.EntryEntity
import app.lade.database.entry.EntryHistoryEntity
import app.lade.entry.domain.models.Entry
import app.lade.entry.domain.models.EntryActual
import app.lade.entry.domain.models.EntryGoalDef
import app.lade.entry.domain.models.EntryHistory
import app.lade.entry.domain.models.EntryKind
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalTime

fun EntryEntity.toDomain() = Entry(
	id = id,
	kind = EntryKind.fromStorage(kind),
	title = title,
	categoryId = categoryId,
	dateFrom = dateFromEpochDay?.let(LocalDate::ofEpochDay),
	dateTo = dateToEpochDay?.let(LocalDate::ofEpochDay),
	startTime = startTimeMinutes?.let { LocalTime.ofSecondOfDay(it * 60L) },
	endTime = endTimeMinutes?.let { LocalTime.ofSecondOfDay(it * 60L) },
	rrule = rrule,
	goalDefs = parseGoalDefs(goalDefsJson),
	alarmMode = alarmMode,
	reminderMinutesBefore = reminderMinutesBefore,
	archivedAtEpochMs = archivedAtEpochMs,
	pausedAtEpochMs = pausedAtEpochMs,
	createdAtEpochMs = createdAtEpochMs,
)

fun Entry.toEntity() = EntryEntity(
	id = id,
	kind = kind.storage,
	title = title,
	categoryId = categoryId,
	dateFromEpochDay = dateFrom?.toEpochDay(),
	dateToEpochDay = dateTo?.toEpochDay(),
	startTimeMinutes = startTime?.toMinuteOfDay(),
	endTimeMinutes = endTime?.toMinuteOfDay(),
	rrule = rrule,
	goalDefsJson = goalDefsToJson(goalDefs),
	alarmMode = alarmMode,
	reminderMinutesBefore = reminderMinutesBefore,
	archivedAtEpochMs = archivedAtEpochMs,
	pausedAtEpochMs = pausedAtEpochMs,
	createdAtEpochMs = createdAtEpochMs,
)

fun EntryHistoryEntity.toDomain() = EntryHistory(
	id = id,
	entryId = entryId,
	kind = EntryKind.fromStorage(kind),
	title = title,
	categoryId = categoryId,
	date = LocalDate.ofEpochDay(dateEpochDay),
	startTime = startTimeMinutes?.let { LocalTime.ofSecondOfDay(it * 60L) },
	endTime = endTimeMinutes?.let { LocalTime.ofSecondOfDay(it * 60L) },
	result = result,
	goals = parseGoalDefs(goalsJson),
	actuals = parseActuals(actualsJson),
	notedAtEpochMs = notedAtEpochMs,
	source = source,
)

fun EntryHistory.toEntity() = EntryHistoryEntity(
	id = id,
	entryId = entryId,
	kind = kind.storage,
	title = title,
	categoryId = categoryId,
	dateEpochDay = date.toEpochDay(),
	startTimeMinutes = startTime?.toMinuteOfDay(),
	endTimeMinutes = endTime?.toMinuteOfDay(),
	result = result,
	goalsJson = goalDefsToJson(goals),
	actualsJson = actualsToJson(actuals),
	notedAtEpochMs = notedAtEpochMs,
	source = source,
)

private fun LocalTime.toMinuteOfDay(): Int = hour * 60 + minute

private fun parseGoalDefs(json: String): List<EntryGoalDef> = runCatching {
	val arr = JSONArray(json)
	(0 until arr.length()).map { i ->
		val o = arr.getJSONObject(i)
		EntryGoalDef(
			key = o.getString("key"),
			label = o.optString("label").takeIf { it.isNotEmpty() },
			unit = o.optString("unit").takeIf { it.isNotEmpty() },
			target = if (o.has("target") && !o.isNull("target")) o.getDouble("target") else null,
		)
	}
}.getOrDefault(emptyList())

private fun goalDefsToJson(defs: List<EntryGoalDef>): String {
	val arr = JSONArray()
	defs.forEach { def ->
		arr.put(
			JSONObject().apply {
				put("key", def.key)
				def.label?.let { put("label", it) }
				def.unit?.let { put("unit", it) }
				def.target?.let { put("target", it) }
			},
		)
	}
	return arr.toString()
}

private fun parseActuals(json: String): List<EntryActual> = runCatching {
	val arr = JSONArray(json)
	(0 until arr.length()).map { i ->
		val o = arr.getJSONObject(i)
		EntryActual(
			key = o.getString("key"),
			value = if (o.has("value") && !o.isNull("value")) o.getDouble("value") else null,
			unit = o.optString("unit").takeIf { it.isNotEmpty() },
		)
	}
}.getOrDefault(emptyList())

private fun actualsToJson(actuals: List<EntryActual>): String {
	val arr = JSONArray()
	actuals.forEach { a ->
		arr.put(
			JSONObject().apply {
				put("key", a.key)
				a.value?.let { put("value", it) }
				a.unit?.let { put("unit", it) }
			},
		)
	}
	return arr.toString()
}
