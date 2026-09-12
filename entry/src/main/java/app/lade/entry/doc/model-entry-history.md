# EntryHistory

Свершившаяся / зафиксированная пользователем запись на дату (и опционально время).  
Снимок состояния на момент записи; поздние правки Entry не переписывают History.

| Поле | Тип | Примечание |
|------|-----|------------|
| id | integer | autoincrement |
| entryId | integer? | FK → Entry; может остаться после архива Entry |
| kind | enum | снимок kind |
| title | string | снимок |
| categoryId | integer | снимок |
| date | localDate | |
| startTime | localTime? | слот, если был |
| endTime | localTime? | |
| result | enum? | см. статусы |
| goals | list snapshot | цели на момент фиксации (key, label, unit, target…) |
| actuals | list snapshot | факты (key, value, unit…) |
| notedAt | instant | когда зафиксировали |
| source | enum? | manual / chat / … |

## Когда писать

Только по **действию пользователя** (не по наступлению полуночи).

| Kind | History |
|------|---------|
| schedule | **не пишем** |
| task | при `done` или `cancelled`; до этого Entry висит и копится |
| habit | при `done` или `skipped` (+ actuals/goals) |
| event | при явной фиксации пользователя; до этого может жить как Entry |

## Статусы `result`

| Значение | Kind | Смысл |
|----------|------|--------|
| done | task, habit, event | сделал / выполнил |
| skipped | habit | пропуск (аналитика) |
| cancelled | task | отмена |
| null | — | не использовать как «авто-факт дня» |

## Связь с календарём

- Будущее / открытое: проекция Entry (+ маска одиночных).
- Факт отметки / закрытия: строки History.
- Проверка полного накрытия: History **и** проекция Entry на день.
