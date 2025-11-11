package bg.sofia.uni.fmi.mjt.glovo.controlcenter;

import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.Location;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntity;
import bg.sofia.uni.fmi.mjt.glovo.controlcenter.map.MapEntityType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryInfo;
import bg.sofia.uni.fmi.mjt.glovo.delivery.DeliveryType;
import bg.sofia.uni.fmi.mjt.glovo.delivery.ShippingMethod;
import bg.sofia.uni.fmi.mjt.glovo.exception.InvalidOrderException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ControlCenter implements ControlCenterApi {
    private final MapEntity[][] layout;
    private final int rows;
    private final int cols;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public ControlCenter(char[][] mapLayout) {
        if (mapLayout == null || mapLayout.length == 0 || mapLayout[0].length == 0) {
            throw new IllegalArgumentException("Invalid map layout");
        }

        this.rows = mapLayout.length;
        this.cols = mapLayout[0].length;
        this.layout = new MapEntity[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char c = mapLayout[i][j];
                MapEntityType type = MapEntityType.fromSymbol(c);
                this.layout[i][j] = new MapEntity(new Location(i, j), type);
            }
        }
    }

    @Override
    public DeliveryInfo findOptimalDeliveryGuy(Location restaurantLocation, Location clientLocation,
                                             double maxPrice, int maxTime, ShippingMethod shippingMethod) {
        validateLocations(restaurantLocation, clientLocation);

        List<MapEntity> deliveryGuys = findDeliveryGuys();
        if (deliveryGuys.isEmpty()) {
            return null;
        }

        DeliveryInfo bestDelivery = null;

        for (MapEntity deliveryGuy : deliveryGuys) {
            DeliveryType type = deliveryGuy.getType() == MapEntityType.DELIVERY_GUY_CAR ? 
                              DeliveryType.CAR : DeliveryType.BIKE;

            // Calculate paths
            int toRestaurant = findShortestPath(deliveryGuy.getLocation(), restaurantLocation);
            int toClient = findShortestPath(restaurantLocation, clientLocation);

            if (toRestaurant == -1 || toClient == -1) {
                continue; // No path found
            }

            double price = (toRestaurant + toClient) * type.getPricePerKm();
            int time = (toRestaurant + toClient) * type.getTimePerKm();

            // Check constraints
            if ((maxPrice != -1 && price > maxPrice) || (maxTime != -1 && time > maxTime)) {
                continue;
            }

            DeliveryInfo current = new DeliveryInfo(deliveryGuy.getLocation(), price, time, type);

            if (bestDelivery == null || isBetterDelivery(current, bestDelivery, shippingMethod)) {
                bestDelivery = current;
            }
        }

        return bestDelivery;
    }

    private boolean isBetterDelivery(DeliveryInfo current, DeliveryInfo best, ShippingMethod method) {
        if (method == ShippingMethod.FASTEST) {
            return current.getEstimatedTime() < best.getEstimatedTime() ||
                  (current.getEstimatedTime() == best.getEstimatedTime() && 
                   current.getPrice() < best.getPrice());
        } else { // CHEAPEST
            return current.getPrice() < best.getPrice() ||
                  (current.getPrice() == best.getPrice() && 
                   current.getEstimatedTime() < best.getEstimatedTime());
        }
    }

    private List<MapEntity> findDeliveryGuys() {
        List<MapEntity> deliveryGuys = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                MapEntity entity = layout[i][j];
                if (entity.getType() == MapEntityType.DELIVERY_GUY_CAR || 
                    entity.getType() == MapEntityType.DELIVERY_GUY_BIKE) {
                    deliveryGuys.add(entity);
                }
            }
        }
        return deliveryGuys;
    }

    private int findShortestPath(Location start, Location end) {
        if (!isValidLocation(start) || !isValidLocation(end)) {
            return -1;
        }

        boolean[][] visited = new boolean[rows][cols];
        Queue<PathNode> queue = new ArrayDeque<>();
        queue.add(new PathNode(start, 0));
        visited[start.getX()][start.getY()] = true;

        while (!queue.isEmpty()) {
            PathNode current = queue.poll();
            Location loc = current.location;

            if (loc.equals(end)) {
                return current.distance;
            }

            for (int[] dir : DIRECTIONS) {
                int newX = loc.getX() + dir[0];
                int newY = loc.getY() + dir[1];

                if (isValidMove(newX, newY, visited)) {
                    visited[newX][newY] = true;
                    queue.add(new PathNode(new Location(newX, newY), current.distance + 1));
                }
            }
        }

        return -1; // No path found
    }

    private boolean isValidMove(int x, int y, boolean[][] visited) {
        return x >= 0 && x < rows && y >= 0 && y < cols && 
               layout[x][y].getType() != MapEntityType.WALL && 
               !visited[x][y];
    }

    private boolean isValidLocation(Location location) {
        int x = location.getX();
        int y = location.getY();
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    private void validateLocations(Location... locations) {
        for (Location loc : locations) {
            if (loc == null || !isValidLocation(loc)) {
                throw new IllegalArgumentException("Invalid location: " + loc);
            }
        }
    }

    @Override
    public MapEntity[][] getLayout() {
        MapEntity[][] copy = new MapEntity[rows][];
        for (int i = 0; i < rows; i++) {
            copy[i] = layout[i].clone();
        }
        return copy;
    }

    private static class PathNode {
        final Location location;
        final int distance;

        PathNode(Location location, int distance) {
            this.location = location;
            this.distance = distance;
        }
    }
}
