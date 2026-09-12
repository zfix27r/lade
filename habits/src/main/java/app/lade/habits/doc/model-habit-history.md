# HabitHistory

Дневной слепок прогресса. Редактируемый; не зависит от текущего [Habit](model-habit.md).

Пример: день пн — бег, цель 2 км (слепок), result=done, actualValue=2.1.

| Поле | Тип | Примечание |
|------|-----|------------|
| id | integer | autoincrement |
| habitId | integer | FK → Habit |
| date | localDate | |
| title | string | слепок |
| categoryId | integer | слепок |
| goalType | enum | слепок |
| goalValue | decimal | слепок |
| goalUnit | string | слепок |
| timeOfDay | localTime? | слепок якоря, если был |
| result | enum? | `done` \| `skipped`; `null` — без отметки |
| actualValue | decimal? | факт к цели |
| notedAt | instant? | |
| source | enum? | `manual` \| `health` \| `inferred` |
| timeBlockId | integer? | опционально, если пользователь связал с блоком занятости |

Инвариант: уникальность `(habitId, date)`.

Поведение — [rules.md](rules.md).
