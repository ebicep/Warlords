package com.ebicep.warlords.database.leaderboards.stats;

import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.bukkit.Location;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Leaderboard {

    private final String title;
    private final Location location;
    private final TreeSet<DatabasePlayer> sortedAllTime;
    private final TreeSet<DatabasePlayer> sortedSeason6;
    private final TreeSet<DatabasePlayer> sortedSeason5;
    private final TreeSet<DatabasePlayer> sortedSeason4;
    private final TreeSet<DatabasePlayer> sortedWeekly;
    private final TreeSet<DatabasePlayer> sortedDaily;
    private final Function<DatabasePlayer, Number> valueFunction;
    private final Function<DatabasePlayer, String> stringFunction;

    public Leaderboard(String title, Location location, Function<DatabasePlayer, Number> valueFunction, Function<DatabasePlayer, String> stringFunction) {
        this.title = title;
        this.location = location;
        this.valueFunction = valueFunction;
        this.stringFunction = stringFunction;
        Comparator<DatabasePlayer> comparator = (o1, o2) -> {
            if (o1.getUuid().equals(o2.getUuid())) return 0;
            BigDecimal value1 = new BigDecimal(valueFunction.apply(o1).toString());
            BigDecimal value2 = new BigDecimal(valueFunction.apply(o2).toString());
            return value2.compareTo(value1);
        };
        this.sortedAllTime = new TreeSet<>(comparator);
        this.sortedSeason6 = new TreeSet<>(comparator);
        this.sortedSeason5 = new TreeSet<>(comparator);
        this.sortedSeason4 = new TreeSet<>(comparator);
        this.sortedWeekly = new TreeSet<>(comparator);
        this.sortedDaily = new TreeSet<>(comparator);
    }

    public static int compare(Number a, Number b) {
        return new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Leaderboard that = (Leaderboard) o;
        return Objects.equals(title, that.title) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, location);
    }

    public TreeSet<DatabasePlayer> getSortedPlayers(PlayersCollections collections) {
        switch (collections) {
            case LIFETIME:
                return sortedAllTime;
            case SEASON_6:
                return sortedSeason6;
            case SEASON_5:
                return sortedSeason5;
            case SEASON_4:
                return sortedSeason4;
            case WEEKLY:
                return sortedWeekly;
            case DAILY:
                return sortedDaily;
        }
        return null;
    }

    public void resetSortedPlayers(Set<DatabasePlayer> newSortedPlayers, PlayersCollections collections) {
        TreeSet<DatabasePlayer> sortedPlayers = getSortedPlayers(collections);
        sortedPlayers.removeAll(newSortedPlayers);
        sortedPlayers.addAll(newSortedPlayers);
    }

    public <T extends Number> T[] getTopThreeValues() {
        //current top value to compare to
        Number topValue = valueFunction.apply(sortedWeekly.first());

        Class<T> clazz = (Class<T>) topValue.getClass();
        //ouput array of type clazz
        T[] output = (T[]) Array.newInstance(clazz, 3);
        //first top number is current top
        output[0] = (T) topValue;

        List<Number> topThree = new ArrayList<>();
        int counter = 0;
        //looping to get the next top two numbers
        //filtering out all players with 3 or less games from leaderboards if the top player has 10 or more (no one game olivers)
        boolean filter = sortedWeekly.first().getPlays() >= 10;
        List<DatabasePlayer> databasePlayers;
        if (filter) {
            databasePlayers = sortedWeekly.stream()
                    .filter(databasePlayer -> databasePlayer.getPlays() > 3)
                    .collect(Collectors.toList());
        } else {
            databasePlayers = new ArrayList<>(sortedWeekly);
        }

        for (DatabasePlayer databasePlayer : databasePlayers) {
            //must have more than 3 plays to get awarded
            if (databasePlayer.getPlays() <= 3) continue;

            Number currentTopValue = valueFunction.apply(databasePlayer);
            if (counter < 2) {
                if (compare(topValue, currentTopValue) > 0) {
                    topThree.add(currentTopValue);
                    topValue = currentTopValue;
                    counter++;
                }
            } else {
                break;
            }
        }

        //adding last two top numbers
        for (int i = 0; i < topThree.size(); i++) {
            output[i + 1] = (T) topThree.get(i);
        }

        return output;
    }

    public String[] getTopThreePlayerNames(Number[] numbers, Function<DatabasePlayer, String> function) {
        String[] topThreePlayers = new String[3];
        Arrays.fill(topThreePlayers, "");

        //matching top value with players
        for (int i = 0; i < numbers.length; i++) {
            Number topValue = numbers[i];
            for (DatabasePlayer databasePlayer : sortedWeekly) {
                if (Objects.equals(valueFunction.apply(databasePlayer), topValue)) {
                    topThreePlayers[i] = topThreePlayers[i] + function.apply(databasePlayer) + ",";
                }
            }
            if (i == 2) {
                break;
            }
        }

        //removing end comma
        for (int i = 0; i < topThreePlayers.length; i++) {
            if (topThreePlayers[i].length() > 0) {
                topThreePlayers[i] = topThreePlayers[i].substring(0, topThreePlayers[i].length() - 1);
            }
        }
        return topThreePlayers;
    }

    public String getTitle() {
        return title;
    }

    public Location getLocation() {
        return location;
    }

    public TreeSet<DatabasePlayer> getSortedAllTime() {
        return sortedAllTime;
    }

    public TreeSet<DatabasePlayer> getSortedSeason6() {
        return sortedSeason6;
    }

    public TreeSet<DatabasePlayer> getSortedSeason5() {
        return sortedSeason5;
    }

    public TreeSet<DatabasePlayer> getSortedSeason4() {
        return sortedSeason4;
    }

    public TreeSet<DatabasePlayer> getSortedWeekly() {
        return sortedWeekly;
    }

    public TreeSet<DatabasePlayer> getSortedDaily() {
        return sortedDaily;
    }

    public Function<DatabasePlayer, Number> getValueFunction() {
        return valueFunction;
    }

    public Function<DatabasePlayer, String> getStringFunction() {
        return stringFunction;
    }
}
