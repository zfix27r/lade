# `devices`

## Tasks

Текущая волна: [tasks-v1.md](tasks-v1.md)

## Термины

| Термин | eng | Смысл |
|--------|-----|--------|
| Проба Health | `HealthSample` | Нормализованная запись из Health |

## Порт

```
DeviceHealthGateway
  refreshAvailability()
  connect() / disconnect()
  peekRecent()
```

## Правила

1. Health → Gateway → Room.
2. UI не знает SDK.
3. Отключение не удаляет ручные данные.
4. Импорт не перекрывает `manual` / schedule.
5. Идемпотентность `(provider, externalId)`.
