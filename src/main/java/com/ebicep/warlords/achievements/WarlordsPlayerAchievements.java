package com.ebicep.warlords.achievements;

import com.ebicep.warlords.player.WarlordsPlayer;

import java.util.function.Predicate;

public enum WarlordsPlayerAchievements {

    ;

    public String name;
    public Predicate<WarlordsPlayer> predicate;

    WarlordsPlayerAchievements(String name, Predicate<WarlordsPlayer> predicate) {
        this.name = name;
        this.predicate = predicate;
    }
}
