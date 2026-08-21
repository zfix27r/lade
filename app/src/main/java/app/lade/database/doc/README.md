# `database`

## Tasks

Текущая волна: [tasks-v2.md](tasks-v2.md)  
Архив: [tasks-v1.md](tasks-v1.md)

## Части

| Часть | Назначение |
|-------|------------|
| entity / dao | Таблицы и запросы |
| seed | Сиды Category |
| DatabaseModule | Hilt provides |

## Схема

```
Category ──< Habit, TimeSchedule, TimeBlock, HabitHistory
Habit ──< HabitHistory
HabitHistory ── TimeBlock?   (опционально)
TimeSchedule ──< TimeBlock
```

Инварианты: SSOT = Room; смена Habit не переписывает History; unique (habitId, date); free = 24h − busy; пересечения блоков запрещены.
