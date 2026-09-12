# `ui`

## Tasks

Текущая волна: [tasks-v5.md](tasks-v5.md)  
Архив: [tasks-v1.md](tasks-v1.md), [tasks-v2.md](tasks-v2.md), [tasks-v3.md](tasks-v3.md), [tasks-v4.md](tasks-v4.md)

## Экраны

| Экран | Назначение |
|-------|------------|
| `Calendar` / `Chat` / `More` | Вкладки bottom bar |
| `More` | Сводка «сегодня» (Entry) + Записи / категории / порядок слоёв / devices / calsync |
| `KindPrioritySettings` | Порядок слоёв kind в календаре |
| `DevicesStub` / `CalendarsStub` | Заглушки |

```
start → Calendar
tabs: Calendar | Chat | More
More → Entries | Categories | KindPriority | Devices | Calsync | DayPart
```

## Части

| Часть | Назначение |
|-------|------------|
| `edit` | `EditSectionCard`, `TitleTextField` |
| `settings` | KindPrioritySettings |
| `screens` | More и stubs |
| `home` | TodaySummaryViewModel (Entry) |

## Документы

| Файл | О чём |
|------|--------|
| [rules.md](rules.md) | Ресурсы, тема, Material 3, UX-паттерны |
| [tasks-v5.md](tasks-v5.md) | Волна 5 — Entry nav, priority settings |
| [tasks-v4.md](tasks-v4.md) | Волна 4 (insets, IME, create labels) |
| [tasks-v3.md](tasks-v3.md) | Волна 3 (UX-паттерны) |

