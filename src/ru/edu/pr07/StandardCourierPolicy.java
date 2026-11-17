
package ru.edu.pr07;

public class StandardCourierPolicy implements DeliveryPolicy {

    @Override
    public boolean supports(DeliveryRequest req) {
        return req.type() == DeliveryType.STANDARD
                && req.weightKg() <= 20.0;
    }

    @Override
    public double calculateCost(DeliveryRequest req) {
        return Math.max(500.0, req.distanceKm() * 50.0);
    }
}
