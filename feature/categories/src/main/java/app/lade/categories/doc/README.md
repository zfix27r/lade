# `categories`

## Tasks

Текущая волна: [tasks-v4.md](tasks-v4.md)  
Архив: [tasks-v1.md](tasks-v1.md), [tasks-v2.md](tasks-v2.md), [tasks-v3.md](tasks-v3.md)

## Экраны

| Экран | Назначение |
|-------|------------|
| `CategoriesList` | Список |
| `CategoryEdit` | Создание / изменение / архив |

## Части

| Часть | Назначение |
|-------|------------|
| domain | CategoryRepository, модель Category |
| data | Room impl |
| ui | list / edit / CategorySelector / CategoryColorPicker / CategoryColorIndicator |

## Термины

| Термин | eng | Смысл |
|--------|-----|--------|
| Категория | `Category` | Общая ось (спорт, сон); не частный вид активности |

## Документы

| Файл | О чём |
|------|--------|
| [model-category.md](model-category.md) | Category, CategoryKey, ColorKey |
