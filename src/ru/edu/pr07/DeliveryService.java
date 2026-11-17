
package ru.edu.pr07;

import java.util.List;

public class DeliveryService {

    private final DeliveryPolicyFactory factory;

    public DeliveryService(DeliveryPolicyFactory f) {
        factory = f;
    }

    public double calculate(DeliveryRequest r) {
        DeliveryPolicy p = factory.get(r.type());

        if (!p.supports(r)) {
            throw new IllegalArgumentException("Unsupported delivery");
        }

        return p.calculateCost(r);
    }

    // Вариант 4, часть Б: сохраняем только запросы, доступные для EXPRESS.
    public List<DeliveryRequest> addExpressOnly(
            DeliveryService service, List<DeliveryRequest> requests) {
        DeliveryPolicy express = service.factory.get(DeliveryType.EXPRESS);
        List<DeliveryRequest> result = new java.util.ArrayList<>();
        for (DeliveryRequest request : requests) {
            if (express.supports(request)) {
                result.add(request);
            }
        }
        return result;
    }
}
