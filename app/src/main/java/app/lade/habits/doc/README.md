# `habits`

## Tasks

Текущая волна: [tasks-v3.md](tasks-v3.md)  
Архив: [tasks-v1.md](tasks-v1.md), [tasks-v2.md](tasks-v2.md)

## Экраны

| Экран | Назначение |
|-------|------------|
| `HabitsList` | Список |
| `HabitEdit` | Создание / изменение |

## Части

| Часть | Назначение |
|-------|------------|
| domain | HabitRepository, HabitHistoryRepository, DueHabitsProjector, HabitStreakProjector, MarkHabitDay, модели |
| data | Room impl |
| ui | list / edit; `HabitDayMarkActions` / streak / history-sheet |

## Термины

| Термин | eng | Смысл |
|--------|-----|--------|
| Привычка | `Habit` | Текущие настройки |
| История привычки | `HabitHistory` | Слепок прогресса за день |
| Слепок | snapshot | Поля History, не зависящие от текущего Habit |
| Соблюдение | `adherence` | Метрика выполнения |
| Серия | `streak` | Серия `done` |

Не путать с шаблонами/блоками занятости (`time`).

## Документы

| Файл | О чём |
|------|--------|
| [model-habit.md](model-habit.md) | Текущие настройки |
| [model-habit-history.md](model-habit-history.md) | Слепок дня |
| [rules.md](rules.md) | Статусы дня, слепок |
