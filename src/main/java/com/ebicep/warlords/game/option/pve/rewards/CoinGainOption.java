package com.ebicep.warlords.game.option.pve.rewards;

import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.util.java.Pair;

import java.util.LinkedHashMap;

public class CoinGainOption implements Option {

    public static final long[] COINS_PER_5 = new long[]{
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
    private boolean playerCoinPer5Bonus = true;
    private final LinkedHashMap<String, LinkedHashMap<String, Long>> mobCoinValues = new LinkedHashMap<>() {{
        put("Bosses Killed", new LinkedHashMap<>() {{
            put("Boltaro", 200L);
            put("Ghoulcaller", 300L);
            put("Narmer", 500L);
            put("Physira", 400L);
            put("Mithra", 400L);
            put("Zenith", 1500L);
        }});
    }};
    private long playerCoinPerKill = 0;
    private Pair<Long, Integer> playerCoinPerXSec = null;
    private long guildCoinInsigniaConvertBonus = 0;
    private Pair<Long, Integer> guildCoinPerXSec = null;
    private boolean disableCoinConversionUpgrade = false;

    public LinkedHashMap<String, LinkedHashMap<String, Long>> getMobCoinValues() {
        return mobCoinValues;
    }

    public void clearMobCoinValueAndSet(String key, LinkedHashMap<String, Long> value) {
        mobCoinValues.clear();
        mobCoinValues.put(key, value);
    }

    public CoinGainOption clearMobCoinValueAndSet(String key, String mobName, long value) {
        mobCoinValues.clear();
        mobCoinValues.put(key, new LinkedHashMap<>() {{
            put(mobName, value);
        }});
        return this;
    }

    public CoinGainOption clearMobCoinValues() {
        mobCoinValues.clear();
        return this;
    }

    public CoinGainOption noPlayerCoinWavesClearedBonus() {
        playerCoinPer5Bonus = false;
        return this;
    }

    public CoinGainOption playerCoinPerKill(long playerCoinPerKill) {
        this.playerCoinPerKill = playerCoinPerKill;
        return this;
    }

    public CoinGainOption playerCoinPerXSec(long coins, int seconds) {
        playerCoinPerXSec = new Pair<>(coins, seconds);
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

    public CoinGainOption disableCoinConversionUpgrade() {
        disableCoinConversionUpgrade = true;
        return this;
    }

    public boolean isPlayerCoinPer5Bonus() {
        return playerCoinPer5Bonus;
    }

    public long getPlayerCoinPerKill() {
        return playerCoinPerKill;
    }

    public Pair<Long, Integer> getPlayerCoinPerXSec() {
        return playerCoinPerXSec;
    }

    public long getGuildCoinInsigniaConvertBonus() {
        return guildCoinInsigniaConvertBonus;
    }

    public Pair<Long, Integer> getGuildCoinPerXSec() {
        return guildCoinPerXSec;
    }

    public boolean isDisableCoinConversionUpgrade() {
        return disableCoinConversionUpgrade;
    }

}
