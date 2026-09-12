# `database` (Gradle `:database`)

## Tasks

Текущая волна: [tasks-v2.md](tasks-v2.md)  
Архив: [tasks-v1.md](tasks-v1.md)

## Части (по владельцу)

| Пакет | Назначение |
|-------|------------|
| `category/` | CategoryEntity, CategoryDao |
| `entry/` | EntryEntity, EntryHistoryEntity, EntryDao, EntryHistoryDao |
| `chat/` | ChatDict/Message Entity+Dao |
| `LadeDatabase` / `DatabaseModule` | Room + Hilt |

**Не в модуле:** `database/ui` (shell), seed — в `:app`.

## Схема

```
Category ──< Entry, EntryHistory
Entry ──< EntryHistory
ChatDict, ChatMessage
```

version = 14 (destructive). `chat_templates` снята; `chat_dicts`: kind + phrases.

## Документы

| Файл | О чём |
|------|--------|
| [rules.md](rules.md) | Доступ к модулю, allowlist |
| [tasks-v1.md](tasks-v1.md) | Волна 1 — скелет Gradle |
| [tasks-v2.md](tasks-v2.md) | Волна 2 — перенос + раскладка |

Канон полей Entry — в `entry/doc/`. SSOT = Room.
