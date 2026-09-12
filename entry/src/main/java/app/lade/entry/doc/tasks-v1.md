# tasks-v1

| Task | Описание | Статус |
|------|----------|--------|
| scope | Entry=настройки; History=фиксация на дату/время; kind task/event/habit/schedule; schedule без History; одиночка > серия; удаление habits+time после cutover | done |
| model-entry | Канон Entry + create-матрица по kind; goalDefs, reminder, archive | done |
| model-history | Канон History: снимки goals/actuals; result done/skipped/cancelled; только по действию пользователя; не для schedule | done |
| room-migrate | Entity/DAO + миграция данных с Habit/TimeSchedule/TimeBlock/HabitHistory/Field* | done |
| project-series | Развёртка серии для UI/календаря без insert History | done |
| series-mask | Одиночная Entry важнее проекции серии на пересекающийся слот | done |
| overlap-containment | Полное накрытие (History + проекция Entry) → диалог: удалить накрытую / разрезать накрывающую | done |
| split-interval | Операция разреза интервала по согласию пользователя | done |
| kind-priority | Дефолт schedule < habit < task < event; порядок хранится в настройках | done |
| day-list-order | День: сначала без времени (открытые due), ниже по времени; слои при пересечении | done |
| series-vs-single | UX: изменить всю серию / создать одиночную на день | done |
| cutover-gate | Create + календарь + history/mark + reminders + чат → Entry; legacy UI снят | done |
| delete-legacy | Удалены пакеты habits/time (код) и таблицы Room; остался архив doc/ | done |
