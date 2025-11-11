package bg.sofia.uni.fmi.mjt.olympics;

import bg.sofia.uni.fmi.mjt.olympics.competition.Competition;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Athlete;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Medal;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class MJTOlympicsTest {
    private static final String COMPETITION_NAME = "100m Sprint";
    private static final String DISCIPLINE = "Athletics";
    
    private MJTOlympics olympics;
    private Competitor usainBolt;
    private Competitor justinGatlin;
    private Competitor andreDeGrasse;
    private Set<Competitor> competitors;
    
    @Before
    public void setUp() {
        olympics = new MJTOlympics();
        
        // Create some test competitors
        usainBolt = new Athlete("A1", "Usain Bolt", "JAM");
        justinGatlin = new Athlete("A2", "Justin Gatlin", "USA");
        andreDeGrasse = new Athlete("A3", "Andre De Grasse", "CAN");
        
        // Register competitors
        olympics.registerCompetitor(usainBolt);
        olympics.registerCompetitor(justinGatlin);
        olympics.registerCompetitor(andreDeGrasse);
        
        // Create a set of competitors for a competition
        competitors = new HashSet<>();
        competitors.add(usainBolt);
        competitors.add(justinGatlin);
        competitors.add(andreDeGrasse);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegisterNullCompetitor() {
        olympics.registerCompetitor(null);
    }

    @Test
    public void testRegisterCompetitor() {
        Competitor newAthlete = new Athlete("A4", "Tyson Gay", "USA");
        olympics.registerCompetitor(newAthlete);
        assertTrue(olympics.getRegisteredCompetitors().contains(newAthlete));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateMedalStatisticsWithNullCompetition() {
        olympics.updateMedalStatistics(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateMedalStatisticsWithUnregisteredCompetitor() {
        Competitor unregistered = new Athlete("A5", "Unknown", "UNK");
        Set<Competitor> invalidCompetitors = new HashSet<>();
        invalidCompetitors.add(unregistered);
        Competition invalidCompetition = new Competition("Invalid", "Invalid", invalidCompetitors);
        
        olympics.updateMedalStatistics(invalidCompetition);
    }

    @Test
    public void testUpdateMedalStatistics() {
        // Create a competition
        Competition competition = new Competition(COMPETITION_NAME, DISCIPLINE, competitors);
        
        // Update medal statistics
        olympics.updateMedalStatistics(competition);
        
        // Verify medals were awarded correctly
        assertTrue(usainBolt.getMedals().contains(Medal.GOLD));
        assertTrue(justinGatlin.getMedals().contains(Medal.SILVER));
        assertTrue(andreDeGrasse.getMedals().contains(Medal.BRONZE));
        
        // Verify nation medal counts
        assertEquals(1, (int) olympics.getNationsMedalTable().get("JAM").get(Medal.GOLD));
        assertEquals(1, (int) olympics.getNationsMedalTable().get("USA").get(Medal.SILVER));
        assertEquals(1, (int) olympics.getNationsMedalTable().get("CAN").get(Medal.BRONZE));
    }

    @Test
    public void testGetNationsRankList() {
        // First competition
        Competition competition1 = new Competition("100m", "Athletics", competitors);
        olympics.updateMedalStatistics(competition1);
        
        // Second competition with different results
        Set<Competitor> competitors2 = new HashSet<>();
        competitors2.add(justinGatlin);
        competitors2.add(usainBolt);
        competitors2.add(andreDeGrasse);
        Competition competition2 = new Competition("200m", "Athletics", competitors2);
        olympics.updateMedalStatistics(competition2);
        
        // Get the rank list
        var rankList = new ArrayList<>(olympics.getNationsRankList());
        
        // USA should be first (1 Gold, 1 Silver)
        assertEquals("USA", rankList.get(0));
        // JAM should be second (1 Gold, 1 Silver, but USA comes first alphabetically with same medal count)
        assertEquals("JAM", rankList.get(1));
        // CAN should be third (2 Bronze)
        assertEquals("CAN", rankList.get(2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTotalMedalsWithNullNationality() {
        olympics.getTotalMedals(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTotalMedalsWithUnregisteredNationality() {
        olympics.getTotalMedals("XYZ");
    }

    @Test
    public void testGetTotalMedals() {
        // Add some medals
        Competition competition = new Competition(COMPETITION_NAME, DISCIPLINE, competitors);
        olympics.updateMedalStatistics(competition);
        
        // Verify total medals
        assertEquals(1, olympics.getTotalMedals("JAM"));
        assertEquals(1, olympics.getTotalMedals("USA"));
        assertEquals(1, olympics.getTotalMedals("CAN"));
    }

    @Test
    public void testGetNationsMedalTable() {
        // Add some medals
        Competition competition = new Competition(COMPETITION_NAME, DISCIPLINE, competitors);
        olympics.updateMedalStatistics(competition);
        
        var medalTable = olympics.getNationsMedalTable();
        
        // Verify the medal table is unmodifiable
        try {
            medalTable.put("TEST", new EnumMap<>(Medal.class));
            fail("Should not be able to modify the medal table directly");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        
        // Verify the medal counts
        assertEquals(1, (int) medalTable.get("JAM").get(Medal.GOLD));
        assertEquals(1, (int) medalTable.get("USA").get(Medal.SILVER));
        assertEquals(1, (int) medalTable.get("CAN").get(Medal.BRONZE));
    }

    @Test
    public void testGetRegisteredCompetitors() {
        var registered = olympics.getRegisteredCompetitors();
        
        // Verify the set is unmodifiable
        try {
            registered.add(new Athlete("A4", "Test", "TEST"));
            fail("Should not be able to modify the registered competitors set directly");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        
        // Verify the competitors are in the set
        assertEquals(3, registered.size());
        assertTrue(registered.contains(usainBolt));
        assertTrue(registered.contains(justinGatlin));
        assertTrue(registered.contains(andreDeGrasse));
    }
}
