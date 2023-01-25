package bg.sofia.uni.fmi.mjt.airbnb;

public class PriceCriterion implements Criterion {

   private double minPrice;
   private double maxPrice;

    public PriceCriterion(double minPrice, double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
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

        return bookable.getPricePerNight() >= minPrice && bookable.getPricePerNight() <= maxPrice;
    }
}
