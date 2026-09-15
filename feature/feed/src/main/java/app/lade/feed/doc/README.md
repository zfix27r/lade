# `feed`

## Tasks

Текущая волна: [tasks-v1.md](tasks-v1.md)

## Экраны

| Экран | Назначение |
|-------|------------|
| `Feed` | Сводка и действия: записи по дням, отметки, scroll к сегодня; позже — блоки статистики |

## Части

| Часть | Назначение |
|-------|------------|
| domain | `FeedEntry`, `FeedState`, `FeedRepository`; mapper — позже |
| ui | Список и секции ленты; навигация — колбэки наружу |
| doc | Канон органа, allowlist, задачи |

Gradle-модуль `:feed` (android-library). Код — по tasks-v1.

## Термины

| Термин | eng | Смысл |
|--------|-----|--------|
| Лента | `FeedState` | Снимок сводки; v1 — один день + entries; позже — stats и др. |
| Строка | `FeedEntry` | Срез Entry: `id`, `kind`, `title`, `result` (v1) |
| Сводка | summary | Агрегаты, due, progress — расширяемые блоки ленты |

## Документы

| Файл | О чём |
|------|--------|
| [rules.md](rules.md) | Порты, allowlist, DI, ресурсы |
| [tasks-v1.md](tasks-v1.md) | Волна 1 — модуль, doc, план оптимизаций |

## Стык

- **Entry** — SSOT конфигов; лента читает через порты domain, не DAO.
- **Calendar** (пока в `:app`) — обзор неделя/месяц/год; не дублирует ленту; split — backlog.
- **Навигация** — `:app` / NavHost; лента `onOpenEntry`; mark — entry.domain.
