package com.ebicep.warlords.database;

import com.ebicep.warlords.database.newdb.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.newdb.repositories.player.pojos.DatabasePlayer;
import org.bukkit.Location;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

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
