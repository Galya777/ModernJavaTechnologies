package bg.sofia.uni.fmi.mjt.airbnb;

public interface Criterion {

    /**
     * @return true, if the bookable matches the criterion. If bookable is null, returns false.
     */
    boolean check(Bookable bookable);

}
