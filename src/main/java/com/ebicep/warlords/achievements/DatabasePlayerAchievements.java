package com.ebicep.warlords.achievements;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;

import java.util.function.Predicate;

public enum DatabasePlayerAchievements {



    ;

    String name;
    Predicate<DatabasePlayer> predicate;

    DatabasePlayerAchievements(String name, Predicate<DatabasePlayer> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    public void giveAchievements(DatabasePlayer databasePlayer) {
        if (predicate.test(databasePlayer)) {

        }
    }
}
