
package ru.edu.pr07;

public class DeliveryPolicyFactory {

    public DeliveryPolicy get(DeliveryType type) {
        return switch (type) {
            case STANDARD -> new StandardCourierPolicy();
            case EXPRESS -> new ExpressPolicy();
            case PICKUP -> new PickupPolicy();
        };
    }
}
