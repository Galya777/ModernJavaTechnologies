package bg.sofia.uni.fmi.mjt.olympics.competitor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AthleteTest {
    private static final String ID = "A1";
    private static final String NAME = "John Doe";
    private static final String NATIONALITY = "USA";
    private static final String DIFFERENT_ID = "A2";
    
    private Athlete athlete;

    @Before
    public void setUp() {
        athlete = new Athlete(ID, NAME, NATIONALITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithNullId() {
        new Athlete(null, NAME, NATIONALITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithBlankId() {
        new Athlete(" ", NAME, NATIONALITY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddMedalWithNull() {
        athlete.addMedal(null);
    }

    @Test
    public void testAddMedal() {
        athlete.addMedal(Medal.GOLD);
        assertEquals(1, athlete.getMedals().size());
        assertTrue(athlete.getMedals().contains(Medal.GOLD));
    }

    @Test
    public void testGetMedalsReturnsUnmodifiableCollection() {
        athlete.addMedal(Medal.GOLD);
        try {
            athlete.getMedals().add(Medal.SILVER);
            fail("Should not be able to modify the medals collection directly");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
    }

    @Test
    public void testEqualsAndHashCode() {
        Athlete sameAthlete = new Athlete(ID, "Different Name", "Different Nationality");
        Athlete differentAthlete = new Athlete(DIFFERENT_ID, NAME, NATIONALITY);

        assertEquals(athlete, sameAthlete);
        assertEquals(athlete.hashCode(), sameAthlete.hashCode());
        assertNotEquals(athlete, differentAthlete);
    }
}
