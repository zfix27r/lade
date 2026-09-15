# tasks-v6

Целевой пайп без legacy: `ParseDraft` → `ResolvedDraft` → `ParseOutcome` → `ChatCommand` → entry.domain. Без `ParseCandidate`, без templates Room, без VERB/ALIAS.

| Task | Описание | Статус |
|------|----------|--------|
| parse-model-clean | `TailResult`, `ResolvedDraft`, `ChatCommand`, `ParseOutcome`, `EntryPick`; обновить `ParseDraft` | done |
| parse-tail-parser | `TailTokenParser` + `TailStage`; wire в оркестратор после Slots | done |
| parse-match-rule | `MatchRule`/`Needle`; corpus JSON v2 (`intent` на needle); убрать `dictType`/`habitLooksDone` | done |
| parse-stages-resolve-decide | `ResolveStage`, `DecideStage`; оркестратор → `ParseOutcome` | done |
| parse-apply-command | `ApplyChatCommand(ChatCommand)` | done |
| parse-ui-wire | `ChatViewModel` + choice на `EntryPick`/`ChatCommand` | done |
| parse-cut-legacy | Удалить `ChatParsePipeline`, `MatchCatalogCandidatesStage`, `TemplateCandidatesStage`, `ParseCandidate`/`PipelineResult` | done |
| dict-unify-user | User dict = corpus shape (`kind`+`phrases`); миграция Room; упростить edit UI | done |
| parse-cut-templates | Drop `chat_templates`, seeder, template screens/routes | done |
| corpus-json-v2 | Обновить 4 assets + loader; version 2; backward read v1 strings | done |
| dict-room-migration | Room migration VERB/ALIAS → kind+phrases; фильтр старых system rows | done |
| parse-regression-checklist | Чеклист фраз в doc для ручной проверки parity | done |
| parse-doc-v6 | `rules-parse.md`, README; архив `model-chat-template.md` | done |
