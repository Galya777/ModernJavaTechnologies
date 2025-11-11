package bg.sofia.uni.fmi.mjt.olympics.competition;

import bg.sofia.uni.fmi.mjt.olympics.competitor.Athlete;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class CompetitionTest {
    private static final String COMPETITION_NAME = "100m Sprint";
    private static final String DISCIPLINE = "Athletics";
    
    private Set<Competitor> competitors;
    private Competition competition;

    @Before
    public void setUp() {
        competitors = new HashSet<>();
        competitors.add(new Athlete("A1", "Usain Bolt", "JAM"));
        competitors.add(new Athlete("A2", "Justin Gatlin", "USA"));
        competitors.add(new Athlete("A3", "Andre De Grasse", "CAN"));
        
        competition = new Competition(COMPETITION_NAME, DISCIPLINE, competitors);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNameInConstructor() {
        new Competition(null, DISCIPLINE, competitors);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDisciplineInConstructor() {
        new Competition(COMPETITION_NAME, null, competitors);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullCompetitorsInConstructor() {
        new Competition(COMPETITION_NAME, DISCIPLINE, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyCompetitorsInConstructor() {
        new Competition(COMPETITION_NAME, DISCIPLINE, new HashSet<>());
    }

    @Test
    public void testCompetitorsAreDefensivelyCopied() {
        // Try to modify the original set
        competitors.add(new Athlete("A4", "Tyson Gay", "USA"));
        
        // The competition's competitors should not change
        assertEquals(3, competition.competitors().size());
    }

    @Test
    public void testCompetitorsIsUnmodifiable() {
        try {
            competition.competitors().add(new Athlete("A4", "Tyson Gay", "USA"));
            fail("Should not be able to modify the competitors set directly");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void testGetters() {
        assertEquals(COMPETITION_NAME, competition.name());
        assertEquals(DISCIPLINE, competition.discipline());
        assertEquals(3, competition.competitors().size());
    }
}
