package com.ebicep.warlords.game.option;

import com.ebicep.warlords.util.java.Pair;

public class ExperienceGainOption implements Option {

    private long playerExpPer = 0;
    private long playerExpGameWinBonus = 0;
    private Pair<Long, Integer> playerExpPerXSec = null;
    //GUILD not effected by difficulty rewards multiplier
    private long guildExpPer = 0;
    private long guildExpGameWinBonus = 0;
    private Pair<Long, Integer> guildExpPerXSec = null;

    public ExperienceGainOption playerExpPer(long playerExpPer) {
        this.playerExpPer = playerExpPer;
        return this;
    }

    public ExperienceGainOption playerExpGameWinBonus(long playerExpGameWinBonus) {
        this.playerExpGameWinBonus = playerExpGameWinBonus;
        return this;
    }

    public ExperienceGainOption playerExpPerXSec(long playerExpPerXSec, int xSec) {
        this.playerExpPerXSec = new Pair<>(playerExpPerXSec, xSec);
        return this;
    }

    public ExperienceGainOption guildExpPer(long guildExpPer) {
        this.guildExpPer = guildExpPer;
        return this;
    }

    public ExperienceGainOption guildExpMaxGameWinBonus(long guildExpMaxGameWinBonus) {
        this.guildExpGameWinBonus = guildExpMaxGameWinBonus;
        return this;
    }

    public ExperienceGainOption guildExpPerXSec(long guildExpPerXSec, int xSec) {
        this.guildExpPerXSec = new Pair<>(guildExpPerXSec, xSec);
        return this;
    }

    public long getPlayerExpPer() {
        return playerExpPer;
    }

    public long getPlayerExpGameWinBonus() {
        return playerExpGameWinBonus;
    }

    public Pair<Long, Integer> getPlayerExpPerXSec() {
        return playerExpPerXSec;
    }

    public long getGuildExpPer() {
        return guildExpPer;
    }

    public long getGuildExpGameWinBonus() {
        return guildExpGameWinBonus;
    }

    public Pair<Long, Integer> getGuildExpPerXSec() {
        return guildExpPerXSec;
    }
}
