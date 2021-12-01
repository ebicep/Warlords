package com.ebicep.warlords.database.leaderboards;

import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import org.bukkit.Location;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Leaderboard {

    private final String title;
    private final Location location;
    private final List<DatabasePlayer> sortedAllTime = new ArrayList<>();
    private final List<DatabasePlayer> sortedWeekly = new ArrayList<>();
    private final Aggregation aggregation;
    private final Function<DatabasePlayer, String> valueFunction;


    public Leaderboard(String title, Location location, Aggregation aggregation, Function<DatabasePlayer, String> valueFunction) {
        this.title = title;
        this.location = location;
        this.aggregation = aggregation;
        this.valueFunction = valueFunction;
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
            case ALL_TIME:
                return sortedAllTime;
            case WEEKLY:
                return sortedWeekly;
        }
        return new ArrayList<>();
    }

    public void resetSortedPlayers(List<DatabasePlayer> newSortedPlayers, PlayersCollections collections) {
        switch (collections) {
            case ALL_TIME:
                this.sortedAllTime.clear();
                this.sortedAllTime.addAll(newSortedPlayers);
                return;
            case WEEKLY:
                this.sortedWeekly.clear();
                this.sortedWeekly.addAll(newSortedPlayers);
                return;
        }
    }

    public <T extends Number> T[] getTopThree(Function<DatabasePlayer, Number> valueFunction) {
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
        for (DatabasePlayer databasePlayer : sortedWeekly) {
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

    public List<DatabasePlayer> getSortedWeekly() {
        return sortedWeekly;
    }

    public Aggregation getAggregation() {
        return aggregation;
    }

    public Function<DatabasePlayer, String> getValueFunction() {
        return valueFunction;
    }
}
