# DayPlan

Опциональный бюджет долей суток без привязки к часам («хочу 8ч сна в сумме»).  
Не заменяет [TimeSchedule](model-time-schedule.md) / [TimeBlock](model-time-block.md): свободное считается по блокам.

| Поле | Тип | Примечание |
|------|-----|------------|
| id | integer | autoincrement |
| date | localDate | unique |
| notes | string? | |

Цели по категориям — DayPlanCategory:

| Поле | Тип | Примечание |
|------|-----|------------|
| id | integer | autoincrement (либо PK `(dayPlanId, categoryId)`) |
| dayPlanId | integer | FK → DayPlan |
| categoryId | integer | FK → Category |
| targetDuration | duration | сумма по дню ≤ 24ч |

