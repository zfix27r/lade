# TimeBlock

Конкретный интервал в одних сутках. Нужен, чтобы видеть занятость и остаток свободного.

Источники: развёртка [TimeSchedule](model-time-schedule.md), manual, позже health/calendar.

| Поле | Тип | Примечание |
|------|-----|------------|
| id | integer | autoincrement |
| date | localDate | |
| start | localDateTime | |
| end | localDateTime | end > start |
| categoryId | integer | FK → Category |
| title | string? | |
| source | enum | `schedule` \| `manual` \| `health` \| `calendar` |
| scheduleId | integer? | FK → TimeSchedule |
| habitHistoryId | integer? | опциональная связь с отметкой привычки |
| healthSampleId | integer? | после ядра |
| calendarEventId | string? | внешний id |
| locked | bool | импорт: ограничить правки |

Правила — [rules.md](rules.md).

Не заменяет слепок привычки: привычка без часов живёт в своём контуре.
