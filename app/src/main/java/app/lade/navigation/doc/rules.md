# UI: ресурсы и Material

Правила для Compose/UI и `res/`. Агент: `.cursor/rules/lade-ui.mdc`.

## Строки и значения

| Что | Куда |
|-----|------|
| Текст пользователю, contentDescription | `strings.xml` → `stringResource` |
| Шаблоны с аргументами (`С %1$s`, списки) | `strings.xml` format → `stringResource(id, …)` |
| Плюралы (1 день / N дней) | `plurals.xml` → `pluralStringResource` |
| Паттерны даты/времени (`HH:mm`) | `strings.xml` → `DateTimeFormatter.ofPattern` |
| Повторяемые отступы/размеры | `dimens.xml` → `dimensionResource` |
| Цвета бренда (не role M3) | `colors.xml` → тема / `colorResource` |
| Наборы подписей (дни недели) | `strings` + маппинг в UI, не в `domain` |

Не хардкодить строки и «магические» числа в `*.kt` экранов.  
Подписи для enum/кодов (`RecurrencePreset`, единицы привычки) — в UI/`strings`, не в `domain`.

## Тема и стили

- Единая тема: `LadeTheme` + Material 3 (`colorScheme` / `typography` / `shapes`).
- XML: `Theme.Lade` — без зоопарка style на экран.
- Не плодить стили: сначала токены M3 и компоненты Material3; общий паттерн — в тему или один shared composable.
- Вторичный текст: `onSurfaceVariant`, не произвольный alpha на `onSurface`.

## Material Design

- `Scaffold`, app bar, FAB, списки — по M3.
- Сетка 8 dp (`spacing_*` в `dimens`), touch target ≥ 48 dp (`min_touch_target`).
- Иконки навигации — `IconButton` + `contentDescription` из ресурсов.

## UX-паттерны (create / app bar)

### Контекстный TopAppBar

- `title` = контекст экрана (дата / диапазон / счётчик / поиск), не декоративная подпись вроде «Календарь».
- `actions` = режимы и команды (виды календаря, удалить…).
- Тап по title там, где есть «якорь» (сегодня / текущий месяц) — быстрый сброс к нему.

### Create: simple → advanced

- Один жест создания с главного места (FAB календаря → sheet).
- Короткая развилка типа (блок / привычка); дальше экраны с **сильными дефолтами**.
- Поля сгруппированы в карточки-секции (`EditSectionCard`): категория, цель/интервал, период, повтор, напоминание.
- Сначала только то, что нужно для сохранения; детали — в секциях / later sheets.

### Shared-секции

- Общий виджет: `app.lade.ui.edit.EditSectionCard`.
- Title: `TitleTextField` — без floating label, `Sentences`, clear (✕).
- Temporal-блоки (дата, интервал, RRULE, alarm) — `TemporalOptionsPanel` с карточками секций (`useSectionCards`).
- Не копировать разметку карточек на каждый edit-экран.

### Insets / IME

- Корневой `LadeApp` Scaffold: `contentWindowInsets = 0` (без двойного top gap; child TopAppBar владеет status bars).
- Chat / create: Scaffold insets без IME; `imePadding()` на контент — app bar на месте.

## Ещё выносить (часто забывают)

- `contentDescription` у всех значимых иконок
- Диалоги: OK / Отмена / Сброс
- Пустые состояния и stub-тексты
- Ключи данных (`km`, `violet`) можно оставить кодами; **отображаемые** имена — в `strings`
- Кастомные иконки/иллюстрации — `drawable` / `mipmap`, не base64 в коде
- Сиды БД с пользовательскими названиями — через `Context.getString`, не литералы в Kotlin
