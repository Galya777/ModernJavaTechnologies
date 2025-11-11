package bg.sofia.uni.fmi.mjt.olympics.comparator;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.olympics.competitor.Medal;

public class NationMedalComparator implements Comparator<String> {
    private final Map<String, EnumMap<Medal, Integer>> medalTable;

    public NationMedalComparator(Map<String, EnumMap<Medal, Integer>> medalTable) {
        this.medalTable = medalTable;
    }

    @Override
    public int compare(String nation1, String nation2) {
        if (nation1 == null || nation2 == null) {
            throw new IllegalArgumentException("Nation cannot be null");
        }

        EnumMap<Medal, Integer> medals1 = medalTable.get(nation1);
        EnumMap<Medal, Integer> medals2 = medalTable.get(nation2);

        int totalMedals1 = medals1.values().stream().mapToInt(Integer::intValue).sum();
        int totalMedals2 = medals2.values().stream().mapToInt(Integer::intValue).sum();

        // Sort by total medals in descending order
        if (totalMedals1 != totalMedals2) {
            return Integer.compare(totalMedals2, totalMedals1);
        }

        // If total medals are equal, sort by gold medals
        int gold1 = medals1.getOrDefault(Medal.GOLD, 0);
        int gold2 = medals2.getOrDefault(Medal.GOLD, 0);
        if (gold1 != gold2) {
            return Integer.compare(gold2, gold1);
        }

        // If gold medals are equal, sort by silver medals
        int silver1 = medals1.getOrDefault(Medal.SILVER, 0);
        int silver2 = medals2.getOrDefault(Medal.SILVER, 0);
        if (silver1 != silver2) {
            return Integer.compare(silver2, silver1);
        }

        // If silver medals are equal, sort by bronze medals
        int bronze1 = medals1.getOrDefault(Medal.BRONZE, 0);
        int bronze2 = medals2.getOrDefault(Medal.BRONZE, 0);
        if (bronze1 != bronze2) {
            return Integer.compare(bronze2, bronze1);
        }

        // If all medal counts are equal, sort alphabetically
        return nation1.compareTo(nation2);
    }
}
