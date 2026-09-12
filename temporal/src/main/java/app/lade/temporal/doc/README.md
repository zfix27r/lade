# `temporal`

## Tasks

Текущая волна: [tasks-v2.md](tasks-v2.md)  
Архив: [tasks-v1.md](tasks-v1.md)

## Части

| Часть | Назначение |
|-------|------------|
| domain | ScheduleEngine, BiweeklyScheduleEngine, RecurrenceDraft, DaysOfWeekFlags, DayPart / DayPartClock, Hilt |
| ui | TemporalOptionsPanel (повтор свёрнут → sheet), DateRange / TimeRange / DaysOfWeek, DayPartChipRow |

## Термины

| Термин | eng | Смысл |
|--------|-----|--------|
| Часть суток | `DayPart` | Утро / обед / вечер; дефолт-часы 09/12/18 |
| Часы частей | `DayPartClock` | Разрешённые времена; prefs — снаружи (`reminders`) |
