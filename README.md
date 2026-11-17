# Практическая работа №10 — вариант 4

Автор: Бешляга Никита, группа ЭФБО-17-25.

Система расчёта доставки на Java с интерфейсом `DeliveryPolicy`, тремя стратегиями, фабрикой и сервисом. Для варианта 4 минимальная стоимость EXPRESS равна 1500 ₽, а метод `addExpressOnly(DeliveryService, List<DeliveryRequest>)` возвращает только запросы, поддерживаемые экспресс-доставкой.

## Запуск

Требуется Java 17 или новее. Из корня проекта:

```bash
javac -encoding UTF-8 -d out src/ru/edu/pr07/*.java
java -cp out ru.edu.pr07.SelfCheck
java -cp out ru.edu.pr07.SelfCheck data
java -cp out ru.edu.pr07.SelfCheck interactive
```

Первые два режима должны показать `22/22 passed` и `12/12 passed`. Для macOS/Linux доступен `./run_selfcheck.sh`.

Актуальные исходники и изображения диаграмм находятся в `diagrams/`. Для пересборки PNG требуется Graphviz:

```bash
dot -Tpng -Gdpi=180 diagrams/activity.dot -o diagrams/activity.png
dot -Tpng -Gdpi=180 diagrams/class.dot -o diagrams/class.png
```

Описание решений — в `DECISIONS.md`, отчёт — в `REPORT10.docx`.
