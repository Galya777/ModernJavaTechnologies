package bg.sofia.uni.fmi.mjt.olympics.competition;

import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @throws IllegalArgumentException when the competition name is null or blank
 * @throws IllegalArgumentException when the competition discipline is null or blank
 * @throws IllegalArgumentException when the competition's competitors is null or empty
 */
public record Competition(String name, String discipline, Set<Competitor> competitors) {
    
    public Competition {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Competition name cannot be null or blank");
        }
        if (discipline == null || discipline.isBlank()) {
            throw new IllegalArgumentException("Discipline cannot be null or blank");
        }
        if (competitors == null || competitors.isEmpty()) {
            throw new IllegalArgumentException("Competitors cannot be null or empty");
        }
        
        // Defensive copy of the competitors set
        competitors = Set.copyOf(competitors);
    }
    
    /**
     * Returns an unmodifiable view of the competitors in this competition.
     */
    public Set<Competitor> competitors() {
        return Collections.unmodifiableSet(competitors);
    }
}
