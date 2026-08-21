# `time`

## Tasks

Текущая волна: [tasks-v2.md](tasks-v2.md)  
Архив: [tasks-v1.md](tasks-v1.md)

## Экраны

| Экран | Назначение |
|-------|------------|
| `TimeSchedulesList` | Список шаблонов |
| `TimeScheduleEdit` | Создание / изменение |
| `TimeBlockEdit` | Разовый блок (manual) |

## Части

| Часть | Назначение |
|-------|------------|
| domain | TimeScheduleRepository, TimeBlockRepository, DayBusyProjector, ExpandTimeSchedule, SaveManualTimeBlock, модели |
| data | Room impl |
| ui | list / edit (schedule + block) |

## Термины

| Термин | eng | Смысл |
|--------|-----|--------|
| Расписание времени | `TimeSchedule` | Шаблон на период дат + часы |
| Блок | `TimeBlock` | Конкретный интервал в сутках |
| План суток | `DayPlan` | Бюджет долей без часов |
| Прочее / свободно | `unallocated` | 24ч − занятое блоками |
| Источник блока | `Source` | `schedule` \| `manual` \| `health` \| `calendar` |

Не путать с привычками (`habits`): здесь часы занятости, не цель в км.

## Документы

| Файл | О чём |
|------|--------|
| [model-time-schedule.md](model-time-schedule.md) | Шаблон |
| [model-time-block.md](model-time-block.md) | Интервал суток |
| [model-day-plan.md](model-day-plan.md) | Бюджет долей |
| [rules.md](rules.md) | Пересечения, free, развёртка |
| [tasks-v2.md](tasks-v2.md) | Волна 2 |

