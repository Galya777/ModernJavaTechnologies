package bg.sofia.uni.fmi.mjt.olympics;

import bg.sofia.uni.fmi.mjt.olympics.comparator.NationMedalComparator;
import bg.sofia.uni.fmi.mjt.olympics.competition.Competition;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Medal;

import java.util.*;
import java.util.stream.Collectors;

public class MJTOlympics implements Olympics {
    private final Set<Competitor> competitors;
    private final Map<String, EnumMap<Medal, Integer>> nationsMedalTable;

    public MJTOlympics() {
        this.competitors = new HashSet<>();
        this.nationsMedalTable = new HashMap<>();
    }

    @Override
    public void updateMedalStatistics(Competition competition) {
        if (competition == null) {
            throw new IllegalArgumentException("Competition cannot be null");
        }

        List<Competitor> competitorsList = new ArrayList<>(competition.competitors());
        if (competitorsList.size() < 3) {
            throw new IllegalArgumentException("Not enough competitors to award medals");
        }

        // Ensure all competitors are registered
        for (Competitor competitor : competitorsList) {
            if (!competitors.contains(competitor)) {
                throw new IllegalArgumentException("Competitor " + competitor.getName() + " is not registered");
            }
        }

        // Award medals
        competitorsList.get(0).addMedal(Medal.GOLD);
        updateNationMedal(competitorsList.get(0).getNationality(), Medal.GOLD);

        competitorsList.get(1).addMedal(Medal.SILVER);
        updateNationMedal(competitorsList.get(1).getNationality(), Medal.SILVER);

        competitorsList.get(2).addMedal(Medal.BRONZE);
        updateNationMedal(competitorsList.get(2).getNationality(), Medal.BRONZE);
    }

    private void updateNationMedal(String nationality, Medal medal) {
        nationsMedalTable.putIfAbsent(nationality, new EnumMap<>(Medal.class));
        EnumMap<Medal, Integer> nationMedals = nationsMedalTable.get(nationality);
        nationMedals.put(medal, nationMedals.getOrDefault(medal, 0) + 1);
    }

    @Override
    public TreeSet<String> getNationsRankList() {
        TreeSet<String> rankedNations = new TreeSet<>(new NationMedalComparator(nationsMedalTable));
        rankedNations.addAll(nationsMedalTable.keySet());
        return rankedNations;
    }

    @Override
    public int getTotalMedals(String nationality) {
        if (nationality == null) {
            throw new IllegalArgumentException("Nationality cannot be null");
        }
        if (!nationsMedalTable.containsKey(nationality)) {
            throw new IllegalArgumentException("Nationality " + nationality + " is not registered");
        }

        return nationsMedalTable.get(nationality).values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    @Override
    public Map<String, EnumMap<Medal, Integer>> getNationsMedalTable() {
        return Collections.unmodifiableMap(nationsMedalTable);
    }

    @Override
    public Set<Competitor> getRegisteredCompetitors() {
        return Collections.unmodifiableSet(competitors);
    }

    /**
     * Registers a competitor to the Olympics.
     *
     * @param competitor the competitor to register
     * @throws IllegalArgumentException if the competitor is null
     */
    public void registerCompetitor(Competitor competitor) {
        if (competitor == null) {
            throw new IllegalArgumentException("Competitor cannot be null");
        }
        competitors.add(competitor);
    }
}
