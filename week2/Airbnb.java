package bg.sofia.uni.fmi.mjt.airbnb;

public class Airbnb implements AirbnbAPI{

private Bookable[] accommodations;

    public Airbnb(Bookable[] accommodations) {
        this.accommodations = accommodations;
    }

    /**
     * Finds an accommodation by ID.
     *
     * @param id the unique ID of the bookable
     * @return the bookable with the specified id (the IDs are case-insensitive).
     * If the id is null or blank, or no accommodation with the specified id is found, return null.
     */
    @Override
    public Bookable findAccommodationById(String id) {
        if ((id == null) || id.isBlank()) {
            return null;
        }

        for (Bookable accommodation : accommodations) {
            if (accommodation.getId().equalsIgnoreCase(id)) {
                return accommodation;
            }
        }

        return null;
    }

    /**
     * Estimates the total revenue of all booked accommodations.
     *
     * @return the sum of total prices of stay for all booked accommodations.
     */
    @Override
    public double estimateTotalRevenue() {
        double sum=0;

        for (Bookable accommodation : accommodations) {
           sum+=accommodation.getTotalPriceOfStay();
        }

        return sum;
    }

    /**
     * @return the number of booked accommodations.
     */
    @Override
    public long countBookings() {
        return accommodations.length;
    }

    /**
     * Filters the accommodations by given criteria
     *
     * @param criteria the criteria to apply
     * @return an array of accommodations matching the specified criteria
     */
    @Override
    public Bookable[] filterAccommodations(Criterion... criteria) {
        int countMatching = 0;
        for (int i = 0; i < accommodations.length; i++) {
            if (matchCriteria(accommodations[i], criteria)) {
                countMatching++;
            }
        }

        Bookable[] included = new Bookable[countMatching];
        int resultIndex = 0;
        for (int i = 0; i < accommodations.length; i++) {
            if (matchCriteria(accommodations[i], criteria)) {
                included[resultIndex++] = accommodations[i];
            }
        }

        return included;
    }

    private boolean matchCriteria(Bookable accommodation, Criterion[] criteria) {
        for (Criterion criterion : criteria) {
            if ((criterion != null) && !criterion.check(accommodation)) {
                return false;
            }
        }

        return true;
    }
}
