# `agenda`

## Что это

Композитная модель **одной записи за день**. Собирает план и факт в готовую `AgendaModel` для отображения.

## Модели

| Модель | Что |
|--------|-----|
| `AgendaModel` | Композит: `entry` + `goals` + `logs` + `date` + `fromSeries` |
| `EntryModel` | План записи: kind, title, даты, время, повтор |
| `GoalModel` | Цель (упражнение) внутри записи: title, unit, amount, repeat, weight |
| `LogModel` | Факт выполнения: что сделано по goal за день |
| `OverlapModel` | Наложение двух записей по времени |
| `SeriesEditDraft` | Черновик правки серии |

## Единицы

| Enum | Что |
|------|-----|
| `EntryKind` | TASK / EVENT / HABIT / SCHEDULE |
| `GoalUnit` | LAP / M / KM / KG / MIN / HOUR / SET |
| `LogOrigin` | CHAT / CALENDAR / AGENDA / WIDGET / MANUAL |
| `SeriesEditScope` | WHOLE_SERIES / THIS_DAY_ONLY |
| `OverlapChoice` | DELETE_COVERED / SPLIT_COVERING |

## API

Единственный интерфейс — `AgendaApi`.

| Метод | Что делает |
|-------|------------|
| `get(entryId, date)` | Читает `AgendaModel` за день |
| `observeList(date)` | Реактивный список за день |
| `saveEntry(entry)` | Сохранить entry |
| `archiveEntry(entryId)` | Архивировать entry |
| `saveGoals(entryId, goals)` | Сохранить goals |
| `saveLogs(saveModel)` | Сохранить логи |
| `applySeriesEdit(entryId, date, scope, draft)` | Отредактировать серию |
| `resolveOverlap(choice, overlap, coveringEntry)` | Разрешить наложение |

## Ошибки

Каждая фича — свой `Error`:

| Ошибка | Фича |
|--------|------|
| `EntryError` | Entry |
| `GoalError` | Goal |
| `LogError` | Log |
| `OverlapError` | Overlap |
| `SeriesError` | Series |

## Результат

`Result<T, E>` — общий. `Success(value)` или `Failure(error)`.

## Пакеты

| Пакет | Что |
|-------|-----|
| `api/` | Публичное: модели, enum, ошибки, `AgendaApi` |
| `data/` | Внутреннее: реализации, сторы, валидаторы, мапперы |
| `data/` не торчит | Соседи видят только `api/` |

## Зависимости

`:agenda/data/` → `:agendastore` (DAO, entity). `:agenda` не знает про Room.