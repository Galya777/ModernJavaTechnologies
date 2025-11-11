package bg.sofia.uni.fmi.mjt.olympics.competitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Athlete implements Competitor {
    private final String identifier;
    private final String name;
    private final String nationality;
    private final List<Medal> medals;

    public Athlete(String identifier, String name, String nationality) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Identifier cannot be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (nationality == null || nationality.isBlank()) {
            throw new IllegalArgumentException("Nationality cannot be null or blank");
        }
        
        this.identifier = identifier;
        this.name = name;
        this.nationality = nationality;
        this.medals = new ArrayList<>();
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNationality() {
        return nationality;
    }

    @Override
    public Collection<Medal> getMedals() {
        return Collections.unmodifiableList(medals);
    }

    @Override
    public void addMedal(Medal medal) {
        if (medal == null) {
            throw new IllegalArgumentException("Medal cannot be null");
        }
        medals.add(medal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Athlete athlete = (Athlete) o;
        return identifier.equals(athlete.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}
