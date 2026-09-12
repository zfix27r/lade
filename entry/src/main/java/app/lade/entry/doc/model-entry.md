# Entry

Настройки / конфиг активной записи. Не факт в календаре.

`kind`: `task` | `event` | `habit` | `schedule` — в UI всегда явные типы.

| Поле | Тип | Примечание |
|------|-----|------------|
| id | integer | autoincrement |
| kind | enum | task / event / habit / schedule |
| title | string | |
| categoryId | integer | FK → Category |
| dateFrom | localDate? | окно / якорь |
| dateTo | localDate? | конец окна; null = открыто |
| dateMode | enum? | single / range / explicitDates / byWeekday (UI-опции дат) |
| dates | list\<localDate\>? | для explicitDates |
| weekdays | list? | для byWeekday |
| startTime | localTime? | внутри суток; несколько кусков дня = несколько Entry |
| endTime | localTime? | |
| rrule | string? | повтор; пресеты → RRULE |
| goalDefs | list? | шаблон целей/метрик (habit); не снимок |
| alarmMode | enum | none / notification / alarm |
| reminderMinutesBefore | int? | |
| archivedAt | instant? | устарела / архив; History не каскадить |
| pausedAt | instant? | опционально |
| createdAt | instant | |

## Create: обязательность по kind

Матрица UI (блоки: название | даты-время | повтор):

| Kind | Обязательно | Опционально |
|------|-------------|-------------|
| task | title, даты (или due) | время, повтор |
| event | title, даты, время | повтор |
| habit | title, повтор | даты, время; goalDefs |
| schedule | title, время, повтор | даты периода |

Правила create: сильные дефолты; дыры — короткий уточняющий шаг.

## Правила продукта

1. Грядущие дни **серии** — только проекция Entry для UI; в History не пишем заранее.
2. **Расписание** в History не храним; календарь занятости из проекции Entry.
3. **Одиночная важнее серии** на пересекающийся слот (маска проекции).
4. Несколько интервалов в дне = несколько Entry (работа / обед отдельно).
5. Частичные наложения ok; полное накрытие → диалог (удалить накрытую / разрезать накрывающую).
6. Приоритет слоёв kind (настраиваемый), дефолт: schedule < habit < task < event.
7. Напоминания считаются из Entry, не из History.
8. После cutover пакеты habits и time удаляются полностью.

Через полночь, calsync/health provenance — backlog.
