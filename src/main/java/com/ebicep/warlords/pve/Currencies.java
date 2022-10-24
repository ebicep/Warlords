package com.ebicep.warlords.pve;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsPlayerCoinSummaryEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsPlayerGiveGuildCoinEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.NumberFormat;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public enum Currencies {

    SYNTHETIC_SHARD(
            "Synthetic Shard",
            ChatColor.WHITE,
            new ItemStack(Material.BLAZE_POWDER)
    ),
    LEGEND_FRAGMENTS(
            "Legend Fragment",
            ChatColor.GOLD,
            new ItemStack(Material.GOLD_NUGGET)
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
            new ItemStack(Material.GOLD_NUGGET)
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
    public final String name;
    public final ChatColor chatColor;
    public final ItemStack item;

    Currencies(String name, ChatColor chatColor, ItemStack item) {
        this.name = name;
        this.chatColor = chatColor;
        this.item = item;
    }

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

        LinkedHashMap<String, Long> coinSummary = new LinkedHashMap<>(waveDefenseOption.getWaveDefenseStats().getCachedBaseCoinSummary());

        Bukkit.getPluginManager().callEvent(new WarlordsPlayerCoinSummaryEvent(warlordsPlayer, coinSummary));

        long totalCoinsEarned = 0;
        for (Long value : coinSummary.values()) {
            totalCoinsEarned += value;
        }

        AtomicDouble guildCoinConversionRate = new AtomicDouble(.05);
        Bukkit.getPluginManager().callEvent(new WarlordsPlayerGiveGuildCoinEvent(warlordsPlayer, guildCoinConversionRate));
        long guildCoinsEarned = Math.min(300, Math.round(totalCoinsEarned * guildCoinConversionRate.get()));

        if (CACHED_PLAYER_COIN_STATS.containsKey(warlordsPlayer.getUuid())) {
            return CACHED_PLAYER_COIN_STATS.get(warlordsPlayer.getUuid())
                    .setCoinSummary(coinSummary)
                    .setTotalCoinsGained(totalCoinsEarned)
                    .setTotalGuildCoinsGained(guildCoinsEarned);
        } else {
            PvECoinSummary pvECoinSummary = new PvECoinSummary(coinSummary, totalCoinsEarned, guildCoinsEarned);
            CACHED_PLAYER_COIN_STATS.put(warlordsPlayer.getUuid(), pvECoinSummary);
            return pvECoinSummary;
        }
    }

    public String getColoredName() {
        return chatColor + name;
    }

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
