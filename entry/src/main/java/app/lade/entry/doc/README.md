# `entry`

## Tasks

Текущая волна: [tasks-v2.md](tasks-v2.md)  
Архив: [tasks-v1.md](tasks-v1.md)

## Экраны

| Экран | Назначение |
|-------|------------|
| `EntryList` | Список конфигов + фильтр kind + архив |
| `EntryEdit` | Создание / правка Entry (матрица полей по kind) |

## Части

| Часть | Назначение |
|-------|------------|
| domain | Entry, EntryHistory, проекция, Detect/ResolveContainment, SplitTimedEntry, ResolveSeriesEdit, KindPriority |
| data | Room impl + KindPriorityStore |
| ui | list / edit / mark actions / unit labels; диалоги накрытия |

## Термины

| Термин | eng | Смысл |
|--------|-----|--------|
| Запись (конфиг) | `Entry` | Настройки: task / event / habit / schedule |
| История | `EntryHistory` | Фиксация пользователя на дату/время (снимок) |
| Серия | series | Entry с повтором; грядущее — развёртка, не History |
| Одиночная | single | Entry на конкретный слот; важнее серии |

## Документы

| Файл | О чём |
|------|--------|
| [model-entry.md](model-entry.md) | Конфиг Entry |
| [model-entry-history.md](model-entry-history.md) | History / снимки / статусы |
| [tasks-v1.md](tasks-v1.md) | Волна 1 — модель и данные |
| [tasks-v2.md](tasks-v2.md) | Волна 2 — create / nav |
