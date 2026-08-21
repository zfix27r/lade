# Ладь (Lade)

Личная система порядка дня: привычки и занятость суток.

## Запуск

1. Android Studio → Gradle Sync → Run `app`.
2. Home: вкладки Календарь · Привычки · Время · Ещё.

`applicationId`: `app.lade`.

## Документация

| Что | Где |
|-----|-----|
| Архитектура, пакеты, стек | [docs/architecture.md](docs/architecture.md) |
| Фича (смысл, модели) | `app/src/main/java/app/lade/<пакет>/doc/README.md` |
| Задачи волны | `…/doc/tasks-vN.md` (`versionCode` = число таких файлов) |
| Сводка прогресса (человек) | `powershell -File human/collect-progress.ps1` → `human/progress.html` |
