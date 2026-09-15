# tasks-v1

| Task | Описание | Статус |
|------|----------|--------|
| scope | Быстрый текстовый ввод по шаблонам; без ИИ; запись через порты habits/time | done |
| template-model | Модель шаблона: trigger, slots, defaults, target (history / block) | done |
| parse-engine | Токен-парсер + словари единиц; без NLP | done |
| chat-ui | Экран чата: сообщения пользователя / подтверждение / ошибка разбора | done |
| template-editor | CRUD шаблонов (триггеры, слоты, цель записи) | done |
| bridge-habit-mark | Команда → HabitHistory (actual + метрики) | done |
| bridge-time-block | Команда → manual TimeBlock | done |
| seed-templates | Сиды базовых шаблонов (бег и т.п.) | done |
| entry-point | Bottom tab «Чат» (рядом с Календарь / Ещё) | done |
| rename-from-intake | Пакет `intake` → `chat` | done |
| ai-hook | Порт ChatParseEnhancer (identity); LLM позже, тот же пайплайн | done |
| smoke | Прогон: фраза → запись → видно в календаре | done |
