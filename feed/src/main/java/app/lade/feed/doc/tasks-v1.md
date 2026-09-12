# tasks-v1

| Task | Описание | Статус |
|------|----------|--------|
| scope | Лента — орган сводки и действий; единая Entry в UI; kind назначает система; calendar split — backlog; Hilt в модуле; минимум deps | done |
| feed-module | Gradle `:feed` (library, Compose + Hilt), подключён к `:app` | done |
| feed-doc | README, rules, tasks-v1 | done |
| feed-wire-app | `:app` implementation `:feed`; versionCode учитывает doc feed | done |
| feed-models | `Feed`, `FeedState`, `FeedRepository` (interface only) | done |
| feed-models-refine | `FeedEntry` + `FeedState` (1 день); поля как Entry (`id`, `kind`, …); repo только `observeState`; без FeedMark/FeedDay | done |
| feed-entry-minimal | `FeedEntry`: только `id`, `kind`, `title`, `result`; остальное — при wire-entry/UI | done |
| feed-domain-contract | Frozen domain: rules.md, lade-feed-domain.mdc, `FeedDomainContractTest`, test deps; `:feed:testDebugUnitTest` | done |
| feed-entry-extend | Добавить в FeedEntry: `categoryId`, `startTime`, `endTime` + обновить contract test | backlog |
| feed-repository-fake | `FakeFeedRepository` + Hilt `@Binds`; отложено — сначала `:database`, затем `feed-wire-entry` | backlog |
| feed-screen-stub | `FeedScreen` + `FeedViewModel` (после feed-wire-entry) | backlog |
| feed-unified-row | UI: одна строка; без split habits/busy в calendar | backlog |
| feed-kind-system | Kind — метка системы, не выбор пользователя в create | backlog |
| feed-mark-gesture | Отметка: один жест (swipe/✓), не AssistChip в trailing | backlog |
| feed-today-action | «Сегодня» в app bar ленты | backlog |
| feed-nav-callbacks | `onOpenEntry`; mark через entry.domain, не FeedRepository | backlog |
| feed-shared-widgets | `:ui-widgets` или свой row; без импорта чужих ui | backlog |
| feed-core-resources | Shared spacing/strings + свои строки feed | backlog |
| feed-wire-entry | `EntryFeedRepository` → entry.domain; убрать fake | backlog |
| feed-arch-allowlist | Konsist/ArchUnit: allowlist импортов feed (без DAO) | backlog |
| feed-move-from-calendar | Перенос Feed UI/VM из calendar; calendar не трогать до готовности | backlog |
| more-no-dup-today | More без дубля «сводка + открыть календарь» (`ui`) | backlog |
| calendar-module-split | Модуль calendar: неделя/месяц/год; отдельно от feed | backlog |
