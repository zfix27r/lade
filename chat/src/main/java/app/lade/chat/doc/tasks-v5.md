# tasks-v5

Системный корпус: JSON в assets (по kind), restore = reload JSON. Room `chat_dicts` — только user. Без category, без needleTitle.

| Task | Описание | Статус |
|------|----------|--------|
| chat-corpus-doc | Формат JSON в `corpus-format.md`; merge system+user; restore = reload assets | done |
| chat-corpus-assets | `assets/chat/corpus-{habit,event,task,schedule}.json` | done |
| chat-corpus-loader | CorpusLoader → domain; кэш; system не в Room | done |
| chat-match-stems | PhraseMatcher: exact + `*stem*`; Intent + MatchCatalogCandidatesStage | done |
| chat-dict-user-only | Убрать system seed в Room; user `systemKey = null` | done |
| chat-corpus-cut-legacy | Intent hardcode, ChatDictSeeder, Alias/Verb stages | done |
| chat-corpus-ui-restore | UI: system read-only из corpus; reload assets | done |
