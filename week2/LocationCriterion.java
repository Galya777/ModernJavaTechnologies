package bg.sofia.uni.fmi.mjt.airbnb;

public class LocationCriterion implements Criterion{
   private Location currentLocation;
   private double maxDistance;

    public LocationCriterion(Location currentLocation, double maxDistance) {
        this.currentLocation = currentLocation;
        this.maxDistance = maxDistance;
    }

    /**
     * @param bookable
     * @return true, if the bookable matches the criterion. If bookable is null, returns false.
     */
    @Override
    public boolean check(Bookable bookable) {
        if (bookable == null) {
            return false;
        }

        Location bookableLocation = bookable.getLocation();
        return currentLocation.distanceTo(bookableLocation) <= maxDistance;
    }
}
