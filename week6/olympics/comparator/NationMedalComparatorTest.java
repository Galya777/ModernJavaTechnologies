package bg.sofia.uni.fmi.mjt.olympics.comparator;

import bg.sofia.uni.fmi.mjt.olympics.competitor.Medal;
import org.junit.Before;
import org.junit.Test;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class NationMedalComparatorTest {
    private Map<String, EnumMap<Medal, Integer>> medalTable;
    private NationMedalComparator comparator;

    @Before
    public void setUp() {
        medalTable = new HashMap<>();
        
        // USA: 2 Gold, 1 Silver, 1 Bronze
        EnumMap<Medal, Integer> usaMedals = new EnumMap<>(Medal.class);
        usaMedals.put(Medal.GOLD, 2);
        usaMedals.put(Medal.SILVER, 1);
        usaMedals.put(Medal.BRONZE, 1);
        medalTable.put("USA", usaMedals);
        
        // CHN: 2 Gold, 0 Silver, 2 Bronze
        EnumMap<Medal, Integer> chnMedals = new EnumMap<>(Medal.class);
        chnMedals.put(Medal.GOLD, 2);
        chnMedals.put(Medal.BRONZE, 2);
        medalTable.put("CHN", chnMedals);
        
        // JPN: 1 Gold, 2 Silver, 1 Bronze
        EnumMap<Medal, Integer> jpnMedals = new EnumMap<>(Medal.class);
        jpnMedals.put(Medal.GOLD, 1);
        jpnMedals.put(Medal.SILVER, 2);
        jpnMedals.put(Medal.BRONZE, 1);
        medalTable.put("JPN", jpnMedals);
        
        // GBR: 1 Gold, 2 Silver, 0 Bronze
        EnumMap<Medal, Integer> gbrMedals = new EnumMap<>(Medal.class);
        gbrMedals.put(Medal.GOLD, 1);
        gbrMedals.put(Medal.SILVER, 2);
        medalTable.put("GBR", gbrMedals);
        
        // AUS: 1 Gold, 1 Silver, 1 Bronze
        EnumMap<Medal, Integer> ausMedals = new EnumMap<>(Medal.class);
        ausMedals.put(Medal.GOLD, 1);
        ausMedals.put(Medal.SILVER, 1);
        ausMedals.put(Medal.BRONZE, 1);
        medalTable.put("AUS", ausMedals);
        
        comparator = new NationMedalComparator(medalTable);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareWithNullNation1() {
        comparator.compare(null, "USA");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompareWithNullNation2() {
        comparator.compare("USA", null);
    }

    @Test
    public void testCompareByTotalMedals() {
        // USA (4) vs JPN (4) - same total, USA has more gold
        assertTrue(comparator.compare("USA", "JPN") < 0);
        
        // CHN (4) vs JPN (4) - same total, CHN has more gold
        assertTrue(comparator.compare("CHN", "JPN") < 0);
        
        // JPN (4) vs GBR (3) - JPN has more total
        assertTrue(comparator.compare("JPN", "GBR") < 0);
    }

    @Test
    public void testCompareByGoldMedals() {
        // USA (2) vs CHN (2) - same gold, USA has more silver
        assertTrue(comparator.compare("USA", "CHN") < 0);
    }

    @Test
    public void testCompareBySilverMedals() {
        // JPN (2) vs GBR (2) - same gold and silver, JPN has more bronze
        assertTrue(comparator.compare("JPN", "GBR") < 0);
    }

    @Test
    public void testCompareByAlphabeticalOrder() {
        // AUS (3) vs GBR (3) - same medals, AUS comes before GBR alphabetically
        assertTrue(comparator.compare("AUS", "GBR") < 0);
        assertTrue(comparator.compare("GBR", "AUS") > 0);
    }

    @Test
    public void testEqualNations() {
        assertEquals(0, comparator.compare("USA", "USA"));
    }
}
