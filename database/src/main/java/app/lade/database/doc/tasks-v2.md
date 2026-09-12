# tasks-v2

| Task | Описание | Статус |
|------|----------|--------|
| database-move-entity | Перенос `entity/*` из app → `:database` | done |
| database-entity-dedupe | Убрать дубли entity из app; SSOT только `:database` | done |
| database-cut-legacy | Вырезать `legacyHabitId`/`legacyScheduleId`/`legacyBlockId`; importer; dao/getByLegacy; calendar keys → entryId | done |
| database-entity-comments | Убрать шумные KDoc в entity | done |
| database-rename-title-match | `defaultHabitMatch` → `defaultTitleMatch` (entity + chat domain/ui/doc) | done |
| database-layout-by-owner | Папки/пакеты `category/`, `entry/`, `chat/` вместо общих entity/dao | done |
| database-bump-schema | version 12 (destructive) после cut/layout | done |
| database-move-dao | Перенос `dao/*` из app → `:database` | done |
| database-move-db | Перенос `LadeDatabase` + `DatabaseModule` из app → `:database` | done |
| database-app-cleanup | Удалить entity/dao/db/module из `app/.../database/`; импорты на `:database` | done |
| database-room-deps | Room/KSP только в `:database`; убрать дубль Room из app | done |
| database-ui-stays-app | `database/ui` (LadeApp, Routes, Theme, stubs) остаётся в app | done |
| database-seed-stays-app | `CategorySeeder` пока в app; dao из `:database` | done |
| database-rules | `rules.md`: dao только для `*.data` (+ seed/app wiring); domain/ui/feed — нет | done |
| database-category-entity-contract | Unit-тест: freeze полей `CategoryEntity` (имена + типы/nullability); Dao не трогаем | done |
| database-compile | `:database` + `:app` compileDebugKotlin | todo |
| database-arch-allowlist | ArchUnit/Konsist: запрет dao вне `*.data` | backlog |
