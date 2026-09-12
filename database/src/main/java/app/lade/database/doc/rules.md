# Database: модуль и доступ

Канон границ Gradle-модуля `:database`.

## Роль

Room SSOT: entity, dao, `LadeDatabase`, `DatabaseModule`. Без UI, без use-case, без domain-логики фич.

Раскладка по владельцу: `category/`, `entry/`, `chat/`.

## Кто может зависеть от `:database`

| Модуль / пакет | Разрешено |
|----------------|-----------|
| `*.data` (entry, categories, chat) | dao + entity через репозитории impl |
| `:app` seed | CategorySeeder → CategoryDao |
| `:app` | Hilt-граф (подхватывает DatabaseModule) |
| `feed`, `calendar.ui`, `*.domain` | **нет** dao/entity |
| `database/ui` в app | не часть `:database` |

## Зависимости Gradle

Room, Hilt, KSP. Без Compose, Navigation, фич. App **не** тянет Room напрямую.

## Схема

version = 14. `chat_templates` снята; `chat_dicts`: `kind` + `phrasesCsv`. Legacy `legacyHabitId` / `legacyScheduleId` / `legacyBlockId` сняты.
