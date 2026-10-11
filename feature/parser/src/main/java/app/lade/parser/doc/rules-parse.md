# Правила парсинга чата

Канон v6 (`tasks-v6`). Пакет `:parse` не выносим — чат единственный потребитель.

## Пайплайн

`ChatParseOrchestrator`: raw → **ParseOutcome** → `ApplyChatCommand` → `entry.domain`.

| # | Stage | Выход |
|---|--------|--------|
| 1 | Normalize | tokens |
| 2 | Intent | `IntentResult` (UserIntent, kindHint, titleHint, restTokens) |
| 3 | Slots | `SlotsResult` (facts + leftover) |
| 4 | Tail | `TailResult` (date, time, title, actuals) |
| 5 | Resolve | `List<ResolvedDraft>` (entryId?) |
| 6 | Decide | `ParseOutcome` |

### ParseOutcome

| Вариант | UI |
|---------|-----|
| `Execute(ChatCommand)` | apply сразу |
| `ChooseEntry` | эфемерные chips |
| `SuggestCreate` | bubble + кнопка создать Entry |
| `Failed` | error bubble |

### ChatCommand

`MarkHistory` · `UpsertEntry` · `TimedBlock` · `Create` (draft для suggest)

## Intent ≠ Kind

- **Intent** — что хочет пользователь (`MARK_DONE`, `NAME_OR_CREATE`, `CREATE_KIND`, `TIMED`).
- **Kind** — тип Entry (`habit`, `task`, …).
- На needle в corpus v2: `"intent": "mark_done"` / `"name_or_create"` (habit).

## Match

- System: assets JSON ([corpus-format.md](corpus-format.md)), `PhraseMatcher` (exact + `*stem*`).
- User: Room `chat_dicts` — `kind` + `phrases`, merge с corpus.
- Restore system = `CorpusLoader.reload()`, не Room.

## Choice

- Только в чате.
- Кнопки эфемерны (не в Room); выбор → текст user bubble + apply.
- Одно поле ввода снизу.

## Entry resolve

- Одна habit match + `MARK_DONE` → mark без вопроса.
- Несколько → `ChooseEntry`.
- Нет Entry + mark intent → `SuggestCreate`.
- `NAME_OR_CREATE` → не auto-mark; suggest/create.
- Метрики — как ввёл пользователь (`SlotsStage` + `TailStage`).

## Вне scope

- kind `note`
- отдельный Gradle-модуль `parse`
- хранение choice в Room

## Проверка

Ручной чеклист: [parse-regression-checklist.md](parse-regression-checklist.md).
