# `temporal`

## Что это

Работа с повтором. Предоставляет движок вычисления вхождений по `rrule` и модели повтора.

## Модели

| Модель | Что |
|--------|-----|
| `RecurrenceEngine` | Интерфейс: `isDue`, `occurrencesBetween` |
| `RecurrenceDraft` | Черновик повтора: `preset`, `interval`, `daysOfWeek` |
| `RecurrencePreset` | Пресет: Daily / Weekdays / Weekly / EveryNDays / Monthly / Yearly |
| `DaysOfWeekFlags` | Битовая маска дней недели |

## Реализация

| Класс | Что |
|-------|-----|
| `RecurrenceEngineBiweekly` | Вычисление вхождений через библиотеку biweekly |

## Пакеты

| Пакет | Что |
|-------|-----|
| `api/` | Публичное: `RecurrenceEngine`, `RecurrenceDraft`, `RecurrencePreset`, `DaysOfWeekFlags` |
| `data/` | Внутреннее: `RecurrenceEngineBiweekly`, DI |