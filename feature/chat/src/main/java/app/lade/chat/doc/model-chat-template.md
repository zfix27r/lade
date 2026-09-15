# ChatTemplate / ChatDict — архив

> Superseded v6. System match → [corpus-format.md](corpus-format.md). User → `ChatDictEntry` (`kind` + `phrases`). Templates Room удалены.

## ChatTemplate (удалено)

Было: triggers + slots + target (`entry_mark` / `entry_timed`) в Room. Заменено corpus JSON + Tail/Slots stages.

## ChatDict (legacy shape)

| Поле | Было |
|------|------|
| type | `verb` \| `alias` |
| value | kind или match needle |

v6: `kind: EntryKind` + `phrases: List<String>` — см. `ChatDictEntity`.

## Apply (v6)

См. [rules-parse.md](rules-parse.md) — `ChatCommand` → `ApplyChatCommand`.
