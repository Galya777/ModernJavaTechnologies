package bg.sofia.uni.fmi.mjt.glovo.delivery;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;

public class DeliveryInfo {
    private final Location deliveryGuyLocation;
    private final double price;
    private final int estimatedTime;
    private final DeliveryType deliveryType;

    public DeliveryInfo(Location deliveryGuyLocation, double price, int estimatedTime, DeliveryType deliveryType) {
        this.deliveryGuyLocation = deliveryGuyLocation;
        this.price = price;
        this.estimatedTime = estimatedTime;
        this.deliveryType = deliveryType;
    }

    public Location getDeliveryGuyLocation() {
        return deliveryGuyLocation;
    }

    public double getPrice() {
        return price;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }
}
