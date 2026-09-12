# `calsync`

## Tasks

Текущая волна: [tasks-v1.md](tasks-v1.md)

## Термины

| Термин | eng | Смысл |
|--------|-----|--------|
| Зеркало календаря | `CalendarLink` | Связь сущности Ладь с внешним event |

## Правила

1. Provider/API → Gateway → Room.
2. UI не знает SDK.
3. Импорт не перекрывает `manual` / schedule.
4. Идемпотентность `(calendarId, eventId)`.
