package bg.sofia.uni.fmi.mjt.glovo.delivery;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;

public class Delivery {
    private final Location client;
    private final Location restaurant;
    private final Location deliveryGuy;
    private final String foodItem;
    private final double price;
    private final int estimatedTime;

    public Delivery(Location client, Location restaurant, Location deliveryGuy, 
                   String foodItem, double price, int estimatedTime) {
        this.client = client;
        this.restaurant = restaurant;
        this.deliveryGuy = deliveryGuy;
        this.foodItem = foodItem;
        this.price = price;
        this.estimatedTime = estimatedTime;
    }

    public Location getClient() {
        return client;
    }

    public Location getRestaurant() {
        return restaurant;
    }

    public Location getDeliveryGuy() {
        return deliveryGuy;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public double getPrice() {
        return price;
    }

    public int getEstimatedTime() {
        return estimatedTime;
    }
}
