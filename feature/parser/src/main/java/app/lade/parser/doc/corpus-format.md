# Chat corpus (assets)

Системный корпус для match-пайпа. **Не** пишется в Room. User-строки — только в `chat_dicts` (`systemKey = null`).

## Файлы

| Asset | Kind |
|-------|------|
| `assets/chat/corpus-habit.json` | habit |
| `assets/chat/corpus-event.json` | event |
| `assets/chat/corpus-task.json` | task |
| `assets/chat/corpus-schedule.json` | schedule |

Kind задаётся **именем файла**, в JSON не дублируется.

## Запись (v2)

```json
{
  "version": 2,
  "entries": [
    {
      "systemKey": "habit_run",
      "title": "Бег",
      "needles": [
        { "text": "*беж*", "intent": "mark_done" },
        { "text": "*бег*", "intent": "name_or_create" },
        { "text": "run", "intent": "name_or_create" }
      ]
    }
  ]
}
```

| Поле | Смысл |
|------|--------|
| systemKey | Стабильный id для restore / UI (не PK в Room) |
| title | Имя Entry + needle для resolve (`title.lowercase()`) |
| needles | exact или stem (`*беж*`) |
| needles[].intent | `mark_done` \| `name_or_create` \| `create_kind` \| `timed` (опционально — default по kind) |

**Нет:** category, needleTitle, kind в JSON.

## v1 (legacy read)

Loader принимает v1: `needles` как массив строк; intent = default по kind файла.

## Needles

- **exact** — токен целиком или multi-word подряд (`к врачу`)
- **stem** — `*xxx*` (мин. 3 символа внутри), проверка `token.contains(xxx)`

## Restore

Системная строка **не в БД**. Restore = `CorpusLoader.reload()` — перечитать assets (после обновления APK или сброса кэша).

## User

Room `chat_dicts`: `kind` + `phrases`. Парсер: `merge(systemCorpus, userDict)`.

Хвост (км, пульс, время) — `TailStage` + `SlotsStage`, не в JSON.
