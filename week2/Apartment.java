package bg.sofia.uni.fmi.mjt.airbnb;

import java.time.LocalDateTime;

public class Apartment extends BookableAccommodation {

    private static final String APARTMENT = "Apartment";

    private static long id_prefix;

    public Apartment(Location location, double pricePerNight) {
        super(APARTMENT, location, pricePerNight);
        ++id_prefix;
    }

    @Override
    protected long getIdSuffix() {
        return id_prefix;
    }

}
