package com.ebicep.warlords.database.leaderboards;

import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.bukkit.Location;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Leaderboard {

    private final String title;
    private final Location location;
    private final List<DatabasePlayer> sortedAllTime = new ArrayList<>();
    private final List<DatabasePlayer> sortedSeason6 = new ArrayList<>();
    private final List<DatabasePlayer> sortedSeason5 = new ArrayList<>();
    private final List<DatabasePlayer> sortedSeason4 = new ArrayList<>();
    private final List<DatabasePlayer> sortedWeekly = new ArrayList<>();
    private final List<DatabasePlayer> sortedDaily = new ArrayList<>();
    private final Function<DatabasePlayer, Number> valueFunction;
    private final Function<DatabasePlayer, String> stringFunction;


    public Leaderboard(String title, Location location, Function<DatabasePlayer, Number> valueFunction, Function<DatabasePlayer, String> stringFunction) {
        this.title = title;
        this.location = location;
        this.valueFunction = valueFunction;
        this.stringFunction = stringFunction;
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

    public List<DatabasePlayer> getSortedPlayers(PlayersCollections collections) {
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
        return new ArrayList<>();
    }

    public void resetSortedPlayers(List<DatabasePlayer> newSortedPlayers, PlayersCollections collections) {
        switch (collections) {
            case LIFETIME:
                this.sortedAllTime.clear();
                this.sortedAllTime.addAll(newSortedPlayers);
                return;
            case SEASON_6:
                this.sortedSeason6.clear();
                this.sortedSeason6.addAll(newSortedPlayers);
                return;
            case SEASON_5:
                this.sortedSeason5.clear();
                this.sortedSeason5.addAll(newSortedPlayers);
                return;
            case SEASON_4:
                this.sortedSeason4.clear();
                this.sortedSeason4.addAll(newSortedPlayers);
                return;
            case WEEKLY:
                this.sortedWeekly.clear();
                this.sortedWeekly.addAll(newSortedPlayers);
                return;
            case DAILY:
                this.sortedDaily.clear();
                this.sortedDaily.addAll(newSortedPlayers);
                return;
        }
    }

    public <T extends Number> T[] getTopThreeValues() {
        //current top value to compare to
        Number topValue = valueFunction.apply(sortedWeekly.get(0));

        Class<T> clazz = (Class<T>) topValue.getClass();
        //ouput array of type clazz
        T[] output = (T[]) Array.newInstance(clazz, 3);
        //first top number is current top
        output[0] = (T) topValue;

        List<Number> topThree = new ArrayList<>();
        int counter = 0;
        //looping to get the next top two numbers
        //filtering out all players with 3 or less games from leaderboards if the top player has 10 or more (no one game olivers)
        boolean filter = sortedWeekly.get(0).getPlays() >= 10;
        List<DatabasePlayer> databasePlayers;
        if (filter) {
            databasePlayers = sortedWeekly.stream()
                    .filter(databasePlayer -> databasePlayer.getPlays() > 3)
                    .collect(Collectors.toList());
        } else {
            databasePlayers = sortedWeekly;
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
            topThreePlayers[i] = topThreePlayers[i].substring(0, topThreePlayers[i].length() - 1);
        }
        return topThreePlayers;
    }

    public static int compare(Number a, Number b) {
        return new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString()));
    }

    public String getTitle() {
        return title;
    }

    public Location getLocation() {
        return location;
    }

    public List<DatabasePlayer> getSortedAllTime() {
        return sortedAllTime;
    }

    public List<DatabasePlayer> getSortedSeason6() {
        return sortedSeason6;
    }

    public List<DatabasePlayer> getSortedSeason5() {
        return sortedSeason5;
    }

    public List<DatabasePlayer> getSortedSeason4() {
        return sortedSeason4;
    }

    public List<DatabasePlayer> getSortedWeekly() {
        return sortedWeekly;
    }

    public List<DatabasePlayer> getSortedDaily() {
        return sortedDaily;
    }

    public Function<DatabasePlayer, Number> getValueFunction() {
        return valueFunction;
    }

    public Function<DatabasePlayer, String> getStringFunction() {
        return stringFunction;
    }
}
