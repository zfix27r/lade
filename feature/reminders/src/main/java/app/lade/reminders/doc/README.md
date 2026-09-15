# `reminders`

## Tasks

Текущая волна: [tasks-v2.md](tasks-v2.md)  
Архив: [tasks-v1.md](tasks-v1.md)

## Части

| Часть | Назначение |
|-------|------------|
| channels / permissions | Каналы soft/alarm + POST_NOTIFICATIONS / exact |
| ReminderAlarmScheduler | AlarmManager schedule/cancel |
| ReminderRescheduler | Якоря из Entry (+ EntryDayProjector), горизонт 14 дней |
| DayPartPreferences | Глобальные утро/обед/вечер (SharedPreferences) |
| ui | DayPartSettingsScreen; chips через temporal |

## Поведение

| Режим | Смысл |
|-------|--------|
| notification | Мягкий push, канал soft |
| alarm | Жёсткий exact / high-importance канал |

Напоминания только из **Entry** (не History). Habit — по `alarmMode` + время; schedule/event — soft на `startTime`.

Prefs daypart живут здесь; `temporal.DayPart` / `DayPartChipRow` — только подписи и UI.

## Документы

| Файл | О чём |
|------|--------|
| [tasks-v2.md](tasks-v2.md) | Волна 2 — reminders от Entry |
| [tasks-v1.md](tasks-v1.md) | Волна 1 |
