
package ru.edu.pr07;

import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;

public class SelfCheck {

    private static int passed = 0;

    // Вывод результата unit-проверки
    private static void check(int number, boolean ok) {
        if (ok) {
            System.out.println("PASS " + number);
            passed++;
        } else {
            System.out.println("FAIL " + number);
        }
    }

    // Сравнение дробных чисел
    private static boolean equal(double actual, double expected) {
        return Math.abs(actual - expected) < 0.001;
    }

    public static void main(String[] args) {

        // Режим чтения CSV
        if (args.length > 0 && args[0].equals("data")) {
            runDataDriven();
            return;
        }

        // Интерактивный режим
        if (args.length > 0 && args[0].equals("interactive")) {
            runInteractive();
            return;
        }

        // ===== UNIT-ПРОВЕРКИ =====

        PickupPolicy pickup = new PickupPolicy();
        StandardCourierPolicy standard = new StandardCourierPolicy();
        ExpressPolicy express = new ExpressPolicy();
        DeliveryPolicyFactory factory = new DeliveryPolicyFactory();
        DeliveryService service = new DeliveryService(factory);

        try {

            // 1. Самовывоз поддерживает запрос
            DeliveryRequest p1 = new DeliveryRequest(
                    DeliveryType.PICKUP, 0, 10, 1000);
            check(1, pickup.supports(p1));

            // 2. Самовывоз бесплатный
            check(2, equal(pickup.calculateCost(p1), 0.0));

            // 3. STANDARD: вес 15 кг разрешен
            DeliveryRequest s1 = new DeliveryRequest(
                    DeliveryType.STANDARD, 10, 15, 5000);
            check(3, standard.supports(s1));

            // 4. STANDARD: другой тип не поддерживается
            DeliveryRequest s2 = new DeliveryRequest(
                    DeliveryType.EXPRESS, 10, 5, 5000);
            check(4, !standard.supports(s2));

            // 5. STANDARD: вес 25 кг запрещен
            DeliveryRequest s3 = new DeliveryRequest(
                    DeliveryType.STANDARD, 10, 25, 5000);
            check(5, !standard.supports(s3));

            // 6. STANDARD: стоимость для 12 км = 600
            DeliveryRequest s4 = new DeliveryRequest(
                    DeliveryType.STANDARD, 12, 4.5, 12000);
            check(6, equal(standard.calculateCost(s4), 600.0));

            // 7. STANDARD: минимальная стоимость 500
            DeliveryRequest s5 = new DeliveryRequest(
                    DeliveryType.STANDARD, 5, 1, 500);
            check(7, equal(standard.calculateCost(s5), 500.0));

            // 8. EXPRESS: вес 5 кг разрешен
            DeliveryRequest e1 = new DeliveryRequest(
                    DeliveryType.EXPRESS, 5, 5, 4000);
            check(8, express.supports(e1));

            // 9. EXPRESS: вес 15 кг запрещен
            DeliveryRequest e2 = new DeliveryRequest(
                    DeliveryType.EXPRESS, 5, 15, 3000);
            check(9, !express.supports(e2));

            // 10. EXPRESS: стоимость = 1540 при новом минимуме 1500
            DeliveryRequest e3 = new DeliveryRequest(
                    DeliveryType.EXPRESS, 5, 1.2, 4000);
            check(10, equal(express.calculateCost(e3), 1540.0));

            // 11. EXPRESS: стоимость = 2100
            DeliveryRequest e4 = new DeliveryRequest(
                    DeliveryType.EXPRESS, 20, 5, 10000);
            check(11, equal(express.calculateCost(e4), 2100.0));

            // 12. Factory выбирает StandardCourierPolicy
            check(12, factory.get(DeliveryType.STANDARD)
                    instanceof StandardCourierPolicy);

            // 13. Factory выбирает ExpressPolicy
            check(13, factory.get(DeliveryType.EXPRESS)
                    instanceof ExpressPolicy);

            // 14. Factory выбирает PickupPolicy
            check(14, factory.get(DeliveryType.PICKUP)
                    instanceof PickupPolicy);

            // 15. Service: стандартная доставка = 600
            check(15, equal(service.calculate(s4), 600.0));

            // 16. Service: превышение веса вызывает исключение
            boolean exceptionThrown = false;

            try {
                service.calculate(s3);
            } catch (IllegalArgumentException e) {
                exceptionThrown = true;
            }

            check(16, exceptionThrown);

            // 17. Service: самовывоз = 0
            check(17, equal(service.calculate(p1), 0.0));

            // 18. Service: экспресс-доставка = 1540
            check(18, equal(service.calculate(e3), 1540.0));

            // ===== ВАРИАНТ 4. ЧАСТЬ А =====

            // 19. EXPRESS: при малой дистанции действует минимум 1500
            DeliveryRequest v1 = new DeliveryRequest(
                    DeliveryType.EXPRESS, 0, 10, 0);
            check(19, equal(service.calculate(v1), 1500.0));

            // 20. EXPRESS: при большой дистанции действует тариф за км
            DeliveryRequest v2 = new DeliveryRequest(
                    DeliveryType.EXPRESS, 20, 10, 10000);
            check(20, equal(service.calculate(v2), 2100.0));

            // ===== ВАРИАНТ 4. ЧАСТЬ Б =====

            // 21. Фильтр оставляет только поддерживаемые EXPRESS-запросы
            List<DeliveryRequest> requests = List.of(
                    e3,
                    s4,
                    new DeliveryRequest(DeliveryType.EXPRESS, 5, 10.1, 3000),
                    v1
            );
            check(21, service.addExpressOnly(service, requests)
                    .equals(List.of(e3, v1)));

            // 22. Для пустого списка результат тоже пуст
            check(22, service.addExpressOnly(service, List.of()).isEmpty());

        } catch (Exception e) {
            System.out.println("FAIL exception: " + e.getMessage());
        }

        System.out.println(
                "Unit checks: " + passed + "/22 passed");
    }

    // ===== DATA-DRIVEN РЕЖИМ =====

    private static void runDataDriven() {

        DeliveryService service = new DeliveryService(
                new DeliveryPolicyFactory());

        int passedData = 0;
        int totalData = 0;

        Path path = Path.of("data", "sample.csv");

        try (BufferedReader reader = Files.newBufferedReader(
                path, StandardCharsets.UTF_8)) {

            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {

                // Пропускаем пустые строки и комментарии
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Пропускаем заголовок CSV
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                totalData++;

                try {
                    String[] parts = line.split(";", -1);

                    if (parts.length != 5) {
                        throw new IllegalArgumentException(
                                "Expected 5 columns");
                    }

                    DeliveryType type =
                            DeliveryType.valueOf(parts[0]);

                    double distance =
                            Double.parseDouble(parts[1]);

                    double weight =
                            Double.parseDouble(parts[2]);

                    double amount =
                            Double.parseDouble(parts[3]);

                    String expected = parts[4];

                    DeliveryRequest req = new DeliveryRequest(
                            type, distance, weight, amount);

                    boolean ok = false;

                    try {
                        double actual = service.calculate(req);

                        if (expected.startsWith("OK:cost=")) {
                            double expectedCost =
                                    Double.parseDouble(
                                            expected.substring(8));

                            ok = equal(actual, expectedCost);
                        }

                    } catch (IllegalArgumentException e) {

                        // Ожидаемый отказ из-за превышения веса
                        if (expected.equals(
                                "SKIP:weight exceeds limit")) {

                            if (type == DeliveryType.STANDARD
                                    && weight > 20.0) {
                                ok = true;
                            }

                            if (type == DeliveryType.EXPRESS
                                    && weight > 10.0) {
                                ok = true;
                            }
                        }
                    }

                    if (ok) {
                        System.out.println(
                                "PASS data/" + totalData);
                        passedData++;
                    } else {
                        System.out.println(
                                "FAIL data/" + totalData);
                    }

                } catch (Exception e) {
                    System.out.println(
                            "FAIL data/" + totalData
                                    + ": " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println(
                    "FAIL CSV: " + e.getMessage());
            return;
        }

        System.out.println("Data-driven: "
                + passedData + "/" + totalData + " passed");
    }

    // ===== ИНТЕРАКТИВНЫЙ РЕЖИМ =====

    private static void runInteractive() {

        Scanner scanner = new Scanner(System.in);

        DeliveryService service = new DeliveryService(
                new DeliveryPolicyFactory());

        System.out.println("=== INTERACTIVE MODE ===");
        System.out.println("Types: STANDARD, EXPRESS, PICKUP");
        System.out.println("Type exit or press Enter to stop.");

        while (true) {

            System.out.print("\nDelivery type: ");
            String typeInput = scanner.nextLine().trim();

            // Завершение интерактивного режима
            if (typeInput.isEmpty()
                    || typeInput.equalsIgnoreCase("exit")) {
                break;
            }

            try {
                DeliveryType type =
                        DeliveryType.valueOf(typeInput);

                System.out.print("Distance (km): ");
                double distance = Double.parseDouble(
                        scanner.nextLine().trim());

                System.out.print("Weight (kg): ");
                double weight = Double.parseDouble(
                        scanner.nextLine().trim());

                System.out.print("Order amount: ");
                double amount = Double.parseDouble(
                        scanner.nextLine().trim());

                DeliveryRequest request = new DeliveryRequest(
                        type, distance, weight, amount);

                double cost = service.calculate(request);

                System.out.println("Delivery cost: " + cost);

            } catch (IllegalArgumentException e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }

        System.out.println("Interactive mode finished.");
    }
}
