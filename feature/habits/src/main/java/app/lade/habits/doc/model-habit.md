# Habit

Текущие настройки. Mutable.  
Пример: «Бег», пн/ср/пт, цель 2 км, без обязательного времени суток.

Не смешивать с шаблонами занятости суток (другой пакет).

| Поле | Тип | Примечание |
|------|-----|------------|
| id | integer | autoincrement |
| title | string | |
| categoryId | integer | FK → Category |
| rrule | string | частота (напр. BYDAY=MO,WE,FR) |
| timeOfDay | localTime? | опционально — якорь напоминания, не занятость суток |
| goalType | enum | `duration` \| `count` \| `distance` |
| goalValue | decimal | в единицах goalUnit |
| goalUnit | string | `min` \| `reps` \| `km` \| … |
| goalMode | enum | `per_due` — цель на каждый due-день; `cumulative` — накопительная сумма |
| alarmMode | enum | `none` \| `notification` \| `alarm` |
| pausedAt | instant? | |
| archivedAt | instant? | |
| createdAt | instant | |

Доп. метрики — [HabitFieldDef](model-habit-fields.md).  
Прогресс — в [HabitHistory](model-habit-history.md).

## Create defaults

Минимум для сохранения: title + category.  
Сильные дефолты: повтор = будни, цель = `1` × `reps`, `goalMode` = `per_due`, без time/alarm.
