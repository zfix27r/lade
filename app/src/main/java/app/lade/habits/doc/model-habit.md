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
| alarmMode | enum | `none` \| `notification` \| `alarm` |
| pausedAt | instant? | |
| archivedAt | instant? | |
| createdAt | instant | |

Прогресс — в [HabitHistory](model-habit-history.md).
