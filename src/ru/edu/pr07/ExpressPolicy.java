
package ru.edu.pr07;

public class ExpressPolicy implements DeliveryPolicy {

    @Override
    public boolean supports(DeliveryRequest req) {
        return req.type() == DeliveryType.EXPRESS
                && req.weightKg() <= 10.0;
    }

    @Override
    public double calculateCost(DeliveryRequest req) {
        return Math.max(1500.0, req.distanceKm() * 100.0)
                + req.orderAmount() * 0.01;
    }
}
