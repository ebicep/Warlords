package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.util.java.Pair;

import java.util.LinkedHashMap;

public class CoinGainOption implements Option {

    public static final LinkedHashMap<String, Long> BOSS_COIN_VALUES = new LinkedHashMap<>() {{
        put("Boltaro", 200L);
        put("Ghoulcaller", 300L);
        put("Narmer", 500L);
        put("Physira", 400L);
        put("Mithra", 400L);
        put("Zenith", 1500L);
    }};
    public static final long[] COINS_PER_5_WAVES = new long[]{
            50,
            100,
            150,
            200,
            300,
            400,
            500,
            600,
            700,
            800,
            900,
            1000,
            1100,
            1200,
            1300,
            1400,
            1500,
            1600,
            1700,
            1800
    };

    private boolean playerCoinWavesClearedBonus = true;
    private boolean playerCoinBossesKilledBonus = true;
    private long playerCoinPerKill = 0;
    private long guildCoinInsigniaConvertBonus = 0;
    private Pair<Long, Integer> guildCoinPerXSec = null;

    public CoinGainOption noPlayerCoinWavesClearedBonus() {
        playerCoinWavesClearedBonus = false;
        return this;
    }

    public CoinGainOption noPlayerCoinBossesKilledBonus() {
        playerCoinBossesKilledBonus = false;
        return this;
    }

    public CoinGainOption playerCoinPerKill(long playerCoinPerKill) {
        this.playerCoinPerKill = playerCoinPerKill;
        return this;
    }

    public CoinGainOption guildCoinInsigniaConvertBonus(long guildCoinInsigniaConvertBonus) {
        this.guildCoinInsigniaConvertBonus = guildCoinInsigniaConvertBonus;
        return this;
    }

    public CoinGainOption guildCoinPerXSec(long coins, int seconds) {
        guildCoinPerXSec = new Pair<>(coins, seconds);
        return this;
    }

    public boolean isPlayerCoinWavesClearedBonus() {
        return playerCoinWavesClearedBonus;
    }

    public boolean isPlayerCoinBossesKilledBonus() {
        return playerCoinBossesKilledBonus;
    }

    public long getPlayerCoinPerKill() {
        return playerCoinPerKill;
    }

    public long getGuildCoinInsigniaConvertBonus() {
        return guildCoinInsigniaConvertBonus;
    }

    public Pair<Long, Integer> getGuildCoinPerXSec() {
        return guildCoinPerXSec;
    }
}
