package com.ebicep.warlords.player.general;

import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;

import java.util.function.Function;

public enum MinuteStats {

    KILLS("Kills", entry -> (long) entry.getKills()),
    ASSISTS("Assists", entry -> (long) entry.getAssists()),
    DEATHS("Deaths", entry -> (long) entry.getDeaths()),
    DAMAGE("Damage", PlayerStatisticsMinute.Entry::getDamage),
    HEALING("Healing", PlayerStatisticsMinute.Entry::getHealing),
    ABSORBED("Absorbed", PlayerStatisticsMinute.Entry::getAbsorbed),

    ;

    public final String name;
    public final Function<PlayerStatisticsMinute.Entry, Long> getValue;

    MinuteStats(String name, Function<PlayerStatisticsMinute.Entry, Long> getValue) {
        this.name = name;
        this.getValue = getValue;
    }
}
