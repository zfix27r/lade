# TimeSchedule

Шаблон повторяющейся занятости на диапазоне дат.  
Пример: «Работа» 8 фев–10 мар, 08:00–12:00 и 14:00–18:00; отдельно «Обед» 12:00–14:00.

Не смешивать с привычками (ритм + цель без обязательных часов).

| Поле | Тип | Примечание |
|------|-----|------------|
| id | integer | autoincrement |
| title | string | |
| categoryId | integer | FK → Category |
| dateFrom | localDate | начало периода (включ.) |
| dateTo | localDate | конец периода (включ.) |
| rrule | string | частота внутри периода |
| intervals | list<{start, end}> | без пересечений; в первом срезе — одна пара start/end |
| archivedAt | instant? | |

## Развёртка

На каждую дату ∈ [dateFrom, dateTo], если due по RRULE, создаются/синхронизируются [TimeBlock](model-time-block.md) с `scheduleId`, `source = schedule`.

Правка шаблона: обновлять будущие блоки; прошлые по умолчанию не трогать.

Разовые записи — сразу TimeBlock без `scheduleId`.
