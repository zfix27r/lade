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

## Ещё выносить (часто забывают)

- `contentDescription` у всех значимых иконок
- Диалоги: OK / Отмена / Сброс
- Пустые состояния и stub-тексты
- Ключи данных (`km`, `violet`) можно оставить кодами; **отображаемые** имена — в `strings`
- Кастомные иконки/иллюстрации — `drawable` / `mipmap`, не base64 в коде
- Сиды БД с пользовательскими названиями — через `Context.getString`, не литералы в Kotlin
