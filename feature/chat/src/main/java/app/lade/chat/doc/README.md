# `chat`



## Tasks



Текущая волна: [tasks-v6.md](tasks-v6.md) — **done**  

Архив: [tasks-v1.md](tasks-v1.md), [tasks-v2.md](tasks-v2.md), [tasks-v3.md](tasks-v3.md), [tasks-v4.md](tasks-v4.md), [tasks-v5.md](tasks-v5.md)



## Экраны



| Экран | Назначение |

|-------|------------|

| `Chat` | Фраза → ParseOutcome → apply / choice / suggest-create |

| `ChatTemplatesList` | Корпус (read-only) + user dict |

| `ChatDictEdit` | User-фраза: kind + needles |



## Части



| Часть | Назначение |

|-------|------------|

| domain/pipeline | Normalize → Intent → Slots → Tail → Resolve → Decide |

| domain | `ChatCommand`, `ApplyChatCommand` → entry.domain; Corpus / Dict / History |

| data | assets corpus; Room dicts + history |

| ui | bubbles, choice, suggest-create; dict editor |



## Термины



| Термин | eng | Смысл |

|--------|-----|--------|

| Intent | `UserIntent` | mark / name / create / timed |

| Draft | `ParseDraft` | intent + slots + tail до resolve |

| Outcome | `ParseOutcome` | execute / choice / suggest / failed |

| Command | `ChatCommand` | действие для apply |

| Корпус | corpus | system JSON в assets |

| Словарь | `ChatDictEntry` | user kind + phrases в Room |



## Документы



| Файл | О чём |

|------|--------|

| [rules-parse.md](rules-parse.md) | Пайплайн v6, choice, match |

| [corpus-format.md](corpus-format.md) | JSON корпус, needles, intent |

| [parse-regression-checklist.md](parse-regression-checklist.md) | Ручная проверка фраз |

| [tasks-v6.md](tasks-v6.md) | Волна 6 — целевой пайп (архив) |

| [model-chat-template.md](model-chat-template.md) | **Архив** — ChatTemplate / VERB\|ALIAS |

| [tasks-v5.md](tasks-v5.md) | Волна 5 — assets corpus |

| [tasks-v4.md](tasks-v4.md) | Волна 4 — intent skeleton |

| [tasks-v3.md](tasks-v3.md) | Волна 3 — Entry, choice |

| [tasks-v2.md](tasks-v2.md) | Волна 2 — история |

| [tasks-v1.md](tasks-v1.md) | Волна 1 |

