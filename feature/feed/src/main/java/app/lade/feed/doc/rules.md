# Feed: порты и доступ

Канон границ модуля `:feed`. Allowlist импортов — `feed-arch-allowlist` в tasks-v1.

## Роль

Лента — **орган сводки и действий** (список, отметки, позже статистика). Не календарная сетка; не create/edit форма.

## Модели (feed.domain)

- `FeedEntry` — срез Entry на день; v1: `id`, `kind`, `title`, `result`. `kind` — storage-код; подписи — UI. `categoryId`, время — задача feed-entry-extend.
- `FeedState` — снимок экрана: `dateEpochDay` + `entries`; позже — другие блоки сводки, не только entry.
- Entry из БД полной моделью; mapper `DaySlot` → `FeedEntry` после wire-entry.
- `historyId` в FeedEntry не храним; mark — entry по `id` + `dateEpochDay` из state.
- `FeedRepository` — только `observeState()`; отметки — `MarkEntryDay` (entry), обновление через Flow Room.

## Контракт (frozen)

Изменение только с задачей в tasks + правкой `FeedDomainContractTest`.

| Тип | Поля / API |
|-----|------------|
| `FeedEntry` | `id`, `kind`, `title`, `result` |
| `FeedState` | `dateEpochDay`, `entries` |
| `FeedRepository` | `observeState(): Flow<FeedState>` |

Проверка: `.\gradlew :feed:testDebugUnitTest`.

## DI

- Hilt в `:feed` для ViewModel и модулей feed.
- `FeedRepository`: fake → real через `@Binds` (задача feed-repository-fake).
- `:entry-domain` / Room — подключение отдельной задачей; на старте не тянуть.

## Навигация

Feed **не** импортирует NavHost / Routes. Только колбэки или `FeedHost` (интерфейс), реализация в `:app`.

## UI

- Чужие `*.ui` фич — **запрещены** (categories, entry.ui, calendar.ui).
- Общие виджеты — только `:ui-widgets` / shared (backlog) или свой renderer в feed.

## Ресурсы

- Строки и dimens ленты — `feed/src/main/res/`.
- Общие spacing / «Назад» / «ОК» — `:core:resources` (backlog); до него — минимум своих строк.

## Allowlist (черновик)

| Порт / зависимость | Кому разрешено |
|--------------------|----------------|
| `FeedRepository` (fake → entry) | `feed.domain`, `feed.ui` |
| `entry.domain` (read, mark) | `feed.domain` — после задачи wire-entry |
| DAO / `database` | **никому** из feed |

Расширение allowlist — явный diff `rules.md` + tasks.

## Зависимости Gradle

Только необходимое: Compose, Material3, Lifecycle VM, Hilt. Без Navigation, Room, calendar, entry-data — пока не задача в tasks.
