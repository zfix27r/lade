# Habit fields

Нормализованные поля привычки (не JSON). Определения на Habit; значения дня — слепок на History.

## HabitFieldDef

Текущие настройки поля у привычки. Mutable.

| Поле | Тип | Примечание |
|------|-----|------------|
| id | integer | autoincrement |
| habitId | integer | FK → Habit, CASCADE |
| fieldKey | string | стабильный ключ: `distance`, `pace`, `cadence`, `custom_…` |
| label | string? | подпись; null → UI по preset/key |
| role | enum | `goal` \| `metric` |
| unit | string? | `km`, `min`, … |
| sortOrder | integer | порядок в UI |
| archivedAt | instant? | |

Инвариант: unique `(habitId, fieldKey)`.

Роль `goal` влияет на done/adherence; `metric` — только факт/чат/аналитика.

Пресеты ключей: `distance`, `duration`, `count`, `pace`, `cadence`, `custom_*`.

При отметке дня (`MarkHabitDay`) defs копируются в `HabitHistoryFieldValue`; для `goal` пишется `actualValue`.

## HabitHistoryFieldValue

Слепок значения поля за день. Не зависит от текущего HabitFieldDef.

| Поле | Тип | Примечание |
|------|-----|------------|
| id | integer | autoincrement |
| historyId | integer | FK → HabitHistory, CASCADE |
| fieldKey | string | слепок ключа |
| label | string? | слепок подписи |
| role | enum | слепок `goal` \| `metric` |
| unit | string? | слепок |
| valueNumber | decimal? | факт |
| sortOrder | integer | слепок порядка |

Инвариант: unique `(historyId, fieldKey)`.

`HabitHistory.actualValue` / `goalValue` остаются для базовой однополевой цели до полного перехода UI.
