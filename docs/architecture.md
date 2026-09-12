# Архитектура Ладь

Нативное Android-приложение (`app.lade`). Фича = каталог-пакет = `doc/` рядом с кодом.

## Стек

| Слой | Выбор |
|------|--------|
| Язык | Kotlin, Coroutines + Flow |
| minSdk | API 26 (runtime checks для Health Connect / exact alarms) |
| UI | Jetpack Compose, Material 3, Navigation Compose, ViewModel |
| DI | Hilt |
| БД | Room (SSOT) |
| Prefs | DataStore |
| RRULE | biweekly за портом `ScheduleEngine` |
| Графики | Vico (когда понадобится) |
| Напоминания | AlarmManager + NotificationCompat; мягкие — WorkManager |
| Сборка | Gradle Kotlin DSL, version catalog |
| Автотесты | не пишем; готовность — ручной прогон |

Почему не Flutter/RN: exact alarms, Health Connect / HMS, Calendar Provider проще нативно.

## Пакеты

Каталоги в `:app`, не отдельные Gradle-модули (пока).

```
app/src/main/java/app/lade/
├── database/     # Room: entity, dao, сиды + doc/
├── temporal/     # domain (RRULE) + ui (пикеры) + doc/
├── categories/   # domain / data / ui + doc/
├── entry/        # Entry + EntryHistory (заменил habits/time)
├── habits/       # архив doc/ only (код удалён после cutover)
├── time/         # архив doc/ only (код удалён после cutover)
├── calendar/     # экран дня/недели/месяца/ленты
├── chat/         # чат/шаблоны → entry.domain
├── reminders/    # уведомления / будильники → Entry
├── devices/      # Health
├── calsync/      # внешние календари → calendar
├── analytics/    # аналитика
└── ui/           # shell: Home, NavHost, тема + doc/
```

Доки фичи: `…/<пакет>/doc/` — изолированы (не ссылаются на чужие `doc/`).  
Канон прогресса — `doc/tasks-vN.md` (`Task | Описание | Статус`); в README — только указатель текущей волны.  
`scope` — в `tasks-v1`. `versionCode` = число файлов `tasks-v*.md` по всем пакетам.  
Даты реализации — только в `human/` (скрипт + `implemented.json`).  
Шаблоны: Cursor `lade-doc-readme`, `lade-doc-tasks`. Контракт импортов: Cursor `lade.mdc`.

## Слои внутри фичи

```
feature/
  domain/   # модели, порты репозиториев
  data/     # impl → DAO Room, мапперы
  ui/       # Compose, ViewModel
  doc/      # смысл, контракт, модели, задачи
```

`temporal`: только `domain` + `ui` (нет своих таблиц).  
`database`: без UI/use-case; канон полей сущностей — в `doc/` фичи-владельца.

## Зависимости

```
ui → domain ← data → database
calendar|entry → categories.domain + CategorySelector / CategoryColorIndicator / CategoryColorPicker
*.ui → temporal.ui
entry|calendar → temporal.domain
chat → entry.domain + categories.domain
*.domain ↛ Compose, Room
temporal.domain ↛ Compose
temporal ↛ database, categories, entry
```

Чужой пакет снаружи видит только `*.domain` (интерфейсы + модели) и явные UI-виджеты
(`CategorySelector`, `CategoryColorIndicator`, `CategoryColorPicker`, `temporal.ui`,
`ui.edit.EditSectionCard`, `entry.ui` mark/unit labels). Не импортировать чужие `data` / DAO.

```
                    ┌──────── shell (ui) ────────┐
                    │  NavHost, Home, Hilt       │
                    └──────┬──────┬──────┬───────┘
         chat.ui     entry.ui  calendar.ui  categories.ui
              │           │        │           │
         *.domain    *.domain *.domain    *.domain
              │           ▲        ▲           ▲
              └───────────┴────────┴───────────┘
                              ▲
                         *.data → database (Room)
```

## Room = SSOT

Внешние Health / Calendar → Gateway → нормализация → Room.  
Отключение интеграции не удаляет ручные данные. Импорт не затирает `manual` / schedule.

## Зависимости снаружи (кратко)

| Что | Зачем |
|-----|--------|
| Room, DataStore, WorkManager | данные, prefs, фон |
| biweekly | RRULE за `ScheduleEngine` |
| Health Connect (+ опц. Huawei Kit) | факты здоровья |
| Calendar Provider (+ опц. Google Calendar API) | внешние события |
| AlarmManager / Notifications | жёсткие / мягкие напоминания |

Не брать: чужой all-in-one трекер как основу; Calendar/Health как единственный SoT; только FCM вместо exact alarms; обязательный backend с дня 1.
