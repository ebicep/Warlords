package com.ebicep.warlords.pve;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsCoinSummaryEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveGuildCoinEvent;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.wavedefense.CoinGainOption;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseStats;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.Pair;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public enum Currencies implements Spendable {

    SYNTHETIC_SHARD(
            "Synthetic Shard",
            ChatColor.WHITE,
            new ItemStack(Material.STAINED_GLASS, 1, (short) 7)
    ),
    LEGEND_FRAGMENTS(
            "Legend Fragment",
            ChatColor.GOLD,
            new ItemStack(Material.BLAZE_POWDER)
    ),
    FAIRY_ESSENCE(
            "Fairy Essence",
            ChatColor.LIGHT_PURPLE,
            new ItemStack(Material.INK_SACK, 1, (short) 13)
    ) {
        @Override
        public boolean pluralIncludeS() {
            return false;
        }
    },
    COMMON_STAR_PIECE(
            "Common Star Piece",
            ChatColor.GREEN,
            new ItemStack(Material.NETHER_STAR)
    ),
    RARE_STAR_PIECE(
            "Rare Star Piece",
            ChatColor.BLUE,
            new ItemStack(Material.NETHER_STAR)
    ),
    EPIC_STAR_PIECE(
            "Epic Star Piece",
            ChatColor.DARK_PURPLE,
            new ItemStack(Material.NETHER_STAR)
    ),
    LEGENDARY_STAR_PIECE(
            "Legendary Star Piece",
            ChatColor.GOLD,
            new ItemStack(Material.NETHER_STAR)
    ),
    SUPPLY_DROP_TOKEN(
            "Supply Drop Token",
            ChatColor.GOLD,
            new ItemStack(Material.FIREWORK_CHARGE)
    ),
    COIN(
            "Coin",
            ChatColor.YELLOW,
            new ItemStack(Material.GOLD_NUGGET)
    ),
    SKILL_BOOST_MODIFIER(
            "Skill Boost Modifier",
            ChatColor.DARK_GRAY,
            new ItemStack(Material.BOOKSHELF)
    ),
    EVENT_POINTS_BOLTARO(
            "Boltaro Event Point",
            ChatColor.YELLOW,
            new ItemStack(Material.DOUBLE_PLANT)
    ),
    EVENT_POINTS_NARMER(
            "Narmer Event Point",
            ChatColor.YELLOW,
            new ItemStack(Material.DOUBLE_PLANT)
    ),
    EVENT_POINTS_MITHRA(
            "Mithra Event Point",
            ChatColor.YELLOW,
            new ItemStack(Material.DOUBLE_PLANT)
    ),
    TITLE_TOKEN_JUGGERNAUT(
            "Juggernaut Title Token",
            ChatColor.YELLOW,
            new ItemStack(Material.SNOW_BALL)
    ),
    TITLE_TOKEN_PHARAOHS_REVENGE(
            "Pharaoh's Revenge Title Token",
            ChatColor.YELLOW,
            new ItemStack(Material.SNOW_BALL)
    ),
    TITLE_TOKEN_SPIDERS_BURROW(
            "Spiders Burrow Title Token",
            ChatColor.YELLOW,
            new ItemStack(Material.SNOW_BALL)
    ),
    LIMIT_BREAKER(
            "Limit Breaker",
            ChatColor.DARK_GRAY,
            new ItemStack(Material.WATCH)
    ),
    MYSTERIOUS_TOKEN(
            "Mysterious Token",
            ChatColor.MAGIC,
            new ItemStack(Material.BEDROCK)
    ),

    ;

    public static final List<Currencies> STAR_PIECES = Arrays.asList(
            COMMON_STAR_PIECE,
            RARE_STAR_PIECE,
            EPIC_STAR_PIECE,
            LEGENDARY_STAR_PIECE
    );
    public static final Currencies[] VALUES = values();
    public static final HashMap<UUID, PvECoinSummary> CACHED_PLAYER_COIN_STATS = new HashMap<>();

    public static PvECoinSummary getCoinGainFromGameStats(
            WarlordsEntity warlordsPlayer,
            WaveDefenseOption waveDefenseOption,
            boolean recalculate
    ) {
        if (!recalculate &&
                CACHED_PLAYER_COIN_STATS.containsKey(warlordsPlayer.getUuid()) &&
                CACHED_PLAYER_COIN_STATS.get(warlordsPlayer.getUuid()) != null
        ) {
            return CACHED_PLAYER_COIN_STATS.get(warlordsPlayer.getUuid());
        }

        WaveDefenseStats.PlayerWaveDefenseStats playerWaveDefenseStats = waveDefenseOption
                .getWaveDefenseStats()
                .getPlayerWaveDefenseStats(warlordsPlayer.getUuid());
        LinkedHashMap<String, Long> coinSummary = new LinkedHashMap<>(playerWaveDefenseStats.getCachedBaseCoinSummary());

        long totalCoinsEarned = 0;
        for (Long value : coinSummary.values()) {
            totalCoinsEarned += value;
        }

        long guildCoinsEarned = 0;
        CoinGainOption coinGainOption = waveDefenseOption
                .getGame()
                .getOptions()
                .stream()
                .filter(CoinGainOption.class::isInstance)
                .map(CoinGainOption.class::cast)
                .findAny()
                .orElse(null);

        if (coinGainOption != null) {
            Pair<Long, Integer> guildCoinPerXSec = coinGainOption.getGuildCoinPerXSec();
            if (guildCoinPerXSec != null) {
                RecordTimeElapsedOption recordTimeElapsedOption = waveDefenseOption
                        .getGame()
                        .getOptions()
                        .stream()
                        .filter(option -> option instanceof RecordTimeElapsedOption)
                        .map(RecordTimeElapsedOption.class::cast)
                        .findAny()
                        .orElse(null);
                if (recordTimeElapsedOption != null) {
                    int secondsElapsed = recordTimeElapsedOption.getTicksElapsed() / 20;
                    Pair<Long, Integer> coinsPerXSec = coinGainOption.getGuildCoinPerXSec();
                    guildCoinsEarned = secondsElapsed / coinsPerXSec.getB() * coinsPerXSec.getA();
                }
            }
            if (coinGainOption.getGuildCoinInsigniaConvertBonus() != 0) {
                AtomicDouble guildCoinConversionRate = new AtomicDouble(.05);
                if (!coinGainOption.isDisableCoinConversionUpgrade()) {
                    Bukkit.getPluginManager().callEvent(new WarlordsGiveGuildCoinEvent(warlordsPlayer, guildCoinConversionRate));
                }
                guildCoinsEarned = Math.min(1000, Math.round(totalCoinsEarned * guildCoinConversionRate.get()));
            }
        }

        Bukkit.getPluginManager().callEvent(new WarlordsCoinSummaryEvent(warlordsPlayer, coinSummary));

        totalCoinsEarned = 0;
        for (Long value : coinSummary.values()) {
            totalCoinsEarned += value;
        }

        if (CACHED_PLAYER_COIN_STATS.containsKey(warlordsPlayer.getUuid())) {
            return CACHED_PLAYER_COIN_STATS
                    .get(warlordsPlayer.getUuid())
                    .setCoinSummary(coinSummary)
                    .setTotalCoinsGained(totalCoinsEarned)
                    .setTotalGuildCoinsGained(guildCoinsEarned);
        } else {
            PvECoinSummary pvECoinSummary = new PvECoinSummary(coinSummary, totalCoinsEarned, guildCoinsEarned);
            CACHED_PLAYER_COIN_STATS.put(warlordsPlayer.getUuid(), pvECoinSummary);
            return pvECoinSummary;
        }
    }

    public final String name;
    public final ChatColor chatColor;
    public final ItemStack item;

    Currencies(String name, ChatColor chatColor, ItemStack item) {
        this.name = name;
        this.chatColor = chatColor;
        this.item = item;
    }

    @Override
    public void addToPlayer(DatabasePlayer databasePlayer, long amount) {
        databasePlayer.getPveStats().addCurrency(this, amount);
    }

    @Override
    public Long getFromPlayer(DatabasePlayer databasePlayer) {
        return databasePlayer.getPveStats().getCurrencyValue(this);
    }

    public String getColoredName() {
        return chatColor + name;
    }


    @Override
    public String getCostColoredName(long cost) {
        return chatColor.toString() + NumberFormat.addCommas(cost) + " " + name + (cost == 1 || !pluralIncludeS() ? "" : "s");
    }

    public boolean pluralIncludeS() {
        return true;
    }

    public static class PvECoinSummary {
        private LinkedHashMap<String, Long> coinSummary;
        private long totalCoinsGained;
        private long totalGuildCoinsGained;

        public PvECoinSummary(LinkedHashMap<String, Long> coinSummary, long totalCoinsGained, long totalGuildCoinsGained) {
            this.coinSummary = coinSummary;
            this.totalCoinsGained = totalCoinsGained;
            this.totalGuildCoinsGained = totalGuildCoinsGained;
        }

        public LinkedHashMap<String, Long> getCoinSummary() {
            return coinSummary;
        }

        public PvECoinSummary setCoinSummary(LinkedHashMap<String, Long> coinSummary) {
            this.coinSummary = coinSummary;
            return this;
        }

        public long getTotalCoinsGained() {
            return totalCoinsGained;
        }

        public PvECoinSummary setTotalCoinsGained(long totalCoinsGained) {
            this.totalCoinsGained = totalCoinsGained;
            return this;
        }

        public long getTotalGuildCoinsGained() {
            return totalGuildCoinsGained;
        }

        public PvECoinSummary setTotalGuildCoinsGained(long totalGuildCoinsGained) {
            this.totalGuildCoinsGained = totalGuildCoinsGained;
            return this;
        }
    }
}
