# tasks-v3

| Task | Описание | Статус |
|------|----------|--------|
| parse-pipeline | Пайплайн стадий (normalize → candidates → resolve Entry → disambiguate → apply); правила в отдельных файлах, не God-parser | done |
| parse-candidates | Выход = список гипотез; ровно один ясный match Entry → apply; иначе choice | done |
| parse-to-entry | Парсер → draft Entry / EntryHistory (не HabitMark/TimeBlock) | done |
| choice-ephemeral | Уточнение кнопками в чате; кнопки не в истории — выбрал → исчезли; выбор фиксируется текстом в ленте / вводе | done |
| suggest-create | Нет Entry: предложить вариант (напр. создать привычку), не тупик «не найден» | done |
| dict-system-user | Словари: системные (неубиваемые / restore) + пользовательские правки и расширения; UI редактирования | done |
| dict-relative-date | Словарь: сегодня / завтра / вчера → date | done |
| dict-daypart | утро / день / вечер → start_time (DayPart prefs) | done |
| dict-intent-verbs | Глаголы → kind (купить→task, встретиться→event, бегать→habit, работать→schedule) | done |
| dict-past-mark | Past/синонимы (пробежал, бег, run) → одна Entry; History result+actuals | done |
| multi-slot-actuals | Несколько метрик в одной фразе («23 мин 5 км») → actuals как написал пользователь | done |
| chat-template-retarget | Эволюция шаблонов → словари Entry/History; target под Entry; seed/idempotent стык с dict-system-user | done |
| apply-entry | Apply пишет через entry.domain | done |
