# TimeSchedule

Шаблон повторяющейся занятости на диапазоне дат.  
Пример: «Работа» с 8 фев, 08:00–12:00; конец периода опционален.

Не смешивать с привычками (ритм + цель без обязательных часов).

| Поле | Тип | Примечание |
|------|-----|------------|
| id | integer | autoincrement |
| title | string | |
| categoryId | integer | FK → Category |
| dateFrom | localDate | начало периода (включ.) |
| dateTo | localDate? | конец (включ.); `null` = бессрочно |
| rrule | string | частота внутри периода |
| intervals | list<{start, end}> | без пересечений; в первом срезе — одна пара start/end |
| archivedAt | instant? | |

## Развёртка

На каждую дату ≥ max(today, dateFrom) и ≤ dateTo (или горизонт ~370 дней, если `dateTo` null), если due по RRULE, создаются/синхронизируются [TimeBlock](model-time-block.md) с `scheduleId`, `source = schedule`.

Правка шаблона: обновлять будущие блоки; прошлые по умолчанию не трогать.

Разовые записи — сразу TimeBlock без `scheduleId`.
