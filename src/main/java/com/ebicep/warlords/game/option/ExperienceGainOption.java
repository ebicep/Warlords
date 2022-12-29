package com.ebicep.warlords.game.option;

import com.ebicep.warlords.util.java.Pair;

public class ExperienceGainOption implements Option {

    private long playerExpPerWave = 0;
    private long playerExpMaxWaveClearBonus = 0;
    private Pair<Long, Integer> playerExpPerXSec = null;
    //GUILD not effected by difficulty rewards multiplier
    private long guildExpPerWave = 0;
    private long guildExpMaxWaveClearBonus = 0;
    private Pair<Long, Integer> guildExpPerXSec = null;

    public ExperienceGainOption playerExpPerWave(long playerExpPerWave) {
        this.playerExpPerWave = playerExpPerWave;
        return this;
    }

    public ExperienceGainOption playerExpMaxWaveClearBonus(long playerExpMaxWaveClearBonus) {
        this.playerExpMaxWaveClearBonus = playerExpMaxWaveClearBonus;
        return this;
    }

    public ExperienceGainOption playerExpPerXSec(long playerExpPerXSec, int xSec) {
        this.playerExpPerXSec = new Pair<>(playerExpPerXSec, xSec);
        return this;
    }

    public ExperienceGainOption guildExpPerWave(long guildExpPerWave) {
        this.guildExpPerWave = guildExpPerWave;
        return this;
    }

    public ExperienceGainOption guildExpMaxWaveClearBonus(long guildExpMaxWaveClearBonus) {
        this.guildExpMaxWaveClearBonus = guildExpMaxWaveClearBonus;
        return this;
    }

    public ExperienceGainOption guildExpPerXSec(long guildExpPerXSec, int xSec) {
        this.guildExpPerXSec = new Pair<>(guildExpPerXSec, xSec);
        return this;
    }

    public long getPlayerExpPerWave() {
        return playerExpPerWave;
    }

    public long getPlayerExpMaxWaveClearBonus() {
        return playerExpMaxWaveClearBonus;
    }

    public Pair<Long, Integer> getPlayerExpPerXSec() {
        return playerExpPerXSec;
    }

    public long getGuildExpPerWave() {
        return guildExpPerWave;
    }

    public long getGuildExpMaxWaveClearBonus() {
        return guildExpMaxWaveClearBonus;
    }

    public Pair<Long, Integer> getGuildExpPerXSec() {
        return guildExpPerXSec;
    }
}
