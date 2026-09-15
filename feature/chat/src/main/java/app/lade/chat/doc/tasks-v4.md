# tasks-v4

Скелет целевого пайпа в `:app` (без модуля `:chat`). Старый `ChatParsePipeline` / template|alias|verb пока не трогаем и не вырезаем.

| Task | Описание | Статус |
|------|----------|--------|
| parse-orchestrator | Новый оркестратор: `String` in → пока наружу Intent (или UNCLEAR); внутри только цепочка stage-файлов | done |
| parse-normalize-wire | Normalize — отдельный файл (есть `NormalizeStage`); подключить к оркестратору первым шагом | done |
| parse-intent-stage | Новый файл Intent: по токенам определить намерение (заглушка/минимальный match ок); подключить вторым шагом | done |
| parse-intent-anywhere | Intent: искать фразу anywhere в токенах (не только с начала); rest = все токены кроме matched; приоритет длиннее, при равной длине MARK_DONE > NAME_OR_CREATE | done |
| parse-slots-stage | Новый stage Slots: из restTokens жадные пары NUM↔unit/metric (оба порядка); facts + leftover; wire в оркестратор 3-м шагом; seed hardcoded | done |
